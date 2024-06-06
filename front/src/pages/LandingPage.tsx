import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import heroImg from "/src/assets/landing/hero.jpg";
import deviceLandscape from "/src/assets/landing/device_landscape.png";
// import serviceMotionGif from "/src/assets/landing/serviceMotion.gif";
import serviceMotionGif1 from "/src/assets/landing/serviceMotion1.gif";
import serviceMotionGif2 from "/src/assets/landing/serviceMotion2.gif";
import serviceMotionGif3 from "/src/assets/landing/serviceMotion3.gif";

import Header from "../components/header/Header";
import useLoginStore from "../store/useLoginStore";
import BasicButton from "../components/button/BasicButton";
import useScrollFadeIn from "../hooks/useScrollFadeIn";
import ShortsVideoItem from "../components/shorts/ShortsVideoItem";
import { Shorts } from "../constants/types";
import { getTopRankingShorts } from "../apis/shorts";

const LandingPage = () => {
  const isLogin = useLoginStore((state) => state.getIsLogin());
  const navigate = useNavigate();

  const HeroTextScroll = useScrollFadeIn({ direction: "none", duration: 1 });
  const HeroCTAScroll = useScrollFadeIn({ direction: "none", duration: 1 });
  const FirstSectionScroll = useScrollFadeIn();
  const SecondSectionScroll = useScrollFadeIn();
  const ThirdSectionScroll = useScrollFadeIn();
  const ForthSectionScroll = useScrollFadeIn();
  const LastSectionScroll = useScrollFadeIn({ direction: "none", duration: 1 });

  const [popularShortsList, setPopularShortsList] = useState<Shorts[] | null>(null);

  const loadPopularShortsList = async () => {
    const data = await getTopRankingShorts();
    if (data) setPopularShortsList(data);
  };

  useEffect(() => {
    loadPopularShortsList();
  }, []);

  // 로그인한 유저의 접근 막기
  useEffect(() => {
    if (isLogin) navigate("/main");
  }, [isLogin, navigate]);

  return (
    <Container>
      <Header />
      <HeroContainer>
        <HeroImg src={heroImg} alt="" />
        <HeroTextContainer {...HeroTextScroll}>
          <h1 className="sectionTitle">{`SHORTORIAL에서\n쉽고 빠르게\n챌린지에 도전하세요`}</h1>
          <h2 className="subTitle">모션 인식 기반 댄스 챌린지 연습 서비스</h2>
        </HeroTextContainer>
        <CTAButtonContainer {...HeroCTAScroll}>
          <BasicButton
            text="지금 시작하기"
            style={CTAButtonStyled}
            onClick={() => navigate("/main")}
          />
        </CTAButtonContainer>
      </HeroContainer>
      <Section {...FirstSectionScroll}>
        <h1 className="sectionTitle">{`숏토리얼과 함께라면 여기가 나만의 연습실`}</h1>
        <p className="subTitle">
          {`웹캠 또는 스마트폰 카메라 하나로\n언제 어디서나 챌린지를 연습해보세요.`}
        </p>
        <ImageContainer>
          <ServiceImg src={serviceMotionGif1} alt="" />
          <DeviceImg src={deviceLandscape} alt="" />
        </ImageContainer>
      </Section>
      <Section {...SecondSectionScroll}>
        <div>
          <h1 className="sectionTitle">{`이제 멀리서 걸어오실 필요 없어요`}</h1>
          <p className="subTitle">{`춤추다가 걸어와서 버튼 누르고...\n이런 귀찮은 과정은 저희가 해결해드릴게요.`}</p>
        </div>
        <ImageContainer>
          <ServiceImg src={serviceMotionGif2} alt="" />
          <DeviceImg src={deviceLandscape} alt="" />
        </ImageContainer>
        <p className="text">
          {`별다른 기기 부착 없이 카메라로 모션을 인식하여 멀리서도 버튼 조작이 가능해요.`}
        </p>
      </Section>
      <Section {...ThirdSectionScroll}>
        <h1 className="sectionTitle">인기 챌린지를 한눈에</h1>
        <p className="subTitle">지금 유행하는 다양한 댄스 챌린지에 도전해보세요.</p>
        <SeriesSection style={{ background: "#ffe5ec", flexDirection: "column" }}>
          <SectionHeaderContainer>
            <SectionTitle>🔥 요즘 이 챌린지가 가장 인기 있어요</SectionTitle>
            <p>{`숏토리얼에서 최근 가장 인기가 많은 챌린지들을 소개합니다.\n지금 바로 유행에 동참하세요!`}</p>
          </SectionHeaderContainer>
          <SectionConents className="nowrap">
            {popularShortsList?.map((shorts) => (
              <ShortsVideoItem key={shorts.shortsId} shortsInfo={shorts} isSerise />
            ))}
          </SectionConents>
        </SeriesSection>
        <p className="text">당신이 좋아할만한 챌린지도 추천해드릴게요.</p>
        <CTAButtonContainer>
          <BasicButton
            text="지금 확인하러 가기"
            style={CTAButtonStyled}
            onClick={() => navigate("/main")}
          />
        </CTAButtonContainer>
      </Section>
      <Section {...ForthSectionScroll}>
        <h1 className="sectionTitle">촬영에서 업로드까지</h1>
        <p className="subTitle">{`연습한 그 자리에서 바로 촬영하고\nSNS에 공유해보세요.`}</p>
        <ImageContainer>
          <ServiceImg src={serviceMotionGif3} alt="" />
          <DeviceImg src={deviceLandscape} alt="" />
        </ImageContainer>
      </Section>
      <CenterSection {...LastSectionScroll}>
        <h1 className="sectionTitle bg">{`춤 출 준비 되셨나요?`}</h1>
        <p className="subTitle">{`처음이라도 괜찮아요!`}</p>
        <p className="subTitle">{`숏토리얼은\n혼자서, 자신만의 속도로\n춤을 연습할 수 있어요.`}</p>
        <p className="subTitle">{`지금 바로 챌린지에 참여해보세요!`}</p>
        <BasicButton
          text="지금 시작하기"
          style={CTAButtonStyled}
          onClick={() => navigate("/main")}
        />
      </CenterSection>
    </Container>
  );
};

