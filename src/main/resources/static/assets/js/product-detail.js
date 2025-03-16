/******************** 전역 변수 **********************/
let productId = 0;
let globalSort = 'createDt,DESC';

const list = document.querySelector('.reviewImage-modal');

/******************** 함수 **********************/
// 리뷰 리스트 조회 데이터 받아서 정렬
const fnSortReviewList = (reviewList, sortBy, order = 'DESC') => {

    return reviewList.sort((a, b) => {
        let review1 = a[sortBy];
        let review2 = b[sortBy];

        // 값이 날짜라면 Date 객체로 변환
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

    // 서버로 보내기
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
            fnSortReviewList(reviewList.contents, finalSort[0], finalSort[1]); // 정렬
            fnShowResult(reviewList.contents); // 리뷰 결과 보여주기
            fnSetPaging(reviewList.currentPage, reviewList.totalPage, reviewList.beginPage, reviewList.endPage); // 페이지네이션
        } else {
            let str = '<div><p>리뷰가 없어요. 첫번째 리뷰 작성자가 되어 주세요!</p></div>';
            $('.pagination-container').append(str);
        }
    })
    .catch((error) => {
        alert('리뷰를 로드하던 중 오류가 발생하였습니다.');
    })
}

// 페이징 설정 함수
const fnSetPaging = (currentPage, totalPage, beginPage, endPage) => {

    // 페이징 추가할 부분
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

    // 페이징 초기화
    pagingContainer.empty();

    // 페이징 설정
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
        if(review.member.profileImage !== '') {
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
            str += '<div class="review-image"><div class="default-image"></div></div>';
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

    // 서버로 보내기
    axios.get('/review/getReviewDetail', {
        params: {
            reviewId: reviewId
        }
    })
    .then((response) => {

        let review = response.data.review;
        let imageContainer = $('.reviewImage-modal');

        // 리뷰 이미지
        let imageStr = '';
        review.reviewImageList.forEach((reviewImage) => {
            imageStr += '<div class="image">';
            imageStr += '<img src="' + reviewImage.reviewImagePath + '"></div>';
        });
        imageContainer.html(imageStr);

        // 별점
        let str = '<p>맛</p>';
        str += '<div class="stars">';
        for(let i = 1; i <= 5; i++) {
            if(review.ratingTaste >= i) {
                str += '<i class="bi bi-star-fill"></i>';
            } else if((review.ratingTaste >= i - 1) && (review.ratingTaste < i)) {
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
            } else if((review.ratingPrice >= i - 1) && (review.ratingPrice < i)) {
                str += '<i class="bi bi-star-half"></i>';
            } else {
                str += '<i class="bi bi-star"></i>';
            }
        }
        str += '</div>';
        $('.rating-modal').html(str);

        // 프로필
        if(review.member.profileImage !== '') {
            $('.writerImage-modal').html('<img src="' + review.member.profileImage + '">');
        } else {
            $('.writerImage-modal').html('<img src="/assets/img/default-profile.jpg">');
        }
        $('.writerProfile-modal p').html(review.member.nickname);

        // 리뷰 내용
        $('.content-2 p:eq(0)').text(review.content);
        $('.location-modal span').text(review.location);

        $('#review-detail').modal('show');

    })
    .catch((error) => {
        alert('리뷰를 로드하던 중 오류가 발생하였습니다.');
    })
}

// 리뷰 삭제
const fnDeleteReview = (evt) => {

    let reviewId = $(evt.currentTarget).closest('.review-item').data('review-id');

    axios.put('/review/deleteReview', {
        reviewId: reviewId
    })
    .then((response) => {
        if(response.data.success) {
            // 리뷰 삭제 성공시
            alert(response.data.message);
            window.location.reload();
        }
    })
    .catch((error) => {
        alert('리뷰 삭제에 실패하였습니다.');
    })
}

/******************** 이벤트 **********************/
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

    // 리뷰 수정 페이지로 이동
    location.href = "/review/reviewUpdate.page?reviewId=" + reviewId + '&productId=' + productId;
});