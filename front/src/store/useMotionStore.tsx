import { create } from "zustand";

interface BtnState {
  btn: string;
  setBtn: (newBtn: string) => void;
}

interface DomState {
  domSize: DOMRect | null | undefined;
  visibleBtnSize: DOMRect | null | undefined;
  timerBtnSize: DOMRect | null | undefined;
  recordBtnSize: DOMRect | null | undefined;
  saveBtnSize: DOMRect | null | undefined;
  learnBtnSize: DOMRect | null | undefined;
  rsltBtnSize: DOMRect | null | undefined;
  canvasBtnSize: DOMRect | null | undefined;
  setDomSize: (newDomSize: DOMRect | null | undefined) => void;
  setVisibleBtnSize: (newBtnSize: DOMRect | null | undefined) => void;
  setTimeBtnSize: (newBtnSize: DOMRect | null | undefined) => void;
  setRecordBtnSize: (newBtnSize: DOMRect | null | undefined) => void;
  setSaveBtnSize: (newBtnSize: DOMRect | null | undefined) => void;
  setLearnBtnSize: (newBtnSize: DOMRect | null | undefined) => void;
  setRsltBtnSize: (newBtnSize: DOMRect | null | undefined) => void;
  setCanvasBtnSize: (newBtnSize: DOMRect | null | undefined) => void;

  playSize: DOMRect | null | undefined;
  challengeSize: DOMRect | null | undefined;
  repeatSize: DOMRect | null | undefined;
  flipSize: DOMRect | null | undefined;
  speedSize: DOMRect | null | undefined;

  setPlaySize: (newDomSize: DOMRect | null | undefined) => void;
  setChallengeSize: (newDomSize: DOMRect | null | undefined) => void;
  setRepeatSize: (newDomSize: DOMRect | null | undefined) => void;
  setFlipSize: (newDomSize: DOMRect | null | undefined) => void;
  setSpeedSize: (newDomSize: DOMRect | null | undefined) => void;
}

interface ActionState {
  action: string;
  setAction: (newAction: string) => void;
}

export const useBtnStore = create<BtnState>((set) => ({
  btn: "none",
  setBtn: (newBtn: string) => set({ btn: newBtn }),
}));

export const useActionStore = create<ActionState>((set) => ({
  action: "none",
  setAction: (newAction: string) => set({ action: newAction }),
}));

export const useDomStore = create<DomState>((set) => ({
  domSize: null,
  visibleBtnSize: null,
  timerBtnSize: null,
  recordBtnSize: null,
  saveBtnSize: null,
  learnBtnSize: null,
  rsltBtnSize: null,
  canvasBtnSize: null,

  playSize: null,
  challengeSize: null,
  repeatSize: null,
  flipSize: null,
  speedSize: null,

  setDomSize: (newDomSize: DOMRect | null | undefined) => set({ domSize: newDomSize }),
  setVisibleBtnSize: (newBtnSize: DOMRect | null | undefined) =>
    set({ visibleBtnSize: newBtnSize }),
  setTimeBtnSize: (newBtnSize: DOMRect | null | undefined) => set({ timerBtnSize: newBtnSize }),
  setRecordBtnSize: (newBtnSize: DOMRect | null | undefined) => set({ recordBtnSize: newBtnSize }),
  setSaveBtnSize: (newBtnSize: DOMRect | null | undefined) => set({ saveBtnSize: newBtnSize }),
  setLearnBtnSize: (newBtnSize: DOMRect | null | undefined) => set({ learnBtnSize: newBtnSize }),
  setRsltBtnSize: (newBtnSize: DOMRect | null | undefined) => set({ rsltBtnSize: newBtnSize }),
  setCanvasBtnSize: (newBtnSize: DOMRect | null | undefined) => set({ canvasBtnSize: newBtnSize }),

  setPlaySize: (newDomSize: DOMRect | null | undefined) => set({ playSize: newDomSize }),
  setChallengeSize: (newDomSize: DOMRect | null | undefined) => set({ challengeSize: newDomSize }),
  setRepeatSize: (newDomSize: DOMRect | null | undefined) => set({ repeatSize: newDomSize }),
  setFlipSize: (newDomSize: DOMRect | null | undefined) => set({ flipSize: newDomSize }),
  setSpeedSize: (newDomSize: DOMRect | null | undefined) => set({ speedSize: newDomSize }),
}));

interface MotionDetection {
  visibleCount: number;
  timerCount: number;
  recordCount: number;
  learnCount: number;
  resultCount: number;
  playCount: number;
  challengeCount: number;
  repeatCount: number;
  flipCount: number;
  speedCount: number;
  saveCount: number;
  canvasCount: number;

  setVisibleCount: (newVisibleCount: number) => void;
  setTimerCount: (newTimerCount: number) => void;
  setRecordCount: (newRecordCount: number) => void;
  setLearnCount: (newLearnCount: number) => void;
  setResultCount: (newRsltCount: number) => void;
  setPlayCount: (newPlayCount: number) => void;
  setChallengeCount: (newChallengeCount: number) => void;
  setRepeatCount: (newRepeatCount: number) => void;
  setFlipCount: (newFlipCount: number) => void;
  setSpeedCount: (newSpeedCount: number) => void;
  setSaveCount: (newSaveCount: number) => void;
  setCanvasCount: (newCanvasCount: number) => void;
}

export const useMotionDetectionStore = create<MotionDetection>((set) => ({
  visibleCount: 0,
  timerCount: 0,
  recordCount: 0,
  learnCount: 0,
  resultCount: 0,
  playCount: 0,
  challengeCount: 0,
  repeatCount: 0,
  flipCount: 0,
  speedCount: 0,
  saveCount: 0,
  canvasCount: 0,

  setVisibleCount: (newVisibleCount: number) => set({ visibleCount: newVisibleCount }),
  setTimerCount: (newTimerCount: number) => set({ timerCount: newTimerCount }),
  setRecordCount: (newRecordCount: number) => set({ recordCount: newRecordCount }),
  setLearnCount: (newLearnCount: number) => set({ learnCount: newLearnCount }),
  setResultCount: (newResultCount: number) => set({ resultCount: newResultCount }),
  setPlayCount: (newPlayCount: number) => set({ playCount: newPlayCount }),
  setChallengeCount: (newChallengeCount: number) => set({ challengeCount: newChallengeCount }),
  setRepeatCount: (newRepeatCount: number) => set({ repeatCount: newRepeatCount }),
  setFlipCount: (newFlipCount: number) => set({ flipCount: newFlipCount }),
  setSpeedCount: (newSpeedCount: number) => set({ speedCount: newSpeedCount }),
  setSaveCount: (newSaveCount: number) => set({ saveCount: newSaveCount }),
  setCanvasCount: (newCanvasCount: number) => set({ canvasCount: newCanvasCount }),
}));
