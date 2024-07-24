import { axios } from "../utils/axios";

const REST_TRIED_SHORTS_URL = "/api/shorts/tried";

// 회원이 시도한 쇼츠 조회
export async function getTryShorts() {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const res = await axios.get(`${REST_TRIED_SHORTS_URL}`, {
      headers: {
        Authorization: token,
      },
    });

    return res.data;
  } catch (error: any) {
    console.error(error.response.data);
  }
}

// 회원이 시도한 쇼츠에 추가
export async function getTryCount(shortsId: number) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const res = await axios.post(
      `${REST_TRIED_SHORTS_URL}/${shortsId}`,
      {},
      {
        headers: {
          Authorization: token,
        },
      }
    );

    return res.data;
  } catch (error: any) {
    console.error(error.response.data);
  }
}

// 회원이 시도한 쇼츠에서 삭제
export async function deleteTriedShorts(shortsId: number) {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const res = await axios.delete(`${REST_TRIED_SHORTS_URL}/${shortsId}`, {
      headers: {
        Authorization: token,
      },
    });

    return res.data;
  } catch (error: any) {
    console.error(error.response.data);
  }
}
