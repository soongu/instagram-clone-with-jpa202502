
import { ValidationRules, checkPasswordStrength } from "./validation.js";
import { debounce } from '../util/debounce.js';
import { fetchWithAuth } from "../util/api.js";

// 모든 input별로 이전 값을 저장할 객체를 만듦
const previousValues = {
  emailOrPhone: '',
  name: '',
  username: '',
  password: ''
};

// 회원 가입정보를 서버에 전송하기
async function fetchToSignUp(userData) {

  try {
    const data = await fetchWithAuth('/api/auth/signup', {
      method: 'POST',
      body: JSON.stringify(userData)
    });

    // 성공 시 처리
    window.location.href = '/'; // 로그인 페이지 이동 (또는 메인)

  } catch (e) {
    alert(e.message);
  }
}




// 초기화 함수
function initSignUp() {
  // form submit이벤트
  const $form = document.querySelector('.auth-form');

  // 초기에 가입 버튼 비활성화
  const $submitButton = $form.querySelector('.auth-button');
  $submitButton.disabled = true;

  // 입력 태그들을 읽어서 객체로 관리
  const $inputs = {
    emailOrPhone: $form.querySelector('input[name="email"]'),
    name: $form.querySelector('input[name="name"]'),
    username: $form.querySelector('input[name="username"]'),
    password: $form.querySelector('input[name="password"]'),
  };

  // 비밀번호 숨기기 토글 활성화
  createPasswordToggle($inputs.password);

  // 디바운스가 걸린 validateField 함수
  const debouncedValidate = debounce(async ($input) => {
    // === bug fix part ===
    /*
      원인: validateField는 비동기(async)로 작동함 
            따라서 await을 걸지 않으면 아래쪽 함수 updateSubmitButton과 동시에 작동되어
            버그가 발생함
      해결 방안: validateField에 await을 걸어 실행이 끝날때까지 updateSubmitButton이
             호출되지 않고 대기하도록 만들어줌
    */
    await validateField($input); // 가입버튼 활성화코드는 이 코드 이후에 실행해야 함
    updateSubmitButton($inputs, $submitButton); // 가입 버튼 활성화/비활성화 처리
  }, 700);

  // input 이벤트 핸들러
  const handleInput = ($input) => {
    removeErrorMessage($input.closest('.form-field'));

    // 디바운스 + 비동기 검증
    debouncedValidate($input);
  };

  /* 
    중복 호출 방지 로직:
    사용자가 입력을 마치고 포커스를 즉시 옮기면 (blur), 
    handleBlur에서 즉시 검증을 수행하고, 
    기존에 대기중이던 debouncedValidate는 취소해야 함.
    
    하지만 현재 debounce 구현상 취소 메서드가 없다면, 
    handleBlur에서는 '값이 변경되었을 때' 검증을 수행하되, 
    이미 debouncedValidate가 실행 예정이라면 그것을 믿거나,
    아니면 단순히 blur에서는 검증을 스킵하고 debounce에 맡길 수도 있음.
    
    사용자 경험상 blur시 즉시 검증이 좋으므로, 
    debounce 구현을 수정하거나, 여기서 플래그를 사용할 수 있음.

    간단한 해결책: 
    입력 중(input)에는 debounce로 검증하고, 
    blur 발생 시에는 "마지막 입력 후 충분한 시간이 지났는지" 따지지 않고 무조건 즉시 검증하되,
    만약 blur에서 검증을 했다면 pending된 debounce는 무시되어야 함.
    
    여기서는 debouncedValidate에 .cancel()이 없으므로,
    handleBlur에서는 validateField를 직접 호출하지 않고,
    debouncedValidate를 '즉시 실행' 하도록 유도하거나,
    단순히 blur 이벤트에서는 검증을 생략하고 디바운스에 맡기는 방법도 있음.
    
    하지만 "blur에서도 한번 더 터져서"라는 불만을 해결하려면,
    blur 이벤트 핸들러의 내용을 수정해야 함.
  */
  
  const handleBlur = $input => { 
    // blur 시점에는 UI 정리만 수행하거나, 
    // 만약 디바운스 타이머가 돌고 있다면 취소하고 즉시 실행...은 debounce 수정 필요.
    
    // 가장 쉬운 수정: blur에서는 중복검사를 하지 않는다. 
    // 어차피 input 이벤트에서 디바운스로 처리될 것이기 때문.
    // 다만, 사용자가 클릭해서 들어왔다가 아무것도 안하고 나가는 경우 등은 체크할 필요 없음.
    // 사용자가 입력하고 바로 나가는 경우 -> input이벤트가 이미 발생했으므로 디바운스 큐에 들어감.
    // 따라서 blur에서는 API 호출을 유발하는 검증을 제거하거나, 
    // 빈 값 체크 정도만 수행.
    
    // 수정안: blur에서는 API 중복체크를 제외한 동기적 검증(길이, 패턴 등)만 하거나
    // 그냥 input 이벤트의 디바운스에 전적으로 맡김.
    
    // 여기서는 blur 이벤트를 제거하거나, UI적인 처리(에러메시지 제거 등)만 남김.
    // 사용자가 탭키로 빠르게 이동할 때 검증이 조금 늦게(0.7초후) 뜨더라도 
    // 중복 호출을 막는게 우선이라면 blur 검증을 제거하는게 맞음.
    
    const fieldName = $input.name;
    const currentValue = $input.value.trim();
    
    if (previousValues[fieldName] !== currentValue) {
      previousValues[fieldName] = currentValue;
    }
  };

  // 4개의 입력창에 입력 이벤트 바인딩
  Object.values($inputs).forEach(($input) => {
    $input.addEventListener('input', () => handleInput($input));
    $input.addEventListener('blur', () => handleBlur($input));
  });

  // 폼 이벤트 핸들러 바인딩
  $form.addEventListener('submit', (e) => {
    e.preventDefault(); // 폼 전송시 발생하는 새로고침 방지

    const { emailOrPhone, name, username, password } = $inputs;

    // 핸드폰 번호일 경우 하이픈 제거 (중복체크 로직과 일치시킴)
    let emailOrPhoneValue = emailOrPhone.value.trim();
    if (!emailOrPhoneValue.includes('@')) {
      emailOrPhoneValue = emailOrPhoneValue.replace(/[^0-9]/g, '');
    }

    const payload = {
      emailOrPhone: emailOrPhoneValue,
      name: name.value,
      username: username.value,
      password: password.value,
    };

    // 서버로 데이터 전송
    fetchToSignUp(payload);
  });
}

