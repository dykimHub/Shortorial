import { axios } from "../utils/axios";

const REST_MEMBER_URL = "/api/member";

export interface MemberInfo {
  username: String;
  memberNickname: String;
  memberProfile: String;
}

// 중복검사
export function getDuplicateCheck(cate: string, input: string) {
  try {
    const response = axios.get(REST_MEMBER_URL + `/check/${cate}/${input}`).then((res) => {
      return res.data.dupCheck;
    });
    return response;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

// 회원가입
export function signUp(id: string, pass: string, nickname: string) {
  try {
    //console.log("signup");
    const response = axios
      .post(REST_MEMBER_URL + `/join`, {
        memberId: id,
        memberPass: pass,
        memberNickname: nickname,
      })
      .then((res) => {
        //console.log("AA");
        return res.data;
      });
    return response;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}

// 회원정보
export async function getInfo() {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");

    const response = await axios.get(`${REST_MEMBER_URL}/info`, {
      headers: {
        Authorization: token,
      },
    });

    return response.data;
  } catch (error: any) {
    console.log(error.response.data);
  }
}
