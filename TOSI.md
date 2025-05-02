# 👪 Tosi(The only story in the world) :books:

<img src="./assets/tosi.png">

## :loud_sound: 주제

- TTS (텍스트 음성 변환)와 AI를 활용한 동화 낭독 서비스

## :sparkling_heart: 소개

- 등장인물을 어린이 이름으로 바꾸어 TTS로 책을 낭독하고, AI로 커스텀 동화책을 생성하거나 동화 속 인물과 소통합니다.

## :date: 기간

- 2024.01.03 ~ 2024.02.16

## :keyboard: 참여 인원 및 역할

| 이름   | 역할                                | 기능                                                                        |
| ------ | ----------------------------------- | --------------------------------------------------------------------------- |
| 천우진 | FullStack, Team Leader, Git Manager | 메인 페이지, 동화 목록, 동화 검색                                           |
| 우지민 | FullStack, FrontEnd Leader          | 모든 동화에 TTS 자동 재생 / 정지 / 배속 / 볼륨 적용                         |
| 김다윤 | FullStack                           | AWS S3, QueryDSL, 등장인물 이름 / 조사 변경, 모든 동화 페이지 구조          |
| 양성주 | FullStack, UI Manager               | AI를 활용한 커스텀 동화 및 썸네일 생성 / 저장, 다른 회원과 커스텀 동화 공유 |
| 이아진 | FullStack, BackEnd Leader, Infra    | AI를 활용한 등장인물과의 채팅, 도커라이징                                   |
| 김소연 | FullStack                           | 회원가입, 자동 로그인(JWT), 마이페이지, 나의 책장                           |

## :deciduous_tree: Stacks

