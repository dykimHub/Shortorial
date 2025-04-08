import { ModifyingTitle, S3PutRequest } from "../constants/types";
import { axios } from "../utils/axios";

const REST_RECORDED_SHORTS_URL = "/api/shorts/recorded";

// 회원이 녹화한 쇼츠 조회
export async function getUploadedShorts() {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const res = await axios.get(`${REST_RECORDED_SHORTS_URL}`, {
      headers: {
        Authorization: token,
      },
    });

    return res.data;
  } catch (error: any) {
    if (error.response.data.status === "MEMBER_002") {
      alert(`${error.response.data.message}`);
    }
    console.log(error.response);
    console.error(error.response.data);
  }
}

// 회원이 녹화한 쇼츠 등록
export async function addRecordedShorts(shortsId: number, metadata: Record<string, string>) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");
    const s3PutRequest: S3PutRequest = {
      shortsId: shortsId,
      metadata: metadata,
    };
    const res = await axios.post(`${REST_RECORDED_SHORTS_URL}`, s3PutRequest, {
      headers: {
        Authorization: token,
      },
    });

    return res.data;
  } catch (error: any) {
    if (error.response.data.code === "MEMBER_002") {
      alert(`${error.response.data.message}`);
    }
    console.error(error.response.data);
  }
}

// 녹화한 쇼츠 상태 변화
export async function modifyRecordedShortsStatus(s3key: string, status: string) {
  try {
    //const token = "Bearer " + localStorage.getItem("accessToken");
    const res = await axios.put(`${REST_RECORDED_SHORTS_URL}`, {
      recordedShortsS3key: s3key,
      status: status,
    });
    return res.data;
  } catch (error: any) {
    console.error(error.response.data);
  }
}

// 회원이 녹화한 쇼츠 제목 수정
export async function updateTitle(modifyingShorts: ModifyingTitle) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const res = await axios.put(`${REST_RECORDED_SHORTS_URL}/title`, modifyingShorts, {
      headers: {
        Authorization: token,
      },
    });

    return res.data;
  } catch (error: any) {
    if (error.response.data.code === "SHORTS_006") {
      alert(`${error.response.data.message}`);
    } else if (error.response.data.code === "MEMBER_002") {
      alert(`${error.response.data.message}`);
    }
    console.log(error.response.data);
  }
}

// 저장된 쇼츠 삭제
export async function deleteShorts(recordedShortsId: number) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const res = await axios.delete(`${REST_RECORDED_SHORTS_URL}/${recordedShortsId}`, {
      headers: {
        Authorization: token,
      },
    });

    return res.data;
  } catch (error: any) {
    if (error.response.data.code === "MEMBER_002") {
      alert(`${error.response.data.message}`);
    }
    console.error(error.response.data);
  }
}
