import { axios } from "../utils/axios";
import { uploadShortsToDB } from "./recordedshorts";

const REST_S3_URL = "/api/s3";

// S3에 있는 파일을 Blob으로 받기
export async function getS3Blob(shortsS3Key: string) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");
    const res = await axios.post(
      `${REST_S3_URL}/blob`,
      { s3key: shortsS3Key }, // map 전송

      {
        headers: {
          Authorization: token,
        },
        responseType: "blob",
      }
    );

    return res.data;
  } catch (error) {
    console.error("Error getS3Blob:", error);
  }
}

// S3에 동영상 업로드
export async function uploadShortsToS3(blob: Blob) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const formData = new FormData();
    formData.append("file", blob, "blob.mp4");

    const res = await axios.post(`${REST_S3_URL}/add`, formData, {
      headers: {
        Authorization: token,
        "Content-Type": "multipart/form-data",
      },
    });

    return uploadShortsToDB(res.data);
  } catch (error) {
    console.error("Error fetching data:", error);
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

// export async function getMyS3Blob(uploadNo: number) {
//   try {
//     const token = "Bearer " + localStorage.getItem("accessToken");

//     const res = await axios.post(
//       `${REST_S3_URL}/bring/myblob/${uploadNo}`,
//       {},
//       {
//         headers: {
//           Authorization: token,
//         },
//         responseType: "blob",
//       }
//     );

//     return res.data;
//   } catch (error) {
//     console.error("Error fetching data:", error);
//   }
// }

// 유튜브 업로드용 임시 파일 url
export async function getFilePath(uploadNo: number) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");
    const data = await axios.post(
      `${REST_S3_URL}/save/${uploadNo}`,
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
