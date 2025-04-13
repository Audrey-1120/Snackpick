/******************** 전역 변수 **********************/
let initComment = '';

/******************** 함수 **********************/
// 리뷰 댓글 데이터 화면에 표시
const fnShowCommentList = (commentList) => {

    let loginId = $('.login-id').val();
    let commentContainer = $('.content-3');
    let writerName = $('.writerImage-modal ~ p').text();

    let str = ''

    if(commentList.length !== 0) {

        commentList.forEach((comment) => {

            if(comment.depth === 0) {
                str += '<div class="comment-modal" data-group-id="' + comment.groupId + '" data-comment-id="' + comment.commentId + '">';
            } else {
                str += '<div class="comment-modal reply" data-group-id="' + comment.groupId + '" data-comment-id="' + comment.commentId + '">';
            }
            str += '<div class="comment-writer-section">';
            if(comment.member.profileImage !== null) {
                str += '<div class="comment-writer"><img src="' + comment.member.profileImage + '"></div>';
            } else {
                str += '<div class="comment-writer"><img src="/assets/img/default-profile.jpg"></div>';
            }
            str += '</div>';
            str += '<div class="w-100">';
            str += '<div class="comment-profile">';
            if(comment.member.nickname === writerName) {
                str += '<p style="color: #D9232D;">글쓴이</p>';
            } else {
                str += '<p>' + comment.member.nickname + '</p>';
            }
            str += '<p>' + fnFormatDate(comment.createDt) + '</p>';
            str += '</div>';

            if(loginId !== undefined) {
                if(comment.member.memberId === Number(loginId)) {
                    if(comment.depth === 0) {
                        str += '<div class="comment-content"><p class="w-75">' + comment.content + '</p>';
                        str += '<div class="d-flex gap-1">';
                        str += '<p class="btn-reply" style="align-content: end;">댓글달기</p>';
                        str += '<div style="align-content: end">';
                        str += '<i class="fa-solid fa-pen-to-square icon-comment-update"></i> | ';
                        str += '<i class="fa-solid fa-trash icon-comment-delete"></i>';
                        str += '</div>';
                        str += '</div>';
                        str += '</div>';
                    } else {
                        str += '<div class="comment-content">';
                        str += '<p class="w-75">' + comment.content + '</p>';
                        str += '<div style="align-content: end">';
                        str += '<i class="fa-solid fa-pen-to-square icon-comment-update"></i>';
                        str += ' | ';
                        str += '<i class="fa-solid fa-trash icon-comment-delete"></i>';
                        str += '</div>';
                        str += '</div>';
                    }
                } else {

                    if(comment.depth === 0) {
                        str += '<div class="comment-content"><p class="w-75">' + comment.content + '</p><p class="btn-reply">댓글달기</p></div>';
                    } else {
                        str += '<div class="comment-content"><p class="w-75">' + comment.content + '</p></div>';
                    }
                }
            } else {
                str += '<div class="comment-content"><p class="w-75">' + comment.content + '</p></div>';
            }
            str += '</div>';
            str += '</div>';
        });

    } else {

        str += '<div class="no-comment">';
        str += '<p>댓글이 없습니다.</p>';
        str += '</div>';
    }

    commentContainer.empty();
    commentContainer.html(str);
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
    const mm = String(date.getMonth()).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    const hh = String(date.getHours()).padStart(2, '0');
    const mi = String(date.getMinutes()).padStart(2, '0');

    return yyyy + '-' + mm + '-' + dd + ' ' + hh + ':' + mi;
}

// 대댓글 선택
const fnSelectReply = (commentItem, isSelected) => {

    let commentInput = $('.comment-input input');

    fnChangeCommentBtn('insert');

    if(isSelected) {

        let comment = commentItem.closest('.comment-modal');

        $('.comment-modal').not(comment).removeClass('selected-comment');
        comment.addClass('selected-comment');
        commentInput.val('');
        commentInput.attr('placeholder', '대댓글을 입력해주세요');

    } else {

        $('.selected-comment').removeClass('selected-comment');
        commentInput.val('');
        commentInput.attr('placeholder', '댓글을 입력해주세요');
    }
}

