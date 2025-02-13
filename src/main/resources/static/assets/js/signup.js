/******************** 전역변수 **********************/
let isValidId = false; // 아이디 유효성 검사 결과
let isDuplicateChecked = false; // 중복 체크 여부
let lastCheckedId = ""; // 마지막으로 중복 체크한 아이디

let isValidPw = false; // 비밀번호 유효성 검사 결과
let isValidName = false; // 이름 유효성 검사 결과
let isValidNickname = false; // 닉네임 유효성 검사 결과


/******************** 이벤트 **********************/
$('#id').on('keyup', () => {
    fnValidateId($('#id').val());
});

$('#password').on('keyup', () => {
    fnValidatePw($('#password').val());
});

$('#name').on('keyup', () => {
    fnValidateName($('#name').val());
});

$('#nickname').on('keyup', () => {
    fnValidateNickname($('#nickname').val());
});

$('.btn-signup').on('click', (evt) => {
    fnSignupFinalCheck(evt);
})

$('.btn-checkId').on('click', () => {
    fnCheckDuplicate($('#id').val());
});

// 프로필 사진 미리보기
$('#profile_image').on('change', (evt) => {
    fnCheckProfileImage(evt);
})


/******************** 함수 **********************/
// 아이디 유효성 검사
const fnValidateId = (id) => {

    let checkIdLabel = $("label[for='id']");

    if(id === '') {
        checkIdLabel.text('아이디는 필수 입력 정보입니다.');
        checkIdLabel.css('color', 'red');
        return;
    }

    const idRegex = /^(?=.*[a-zA-Z])(?=.*\d)[^\s]{6,12}$/;

    // 정규식 검사
    isValidId = idRegex.test(id);

    if(isValidId === false) {
        checkIdLabel.text('아이디는 숫자와 영문자를 혼용하여 6~12자리로 입력해주세요.');
        checkIdLabel.css('color', 'red');
    } else {
        checkIdLabel.text('');
    }

    // 아이디 중복 체크 여부 초기화
    isDuplicateChecked = false;
}

// 비밀번호 유효성 검사
const fnValidatePw = (pw) => {

    let checkPwLabel = $("label[for='password']");

    if(pw === '') {
        checkPwLabel.text('비밀번호는 필수 입력 정보입니다.');
        checkPwLabel.css('color', 'red');
        return;
    }

    const pwRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[\W_])[^\s]{8,16}$/;

    isValidPw = pwRegex.test(pw);

    if(isValidPw === false) {
        checkPwLabel.text('비밀번호는 숫자와 영문자, 특수문자를 혼용하여 8~16자리로 입력해주세요.');
        checkPwLabel.css('color', 'red');
    } else {
        checkPwLabel.text('');
    }
}

// 닉네임 유효성 검사
const fnValidateNickname = (nickname) => {

    let checkNicknameLabel = $("label[for='nickname']");

    if(nickname === '') {
        checkNicknameLabel.text('닉네임은 필수 입력 정보입니다.');
        checkNicknameLabel.css('color', 'red');
        isValidNickname = false;
        return;
    }

    isValidNickname = nickname.length < 20 && nickname !== '';

    if(!isValidNickname) {
        checkNicknameLabel.text('닉네임은 20자 이내로 입력해주세요.');
        checkNicknameLabel.css('color', 'red');
    } else {
        checkNicknameLabel.text('');
    }
}

// 이름 유효성 검사
const fnValidateName = (name) => {

    let checkNameLabel = $("label[for='name']");

    if(name === '') {
        checkNameLabel.text('이름은 필수 입력 정보입니다.');
        checkNameLabel.css('color', 'red');
        isValidName = false;
        return;
    }

    isValidName = name.length < 20 && name !== '';

    if(isValidName === false) {
        checkNameLabel.text('이름은 20자 이내로 입력해주세요.');
        checkNameLabel.css('color', 'red');
    } else {
        checkNameLabel.text('');
    }
}


