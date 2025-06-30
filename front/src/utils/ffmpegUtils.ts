import { createFFmpeg } from "@ffmpeg/ffmpeg";

const ffmpeg = createFFmpeg({ log: false });

enum Path {
  danceVideo = "dance_video.mp4",
  danceAudio = "dance_audio.aac",
  userVideo = "user_video.mp4",
  mergeVideo = "merge_vedio.mp4",
  flipVideo = "flip_video.mp4",
}

// 오디오 추츨
export const extractAudio = async ({
  shortsBlob,
  setFfmpegLog,
}: {
  shortsBlob: Blob;
  setFfmpegLog: (msg: string) => void;
}) => {
  if (!ffmpeg.isLoaded()) await ffmpeg.load();

  await writeBlobToFS(Path.danceVideo, shortsBlob);

  await runWithProgress(
    [
      "-i",
      Path.danceVideo,
      "-vn", // 비디오 무시
      "-c:a",
      "copy", // 오디오 복사
      Path.danceAudio,
    ],
    "노래 추출...",
    setFfmpegLog
  );
};

// 오디오를 비디오에 합성
export const addAudioToVideo = async ({
  userBlob,
  setFfmpegLog,
}: {
  userBlob: Blob;
  setFfmpegLog: (msg: string) => void;
}) => {
  if (!ffmpeg.isLoaded()) await ffmpeg.load();

  await writeBlobToFS(Path.userVideo, userBlob);

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
      Path.mergeVideo,
    ],
    "오디오 합성 중...",
    setFfmpegLog
  );
};

// 비디오 좌우 반전
export const flipUserVideo = async ({ setFfmpegLog }: { setFfmpegLog: (msg: string) => void }) => {
  if (!ffmpeg.isLoaded()) await ffmpeg.load();

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
    setFfmpegLog
  );

  // 최종 비디오 파일 읽기
  const finalVideo = ffmpeg.FS("readFile", Path.flipVideo);
  const finalBlob = new Blob([finalVideo.buffer], { type: "video/mp4" });

  return finalBlob;
};

// Blob → Uint8Array
// FFmpeg.FS('writeFile')은 Uint8Array만 받을 수 있음
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
  setFfmpegLog: (msg: string) => void
) => {
  ffmpeg.setProgress(({ ratio }: { ratio: number }) => {
    if (ratio > 0) {
      setFfmpegLog(`${label} ${Math.round(ratio * 100)}%\n`);
    }
  });

  await ffmpeg.run(...args);
};
