import styled from "styled-components";
import { Pause, PlayArrow, Close } from "@mui/icons-material";
import { Skeleton } from "@mui/material";
import { RecomShorts, Shorts } from "../../constants/types";
import { MouseEvent, useRef, useState } from "react";

interface ShortsVideoPrpos {
  shortsInfo: RecomShorts | Shorts | undefined;
  isLoading?: boolean;
  isSerise?: boolean;
  onClick?: () => void;
  triedShortsId?: number;
  onDelete?: (id: number) => void;
}

const ShortsVideoItem = ({
  shortsInfo,
  isLoading,
  isSerise,
  onClick,
  triedShortsId,
  onDelete,
}: ShortsVideoPrpos) => {
  const videoRef = useRef<HTMLVideoElement>(null);
  const [isPlaying, setIsPlaying] = useState(false);

  const handlePlayButtonClick = (e: MouseEvent) => {
    e.stopPropagation();

    if (videoRef.current) {
      if (videoRef.current.paused) {
        videoRef.current.play();
        videoRef.current.autoplay = true;
        setIsPlaying(true);
      } else {
        videoRef.current.pause();
        videoRef.current.currentTime = 0;
        setIsPlaying(false);
      }
    }
  };

  return (
    <>
      {isLoading ? (
        <SkeletonContainer>
          <Skeleton variant="rounded" className="video" />
        </SkeletonContainer>
      ) : (
        shortsInfo && (
          <VideoContainer className={isSerise ? "serise" : ""} onClick={onClick}>
            <VideoBox>
              <Video ref={videoRef} src={shortsInfo.shortsS3Link} crossOrigin="anonymous" />
              <Gradient className="gradient" />
              <PlayButton onClick={handlePlayButtonClick}>
                {!isPlaying ? (
                  <PlayArrow sx={{ color: "white" }} />
                ) : (
                  <Pause sx={{ color: "white" }} />
                )}
              </PlayButton>
              {triedShortsId && (
                <CloseButton
                  onClick={(e) => {
                    e.stopPropagation();
                    if (onDelete && triedShortsId !== undefined) {
                      onDelete(triedShortsId);
                    }
                  }}
                />
              )}
            </VideoBox>
            <DetailsContainer>
              <div className="title">{shortsInfo.shortsTitle}</div>
              {/* <div className="detail">챌린저 {shortsInfo.shortsChallengerNum}명</div> */}
            </DetailsContainer>
          </VideoContainer>
        )
      )}
    </>
  );
};

const Gradient = styled.div`
  position: absolute;
  bottom: 5px;
  width: 100%;
  height: 100px;
  background: rgb(0, 0, 0);
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.25));
  border-radius: 0 0 12px 12px;
  opacity: 0;
  transition: opacity 0.2s;
`;

const PlayButton = styled.button`
  position: absolute;
  bottom: 12px;
  left: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 36px;
  height: 36px;
  background-color: rgba(0, 0, 0, 0.8);
  border-radius: 50%;
  transition: background-color 0.2s;
`;

const VideoBox = styled.div`
  position: relative;

  &:hover button {
    background-color: #fb2576;
  }

  &:hover .gradient {
    opacity: 1;
  }
`;

const VideoContainer = styled.div`
  position: relative;
  width: calc(100% / var(--grid-items-per-row) - var(--grid-item-margin));
  margin: calc(var(--grid-item-margin) / 2);
  color: #000;

  &.serise {
    --grid-items-per-row: 3;
    max-width: 220px;
  }

  @media screen and (min-width: 600px) {
    max-width: var(--grid-item-max-width);
  }
`;

const Video = styled.video`
  width: 100%;
  border-radius: 12px;
  object-fit: contain;
`;

const DetailsContainer = styled.div`
  width: 100%;

  .title {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    height: 48px;
    line-height: 24px;
    font-size: 16px;
    font-weight: bold;
    text-overflow: ellipsis;
    overflow: hidden;
  }

  .detail {
    font-size: 14px;
    color: #606060;
  }
`;

const SkeletonContainer = styled.div`
  position: relative;
  width: calc(100% / var(--grid-items-per-row) - var(--grid-item-margin));
  margin: calc(var(--grid-item-margin) / 2);
  aspect-ratio: 9 / 16;

  .video {
    height: 100%;
  }
`;

const CloseButton = styled(Close)`
  position: absolute;
  right: 5px;
  top: 8px;
  cursor: pointer;
`;

export default ShortsVideoItem;
