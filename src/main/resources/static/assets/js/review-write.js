/******************** kakao maps api **********************/
var infowindow = new kakao.maps.InfoWindow({zIndex:1});

var container = document.getElementById('map');

var options = {
    center: new kakao.maps.LatLng(37.50185785121449, 126.78766910206697),
    level: 3
};

var map = new kakao.maps.Map(container, options);
var ps = new kakao.maps.services.Places(map);

function searchCon() {
    let keyword = $('.location-input').find('input').val();
    ps.keywordSearch(keyword, placesSearchCB, {
        category_group_code: 'CS2'
    });
}

function placesSearchCB (data, status) {
    if (status === kakao.maps.services.Status.OK) {

        var bounds = new kakao.maps.LatLngBounds();

        for (var i=0; i<data.length; i++) {
            displayMarker(data[i]);
            bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
        }
        map.setBounds(bounds);
    }
}

function displayMarker(place) {

    var marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x)
    });

    var locationFinal = $('.location-final');
    var locationInput = $('.location-input');

    kakao.maps.event.addListener(marker, 'click', function() {

        infowindow.setContent('<div style="padding:5px;font-size:12px;">' + place.place_name + '</div>');
        infowindow.open(map, marker);

        locationInput.val(place.place_name);
        locationFinal.text(place.place_name);
        locationFinal.removeClass(('no-location'));
    });
}

/******************** 전역 변수 **********************/
let isFixed = {};
let selectedRating = {};

let addProduct = false;
let ratingTaste = 0;
let ratingPrice = 0;

let subCategoryList = [];

/******************** 함수 **********************/

function fnSetRating(event) {

    let starsGroup = $(event.currentTarget).closest('.stars');
    let index = $(event.currentTarget).data('index');
    let halfPoint = $(event.currentTarget).offset().left + $(event.currentTarget).width() / 2;
    let isHalf = event.pageX < halfPoint;

    selectedRating[starsGroup.attr('id')] = { score: index, isHalf: isHalf };
    fnUpdateStars(starsGroup, index, isHalf);
}

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

const fnSearchProduct = () => {

    let searchKeyword = $('#product-name').val().replace(/\s+/g, ' ').trim();

    if(searchKeyword === '') {
        $('#product-name').focus();
        return;
    }

    axios.get('/product/searchProduct', {
        params: {
            searchKeyword: searchKeyword
        }
    })
    .then((response) => {
        fnShowSearchResult(response.data.productList);
    })
    .catch((error) => {
        alert(error.response.data.message);
    })
}

const fnShowSearchResult = (productList) => {

    let container = $('.product-search');
    container.empty();

    if(productList.length === 0) {

        let productName = $('#product-name').val().replace(/\s+/g, ' ').trim();
        let str = '<div class="product-item no-content">';
        str += '<p>검색 결과가 없습니다. <span>\'' + productName + '\'</span> 직접 등록하기.</p>';
        str += '</div>';

        container.append(str);
        return;
    }

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

function fnAddProductName() {

    let defaultInput = $('.select-product input');
    $('#productId').remove();

    if($(this).hasClass('no-content')) {

        let productName = $('#product-name').val().replace(/\s+/g, ' ').trim()
        defaultInput.val(productName);
        $('.review-category').css('display', 'block');
        addProduct = true;

    } else {

        let productName = $(this).find('p').text();
        let productId = $(this).find('input:eq(0)').val();

        defaultInput.val(productName);
        defaultInput.after('<input type="hidden" id="productId" name="productId" value="' + productId + '">');

        $('.review-category').css('display', 'none');
        addProduct = false;
    }
    $('#search-modal').modal('hide');
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

const fnSetSubCategory = () => {

    if(!addProduct) {
        return;
    }

    let subCate = $('#select-cat2');
    let selectedMainCate = parseInt($('#select-cat1 option:selected').val());

    subCate.empty();
    $.each(subCategoryList, function (i, item) {
        if($(item).data('parent') === selectedMainCate) {
            subCate.append($(this).clone());
        }
    });

    subCate.prop('selectedIndex', 0);
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
    let files = Array.from(fileInput[0].files);

    Promise.all(files.map(file => {
        return new Promise(resolve => {
            let reader = new FileReader();
            reader.onload = (e) => resolve(`<div class="image"><img src="${e.target.result}"></div>`);
            reader.readAsDataURL(file);
        });
    })).then(results => {
        $('.image-preview').html(results.join(''));
        $('.image:first').addClass('is-represent');
    });
}

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

const fnCheckImagecount = (evt) => {

    let fileInput = $(evt.currentTarget);
    let files = fileInput[0].files;

    if(files.length > 5) {
        alert('이미지는 최대 5장까지 첨부 가능합니다.');
        fileInput.val('');
    }
    fnCheckProfileImage(fileInput);
}

const fnSelectRepresentImage = (evt) => {

    let imageDiv = $(evt.currentTarget).closest('.image');
    let currentImage = $('.is-represent');

    currentImage.removeClass('is-represent');
    imageDiv.addClass('is-represent');

    fnRepresentIndex();

}

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

const fnCheckEmpty = () => {

    if(addProduct) {
        let topCategorySelect = $('#select-cat1 option:selected');
        let subCategorySelect = $('#select-cat2 option:selected');

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

    let tasteStars = $('#taste-rating i');
    let priceStars = $('#price-rating i');

    let productInput = $('.productName-input');
    let contentArea = $('.content-area');
    let locationInput = $('.location-final');

    ratingTaste = fnCalculateRating(tasteStars);
    ratingPrice = fnCalculateRating(priceStars);

    if(productInput.val() === '') {
        alert('제품을 선택해주세요.');
        productInput.focus();
        return false;
    }

    if(ratingTaste === 0) {
        alert('맛 별점을 선택해주세요.');
        return false;
    }

    if(ratingPrice === 0) {
        alert('가격 별점을 선택해주세요.');
        return false;
    }

    if(contentArea.val().trim() === '') {
        alert('리뷰를 작성해주세요.');
        contentArea.focus();
        return false;
    }

    if(locationInput.hasClass('no-location')) {
        alert('편의점 위치를 등록해주세요.');
        locationInput.focus();
        return false;
    }
    return true;
}

const fnFinalCheck = () => {

    if(!fnCheckEmpty()) {
        return;
    }

    fnRepresentIndex();
    fnWriteReview();
}

const fnGetCsrfToken = () => {
    return $('meta[name="csrf-token"]').attr('content');
}

const fnWriteReview = () => {

    let form = $('#write-form')[0];
    let formData = new FormData(form);

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
            content: $('.content-area').val().trim(),
            location: $('.location-final').text()
        },
        representIndex: $('#represent-index').val(),
        addProduct: addProduct
    }

    const jsonBlob = new Blob([JSON.stringify(reviewRequestDTO)], {
        type: "application/json"
    });
    formData.append("reviewRequest", jsonBlob);

    let fileInput = $('#review-image')[0];
    if(fileInput.files.length > 0) {
        Array.from(fileInput.files).forEach(file => {
            formData.append('reviewImageList', file);
        })
    }

    axios.post('/review/insertReview', formData, {
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

/******************** 이벤트 **********************/

$(document).ready(() => {
    subCategoryList = Array.from($('#select-cat2 option'));
})

$('.stars').on('click', 'i', fnSetRating);

$('#product-name').on('keyup', fnDebounce(fnSearchProduct, 400));

$(document).on('click', '.product-item', fnAddProductName);

$('#search-modal').on('show.bs.modal', () => {

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

$(document).on('change', '#select-cat1', fnSetSubCategory);