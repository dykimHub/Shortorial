import { AxiosResponse } from "axios";

export function getAccessToken(): string {
  const token = localStorage.getItem("accessToken");
  if (token) return token;
  else return "";
}

export function setAccessToken(res: AxiosResponse) {
  const accessToken = res.data.accessToken || "";
  localStorage.setItem("accessToken", accessToken);
}
