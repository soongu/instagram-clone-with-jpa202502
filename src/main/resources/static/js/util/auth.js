import { fetchWithAuth } from "./api.js";


// Promise를 캐싱할 변수
let currentUserPromise = null;

// 현재 로그인한 사용자 정보를 요청하기
export async function getCurrentUser() {

  if (currentUserPromise) {
    return currentUserPromise;
  }

  currentUserPromise = (async () => {
    const response = await fetchWithAuth(`/api/profiles/me`);

    if (!response.ok) {
      currentUserPromise = null; // 실패 시 캐시 초기화
      alert('로그인한 사용자 정보를 불러오는 데 실패했습니다.');
      throw new Error('Failed to fetch user');
    }

    return await response.json();
  })();

  return currentUserPromise;
}