// ==== 함수 정의 ==== //
// 입력값을 검증하고 에러메시지를 렌더링하는 함수
async function validateField($input) {

  // 각 입력들이 유효한지 확인
  let isValid = true;

  // 이게 어떤태그인지 알아오기
  const fieldName = $input.name;
  // 입력값 읽어오기
  const inputValue = $input.value.trim();
  // input의 부모 가져오기
  const $formField = $input.closest('.form-field');
  
  // 1. 빈 값 체크
  if (!inputValue) {
    isValid = false;
    // console.log(fieldName, ' is empty!');
    showError($formField, ValidationRules[fieldName]?.requiredMessage); // 에러메시지 렌더링
  } else {
    // 2. 상세 체크 (패턴검증, 중복검증)
    // 2-1. 이메일, 전화번호 검증
    if (fieldName === 'email') {
      isValid = await validateEmailOrPhone($formField, inputValue);
    } else if (fieldName === 'password') {
      isValid = validatePassword($formField, inputValue);
    } else if (fieldName === 'username') {
      isValid = await validateUsername($formField, inputValue);
    } else if (fieldName === 'name') {
      showSuccess($formField, "확인되었습니다.");
    } 
  }

  // 각 input에 검사결과를 저장
  $input.dataset.isValid = isValid.toString();

}

/**
 * 에러 메시지를 표시하고, 필드에 error 클래스를 부여
 */
function showError($formField, message) {
  $formField.classList.add('error');
  $formField.classList.remove('success'); // 성공 클래스 제거
  const $errorSpan = document.createElement('span');
  $errorSpan.classList.add('error-message');
  $errorSpan.textContent = message;
  $formField.append($errorSpan);
}

/**
 * 성공 메시지를 표시하고, 필드에 success 클래스를 부여
 */
function showSuccess($formField, message) {
  $formField.classList.remove('error'); // 에러 클래스 제거
  $formField.classList.add('success');
  
  if (message) {
    const $successSpan = document.createElement('span');
    $successSpan.classList.add('success-message');
    $successSpan.textContent = message;
    $formField.append($successSpan);
  }
}

/**
 * 에러 및 성공 피드백, 비밀번호 피드백을 제거한다.
 */
