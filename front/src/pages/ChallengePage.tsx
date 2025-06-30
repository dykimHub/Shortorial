import { useCallback, useRef, useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import styled, { keyframes } from "styled-components";
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
import recordingImg from "../assets/challenge/recording.svg";
import StarEffect from "../components/style/StarEffect";
import loading from "../assets/challenge/loading.gif";
import complete from "../assets/challenge/complete.svg";
import uncomplete from "../assets/challenge/uncomplete.svg";
// 타입 및 함수
import { getShortsInfo } from "../apis/shorts";
import { getS3Blob, uploadShortsToS3 } from "../apis/s3";
import { Shorts } from "../constants/types";
import { addAudioToVideo, extractAudio, flipUserVideo } from "../utils/ffmpegUtils";
import LoadingModalComponent from "../components/modal/LoadingModalComponent";
import VideoMotionButton from "../components/button/VideoMotionButton";
// 모션
import { NormalizedLandmark } from "@mediapipe/tasks-vision";
import { predictWebcamChallenge, setBtnInfo } from "../modules/Motion";
import { useBtnStore, useMotionDetectionStore } from "../store/useMotionStore";

const ChallengePage = () => {
  const navigate = useNavigate();
  const params = useParams();
  // 비디오
  const danceVideoRef = useRef<HTMLVideoElement>(null);
  const userVideoRef = useRef<HTMLVideoElement>(null);
  const [shorts, setShorts] = useState<Shorts | null>(null);
  // 웹캠
  const [mediaRecorder, setMediaRecorder] = useState<MediaRecorder | null>(null);
  const [stream, setStream] = useState<MediaStream | null>(null);
  enum ChallengeState {
    READY = "READY",
    RECORD = "RECORD",
  }
  const [state, setState] = useState<ChallengeState>(ChallengeState.READY);
  // 버튼
  const [timer, setTimer] = useState<number>(parseInt(localStorage.getItem("timer") ?? "3"));
  const [isFlipped, setIsFlipped] = useState<boolean>(false);
  // 모달
  const [show, setShow] = useState(false);
  const [loadPath, setLoadPath] = useState<string>("");
  const [ffmpegLog, setFfmpegLog] = useState<string>("");

  // 모션 인식 카운트
  const { btn, setBtn } = useBtnStore();
  const { visibleCount, timerCount, recordCount, learnCount, resultCount, saveCount } =
    useMotionDetectionStore();

  // 사용자가 클릭한 쇼츠 조회
  const loadDanceVideo = async () => setShorts(await getShortsInfo(`${params.shortsId}`));

  // 녹화 시작 버튼
  // 1. 타이머 카운트
  const handleStartCountdown = () => {
    let count = timer;
    const intervalId = setInterval(() => {
      if (count <= 1) {
        clearInterval(intervalId);
        setTimer(timer);
        startRecording();
      } else {
        setTimer((prev) => prev - 1);
        count -= 1;
      }
    }, 1000);
  };
  // 2. 녹화 시작
  const startRecording = async () => {
    // 버튼 목록 전환
    setState(ChallengeState.RECORD);
    // 녹화 진행
    if (shorts?.shortsS3Key) {
      await startRecordingHandler(shorts.shortsS3Key);
    }
  };
  // 3. 녹화 중지 명령 대기
  const startRecordingHandler = async (shortsS3Key: string) => {
    if (!mediaRecorder) throw new Error();

    try {
      const chunks: BlobPart[] = [];
      mediaRecorder.ondataavailable = (e) => chunks.push(e.data);

      // mediaRecorder.stop() 트리거
      mediaRecorder.onstop = async () => {
        // 쇼츠, 사용자 비디오 블롭 변환
        const shortsBlob = await getS3Blob(shortsS3Key);
        const userBlob = new Blob(chunks, { type: mediaRecorder.mimeType });
        // 비디오 처리
        await extractAudio({ shortsBlob, setFfmpegLog });
        await addAudioToVideo({ userBlob, setFfmpegLog });
        const finalVideo: Blob = await flipUserVideo({ setFfmpegLog });
        await s3Upload(finalVideo);

        handleCloseModal();
      };

      // MediaRecorder 실행
      mediaRecorder.start();
      // 쇼츠 실행
      if (danceVideoRef.current) danceVideoRef.current.play();
    } catch (e) {
      console.error(e);
      alert("녹화 도중 오류가 발생했습니다. 다시 시도해 주세요.");
    }
  };

  // 4. 최종 비디오 S3 업로드
  const s3Upload = async (finalBlob: Blob) => {
    try {
      await uploadShortsToS3(finalBlob);

      setLoadPath(complete);
      setFfmpegLog("저장 완료");
    } catch (error) {
      setLoadPath(uncomplete);
      setFfmpegLog("저장 실패");
      console.error("s3 upload fail", error);
    }
  };

  // 저장 버튼
  // 1. 모달 열기
  const handleShowModal = async () => {
    if (mediaRecorder) {
      // mediaRecorder.onstop 실행
      mediaRecorder.stop();
      setShow(true);
      setFfmpegLog("처리 준비...");
      setLoadPath(loading);

      // 비디오 초기화
      if (danceVideoRef.current) {
        danceVideoRef.current.pause();
        danceVideoRef.current.currentTime = 0;
      }
    }
  };
  // 2. 모달 닫기
  const handleCloseModal = () => {
    setState(ChallengeState.READY);
    setTimeout(() => {
      setShow(false);
    }, 2000);
  };

  // 타이머 버튼
  const changeTimer = () => {
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

  // 모션 인식 설정
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

      const options = {
        audioBitsPerSecond: 128000,
        videoBitsPerSecond: 2500000,
        // H.264(비디오) + AAC(오디오) 코덱의 MP4
        mimeType: "video/mp4;codecs=avc1.64003E,mp4a.40.2",
      };

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

  // 화면 크기 계산
  const initVideoSize = (videoRef: React.RefObject<HTMLVideoElement>) => {
    if (videoRef.current) {
      switch (screen.orientation.type) {
        case "landscape-primary":
        case "landscape-secondary":
          videoRef.current.height = window.innerHeight;
          videoRef.current.width = Math.floor((window.innerHeight * 9) / 16);
          break;
        case "portrait-primary":
        case "portrait-secondary":
          videoRef.current.width = window.innerWidth;
          videoRef.current.height = Math.floor((window.innerWidth * 16) / 9);
      }
    }
  };

  // state 변화 감지
  useEffect(() => {
    setBtnInfo();
  }, [state]);

  // btn 변화 감지
  useEffect(() => {
    switch (btn) {
      case "visible":
        //console.log("visible");
        if (state === ChallengeState.READY) handleStartCountdown();
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

  // 초기 실행
  useEffect(() => {
    loadDanceVideo();
    setInit();

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
                onClick={() => handleStartCountdown()}
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
