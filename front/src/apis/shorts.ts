import { axios, pyaxios } from "../utils/axios";

const REST_SHORTS_URL = "/api/s3";
const REST_SHORTS_LIST_URL = "/api/shorts";

// S3 동영상 업로드
export async function postUploadShorts(blob: Blob, fileName: string) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const formData = new FormData(); // FormData 객체 생성
    // formData.append("file", file); // 파일 추가
    formData.append("file", blob, `${fileName}.mp4`);
    formData.append("fileName", fileName); // 파일 이름 추가

    const data = await axios.post(`${REST_SHORTS_URL}/upload`, formData, {
      headers: {
        Authorization: token,
        "Content-Type": "multipart/form-data", // 파일 업로드 시 Content-Type 설정
      },
    });

    return data.data;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

// 쇼츠 리스트 조회
export const getShortsList = async () => {
  try {
    const response = await axios.get(REST_SHORTS_LIST_URL);
    return response.data;
  } catch (error) {
    console.error(error);
  }
};

// 특정 쇼츠 조회
export const getShortsInfo = async (shortsNo: string) => {
  try {
    const response = await axios.get(`${REST_SHORTS_LIST_URL}/${shortsNo}`);
    console.log(response.data);

    return response.data;
  } catch (error) {
    console.error(error);
  }
};

//영상 시도하면 카운트
export async function getTryCount(shortsNo: number) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");
    const data = {
      shortsNo: shortsNo,
    };

    const response = await axios.put(`${REST_SHORTS_LIST_URL}/addTryCount`, data, {
      headers: {
        Authorization: token,
      },
    });

    return response.data;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

// S3에 있는 파일을 Blob으로 받기
export async function getS3Blob(shortsNo: number) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");
    const data = await axios.post(
      `${REST_SHORTS_URL}/bring/blob/${shortsNo}`,
      {},
      {
        headers: {
          Authorization: token,
        },
        responseType: "blob",
      }
    );

    return data.data;
  } catch (error) {
    console.error("Error getS3Blob:", error);
  }
}

export async function getMyS3Blob(uploadNo: number) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const data = await axios.post(
      `${REST_SHORTS_URL}/bring/myblob/${uploadNo}`,
      {},
      {
        headers: {
          Authorization: token,
        },
        responseType: "blob",
      }
    );

    return data.data;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

// 동영상 파일 이름 업데이트
export async function updateTitle(updatingShorts: Map<string, string>, uploadNo: number) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const updatingShortsObj = Object.fromEntries(updatingShorts);

    const data = await axios.put(`${REST_SHORTS_URL}/rename/${uploadNo}`, updatingShortsObj, {
      headers: {
        Authorization: token,
      },
    });

    return data.data;
  } catch (error) {
    console.error("Error Renaming data:", error);
  }
}

// 동영상 파일 이름 중복검사
export async function checkTitle(title: string) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const data = {
      title: title,
    };

    const response = await axios.post(`${REST_SHORTS_LIST_URL}/checkName`, data, {
      headers: {
        Authorization: token,
      },
    });
    // 이름이 존재하면 true, 존재하지 않으면 false를 반환
    return response.data;
  } catch (error) {
    console.error("Error Renaming data:", error);
  }
}

// 유튜브 업로드
const youtubeUrl = import.meta.env.VITE_YOUTUBE_URL;
export async function shareShorts(filePath: string, uploadNo: number) {
  console.log(filePath);

  try {
    const response = await axios.get(
      `${youtubeUrl}/authenticate?filePath=${encodeURIComponent(filePath)}&uploadNo=${uploadNo}`
    );
    // 서버에서 응답받은 authUrl로 이동
    window.location.href = response.data.authUrl;
  } catch (error) {
    console.error("Upload Error:", error);
  }
}

// 유튜브 업로드용 임시 파일 url
export async function getFilePath(uploadNo: number) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");
    const data = await axios.post(
      `${REST_SHORTS_URL}/save/${uploadNo}`,
      {},
      {
        headers: {
          Authorization: token,
        },
      }
    );

    return data.data;
  } catch (error) {
    console.log("filePath error:" + error);
  }
}

// 저장된 쇼츠 삭제
export async function deleteShorts(deletingShorts: Map<string, string>) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const deletingShortsObj = Object.fromEntries(deletingShorts);

    const data = await axios.delete(`${REST_SHORTS_URL}/delete`, {
      headers: {
        Authorization: token,
      },
      data: deletingShortsObj,
    });

    return data.data;
  } catch (error) {
    console.error("Error Deleting data:", error);
  }
}

// 인기순 쇼츠 조회
export async function getTopRankingShorts() {
  try {
    const data = await axios.get(`${REST_SHORTS_LIST_URL}/topRanking`);
    return data.data;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

// 추천 쇼츠 조회
export async function getRecommendedShorts() {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");
    const data = await pyaxios.get("/pyapi/music", {
      headers: {
        Authorization: token,
      },
    });
    return data.data;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}
