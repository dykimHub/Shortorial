import { useCallback, useEffect, useRef, useState } from "react";
import styled from "styled-components";
import {
  Flip,
  Pause,
  PlayArrow,
  Repeat,
  Videocam,
  // Visibility,
  // VisibilityOff,
} from "@mui/icons-material";
import noRepeat from "/src/assets/icon/repeat-off.svg";
import { useNavigate, useParams } from "react-router-dom";
import { VideoSection, Shorts } from "../constants/types";
import { predictVideo, setBtnInfo } from "../modules/Motion";
import { getShortsInfo } from "../apis/shorts";
import useLearnStore from "../store/useLearnStore";
import { useActionStore, useBtnStore, useMotionDetectionStore } from "../store/useMotionStore";
import SectionButtonList from "../components/buttonList/SectionButtonList";
import MotionCamera from "../components/motion/MotionCamera";
import VideoMotionButton from "../components/button/VideoMotionButton";
//import { Acc } from "../modules/Acc";
import {
  useMotionLandmarkStore,
  useVideoLandmarkStore,
  //useValueStore,
  useCountStore,
} from "../store/useAccStore";

//import greatImage from "../assets/score/great.png";
//import goodImage from "../assets/score/good.png";
import StarEffect from "../components/style/StarEffect";

const LearnPage = () => {
  type LearnState = "INIT" | "PAUSE" | "READY" | "PLAY";

  const videoRef = useRef<HTMLVideoElement>(null);
  const video = videoRef.current;
  const leftSectionRef = useRef<HTMLDivElement>(null);
  const centerSectionRef = useRef<HTMLDivElement>(null);

  const [videoSize, setVideoSize] = useState({ width: 0, height: 0 });
  const [centerSectionSize, setCenterSectionSize] = useState({
    width: 0,
    height: 0,
  });

  const [startFlag, setStartFlag] = useState(false);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);
  const interval = intervalRef.current;

  const navigate = useNavigate();
  const params = useParams();

  const [state, setState] = useState<LearnState>("INIT");

  const [videoInfo, setVideoInfo] = useState<Shorts>({
    shortsId: 0,
    shortsTime: 0,
    shortsTitle: "",
    shortsSource: "",
    shortsChallengerNum: 0,
    shortsS3key: "",
    shortsS3URL: "",
    shortsMusicSinger: "",
    shortsMusicTitle: "",
  });

  const [sectionList, setSectionList] = useState<VideoSection[]>([]);

  const [currentTime, setCurrentTime] = useLearnStore((state) => [
    state.currentTime,
    state.setCurrentTime,
  ]);

  const [timer, resetTimer, countdownTimer] = useLearnStore((state) => [
    state.timer,
    state.resetTimer,
    state.countdownTimer,
  ]);

  const [isLooping, loopSection, setIsLooping, setLoopSection] = useLearnStore((state) => [
    state.isLooping,
    state.loopSection,
    state.setIsLooping,
    state.setLoopSection,
  ]);

  const [isFlipped, setIsFlipped] = useLearnStore((state) => [state.isFlipped, state.setIsFlipped]);

  const [playSpeed, changePlaySpeed] = useLearnStore((state) => [
    state.playSpeed,
    state.changePlaySpeed,
  ]);

  const currentSection = useLearnStore((state) => state.currentSection);
  //const setCurrentSection = useLearnStore((state) => state.setCurrentSection);

  const btn = useBtnStore((state) => state.btn);
  const action = useActionStore((state) => state.action);
  const [canAction, setCanAction] = useState(true);

  const [playCount, challengeCount, repeatCount, flipCount, speedCount, canvasCount] =
    useMotionDetectionStore((state) => [
      state.playCount,
      state.challengeCount,
      state.repeatCount,
      state.flipCount,
      state.speedCount,
      state.canvasCount,
    ]);

  // 영상 정보 가져오기
  const loadVideo = useCallback(async () => {
    if (params.shortsId) {
      const data: Shorts = await getShortsInfo(params.shortsId);
      if (data) {
        setVideoInfo(data);
        initSectionList(data.shortsTime);
        setState("PAUSE");
      }
    }
  }, [params.shortsId]);

  // 구간 리스트 초기화
  const initSectionList = (videoLength: number) => {
    const secondsPerSection = 3;
    const numberOfSections = videoLength / secondsPerSection;
    const result: VideoSection[] = [];

    for (let i = 0; i < numberOfSections; i++) {
      result.push({
        id: i,
        start: i * secondsPerSection,
        end: (i + 1) * secondsPerSection,
        //acc: 0,
        //maxAcc: 0,
      });
    }
    console.log(sectionList);

    setSectionList(result);
  };

  // 영상 시간 이동하기
  const moveVideoTime = useCallback(
    (startTime: number) => {
      if (video) {
        video.currentTime = startTime;
        setCurrentTime(startTime);
      }
    },
    [setCurrentTime, video]
  );

  // 타이머 인터벌 초기화
  const initInterval = useCallback(() => {
    if (interval) clearInterval(interval);
    intervalRef.current = null;
    resetTimer();
  }, [interval, resetTimer]);

  // 카운트다운 시작
  const startCountdown = useCallback(() => {
    if (interval) initInterval();
    intervalRef.current = setInterval(countdownTimer, 1000);
    setState("READY");
  }, [countdownTimer, initInterval, interval]);

  // 영상 재생
  const playVideo = useCallback(() => {
    if (video) {
      if (video.ended) {
        video.currentTime = 0;
        setStartFlag(false);
        setCurrentTime(0);
      }

      video.playbackRate = playSpeed;
      video.play();
      setState("PLAY");
      if (!startFlag) {
        setTimeout(() => {
          setStartFlag(true);
        }, 3000);
      }
    }
  }, [playSpeed, setCurrentTime, video]);

  // 영상 일시정지
  const pauseVideo = useCallback(() => {
    if (video) {
      video.pause();
      initInterval();
      moveVideoTime(currentSection.start); // 현재 구간 시작 시간으로 이동
      setState("PAUSE");
      setStartFlag(false);
      // setAccFlag(false);
    }
  }, [currentSection.start, initInterval, moveVideoTime, video]);

  const [flag, setFlag] = useState(false);
  // 영상의 현재 시간을 갱신, 반복인 경우 현재 시간 이전으로 되돌아가기
  const handleTimeUpdate = useCallback(() => {
    if (video) {
      // 반복하지 않는 경우
      if (!isLooping) {
        //console.log("not Loop");
        setCurrentTime(video.currentTime);
        if (video.ended) {
          {
            video.currentTime = 0;
            setState("PAUSE");
            setFlag(true);
            setTimeout(() => {
              setStartFlag(false);
            }, 500);
            // setAccFlag(false);
          }
        }
        return;
      }

      // 반복하는 경우
      else if (isLooping && loopSection) {
        if (video.currentTime >= loopSection.end || video.ended) {
          setFlag(true);
          video.currentTime = loopSection.start;
          setCurrentTime(loopSection.start);
          if (video.ended) playVideo();
          return;
        }
      }
    }
  }, [isLooping, loopSection, playVideo, setCurrentTime, video]);

  // 구간 반복 토글
  const toggleLooping = () => {
    if (isLooping) {
      setIsLooping(false);
      setLoopSection(null);
    } else {
      setIsLooping(true);
      setLoopSection(currentSection);
    }
  };

  // 다음 구간으로 이동
  const moveToNextSection = () => {
    if (currentSection.id >= sectionList.length - 1) return;

    if (video) {
      const nextTime = sectionList[currentSection.id + 1].start;
      video.currentTime = nextTime;
      setCurrentTime(nextTime);
    }
  };

  // 이전 구간으로 이동
  const moveToPrevSection = () => {
    if (currentSection.id <= 0) return;

    if (video) {
      const nextTime = sectionList[currentSection.id - 1].start;
      video.currentTime = nextTime;
      setCurrentTime(nextTime);
    }
  };

  // 컴포넌트가 처음 마운트될 때 실행
  useEffect(() => {
    loadVideo();
  }, [loadVideo]);

  // 비디오 크기 초기화
  const initVideoSize = useCallback(() => {
    const height = centerSectionSize.height;
    const width = Math.floor((centerSectionSize.height * 9) / 16);
    setVideoSize({ width, height });
  }, [centerSectionSize.height]);

  // Left Section 너비 반환
  const getLeftSectionWidth = useCallback(() => {
    if (leftSectionRef.current) {
      const { width } = leftSectionRef.current.getBoundingClientRect() ?? 0;
      return width;
    }
  }, []);

  // 화면 크기 바뀔 때마다 실행 - videoSize 초기화
  const handleResize = useCallback(() => {
    if (centerSectionRef.current) {
      const { width, height } = centerSectionRef.current.getBoundingClientRect();
      setCenterSectionSize({ width, height });
      initVideoSize();
    }
  }, [initVideoSize]);

  // window resize 이벤트 추가
  useEffect(() => {
    setTimeout(handleResize, 100);
    window.addEventListener("resize", () => setTimeout(handleResize, 100));

    return () => window.removeEventListener("resize", () => setTimeout(handleResize, 200));
  }, [handleResize, initVideoSize]);

  // 화면의 준비가 모두 완료했을 때 실행
  useEffect(() => {
    if (state === "INIT") {
      ``;
      if (videoInfo && sectionList && centerSectionRef) {
        initInterval();
      }
    }
  }, [centerSectionRef, initInterval, sectionList, state, videoInfo]);

  // 카운트다운이 끝나면 영상 재생
  useEffect(() => {
    if (interval && timer <= 0) {
      initInterval();
      playVideo();
    }
  }, [initInterval, interval, playVideo, timer]);

  useEffect(() => {
    if (video) predictVideo(video);
  }, []);

  // 영상에 timeupdate 이벤트 추가
  useEffect(() => {
    if (video) video.addEventListener("timeupdate", handleTimeUpdate);

    return () => {
      if (video) video.removeEventListener("timeupdate", handleTimeUpdate);
    };
  }, [handleTimeUpdate, video]);

  // 영상 버튼 정보 가져오기
  useEffect(() => {
    setBtnInfo();
  }, [centerSectionSize]);

  // 영상 이동 액션 감지
  useEffect(() => {
    if (state !== "PAUSE") return;

    switch (action) {
      case "prev":
        if (canAction) {
          moveToPrevSection();
          setCanAction(false);
        }
        break;
      case "next":
        if (canAction) {
          moveToNextSection();
          setCanAction(false);
        }
        break;
      case "none":
        setCanAction(true);
        break;
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [action]);

  // 영상 버튼 모션 액션 감지
  useEffect(() => {
    switch (btn) {
      case "play":
        if (state === "PAUSE") startCountdown();
        else pauseVideo();
        break;
      case "challenge":
        if (state === "PAUSE") navigate(`/challenge/${params.shortsId}`);
        break;
      case "repeat":
        if (state === "PAUSE") toggleLooping();
        break;
      case "flip":
        if (state === "PAUSE") setIsFlipped(!isFlipped);
        break;
      case "speed":
        if (state === "PAUSE") changePlaySpeed();
        break;
      case "canvas":
        //console.log("canvas");
        canvasSetting();
        break;
      default:
        break;
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [btn]);

  const [isCanvas, setIsCanvas] = useState(false);

  const canvasSetting = () => {
    setIsCanvas(!isCanvas);
  };

  // landmark 정보
  const videoLandmark = useVideoLandmarkStore.getState().videoLandmark;
  const motionLandmark = useMotionLandmarkStore.getState().motionLandmark;
  //const [acc, setAcc] = useState(0);
  //const [accValue, setAccValue] = useValueStore((state) => [state.accValue, state.setAccValue]);
  const [count, setCount] = useCountStore((state) => [state.count, state.setCount]);

  //const [scoreImage, setScoreImage] = useState("");
  // 정확도 계산하기
  // useEffect(() => {
  //   if (sectionList.length > 0 || state === "PAUSE") {
  //     const sectionListTmp = sectionList;

  //     if (currentSection.id > 0) {
  //       sectionListTmp[currentSection.id - 1].maxAcc = Math.max(
  //         //acc / count,
  //         sectionListTmp[currentSection.id - 1].acc
  //       );
  //       sectionListTmp[currentSection.id - 1].acc = acc / count;
  //     } else {
  //       sectionListTmp[sectionListTmp.length - 1].maxAcc = Math.max(
  //         //acc / count,
  //         sectionListTmp[sectionListTmp.length - 1].acc
  //       );
  //       sectionListTmp[sectionListTmp.length - 1].acc = acc / count;
  //     }
  //     settingImage(acc / count);
  //     setSectionList(sectionListTmp);
  //     setAccValue(0);
  //     setCount(0);
  //   }
  // }, [currentSection.id, state]);

  useEffect(() => {
    if (sectionList.length > 0 && flag) {
      const sectionListTmp = sectionList;
      // sectionListTmp[currentSection.id].maxAcc = Math.max(
      //   acc / count,
      //   sectionListTmp[currentSection.id].acc
      // );
      //sectionListTmp[currentSection.id].acc = acc / count;
      setSectionList(sectionListTmp);
    }
    //settingImage(acc / count);
    //setAccValue(0);
    setCount(0);
    setFlag(false);
  }, [flag]);

  //const [isVisible, setIsVisible] = useState(false);
  // const settingImage = (score: number) => {
  //   setIsVisible(true);
  //   if (score > 40) {
  //     setScoreImage(greatImage);
  //   } else {
  //     setScoreImage(goodImage);
  //   }
  //   setTimeout(() => {
  //     setIsVisible(false);
  //   }, 500);
  // };

  // useEffect(() => {
  //   if (state !== "PLAY") {
  //     setIsVisible(false);
  //   }
  // }, [state]);
  useEffect(() => {
    const fetchData = async () => {
      if (videoLandmark && motionLandmark) {
        //setAcc(await Acc(videoLandmark, motionLandmark));
        if (state === "PLAY") {
          //setAccValue(acc + accValue);
          setCount(count + 1);
        }
      }
    };

    fetchData();
  }, [videoLandmark, motionLandmark]);

  return (
    <Container>
      <StarEffect numStars={80} />
      {state === "INIT" ? (
        <LoadingText>Loading...</LoadingText>
      ) : (
        <>
          <LeftSection ref={leftSectionRef}>
            {/* <div style={{ color: "white" }}>왼쪽 패널</div> */}
            <SectionButtonList
              sectionList={sectionList}
              parentWidth={getLeftSectionWidth()}
              currentTime={currentTime}
              isLooping={isLooping}
              clickHandler={(section) => moveVideoTime(section.start)}
            />
          </LeftSection>
          <CenterSection ref={centerSectionRef}>
            <VideoContainer>
              <video
                width={videoSize.width}
                height={videoSize.height}
                src={videoInfo.shortsS3URL}
                ref={videoRef}
                className={isFlipped ? "flip" : ""}
                crossOrigin="anonymous"
              ></video>
            </VideoContainer>
            <VideoContainer id="dom">
              <MotionCamera
                width={videoSize.width}
                height={videoSize.height}
                className="camera flip"
                autoPlay
                isCanvas={isCanvas}
              ></MotionCamera>
              {/* <Image src={scoreImage} $visible={isVisible && startFlag} /> */}
              <VideoMotionButtonList>
                {state === "PAUSE" ? (
                  <VideoMotionButton
                    id="play"
                    icon={<PlayArrow />}
                    toolTip="재생"
                    onClick={startCountdown}
                    progress={playCount}
                  />
                ) : (
                  <VideoMotionButton
                    id="play"
                    icon={<Pause />}
                    toolTip="일시정지"
                    onClick={pauseVideo}
                    progress={playCount}
                  />
                )}
                <FoldList>
                  {isLooping ? (
                    <VideoMotionButton
                      id="repeat"
                      icon={<Repeat />}
                      toolTip="구간 반복 해제"
                      onClick={toggleLooping}
                      progress={repeatCount}
                      isVisible={state === "PAUSE"}
                    />
                  ) : (
                    <VideoMotionButton
                      id="repeat"
                      imgSrc={noRepeat}
                      toolTip="구간 반복"
                      onClick={toggleLooping}
                      progress={repeatCount}
                      isVisible={state === "PAUSE"}
                    />
                  )}
                  <VideoMotionButton
                    id="flip"
                    icon={<Flip />}
                    toolTip="거울 모드"
                    onClick={() => setIsFlipped(!isFlipped)}
                    progress={flipCount}
                    isVisible={state === "PAUSE"}
                  />
                  <VideoMotionButton
                    id="speed"
                    text={`${playSpeed}x`}
                    toolTip="재생 속도"
                    onClick={changePlaySpeed}
                    progress={speedCount}
                    isVisible={state === "PAUSE"}
                  />
                  <VideoMotionButton
                    id="challenge"
                    icon={<Videocam />}
                    toolTip="챌린지 모드로 이동"
                    link={`/challenge/${params.shortsId}`}
                    progress={challengeCount}
                    isVisible={state === "PAUSE"}
                  />
                  {/* <VideoMotionButton
                    id="canvasBtn"
                    icon={isCanvas ? <VisibilityOff /> : <Visibility />}
                    toolTip={isCanvas ? "가이드 해제" : "가이드 표시"}
                    progress={canvasCount}
                    isVisible={state === "PAUSE"}
                    onClick={canvasSetting}
                  /> */}
                </FoldList>
              </VideoMotionButtonList>
              {state === "READY" && <Timer>{timer}</Timer>}
            </VideoContainer>
          </CenterSection>
          <RightSection></RightSection>
        </>
      )}
    </Container>
  );
};

const Container = styled.div`
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  background-color: #000;

  @media screen and (orientation: portrait) {
    display: flex;
    flex-direction: column-reverse;
  }

  background: linear-gradient(
    180deg,
    rgba(0, 0, 0, 1) 0%,
    rgba(48, 13, 45, 1) 80%,
    rgba(112, 0, 102, 1) 100%
  );
`;

const Section = styled.section`
  position: relative;
  display: flex;
`;

const RightSection = styled(Section)`
  flex: 1;
  margin: 8px;

  @media screen and (orientation: portrait) {
    display: none;
  }
`;

const CenterSection = styled(Section)`
  flex: 1;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;

  @media screen and (orientation: portrait) {
    height: 80%;
    flex: auto;
  }
`;

const LeftSection = styled(Section)`
  flex: 1;
  justify-content: flex-end;
  align-items: center;
  margin: 8px;
  color: #fff;

  @media screen and (orientation: portrait) {
    align-items: center;
    height: 20%;
    flex: auto;
  }
`;

const VideoContainer = styled.div`
  position: relative;
  display: flex;
  height: 100%;
  justify-content: center;
  video {
    display: flex;
    object-fit: cover;
  }

  video.flip {
    transform: scaleX(-1);
  }

  @media screen and (orientation: portrait) {
    .camera {
      display: none;
    }
  }
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
  box-sizing: border-box;
`;

const FoldList = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  height: auto;
  min-height: 80%;
  max-height: 100%;
`;

const LoadingText = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  font-size: 24px;
  color: #fff;
`;

// const Image = styled.img<{ $visible: boolean }>`
//   position: absolute;
//   width: 50%;
//   display: ${(props) => (props.$visible ? "flex" : "none")};
//   z-index: 2;
// `;
export default LearnPage;
