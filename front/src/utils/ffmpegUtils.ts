import { getS3Blob, uploadShortsToS3 } from "../apis/s3";
import { createFFmpeg } from "@ffmpeg/ffmpeg";

import loading from "../assets/challenge/loading.gif";
import complete from "../assets/challenge/complete.svg";
import uncomplete from "../assets/challenge/uncomplete.svg";

const ffmpeg = createFFmpeg({ log: false });

enum Path {
  danceVideo = "dance_video.mp4",
  danceAudio = "dance_audio.aac",
  userVideo = "user_video.mp4",
  mergeVideo = "merge_vedio.mp4",
  flipVideo = "flip_video.mp4",
}

// Blob → Uint8Array 변환
export const readBlobAsUint8Array = async (blob: Blob) => {
  const buffer = await blob.arrayBuffer();
  return new Uint8Array(buffer);
};

// FFmpeg 가상 파일 시스템에 Blob 저장
export const writeBlobToFS = async (filename: string, blob: Blob) => {
  const data = await readBlobAsUint8Array(blob);
  ffmpeg.FS("writeFile", filename, data);
};

// FFmpeg 명령 실행하며 진행률 출력
export const runWithProgress = async (
  args: string[],
  label: string,
  img: string,
  setFfmpegLog: (msg: string) => void,
  setLoadPath: (img: string) => void
) => {
  setLoadPath(img);

  ffmpeg.setProgress(({ ratio }: { ratio: number }) => {
    if (ratio > 0) {
      setFfmpegLog(`${label} ${Math.round(ratio * 100)}%\n`);
    }
  });

  await ffmpeg.run(...args);
};

// 녹화 종료 시 처리
export const handleRecorderStop = async ({
  shortsBlob,
  userBlob,
  setFfmpegLog,
  setLoadPath,
}: {
  shortsBlob: Blob;
  userBlob: Blob;
  setFfmpegLog: (msg: string) => void;
  setLoadPath: (img: string) => void;
}) => {
  await writeBlobToFS("dance_video.mp4", shortsBlob);

  await runWithProgress(
    ["-i", Path.danceVideo, "-vn", "-c:a", "copy", Path.danceAudio],
    "노래 추출...",
    loading,
    setFfmpegLog,
    setLoadPath
  );

  await addAudioToVideo({ userBlob, setFfmpegLog, setLoadPath });
};

// 오디오를 사용자 비디오에 합성
export const addAudioToVideo = async ({
  userBlob,
  setFfmpegLog,
  setLoadPath,
}: {
  userBlob: Blob;
  setFfmpegLog: (msg: string) => void;
  setLoadPath: (img: string) => void;
}) => {
  await writeBlobToFS("user_video.mp4", userBlob);

  await runWithProgress(
    [
      "-i",
      Path.userVideo,
      "-i",
      Path.danceAudio,
      //"-map",
      // "0:v:0", // 첫 번째 입력의 비디오만 사용
      // "-map",
      // "1:a:0", // 두 번째 입력의 오디오만 사용
      "-c:v",
      "copy", // 비디오 복사
      "-c:a",
      "copy", // 오디오 복사
      "-shortest", // 더 짧은 쪽에 맞춰서 자름
      "user_merge_video.mp4",
    ],
    "오디오 합성 중...",
    loading,
    setFfmpegLog,
    setLoadPath
  );

  await flipUserVideo({ setFfmpegLog, setLoadPath });
};

export const flipUserVideo = async ({
  setFfmpegLog,
  setLoadPath,
}: {
  setFfmpegLog: (msg: string) => void;
  setLoadPath: (img: string) => void;
}) => {
  await runWithProgress(
    [
      "-i",
      Path.mergeVideo,
      "-vf",
      "hflip", // 좌우 반전
      "-preset",
      "fast", // 압축 줄여서 빠르게(용량 증가)
      Path.flipVideo,
    ],
    "좌우 반전 중...",
    loading,
    setFfmpegLog,
    setLoadPath
  );

  const data = ffmpeg.FS("readFile", "user_flip_video.mp4");
  const finalBlob = new Blob([data.buffer], { type: "video/mp4" });
  await s3Upload({ finalBlob, setFfmpegLog, setLoadPath });
};

export const startRecordingHandler = async ({
  shortsS3Key,
  mediaRecorder,
  danceVideoRef,
  setFfmpegLog,
  setLoadPath,
  onStopEnd,
}: {
  shortsS3Key: string;
  mediaRecorder: MediaRecorder | null;
  danceVideoRef: React.RefObject<HTMLVideoElement>;
  setFfmpegLog: (msg: string) => void;
  setLoadPath: (img: string) => void;
  onStopEnd?: () => void;
}) => {
  if (!mediaRecorder) return;

  // FFmpeg 로드
  if (!ffmpeg.isLoaded()) await ffmpeg.load();

  try {
    const chunks: BlobPart[] = [];
    mediaRecorder.ondataavailable = (e) => chunks.push(e.data);

    // MediaRecorder 중지
    mediaRecorder.onstop = async () => {
      setFfmpegLog("처리 준비...");
      setLoadPath(loading);
      const shortsBlob = await getS3Blob(shortsS3Key);
      const userBlob = new Blob(chunks, { type: mediaRecorder.mimeType });

      await handleRecorderStop({ shortsBlob, userBlob, setFfmpegLog, setLoadPath });

      if (onStopEnd) onStopEnd();
    };

    // MediaRecorder 실행
    mediaRecorder.start();
    // 쇼츠 실행
    if (danceVideoRef.current) danceVideoRef.current.play();
  } catch (e) {
    console.error(e);
    alert("녹화 도중 오류가 발생했습니다. 다시 시도해 주세요.");
  }
};

// 오디오를 사용자 비디오에 합성
export const s3Upload = async ({
  finalBlob,
  setFfmpegLog,
  setLoadPath,
}: {
  finalBlob: Blob;
  setFfmpegLog: (msg: string) => void;
  setLoadPath: (img: string) => void;
}) => {
  try {
    await uploadShortsToS3(finalBlob);
    setLoadPath(complete);
    setFfmpegLog("저장 완료");
  } catch (error) {
    setLoadPath(uncomplete);
    setFfmpegLog("저장 실패");
    console.error("s3 upload fail", error);
  }
};
