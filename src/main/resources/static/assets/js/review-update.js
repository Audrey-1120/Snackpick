/******************** Kakao Maps API **********************/
// 지도 띄우기
var infowindow = new kakao.maps.InfoWindow({zIndex:1});

var container = document.getElementById('map'); // 지도를 담을 영역

var options = { // 지도의 중심 좌표
    center: new kakao.maps.LatLng(37.50185785121449, 126.78766910206697), // center는 지도 생성에 반드시 필요함! 인자는 위도, 경도
    level: 3 // 지도 확대 레벨
};

// 지도 생성
var map = new kakao.maps.Map(container, options);

// 장소 검색 객체 생성
var ps = new kakao.maps.services.Places(map);

// 키워드 주소 검색 함수
function searchCon() {
    let keyword = $('.location-input').find('input').val();
    ps.keywordSearch(keyword, placesSearchCB);
}

// 키워드 검색 완료 시 호출되는 콜백 함수
function placesSearchCB (data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {

        // LatLngBounds 객체에 좌표 추가
        var bounds = new kakao.maps.LatLngBounds();

        for (var i=0; i<data.length; i++) {
            displayMarker(data[i]);
            bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
        }

        // 검색된 장소 위치 기준으로 지도 범위 재설정
        map.setBounds(bounds);
    }
}

// 지도에 마커 표시하는 함수
function displayMarker(place) {

    // 마커를 생성하고 지도에 표시
    var marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x)
    });

    // 마커 클릭 이벤트 등록
    kakao.maps.event.addListener(marker, 'click', function() {

        // 인포윈도우 보여줌
        infowindow.setContent('<div style="padding:5px;font-size:12px;">' + place.place_name + '</div>');
        infowindow.open(map, marker);

        // 선택된 편의점 이름 변경
        $('.location-final').text(place.place_name);
        $('.location-input').val(place.place_name);

    });
}

/******************** 전역 변수 **********************/
let productId = 0;
let reviewId = 0;
let initForm;
let fileChanged = false;

let isFixed = {}; // 별점 그룹별로 고정 상태 저장
let selectedRating = {}; // 별점 그룹별로 선택된 값 저장

let ratingTaste = $('#taste-rating').data('ratingTaste');
let ratingPrice = $('#price-rating').data('ratingPrice');


// 별점 기능
function fnRating(event) {

    console.log('별점 기능');

    let starsGroup = $(event.currentTarget).closest(".stars"); // 현재 별점 그룹
    let index = $(event.currentTarget).data("index"); // 몇 번째 별인지
    let halfPoint = $(event.currentTarget).offset().left + $(event.currentTarget).width() / 2; // 별의 중간 지점
    let isHalf = event.pageX < halfPoint; // 마우스가 왼쪽에 있으면 반별

    // 별점 업데이트
    starsGroup.find("i").each(function () {
        let starIndex = $(this).data("index");

        if (starIndex < index) {
            $(this).removeClass("bi-star bi-star-half").addClass("bi-star-fill"); // 완별
        } else if (starIndex === index) {
            $(this).removeClass("bi-star bi-star-fill").addClass(isHalf ? "bi-star-half" : "bi-star-fill"); // 반별 or 완별
        } else {
            $(this).removeClass("bi-star-fill bi-star-half").addClass("bi-star"); // 빈별
        }
    });

}

// 클릭 시 별점 고정시키는 함수
function fnSetRating(event) {

    let starsGroup = $(event.currentTarget).closest(".stars");
    let index = $(event.currentTarget).data("index");
    let halfPoint = $(event.currentTarget).offset().left + $(event.currentTarget).width() / 2;
    let isHalf = event.pageX < halfPoint;

    // 선택된 별점 저장
    selectedRating[starsGroup.attr("id")] = { score: index, isHalf: isHalf };

    fnRating(event); // 별점 업데이트
}

