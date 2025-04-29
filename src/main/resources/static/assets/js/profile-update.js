
/******************** 전역 변수 **********************/
let initName;
let initNickname;
let fileChanged = false;
let defaultImageUrl = '/assets/img/default-profile.jpg';
let isValidPassword = false;

/******************** 함수 **********************/
// 이미지 사이즈 제한
const fnIsOverSize = (file) => {
    const maxSize = 1024 * 1024 * 5;
    return file.size < maxSize;
}

// 이미지 파일 확장자 확인
const fnIsImage = (file) => {
    const contentType = file.type;
    return contentType.startsWith('image');
}

// 이미지 미리보기
const fnPreview = (fileInput) => {

    let container = $('#image-preview');
    container.empty();

    let file = fileInput.target.files[0];
    let reader = new FileReader();

    reader.onload = (e) => {
        container.html(`<img src="${e.target.result}">`);
    }
    reader.readAsDataURL(file);
}

// 프로필 이미지 체크
const fnCheckProfileImage = (fileInput) => {

    let input = fileInput.target;

    if(!fnIsOverSize(input.files[0])) {
        alert('프로필 이미지는 5MB 이내로 업로드 해주세요.');
        $(input).val('');
        return;
    }

    if(!fnIsImage(input.files[0])) {
        alert('이미지 파일만 첨부 가능합니다.')
        $(input).val('');
        return;
    }
    fnPreview(fileInput);
    fnChangeProfileType();
    fileChanged = true;
}

// 이미지 프로필 타입 변경
const fnChangeProfileType = () => {

    let profileType = $('.profile-type');

    if($('#image-preview img').attr('src') === defaultImageUrl) {
        profileType.val('default');
    } else {
        profileType.val('non-default');
    }
}

// 저장 버튼 활성화
const fnActiveUpdateBtn = () => {

    let currentName = $('.name-input').val().trim();
    let currentNickname = $(".nickname-input").val().trim();
    let isFormChanged = (currentName !== initName) || (currentNickname !== initNickname) || fileChanged;

    $('.btn-save').prop('disabled', !isFormChanged);
}

// 빈값 검사
const fnCheckEmpty = () => {

    let name = $('.name-input');
    let nickname = $('.nickname-input');

    if(name.val().trim() === '') {
        alert('이름을 작성해주세요.');
        name.focus();
        return false;
    }

    if(nickname.val().trim() === '') {
        alert('닉네임을 작성해주세요.');
        nickname.focus();
        return false;
    }
    return true;
}

// 최종 점검
const fnFinalCheck = () => {
    if(!fnCheckEmpty()) {
        return;
    }

    $('.name-input').val((i, value) => {
        return value.trim();
    });

    $('.nickname-input').val((i, value) => {
        return value.trim();
    });

    fnUpdateProfile();
}

// csrf 토큰 가져오기
const fnGetCsrfToken = () => {
    return $('meta[name="csrf-token"]').attr('content');
}

// 서버로 수정할 회원 데이터 전달
const fnUpdateProfile = () => {

    let updateForm = $('#update-form')[0];
    let formData = new FormData(updateForm);

    formData.append('fileChanged', fileChanged);
    formData.append('profileType', $('.profile-type').val());

    axios.put('/member/updateProfile', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
            'X-XSRF-TOKEN': fnGetCsrfToken()
        }
    })
    .then((response) => {
        if(response.data.success) {
            alert(response.data.message);
            window.location.href = response.data.redirectUrl;
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    });
}

// 새 비밀번호 유효성 검사
const fnValidatePassword = () => {

    let passwordLabel = $('.new-password-info');
    let password = $('#new-password');

    const pwRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[\W_])[^\s]{8,16}$/;

    isValidPw = pwRegex.test(password.val());

    if(!isValidPw) {
        isValidPassword = false;
        passwordLabel.css('display', '');
    } else {
        isValidPassword = true;
        passwordLabel.css('display', 'none');
    }
}

// 비밀번호란 공백 및 유효성 검사
const fnCheckResetPassword = () => {

    let currentPassword = $('#current-password');
    let newPassword = $('#new-password');
    let newPasswordCheck = $('#new-password-check');

    if(currentPassword.val().trim() === '') {
        alert('현재 비밀번호를 입력해주세요.');
        currentPassword.focus();
        return false;
    }

    if(newPassword.val().trim() === '') {
        alert('새 비밀번호를 입력해주세요.');
        newPassword.focus();
        return false;
    }

    if(newPasswordCheck.val().trim() === '') {
        alert('새 비밀번호를 한번 더 입력해주세요.');
        newPasswordCheck.focus();
        return false;
    }

    if(!isValidPassword) {
        alert('새 비밀번호를 다시 확인해주세요.');
        return false;
    }

    if(newPassword.val().trim() !== newPasswordCheck.val().trim()) {
        alert('새 비밀번호와 새 비밀번호 확인란이 일치하지 않습니다.');
        newPasswordCheck.focus();
        return false;
    }

    return true;
}

// 비밀번호 재설정
const fnResetPassword = () => {

    if(!fnCheckResetPassword()) {
        return;
    }

    axios.put('/member/resetPassword', {
        password: $('#current-password').val().trim(),
        newPassword: $('#new-password').val().trim()
    })
    .then((response) => {

        let data = response.data;

        if(data.success) {
           alert(data.message);
           window.location.href=data.redirectUrl;
        } else {
            alert(data.message);
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    });
}

const fnDebounce = (fn, delay)  => {
    let timer;
    return function(...args) {
        clearTimeout(timer);
        timer = setTimeout(() => {
            fn.apply(this, args);
        }, delay);
    };
}

/******************** 이벤트 **********************/
$(document).ready(() => {
    initName = $('.name-input').val().trim();
    initNickname = $('.nickname-input').val().trim();

    fnChangeProfileType();
});

$('#update-form input').on('keyup', fnDebounce(fnActiveUpdateBtn, 200));

$('.profile-image').on('change', (evt) => {
    fnCheckProfileImage(evt);
    fnActiveUpdateBtn();
});

$('.btn-deleteImage').on('click', () => {

    $('.profile-image-form img').attr('src', defaultImageUrl);
    fileChanged = true;

    fnChangeProfileType();
    fnActiveUpdateBtn();
});

$('#update-form').on('submit', (evt) => {
    evt.preventDefault();
    fnFinalCheck();
})

$('.btn-undo').on('click', () => {
    window.history.back();
});

$('.btn-reset-password').on('click', () => {
    fnResetPassword();
});

$('#new-password').on('keyup', fnDebounce(fnValidatePassword, 500));

$('#reset-password').on('hidden.bs.modal', () => {
    $('.reset-password-modal input').val('');
    $('.new-password-info').css('display', 'none');
});