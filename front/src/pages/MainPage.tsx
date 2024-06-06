import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import { RecomShorts, Shorts } from "../constants/types";
import {
  getRecommendedShorts,
  getShortsList,
  getTopRankingShorts,
  getTryCount,
} from "../apis/shorts";
import Header from "../components/header/Header";
import ShortsVideoItem from "../components/shorts/ShortsVideoItem";
import {
  CancelPresentation,
  Copyright,
  EmojiPeople,
  MusicNote,
  TimerOutlined,
} from "@mui/icons-material";

const MainPage = () => {
  const navigate = useNavigate();
  const [showDetails, setShowDetails] = useState<boolean>(false);
  const [selectedShorts, setSelectedShorts] = useState<Shorts | RecomShorts | null>(null);

  const [isLoading, setIsLoading] = useState(true);
  const [allShortsList, setAllShortsList] = useState<Shorts[]>();
  const [popularShortsList, setPopularShortsList] = useState<Shorts[]>();
  const [recommendedShorts, setRecommendedShorts] = useState<RecomShorts[]>();

  const openModal = (shorts: Shorts | RecomShorts) => {
    return () => {
      setSelectedShorts(shorts);
      setShowDetails(true);
    };
  };

  const closeModal = () => {
    setShowDetails(false);
    setSelectedShorts(null);
  };

  const goToLearnMode = (shortsNo: number) => {
    getTryCount(shortsNo);
    navigate(`/learn/${shortsNo}`);
  };

  const goToChallengeMode = (shortsNo: number) => {
    getTryCount(shortsNo);
    navigate(`/challenge/${shortsNo}`);
  };

  // 둘러보기 쇼츠 리스트 가져오기
  const loadAllShortsList = async () => {
    const data = await getShortsList();
    if (data) setAllShortsList(data);
  };

  // 인기 쇼츠 리스트 가져오기
  const loadPopularShortsList = async () => {
    const data = await getTopRankingShorts();
    if (data) setPopularShortsList(data);
  };

  // 추천 쇼츠 리스트 가져오기
  const loadRecommendedShortsList = async () => {
    const data = await getRecommendedShorts();
    if (data) setRecommendedShorts(data);
  };

  useEffect(() => {
    loadAllShortsList();
    loadPopularShortsList();
    loadRecommendedShortsList();
  }, []);

  useEffect(() => {
    if (popularShortsList && allShortsList) {
      setIsLoading(false);
    }
  }, [allShortsList, popularShortsList]);

  return (
    <Container>
      <Header />
      <SectionWrapper>
        {recommendedShorts && (
          <SeriesSection style={{ background: "#fefae0" }}>
            <SectionHeaderContainer>
              <SectionTitle>⭐ 이런 챌린지는 어떠세요?</SectionTitle>
              <p>당신이 좋아할 만한 챌린지를 추천해드릴게요.</p>
            </SectionHeaderContainer>
            <SectionConents className="nowrap">
              {recommendedShorts?.map((shorts) => (
                <ShortsVideoItem
                  key={shorts.shortsId}
                  shortsInfo={shorts}
                  isLoading={isLoading}
                  isSerise
                  onClick={openModal(shorts)}
                ></ShortsVideoItem>
              ))}
            </SectionConents>
          </SeriesSection>
        )}
        <SeriesSection style={{ background: "#ffe5ec" }}>
          <SectionHeaderContainer>
            <SectionTitle>🔥 요즘 이 챌린지가 가장 인기 있어요</SectionTitle>
            <p>{`숏토리얼에서 최근 가장 인기가 많은 챌린지들을 소개합니다.\n지금 바로 유행에 동참하세요!`}</p>
          </SectionHeaderContainer>
          <SectionConents className="nowrap">
            {popularShortsList?.map((shorts) => (
              <ShortsVideoItem
                key={shorts.shortsId}
                shortsInfo={shorts}
                isLoading={isLoading}
                isSerise
                onClick={openModal(shorts)}
              />
            ))}
          </SectionConents>
        </SeriesSection>
        <Section>
          <SectionTitle>둘러보기</SectionTitle>
          <SectionConents>
            {allShortsList?.map((shorts) => (
              <ShortsVideoItem
                key={shorts.shortsId}
                shortsInfo={shorts}
                isLoading={isLoading}
                onClick={openModal(shorts)}
              />
            ))}
          </SectionConents>
        </Section>
      </SectionWrapper>
      {showDetails && selectedShorts && (
        <Modal>
          <CancelIcon>
            <CancelPresentation onClick={closeModal} fontSize="large" />
          </CancelIcon>
          <Details>
            <Detail text={selectedShorts.shortsTitle} fontWeight="bold" fontSize="23px"></Detail>
            <div>
              <Detail
                icon={<MusicNote />}
                text={`${selectedShorts.shortsMusicTitle}`}
                fontSize="18px"
              ></Detail>

              <Detail
                icon={<TimerOutlined />}
                text={`${selectedShorts.shortsTime}초`}
                fontSize="18px"
              ></Detail>
              <Detail
                icon={<EmojiPeople />}
                text={`${selectedShorts.shortsChallengerNum}명의 챌린저`}
                fontSize="18px"
              ></Detail>
              <Detail icon={<Copyright />} fontSize="18px">
                <a href={selectedShorts.shortsSource} target="_blank">
                  원본 영상 보기
                </a>
              </Detail>
            </div>
          </Details>

          <ButtonContainer>
            <RouteButton onClick={() => goToLearnMode(selectedShorts.shortsId)}>
              연습모드
            </RouteButton>
            <RouteButton onClick={() => goToChallengeMode(selectedShorts.shortsId)}>
              챌린지모드
            </RouteButton>
          </ButtonContainer>
        </Modal>
      )}
    </Container>
  );
};