**Environment**  
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-007ACC?style=for-the-badge&logo=Visual%20Studio%20Code&logoColor=white)  
**BackEnd**  
![Spring Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)  
**FrontEnd**  
![Vue.js](https://img.shields.io/badge/vue.js-%2335495e.svg?style=for-the-badge&logo=vuedotjs&logoColor=%234FC08D)
<img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white)  
**DataBase**  
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS%20S3-%23FF9900.svg?style=for-the-badge&logo=amazons3&logoColor=white)  
**Version Control**  
![GitLab](https://img.shields.io/badge/gitlab-%23181717.svg?style=for-the-badge&logo=gitlab&logoColor=white)
![Gerrit](https://img.shields.io/badge/-Gerrit-lightgreen?style=for-the-badge&logo=gerrit&logoColor=white)  
**Deployment**  
![Docker](https://img.shields.io/badge/Docker-%232496ED.svg?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)
![EC2](https://img.shields.io/badge/EC2-%23FF9900.svg?style=for-the-badge&logo=amazonec2&logoColor=white)  
**Communication**  
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)
![Jira](https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white)
![Mattermost](https://img.shields.io/badge/-Mattermost-blue?style=for-the-badge&logo=mattermost&logoColor=white)

## :art: Swagger

<img src="./assets/swagger.PNG" width="400" />
<img src="./assets/swagger2.PNG" width="400" />

## :book: 토씨의 기능

<table>
  <tr>
    <th style="text-align: center;">메인페이지</th>
    <th style="text-align: center;">동화 목록</th>
  </tr>
  <tr>
    <td><img src="./assets/main.gif"></td>
    <td><img src="./assets/booklist.gif"></td>
  </tr>
  <tr>
    <td>- 비회원용 메인페이지에서는 토씨의 기능에 대해서 음성으로 설명합니다. <br> - 로그인에 성공하면 토씨의 기능을 사용할 수 있습니다.</td>
    <td>- 동화를 인기순, 이름순, 랜덤으로 정렬합니다. <br> - 동화를 제목의 일부로 검색할 수 있습니다. <br> - 동화 수에 따라 페이지네이션이 가능합니다.</td>
  </tr>
</table>
<table>
  <tr>
    <th style="text-align: center;">동화 상세</th>
    <th style="text-align: center;">동화 구연</th>
  </tr>
  <tr>
    <td><img src="./assets/bookdetail.gif"></td>
    <td><img src="./assets/bookread.gif"></td>
  </tr>
  <tr>
    <td>- 동화 찜 버튼을 누르면 나의 책장에 추가 됩니다. <br> - 이름을 바꾸길 원하는 등장인물과 어린이의 이름을 선택할 수 있습니다. 이미 선택된 등장인물은 클릭할 수 없습니다. <br> - 동화를 읽어주길 원하는 목소리를 선택할 수 있습니다. <br> - 스피커를 누르면 목소리를 들어볼 수 있습니다.</td>
    <td>- 등장인물의 이름이 어린이의 이름으로 바뀌고 알맞은 조사가 붙어서 출력됩니다. <br> - TTS로 글을 다 읽히면 자동으로 다음 페이지로 넘어갑니다. 지정한 페이지에서 삽화가 바뀝니다.  <br> - 화살표를 누르면 이전 / 다음 페이지로 넘어갈 수 있습니다. <br> - 하단에서 재생 제어, 볼륨 조절, 속도 조절이 가능합니다. </td>
  </tr>
  
</table>
<table>
  <tr>
    <th style="text-align: center;">등장인물과의 채팅 준비</th>
    <th style="text-align: center;">등장인물과의 채팅</th>
  </tr>
  <tr>
    <td><img src="./assets/bookend.gif"></td>
    <td><img src="./assets/chat.gif"></td>
  </tr>
  <tr>
    <td>- 동화 구연이 끝날 때마다 등장인물 중 한 명이 랜덤 메세지를 보냅니다. <br> - 원하는 등장인물에게 채팅을 걸 수 있습니다. 선택한 어린이의 이름으로 채팅방이 형성됩니다. <br> - 선택한 등장인물이 입장했다는 메세지가 뜹니다. 해당 등장인물에게 5번의 대답을 들을 수 있습니다. </td>
    <td>  - 메세지가 추가될 때마다 스크롤을 최하단으로 내려 최신 메세지를 바로 볼 수 있도록 합니다. <br> - 5번의 대답이 끝나면 등장인물이 작별 인사를 보내고 대화를 종료합니다. 더 이상 채팅을 입력할 수 없고 나가기를 눌러 동화 목록 페이지로 갈 수 있습니다. </td>
  </tr>
  
</table>
<table>
  <tr>
    <th style="text-align: center;">커스텀 동화 생성 및 저장</th>
    <th style="text-align: center;">다른 회원의 커스텀 동화 구연</th>
  </tr>
  <tr>
    <td><img src="./assets/custom.gif"></td>
    <td><img src="./assets/othercustom.gif"></td>
  </tr>
  <tr>
    <td>- 원하는 배경과 키워드를 입력하면 어린이의 성별까지 반영한 썸네일과 동화가 생성됩니다. <br> - 커스텀 동화 구연을 마치면 원하는 제목을 짓고 공개 여부를 선택하여 저장할 수 있습니다. <br> - 내가 만든 동화 목록에서 저장된 동화를 확인할 수 있고 공개 여부를 수정하거나 삭제할 수 있습니다.   </td>
    <td>- 다른 회원이 공개한 커스텀 동화 목록을 볼 수 있습니다. <br> - 선택한 커스텀 동화의 구연이 끝나면 랜덤으로 선택된 다른 회원의 동화를 감상할 수 있습니다. <br> - 다시 보거나 나가기를 눌러 목록으로 돌아갈 수 있습니다. </td>
  </tr>
  
</table>

# ⚙︎ 프로젝트 설명

## ⭐️ 기획 배경

##### 어린이 문해력 저하

코로나19로 인해 영상 매체 시청시간이 늘어나면서, 어린이들의 문해력 저하가 사회 문제로 대두되었습니다. 접근성 높은 문학 콘텐츠를 제공함으로써 문해력 향상을 도모하고 독서 습관이 감소하는 추세에 대응하고자 합니다.

##### 돌봄 인력 부족

핵가족화가 진행되면서 지역사회와의 연결이 약화되었습니다. 아이들과 함께 보낼 수 있는 시간이 상대적으로 적은 양육자를 보조하여 동화를 읽어주는 역할을 합니다.

## ⭐️ 기획 목적

##### 문해력 개발 촉진

자신의 이름이 들어간 동화로 내용에 더 집중할 수 있도록 합니다. 재미있게 많은 문학을 접하게 함으로써 건강한 독서 습관을 형성할 수 있습니다.

##### 양육자 고충 해소

자녀와 보내는 질적인 시간이 부족한 맞벌이 가정에게 도움을 줄 수 있습니다. 양육자가 바빠도 아이들이 동화를 듣고 즐길 수 있습니다.

## ⭐️ 기대 효과

##### 언어 습득과 사회성 강화

자신의 이름에 반응하면서 언어 습득에 긍정적 영향을 미칠 수 있습니다. 동화 속 캐릭터들과 상호작용하며 어휘력을 향상시키고 사회적으로 행동하는 법을 배웁니다.

##### 창의력의 증진

어린이들이 스토리텔링에 더욱 몰입하여 다양한 상황과 캐릭터를 상상하며 창의적인 사고를 발달시킬 수 있습니다.

##### 학습 동기 부여

어린이들이 더 적극적으로 교육 콘텐츠에 참여하고, 독서에 대한 동기를 높일 수 있는 방향을 제시합니다.

## ⭐️ 타깃

- 육아를 보조해 줄 질적인 도구가 필요한 양육자
- 이야기 듣기를 좋아하는 어린이
- 독서를 지루해하는 어린이

## ⭐️ 결과 화면

##### 전체 버전

<img src="./assets/Tosi_UCC.mp4">

##### Short 버전

<img src="./assets/Tosi_UCC_Short.mp4">
