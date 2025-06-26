// ffmpegUtils.ts (MP4 인코딩 + 좌우 반전 반영 + 진행률 추적 유틸 추가)

import { getS3Blob } from "../apis/s3";

export const readBlobAsUint8Array = async (blob: Blob): Promise<Uint8Array> => {
  const buffer = await blob.arrayBuffer();
  return new Uint8Array(buffer);
};

export const writeBlobToFS = async (ffmpeg: any, filename: string, blob: Blob): Promise<void> => {
  const data = await readBlobAsUint8Array(blob);
  ffmpeg.FS("writeFile", filename, data);
};

export const runWithProgress = async (
  ffmpeg: any,
  args: string[],
  label: string,
  setFfmpegLog: (msg: string) => void,
  setLoadPath: (path: string) => void,
  loading: string
) => {
  ffmpeg.setProgress(({ ratio }: { ratio: number }) => {
    if (ratio >= 0 && ratio <= 1) {
      setLoadPath(loading);
      setFfmpegLog(`${label} ${Math.round(ratio * 100)}%\n`);
    }
  });

  await ffmpeg.run(...args);
};

export const handleRecorderStop = async ({
  chunks,
  short,
  ffmpeg,
  setFfmpegLog,
  setLoadPath,
  loading,
  addAudio,
}: {
  chunks: BlobPart[];
  short: any;
  ffmpeg: any;
  setFfmpegLog: (msg: string) => void;
  setLoadPath: (path: string) => void;
  loading: string;
  addAudio: (blob: Blob) => Promise<void>;
}) => {
  let s3blob: Blob | null = null;
  if (short) {
    s3blob = await getS3Blob(short.shortsS3Key);
  }

  if (!ffmpeg.isLoaded()) {
    await ffmpeg.load();
  }

  const userVideoBlob = new Blob(chunks, { type: "video/webm" });

  if (s3blob) {
    await writeBlobToFS(ffmpeg, "danceVideo.mp4", s3blob);
  }

  await runWithProgress(
    ffmpeg,
    ["-i", "danceVideo.mp4", "-vn", "-c:a", "aac", "dance_audio.aac"],
    "노래 추출...",
    setFfmpegLog,
    setLoadPath,
    loading
  );

  await addAudio(userVideoBlob);
};

export const addAudioToVideo = async ({
  ffmpeg,
  userVideoBlob,
  setFfmpegLog,
  setLoadPath,
  loading,
  makeDownloadURL,
}: {
  ffmpeg: any;
  userVideoBlob: Blob;
  setFfmpegLog: (msg: string) => void;
  setLoadPath: (path: string) => void;
  loading: string;
  makeDownloadURL: (blob: Blob) => Promise<void>;
}) => {
  await writeBlobToFS(ffmpeg, "userVideo.webm", userVideoBlob);

  await runWithProgress(
    ffmpeg,
    ["-i", "userVideo.webm", "-c:v", "libx264", "-an", "-preset", "ultrafast", "userVideo.mp4"],
    "포맷 변환 중...",
    setFfmpegLog,
    setLoadPath,
    loading
  );

  await runWithProgress(
    ffmpeg,
    [
      "-i",
      "userVideo.mp4",
      "-i",
      "dance_audio.aac",
      "-c:v",
      "copy",
      "-c:a",
      "aac",
      "-shortest",
      "tempMerged.mp4",
    ],
    "오디오 삽입 중...",
    setFfmpegLog,
    setLoadPath,
    loading
  );

  await runWithProgress(
    ffmpeg,
    ["-i", "tempMerged.mp4", "-vf", "hflip", "-preset", "ultrafast", "finalUserVideo.mp4"],
    "좌우 반전 중...",
    setFfmpegLog,
    setLoadPath,
    loading
  );

  const data = ffmpeg.FS("readFile", "finalUserVideo.mp4");
  const finalBlob = new Blob([data.buffer], { type: "video/mp4" });
  await makeDownloadURL(finalBlob);
};

export const startRecordingHandler = async ({
  stream,
  setMediaRecorder,
  short,
  ffmpeg,
  setFfmpegLog,
  setLoadPath,
  loading,
  addAudio,
  danceVideoRef,
}: {
  stream: MediaStream | null;
  setMediaRecorder: (r: MediaRecorder) => void;
  short: any;
  ffmpeg: any;
  setFfmpegLog: (msg: string) => void;
  setLoadPath: (path: string) => void;
  loading: string;
  addAudio: (blob: Blob) => Promise<void>;
  danceVideoRef: React.RefObject<HTMLVideoElement>;
}) => {
  if (!stream) {
    alert("카메라 접근을 허용해주세요.");
    return;
  }

  try {
    const chunks: BlobPart[] = [];
    const recorder = new MediaRecorder(stream);

    recorder.ondataavailable = (e) => chunks.push(e.data);
    recorder.onstop = () =>
      handleRecorderStop({
        chunks,
        short,
        ffmpeg,
        setFfmpegLog,
        setLoadPath,
        loading,
        addAudio,
      });

    recorder.start();
    setMediaRecorder(recorder);
    if (danceVideoRef.current) danceVideoRef.current.play();
  } catch (e) {
    console.error(e);
    alert("녹화를 다시 시작해 주세요.");
  }
};
