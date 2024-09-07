import { useState } from "react";
import styled from "styled-components";
import { Edit, EditOff, Close, Download } from "@mui/icons-material";
import { updateTitle, deleteShorts } from "../../apis/recordedshorts";
import { getS3Blob } from "../../apis/s3";
import { UploadShorts } from "../../constants/types";
import moment from "moment";

interface UploadComponentProps {
  uploadShorts: UploadShorts;
  onDelete: () => void;
}

const UploadComponent = ({ uploadShorts, onDelete }: UploadComponentProps) => {
  const [title, setTitle] = useState<string>(uploadShorts.recordedShortsTitle);
  const [modify, setModify] = useState<boolean>(false);
  const [download, setDownload] = useState<boolean>(false);
  //const [share, setShare] = useState<boolean>(false);
  //const [link, setLink] = useState<string | null>(uploadShorts.recordedShortsYoutubeUrl || null);

  const handleTitleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setTitle(event.target.value);
  };

  const deleteUploadedShorts = async (recordedShortsS3key: string) => {
    alert("복구할 수 없습니다. 그래도 삭제하시겠습니까?");
    await deleteShorts(recordedShortsS3key);
    onDelete();
  };

  const saveTitle = async (recordedShortsId: number, title: string) => {
    if (title.includes("/")) {
      alert("제목에 슬래시(/)를 포함할 수 없습니다.");
      return;
    }

    const modifyingShorts = {
      recordedShortsId: recordedShortsId,
      newRecordedShortsTitle: title,
    };

    const data = await updateTitle(modifyingShorts);
    if (data) {
      setTitle(title);
      setModify(false);
    }
  };

  const downloadVideo = async () => {
    setDownload(true);

    try {
      const videoBlob = await getS3Blob(uploadShorts.recordedShortsS3key);
      const downloadUrl = URL.createObjectURL(videoBlob);

      const downloadLink = document.createElement("a");
      downloadLink.href = downloadUrl;
      downloadLink.setAttribute("download", `${title}.mp4`);
      document.body.appendChild(downloadLink);
      downloadLink.click();

      document.body.removeChild(downloadLink);
      URL.revokeObjectURL(downloadUrl);
    } catch (err) {
      alert("다운로드에 실패했습니다.");
      console.log(err);
    } finally {
      setDownload(false);
    }
  };

  // const shareShortsToYoutube = async () => {
  //   setShare(true);

  //   const filePath = await getFilePath(uploadShorts.recordedShortsId);
  //   await shareShorts(filePath, uploadShorts.recordedShortsId);
  // };

  // useEffect(() => {
  //   const urlParams = new URLSearchParams(window.location.search);
  //   const alertParam = urlParams.get("auth");

  //   if (alertParam === "true") {
  //     alert(
  //       "유튜브 권한 설정이 완료되었습니다.\n공유 버튼을 누르면 채널에 비공개 동영상으로 업로드 됩니다."
  //     );
  //     urlParams.delete("auth");
  //     const newUrl = window.location.pathname;
  //     window.history.replaceState({}, document.title, newUrl);
  //   }
  // }, []);

  return (
    <ResultContainer>
      <VideoContainer>
        <Gradient className="gradient" />
        <Video src={uploadShorts.recordedShortsS3URL} controls crossOrigin="anonymous"></Video>
        <MyVideoControlComponent>
          <CloseIcon
            onClick={() => deleteUploadedShorts(uploadShorts.recordedShortsS3key)}
          ></CloseIcon>
          {!download && <DownloadIcon onClick={downloadVideo}></DownloadIcon>}
          {download && (
            <DownloadingIcon src="../src/assets/mypage/downloading.gif"></DownloadingIcon>
          )}
          {/* <IosShareIcon onClick={shareShortsToYoutube}></IosShareIcon>
          {uploadShorts.recordedShortsYoutubeURL && (
            <YoutubeIcon
              onClick={() => (window.location.href = uploadShorts.recordedShortsYoutubeURL)}
            ></YoutubeIcon>
          )}
          {share && <SharingIcon src="../src/assets/mypage/downloading.gif"></SharingIcon>} */}
        </MyVideoControlComponent>
      </VideoContainer>
      {!modify && (
        <TitleContainer>
          <Title>{title}</Title>
          <ModifyIcon onClick={() => setModify(true)}></ModifyIcon>
        </TitleContainer>
      )}
      {modify && (
        <TitleContainer>
          <InputBox type="text" value={title} onChange={handleTitleChange} />
          <CheckIcon onClick={() => saveTitle(uploadShorts.recordedShortsId, title)}></CheckIcon>
        </TitleContainer>
      )}
      <div className="date">{moment(uploadShorts.recordedShortsDate).format("MMM DD, hA")}</div>
    </ResultContainer>
  );
};

const ResultContainer = styled.div`
  display: flex;
  flex-direction: column;

  position: relative;
  width: calc(100% / var(--grid-items-per-row) - var(--grid-item-margin));
  margin: calc(var(--grid-item-margin) / 2);
  color: #000;

  &.serise {
    --grid-items-per-row: 3;
    max-width: 220px;
  }

  @media screen and (min-width: 600px) {
    max-width: var(--grid-item-max-width);
  }

  .date {
    margin-right: auto;
  }
`;

const VideoContainer = styled.div`
  display: flex;
  position: relative;
`;

const Gradient = styled.div`
  position: absolute;
  bottom: 5px;
  width: 100%;
  height: 100px;
  background: rgb(0, 0, 0);
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.25));
  border-radius: 0 0 12px 12px;
  opacity: 0;
  transition: opacity 0.2s;
`;

const Video = styled.video`
  width: 100%;
  border-radius: 12px;
  object-fit: cover;
`;

const MyVideoControlComponent = styled.div`
  position: absolute;
  display: flex;
  flex-direction: column;
  right: 5px;
  top: 8px;
`;

const DownloadIcon = styled(Download)`
  cursor: pointer;
  margin-bottom: 5px;
`;

const DownloadingIcon = styled.img`
  cursor: pointer;
  margin-bottom: 5px;
  width: 30px;
  height: 30px;
`;

// const IosShareIcon = styled(IosShare)`
//   cursor: pointer;
//   margin-bottom: 5px;
// `;

// const SharingIcon = styled.img`
//   cursor: pointer;
//   margin-bottom: 5px;
// `;

// const YoutubeIcon = styled(YouTube)`
//   cursor: pointer;
//   margin-bottom: 5px;
//   color: red;
// `;

const CloseIcon = styled(Close)`
  cursor: pointer;
  margin-bottom: 5px;
`;

const TitleContainer = styled.div`
  display: flex;
  position: relative;
  width: 100%;
`;

const Title = styled.div`
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  height: 24px;
  line-height: 24px;
  font-size: 16px;
  font-weight: bold;
  padding-right: 1.4rem;
`;

const ModifyIcon = styled(Edit)`
  position: absolute;
  cursor: pointer;
  right: 0;
  bottom: 0;
`;

const InputBox = styled.input`
  border: 0;
  outline: none;
  height: 24px;
  line-height: 24px;
  font-size: 16px;
  overflow: hidden;
  padding-right: 2rem;
  width: 100%;
`;

const CheckIcon = styled(EditOff)`
  position: absolute;
  right: 0px;
  bottom: 0px;
  cursor: pointer;
`;

export default UploadComponent;
