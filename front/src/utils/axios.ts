import Axios from "axios";
import { REACT_APP_SPRING_URL } from "../constants/api";

export const axios = Axios.create({
  baseURL: REACT_APP_SPRING_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// export const pyaxios = Axios.create({
//   baseURL: REACT_APP_FLASK_URL,
//   headers: {
//     "Content-Type": "application/json",
//   },
// });
