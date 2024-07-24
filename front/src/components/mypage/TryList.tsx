import { useEffect, useState } from "react";
import { getTryShorts, deleteTriedShorts, getTryCount } from "../../apis/triedshorts";
import styled, { keyframes } from "styled-components";
import ShortsVideoItem from "../shorts/ShortsVideoItem";
import {
  CancelPresentation,
  Copyright,
  EmojiPeople,
  MusicNote,
  TimerOutlined,
  Mic,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { Shorts, TryShorts } from "../../constants/types";

export default function TryList() {
  const navigate = useNavigate();
  const [showDetails, setShowDetails] = useState<boolean>(false);
  const [selectedShorts, setSelectedShorts] = useState<Shorts | null>(null);

  const [isLoading, setIsLoading] = useState(true);
  const [tryShortsList, settryShortsList] = useState<TryShorts[]>([]);

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
  const loadtryShortsList = async () => {
    const data = await getTryShorts();
    if (data) settryShortsList(data);
  };

  const handleDeleteButton = async (shortsId: number) => {
    await deleteTriedShorts(shortsId);
    loadtryShortsList();
  };

  useEffect(() => {
    loadtryShortsList();
  }, []);

  useEffect(() => {
    if (tryShortsList) {
      setIsLoading(false);
    }
  }, [tryShortsList]);

  return (
    <Container>
      <SectionWrapper>
        <Section>
          <SectionConents>
            {tryShortsList.length === 0 ? (
              <P>시도한 영상이 없습니다</P>
            ) : (
              tryShortsList?.map((tryShorts) => (
                <ShortsVideoItem
                  key={tryShorts.triedShortsId}
                  shortsInfo={tryShorts.shortsDto}
                  isLoading={isLoading}
                  onClick={openModal(tryShorts.shortsDto)}
                  shortsId={tryShorts.shortsDto.shortsId}
                  onDelete={handleDeleteButton}
                />
              ))
            )}
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
                icon={<Mic />}
                text={`${selectedShorts.shortsMusicSinger}`}
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
                  쇼츠 출처
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
}
const P = styled.p`
  text-align: center;
`;

const Container = styled.div`
  position: relative;
  width: 100%;
  height: 100%;
  background-color: rgba(251, 37, 118, 0.1);
`;

const Section = styled.section`
  position: relative;
  margin: 0px 16px;
  box-sizing: border-box;
`;

const SectionWrapper = styled.div`
  position: relative;
  max-width: 1100px;
  margin: 0 auto;
`;

const SectionConents = styled.div`
  display: flex;
  flex-wrap: wrap;

  &.nowrap {
    justify-content: center;
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
  background-color: rgba(240, 240, 240, 0.8);
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
