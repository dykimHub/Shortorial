import { useState } from "react";
import Profile from "../components/mypage/Profile";
import UploadList from "../components/mypage/UploadList";
import styled from "styled-components";
import TryList from "../components/mypage/TryList";
import Header from "../components/header/Header";

export default function MyPage() {
  const [currentTab, clickTab] = useState(0);

  const menuArr = [
    { name: "녹화", content: <UploadList /> },
    { name: "참여", content: <TryList /> },
  ];

  const selectMenuHandler = (index: number) => {
    clickTab(index);
  };
  return (
    <OutSide>
      <Header />
      <MypageContainer>
        <ProfileContainer>
          <Profile />
        </ProfileContainer>
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
