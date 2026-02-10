// js/util/api.js



// 인증 헤더를 생성하는 함수
function createAuthHeader() {
  // 액세스 토큰을 브라우저에서 가져오기
  const token = localStorage.getItem('accessToken');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

// 공통 fetch 함수 (ApiResponse 처리 포함)
export async function fetchWithAuth(url, options = {}) {
  // 기본 헤더에 인증 헤더 추가
  const headers = {
    'Content-Type': 'application/json', // 기본적으로 JSON 통신이라 가정
    ...options.headers,
    ...createAuthHeader(),
  };

  try {
    const response = await fetch(url, {
      ...options,
      headers,
    });

    // 401 Unauthorized 처리
    if (response.status === 401 && options.handleUnauthorized !== false) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      window.location.href = '/';
      return; // 리다이렉트 후 종료
    }

    // 응답 파싱
    const json = await response.json();

    // ApiResponse 규격 처리
    // success가 true이면 data를 반환
    if (json.success) {
      return json.data;
    } 
    
    // success가 false이면 에러를 던짐 (ErrorResponse 객체 포함)
    throw new Error(json.error?.message || '알 수 없는 에러가 발생했습니다.');

  } catch (e) {
    // 네트워크 에러이거나, 위에서 던진 에러일 수 있음
    console.error('API Request Failed:', e);
    throw e; // 호출자에게 에러 전파
  }
}


// 서버에 팔로우 토글 요청을 보내기
export async function toggleFollow(targetUsername) {
  // fetchWithAuth가 이미 data를 반환하므로 바로 리턴
  return await fetchWithAuth(`/api/follows/${targetUsername}`, {
    method: 'POST',
  });
}
