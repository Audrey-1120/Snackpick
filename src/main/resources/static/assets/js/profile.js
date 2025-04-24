/******************** 전역 변수 **********************/
let initComment = '';
let commentModifyForm;
let commentViewForm;

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

        str += '<div class="comment-item mt-3" data-comment-id="' + comment.commentId + '">';
        str += '<div class="comment-modify-form" style="display: none;">';
        str += '<textarea class="comment-input w-100" placeholder="수정 댓글 입력해주세요." maxlength="150">' + comment.content + '</textarea>';
        str += '<div class="comment-update-wrap">';
        str += '<button type="button" class="btn-comment-save">저장</button>';
        str += '<button type="button" class="btn-comment-undo">취소</button>';
        str += '</div>';
        str += '</div>';
        str += '<div class="comment-view-form">';
        str += '<p class="comment-content text-start">' + comment.content + '</p>';
        str += '<div class="comment-wrap mt-2">';
        str += '<div>' + fnFormatDate(comment.createDt) + '</div>';
        str += '<div class="comment-buttons" style="">';
        str += '<div class="btn-comment-update">';
        str += '<i class="bi bi-pencil-square"></i>';
        str += '</div>';
        str += '<div class="btn-comment-delete">';
        str += '<i class="bi bi-trash3"></i>';
        str += '</div>';
        str += '</div>';
        str += '</div>';
        str += '</div>';
        str += '</div>';
    });

    $('.comment-item').remove();
    container.after(str);

}

// 댓글 수정
const fnUpdateComment = (item) => {

    let content = commentModifyForm.find('.comment-input')
    let commentId = item.closest('.comment-item').data('comment-id');

    if(content.val().trim() === '') {
        alert('댓글을 입력해주세요.');
        return;
    }

    if(content.val().trim() === initComment) {
        alert('댓글이 수정되었습니다.');
        commentModifyForm.find('.comment-input').val(initComment);
        commentViewForm.show();
        commentModifyForm.hide();
        return;
    }

    const commentDTO = {
        commentId: commentId,
        content: content.val().trim()
    }

    axios.put('/comment/updateComment', commentDTO,
        {
            headers: {
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': fnGetCsrfToken()
            }
        })
    .then((response) => {
        let data = response.data;
        if(data.success) {
            alert(data.message);
            fnChangeCommentForm(data.comment.content, 'success');
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    });

}

// 댓글 삭제
const fnDeleteComment = (item) => {

    let commentItem = item.closest('.comment-item');
    let commentId = commentItem.data('commentId');

    const commentDTO = {
        commentId: commentId
    }

    axios.put('/comment/deleteComment', commentDTO,
        {
            headers: {
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': fnGetCsrfToken()
            }
        })
    .then((response) => {
        if(response.data.success) {
            alert('댓글이 삭제되었습니다.');
            $('.review-item').remove();
            fnGetCommentListByMemberId(1);
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    });
}

// 댓글 폼 변경
const fnChangeCommentForm = (item, type) => {

    let targetButton;

    if(type === 'update') {

        targetButton = item.closest('.comment-buttons');

        // 이 외의 수정 및 삭제 버튼 안보이게
        $('.comment-buttons').not(targetButton).css('display', 'none');

        // viewForm에서 원래 댓글 내용 가져오기
        initComment = item.parents('.comment-wrap').prev().text();

        // 조회폼, 수정폼 가져오기
        commentViewForm = item.closest('.comment-view-form');
        commentModifyForm = commentViewForm.siblings('.comment-modify-form');

        // 조회폼, 수정폼 각각 표시
        commentViewForm.hide();
        commentModifyForm.show();

    } else if(type === 'undo') {

        targetButton = item.closest('.comment-buttons');

        // 이 외의 수정 및 삭제 버튼 보이게
        $('.comment-buttons').not(targetButton).css('display', 'flex');

        // 조회폼, 수정폼 각각 표시
        commentViewForm.show();
        commentModifyForm.hide();

        commentModifyForm.find('.comment-input').val(initComment);

    } else if(type === 'success') {

        // 수정 버튼 다시 다 보이게
        $('.comment-buttons').css('display', 'flex');

        // 여기서는 item = response.data.comment.content
        commentViewForm.find('.comment-content').text(item)
        commentModifyForm.find('.comment-input').val(item);

        commentViewForm.show();
        commentModifyForm.hide();
    }

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
        str += '<div class="review-item mt-3" data-review-id="' + review.reviewId + '" data-product-id="' + review.productId + '">';
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

// 리뷰 삭제
const fnDeleteReview = (item) => {

    let reviewId = item.closest('.review-item').data('review-id');

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

// csrf 토큰 가져오기
const fnGetCsrfToken = () => {
    return $('meta[name="csrf-token"]').attr('content');
}

/******************** 이벤트 **********************/
window.fnGetReviewListByMemberId = fnGetReviewListByMemberId;
window.fnGetCommentListByMemberId = fnGetCommentListByMemberId;

$(document).ready(() => {
    fnGetReviewListByMemberId(1);
});

// 작성한 리뷰 조회
$('.btn-write-review').on('click', (evt) => {
    fnChangeMenu($(evt.currentTarget), 'review');
    fnGetReviewListByMemberId(1);
});

// 작성한 리뷰 수정 페이지 이동
$(document).on('click', '.btn-update', (evt) => {
    evt.stopPropagation();
    let item = $(evt.currentTarget).closest('.review-item');

    let reviewId = item.data('review-id');
    let productId = item.data('product-id');
    location.href = "/review/reviewUpdate.page?reviewId=" + reviewId + '&productId=' + productId;
});

// 작성한 리뷰 삭제
$(document).on('click', '.btn-delete', (evt) => {
    evt.stopPropagation();
    if(confirm("리뷰를 삭제하시겠습니까?")) {
        fnDeleteReview($(evt.currentTarget));
    }
});

// 작성한 댓글 조회
$('.btn-write-comment').on('click', (evt) => {
    fnChangeMenu($(evt.currentTarget), 'comment');
    fnGetCommentListByMemberId(1);
});

// 댓글 수정란 표시
$(document).on('click', '.btn-comment-update', (evt) => {
    fnChangeCommentForm($(evt.currentTarget), 'update');
});

// 댓글 수정
$(document).on('click', '.btn-comment-save', (evt) => {
    fnUpdateComment($(evt.currentTarget));
});

// 댓글 수정 취소
$(document).on('click', '.btn-comment-undo', (evt) => {
    fnChangeCommentForm($(evt.currentTarget), 'undo');
});

// 작성한 댓글 삭제
$(document).on('click', '.btn-comment-delete', (evt) => {
    evt.stopPropagation();
    if(confirm("댓글을 삭제하시겠습니까?")) {
        fnDeleteComment($(evt.currentTarget));
    }
});