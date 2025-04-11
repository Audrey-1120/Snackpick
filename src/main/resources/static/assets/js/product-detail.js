import * as comment from './comment.js';

/******************** 전역 변수 **********************/
let productId = 0;
let globalSort = 'createDt,DESC';

/******************** 함수 **********************/
// 리뷰 리스트 조회 데이터 받아서 정렬
const fnSortReviewList = (reviewList, sortBy, order = 'DESC') => {

    return reviewList.sort((a, b) => {
        let review1 = a[sortBy];
        let review2 = b[sortBy];

        if (sortBy === 'createDt') {
            review1 = new Date(review1);
            review2 = new Date(review2);
        }

        if (order === 'ASC') {
            return review1 > review2 ? 1 : (review1 < review2 ? -1 : 0);
        } else {
            return review1 > review2 ? -1 : (review1 < review2 ? 1 : 0);
        }
    });
}

// 리뷰 리스트 조회
const fnGetReviewList = (page, sort) => {

    axios.get('/review/getReviewList', {
        params: {
            page: page,
            sort: sort,
            productId: productId
        }
    })
    .then((response) => {

        let finalSort = sort.split(',');
        let reviewList = response.data.reviewList;

        if(reviewList.contents.length !== 0) {
            fnSortReviewList(reviewList.contents, finalSort[0], finalSort[1]);
            fnShowResult(reviewList.contents);
            fnSetPaging(reviewList.currentPage, reviewList.totalPage, reviewList.beginPage, reviewList.endPage);
        } else {
            let str = '<div><p>리뷰가 없어요. 첫번째 리뷰 작성자가 되어 주세요!</p></div>';
            let container = $('.pagination-container');

            container.empty();
            container.append(str);
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    })
}

// 페이징 설정 함수
const fnSetPaging = (currentPage, totalPage, beginPage, endPage) => {

    let pagingContainer = $('.pagination-container');
    let paging = '<ul>';

    if(beginPage === 1) {
        paging += '<li><a href="javascript:void(0);" onclick="return false;"><i class="bi bi-chevron-left"></i></a></li>';
    } else {
        paging += '<li><a href="javascript:void(0);" onclick="fnGetReviewList(' + (beginPage - 1) + ',\'' + globalSort + '\')"><i class="bi bi-chevron-left"></i></a></li>';
    }

    for(let i = beginPage; i <= endPage; i++) {
        if(i === currentPage) {
            paging += '<li><a href="javascript:void(0);" class="active" onclick="fnGetReviewList(' + i + ',\'' + globalSort + '\')">' + i + '</a></li>';
        } else {
            paging += '<li><a href="javascript:void(0);" onclick="fnGetReviewList(' + i + ',\'' + globalSort + '\')">' + i + '</a></li>';
        }
    }

    if(endPage === totalPage) {
        paging += '<li><a href="javascript:void(0);" onclick="return false;"><i class="bi bi-chevron-right"></i></a></li>';
    } else {
        paging += '<li><a href="javascript:void(0);" onclick="fnGetReviewList(' + (endPage + 1) + ',\'' + globalSort + '\')"><i class="bi bi-chevron-right"></i></a></li>';
    }

    paging += '</ul>';

    pagingContainer.empty();
    pagingContainer.append(paging);

}

// 리뷰 리스트 조회 결과 표시
const fnShowResult = (reviewList) => {

    let container = $('.review-select');
    let str = '';

    reviewList.forEach((review) => {
        str += '<div class="review-item mt-3" data-review-id="' + review.reviewId + '">';
        str += '<div class="testimonial-item">';
        str += '<div class="writer-profile">';
        str += '<div class="writer-image">';
        if(review.member.profileImage !== null) {
            str += '<img src="' + review.member.profileImage + '">';
        } else {
            str += '<img src="/assets/img/default-profile.jpg">';
        }
        str += '</div>';
        str += '<p class="writer-name">' + review.member.nickname + '</p>';
        str += '</div>';
        str += '<div class="review-main">';
        if(review.reviewImageList.length !== 0) {
            str += '<div class="review-image">'
            str += '<img src="' + review.reviewImageList[0].reviewImagePath + '">';
            str += '</div>';
        } else {
            str += '<div class="review-image"><img src="/assets/img/snackpick-circle-logo.png"></div>';
        }
        str += '<div class="content-form">';
        str += '<div class="rating mb-1">';
        str += '<h4>맛</h4>';
        str += '<div class="stars">'
        for(let i = 1; i <= 5; i++) {
            if(review.ratingTaste >= i) {
                str += '<i class="bi bi-star-fill"></i>';
            } else if((review.ratingTaste > i - 1) && (review.ratingTaste < i)) {
                str += '<i class="bi bi-star-half"></i>';
            } else {
                str += '<i class="bi bi-star"></i>';
            }
        }
        str += '</div>';
        str += '<h4>가격</h4>';
        str += '<div class="stars">';
        for(let i = 1; i <= 5; i++) {
            if(review.ratingPrice >= i) {
                str += '<i class="bi bi-star-fill"></i>';
            } else if((review.ratingPrice > i - 1) && (review.ratingPrice < i)) {
                str += '<i class="bi bi-star-half"></i>';
            } else {
                str += '<i class="bi bi-star"></i>';
            }
        }
        str += '</div>';
        str += '</div>';
        str += '<div class="line"></div>';
        str += '<h4 class="review-content mt-3">' + review.content + '</h4>';
        str += '<div class="review-bottom d-flex justify-content-between">';
        str += '<p class="review-location mt-3"><span>' + review.location + '</span>에서 구매했어요!</p>';

        if(Number($('.login-id').val()) === review.member.memberId) {
            str += '<div class="d-flex gap-2">';
            str += '<div class="btn-update"><i class="bi bi-pencil-square"></i></div>';
            str += '<div class="btn-delete"><i class="bi bi-trash3"></i></div>';
            str += '</div>';
        }
        str += '</div>';
        str += '</div>';
        str += '</div>';
        str += '</div>';

    });

    $('.review-item').remove();
    container.after(str);

}

// 리뷰 상세 조회
const fnGetReviewDetail = (evt) => {

    let reviewId = $(evt.currentTarget).data('review-id');

    axios.get('/review/getReviewDetail', {
        params: {
            reviewId: reviewId
        }
    })
    .then((response) => {

        fnShowReviewDetail(response.data.review);
        comment.fnShowCommentList(response.data.commentList);

        $('#review-detail').modal('show');
    })
    .catch((error) => {
        alert(error.response.data.message);
    })
}

// 리뷰 상세 조회 데이터 화면에 추가
const fnShowReviewDetail = (review) => {

    let imageContainer = $('.reviewImage-modal');

    let imageStr = '';
    review.reviewImageList.forEach((reviewImage) => {
        imageStr += '<div class="image">';
        imageStr += '<img src="' + reviewImage.reviewImagePath + '"></div>';
    });
    imageContainer.html(imageStr);

    let str = '<p>맛</p>';
    str += '<div class="stars">';
    for(let i = 1; i <= 5; i++) {
        if(review.ratingTaste >= i) {
            str += '<i class="bi bi-star-fill"></i>';
        } else if((review.ratingTaste > i - 1) && (review.ratingTaste < i)) {
            str += '<i class="bi bi-star-half"></i>';
        } else {
            str += '<i class="bi bi-star"></i>';
        }
    }
    str += '</div>';
    str += '<p>가격</p>';
    str += '<div class="stars">';
    for(let i = 1; i <= 5; i++) {
        if(review.ratingPrice >= i) {
            str += '<i class="bi bi-star-fill"></i>';
        } else if((review.ratingPrice > i - 1) && (review.ratingPrice < i)) {
            str += '<i class="bi bi-star-half"></i>';
        } else {
            str += '<i class="bi bi-star"></i>';
        }
    }
    str += '</div>';
    $('.rating-modal').html(str);

    if(review.member.profileImage !== null) {
        $('.writerImage-modal').html('<img src="' + review.member.profileImage + '">');
    } else {
        $('.writerImage-modal').html('<img src="/assets/img/default-profile.jpg">');
    }
    $('.writerProfile-modal p').html(review.member.nickname);
    $('.review-id').val(review.reviewId);

    $('.content-2 p:eq(0)').text(review.content);
    $('.location-modal span').text(review.location);

}

// csrf 토큰 가져오기
const fnGetCsrfToken = () => {
    return $('meta[name="csrf-token"]').attr('content');
}

// 리뷰 삭제
const fnDeleteReview = (evt) => {

    let reviewId = $(evt.currentTarget).closest('.review-item').data('review-id');

    axios.put('/review/deleteReview',
        { reviewId: reviewId },
        {
            headers: {
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': fnGetCsrfToken()
            }
    })
    .then((response) => {
        if(response.data.success) {
            alert(response.data.message);
            window.location.reload();
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    });
}

/******************** 이벤트 **********************/
window.fnGetReviewList = fnGetReviewList;

$(document).ready(() => {
    const urlParams = new URL(location.href).searchParams;
    productId = urlParams.get('productId');
    fnGetReviewList(1, 'createDt,DESC');
});

// 정렬 선택
$('#review-sort').on('change', () => {
    globalSort = $('#review-sort option:selected').val();
    fnGetReviewList(1, globalSort);
});

// 리뷰 작성 페이지로 이동
$('.btn-write').on('click', () => {
    let productId = $('.productId').val();
    location.href = '/review/reviewWrite.page?productId=' + productId;
});

// 리뷰 상세 조회
$(document).off('click', '.review-item').on('click', '.review-item', (evt) => {
    evt.stopPropagation();
    fnGetReviewDetail(evt);
});

// 리뷰 삭제
$(document).on('click', '.btn-delete', (evt) => {
    evt.stopPropagation();
    if(confirm("리뷰를 삭제하시겠습니까?")) {
        fnDeleteReview(evt);
    }
});

// 리뷰 수정
$(document).on('click', '.btn-update', (evt) => {
    evt.stopPropagation();
    let reviewId = $(evt.currentTarget).closest('.review-item').data('review-id');
    location.href = "/review/reviewUpdate.page?reviewId=" + reviewId + '&productId=' + productId;
});

