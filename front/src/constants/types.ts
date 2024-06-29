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
  shortsS3Link: string;
  shortsMusicTitle: string;
  shortsMusicSinger: string;
}

export interface RecomShorts {
  musicNo: number;
  shortsChallengerNum: number;
  shortsS3Link: string;
  shortsId: number;
  shortsTime: number;
  shortsTitle: string;
  shortsSource: string;
  shortsMusicTitle: string;
  shortsMusicSinger: string;
}

export interface UploadShorts {
  recordedShortsId: number;
  recordedShortsS3Link: string;
  recordedShortsTitle: string;
  recordedShortsYoutubeUrl: string;
}

export interface TryShorts {
  triedShortsId: number;
  triedShortsDate: Date;
  shortsDto: Shorts;
}
