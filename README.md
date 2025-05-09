# :dancers: Shortorial(Shorts + Tutorial)을 소개합니다. :tada:

<div align="center">
<img src="assets/1_logo.png" width="" height="100"></img>

숏토리얼은 댄스 챌린지를 쉽게 연습하고, 촬영하는 기능을 제공하는 **댄스 챌린지 어시스턴트 서비스** 입니다.

</div>

# :white_check_mark: 목적

춤을 처음 배우는 사용자도 쉽게 따라할 수 있도록,  
**구간 반복**과 **속도 조절** 기능을 제공하여 집중적으로 연습할 수 있도록 지원합니다.

반복적인 편집 작업을 줄이고 싶은 사용자를 위해,  
녹화가 끝나면 자동으로 **원본 영상의 음악을 합성**해 편리하게 영상을 완성할 수 있도록 합니다.

멀리 거치된 기기를 오가며 조작하기 번거로운 사용자를 위해,  
챌린지 연습과 녹화 과정에서 **모션 인식**으로 버튼을 누를 수 있도록 구현했습니다.

# :date: 기간

_SSAFY 10기 자율 프로젝트_  
**2024.04.08 - 2024.05.20 (6주)**

_1차 리팩토링_  
**2024.06.05 - 2024.07.24**

_2차 리팩토링_  
**2025.03.03 - 2025.04.22**

# :computer: **Team. 둠칫둠칫**

| 팀원         | 역할   | 담당                                                                                                                                                           |
| ------------ | ------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 우지민(팀장) | BE     | 숏폼 영상 조회 및 마이페이지 콘텐츠 API 구현                                                                                                                   |
| 조민준       | INFRA  | AWS EC2 환경 기반 자동 배포 파이프라인 구축                                                                                                                    |
| 전성수       | BE     | JWT 기반 회원 인증 및 인가 API 구현                                                                                                                            |
| 김다윤       | FE, BE | 챌린지 녹화 및 오디오 기능, S3 연동 API 구현<br> 1차 리팩토링: DTO Projection 기반 API 성능 개선<br> 2차 리팩토링: AWS ECS 환경 기반 자동 배포 파이프라인 구축 |
| 이현정       | AI, FE | 모션 기반 제스처 감지 및 UI 제어 기능 구현                                                                                                                     |
| 임지은       | FE     | 챌린지 구간 연습 기능, 랜딩/메인 페이지 구현                                                                                                                   |

# :art: 서비스 아키텍처 & 기술 스택

<img src="assets/18_리팩토링_서비스아키텍처.png" />

<table>
  <tr>
    <th>Backend</th>
    <th>Frontend</th>
    <th>Database</th>
  </tr>
  <tr>
    <td>
      <img src="https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white">
      <img src="https://img.shields.io/badge/Spring%20Data%20JPA-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white"> <br>
      <img src="https://img.shields.io/badge/Spring%20Security-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white"> 
      <img src="https://img.shields.io/badge/JWT-000000.svg?style=for-the-badge&logo=jsonwebtokens&logoColor=white"> <br>
      <img src=https://img.shields.io/badge/QueryDSL-007ACC.svg?style=for-the-badge&logo=&logoColor=white>
      <img src="https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white">
    </td>
    <td>
      <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=black">
      <img src="https://img.shields.io/badge/MediaPipe-00BFA5?style=for-the-badge&logo=mediapipe&logoColor=white"> <br>
      <img src="https://img.shields.io/badge/Typescript-3178C6?style=for-the-badge&logo=Typescript&logoColor=white"> 
      <img src="https://img.shields.io/badge/Zustand-%235B2C6F.svg?style=for-the-badge&logo=React&logoColor=white"> <br>
      <img src="https://img.shields.io/badge/styled--components-DB7093.svg?style=for-the-badge&logo=styled-components&logoColor=white"> <br>
      <img src="https://img.shields.io/badge/MediaRecorder-FF5722?style=for-the-badge&logo=html5&logoColor=white">
      <img src="https://img.shields.io/badge/Canvas%20API-2E8B57?style=for-the-badge&logo=html5&logoColor=white">
    </td>
    <td>
      <img src="https://img.shields.io/badge/MySQL-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white">
      <img src="https://img.shields.io/badge/Redis-DC382D.svg?style=for-the-badge&logo=redis&logoColor=white">
    </td>
  </tr>