const CTAButtonContainer = styled.div`
  z-index: 10;
`;

const CTAButtonStyled = {
  width: "auto",
  height: "auto",
  marginTop: "20px",
  padding: "11px 16px",
  fontSize: "17px",
  zIndex: "10",
};

const DeviceImg = styled.img`
  width: 100%;
  height: 100%;
`;

const ServiceImg = styled.img`
  position: absolute;
  top: 52%;
  left: 51%;
  width: 87%;
  height: 84%;
  border-radius: 1rem;
  box-shadow: 12px 12px 15px rgba(0, 0, 0, 0.3);
  transform: translate(-50%, -50%);
`;

const ImageContainer = styled.div`
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const Section = styled.section`
  position: relative;
  width: 300px;
  margin: 0 auto;
  padding-top: 100px;
  padding-bottom: 50px;
  box-sizing: border-box;

  @media screen and (min-width: 600px) {
    width: 550px;
    padding-top: 120px;
    padding-bottom: 80px;
  }

  @media screen and (min-width: 1024px) {
    width: 700px;
    padding-top: 150px;
    padding-bottom: 100px;
  }
`;

const CenterSection = styled(Section)`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;

  @media screen and (min-width: 600px) {
    padding-bottom: 120px;
  }

  @media screen and (min-width: 1024px) {
    padding-bottom: 150px;
  }
`;

const HeroTextContainer = styled.div`
  position: relative;
  z-index: 10;

  & > * {
    text-align: center;
    color: #fff;
  }

  h2 {
    font-size: 18px;
  }
`;

const HeroImg = styled.img`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 10;
`;

const HeroContainer = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
`;

const Container = styled.div`
  position: relative;
  width: 100%;
  word-break: keep-all;
  white-space: pre-wrap;
  background-color: #f9f9f9;

  .sectionTitle {
    font-size: 30px;
    font-weight: bold;
    line-height: 1.4;
    margin-bottom: 1rem;

    @media screen and (min-width: 1024px) {
      font-size: 40px;
    }
  }

  .bg {
    color: #fff;
    background-color: #000;
    padding: 6px 8px;
  }

  .subTitle {
    font-size: 20px;
    line-height: 1.4;

    @media screen and (min-width: 1024px) {
      font-size: 24px;
    }
  }

  .text {
    margin: 50px 0 0;
    color: #333d4b;
    font-size: 18px;
    line-height: 1.4;

    @media screen and (min-width: 1024px) {
      font-size: 20px;
    }
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

export default LandingPage;
