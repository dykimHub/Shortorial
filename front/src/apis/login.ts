import { isAxiosError } from "axios";
import { axios } from "../utils/axios";
import parseJwt from "../modules/auth/parseJwt";
import { getAccessToken, setAccessToken } from "../modules/auth/accessToken";

const REST_MEMBER_API = "/api/member";

//일반 로그인
export async function postLogin(id: string, password: string) {
  try {
    const res = await axios.post(REST_MEMBER_API + "/login", {
      memberId: id,
      memberPass: password,
    });

    setAccessToken(res);
    //로그인한 회원의 id 반환
    return parseJwt(getAccessToken()).sub;
  } catch (error) {
    if (isAxiosError(error) && error.response) {
      throw new Error(error.response.status + "");
    }
  }
}

export async function getMemberInfo() {
  try {
    const token = "Bearer " + localStorage.getItem("accessToken");
    const res = await axios.get(REST_MEMBER_API + `/info`, {
      headers: {
        Authorization: token,
      },
    });

    return res.data;
  } catch (error) {
    if (isAxiosError(error) && error.response) {
      throw new Error("500");
    }
  }
}
