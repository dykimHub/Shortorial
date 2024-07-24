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
  shortsS3Key: string;
  shortsS3URL: string;
  shortsMusicTitle: string;
  shortsMusicSinger: string;
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
