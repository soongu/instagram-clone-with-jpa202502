import { getCurrentUser } from '../util/auth.js';
import { onProfileImageUpdate } from '../util/profile-image-updater.js';

// 사이드바의 프로필 영역 처리
async function renderSideBarProfile() {
  // 로그인한 유저 정보 가져와야 함.
  const { username, profileImage } = await getCurrentUser();

  const $profileItem = document.querySelector('.menu-item[href="/profile"]');
  // 프로필 사진 렌더링
  const $profileImg = $profileItem.querySelector('.profile-image img');
  $profileImg.src = profileImage || '/images/default-profile.svg';
  $profileImg.alt = `${username}님의 프로필 사진`;

  // 프로필 링크 걸어야 함
  $profileItem.setAttribute('href', `/${username}`);
}

function initSideBar() {
  renderSideBarProfile();

  // 프로필 사진 업데이트 이벤트 리스너 등록
  onProfileImageUpdate(({ imageUrl, username }) => {
    const $profileImg = document.querySelector('.sidebar .profile-image img');
    if ($profileImg) {
      $profileImg.src = imageUrl;
      $profileImg.alt = `${username}님의 프로필 사진`;
    }
  });
}

export default initSideBar;
