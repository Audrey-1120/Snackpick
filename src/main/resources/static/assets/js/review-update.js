import * as kakaoMaps from './kakao-maps.js';

/******************** 전역 변수 **********************/
let productId = 0;
let reviewId = 0;

let initForm;
let initRepresentIndex = 0;
let initRatingTaste = 0;
let initRatingPrice = 0;

let fileChanged = false;
let representImageId = 0;
let deleteAllImageList = false;

let selectedRating = {}; // 별점 그룹별로 선택된 값 저장

let ratingTaste = $('#taste-rating').data('ratingTaste');
let ratingPrice = $('#price-rating').data('ratingPrice');

/******************** 함수 **********************/

// 별점 고정
function fnSetRating(event) {

    let starsGroup = $(event.currentTarget).closest('.stars');
    let index = $(event.currentTarget).data('index');
    let halfPoint = $(event.currentTarget).offset().left + $(event.currentTarget).width() / 2;
    let isHalf = event.pageX < halfPoint;

    selectedRating[starsGroup.attr('id')] = { score: index, isHalf: isHalf };
    fnUpdateStars(starsGroup, index, isHalf);
}

// 별점 업데이트
function fnUpdateStars(starsGroup, score, isHalf) {

    starsGroup.find('i').each(function () {
        let starIndex = $(this).data('index');

        if (starIndex < score) {
            $(this).removeClass("bi-star bi-star-half").addClass("bi-star-fill");
        } else if (starIndex === score) {
            if (isHalf) {
                $(this).removeClass("bi-star bi-star-fill").addClass("bi-star-half");
            } else {
                $(this).removeClass("bi-star bi-star-half").addClass("bi-star-fill");
            }
        } else {
            $(this).removeClass("bi-star-fill bi-star-half").addClass("bi-star");
        }
    });
}


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
    let files = Array.from(fileInput[0].files);

    Promise.all(files.map(file => {
        return new Promise(resolve => {
            let reader = new FileReader();
            reader.onload = (e) => resolve(`<div class="image"><img src="${e.target.result}" alt=""></div>`);
            reader.readAsDataURL(file);
        });
    })).then(results => {
        $('.image-preview').html(results.join(''));
        $('.image:first').addClass('is-represent');
    });
}

// 이미지 체크
const fnCheckProfileImage = (fileInput) => {

    let files = Array.from(fileInput[0].files);

    files.forEach((file) => {

        if(!fnIsOverSize(file)) {
            alert('프로필 이미지는 5MB 이내로 업로드 해주세요.');
            $(fileInput).val('');
            return;
        }

        if(!fnIsImage(file)) {
            alert('이미지 파일만 첨부 가능합니다.');
            $(fileInput).val('');
            return;
        }
        fnPreview(fileInput);
    });
}

// 이미지 개수 제한
const fnCheckImagecount = (evt) => {

    let fileInput = $(evt.currentTarget);
    let files = fileInput[0].files;

    if(files.length > 5) {
        alert('이미지는 최대 5장까지 첨부 가능합니다.');
        fileInput.val('');
    }
    fnCheckProfileImage(fileInput);
}

// 대표 이미지 지정
const fnSelectRepresentImage = (evt) => {

    let imageDiv = $(evt.currentTarget).closest('.image');
    let currentImage = $('.is-represent');
    representImageId = $(evt.currentTarget).data('reviewImage');

    currentImage.removeClass('is-represent');
    imageDiv.addClass('is-represent');

    fnRepresentIndex();
}

// 대표 이미지 인덱스 지정
const fnRepresentIndex = () => {

    let imageDivs = $('.image');
    let representIndex = -1;

    imageDivs.each(function(index) {
        if($(this).hasClass('is-represent')) {
            representIndex = index;
        }
    });

    if(imageDivs.length === 1) {
        representIndex = 0;
    }

    $('#represent-index').val(representIndex);
}

// 맛, 가격 별점 소수로 변환
const fnCalculateRating = (list) => {

    let rating = 0;

    Array.from(list).forEach((stars) => {

        if ($(stars).hasClass("bi-star-fill")) {
            rating += 1;
        } else if ($(stars).hasClass("bi-star-half")) {
            rating += 0.5;
        }
    });
    return rating;
}

