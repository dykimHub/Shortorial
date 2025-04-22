import { AxiosResponse } from "axios";

export function getAccessToken(): string {
  const token = localStorage.getItem("accessToken");
  if (token) return token;
  else return "";
}

export function setAccessToken(res: AxiosResponse) {
  const accessToken = res.data.accessToken || "";
  localStorage.setItem("accessToken", accessToken);
  console.log(accessToken.length > 0 ? "토큰이 저장되었습니다." : "토큰이 비어있습니다.");
}
