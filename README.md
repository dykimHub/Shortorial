# :dancers: Shortorial(Shorts + Tutorial)을 소개합니다. :tada:

<div align="center">
<img src="README_assets/1_logo.png" width="" height="100"></img>

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
**2025.03.03 - 2024.04.22**

# :computer: **Team. 둠칫둠칫**

| 팀원         | 역할   | 담당                                                                                                                                                           |
| ------------ | ------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 우지민(팀장) | BE     | 숏폼 영상 조회 및 마이페이지 콘텐츠 API 구현                                                                                                                   |
| 조민준       | INFRA  | AWS EC2 환경 기반 자동 배포 파이프라인 구축                                                                                                                    |
| 전성수       | BE     | JWT 기반 회원 인증 및 인가 API 구현                                                                                                                            |
| 김다윤       | FE, BE | 챌린지 녹화 및 오디오 기능, S3 연동 API 구현<br> 1차 리팩토링: DTO Projection 기반 API 성능 개선<br> 2차 리팩토링: AWS ECS 환경 기반 자동 배포 파이프라인 구축 |
| 이현정       | AI, FE | 모션 기반 제스처 감지 및 UI 제어 기능 구현                                                                                                                     |
| 임지은       | FE     | 챌린지 구간 연습 기능, 랜딩/메인 페이지 구현                                                                                                                   |

# :zap: 서비스 아키텍처 & 기술 스택

<img src="README_assets/18_리팩토링_서비스아키텍처.png" />

<table>
  <tr>
    <th style="border-right:1px solid gray; padding-right:10px; text-align:center;">Backend</th>
    <th style="border-right:1px solid gray; padding-right:10px; text-align:center;">Frontend</th>
    <th style="border-right:1px solid gray; padding-right:10px; text-align:center;">Database</th>
    <th style="border-right:1px solid gray; padding-right:10px; text-align:center;">Infra</th>
    <th style="border-right:1px solid gray; padding-right:10px; text-align:center;">DevTool</th>
    <th style="border-right:1px solid gray; padding-right:10px; text-align:center;">Others</th>
  </tr>
  <tr>
    <td style="border-right:1px solid gray; text-align:center;">
      <img src="https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white">
      <img src="https://img.shields.io/badge/Spring%20Data%20JPA-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white">
      <img src="https://img.shields.io/badge/Spring%20Security-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white">
      <img src="https://img.shields.io/badge/JWT-000000.svg?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
      <img src=https://img.shields.io/badge/QueryDSL-007ACC.svg?style=for-the-badge&logo=&logoColor=white>
      <img src="https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white">
    </td>
    <td style="border-right:1px solid gray; text-align:center;">
      <img src="https://img.shields.io/badge/MediaPipe-00BFA5?style=for-the-badge&logo=mediapipe&logoColor=white">
      <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=black">
      <img src="https://img.shields.io/badge/Typescript-3178C6?style=for-the-badge&logo=Typescript&logoColor=white">
      <img src="https://img.shields.io/badge/Zustand-%235B2C6F.svg?style=for-the-badge&logo=React&logoColor=white">
      <img src="https://img.shields.io/badge/styled--components-DB7093.svg?style=for-the-badge&logo=styled-components&logoColor=white">
      <img src="https://img.shields.io/badge/MediaRecorder-FF5722?style=for-the-badge&logo=html5&logoColor=white">
      <img src="https://img.shields.io/badge/Canvas%20API-2E8B57?style=for-the-badge&logo=html5&logoColor=white">
    </td>
    <td style="border-right:1px solid gray; text-align:center;">
      <img src="https://img.shields.io/badge/MySQL-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white">
      <img src="https://img.shields.io/badge/Redis-DC382D.svg?style=for-the-badge&logo=redis&logoColor=white">
    </td>
    <td style="border-right:1px solid gray; text-align:center;">
      <img src="https://img.shields.io/badge/Docker-2496ED.svg?style=for-the-badge&logo=docker&logoColor=white">
      <img src="https://img.shields.io/badge/Nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white">
      <img src="https://img.shields.io/badge/FFmpeg-007808.svg?style=for-the-badge&logo=ffmpeg&logoColor=white">
      <img src="https://img.shields.io/badge/Amazon%20ECS-FF9900.svg?style=for-the-badge&logo=amazonecs&logoColor=white">
      <img src="https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
      <img src="https://img.shields.io/badge/Amazon%20S3-569A31.svg?style=for-the-badge&logo=amazons3&logoColor=white">
      <img src="https://img.shields.io/badge/AWS%20Lambda-F58536.svg?style=for-the-badge&logo=awslambda&logoColor=white">
      <img src="https://img.shields.io/badge/Route%2053-6A34D1.svg?style=for-the-badge&logo=amazonroute53&logoColor=white">
    </td>
    <td style="border-right:1px solid gray; text-align:center;">
      <img src="https://img.shields.io/badge/GitHub-181717.svg?style=for-the-badge&logo=github&logoColor=white">
      <img src="https://img.shields.io/badge/GitHub%20Actions-2088FF.svg?style=for-the-badge&logo=githubactions&logoColor=white">
      <img src="https://img.shields.io/badge/OpenAPI-85EA2D.svg?style=for-the-badge&logo=swagger&logoColor=black">
      <img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
      <img src="https://img.shields.io/badge/Visual%20Studio%20Code-007ACC?style=for-the-badge&logo=&logoColor=white">
    </td>
    <td style="border-right:1px solid gray; text-align:center;">
      <img src="https://img.shields.io/badge/Figma-F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white">
      <img src="https://img.shields.io/badge/Draw.io-F08705.svg?style=for-the-badge&logo=diagramsdotnet&logoColor=white">
      <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white">
      <img src="https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white">
      <img src="https://img.shields.io/badge/-Mattermost-blue?style=for-the-badge&logo=mattermost&logoColor=white">
    </td>

  </tr>
