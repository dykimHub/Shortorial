import { ModifyingShorts, S3PutRequest } from "../constants/types";
import { axios } from "../utils/axios";
import { deleteShortsFromS3 } from "./s3.ts";

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
    console.error(error.response.data);
  }
}

// 회원이 녹화한 쇼츠 등록
export async function getPresignedPutURL(shortsId: number, metadata: Record<string, string>) {
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
    console.error(error.response.data);
  }
}

// // 회원이 녹화한 쇼츠 등록
// export async function uploadShortsToDB(s3key: string) {
//   try {
//     const token = "Bearer " + localStorage.getItem("accessToken");

//     const res = await axios.post(
//       `${REST_RECORDED_SHORTS_URL}`,
//       { s3key: s3key }, // map 전송
//       {
//         headers: {
//           Authorization: token,
//         },
//       }
//     );

//     return res.data;
//   } catch (error: any) {
//     console.error(error.response.data);
//   }
// }

// 회원이 녹화한 쇼츠 제목 수정
export async function updateTitle(modifyingShorts: ModifyingShorts) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const res = await axios.put(`${REST_RECORDED_SHORTS_URL}`, modifyingShorts, {
      headers: {
        Authorization: token,
      },
    });

    return res.data;
  } catch (error: any) {
    console.log(error.response.data);
    if (error.response.data.code === "SHORTS_006") {
      alert(`${error.response.data.message}`);
    }
  }
}

// 저장된 쇼츠 삭제
export async function deleteShorts(s3key: string) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    console.log(s3key);

    await axios.delete(`${REST_RECORDED_SHORTS_URL}`, {
      headers: {
        Authorization: token,
      },
      data: {
        s3key: s3key,
      },
    });

    return deleteShortsFromS3(s3key);
  } catch (error: any) {
    console.error(error.response.data);
  }
}