</table>
<table>
  <tr>
    <th>Infra</th>
    <th>DevTool</th>
    <th>Others</th>
  </tr>
  <tr>
    <td>
      <img src="https://img.shields.io/badge/Docker-2496ED.svg?style=for-the-badge&logo=docker&logoColor=white">
      <img src="https://img.shields.io/badge/Nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white">
      <img src="https://img.shields.io/badge/FFmpeg-007808.svg?style=for-the-badge&logo=ffmpeg&logoColor=white"> <br>
      <img src="https://img.shields.io/badge/Amazon%20ECS-FF9900.svg?style=for-the-badge&logo=amazonecs&logoColor=white">
      <img src="https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"> <br>
      <img src="https://img.shields.io/badge/Amazon%20S3-569A31.svg?style=for-the-badge&logo=amazons3&logoColor=white">
      <img src="https://img.shields.io/badge/AWS%20Lambda-F58536.svg?style=for-the-badge&logo=awslambda&logoColor=white"> <br>
      <img src="https://img.shields.io/badge/Route%2053-6A34D1.svg?style=for-the-badge&logo=amazonroute53&logoColor=white">
    </td>
    <td>
      <img src="https://img.shields.io/badge/GitHub-181717.svg?style=for-the-badge&logo=github&logoColor=white">
      <img src="https://img.shields.io/badge/GitHub%20Actions-2088FF.svg?style=for-the-badge&logo=githubactions&logoColor=white"> <br>
      <img src="https://img.shields.io/badge/OpenAPI-85EA2D.svg?style=for-the-badge&logo=swagger&logoColor=black">
      <img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white"> <br>
      <img src="https://img.shields.io/badge/Visual%20Studio%20Code-007ACC?style=for-the-badge&logo=&logoColor=white">
    </td>
    <td>
      <img src="https://img.shields.io/badge/Figma-F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white">
      <img src="https://img.shields.io/badge/Draw.io-F08705.svg?style=for-the-badge&logo=diagramsdotnet&logoColor=white">
      <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white"> <br>
      <img src="https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white">
      <img src="https://img.shields.io/badge/-Mattermost-blue?style=for-the-badge&logo=mattermost&logoColor=white">
    </td>

  </tr>
</table>

# 💖 서비스 화면 및 기능 상세

<table>
  <tr>
    <th>랜딩 페이지</th>
    <th>메인 페이지</th>
  </tr>
  <tr>
    <td><img src="assets/3_랜딩페이지.gif"></td>
    <td><img src="assets/4_메인페이지.gif"></td>
  </tr>
  <tr>
    <td>
      - 랜딩 페이지에서 숏토리얼 서비스를 한눈에 파악할 수 있습니다. <br>
      - 회원가입 및 로그인 후 숏토리얼이 제공하는 숏폼을 연습하거나 촬영할 수 있습니다.
    </td>
    <td>
      - 메인 페이지에서 전체 숏폼과 인기 숏폼 목록을 제공합니다. <br>
      - 원하는 숏폼을 클릭하면 모달 창에서 숏폼 정보와 챌린저 수를 확인할 수 있습니다. <br>
    </td>
  </tr>
</table>
<table>
  <tr>
    <th>연습 모드 - 1</th>
    <th>연습 모드 - 2</th>
  </tr>
  <tr>
    <td><img src="assets/5_연습페이지.png"></td>
    <td><img src="assets/7_연습모드_제스처.gif"></td>
  </tr>
  <tr>
    <td>
      - 연습 모드를 클릭하면 숏폼이 3초 단위로 분할되도록 구성했으며, 좌측에 구간 목록이 표시됩니다. <br> 
      - 우측에는 숏폼 재생, 구간 반복(해제), 거울 모드(해제), 배속 변경(1/0.75/0.5), 챌린지 모드 이동 버튼 세트가 있습니다. <br>
      - 숏폼이 시작되면 숏폼 일시정지 버튼으로 교체됩니다.
    </td>
    <td> 
      - 손 좌우 스와이프 제스처를 인식하여 원하는 구간으로 간편하게 이동할 수 있습니다. <br> 
      - 손바닥이 버튼 영역 내에서 약 3초간 감지되면 기능을 실행합니다. <br>
      - 구간 반복이 활성화되어 있으면 선택한 구간만 반복하며, 비활성화하면 모든 구간을 순차적으로 재생합니다.
    </td>
  </tr>
</table>
<table>
  <tr>
    <th>챌린지 모드 - 1</th>
    <th>챌린지 모드 - 2</th>
  </tr>
  <tr>
    <td><img src="assets/10_챌린지모드_버튼.png"></td>
    <td><img src="assets/11_챌린지모드_녹화.gif"></td>
  </tr>
  <tr>
    <td>
      - 챌린지 모드를 클릭하면 웹캠 녹화를 위해 카메라 권한을 요청합니다.<br> 
      - 우측에 녹화 시작, 녹화 카운트다운 변경(3/5/10초), 거울 모드(해제), 마이 페이지 이동, 연습 모드 이동 버튼 세트가 있습니다. <br> 
      - 녹화가 시작되면 녹화 중지, 저장 버튼 세트로 교체됩니다. 
    <td>  
      - 숏폼이 종료되면 녹화된 영상이 자동으로 S3에 업로드됩니다.<br> 
      - 그 전에 저장 버튼을 실행하면 해당 시점까지 녹화된 영상이 업로드 됩니다. <br>
      - Lambda는 S3 업로드 이벤트를 감지해 숏폼 오디오(노래)를 사용자 영상에 합성하고, 최종 영상을 S3에 업로드합니다.
    </td>
  </tr>
</table>
<table>
  <tr>
    <th>마이 페이지 - 1</th>
    <th>마이 페이지 - 2</th>
  </tr>
  <tr>
    <td><img src="assets/12_마이페이지_녹화.png"></td>
    <td><img src="assets/13_마이페이지_참여.png"></td>
  </tr>
  <tr>
    <td>
      - 녹화 탭에서는 사용자가 녹화한 영상 목록과 개수를 확인할 수 있습니다. <br>
      - 영상 재생, 다운로드, 제목 수정, 삭제 기능을 제공합니다.<br>     
    </td>
    <td>
      - 참여 탭에서는 사용자가 연습한 숏폼 목록과 개수를 확인할 수 있습니다. <br> 
      - 상단에서 연습과 녹화 현황을 비교해 챌린지 참여를 응원하는 메시지를 표시합니다. <br> 
    </td>
  </tr>
  
</table>