// 저장 버튼 활성화 여부
const fnActivateUpdateBtn = () => {

    let currentForm = $('#update-form').serializeArray();
    currentForm.push({name: 'location', value: $('.location-final').text()});

    let formChange = !_.isEqual(currentForm, initForm) || fileChanged;

    let currentTasteRating = fnCalculateRating($('#taste-rating i'));
    let currentPriceRating = fnCalculateRating($('#price-rating i'));
    let ratingChange = initRatingTaste !== currentTasteRating || initRatingPrice !== currentPriceRating;

    $('.btn-update').prop('disabled', !(formChange || ratingChange || deleteAllImageList));
}

// 빈값 검사
const fnCheckEmpty = () => {

    let contentArea = $('.content-area');

    if(contentArea.val().trim() === '') {
        alert('리뷰를 작성해주세요.');
        contentArea.focus();
        return false;
    }

    return true;
}

// 저장 버튼 클릭 시 최종 점검
const fnFinalCheck = () => {

    if(!fnCheckEmpty()) {
        return;
    }

    fnRepresentIndex();
    fnUpdateReview();
}

// csrf 토큰 가져오기
const fnGetCsrfToken = () => {
    return $('meta[name="csrf-token"]').attr('content');
}

// 서버로 작성할 폼 데이터 전송
const fnUpdateReview = () => {

    let form = $('#update-form')[0];
    let formData = new FormData(form);

    let tasteStars = $('#taste-rating i');
    let priceStars = $('#price-rating i');

    let reviewRequestDTO = {
        reviewDTO: {
            reviewId: reviewId,
            productId: productId,
            ratingTaste: fnCalculateRating(tasteStars),
            ratingPrice: fnCalculateRating(priceStars),
            content: $('.content-area').val().trim(),
            location: $('.location-final').text()
        },
        representIndex: $('#represent-index').val(),
        representImageId: representImageId,
        deleteAllImageList: deleteAllImageList
    };

    const jsonBlob = new Blob([JSON.stringify(reviewRequestDTO)], {
        type: "application/json"
    });
    formData.append("reviewRequest", jsonBlob);

    if(fileChanged) {

        let fileInput = $('#review-image')[0];
        if(fileInput.files.length > 0) {
            Array.from(fileInput.files).forEach(file => {
                formData.append('reviewImageList', file);
            })
        }
    }

    axios.put('/review/updateReview', formData, {
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
    }).catch((error) => {
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
    const urlParams = new URL(location.href).searchParams;
    productId = urlParams.get('productId');
    reviewId = urlParams.get('reviewId');

    let locationFinal = $('.location-final').text();

    kakaoMaps.searchCon();

    $('.location-input').val(locationFinal);

    fnRepresentIndex();

    initForm = $('#update-form').serializeArray();
    initForm.push({name: 'location', value: locationFinal});

    initRatingTaste = Number($('#taste-rating').data('ratingTaste'));
    initRatingPrice = Number($('#price-rating').data('ratingPrice'));
    initRepresentIndex = $('#represent-index').val();
});

$('.stars').on('click', 'i', (evt) => {
    fnSetRating(evt);
    fnActivateUpdateBtn();
});

$('#review-image').on('change', fnCheckImagecount);

$('input[type="file"]').on('change', () => {
    fileChanged = true;
    deleteAllImageList = false;
    fnActivateUpdateBtn();
});

$('.btn-LocationSearch').on('click', kakaoMaps.searchCon);

$('#update-form textarea').on('keyup', fnDebounce(fnActivateUpdateBtn, 300));

$('.btn-deleteAllImage').on('click', () => {
    $('#review-image').val('');
    $('.image-preview').empty();
    representImageId = 0;
    deleteAllImageList = true;
    fnActivateUpdateBtn();
});

$(document).on('click', '.image img', (evt) => {
    fnSelectRepresentImage(evt);
    if(initRepresentIndex !== $('#represent-index').val()) {
        $('.btn-update').prop('disabled', false);
    }
    fnActivateUpdateBtn();
});

$('#update-form').on('submit', (evt) => {
    evt.preventDefault();
    fnFinalCheck();
});

$('.btn-undo').on('click', () => {
    window.history.back();
});
