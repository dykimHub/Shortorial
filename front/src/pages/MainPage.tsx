import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import { Shorts } from "../constants/types";
import { getShortsList, getTryCount } from "../apis/shorts";
import Header from "../components/header/Header";
import ShortsVideoItem from "../components/shorts/ShortsVideoItem";
import { CancelPresentation, EmojiPeople, MusicNote, TimerOutlined } from "@mui/icons-material";

const MainPage = () => {
  const navigate = useNavigate();
  const [showDetails, setShowDetails] = useState<boolean>(false);
  const [selectedShorts, setSelectedShorts] = useState<Shorts | null>(null);

  const [isLoading, setIsLoading] = useState(true);
  const [allShortsList, setAllShortsList] = useState<Shorts[]>();
  const [popularShortsList, setPopularShortsList] = useState<Shorts[]>();

  const openModal = (shorts: Shorts) => {
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

  const loadPopularShortsList = async () => {
    const data = await getShortsList();
    if (data) setPopularShortsList([data[0], data[1], data[2]]);
  };

  useEffect(() => {
    loadAllShortsList();
    loadPopularShortsList();
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
        <Section>
          <SectionTitle>이 쇼츠에 도전해보세요!</SectionTitle>
          <SectionConents className="nowrap">
            {popularShortsList?.map((shorts) => (
              <ShortsVideoItem
                key={shorts.shortsNo}
                shortsInfo={shorts}
                isLoading={isLoading}
                isSerise
                onClick={openModal(shorts)}
              ></ShortsVideoItem>
            ))}
          </SectionConents>
        </Section>
        <Section>
          <SectionTitle>실시간 인기 쇼츠</SectionTitle>
          <SectionConents className="nowrap">
            {popularShortsList?.map((shorts) => (
              <ShortsVideoItem
                key={shorts.shortsNo}
                shortsInfo={shorts}
                isLoading={isLoading}
                isSerise
              />
            ))}
          </SectionConents>
        </Section>
        <Section>
          <SectionTitle>둘러보기</SectionTitle>
          <SectionConents>
            {allShortsList?.map((shorts) => (
              <ShortsVideoItem key={shorts.shortsNo} shortsInfo={shorts} isLoading={isLoading} />
            ))}
          </SectionConents>
        </Section>
      </SectionWrapper>
      {showDetails && selectedShorts && (
        <DetailContainer>
          <CancelIcon>
            <CancelPresentation onClick={closeModal} />
          </CancelIcon>
          <Details>
            <Detail
              icon={<MusicNote />}
              text={selectedShorts.shortsTitle}
              fontWeight="bold"
              fontSize="22px"
            ></Detail>
            <Detail text={selectedShorts.shortsDirector} fontSize="18px"></Detail>
            <Detail icon={<TimerOutlined />} text={`${selectedShorts.shortsTime}초`}></Detail>
            <Detail
              icon={<EmojiPeople />}
              text={`${selectedShorts.shortsChallengers}명의 챌린저`}
            ></Detail>
          </Details>
          <ButtonContainer>
            <RouteButton onClick={() => goToLearnMode(selectedShorts.shortsNo)}>
              연습모드
            </RouteButton>
            <RouteButton onClick={() => goToChallengeMode(selectedShorts.shortsNo)}>
              챌린지모드
            </RouteButton>
          </ButtonContainer>
        </DetailContainer>
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
  width: 100%;

  &.nowrap {
    flex-wrap: nowrap;
  }
`;

const DetailContainer = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 1;
  padding: 10px;
  width: 50%;
  height: 50%;
`;

const CancelIcon = styled.div`
  display: flex;
  justify-content: flex-end;
`;

const Details = styled.div`
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
`;

interface DetailType {
  text?: string;
  fontSize?: string;
  fontWeight?: string;
  icon?: JSX.Element;
}

const Detail = ({ icon, text, fontSize, fontWeight }: DetailType) => {
  return (
    <div style={{ fontSize: fontSize, fontWeight: fontWeight }}>
      {icon} {text}
    </div>
  );
};

const ButtonContainer = styled.div`
  display: flex;
  justify-content: center;
`;

const RouteButton = styled.button`
  border: 3px solid black;
  border-radius: 20px;
  background-color: #f3f3f3;
  color: black;
  padding: 8px;
  cursor: pointer;
  margin: 5px; 0px;
  font-size: 14px;

  &:hover {
    background-color: #FF7EA0;; 
  }
`;

export default MainPage;
