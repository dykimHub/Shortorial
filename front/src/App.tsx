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
import VideoTrimPage from "./pages/VideoTrimPage";
import VideoMarkerPage from "./pages/VIdeoMarkerPage";
import MyPage from "./pages/MyPage";
import FeedPage from "./pages/FeedPage";

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
          <Route path="/video-trim" element={<VideoTrimPage />} />
          <Route path="/video-marker" element={<VideoMarkerPage />} />
          {/* <Route path="/video-resize" element={<VideoResizePage />} /> */}
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/feed" element={<FeedPage />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
