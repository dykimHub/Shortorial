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
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

// 회원이 녹화한 쇼츠 등록
export async function uploadShortsToDB(s3key: string) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const res = await axios.post(
      `${REST_RECORDED_SHORTS_URL}`,
      { s3key: s3key }, // map 전송
      {
        headers: {
          Authorization: token,
        },
      }
    );

    return res.data;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

// 저장된 쇼츠 삭제
export async function deleteShorts(deletingShorts: Map<string, string>) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const deletingShortsObj = Object.fromEntries(deletingShorts);

    const res = await axios.delete(`${REST_RECORDED_SHORTS_URL}/delete`, {
      headers: {
        Authorization: token,
      },
      data: deletingShortsObj,
    });

    return res.data;
  } catch (error) {
    console.error("Error Deleting data:", error);
  }
}

// 동영상 파일 이름 중복검사
// export async function checkTitle(title: string) {
//   try {
//     const token = "Bearer " + localStorage.getItem("accessToken");

//     const data = {
//       title: title,
//     };

//     const response = await axios.post(`${REST_SHORTS_URL}/checkName`, data, {
//       headers: {
//         Authorization: token,
//       },
//     });
//     // 이름이 존재하면 true, 존재하지 않으면 false를 반환
//     return response.data;
//   } catch (error) {
//     console.error("Error Renaming data:", error);
//   }
// }

// 동영상 파일 이름 업데이트
// export async function updateTitle(updatingShorts: Map<string, string>, uploadNo: number) {
//   try {
//     const token = "Bearer " + localStorage.getItem("accessToken");

//     const updatingShortsObj = Object.fromEntries(updatingShorts);

//     const data = await axios.put(`${REST_SHORTS_URL}/rename/${uploadNo}`, updatingShortsObj, {
//       headers: {
//         Authorization: token,
//       },
//     });

//     return data.data;
//   } catch (error) {
//     console.error("Error Renaming data:", error);
//   }
// }

//사용자가 저장한 쇼츠 이름 수정
// export async function putUpdateTitle(oldTitle: string, newTitle: string, uploadNo: number) {
//   try {
//     const token = "Bearer " + localStorage.getItem("accessToken");

//     const data = {
//       uploadNo: uploadNo,
//       oldTitle: oldTitle,
//       newTitle: newTitle,
//     };

//     const response = await axios.put(`${REST_S3_URL}/rename`, data, {
//       headers: {
//         Authorization: token,
//       },
//     });

//     return response.data;
//   } catch (error) {
//     console.error("Error fetching data:", error);
//   }
// }