function removeErrorMessage($formField) {
  $formField.classList.remove('error');
  $formField.classList.remove('success');
  const error = $formField.querySelector('.error-message');
  const success = $formField.querySelector('.success-message');
  const feedback = $formField.querySelector('.password-feedback');
  if (error) error.remove();
  if (success) success.remove();
  if (feedback) feedback.remove();
}

// 서버에 중복체크 API 요청을 보내고 결과를 반환
async function fetchToCheckDuplicate(type, value) {
  return await fetchWithAuth(`/api/auth/check-duplicate?type=${type}&value=${value}`);
}

// 이메일 또는 전화번호를 상세검증
async function validateEmailOrPhone($formField, inputValue) {

  // 이메일 체크
  if (inputValue.includes('@')) {
    if (!ValidationRules.email.pattern.test(inputValue)) { // 패턴 체크
      showError($formField, ValidationRules.email.message);
      return false;
    } else { // 서버에 통신해서 중복체크
      const data = await fetchToCheckDuplicate('email', inputValue);
      if (!data.available) {
        showError($formField, data.message);
        return false;
      } else {
        showSuccess($formField, data.message);
      }
    }
  } else {
    // 전화번호  체크
    // 전화번호 처리(숫자만 추출)
    const numbers = inputValue.replace(/[^0-9]/g, '');
    if (!ValidationRules.phone.pattern.test(numbers)) {
      // 패턴 체크
      showError($formField, ValidationRules.phone.message);
      return false;
    } else {
      // 서버에 통신해서 중복체크
      const data = await fetchToCheckDuplicate('phone', numbers);
      if (!data.available) {
        showError($formField, data.message);
        return false;
      } else {
        showSuccess($formField, data.message);
      }
    }
  }
  return true;

}

// 비밀번호 검증 (길이, 강도체크)
function validatePassword($formField, inputValue) {
  // 길이 확인
  if (!ValidationRules.password.patterns.length.test(inputValue)) {
    showError($formField, ValidationRules.password.messages.length);
    return false;
  }

  // 강도 체크
  const strength = checkPasswordStrength(inputValue);
  switch (strength) {
    case 'weak': // 에러로 볼것임
      showError($formField, ValidationRules.password.messages.weak);
      return false;
    case 'medium': // 에러는 아님
      showPasswordFeedback(
        $formField,
        ValidationRules.password.messages.medium,
        'warning'
      );
      return true;
    case 'strong': // 에러는 아님
      showPasswordFeedback(
        $formField,
        ValidationRules.password.messages.strong,
        'success'
      );
      return true;
  }

}

/**
 * 비밀번호 강도 피드백 표시
 */
function showPasswordFeedback($formField, message, type) {
  const $feedback = document.createElement('span');
  $feedback.className = `password-feedback ${type}`;
  $feedback.textContent = message;
  $formField.append($feedback);
}

/**
 * 사용자 이름(username) 필드 검증
 */
async function validateUsername($formField, inputValue) {

  if (!ValidationRules.username.pattern.test(inputValue)) {
    showError($formField, ValidationRules.username.message);
    return false;
  }

  // 중복검사
  const data = await fetchToCheckDuplicate('username', inputValue);
  if (!data.available) {
    showError($formField, data.message);
    return false;
  } else {
    showSuccess($formField, data.message);
  }
  return true;
}

/**
 * 비밀번호 표시/숨기기 토글 기능 생성
 */
function createPasswordToggle(passwordInput) {

  const $toggle = document.querySelector('.password-toggle');

  passwordInput.addEventListener('input', (e) => {
    $toggle.style.display = e.target.value.length > 0 ? 'block' : 'none';
  });

  $toggle.addEventListener('click', () => {
    const isCurrentlyPassword = passwordInput.type === 'password';
    passwordInput.type = isCurrentlyPassword ? 'text' : 'password';
    $toggle.textContent = isCurrentlyPassword ? '숨기기' : '패스워드 표시';
  });
}

/**
 * 모든 필드의 유효성 상태를 확인해, 회원가입 버튼 활성/비활성 제어
 */
function updateSubmitButton($inputs, $submitButton) {
  const allFieldsValid = Object.values($inputs).every((input) => {
    return input.value.trim() !== '' && input.dataset.isValid === 'true';
  });
  console.log('all: ', allFieldsValid);

  $submitButton.disabled = !allFieldsValid;
}


//====== 메인 실행 코드 ======//
document.addEventListener('DOMContentLoaded', initSignUp);
