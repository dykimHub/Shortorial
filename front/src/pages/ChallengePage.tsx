import { useCallback, useRef, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useParams } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import LoadingModalComponent from "../components/modal/LoadingModalComponent";
import { predictWebcamChallenge, setBtnInfo } from "../modules/Motion";
import { NormalizedLandmark } from "@mediapipe/tasks-vision";
import { useBtnStore, useMotionDetectionStore } from "../store/useMotionStore";
import VideoMotionButton from "../components/button/VideoMotionButton";
import {
  Flip,
  RadioButtonChecked,
  TimerRounded,
  DirectionsRun,
  DoDisturb,
  Save,
  Movie,
} from "@mui/icons-material";
import { getShortsInfo } from "../apis/shorts";
import { getPresignedGetURL, getPresignedPutURL } from "../apis/s3";
import loading from "../assets/challenge/loading.gif";
import complete from "../assets/challenge/complete.svg";
import recordingImg from "../assets/challenge/recording.svg";
import uncomplete from "../assets/challenge/uncomplete.svg";
import StarEffect from "../components/style/StarEffect";
import { Shorts } from "../constants/types";
import { axios } from "../utils/axios";

const ChallengePage = () => {
  const navigate = useNavigate();
  const params = useParams();

  const userVideoRef = useRef<HTMLVideoElement>(null);
  const danceVideoRef = useRef<HTMLVideoElement>(null);

  const [short, setShort] = useState<Shorts | null>(null);
  const [mediaRecorder, setMediaRecorder] = useState<MediaRecorder | null>(null);
  const [stream, setStream] = useState<MediaStream | null>(null);
  const [danceVideoPath, setDanceVideoPath] = useState<string>("");

  const [show, setShow] = useState(false);
  const [recording, setRecording] = useState(false); // 녹화 진행
  const initialTimer = parseInt(localStorage.getItem("timer") || "3");
  const [timer, setTimer] = useState<number>(initialTimer); // 타이머
  const [loadPath, setLoadPath] = useState(loading); // 로딩 이미지 경로
  const [ffmpegLog, setFfmpegLog] = useState("");
  const [isFlipped, setIsFlipped] = useState<boolean>(false);

  type LearnState = "RECORD" | "READY";
  const [state, setState] = useState<LearnState>("READY");
  // 모션 인식 카운트
  const { btn, setBtn } = useBtnStore();
  const { visibleCount, timerCount, recordCount, learnCount, resultCount, saveCount } =
    useMotionDetectionStore();

  const loadDanceVideo = async () => {
    // 댄스비디오 s3 url
    const thisShort = await getShortsInfo(`${params.shortsId}`);

    setShort(thisShort);
    if (thisShort) {
      setDanceVideoPath(thisShort.shortsS3URL); // 쇼츠 s3 링크
    } else {
      alert("새로고침 해주세요.");
    }
  };

  const handleShowModal = () => {
    setShow(true); // 모달 열기
    stopRecording();
  };

  const handleCloseModal = () => setShow(false);
  const showRecordButton = () => setRecording(false);
  const showCancelButton = () => setRecording(true); // 타이머 useEffect 시작

  const goToLearnMode = () => {
    stream?.getTracks().forEach((track) => track.stop());
    if (short) navigate(`/learn/${short.shortsId}`);
  };

  const goToResult = () => {
    stream?.getTracks().forEach((track) => track.stop());
    navigate("/mypage");
  };

  const changeTimer = () => {
    const nextTimer = timer == 3 ? 5 : timer == 5 ? 10 : 3;

    localStorage.setItem("timer", nextTimer.toString());
    setTimer(nextTimer);
  };

  const cancelRecording = () => {
    setState("READY");
    showRecordButton();
    if (danceVideoRef.current) {
      danceVideoRef.current.pause();
      danceVideoRef.current.currentTime = 0;
    }
  };

  const stopRecording = () => {
    setLoadPath(loading);
    setFfmpegLog("동영상 저장...");
    cancelRecording();
    mediaRecorder?.stop(); // recorder.onstop() 실행
  };

  // 녹화 시작 버튼이 눌리면
  const startRecording = () => {
    setState("RECORD");

    if (!stream) {
      alert("카메라 접근을 허용해주세요.");
      return;
    }

    const canvas = document.createElement("canvas"); // 캔버스 생성
    const ctx = canvas.getContext("2d")!; // 2D 렌더링 컨텍스트
    const [track] = stream.getVideoTracks(); // 스트림에서 비디오 트랙을 가져오기
    const { width = 405, height = 720 } = track.getSettings(); // 해상도 기본 값

    setShow(true); // 모달 열기
    setFfmpegLog(`📸 녹화 해상도: ${width}x${height}`);
    setTimeout(handleCloseModal, 1500);

    canvas.width = width;
    canvas.height = height;
    ctx.imageSmoothingEnabled = false;

    try {
      const outputStream = canvas.captureStream(); // 캔버스에서 초당 30개의 이미지를 캡처하여 비디오 스트림으로 변환
      const recorder = new MediaRecorder(outputStream); // 변환된 스트림을 MediaRecorder로 녹화
      const chunks: BlobPart[] = []; // 스트림 조각을 넣을 배열
      recorder.ondataavailable = (e) => chunks.push(e.data); // 스트림 데이터가 쌓이면 배열에 추가

      // 녹화 중지되면
      recorder.onstop = async () => {
        const userVideoBlob = new Blob(chunks, { type: "video/mp4" }); // 여러 개의 Blob을 하나로 합쳐 최종 비디오 생성
        await s3Upload(userVideoBlob); // s3에 업로드
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

  const s3Upload = async (blob: Blob) => {
    try {
      const createdAt = Date.now().toString(); // 파일명
      // s3에 객체를 업로드할 수 있는 presignedputurl 조회
      const presignedURL = await getPresignedPutURL(createdAt, short?.shortsS3Key);

      // s3 메타데이터를 presignedurl과 똑같이 key value를 지정해야함
      // s3 메타데이터는 baseu4 encoding안하면 오류남
      const songS3key = btoa(String.fromCharCode(...new TextEncoder().encode(short?.shortsS3Key)));

      await axios.put(presignedURL, blob, {
        headers: {
          "Content-Type": "video/mp4",
          "x-amz-meta-song": songS3key,
        },
      });

      setLoadPath(loading);
      setFfmpegLog("음악 삽입...");

      // aws lambda가 처리를 완료했는지 조회
      await check(createdAt);
    } catch (error: any) {
      setLoadPath(uncomplete);
      setFfmpegLog("동영상 처리 실패");
      console.error("s3 upload fail", error.data);
    } finally {
      setState("READY");
    }
  };

  const check = async (createdAt: string) => {
    // 객체 업로드 됐는지 확인할 presignedGetUrl
    const presignedGetURL = await getPresignedGetURL(createdAt);
    //console.log(presignedGetURL);

    let attempts = 0; // 요청 횟수 추적

    // 10초마다 요청하기 위해 setInterval 사용
    const interval = setInterval(async () => {
      const exists = await isExist(presignedGetURL);

      if (exists) {
        clearInterval(interval); // 객체가 생성되면 요청 중단
        setLoadPath(complete);
        setFfmpegLog("완성!");
        setTimeout(handleCloseModal, 1000);
      } else {
        attempts++;
        console.log(`❌ 아직 객체가 존재하지 않음, 다시 확인... (${attempts}/6)`);

        if (attempts >= 6) {
          // 6번(1분) 요청 후 중단
          clearInterval(interval);
          setLoadPath(uncomplete);
          setFfmpegLog("동영상 처리 실패");
          setTimeout(handleCloseModal, 3000);
        }
      }
    }, 10000); // 10초 (10000ms) 간격으로 요청
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

  // 타이머
  useEffect(() => {
    if (recording) {
      // 녹화 시작 버튼을 눌렀을 때
      const intervalId = setInterval(() => {
        setTimer((prevTimer) => {
          if (prevTimer <= 1) {
            clearInterval(intervalId); // 인터벌 종료
            startRecording(); // 녹화 시작
            return initialTimer; // 로컬스토리지에 저장된 타이머값으로 초기화
          }
          return prevTimer - 1; // timer에 저장된 값에서 1을 뺌
        });
      }, 1000); // 1초에 한번씩

      return () => {
        clearInterval(intervalId);
      };
    }
  }, [recording]);

  const lastWebcamTime = -1;
  const before_handmarker: NormalizedLandmark | null = null;
  const curr_handmarker: NormalizedLandmark | null = null;

  // camera가 있을 HTML
  const setInit = useCallback(async () => {
    const constraints: MediaStreamConstraints = {
      video: {
        aspectRatio: 9 / 16,
        // 이상적인 해상도 값
        width: { ideal: 810 },
        height: { ideal: 1440 },
      },
      audio: false,
    };

    try {
      // 카메라 불러오기
      const mediaStream = await navigator.mediaDevices.getUserMedia(constraints);
      // userVideoRef를 참조하고 있는 DOM에 넣기
      if (userVideoRef.current) {
        userVideoRef.current.srcObject = mediaStream;
        setStream(mediaStream);
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
    } catch (error) {
      alert("카메라 접근을 허용해주세요.");
      console.log(error);
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

  useEffect(() => {
    setBtnInfo();
  }, [state]);

  // 모션인식 설정
  useEffect(() => {
    switch (btn) {
      case "visible":
        //console.log("record");
        if (state === "READY") {
          showCancelButton();
        } else {
          cancelRecording();
        }
        break;
      case "timer":
        //console.log("timer");
        if (state === "READY") {
          changeTimer();
        }
        break;
      case "save":
        //console.log("save");
        if (state === "READY") break;
        handleShowModal();
        break;
      case "record":
        if (state == "RECORD") break;
        //console.log("flip");
        setIsFlipped(!isFlipped);
        break;
      case "learn":
        if (state == "RECORD") break;
        //console.log("learn");
        goToLearnMode();
        break;
      case "rslt":
        if (state == "RECORD") break;
        //console.log("result");
        goToResult();
        break;
    }
  }, [btn]);

  useEffect(() => {
    setBtnInfo();
  }, []);

  return (
    <ChallengeContainer>
      <StarEffect numStars={80} />

      <VideoContainer
        ref={danceVideoRef}
        src={danceVideoPath}
        playsInline
        onEnded={handleShowModal}
        className={isFlipped ? "flip" : ""}
        crossOrigin="anonymous"
      ></VideoContainer>

      <UserContainer id="dom">
        <UserVideoContainer ref={userVideoRef} autoPlay playsInline></UserVideoContainer>
        {state === "READY" ? (
          <Timer>{timer}</Timer>
        ) : (
          <RecordingComponent>
            <RecordingTEXT>REC</RecordingTEXT>
            <Recording src={recordingImg} />
          </RecordingComponent>
        )}
        <VideoMotionButtonList>
          {state === "READY" ? (
            <div className="foldList">
              <VideoMotionButton
                icon={<RadioButtonChecked />}
                toolTip="녹화"
                onClick={showCancelButton}
                id="visible"
                progress={visibleCount}
                isVisible={state === "READY"}
              />
              <VideoMotionButton
                icon={<TimerRounded />}
                toolTip="타이머"
                onClick={changeTimer}
                id="timer"
                progress={timerCount}
                isVisible={state === "READY"}
              />
              <VideoMotionButton
                icon={<Flip />}
                toolTip="거울 모드"
                onClick={() => setIsFlipped(!isFlipped)}
                id="record"
                progress={recordCount}
                isVisible={state === "READY"}
              />
              <VideoMotionButton
                icon={<Movie />}
                toolTip="마이페이지"
                onClick={goToResult}
                id="rslt"
                progress={resultCount}
                isVisible={state === "READY"}
              />
              <VideoMotionButton
                icon={<DirectionsRun />}
                toolTip="연습 모드로 이동"
                onClick={goToLearnMode}
                id="learn"
                progress={learnCount}
                isVisible={state === "READY"}
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
                isVisible={state === "RECORD"}
              />
              <VideoMotionButton
                icon={<Save />}
                toolTip="저장"
                onClick={handleShowModal}
                id="save"
                progress={saveCount}
                isVisible={state === "RECORD"}
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