// 커서 뗄 경우 선택한 값 유지시키는 함수
function fnResetStars(event) {
    let starsGroup = $(event.currentTarget);
    let ratingData = selectedRating[starsGroup.attr("id")];

    if (ratingData) {
        let { score, isHalf } = ratingData;

        starsGroup.find("i").each(function () {
            let starIndex = $(this).data("index");

            if (starIndex < score) {
                $(this).removeClass("bi-star bi-star-half").addClass("bi-star-fill"); // 완별
            } else if (starIndex === score) {
                $(this).removeClass("bi-star bi-star-fill").addClass(isHalf ? "bi-star-half" : "bi-star-fill"); // 반별 유지
            } else {
                $(this).removeClass("bi-star-fill bi-star-half").addClass("bi-star"); // 빈별
            }
        });
    } else {
        // 초기 별점 값 유지
        fnInitializeStars(starsGroup);
    }
}

function throttle(func, delay) {
    let lastCall = 0;
    let timeoutId = null;

    return function (...args) {
        let now = new Date().getTime();

        if (now - lastCall >= delay) {
            lastCall = now;
            func.apply(this, args);
        } else {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => {
                lastCall = new Date().getTime();
                func.apply(this, args);
            }, delay);

        }
    };
}


/******************** 함수 **********************/
// 이미지 사이즈 제한
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

    let files = Array.from(fileInput[0].files); // Arrays.from()으로 진짜 배열로 변경

    Promise.all(files.map(file => { // files.map() : 각 파일을 처리하는 Promise 배열 생성
        // Promise all() : 모든 Promise가 완료될때까지 대기 후 한꺼번에 처리
        return new Promise(resolve => {
            let reader = new FileReader(); // 파일 읽기 위해서 FileReader 객체 생성
            // reader.onload는 파일이 완전히 읽힌 후 실행되는 콜백 함수이다.
            reader.onload = (e) => resolve(`<div class="image"><img src="${e.target.result}"></div>`);
            // e.target.result에 Base64 형식의 파일 데이터가 담긴다.
            // 다시 말해서 파일 하나를 다 읽고 나면 resolve()를 호출해서 해당 파일의 HTML 반환한다...
            reader.readAsDataURL(file);
        });
    })).then(results => {
        // results는 모든 Promise가 resolve()한 값들의 배열이 된다.
        // join('')을 사용해서 문자열로 합친다.
        $('.image-preview').html(results.join(''));

        // 첫번째 요소에 대표 이미지 추가
        $('.image:first').addClass('is-represent');
    });
}