// csrf 토큰 가져오기
const fnGetCsrfToken = () => {
    return $('meta[name="csrf-token"]').attr('content');
}

// 작성 댓글 표시
const fnShowComment = (comment) => {

    let commentArea = $('.content-3');
    let isReply = comment.depth === 0;
    let writerName = $('.writerImage-modal ~ p').text();

    let commentContainer
        = isReply ? commentArea : $('[data-group-id="' + comment.groupId + '"]').last();

    let loginId = $('.login-id').val();

    let str = '';
    if(isReply) {
        str += '<div class="comment-modal" data-group-id="' + comment.groupId + '" data-comment-id="' + comment.commentId + '">';
    } else {
        str += '<div class="comment-modal reply" data-group-id="' + comment.groupId + '" data-comment-id="' + comment.commentId + '">';
    }

    str += '<div class="comment-writer-section">';
    if(comment.member.profileImage !== null) {
        str += '<div class="comment-writer"><img src="' + comment.member.profileImage + '"></div>';
    } else {
        str += '<div class="comment-writer"><img src="/assets/img/default-profile.jpg"></div>';
    }
    str += '</div>';
    str += '<div class="w-100">';
    str += '<div class="comment-profile">';
    if(comment.member.nickname === writerName) {
        str += '<p style="color: #D9232D;">글쓴이</p>';
    } else {
        str += '<p>' + comment.member.nickname + '</p>';
    }
    str += '<p>' + fnFormatDate(comment.createDt) + '</p>';
    str += '</div>';

    if(loginId !== undefined) {

        if(comment.member.memberId === Number(loginId)) {

            if(comment.depth === 0) {
                str += '<div class="comment-content"><p class="w-75">' + comment.content + '</p>';
                str += '<div class="d-flex gap-1">';
                str += '<p class="btn-reply" style="align-content: end;">댓글달기</p>';
                str += '<div style="align-content: end">';
                str += '<i class="fa-solid fa-pen-to-square icon-comment-update"></i> | ';
                str += '<i class="fa-solid fa-trash icon-comment-delete"></i>';
                str += '</div>';
                str += '</div>';
                str += '</div>';
            } else {
                str += '<div class="comment-content">';
                str += '<p class="w-75">' + comment.content + '</p>';
                str += '<div style="align-content: end">';
                str += '<i class="fa-solid fa-pen-to-square icon-comment-update"></i>';
                str += ' | ';
                str += '<i class="fa-solid fa-trash icon-comment-delete"></i>';
                str += '</div>';
                str += '</div>';
            }
        } else {

            if(comment.depth === 0) {
                str += '<div class="comment-content"><p class="w-75">' + comment.content + '</p><p class="btn-reply">댓글달기</p></div>';
            } else {
                str += '<div class="comment-content"><p class="w-75">' + comment.content + '</p></div>';
            }
        }
    } else {
        str += '<div class="comment-content"><p class="w-75">' + comment.content + '</p></div>';
    }
    str += '</div>';
    str += '</div>';

    if(isReply) {
        commentContainer.append(str);
        commentArea.scrollTop(commentArea[0].scrollHeight);
    } else {
        commentContainer.after(str);
    }

    $('.no-comment').remove();

    $('.selected-comment').removeClass('selected-comment');
    $('.comment-input input').val('');

}

// 댓글 작성
const fnWriteComment = (selectedComment) => {

    const isReply = selectedComment.length !== 0;

    let content = $('.comment-input input').val().trim();
    let reviewId = $('.review-id').val();

    if(content === '') {
        alert('댓글을 입력해주세요.');
        return;
    }

    const commentDTO = {
        reviewId,
        content,
        depth: isReply ? 1 : 0,
        groupId: isReply ? selectedComment.data('commentId') : undefined,
        state: false
    }

    axios.post('/comment/insertComment', commentDTO,
        {
            headers: {
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': fnGetCsrfToken()
            }
        })
    .then((response) => {
        if(response.data.success) {
            fnShowComment(response.data.comment);
        }
    })
    .catch((error) => {
        alert(response.data.message);
    });
}

