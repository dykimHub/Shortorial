import { useEffect, useState } from "react";
import styled, { keyframes } from "styled-components";
import Header from "../components/header/Header";
import { Countings } from "../constants/types";
import { getCounting } from "../apis/shorts";
import { MemberInfo, getInfo } from "../apis/member";
import UploadList from "../components/mypage/UploadList";
import TryList from "../components/mypage/TryList";
import useMypageStore from "../store/useMypageStore";
import message from "../assets/mypage/message.png";

export default function MyPage() {
  const [currentTab, setCurrentTab] = useState(0);
  const { countings } = useMypageStore();
  const [counting, setCounting] = useState<Countings>({
    triedShortsNum: 0,
    recordedShortsNum: 0,
    unRecordedShortsTitle: "",
  });
  const [memberInfo, setMemberInfo] = useState<MemberInfo>();

  const menuArr = [
    { name: "녹화", count: counting?.recordedShortsNum, content: <UploadList /> },
    { name: "참여", count: counting?.triedShortsNum || 0, content: <TryList /> },
  ];

  useEffect(() => {
    async function fetchData() {
      try {
        const [countData, memberData] = await Promise.all([getCounting(), getInfo()]);
        setCounting(countData);
        setMemberInfo(memberData);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    }
    fetchData();
  }, []);

  useEffect(() => {
    if (countings) {
      setCounting(countings);
    }
  }, [countings]);

  return (
    <OutSide>
      <Header />
      <MypageContainer>
        <ProfileContainer>
          <MessageContainer>
            <Message src={message} />
            {memberInfo?.memberNickname}님,{" "}
            {counting.triedShortsNum === 0 && counting.recordedShortsNum === 0 ? (
              <>
                환영합니다. <br />
                숏토리얼이 첫 챌린지를 응원할게요! 🌟 <br />
              </>
            ) : counting.unRecordedShortsTitle ? (
              <>
                {counting.unRecordedShortsTitle} 챌린지를 연습 중이시네요! <br />
                연습한 만큼 촬영해 보시는 건 어떠세요? 🙂
              </>
            ) : (
              <>
                연습한 챌린지를 모두 촬영하셨네요! 🎉 <br />
                다른 챌린지도 둘러보실래요?
              </>
            )}
          </MessageContainer>
        </ProfileContainer>

        <TabMenu>
          {menuArr.map((el, index) => (
            <li
              key={index}
              className={index === currentTab ? "submenu focused" : "submenu"}
              onClick={() => setCurrentTab(index)}
            >
              {el.name}
              <Badge>{el.count}</Badge>
            </li>
          ))}
        </TabMenu>

        <div>{menuArr[currentTab].content}</div>
      </MypageContainer>
    </OutSide>
  );
}

const OutSide = styled.div`
  position: relative;
  padding: 36px;
`;
const MypageContainer = styled.div`
  display: flex;
  flex-direction: column;
  max-width: 1100px;
  margin: 0 auto;
  padding-top: 70px;
`;

const ProfileContainer = styled.div`
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
`;

const TabMenu = styled.div`
  font-weight: bold;
  display: flex;
  flex-direction: row;
  align-items: center;
  list-style: none;
  text-align: center;

  .submenu {
    display: flex;
    justify-content: center;
    height: 3rem;
    align-items: center;
    width: calc(100% / 2);
    padding-bottom: 5px;
    font-size: 15px;
    transition: 0.5s;
    border-bottom: 2px solid #fb2576;
    border: 2px solid #fb2576;
    border-radius: 20px 20px 0px 0px;
    background: white;
  }

  .focused {
    border: 2px solid #fb2576;
    border-radius: 20px 20px 0px 0px;
    background: #fb2576;
    color: white;
  }
`;

const slideUp = keyframes`
  0% {
    transform: translateY(20px);
    opacity: 0;
  }
  50% {
    transform: translateY(-10px);
    opacity: 1;
  }
  100% {
    transform: translateY(0);
    opacity: 1;
  }
`;

const MessageContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 20px;
  margin: 10px 0;
  border-radius: 16px;
  background-color: #fff5f9;
  box-sizing: border-box;
  max-width: 600px;
  min-width: 450px;
  margin: 0 auto;
  font-size: 18px;
  font-weight: 500;
  text-align: center;
  color: #444;
  line-height: 1.5;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  animation: ${slideUp} 0.8s ease;
`;

const Message = styled.img`
  width: 50px;
  height: 50px;
  margin: 10px;
`;

const Badge = styled.span`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 6px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: #fb2576;
  color: white;
  font-size: 12px;
  font-weight: bold;
  line-height: 1;
  box-shadow: 0 0 0 1px white;
`;