// 이미지 체크
const fnCheckProfileImage = (fileInput) => {

    let checkFileLabel = $('#image-preview + p');
    let files = Array.from(fileInput[0].files);

    files.forEach((file) => {

        // 사이즈 체크
        if(!fnIsOverSize(file)) {
            checkFileLabel.text('프로필 이미지는 5MB 이내로 업로드 해주세요.');
            checkFileLabel.css('color', 'red');
            $(fileInput).val('');
            return;
        }

        // 이미지 확장자 체크
        if(!fnIsImage(file)) {
            checkFileLabel.text('이미지 파일만 첨부 가능합니다.');
            checkFileLabel.css('color', 'red');
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

    currentImage.removeClass('is-represent');
    imageDiv.addClass('is-represent');

}

// 맛, 가격 별점 소수로 변환
const fnCalculateRating = (list) => {

    // 별점 아이콘 가져와서 점수로 변환(float)

    let rating = 0;

    // i 요소 리스트 전달받기
    Array.from(list).forEach((stars) => {

        if ($(stars).hasClass("bi-star-fill")) {
            rating += 1;
        } else if ($(stars).hasClass("bi-star-half")) {
            rating += 0.5;
        }
    });
    return rating;
}

// 빈값 검사
const fnCheckEmpty = () => {

    let productInput = $('.productName-input');
    let contentArea = $('.content-area');
    let locationInput = $('.location-input');

    // 제품 input 빈값인 경우
    if(productInput.val() === '') {
        alert('제품을 선택해주세요.');
        productInput.focus();
        return false;
    }

    if(contentArea.val() === '') {
        alert('리뷰를 작성해주세요.');
        contentArea.focus();
        return false;
    }

    if(locationInput.val() === '') {
        alert('편의점 위치를 등록해주세요.');
        locationInput.focus();
        return false;
    }
    return true;
}

// 저장 버튼 클릭 시 최종 점검
const fnFinalCheck = () => {

    // 1. 빈칸 검사
    if(!fnCheckEmpty()) {
        return;
    }

    let tasteStars = $('#taste-rating i');
    let priceStars = $('#price-rating i');

    // 2. 별점 점수로 변환(fnCalculateRating)
    ratingTaste = fnCalculateRating(tasteStars);
    ratingPrice = fnCalculateRating(priceStars);

    if(ratingTaste === 0) {
        alert('맛 별점을 선택해주세요.');
        return;
    }

    if(ratingPrice === 0) {
        alert('가격 별점을 선택해주세요.');
        return;
    }

    // 3. 이미지 대표 이미지로 설정
    // 대표 이미지가 설정된 div가 image 리스트에서 몇번째인지 찾는다.
    // 해당 인덱스를 hidden index에 찾는다.
    let files = $('#review-image')[0].files;
    let imageDivs = $('.image');
    let representIndex = -1;

    imageDivs.each(function(index) {
        if($(this).hasClass('is-represent')) {
            representIndex = index;
        }
    });

    $('#represent-index').val(representIndex);

    // 데이터 보내기
    fnUpdateReview();


}

// 폼의 입력값 JSON으로 변환
const formToJson = (form) => {
    let obj = {};
    let formData = new FormData(form);
    formData.forEach((value, key) => {
        obj[key] = value;
    });
    return obj;
}

// 서버로 작성할 폼 데이터 전송
const fnUpdateReview = () => {

    /*
        reviewId
        productId
        rating_taste
        rating_price
        content
        location
        update_dt
     */

    let form = $('#update-form')[0];
    let formData = new FormData(form);

    // 폼 데이터 -> DTO 구조
    let reviewRequestDTO = {
        reviewDTO: {
            reviewId: reviewId,
            productId: productId,
            ratingTaste: ratingTaste,
            ratingPrice: ratingPrice,
            content: $('.content-area').val(),
            location: $('.location-input').val()
        },
        representIndex: $('#represent-index').val()
    }

    // JSON blob으로 변환 후 FormData에 추가
    const jsonBlob = new Blob([JSON.stringify(reviewRequestDTO)], {
        type: "application/json"
    });
    formData.append("reviewRequest", jsonBlob);

    // 파일 input 처리하기
    let fileInput = $('#review-image')[0];
    if(fileInput.files.length > 0) {
        Array.from(fileInput.files).forEach(file => {
            formData.append('reviewImageList', file);
        })
    }

    axios.put('/review/updateReview', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
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
        alert('리뷰 수정에 실패하였습니다.');
    });

}


/******************** 이벤트 **********************/
$(document).ready(() => { // 지도에 해당 편의점 위치 띄우기

    const urlParams = new URL(location.href).searchParams;
    productId = urlParams.get('productId');
    reviewId = urlParams.get('reviewId');

    searchCon();

    $('.location-input').val($('.location-final').text());

    initForm = $('#update-form').serializeArray();


});

let throttledUpdate = throttle(fnRating, 25); // 50ms 마다 실행된다.

$(".stars i").on("mousemove", throttledUpdate);
$(".stars i").on("click", fnSetRating);
$(".stars").on("mouseleave", fnResetStars);

$('#review-image').on('change', fnCheckImagecount);

$('input[type="file"]').on('change', () => {
    fileChanged = true;
});

$('.btn-LocationSearch').on('click', searchCon);

$('#update-form input, #update-form textarea').on('keyup change', () => {

    // 현재 폼
    let currentForm = $('#update-form').serializeArray();

    // 현재 폼과 초기 폼 데이터 비교
    if(!_.isEqual(currentForm, initForm) || fileChanged) {
        $('.btn-update').prop('disabled', false);
    } else {
        $('.btn-update').prop('disabled', true);
    }

});

$('.btn-deleteAllImage').on('click', () => {
    $('#review-image').val('');
    $('.image-preview').empty();
});

$(document).on('click', '.image img', fnSelectRepresentImage);

$('#update-form').on('submit', (evt) => {
    evt.preventDefault();
    fnFinalCheck();
})
