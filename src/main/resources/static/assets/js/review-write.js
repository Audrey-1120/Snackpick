
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
let isFixed = {}; // 별점 그룹별로 고정 상태 저장
let selectedRating = {}; // 별점 그룹별로 선택된 값 저장

let addProduct = false; // 제품 직접 입력 여부
let ratingTaste = 0;
let ratingPrice = 0;

/**
 * {
 *     "taste-rating": {score: 3, isHalf: true},
 *     "price-rating": {score: 4, isHalf: false},
 * }
 *  여기서 이제 score는 몇번째 별까지 선택되었는가?
 *  isHalf: true -> 반별이 선택되었다.(0.5점!)
 */


/******************** 함수 **********************/

// 별점 기능
function fnRating(event) {

    console.log('별점 기능');

    let starsGroup = $(event.currentTarget).closest(".stars"); // 선택한 게 맛 별점인지 가격 별점인지
    // event.currentTarget = 현재 커서가 올라간 별

    let halfPoint = $(event.currentTarget).offset().left + $(event.currentTarget).width() / 2; // 별의 왼쪽과 오른쪽 구분하는 기준!
    let index = $(event.currentTarget).data("index"); // 몇번째 별인가? (별의 인덱스)
    let isHalf = event.pageX < halfPoint; // 클릭한 부분의 좌표
    // event.pageX < halfPoint -> 마우스 위치가 왼쪽일 경우 반별, 오른쪽일 경우 완별.

    starsGroup.find("i").each(function () {
        // each()로 별 순회
        // 선택한 별보다 왼쪽 -> 완별(이미 선택한 별이므로)
        // 선택한 별인 경우 -> 반별 or 완별
        // 선택한 별보다 오른쪽인 경우 -> 빈별

        let starIndex = $(this).data("index");
        if (starIndex < index) {
            $(this).removeClass("bi-star bi-star-half").addClass("bi-star-fill"); // 완별
        } else if (starIndex === index) {
            $(this).removeClass("bi-star bi-star-fill");
            $(this).addClass(isHalf ? "bi-star-half" : "bi-star-fill"); // 반별 or 완별 적용
        } else {
            $(this).removeClass("bi-star-fill bi-star-half").addClass("bi-star"); // 빈별
        }
    });

}

// 클릭 시 별점 고정시키는 함수
function fnSetRating(event) {
    let starsGroup = $(event.currentTarget).closest(".stars"); // 선택한 게 맛 별점인지 가격 별점인지
    let index = $(event.currentTarget).data("index"); // 몇번째 별인가? (별의 인덱스)
    let halfPoint = $(event.currentTarget).offset().left + $(event.currentTarget).width() / 2; // 별의 왼쪽과 오른쪽 구분하는 기준!
    let isHalf = event.pageX < halfPoint; // 클릭한 부분의 좌표 (왼쪽이면 반별, 오른쪽이면 완별)

    // 선택된 별점과 반별 여부 저장
    selectedRating[starsGroup.attr("id")] = { score: index, isHalf: isHalf }; // score는 지금 선택한 index, isHalf는 왼쪽 혹은 오른쪽

    /**
     * {
     *     "taste-rating": {score: 3, isHalf: true},  // 맛 별 별점 선택한다면..
     *     "price-rating": {score: 4, isHalf: false}, // 가격 별 맛점 선택한다면..
     * }
     *  여기서 이제 score는 몇번째 별까지 선택되었는가?
     *  isHalf: true -> 반별이 선택되었다.(0.5점!)
     */
    fnRating(event); // 별점 업데이트
}

// 커서 뗄 경우 선택한 값 유지시키는 함수
function fnResetStars(event) {
    let starsGroup = $(event.currentTarget);
    let ratingData = selectedRating[starsGroup.attr("id")]; // "taste-rating": {scroe: 3, isHalf: true}

    if (ratingData) {
        let {score, isHalf} = ratingData; // 저장된 값 불러오기

        starsGroup.find("i").each(function () { // 선택한 별점의 별 5개를 가져와서 each문 돌린다.
            let starIndex = $(this).data("index");
            if (starIndex < score) {
                $(this).removeClass("bi-star bi-star-half").addClass("bi-star-fill"); // 완별
            } else if (starIndex === score) {
                $(this).removeClass("bi-star bi-star-fill");
                $(this).addClass(isHalf ? "bi-star-half" : "bi-star-fill"); // 반별 유지
            } else {
                $(this).removeClass("bi-star-fill bi-star-half").addClass("bi-star"); // 빈별
            }
        });
    }
}

