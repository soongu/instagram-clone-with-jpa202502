import { getCurrentUser } from './auth.js';

// 프로필 사진을 전역적으로 업데이트하는 유틸리티 함수

/**
 * 모든 프로필 사진을 업데이트하는 함수
 * @param {string} imageUrl - 업데이트할 프로필 이미지 URL
 */
export async function updateAllProfileImages(imageUrl) {
  // 현재 사용자 정보 가져오기
  const currentUser = await getCurrentUser();
  if (!currentUser) return;

  // 프로필 페이지의 프로필 사진 업데이트
  const $profilePageImg = document.querySelector(
    '.profile-image-container .profile-image img'
  );
  if ($profilePageImg) {
    $profilePageImg.src = imageUrl;
  }

  // 사이드바의 프로필 사진 업데이트
  const $sidebarProfileImg = document.querySelector(
    '.sidebar .profile-image img'
  );
  if ($sidebarProfileImg) {
    $sidebarProfileImg.src = imageUrl;
  }

  // 사용자 추천 영역의 프로필 사진 업데이트
  const $suggestionsProfileImg = document.querySelector(
    '.current-user .profile-image img'
  );
  if ($suggestionsProfileImg) {
    $suggestionsProfileImg.src = imageUrl;
  }

  // 피드의 사용자 프로필 사진 업데이트 (현재 사용자가 작성한 게시물)
  const $feedProfileImgs = document.querySelectorAll(
    '.post .post-profile-image img'
  );
  $feedProfileImgs.forEach((img) => {
    // 현재 사용자의 프로필 사진인지 확인 (사용자명으로 판단)
    const username = img
      .closest('.post')
      ?.querySelector('.post-username')?.textContent;
    if (username && username === currentUser.username) {
      img.src = imageUrl;
    }
  });

  // 커스텀 이벤트 발생 (다른 컴포넌트에서 프로필 사진 업데이트를 감지할 수 있도록)
  window.dispatchEvent(
    new CustomEvent('profileImageUpdated', {
      detail: { imageUrl, username: currentUser.username },
    })
  );
}

/**
 * 프로필 사진 업데이트 이벤트 리스너를 등록하는 함수
 * @param {Function} callback - 프로필 사진 업데이트 시 실행할 콜백 함수
 */
export function onProfileImageUpdate(callback) {
  window.addEventListener('profileImageUpdated', (event) => {
    callback(event.detail);
  });
}
