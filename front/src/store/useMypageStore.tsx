import { create } from "zustand";
import { Countings } from "../constants/types";

interface MyPageStore {
  countings: Countings;
  setCountings: (data: Countings) => void;
}

const useMypageStore = create<MyPageStore>((set) => ({
  countings: { triedShortsNum: 0, recordedShortsNum: 0, uploadedShortsNum: 0 },
  setCountings: (data: Countings) => set({ countings: data }),
}));
export default useMypageStore;
