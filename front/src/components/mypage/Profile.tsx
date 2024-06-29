import { useEffect, useState } from "react";
import styled from "styled-components";
import { Countings, getCounting } from "../../apis/mypage";
import { MemberInfo, getInfo } from "../../apis/member";

export default function Profile() {
  const [counting, setCounting] = useState<Countings>();
  const [memberInfo, setMemberInfo] = useState<MemberInfo>();
  let imageUrl = "/src/assets/profiles/profile";
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
    if (memberInfo) {
      imageUrl = `${imageUrl}${memberInfo.memberProfile}.jpeg`;
    }
  }, []);

  return (
    <ProfileContainer>
      <ProfileNumContainer key="memberNickname" className="left">
        {memberInfo?.memberNickname}
      </ProfileNumContainer>

      <ProfilRightContainer>
        <ProfileNumContainer key="tryShorts">
          <div className="number">{counting?.triedShortsNum}</div>
          <div>참여</div>
        </ProfileNumContainer>
        <ProfileNumContainer key="uploadShorts">
          <div className="number">{counting?.recordedShortsNum}</div>
          <div>완료</div>
        </ProfileNumContainer>
        <ProfileNumContainer key="youtubeUrl">
          <div className="number">{counting?.uploadedShortsNum}</div>
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
  height: 100%;
  width: 100%;
  border: 2px solid #fb2576;
  border-radius: 20px;
  //box-shadow: 2px 2px #fb2576;
  background: white;
`;
const ProfilRightContainer = styled.div`
  display: flex;
`;
const ProfileNumContainer = styled.div`
  display: flex;
  flex-direction: column; /* 세로로 나열 */
  justify-content: center; /* 수평 가운데 정렬 */
  align-items: center; /* 수직 가운데 정렬 */

  margin-right: 10px;
  margin-left: 10px;

  .number {
    font-size: 25px;
  }

  &.left {
    font-size: 30px;
  }
`;
