import { useEffect, useState } from "react";
import styled, { keyframes } from "styled-components";
import { Countings } from "../../constants/types";
import { getCounting } from "../../apis/shorts";
import { MemberInfo, getInfo } from "../../apis/member";
import useMypageStore from "../../store/useMypageStore";
import message from "../../assets/mypage/message.png";
import { MusicNote } from "@mui/icons-material";

export default function Profile() {
  const { countings } = useMypageStore();
  const [counting, setCounting] = useState<Countings>();
  const [memberInfo, setMemberInfo] = useState<MemberInfo>();

  const getCounts = async () => {
    try {
      const data = await getCounting();
      setCounting(data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const getMemberInfo = async () => {
    try {
      const data = await getInfo();
      setMemberInfo(data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  useEffect(() => {
    getCounts();
    getMemberInfo();
  }, []);

  useEffect(() => {
    if (countings) {
      setCounting(countings);
    }
  }, [countings]);

  return (
    <ProfileContainer>
      <Message src={message} />
      <div>
        {memberInfo?.memberNickname}님,{" "}
        {counting?.unRecordedShortsTitle ? (
          <>
            <MusicNote />
            {counting.unRecordedShortsTitle} 챌린지를 연습 중이시네요! <br />
            연습한 만큼 촬영해 보시는 건 어떠세요? 🙂
          </>
        ) : (
          <>
            연습한 챌린지를 모두 촬영하셨네요! 🎉 <br />
            다른 챌린지도 둘러보실래요?
          </>
        )}
      </div>
      {/* <ProfileNumContainer key="memberNickname" className="left">
        {memberInfo?.memberNickname}
      </ProfileNumContainer>
      <ProfilRightContainer>
        <ProfileNumContainer key="tryShorts">
          <div className="number">{counting?.triedShortsNum}</div>
          <div>연습중</div>
        </ProfileNumContainer>
        <ProfileNumContainer key="uploadShorts">
          <div className="number">{counting?.recordedShortsNum}</div>
          <div>완료</div>
        </ProfileNumContainer>
        <ProfileNumContainer key="youtubeUrl">
          <div className="number">{counting?.unRecordedShortsTitle}</div>
          <div>게시</div>
        </ProfileNumContainer>
      </ProfilRightContainer> */}
    </ProfileContainer>
  );
}

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

const ProfileContainer = styled.div`
  align-items: center;
  justify-content: center;
  padding: 20px 20px;
  margin: 10px 0;
  // border: 2px solid #fb2576;
  border-radius: 16px;
  background-color: #fff5f9;
  box-sizing: border-box;
  max-width: 600px;
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

// const ProfilRightContainer = styled.div`
//   display: flex;
// `;
// const ProfileNumContainer = styled.div`
//   display: flex;
//   flex-direction: column;
//   justify-content: center;
//   align-items: center;

//   margin-right: 10px;
//   margin-left: 10px;

//   .number {
//     font-size: 25px;
//   }

//   &.left {
//     font-size: 30px;
//   }
// `;
