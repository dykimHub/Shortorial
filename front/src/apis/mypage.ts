import { axios } from "../utils/axios";

const REST_MYPAGE_URL = "/api/shorts";

export interface Countings {
  tryShortsCount: number;
  uploadShortsCount: number;
  youtubeUrlCount: number;
}

//사용자가 저장한 쇼츠 조회
export async function getUploadedShorts() {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const data = await axios.get(`${REST_MYPAGE_URL}/recoreded-shorts`, {
      headers: {
        Authorization: token,
      },
    });

    return data.data;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

//사용자가 저장한 쇼츠 이름 수정
export async function putUpdateTitle(oldTitle: string, newTitle: string, uploadNo: number) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const data = {
      uploadNo: uploadNo,
      oldTitle: oldTitle,
      newTitle: newTitle,
    };

    const response = await axios.put(`${REST_MYPAGE_URL}/rename`, data, {
      headers: {
        Authorization: token,
      },
    });

    return response.data;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

//사용자가 시도한 쇼츠 조회
export async function getTryShorts() {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const data = await axios.get(`${REST_MYPAGE_URL}/tried-shorts`, {
      headers: {
        Authorization: token,
      },
    });
    
    return data.data;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

//사용자 통계
export async function getCounting() {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const response = await axios.get(`${REST_MYPAGE_URL}/counting`, {
      headers: {
        Authorization: token,
      },
    });

    const countingsData: Countings = response.data;

    return countingsData;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}
