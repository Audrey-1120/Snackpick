/******************** 전역변수 **********************/
let isValidId = false;
let isDuplicateChecked = false;
let lastCheckedId = "";

let isValidPw = false;
let isValidName = false;
let isValidNickname = false;

/******************** 함수 **********************/

const fnValidateId = () => {

    let id = $('#id').val();
    let checkIdLabel = $("label[for='id']");

    if(id === '') {
        checkIdLabel.text('아이디는 필수 입력 정보입니다.');
        checkIdLabel.css('color', 'red');
        return;
    }

    const idRegex = /^(?=.*[a-zA-Z])(?=.*\d)[^\s]{6,12}$/;

    isValidId = idRegex.test(id);

    if(isValidId === false) {
        checkIdLabel.text('아이디는 숫자와 영문자를 혼용하여 6~12자리로 입력해주세요.');
        checkIdLabel.css('color', 'red');
    } else {
        checkIdLabel.text('');
    }

    isDuplicateChecked = false;
}

const fnValidatePw = () => {

    let pw = $('#password').val();
    let checkPwLabel = $("label[for='password']");

    if(pw === '') {
        checkPwLabel.text('비밀번호는 필수 입력 정보입니다.');
        checkPwLabel.css('color', 'red');
        return;
    }

    const pwRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[\W_])[^\s]{8,16}$/;

    isValidPw = pwRegex.test(pw);

    if(!isValidPw) {
        checkPwLabel.text('비밀번호는 숫자와 영문자, 특수문자를 혼용하여 8~16자리로 입력해주세요.');
        checkPwLabel.css('color', 'red');
    } else {
        checkPwLabel.text('');
    }
}

const fnValidateNickname = () => {

    let nickname = $('#nickname').val().trim();
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

const fnValidateName = () => {

    let name = $('#name').val().trim();
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


const fnCheckDuplicate = (id) => {

    if(!isValidId) {
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

                isDuplicateChecked = true;
                lastCheckedId = id;

            } else {
                checkIdLabel.text("중복된 아이디입니다.");
                checkIdLabel.css("color", "red");
            }
        })
        .catch((error) => {
            alert(error.response.data.message);
        });

    }
}

const fnIsOverSize = (file) => {
    const maxSize = 1024 * 1024 * 5;
    return file.size < maxSize;
}

const fnIsImage = (file) => {
    const contentType = file.type;
    return contentType.startsWith('image');
}

const fnPreview = (fileInput) => {

    let file = fileInput.target.files[0];
    let reader = new FileReader();

    reader.onload = (e) => {
        $('#image-preview').html(`<img src="${e.target.result}">`);
    }
    reader.readAsDataURL(file);
}

const fnCheckProfileImage = (fileInput) => {

    let checkFileLabel = $('#image-preview + p');
    let input = fileInput.target;

    if(!fnIsOverSize(input.files[0])) {
        checkFileLabel.text('프로필 이미지는 5MB 이내로 업로드 해주세요.');
        checkFileLabel.css('color', 'red');
        $(input).val('');
        return;
    }

    if(!fnIsImage(input.files[0])) {
        checkFileLabel.text('이미지 파일만 첨부 가능합니다.');
        checkFileLabel.css('color', 'red');
        $(input).val('');
        return;
    }
    fnPreview(fileInput);
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

const fnSignupFinalCheck = () => {

    let currentId = $('#id');
    let checkIdLabel = $("label[for='id']");
    let checkPwLabel = $("label[for='password']");
    let checkNicknameLabel = $("label[for='nickname']");
    let checkNameLabel = $("label[for='name']");

    if(!isValidId) {
        checkIdLabel.text('아이디가 유효하지 않습니다.');
        checkIdLabel.css('color', 'red');
        currentId.focus();
        return;
    }

    if(!isDuplicateChecked || lastCheckedId !== currentId.val()) {
        checkIdLabel.text('아이디 중복 확인을 해주세요.');
        checkIdLabel.css('color', 'red');
        currentId.focus();
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

const fnGetCsrfToken = () => {
    return $('meta[name="csrf-token"]').attr('content');
}

const fnSignup = () => {

    let signupForm = $('#signup-form')[0];
    let formData = new FormData(signupForm);

    axios.post('/member/signup', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
            'X-XSRF-TOKEN': fnGetCsrfToken()
        }
    })
    .then((response) => {
        if(response.data.success) {
            alert(response.data.message);
            window.location.href = response.data.redirectUrl;
        } else {
            alert(response.data.message);
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    });
}

/******************** 이벤트 **********************/
$('#id').on('keyup', fnDebounce(fnValidateId, 300));
$('#password').on('keyup', fnDebounce(fnValidatePw, 300));
$('#name').on('keyup', fnDebounce(fnValidateName, 300));
$('#nickname').on('keyup', fnDebounce(fnValidateNickname, 300));

$('.btn-signup').on('click', (evt) => {
    fnSignupFinalCheck(evt);
});

$('.btn-checkId').on('click', () => {
    fnCheckDuplicate($('#id').val());
});

$('#profileImage').on('change', (evt) => {
    fnCheckProfileImage(evt);
});

$('.btn-delete').on('click', () => {
    $('#profileImage').val('');
    $('#image-preview').empty();
});