// 아이디 중복 체크
const fnCheckDuplicate = (id) => {

    if(!isValidId) { // 유효성 검사 통과한 아이디인가?
        alert("아이디가 유효하지 않습니다.");
        $('#id').focus();
        return;
    }

    if(id !== '') {
        axios.get('/member/checkId', {
            params: {
                id: id
            }
        })
        .then((response) => {

            let isExist = response.data.isExist;
            let checkIdLabel = $("label[for='id']");

            if(isExist === false) {
                checkIdLabel.text("사용가능한 아이디입니다.");
                checkIdLabel.css("color", "blue");

                isDuplicateChecked = true; // 중복체크 여부 갱신
                lastCheckedId = id; // 마지막으로 중복검사한 아이디 갱신

            } else {
                checkIdLabel.text("중복된 아이디입니다.");
                checkIdLabel.css("color", "red");
            }
        })
        .catch((error) => {
            console.log(error);
        });

    } else {
        return;
    }
}

// 프로필 사이즈 제한
const fnIsOverSize = (file) => {
    const maxSize = 1024 * 1024 * 5; // 5MB 사이즈 제한
    return file.size < maxSize;
}

// 이미지 파일 확장자 확인
const fnIsImage = (file) => {
    const contentType = file.type;
    return contentType.startsWith('image');
}

// 이미지 미리보기
const fnPreview = (fileInput) => {

    let file = fileInput.target.files[0];
    let reader = new FileReader();

    reader.onload = (e) => {
        $('#image-preview').html(`<img src="${e.target.result}">`);
    }
    reader.readAsDataURL(file);
}

// 프로필 이미지 체크
const fnCheckProfileImage = (fileInput) => {

    let checkFileLabel = $('#image-preview + p');
    let input = fileInput.target;

    // 사이즈 체크
    if(!fnIsOverSize(input.files[0])) {
        checkFileLabel.text('프로필 이미지는 5MB 이내로 업로드 해주세요.');
        checkFileLabel.css('color', 'red');
        $(input).val('');
        return;
    }

    // 이미지 확장자 체크
    if(!fnIsImage(input.files[0])) {
        checkFileLabel.text('이미지 파일만 첨부 가능합니다.');
        checkFileLabel.css('color', 'red');
        $(input).val('');
        return;
    }
    fnPreview(fileInput);
}

// 회원가입 최종 체크
const fnSignupFinalCheck = () => {

    let currentId = $('#id').val();

    let checkIdLabel = $("label[for='id']");
    let checkPwLabel = $("label[for='password']");
    let checkNicknameLabel = $("label[for='nickname']");
    let checkNameLabel = $("label[for='name']");

    // 유효성 검사 통과했는가?
    if(!isValidId) {
        checkIdLabel.text('아이디가 유효하지 않습니다.');
        checkIdLabel.css('color', 'red');
        $('#id').focus();
        return;
    }

    // 아이디 중복 검사 통과하고 현재 아이디가 마지막으로 중복검사한 아이디와 같은가?
    if(!isDuplicateChecked || lastCheckedId !== currentId) {
        checkIdLabel.text('아이디 중복 확인을 해주세요.');
        checkIdLabel.css('color', 'red');
        $('#id').focus();
        return;
    }

    if(!isValidPw) {
        checkPwLabel.text('비밀번호를 다시 확인해주세요.');
        checkPwLabel.css('color', 'red');
        $('#password').focus();
        return;
    }

    if(!isValidName) {
        checkNameLabel.text('이름을 다시 확인해주세요');
        checkNameLabel.css('color', 'red');
        $('#name').focus();
        return;
    }

    if(!isValidNickname) {
        checkNicknameLabel.text('닉네임을 다시 확인해주세요.');
        checkNicknameLabel.css('color', 'red');
        $('#nickname').focus();
        return;
    }

    fnSignup();
}

const fnSignup = () => {

    let signupForm = $('#signup-form')[0];
    let formData = new FormData(signupForm);

    axios.post('/member/signup', formData)
        .then((response) => {
            if(response.data.success) {
                alert(response.data.message);
                window.location.href = response.data.redirectUrl;
            } else {
                alert(response.data.message);
            }
        }).catch((error) => {
        alert("회원가입 중 오류가 발생했습니다.");
    })
}
