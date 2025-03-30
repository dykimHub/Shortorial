export interface VideoSection {
  id: number;
  start: number;
  end: number;
  acc: number;
  maxAcc: number;
}

export default interface Member {
  memberId: string;
  memberEmail: string;
  memberNickname: string;
  memberPassword: string;
  memberProfile: string;
}
export interface Shorts {
  shortsId: number;
  shortsSource: string;
  shortsTitle: string;
  shortsTime: number;
  shortsChallengerNum: number;
  shortsS3URL: string;
  shortsMusicTitle: string;
  shortsMusicSinger: string;
}

export interface ShortsDTO {
  shortsId: number;
  shortsTitle: string;
  shortsS3Key: string;
  shortsS3URL: string;
}

export interface RecomShorts {
  musicNo: number;
  shortsChallengerNum: number;
  shortsS3URL: string;
  shortsId: number;
  shortsTime: number;
  shortsTitle: string;
  shortsSource: string;
  shortsMusicTitle: string;
  shortsMusicSinger: string;
}

export interface UploadShorts {
  recordedShortsId: number;
  recordedShortsS3key: string;
  recordedShortsS3URL: string;
  recordedShortsTitle: string;
  recordedShortsYoutubeURL: string;
  recordedShortsDate: Date;
}

export interface TryShorts {
  triedShortsId: number;
  triedShortsDate: Date;
  shortsDto: Shorts;
}

export interface Countings {
  triedShortsNum: number;
  recordedShortsNum: number;
  uploadedShortsNum: number;
}

export interface ModifyingShorts {
  recordedShortsId: number;
  newRecordedShortsTitle: string;
}

export interface ErrorResponse {
  status: string;
  code: string;
  message: string;
}

export interface S3Object {
  key: string; // S3에서의 파일 경로 (예: "test/file1.jpg")
  url: string; // Presigned URL 또는 정적 S3 URL
  size: number; // 파일 크기 (bytes)
  lastModified: Date; // 마지막 수정 날짜
}

export interface S3PutRequest {
  fileName: string;
  metadata: Record<string, string>;
}