// 댓글 수정 입력 표시
const fnSetUpdateComment = (selectedComment) => {

    let commentId = selectedComment.closest('.comment-modal').data('commentId');
    let content = selectedComment.closest('.comment-content').find('p').first().text();
    let commentInput = $('.comment-input input');

    $('.selected-comment').removeClass(('selected-comment'));
    fnChangeCommentBtn('update');
    commentInput.val(content);
    commentInput.attr('placeholder', '수정할 댓글을 입력해주세요.');

    initComment = content;

    $('.update-comment-id').val(commentId);
}

// 댓글 버튼 타입 변경
const fnChangeCommentBtn = (type) => {
    if(type === 'insert') {
        $('.btn-comment').css('display', '');
        $('.btn-comment-update').css('display', 'none');
    } else {
        $('.btn-comment').css('display', 'none');
        $('.btn-comment-update').css('display', '');
    }
}

// 수정한 댓글 표시
const fnShowUpdateComment = (comment) => {

    let commentId = comment.commentId;
    let commentContent = $('[data-comment-id="' + commentId + '"]').find('.comment-content p').first();
    let commentInput = $('.comment-input input');

    commentContent.text(comment.content);
    commentInput.val('');
    commentInput.attr('placeholder', '댓글을 입력해주세요.');

    fnChangeCommentBtn('insert');
}

// 댓글 수정
const fnUpdateComment = () => {

    let content = $('.comment-input input');
    let commentId = $('.update-comment-id').val();

    if(content.val().trim() === '') {
        alert('댓글을 입력해주세요.');
        return;
    }

    if(content.val().trim() === initComment) {
        alert('댓글이 수정되었습니다.');
        content.val('');
        content.attr('placeholder', '댓글을 작성해주세요.');
        fnChangeCommentBtn('insert');
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
        if(response.data.success) {
            alert('댓글이 수정되었습니다.');
            fnShowUpdateComment(response.data.comment);
        }
    })
    .catch(() => {
        alert(response.data.message);
    });
}

// 댓글 삭제
const fnDeleteComment = (evt) => {

    let commentId = evt.closest('.comment-modal').data('commentId');

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
            evt.closest('.comment-modal').remove();
            if($('.comment-modal').length === 0) {
                let str = '<div class="no-comment">';
                str += '<p>댓글이 없습니다.</p>';
                str += '</div>';
                $('.content-3').append(str);
            }
        }
    })
    .catch(() => {
        alert(response.data.message);
    });
}

/******************** 이벤트 **********************/
// 대댓글 달 원글 선택
$(document).on('click', '.btn-reply', (evt) => {
    evt.stopPropagation();
    fnSelectReply($(evt.currentTarget), true);
});

// 취소 버튼 클릭 시 일반 댓글 창 표시
$('.btn-comment-undo').on('click', (evt) => {
    evt.stopPropagation();
    fnSelectReply($(evt.currentTarget), false);
})

// 댓글 작성
$('.btn-comment').on('click', () => {
    fnWriteComment($('.selected-comment'));
});

// 댓글 수정 input 표시
$(document).on('click', '.icon-comment-update', (evt) => {
    evt.stopPropagation();
    fnSetUpdateComment($(evt.currentTarget));
});

// 댓글 수정
$('.btn-comment-update').on('click', () => {
    fnUpdateComment();
});

// 댓글 삭제
$(document).on('click', '.icon-comment-delete', (evt) => {
    evt.stopPropagation();
    if(confirm("댓글을 삭제하시겠습니까?")) {
        fnDeleteComment($(evt.currentTarget));
    }
})

export { fnShowCommentList, fnShowComment };