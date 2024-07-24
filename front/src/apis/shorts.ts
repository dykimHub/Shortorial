import { axios, pyaxios } from "../utils/axios";

const REST_SHORTS_URL = "/api/shorts";

// 쇼츠 리스트 조회
export const getShortsList = async () => {
  try {
    const res = await axios.get(REST_SHORTS_URL);
    return res.data;
  } catch (error: any) {
    console.error(error.response.data);
  }
};

// 특정 쇼츠 조회
export const getShortsInfo = async (shortsId: string) => {
  try {
    const res = await axios.get(`${REST_SHORTS_URL}/${shortsId}`);
    return res.data;
  } catch (error: any) {
    console.error(error.response.data);
  }
};

// 인기순 쇼츠 조회
export async function getTopRankingShorts() {
  try {
    const res = await axios.get(`${REST_SHORTS_URL}/rank`);
    return res.data;
  } catch (error: any) {
    console.error(error.response.data);
  }
}

// 회원 쇼츠 통계
export async function getCounting() {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const res = await axios.get(`${REST_SHORTS_URL}/stats`, {
      headers: {
        Authorization: token,
      },
    });

    return res.data;
  } catch (error: any) {
    console.error(error.response.data);
  }
}

// 추천 쇼츠 조회
// export async function getRecommendedShorts() {
//   try {
//     const token = "Bearer " + localStorage.getItem("accessToken");
//     const data = await pyaxios.get("/pyapi/music", {
//       headers: {
//         Authorization: token,
//       },
//     });
//     return data.data;
//   } catch (error) {
//     console.error("Error fetching data:", error);
//   }
// }
