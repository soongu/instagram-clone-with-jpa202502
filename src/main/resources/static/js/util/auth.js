import { fetchWithAuth } from "./api.js";


// Promise를 캐싱할 변수
let currentUserPromise = null;

// 현재 로그인한 사용자 정보를 요청하기
export async function getCurrentUser() {

  if (currentUserPromise) {
    return currentUserPromise;
  }

  currentUserPromise = (async () => {
    try {
      return await fetchWithAuth(`/api/profiles/me`);
    } catch (e) {
      currentUserPromise = null; // 실패 시 캐시 초기화
      console.error('Failed to fetch user:', e);
      return null;
    }
  })();

  return currentUserPromise;
}