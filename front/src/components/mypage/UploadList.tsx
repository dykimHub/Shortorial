import { useEffect, useState } from "react";
//import { UploadShorts } from "../../constants/types";
import { S3Object } from "../../constants/types";
//import { getUploadedShorts } from "../../apis/recordedshorts";
import { getRecordedShorts } from "../../apis/s3";
import { getCounting } from "../../apis/shorts";
import useMypageStore from "../../store/useMypageStore";
import styled from "styled-components";
import UploadComponent from "./UploadComponent";

export default function UploadList() {
  const [uploadedShortsList, setUploadedShortsList] = useState<S3Object[]>([]);
  const { setCountings } = useMypageStore();

  const handleDeleteShort = async () => {
    await loadRecordedShortsList();
    const countings = await getCounting();
    setCountings(countings);
  };

  const loadRecordedShortsList = async () => {
    //const data = await getUploadedShorts();
    const data = await getRecordedShorts();
    if (data) setUploadedShortsList(data);
  };

  useEffect(() => {
    loadRecordedShortsList();
  }, []);

  return (
    <Container>
      <SectionWrapper>
        <Section>
          <SectionConents>
            {uploadedShortsList.length === 0 ? (
              <P>저장한 영상이 없습니다</P>
            ) : (
              uploadedShortsList.map((uploadShorts) => (
                <UploadComponent
                  key={uploadShorts.key}
                  uploadShorts={uploadShorts}
                  onDelete={handleDeleteShort}
                />
              ))
            )}
          </SectionConents>
        </Section>
      </SectionWrapper>
    </Container>
  );
}

const P = styled.p`
  text-align: center;
`;

const Container = styled.div`
  position: relative;
  width: 100%;
  height: 100%;
  background-color: rgba(251, 37, 118, 0.1);
`;

const Section = styled.section`
  position: relative;
  margin: 0px 16px;
  box-sizing: border-box;
`;

const SectionConents = styled.div`
  display: flex;
  flex-wrap: wrap;

  &.nowrap {
    justify-content: center;
  }
`;
const SectionWrapper = styled.div`
  position: relative;
  max-width: 1100px;
  margin: 0 auto;
`;
