import { useCallback, useRef, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useParams } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import { createFFmpeg } from "@ffmpeg/ffmpeg";
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
import { uploadShortsToS3, getS3Blob } from "../apis/s3";
import loading from "../assets/challenge/loading.gif";
import complete from "../assets/challenge/complete.svg";
import recordingImg from "../assets/challenge/recording.svg";
import uncomplete from "../assets/challenge/uncomplete.svg";
import StarEffect from "../components/style/StarEffect";
import { Shorts } from "../constants/types";

const ChallengePage = () => {
  const navigate = useNavigate();
  const params = useParams();
  const ffmpeg = createFFmpeg({ log: false });

  const userVideoRef = useRef<HTMLVideoElement>(null);
  const danceVideoRef = useRef<HTMLVideoElement>(null);

  const [short, setShort] = useState<Shorts | null>(null);
  const [mediaRecorder, setMediaRecorder] = useState<MediaRecorder | null>(null);
  const [stream, setStream] = useState<MediaStream | null>(null);
  const [danceVideoPath, setDanceVideoPath] = useState<string>("");
  // const [danceVideoS3blob, setDanceVideoS3blob] = useState<Blob | null>(null);

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

    setFfmpegLog("대기중...");
    cancelRecording();
    mediaRecorder?.stop(); // recorder.onstop() 실행
  };

  const startRecording = () => {
    setState("RECORD");

    if (!stream) {
      alert("카메라 접근을 허용해주세요.");
      return;
    }

    try {
      const recorder = new MediaRecorder(stream); // 녹화형으로 변환
      const chunks: BlobPart[] = []; // 스트림 조각을 넣을 배열
      recorder.ondataavailable = (e) => chunks.push(e.data); // 스트림 조각이 어느 정도 커지면 push하기

      recorder.onstop = async () => {
        let s3blob: Blob | null = null;
        if (short) {
          s3blob = await getS3Blob(short.shortsS3Key); // 쇼츠 블롭화
          //console.log("s3blob:", s3blob);
        }
        if (!ffmpeg.isLoaded()) {
          await ffmpeg.load(); // ffmpeg 로드
        }

        const userVideoBlob = new Blob(chunks, { type: "video/mp4" }); // user video blob 생성

        const reader = new FileReader();

        if (s3blob) reader.readAsArrayBuffer(s3blob); // dance video blob array buffer로 변환
        reader.onloadend = async () => {
          const arrayBuffer = reader.result as ArrayBuffer;
          const uint8Array = new Uint8Array(arrayBuffer);
          ffmpeg.FS("writeFile", "danceVideo.mp4", uint8Array); // Blob을 가상 파일로 변환
        };

        ffmpeg.setProgress(({ ratio }) => {
          if (ratio > 0) {
            setLoadPath(loading);
            setFfmpegLog(`노래 추출... ${Math.round(ratio * 100)}%\n`);
          }
        });

        await ffmpeg.run(
          "-i",
          "danceVideo.mp4",
          "-vn", // 비디오 무시
          "-c:a",
          "copy", // aac 코덱 복사
          "dance_audio.m4a" // 오디오 파일 생성
        );

        // 비디오에 오디오 추가
        await addAudio(userVideoBlob);
      };

      recorder.start(); // 녹화 시작
      setMediaRecorder(recorder);
      danceVideoRef.current?.play(); // 댄스 비디오 시작
    } catch (error) {
      console.log(error);
      alert("녹화를 다시 시작해 주세요.");
    }
  };

  const addAudio = async (userVideoBlob: Blob) => {
    try {
      const reader = new FileReader();
      reader.readAsArrayBuffer(userVideoBlob);
      // 파일 읽기가 완료 되면
      reader.onloadend = async () => {
        const arrayBuffer = reader.result as ArrayBuffer;
        const uint8Array = new Uint8Array(arrayBuffer); // ffmpeg가 읽을 수 있는 8비트 정수 배열로 변환
        ffmpeg.FS("writeFile", "userVideo.mp4", uint8Array); // 사용자 비디오 가상 파일 만들기

        ffmpeg.setProgress(({ ratio }) => {
          if (ratio > 0) {
            setLoadPath(loading);
            setFfmpegLog(`노래 삽입... ${Math.round(ratio * 100)}%\n`);
          }
        });

        await ffmpeg.run(
          "-i",
          "userVideo.mp4", // 사용자 영상
          "-i",
          "dance_audio.m4a", // 원본 오디오
          "-map",
          "0:v:0", // 첫번째 파일(사용자 영상)의 0번째 스트림
          "-map",
          "1:a:0", // 두번째 파일(원본 오디오)의 0번째 스트림
          "-c:v",
          "copy", // 비디오 인코딩 복사
          "-c:a",
          "copy", // 오디오 인코딩 복사
          "-shortest", // 두 개 파일 중 짧은 쪽에 맞춤
          "finalUserVideo.mp4" // 파일 생성
        );

        ffmpeg.setProgress(({ ratio }) => {
          if (ratio > 0) {
            setLoadPath(loading);
            setFfmpegLog(`거울모드로 저장... ${Math.round(ratio * 100)}%\n`);
          }
        });

        await ffmpeg.run(
          "-i",
          "finalUserVideo.mp4",
          "-vf", // 비디오 필터
          "hflip", // 좌우반전
          "finalUserVideoFlip.mp4"
        );

        const userVideoFlipFinal = ffmpeg.FS("readFile", "finalUserVideoFlip.mp4");
        // 최종 파일 Blob 변환
        const userVideoFinalBlob = new Blob([userVideoFlipFinal.buffer], {
          type: "video/mp4",
        });

        // 최종파일 url 전달
        makeDownloadURL(userVideoFinalBlob);
      };
    } catch (error) {
      console.log(error);
      alert("오디오를 추가할 수 없습니다.");
    }
  };

  const makeDownloadURL = async (userVideoFinalBlob: Blob) => {
    try {
      await s3Upload(userVideoFinalBlob);
      setTimeout(handleCloseModal, 2000);
    } catch (error) {
      console.error("비디오 저장 중 오류 발생:", error);
    }
  };

  const s3Upload = async (blob: Blob) => {
    try {
      //const title = getCurrentDateTime();
      await uploadShortsToS3(blob);
      setLoadPath(complete);
      setFfmpegLog("저장 완료");
      //console.log("s3 upload success", uploadResponse.data);
    } catch (error) {
      setLoadPath(uncomplete);
      //if (error instanceof Error && error.stack) setFfmpegLog(error.stack);
      setFfmpegLog("저장 실패");
      console.error("s3 upload fail", error);
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
        console.log("record");
        if (state === "READY") {
          showCancelButton();
        } else {
          cancelRecording();
        }
        break;
      case "timer":
        console.log("timer");
        if (state === "READY") {
          changeTimer();
        }
        break;
      case "save":
        console.log("save");
        if (state === "READY") break;
        handleShowModal();
        break;
      case "record":
        if (state == "RECORD") break;
        console.log("flip");
        setIsFlipped(!isFlipped);
        break;
      case "learn":
        if (state == "RECORD") break;
        console.log("learn");
        goToLearnMode();
        break;
      case "rslt":
        if (state == "RECORD") break;
        console.log("result");
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
            <Recording src={recordingImg} />
            <RecordingTEXT>REC</RecordingTEXT>
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
`;

const Recording = styled.img`
  width: 15px;
  height: 15px;
  z-index: 1;
  animation: ${blinkEffect} 1s step-end infinite;
`;

const RecordingTEXT = styled.div`
  width: 15px;
  height: 15px;
  z-index: 1;
  margin-left: 13px;
  font-size: 15px;
  line-height: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
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