</table>

# <span style="color:#FF1493">❤</span> 주요 기능

#### 1. 연습 모드

- 웹캠 또는 핸드폰 카메라를 전신 거울 삼아 언제 어디서든 영상을 보며 춤을 따라 출 수 있습니다.
- 영상의 구간 반복, 배속 변경, 좌우 반전의 기능으로 편리한 연습 환경을 제공합니다.
- 모션 인식을 이용한 유사도 측정으로 점수를 표시해줍니다.
- 모션 인식을 통해 오른손의 위치를 감지하고, 오른손이 카메라상의 버튼 위치에 닿으면 버튼이 활성화되어 기능이 작동합니다.

#### 2. 챌린지 모드

- 챌린지를 촬영하고 저장합니다.
- 촬영된 영상은 마이페이지에서 확인하실 수 있습니다.
- 모션 인식을 통해 오른손의 위치를 감지하고, 오른손이 카메라상의 버튼 위치에 닿으면 버튼이 활성화되어 기능이 작동합니다.

# 주요 화면

#### 랜딩 페이지

<img src="README_assets/3_랜딩페이지.gif" width="688"/>

#### 메인 페이지

- 추천 쇼츠 챌린지, 인기 쇼츠 챌린지, 전체 쇼츠 챌린지
- 쇼츠 상세 정보  
  <img src="README_assets/4_메인페이지.gif" width="688"/>

#### 연습 모드

- 연습 모드 화면 구성
- 영상을 일정 시간마다 나누어 여러 구간으로 표시
- 구간 반복, 거울 모드(좌우 반전), 배속 변경 기능 사용 가능  
  <img src="README_assets/5_연습페이지.png" width="688"/>

- 모션 인식을 활용한 버튼 클릭  
  <img src="README_assets/6_연습모드_버튼.gif" width="688"/>

- 손 제스처로 구간 이동  
  <img src="README_assets/7_연습모드_제스처.gif" width="688"/>

#### 챌린지 모드

- 챌린지 모드 화면 구성
- 영상 녹화 가능
- 타이머, 거울 모드(좌우 반전) 기능 사용 가능  
  <img src="README_assets/9_챌린지페이지.png" width="688"/>

- 모션 인식을 활용한 버튼 클릭  
  <img src="README_assets/10_챌린지모드_버튼.gif" width="688"/>

- 녹화 후 영상 저장  
  <img src="README_assets/11_챌린지녹화.gif" width="688"/>

#### 마이 페이지

- 촬영한 영상과 시도한 영상 확인  
  <img src="README_assets/12_마이페이지.gif" width="688"/>
