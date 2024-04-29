  import React from "react";
  import styled from "styled-components";
  import danceVideo from "../assets/sample.mp4";
  import MotionCamera from "../components/motion/MotionCamera";

  export default function LearnPage() {
    return (
      <Container>
        <VideoContainer>
          <Video src={danceVideo} controls />
        </VideoContainer>
        <MotionCameraContainer>
          <MotionCamera />
        </MotionCameraContainer>
      </Container>
    );
  }

  const Container = styled.div`
    position: relative;
    display: flex;
    width: 100%;
    height: 100%;
    background-color: #000;

    @media screen and (orientation: portrait) {
      flex-direction: column;
    }

    @media screen and (orientation: landscape) {
      flex-direction: row;
      justify-content: center;
    }
  `;

  const VideoContainer = styled.div`
    @media screen and (orientation: portrait) {
      width: 100%;
      height: 80%;
      display: flex;
      justify-content: center;
    }

    @media screen and (orientation: landscape) {
      height: 100%;
    }
  `;

  const Video = styled.video`
    width: 100%;
    height: 100%;
  `;

  const MotionCameraContainer = styled.div`
    position: relative;
    width: 100%;
    height: 100%;

    @media screen and (orientation: portrait) {
      display: block;
    }

    @media screen and (orientation: landscape) {
      display: block;
      transform: scaleX(-1); // 수평으로 뒤집기
    }
  `;
