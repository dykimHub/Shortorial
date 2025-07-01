import { axios } from "../utils/axios";

const REST_S3_URL = "/api/s3";

export async function getPresignedGetURL(s3key: string) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");
    const res = await axios.get(`${REST_S3_URL}/get`, {
      headers: { Authorization: token },
      params: { s3key },
    });

    return res.data;
  } catch (error: any) {
    console.error(error.response.data);
  }
}

export async function s3Put(
  presignedPutURL: string,
  videoBlob: Blob,
  metadata: { [key: string]: string }
) {
  const res = await axios.put(presignedPutURL, videoBlob, {
    headers: {
      "Content-Type": "video/mp4",
      "x-amz-meta-song": metadata["song"],
    },
  });

  return res.data;
}

export async function tryS3Get(presignedGetURL: string) {
  try {
    await axios.get(presignedGetURL);
    return true;
  } catch (error: any) {
    return false;
  }
}

// 유튜브 업로드
// const youtubeUrl = import.meta.env.VITE_YOUTUBE_URL;
// export async function shareShorts(filePath: string, uploadNo: number) {
//   console.log(filePath);

//   try {
//     const response = await axios.get(
//       `${youtubeUrl}/authenticate?filePath=${encodeURIComponent(filePath)}&uploadNo=${uploadNo}`
//     );
//     // 서버에서 응답받은 authUrl로 이동
//     window.location.href = response.data.authUrl;
//   } catch (error: any) {
//     console.error(error.response.data);
//   }
// }

// 유튜브 업로드용 임시 파일 url
// export async function getFilePath(uploadNo: number) {
//   try {
//     const token = "Bearer " + localStorage.getItem("accessToken");
//     const data = await axios.post(
//       `${REST_S3_URL}/save/${uploadNo}`,
//       {},
//       {
//         headers: {
//           Authorization: token,
//         },
//       }
//     );

//     return data.data;
//   } catch (error: any) {
//     console.error(error.response.data);
//   }
// }
