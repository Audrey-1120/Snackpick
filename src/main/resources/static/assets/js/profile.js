import * as comment from './comment.js';

/******************** 전역 변수 **********************/
let initComment = '';
let commentModifyForm;
let commentViewForm;

/******************** 함수 **********************/``

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

const fnShowCommentResult = (commentList) => {

    let container = $('.review-title');
    let str = '';

    commentList.forEach((comment) => {

        str += '<div class="comment-item mt-3" data-comment-id="' + comment.commentId + '">';
        str += '<div class="comment-modify-form" style="display: none;">';
        str += '<textarea class="comment-input w-100" placeholder="수정할 댓글 입력해주세요." maxlength="150">' + comment.content + '</textarea>';
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
            $('.comment-item').remove();
            fnGetCommentListByMemberId(1);
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    });
}

const fnChangeCommentForm = (item, type) => {

    let targetButton;

    if(type === 'update') {

        targetButton = item.closest('.comment-buttons');

        $('.comment-buttons').not(targetButton).css('display', 'none');

        initComment = item.parents('.comment-wrap').prev().text();

        commentViewForm = item.closest('.comment-view-form');
        commentModifyForm = commentViewForm.siblings('.comment-modify-form');

        commentViewForm.hide();
        commentModifyForm.show();

    } else if(type === 'undo') {

        targetButton = item.closest('.comment-buttons');

        $('.comment-buttons').not(targetButton).css('display', 'flex');

        commentViewForm.show();
        commentModifyForm.hide();
        commentModifyForm.find('.comment-input').val(initComment);

    } else if(type === 'success') {

        $('.comment-buttons').css('display', 'flex');

        commentViewForm.find('.comment-content').text(item)
        commentModifyForm.find('.comment-input').val(item);

        commentViewForm.show();
        commentModifyForm.hide();
    }

}

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
        str += '<h4 class="review-content">' + review.content + '</h4>';
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
            comment.insertDeleteParentComment();

            $('#review-detail').modal('show');
        })
        .catch((error) => {
            alert(error.response.data.message);
        })
}

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

const fnCheckPassword = () => {

    let password = $('.input-password').val().trim();

    if(password === '') {
        alert('비밀번호를 입력해주세요.');
        return;
    }

    axios.post('/member/checkPassword', {
        password: password
    })
    .then((response) => {
        if(response.data.isMatch) {
            location.href='/member/profile-update.page';
        } else {
            alert('비밀번호가 일치하지 않습니다.');
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    });
}

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

const fnChangeMenu = (item, type) => {

    $('.selected-menu').removeClass('selected-menu');

    if(type === 'review') {
        $('.comment-item').remove();
    } else {
        $('.review-item').remove();
    }

    item.addClass('selected-menu');
}

const fnFormatDate = (datetime) => {
    const date = new Date(datetime);
    const now = new Date();
    const diffMs = now - date;
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

    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    const hh = String(date.getHours()).padStart(2, '0');
    const mi = String(date.getMinutes()).padStart(2, '0');

    return yyyy + '-' + mm + '-' + dd + ' ' + hh + ':' + mi;
}

const fnGetCsrfToken = () => {
    return $('meta[name="csrf-token"]').attr('content');
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

$(document).on('click', '.btn-update', (evt) => {
    evt.stopPropagation();
    let item = $(evt.currentTarget).closest('.review-item');

    let reviewId = item.data('review-id');
    let productId = item.data('product-id');
    location.href = "/review/reviewUpdate.page?reviewId=" + reviewId + '&productId=' + productId;
});

$(document).off('click', '.review-item').on('click', '.review-item', (evt) => {
    evt.stopPropagation();
    fnGetReviewDetail(evt);
});

$(document).on('click', '.btn-delete', (evt) => {
    evt.stopPropagation();
    if(confirm("리뷰를 삭제하시겠습니까?")) {
        fnDeleteReview($(evt.currentTarget));
    }
});

$('.btn-write-comment').on('click', (evt) => {
    fnChangeMenu($(evt.currentTarget), 'comment');
    fnGetCommentListByMemberId(1);
});

$(document).on('click', '.btn-comment-update', (evt) => {
    fnChangeCommentForm($(evt.currentTarget), 'update');
});

$(document).on('click', '.btn-comment-save', (evt) => {
    fnUpdateComment($(evt.currentTarget));
});

$(document).on('click', '.btn-comment-undo', (evt) => {
    fnChangeCommentForm($(evt.currentTarget), 'undo');
});

$(document).on('click', '.btn-comment-delete', (evt) => {
    evt.stopPropagation();
    if(confirm("댓글을 삭제하시겠습니까?")) {
        fnDeleteComment($(evt.currentTarget));
    }
});

$('.btn-check-password').on('click', () => {
    fnCheckPassword();
});