// debouce와 throttle의 차이점
// debounce : 여러 번 발생하는 이벤트에서 가장 마지막 이벤트 만을 실행되도록 한다. (입력창 자동완성 등에 많이 사용된다.) - (keyup, input, search)
// throttle : 여러 번 발생하는 이벤트를 일정 시간 동안, 한번만 실행되도록 만든다. (스크롤 이벤트 등에 많이 사용됨.) (mousemove, scroll, resize)
// moutmove이벤트는 마우스를 조금만 움직여도 계속 실행되기 때문에 성능에 부담이 갈 수 있다. 이 경우에는 throttle을 사용하는게 더 적당하다.
    // 50ms마다 이벤트 실행하도록 제한을 둔다.


// throttle에 대한 설명
// throttle은 특정 시간 간격 마다 한 번씩만 실행되도록 제한하는 함수이다.
    // 이를 통해 이벤트가 너무 자주 발생하는 것을 방지하고 성능을 최적화할 수 있다.


function throttle(func, delay) {
    let lastCall = 0; // 마지막으로 실행된 시간 기록 (초기값은 0이다.)
    let timeoutId = null;

    // 이제 내부에서 새로운 익명 함수를 반환한다.
    return function (...args) {
        let now = new Date().getTime(); // 현재 시간 (밀리초)

        // 1. 첫 이벤트는 즉시 실행한다.
        if (now - lastCall >= delay) { // 마지막 실행 이후 delay ms 이상 지나야 실행된다.
            lastCall = now; // 현재 시간을 마지막 실행 시간으로 업데이트 한다.
            func.apply(this, args); // 원래 함수 실행한다.
        } else {
            // 2. 이후에는 delay만큼 기다렸다가 실행
            // 빠르게 움직이면 중간에 남아 있는 setTimeout()을 취소하고 새로운 setTimeout()을 설정한다.
            clearTimeout(timeoutId);
            // 마우스를 빠르게 움직이면 마지막 이벤트를 기준으로 실행된다.
            timeoutId = setTimeout(() => {
                lastCall = new Date().getTime();
                func.apply(this, args);
            }, delay);

        }
    };
}


// 제품 검색하기
const fnSearchProduct = () => {

    // 제품명
    let searchKeyword = $('.modal-body input').val();

    if(searchKeyword === '') {
        $('.modal-body input').focus();
        return;
    }
    // /product/searchProduct 경로로 보내기
    axios.get('/product/searchProduct', {
        params: {
            searchKeyword: searchKeyword
        }
    })
        .then((response) => {
            console.log("searchResult", response.data.productList);
            fnShowSearchResult(response.data.productList);
        })
        .catch((error) => {
            alert('검색 중 오류가 발생하였습니다.');
        })

}

// 제품 검색 결과 보여주기
const fnShowSearchResult = (productList) => {

    /*
        {
        "productList": [
            {
                "productId": 1,
                "reviewCount": 2,
                "productName": "새우깡",
                "subCategory": "과자",
                "topCategory": "스낵/과자류",
                "ratingTasteAverage": 4.5,
                "ratingPriceAverage": 3.5
                }
            ]
        }

     */

    let container = $('.product-search');
    container.empty();

    // 결과가 없을 경우, 직접 입력으로 변경한다.
    if(productList.length === 0) {

        let str = '<div class="product-item no-content">';
        str += '<p>검색 결과가 없습니다. 직접 등록하기.</p>';
        str += '</div>';

        container.append(str);
        return;
    }

    // 결과가 있을 경우
    $.each(productList, function (i, item) {

        let str = '<div class="product-item">';
        str += '<p>' + item.productName + '</p>';
        str += '<input type="hidden" value="' +item.productId + '">';
        str += '<input type="hidden" value="' +item.topCategory + '">';
        str += '<input type="hidden" value="' +item.subCategory + '">';
        str += '</div>';

        container.append(str);
    });
}

