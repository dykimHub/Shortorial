import styled from "styled-components";
import { VideoSection } from "../../constants/types";
import SectionButton from "../button/SectionButton";
import { useCallback, useEffect } from "react";
import useLearnStore from "../../store/useLearnStore";

interface SectionButtonListProps {
  sectionList: VideoSection[] | null;
  parentWidth: number | undefined;
  currentTime: number;
  isLooping?: boolean;
  clickHandler: (section: VideoSection) => void;
}

const SectionButtonList = ({
  sectionList = [],
  parentWidth = 0,
  currentTime = 0,
  isLooping = false,
  clickHandler,
}: SectionButtonListProps) => {
  const [currentSection, loopSection, setCurrentSection, setLoopSection] = useLearnStore(
    (state) => [
      state.currentSection,
      state.loopSection,
      state.setCurrentSection,
      state.setLoopSection,
    ]
  );

  // 현재 시간에 있는 구간 반환
  const loadCurrSection = useCallback(() => {
    let result: VideoSection | undefined;
    const time = currentTime;

    if (sectionList) {
      result = sectionList.find((item) => time >= item.start && time < item.end);
    }

    //result = result ?? { id: 0, start: 0, end: 0, acc: 0, maxAcc: 0 };
    result = result ?? { id: 0, start: 0, end: 0 };
    setCurrentSection(result);

    if (isLooping) setLoopSection(result);
  }, [currentTime, isLooping, sectionList, setCurrentSection, setLoopSection]);

  // 현재 구간 인덱스 설정
  useEffect(() => {
    loadCurrSection();
  }, [loadCurrSection, currentTime]);

  return (
    <Container>
      {sectionList?.map((section) => {
        return (
          <SectionButton
            key={section.id}
            time={section.start}
            isSmall={parentWidth < 100}
            active={section.id === currentSection.id}
            isLooping={(isLooping && section.id === loopSection?.id) ?? -1}
            onClick={() => clickHandler(section)}
          ></SectionButton>
        );
      })}
    </Container>
  );
};

const Container = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;

  button {
    margin: 8px 0;
  }

  @media screen and (orientation: portrait) {
    flex-direction: row;
    justify-content: start;
    align-items: flex-start;
    overflow: scroll;
    height: auto;
    padding: 16px 0;

    button {
      min-width: 120px;
      margin: 0 8px;
    }
  }
`;

export default SectionButtonList;
