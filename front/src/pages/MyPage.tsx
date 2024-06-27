import { useState } from "react";
import Profile from "../components/mypage/Profile";
import UploadList from "../components/mypage/UploadList";
import styled from "styled-components";
import TryList from "../components/mypage/TryList";
import Header from "../components/header/Header";

export default function MyPage() {
  const [currentTab, clickTab] = useState(0);

  const menuArr = [
    //{ name: "업로드", content: <UploadList /> },
    { name: "참여", content: <TryList /> },
  ];

  const selectMenuHandler = (index: number) => {
    clickTab(index);
  };
  return (
    <OutSide>
      <Header />
      <MypageContainer>
        {/* <ProfileContainer>
          <Profile />
        </ProfileContainer> */}
        <div>
          <TabMenu>
            {menuArr.map((el, index) => (
              <li
                key={index}
                className={index === currentTab ? "submenu focused" : "submenu"}
                onClick={() => selectMenuHandler(index)}
              >
                {el.name}
              </li>
            ))}
          </TabMenu>
          <div>{menuArr[currentTab].content}</div>
        </div>
      </MypageContainer>
    </OutSide>
  );
}
const OutSide = styled.div`
  position: relative;
  /* height: 100%; */
  // background: linear-gradient(
  //   180deg,
  //   rgba(0, 0, 0, 1) 0%,
  //   rgba(48, 13, 45, 1) 80%,
  //   rgba(112, 0, 102, 1) 100%
  // );
`;
const MypageContainer = styled.div`
  display: flex;
  margin-right: 20px; /* 오른쪽 마진을 20px로 설정 */
  margin-left: 20px; /* 왼쪽 마진을 20px로 설정 */
  padding-top: 80px;
  height: 100%;
  flex-direction: column; /* 세로로 나열 */

  @media screen and (orientation: landscape) {
    margin-right: 15%; /* 오른쪽 마진을 20px로 설정 */
    margin-left: 15%; /* 왼쪽 마진을 20px로 설정 */
  }
`;

const ProfileContainer = styled.div`
  height: 96px;
  margin-top: 1rem;
  margin-bottom: 2rem;

  @media screen and (orientation: landscape) {
    padding-right: 15%; /* 오른쪽 마진을 20px로 설정 */
    padding-left: 15%; /* 왼쪽 마진을 20px로 설정 */
  }
`;

const TabMenu = styled.div`
  font-weight: bold;
  display: flex;
  flex-direction: row;
  align-items: center;
  list-style: none;
  text-align: center;

  .submenu {
    // 기본 Tabmenu 에 대한 CSS를 구현

    display: flex;
    justify-content: center;
    height: 3rem;
    align-items: center; /* 수직 가운데 정렬 */
    width: calc(100% / 2);
    padding-bottom: 5px;
    font-size: 15px;
    transition: 0.5s;
    border-bottom: 2px solid #fb2576;
    border: 2px solid #fb2576;
    border-radius: 5px 5px 0px 0px;
    background: white;
  }

  .focused {
    //선택된 Tabmenu 에만 적용되는 CSS를 구현

    border: 2px solid #fb2576;
    border-radius: 20px 20px 0px 0px;
    background: #fb2576;
    color: white;
  }
`;
