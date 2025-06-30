import { useCallback, useRef, useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import { axios } from "../utils/axios";
// 아이콘
import {
  Flip,
  RadioButtonChecked,
  TimerRounded,
  DirectionsRun,
  DoDisturb,
  Save,
  Movie,
} from "@mui/icons-material";
import loading from "../assets/challenge/loading.gif";
import complete from "../assets/challenge/complete.svg";
import recordingImg from "../assets/challenge/recording.svg";
import uncomplete from "../assets/challenge/uncomplete.svg";
import camera from "../assets/challenge/camera.png";
import StarEffect from "../components/style/StarEffect";
// 타입 및 함수
import LoadingModalComponent from "../components/modal/LoadingModalComponent";
import VideoMotionButton from "../components/button/VideoMotionButton";
import { Shorts } from "../constants/types";
import { getShortsInfo } from "../apis/shorts";
import { getPresignedGetURL } from "../apis/s3";
import { addRecordedShorts, modifyRecordedShortsStatus } from "../apis/recordedshorts";
// 모션
import { NormalizedLandmark } from "@mediapipe/tasks-vision";
import { predictWebcamChallenge, setBtnInfo } from "../modules/Motion";
import { useBtnStore, useMotionDetectionStore } from "../store/useMotionStore";

const ChallengePage = () => {
  const navigate = useNavigate();
  const params = useParams();
  // 비디오
  const userVideoRef = useRef<HTMLVideoElement>(null);
  const danceVideoRef = useRef<HTMLVideoElement>(null);
  const [shorts, setShorts] = useState<Shorts | null>(null);
  // MediaRecorder 설정
  const [mediaRecorder, setMediaRecorder] = useState<MediaRecorder | null>(null);
  const [stream, setStream] = useState<MediaStream | null>(null);
  const options = {
    audioBitsPerSecond: 128000,
    videoBitsPerSecond: 2500000,
    // H.264(비디오) + AAC(오디오) 코덱의 MP4
    mimeType: "video/mp4;codecs=avc1.64003E,mp4a.40.2",
  };
  // 상태 관리
  enum ChallengeState {
    READY = "READY",
    RECORD = "RECORD",
    UPLOADED = "UPLOADED",
    COMPLETED = "COMPLETED",
    FAILD = "FAILED",
  }
  const [state, setState] = useState<ChallengeState>(ChallengeState.READY);
  // 버튼
  const [timer, setTimer] = useState<number>(parseInt(localStorage.getItem("timer") ?? "3")); // 타이머
  const [isFlipped, setIsFlipped] = useState<boolean>(false);
  // 모달
  const [show, setShow] = useState(false);
  const [loadPath, setLoadPath] = useState(loading); // 로딩 이미지 경로
  const [ffmpegLog, setFfmpegLog] = useState(""); // 동영상 상태
  const videoResolutionRef = useRef({ width: 405, height: 720 }); // 해상도
  // 모션 인식 카운트
  const { btn, setBtn } = useBtnStore();
  const { visibleCount, timerCount, recordCount, learnCount, resultCount, saveCount } =
    useMotionDetectionStore();

  // 사용자가 클릭한 쇼츠 조회
  const loadDanceVideo = async () => setShorts(await getShortsInfo(`${params.shortsId}`));

  // 녹화 시작 버튼
  // 1. 녹화 준비
  const prepareRecording = () => {
    if (!stream) {
      alert("카메라 접근을 허용해주세요.");
      return;
    }

    const { width = 405, height = 720 } = stream.getVideoTracks()[0].getSettings();
    videoResolutionRef.current = { width, height };

    // 모달에 해상도 표시
    setLoadPath(camera);
    setFfmpegLog(`녹화 해상도: ${width}x${height}`);

    handleStartCountdown();
    setShow(true);
  };
  // 2. 카운트 다운
  const handleStartCountdown = () => {
    let count = timer;
    const intervalId = setInterval(() => {
      if (count <= 1) {
        setShow(false); // 모달 닫기
        clearInterval(intervalId);
        setState(ChallengeState.RECORD); // 버튼 목록 전환
        setTimer(timer); // 타이머 초기화
        startRecording(); // 녹화 시작
      } else {
        setTimer((prev) => prev - 1);
        count -= 1;
      }
    }, 1000);
  };
  // 3. 캔버스 준비
  const startRecording = () => {
    // 캔버스 생성
    const canvas = document.createElement("canvas");
    const ctx = canvas.getContext("2d")!;
    const { width, height } = videoResolutionRef.current;

    canvas.width = width;
    canvas.height = height;
    ctx.imageSmoothingEnabled = false;

    try {
      // 캔버스에서 초당 30개의 이미지를 캡처하여 비디오 스트림으로 변환
      const outputStream = canvas.captureStream();
      // 변환된 스트림을 MediaRecorder로 녹화
      const recorder = new MediaRecorder(outputStream, options);
      // 스트림 조각을 넣을 배열
      const chunks: BlobPart[] = [];
      // 스트림 데이터가 쌓이면 배열에 추가
      recorder.ondataavailable = (e) => chunks.push(e.data);

      // mediaRecorder?.stop() 트리거
      recorder.onstop = async () => {
        // 여러 개의 Blob을 하나로 합쳐 최종 비디오 생성
        const userVideoBlob = new Blob(chunks, { type: recorder.mimeType });
        // s3에 업로드
        await s3Upload(userVideoBlob);
      };

      // 녹화 시작되면
      recorder.start();
      setMediaRecorder(recorder);
      danceVideoRef.current?.play(); // 댄스 비디오 시작

      // 프레임을 실시간으로 캔버스에 그리기
      function drawFrame() {
        if (!userVideoRef.current) return;
        ctx.save(); // 현재 캔버스 상태 저장
        ctx.scale(-1, 1); // 캔버스 좌우 반전하여 거울 모드 적용
        ctx.drawImage(userVideoRef.current, -width, 0, width, height); // 반전된 상태로 비디오 프레임 그리기기
        ctx.restore(); // 캔버스 상태 복구
        requestAnimationFrame(drawFrame); // 다음 프레임을 요청하여 반복 실행
      }

      drawFrame();
    } catch (error) {
      console.log(error);
      alert("녹화를 다시 시작해 주세요.");
    }
  };

  // 타이머 버튼
  const changeTimer: () => void = () => {
    const nextTimer = timer == 3 ? 5 : timer == 5 ? 10 : 3;
    localStorage.setItem("timer", nextTimer.toString());
    setTimer(nextTimer);
  };

  // 연습모드 버튼
  const goToLearnMode = () => {
    stream?.getTracks().forEach((track) => track.stop());
    if (shorts) navigate(`/learn/${shorts.shortsId}`);
  };

  // 마이페이지 버튼
  const goToResult = () => {
    stream?.getTracks().forEach((track) => track.stop());
    navigate("/mypage");
  };

  // 녹화 취소 버튼
  const cancelRecording = () => {
    // 버튼 목록 전환
    setState(ChallengeState.READY);
    // 비디오 초기화
    if (danceVideoRef.current) {
      danceVideoRef.current.pause();
      danceVideoRef.current.currentTime = 0;
    }
  };

  // 녹화 저장 버튼
  // 1. 모달 열기
  const handleShowModal = () => {
    setLoadPath(loading);
    setFfmpegLog("동영상 저장...");
    setShow(true);

    // 비디오 초기화
    if (danceVideoRef.current) {
      danceVideoRef.current.pause();
      danceVideoRef.current.currentTime = 0;
    }

    // recorder.onstop() 실행
    mediaRecorder?.stop();
  };
  // 2. 모달 닫기
  const handleCloseModal = () => {
    setState(ChallengeState.READY);
    setTimeout(() => {
      setShow(false);
    }, 2000);
  };

  // S3에 사용자 비디오 업로드
  const s3Upload = async (blob: Blob) => {
    if (!shorts) {
      alert("현재 쇼츠에 오류가 있습니다.");
      throw new Error("원본 쇼츠가 존재하지 않습니다.");
    }

    let processedShortsS3key = "";

    try {
      // 원본 쇼츠 key를 사용자 쇼츠 메타데이터에 삽입
      // s3 메타데이터는 메타 데이터는 특수 문자 이슈 방지를 위해 Base64 인코딩함
      const metadata = {
        song: btoa(String.fromCharCode(...new TextEncoder().encode(shorts.shortsS3key))),
      };

      // s3에 객체를 업로드할 수 있는 presignedputurl 및 lambda 처리 완료됐다고 가정하고 생성한 s3key 받음
      const result = await addRecordedShorts(shorts.shortsId, metadata);
      processedShortsS3key = result.processedShortsS3key;

      // 생성된 presignedurl과 "똑같은" 헤더로 aws에 put요청을 해야함
      await axios.put(result.presignedPutURL, blob, {
        headers: {
          "Content-Type": "video/mp4",
          "x-amz-meta-song": metadata["song"],
        },
      });

      // S3 Put 요청에 성공하면 uploaded 상태로 변경
      await modifyRecordedShortsStatus(processedShortsS3key, ChallengeState.UPLOADED);

      setLoadPath(loading);
      setFfmpegLog("음악 삽입...");

      // aws lambda가 처리를 완료했는지 조회
      await check(processedShortsS3key);
    } catch (error: any) {
      // s3 업로드 실패했다면 failed로 상태 변경
      await modifyRecordedShortsStatus(processedShortsS3key, ChallengeState.FAILD);
      setLoadPath(uncomplete);
      setFfmpegLog("동영상 처리 실패");
      console.error("s3 업로드 실패", error.data);
    } finally {
      setState(ChallengeState.READY);
    }
  };

  // 비디오 상태 추척 함수
  const check = async (processedShortsS3key: string) => {
    // 객체 업로드 됐는지 확인할 presignedGetUrl
    const presignedGetURL = await getPresignedGetURL(processedShortsS3key);
    // 요청 횟수 추적
    let attempts = 0;
    // 10초마다 요청하기 위해 setInterval 사용
    const interval = setInterval(async () => {
      const exists = await isExist(presignedGetURL);

      if (exists) {
        // aws lambda가 처리를 완료했다면 completed로 상태 변경
        await modifyRecordedShortsStatus(processedShortsS3key, ChallengeState.COMPLETED);
        clearInterval(interval); // 객체가 생성되면 요청 중단
        setLoadPath(complete);
        setFfmpegLog("완성!");
        setTimeout(handleCloseModal, 1000);
      } else {
        attempts++;
        console.log(`❌ 아직 객체가 존재하지 않음, 다시 확인... (${attempts}/12)`);
        // 12번(1분) 요청 후 중단
        if (attempts >= 12) {
          // 람다 처리 실패했다면 failed로 상태 변경
          await modifyRecordedShortsStatus(processedShortsS3key, ChallengeState.FAILD);
          clearInterval(interval);
          setLoadPath(uncomplete);
          setFfmpegLog("동영상 처리 실패");
          setTimeout(handleCloseModal, 3000);
        }
      }
    }, 5000); // 5초 (5000ms) 간격으로 요청
  };

  const isExist = async (presignedGetURL: string) => {
    try {
      await axios.get(presignedGetURL);
      return true; // 객체 존재함
    } catch (error: any) {
      console.error(error.data);
      return false;
    }
  };

  const lastWebcamTime = -1;
  const before_handmarker: NormalizedLandmark | null = null;
  const curr_handmarker: NormalizedLandmark | null = null;

  // 웹캠 초기화
  const setInit = useCallback(async () => {
    const constraints: MediaStreamConstraints = {
      video: {
        aspectRatio: 9 / 16, // 9 : 16 비율
        width: { ideal: 608 },
        height: { ideal: 1080 }, // 1080p
      },
      audio: false, // 오디오 녹음 안 함
    };

    try {
      const stream = await navigator.mediaDevices.getUserMedia(constraints);

      const mediaRecorder = new MediaRecorder(stream, options);

      setStream(stream);
      setMediaRecorder(mediaRecorder);

      if (userVideoRef.current) {
        userVideoRef.current.srcObject = stream;
        userVideoRef.current.addEventListener("loadeddata", () => {
          predictWebcamChallenge(
            "challenge",
            userVideoRef.current,
            lastWebcamTime,
            before_handmarker,
            curr_handmarker,
            setBtn
          );
        });
      }
    } catch (error: any) {
      alert("카메라 권한을 허용해주세요.");
      console.error("MediaRecorder 설정 실패:", error);
    }
  }, []);

  // 비디오 크기 초기화
  const initVideoSize = (videoRef: React.RefObject<HTMLVideoElement>) => {
    if (videoRef.current) {
      switch (screen.orientation.type) {
        case "landscape-primary":
        case "landscape-secondary":
          videoRef.current.height = window.innerHeight;
          videoRef.current.width = Math.floor((window.innerHeight * 9) / 16);
          //console.log(videoRef.current.height, videoRef.current.width);
          break;
        case "portrait-primary":
        case "portrait-secondary":
          videoRef.current.width = window.innerWidth;
          videoRef.current.height = Math.floor((window.innerWidth * 16) / 9);
        //console.log(videoRef.current.height, videoRef.current.width);
      }
    }
  };

  // 초기 설정
  useEffect(() => {
    setInit(); // 카메라 초기화
    loadDanceVideo(); // 댄스 비디오 로드
    initVideoSize(danceVideoRef);
    initVideoSize(userVideoRef);

    const handleOrientationChange = () => {
      setTimeout(() => {
        initVideoSize(danceVideoRef);
        initVideoSize(userVideoRef);
      }, 200);
    };

    window.addEventListener("orientationchange", handleOrientationChange);

    return () => {
      window.removeEventListener("orientationchange", handleOrientationChange);
    };
  }, []);

  // state 변화 감지
  useEffect(() => {
    setBtnInfo();
  }, [state]);

  // btn 변화 감지
  useEffect(() => {
    switch (btn) {
      case "visible":
        //console.log("visible");
        if (state === ChallengeState.READY) prepareRecording();
        else cancelRecording();
        break;
      case "timer":
        //console.log("timer");
        if (state === ChallengeState.READY) changeTimer();
        break;
      case "save":
        //console.log("save");
        if (state !== ChallengeState.READY) handleShowModal();
        break;
      case "record":
        //console.log("flip");
        if (state !== ChallengeState.RECORD) setIsFlipped(!isFlipped);
        break;
      case "learn":
        //console.log("learn");
        if (state !== ChallengeState.RECORD) goToLearnMode();
        break;
      case "rslt":
        //console.log("result");
        if (state !== ChallengeState.RECORD) goToResult();
        break;
    }
  }, [btn]);

  // state 변화 감지
  useEffect(() => {
    setBtnInfo();
  }, [state]);

  return (
    <ChallengeContainer>
      <StarEffect numStars={80} />

      <VideoContainer
        ref={danceVideoRef}
        src={shorts?.shortsS3URL}
        playsInline
        onEnded={handleShowModal}
        className={isFlipped ? "flip" : ""}
        crossOrigin="anonymous"
      ></VideoContainer>

      <UserContainer id="dom">
        <UserVideoContainer ref={userVideoRef} autoPlay playsInline></UserVideoContainer>
        {state === ChallengeState.READY ? (
          <Timer>{timer}</Timer>
        ) : (
          <RecordingComponent>
            <RecordingTEXT>REC</RecordingTEXT>
            <Recording src={recordingImg} />
          </RecordingComponent>
        )}
        <VideoMotionButtonList>
          {state === ChallengeState.READY ? (
            <div className="foldList">
              <VideoMotionButton
                icon={<RadioButtonChecked />}
                toolTip="녹화"
                onClick={() => prepareRecording()}
                id="visible"
                progress={visibleCount}
                isVisible={state === ChallengeState.READY}
              />
              <VideoMotionButton
                icon={<TimerRounded />}
                toolTip="타이머"
                onClick={changeTimer}
                id="timer"
                progress={timerCount}
                isVisible={state === ChallengeState.READY}
              />
              <VideoMotionButton
                icon={<Flip />}
                toolTip="거울 모드"
                onClick={() => setIsFlipped(!isFlipped)}
                id="record"
                progress={recordCount}
                isVisible={state === ChallengeState.READY}
              />
              <VideoMotionButton
                icon={<Movie />}
                toolTip="마이페이지"
                onClick={goToResult}
                id="rslt"
                progress={resultCount}
                isVisible={state === ChallengeState.READY}
              />
              <VideoMotionButton
                icon={<DirectionsRun />}
                toolTip="연습 모드로 이동"
                onClick={goToLearnMode}
                id="learn"
                progress={learnCount}
                isVisible={state === ChallengeState.READY}
              />
            </div>
          ) : (
            <div className="recordfoldlist">
              <VideoMotionButton
                icon={<DoDisturb />}
                toolTip="취소"
                onClick={cancelRecording}
                id="visible"
                progress={visibleCount}
                isVisible={state === ChallengeState.RECORD}
              />
              <VideoMotionButton
                icon={<Save />}
                toolTip="저장"
                onClick={handleShowModal}
                id="save"
                progress={saveCount}
                isVisible={state === ChallengeState.RECORD}
              />
            </div>
          )}
        </VideoMotionButtonList>
      </UserContainer>
      <LoadingModalComponent
        progress={ffmpegLog}
        showModal={show}
        handleCloseModal={handleCloseModal}
        path={loadPath}
      ></LoadingModalComponent>
    </ChallengeContainer>
  );
};

const ChallengeContainer = styled.div`
  position: relative;
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: black;

  background: linear-gradient(
    180deg,
    rgba(0, 0, 0, 1) 0%,
    rgba(48, 13, 45, 1) 80%,
    rgba(112, 0, 102, 1) 100%
  );
`;

const VideoContainer = styled.video`
  position: relative;
  display: none;

  &.flip {
    transform: scaleX(-1);
  }

  @media screen and (min-width: 1024) {
    display: flex;
  }

  @media screen and (orientation: landscape) {
    display: flex;
  }
`;

const UserContainer = styled.div`
  position: relative;
`;

const UserVideoContainer = styled.video`
  position: relative;
  display: flex;
  object-fit: cover;
  transform: scaleX(-1);
`;

const blinkEffect = keyframes`
  50% {
    opacity: 0;
  }
`;

const RecordingComponent = styled.div`
  position: absolute;
  top: 5%;
  left: 5%;
  display: flex;
  align-items: center;
  gap: 5px;
`;

const Recording = styled.img`
  width: 15px;
  height: 15px;
  z-index: 1;
  animation: ${blinkEffect} 1s step-end infinite;
  margin-right: 5px;
`;

const RecordingTEXT = styled.div`
  font-size: 15px;
  line-height: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 5px;
`;

const Timer = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  justify-content: center;
  align-items: center;
  width: 84px;
  height: 84px;
  font-size: 48px;
  color: #fff;
  background: #35353580;
  border: 5px solid #fff;
  border-radius: 50%;
`;

const VideoMotionButtonList = styled.div`
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 18px 8px 0;

  .foldList {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    height: auto;
    min-height: 80%;
    max-height: 100%;
  }

  .recordfoldlist {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    height: auto;
    min-height: 33%;
    max-height: 100%;
  }

  button {
    display: inline-block;
    margin-bottom: 24px;
  }

  @media screen and (min-width: 768px) {
    button {
      width: 55px;
      height: 55px;
    }
  }
`;

export default ChallengePage;