// 아이템 클릭 시 input에 추가되기
function fnAddProductName() {

    // 기존 input
    let defaultInput = $('.select-product input');

    // input hidden 요소가 있다면 삭제(productId)
    $('#productId').remove();

    if($(this).hasClass('no-content')) {

        // 1. 직접 입력 아이템 클릭 시

        // input 이름 가져오기
        let productName = $('.modal-body input').val();

        // input에 값 할당해주기 - 이때 새 제품이므로 제품 번호는 필요 없음.
        defaultInput.val(productName);

        // 카테고리 섹션 안보이게
        $('.review-category').css('display', 'block');

        // 상품 직접 추가했는지 유무
        addProduct = true;

    } else {

        // 2. 검색된 아이템 클릭 시
        let productName = $(this).find('p').text();
        let productId = $(this).find('input:eq(0)').val();

        // input에 값 할당해주기 - 이때 새 제품이므로 제품 번호는 필요 없음.
        defaultInput.val(productName);
        defaultInput.after('<input type="hidden" id="productId" name="productId" value="' + productId + '">');

        // 카테고리 섹션 안보이게
        $('.review-category').css('display', 'none');

        // 상품 직접 추가했는지 유무
        addProduct = false;
    }

    // 모달 닫기
    $('#search-modal').modal('hide');

}

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

    // 제품 추가 유무에 따라 카테고리 select 검사
    if(addProduct) {
        let topCategorySelect = $('#select-cat1 option:selected');
        let subCategorySelect = $('#select-cat2 option:selected');

        // 대분류/중분류 - 선택한 값의 val값이 0인 경우
        if(topCategorySelect.val() === '0') {
            alert('대분류를 선택해주세요.');
            topCategorySelect.focus();
            return false;
        }

        if(subCategorySelect.val() === '0') {
            alert('중분류를 선택해주세요.');
            subCategorySelect.focus();
            return false;
        }
    }

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

// 작성 버튼 클릭 시 최종 점검
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
    fnWriteReview();

}

// 서버로 작성 폼 데이터 전송
const fnWriteReview = () => {

    // form
    // productDTO: productName, topCategory, subCategory
    // reviewDTO: content, location, reviewImage, productId(기존 제품 선택시), memberId:1, ratingTaste, ratingPrice
    // reviewRequestDTO: representIndex, addProduct

    let form = $('#write-form')[0];
    let formData = new FormData(form);

    // 폼 데이터 -> DTO 구조.. JSON 변환
    let reviewRequestDTO = {
        productDTO: {
            productName: $('.productName-input').val(),
            topCategory: $('#select-cat1 option:selected').val(),
            subCategory: $('#select-cat2 option:selected').val()

        },
        reviewDTO: {
            ratingTaste: ratingTaste,
            ratingPrice: ratingPrice,
            productId: $('#productId').val(),
            content: $('.content-area').val(),
            location: $('.location-input').val()
        },
        representIndex: $('#represent-index').val(),
        addProduct: addProduct
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

    axios.post('/review/insertReview', formData, {
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
        alert('리뷰 작성에 실패하였습니다.');
    });
}

/******************** 이벤트 **********************/

// 쓰로틀 적용 (50ms 간격)
let throttledUpdate = throttle(fnRating, 25); // 50ms 마다 실행된다.

$(".stars i").on("mousemove", throttledUpdate);
$(".stars i").on("click", fnSetRating);
$(".stars").on("mouseleave", fnResetStars);

$('.btn-ProductSearch').on('click', fnSearchProduct);

$(document).on('click', '.product-item', fnAddProductName);

$('#search-modal').on('show.bs.modal', () => {

    // 모달 초기화
    let container = $('.product-search');
    container.empty();
    $('#product-name').val('');

});

$('.btn-LocationSearch').on('click', searchCon);

$('#review-image').on('change', fnCheckImagecount);

$('.btn-deleteAllImage').on('click', () => {
    $('#review-image').val('');
    $('.image-preview').empty();
});

$(document).on('click', '.image img', fnSelectRepresentImage);

$('#write-form').on('submit', (evt) => {
    evt.preventDefault();
    fnFinalCheck();
});