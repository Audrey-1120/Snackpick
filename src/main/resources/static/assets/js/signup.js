/******************** 전역변수 **********************/
let isValidId = false; // 아이디 유효성 검사 결과
let isDuplicateChecked = false; // 중복 체크 여부
let lastCheckedId = ""; // 마지막으로 중복 체크한 아이디

let isValidPw = false; // 비밀번호 유효성 검사 결과
let isValidName = false; // 비밀번호 유효성 검사 결과
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

$('.btn-signup').on('click', () => {
    fnSignup();
});

$('.btn-checkId').on('click', () => {
    fnCheckDuplicate($('#id').val());
});


/******************** 함수 **********************/
// 아이디 유효성 검사
const fnValidateId = (id) => {

    let checkIdLabel = $("label[for='id']");

    if(id === '') {
        checkIdLabel.text('아이디는 필수 입력 정보입니다.');
        checkIdLabel.css('color', 'red');
        return;
    }

    // 정규식
    const idRegex = /^(?=.*[a-zA-Z])(?=.*\d)[^\s]{6,12}$/;

    // 정규식 검사
    isValidId = idRegex.test(id);

    if(isValidId === false) {
        checkIdLabel.text('아이디는 숫자와 영문자를 혼용하여 6~12자리로 입력해주세요.');
        checkIdLabel.css('color', 'red');
    } else {
        checkIdLabel.text('');
    }

    // 중복 체크 여부 초기화 - 새로 아이디 작성하면 중복 체크도 다시 해야함.
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

    // 정규식 검사
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
        // 아이디 서버로 보낸다.
        axios.get('/member/checkId', {
            params: {
                id: id
            }
        })
        .then((response) => {

            let isExist = response.data.isExist;
            let checkIdLabel = $("label[for='id']");

            if(isExist === false) { // 아이디 중복 X
                checkIdLabel.text("사용가능한 아이디입니다.");
                checkIdLabel.css("color", "blue");

                isDuplicateChecked = true; // 중복체크 여부 true
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

// 회원가입 최종 체크
const fnSignup = () => {

    let currentId = $('#id').val();
    let pw = $('#password').val();
    let name = $('#name').val();
    let nickname = $('#nickname').val();

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
}
