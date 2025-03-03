/******************** 전역 변수 **********************/
let productId = 0;
let globalSort = 'createDt,DESC';


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
        str += '<div class="review-item mt-3">';
        str += '<div class="testimonial-item">';
        str += '<div class="writer-profile">';
        str += '<div class="writer-image">' + review.member.profileImage + '</div>';
        str += '<p class="writer-name">' + review.member.nickname + '</p>';
        str += '</div>';
        str += '<div class="review-main">';
        if(review.reviewImageList.length !== 0) {
            str += '<div class="review-image">' + review.reviewImageList[0].reviewImagePath + '</div>';
        } else {
            str += '<div class="review-image" style="background-color: lightgray"></div>';
        }
        str += '<div class="content-form">';
        str += '<div class="rating mb-1">';
        str += '<h4>맛</h4>';
        str += '<div class="stars">'
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
        str += '<h4>가격</h4>';
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
        str += '</div>';
        str += '<div class="line"></div>';
        str += '<h4 class="review-content mt-3">' + review.content + '</h4>'
        str += '<p class="review-location mt-3"><span>' + review.location + '</span>에서 구매했어요!</p>';
        str += '</div>';
        str += '</div>';
        str += '</div>';
        str += '</div>';

    });

    $('.review-item').remove();
    container.after(str);

}

/******************** 이벤트 **********************/
$(document).ready(() => {

    // 파라미터에서 productId값 가져오기
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
})


