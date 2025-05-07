import { useEffect, useState } from "react";
import styled from "styled-components";
import { Countings } from "../../constants/types";
import { getCounting } from "../../apis/shorts";
import { MemberInfo, getInfo } from "../../apis/member";
import useMypageStore from "../../store/useMypageStore";

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
      <ProfileNumContainer key="memberNickname" className="left">
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
          <div className="number">{counting?.uploadedShorts}</div>
          <div>게시</div>
        </ProfileNumContainer>
      </ProfilRightContainer>
    </ProfileContainer>
  );
}
const ProfileContainer = styled.div`
  display: flex;
  justify-content: space-around;
  padding: 10px;
  border: 2px solid #fb2576;
  border-radius: 20px;
  box-sizing: border-box;
  width: 70%;
  min-width: 400px;
`;
const ProfilRightContainer = styled.div`
  display: flex;
`;
const ProfileNumContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  margin-right: 10px;
  margin-left: 10px;

  .number {
    font-size: 25px;
  }

  &.left {
    font-size: 30px;
  }
`;
