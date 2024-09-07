import { Routes, Route, BrowserRouter } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import PrivateRoute from "./modules/auth/PrivateRoute";
import GlobalStyle from "./GlobalStyle";
import MainPage from "./pages/MainPage";
import LandingPage from "./pages/LandingPage";
import LoginForm from "./pages/LoginForm";
import SignUp from "./pages/SignUpPage";
import LearnPage from "./pages/LearnPage";
import ChallengePage from "./pages/ChallengePage";
import MyPage from "./pages/MyPage";

function App() {
  return (
    <>
      <GlobalStyle />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/main" element={<PrivateRoute component={<MainPage />} />} />
          <Route path="/login" element={<LoginForm />} />
          <Route path="/sign-up" element={<SignUp />} />
          <Route path="/learn/:shortsId" element={<PrivateRoute component={<LearnPage />} />} />
          <Route
            path="/challenge/:shortsId"
            element={<PrivateRoute component={<ChallengePage />} />}
          />
          {/* <Route path="/video-resize" element={<VideoResizePage />} /> */}
          <Route path="/mypage" element={<MyPage />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