const Container = styled.div`
  position: relative;
  width: 100%;
  height: 100%;
`;

const Section = styled.section`
  position: relative;
  margin: 16px;
  box-sizing: border-box;
`;

const SectionWrapper = styled.div`
  position: relative;
  max-width: 1100px;
  margin: 0 auto;
  padding-top: 70px;
`;

const SectionTitle = styled.h3`
  font-size: 22px;
  font-weight: bold;
  margin: 1rem;
  margin-left: calc(var(--grid-item-margin) / 2);
`;

const SectionConents = styled.div`
  display: flex;
  flex-wrap: wrap;

  &.nowrap {
    justify-content: center;
  }
`;

const SeriesSection = styled.section`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  background: #ededed;
  border-radius: 16px;
  padding: 36px;
  margin: 48px 16px;

  @media screen and (max-width: 1024px) {
    flex-direction: column;
    justify-content: flex-start;
  }
`;

const SectionHeaderContainer = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-right: 16px;
  word-break: keep-all;
  white-space: pre-line;

  h3 {
    margin: 1rem 0;
  }

  @media screen and (max-width: 1024px) {
    margin: calc(var(--grid-item-margin) / 2);
  }
`;

const pulse = keyframes`
  0% { 
    transform: translate(-50%, -50%) scale(1); 
  }
  50% { 
    transform: translate(-50%, -50%) scale(1.1); 
  }
  100% { 
    transform: translate(-50%, -50%) scale(1); 
  }
`;

const Modal = styled.div`
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 1;
  padding: 20px;
  width: 50%;
  animation: ${pulse} 0.5s ease-in-out;

  @media (max-width: 700px) {
    width: 60%;
  }
`;

const CancelIcon = styled.div`
  display: flex;
  justify-content: flex-end;
  cursor: pointer;
`;

const Details = styled.div`
  display: flex;
  justify-content: space-evenly;
  flex-direction: column;
  align-items: center;
  margin-bottom: 10px;
`;

interface DetailType {
  text?: string;
  fontSize?: string;
  fontWeight?: string;
  icon?: JSX.Element;
  children?: React.ReactNode;
}

const Detail = ({ icon, text, fontSize, fontWeight, children }: DetailType) => {
  return (
    <div style={{ fontSize, fontWeight, margin: "5px 0px", display: "flex", alignItems: "center" }}>
      {icon} &nbsp; {text || children}
    </div>
  );
};

const ButtonContainer = styled.div`
  display: flex;
  justify-content: center;

  @media (max-aspect-ratio: 1/1) {
    flex-direction: column;
    padding: 0px 25px;
  }
`;

const RouteButton = styled.button`
  border: 2px solid #fb2576;
  border-radius: 10px;
  background-color: white;
  color: black;
  padding: 10px 20px;
  cursor: pointer;
  margin: 5px;

  &:hover {
    background-color: #d3d3d3;
  }
`;

export default MainPage;
