/******************** 함수 **********************/

// 댓글 목록 조회
const fnGetCommentListByMemberId = (page) => {

    axios.get('/comment/getCommentListByMemberId', {
        params: {
            page: page
        }
    })
    .then((response) => {

        let commentList = response.data.commentList;

        if(commentList.contents.length !== 0) {
            fnShowCommentResult(commentList.contents);
            fnSetPaging(commentList.currentPage, commentList.totalPage, commentList.beginPage, commentList.endPage, 'fnGetCommentListByMemberId');
        } else {
            let str = '<div><p>작성한 댓글이 없습니다.</p></div>';
            let container = $('.pagination-container');

            container.empty();
            container.append(str);
        }

    })
}

// 리뷰 목록 조회
const fnGetReviewListByMemberId = (page) => {

    axios.get('/review/getReviewListByMemberId', {
        params: {
            page: page
        }
    })
    .then((response) => {

        let reviewList = response.data.reviewList;

        if(reviewList.contents.length !== 0) {
            fnSortReviewList(reviewList.contents, 'createDt');
            fnShowResult(reviewList.contents);
            fnSetPaging(reviewList.currentPage, reviewList.totalPage, reviewList.beginPage, reviewList.endPage, 'fnGetReviewListByMemberId');
        } else {
            let str = '<div><p>작성한 리뷰가 없습니다.</p></div>';
            let container = $('.pagination-container');

            container.empty();
            container.append(str);
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    })
}

// 댓글 아이템 표시
const fnShowCommentResult = (commentList) => {

    let container = $('.review-title');
    let str = '';

    commentList.forEach((comment) => {

        str += '<div class="comment-item mt-3">';
        str += '<p class="comment-content text-start">' + comment.content + '</p>';
        str += '<div class="comment-wrap mt-2">';
        str += '<div>' + fnFormatDate(comment.createDt) + '</div>';
        str += '<div class="comment-buttons d-flex gap-2" style="">';
        str += '<div class="btn-comment-update">';
        str += '<i class="bi bi-pencil-square"></i>';
        str += '</div>';
        str += '<div class="btn-comment-delete">';
        str += '<i class="bi bi-trash3"></i>';
        str += '</div>';
        str += '</div>';
        str += '</div>';
        str += '</div>';

    });

    $('.comment-item').remove();
    container.after(str);

}

// 리뷰 리스트 조회 데이터 정렬
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

// 리뷰 리스트 조회 결과 표시
const fnShowResult = (reviewList) => {

    let container = $('.review-title');
    let str = '';

    reviewList.forEach((review) => {
        str += '<div class="review-item mt-3" data-review-id="' + review.reviewId + '">';
        str += '<div class="testimonial-item">';
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
        str += '</div>';

    });

    $('.review-item').remove();
    container.after(str);

}

// 페이징 설정 함수
const fnSetPaging = (currentPage, totalPage, beginPage, endPage, fnName) => {

    let pagingContainer = $('.pagination-container');
    let paging = '<ul>';

    if(beginPage === 1) {
        paging += '<li><a href="javascript:void(0);" onclick="return false;"><i class="bi bi-chevron-left"></i></a></li>';
    } else {
        paging += '<li><a href="javascript:void(0);" onclick="' + fnName + '(' + (beginPage - 1) + ')"><i class="bi bi-chevron-left"></i></a></li>';
    }

    for(let i = beginPage; i <= endPage; i++) {
        if(i === currentPage) {
            paging += '<li><a href="javascript:void(0);" class="active" onclick="' + fnName + '(' + i + ')">' + i + '</a></li>';
        } else {
            paging += '<li><a href="javascript:void(0);" onclick="' + fnName + '(' + i + ')">' + i + '</a></li>';
        }
    }

    if(endPage === totalPage) {
        paging += '<li><a href="javascript:void(0);" onclick="return false;"><i class="bi bi-chevron-right"></i></a></li>';
    } else {
        paging += '<li><a href="javascript:void(0);" onclick="' + fnName + '(' + (endPage + 1) + ')"><i class="bi bi-chevron-right"></i></a></li>';
    }

    paging += '</ul>';

    pagingContainer.empty();
    pagingContainer.append(paging);

}

// 메뉴 변경
const fnChangeMenu = (item, type) => {

    $('.selected-menu').removeClass('selected-menu');

    if(type === 'review') {
        $('.comment-item').remove();
    } else {
        $('.review-item').remove();
    }

    item.addClass('selected-menu');
}

// 날짜 포맷
const fnFormatDate = (datetime) => {
    const date = new Date(datetime);
    const now = new Date();
    const diffMs = now - date; // 시간 차이 (밀리초 단위)
    const diffMin = Math.floor(diffMs / 60000);

    if(diffMin < 1) {
        return '방금 전';
    }

    if(diffMin < 60) {
        return diffMin + '분 전';
    }

    if(diffMin < 120) {
        return '1시간 전';
    }

    // 1시간 이상 경과되었을 경우..
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    const hh = String(date.getHours()).padStart(2, '0');
    const mi = String(date.getMinutes()).padStart(2, '0');

    return yyyy + '-' + mm + '-' + dd + ' ' + hh + ':' + mi;
}

/******************** 이벤트 **********************/
window.fnGetReviewListByMemberId = fnGetReviewListByMemberId;
window.fnGetCommentListByMemberId = fnGetCommentListByMemberId;

$(document).ready(() => {
    fnGetReviewListByMemberId(1);
});

$('.btn-write-review').on('click', (evt) => {
    fnChangeMenu($(evt.currentTarget), 'review');
    fnGetReviewListByMemberId(1);
});

$('.btn-write-comment').on('click', (evt) => {
    fnChangeMenu($(evt.currentTarget), 'comment');
    fnGetCommentListByMemberId(1);
});