/******************** 전역 변수 **********************/
let initComment = '';

/******************** 함수 **********************/

const fnShowCommentList = (commentList) => {

    let loginId = $('.login-id').val();
    let commentContainer = $('.content-3');
    let writerName = $('.writerImage-modal ~ p').text();
    let commentInput = $('.comment-input input');

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

    fnChangeCommentBtn('insert');
    commentInput.val('');
    commentInput.attr('placeholder', '댓글을 입력해주세요');

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

const fnGetCsrfToken = () => {
    return $('meta[name="csrf-token"]').attr('content');
}

const fnShowComment = (comment) => {

    let commentArea = $('.content-3');
    let isReply = comment.depth === 1;
    let writerName = $('.writerImage-modal ~ p').text();
    let commentInput = $('.comment-input input');

    let commentContainer
        = isReply ? $('[data-group-id="' + comment.groupId + '"]').last() : commentArea;

    let loginId = $('.login-id').val();

    let str = '';
    if(isReply) {
        str += '<div class="comment-modal reply" data-group-id="' + comment.groupId + '" data-comment-id="' + comment.commentId + '">';
    } else {
        str += '<div class="comment-modal" data-group-id="' + comment.groupId + '" data-comment-id="' + comment.commentId + '">';
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
        commentContainer.after(str);
    } else {
        commentContainer.append(str);
        commentArea.scrollTop(commentArea[0].scrollHeight);
    }

    $('.no-comment').remove();
    $('.selected-comment').removeClass('selected-comment');
    commentInput.val('');
    commentInput.attr('placeholder', '댓글을 입력해주세요');

}

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
        groupId: isReply ? selectedComment.data('groupId') : undefined,
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
        alert(error.response.data.message);
    });
}

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

const fnChangeCommentBtn = (type) => {
    if(type === 'insert') {
        $('.btn-comment').css('display', '');
        $('.btn-comment-update').css('display', 'none');
    } else {
        $('.btn-comment').css('display', 'none');
        $('.btn-comment-update').css('display', '');
    }
}

const fnShowUpdateComment = (comment) => {

    let commentId = comment.commentId;
    let commentContent = $('[data-comment-id="' + commentId + '"]').find('.comment-content p').first();
    let commentInput = $('.comment-input input');

    commentContent.text(comment.content);
    commentInput.val('');
    commentInput.attr('placeholder', '댓글을 입력해주세요.');

    fnChangeCommentBtn('insert');
}

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
            alert(response.data.message);
            fnShowUpdateComment(response.data.comment);
        } else {
            alert(response.data.message);
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    });
}

const fnHandleDeleteComment = (comment) => {

    let isReply = comment.hasClass('reply');
    let groupId = comment.data('groupId');

    if(!isReply) {

        let deleteParent = '<div class="comment-modal deleted-parent" data-group-id="' + groupId + '" data-comment-id="deleted-' + groupId + '">';

        deleteParent += '<div class="comment-writer-section">';
        deleteParent += '<div class="comment-writer">';
        deleteParent += '<img src="/assets/img/default-profile.jpg">';
        deleteParent += '</div>';
        deleteParent += '</div>';
        deleteParent += '<div class="w-100">';
        deleteParent += '<div class="comment-profile">';
        deleteParent += '<p class="text-muted">알 수 없음</p>';
        deleteParent += '<p class="text-muted">--</p>';
        deleteParent += '</div>';
        deleteParent += '<div class="comment-content">';
        deleteParent += '<p class="text-muted">삭제된 댓글입니다.</p>';
        deleteParent += '</div>';
        deleteParent += '</div>';
        deleteParent += '</div>';

        comment.after(deleteParent);
        comment.remove();

    } else {
        comment.remove();
    }
}

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
            fnHandleDeleteComment(evt.closest('.comment-modal'));
        }
    })
    .catch((error) => {
        alert(error.response.data.message);
    });
}

const insertDeleteParentComment = () => {

    let allCommentList = $('.comment-modal');

    let group = _.groupBy(allCommentList, (item) => {
        return $(item).data('groupId');
    });

    _.forEach(group, (commentList, groupId) => {

        let hasParent = commentList.some((item) => {
            return !$(item).hasClass('reply');
        });

        if(!hasParent) {
            let firstReply = $(commentList[0]);

            let deleteParent = '<div class="comment-modal deleted-parent" data-group-id="' + groupId + '" data-comment-id="deleted-' + groupId + '">';
            deleteParent += '<div class="comment-writer-section">';
            deleteParent += '<div class="comment-writer">';
            deleteParent += '<img src="/assets/img/default-profile.jpg">';
            deleteParent += '</div>';
            deleteParent += '</div>';
            deleteParent += '<div class="w-100">';
            deleteParent += '<div class="comment-profile">';
            deleteParent += '<p class="text-muted">알 수 없음</p>';
            deleteParent += '<p class="text-muted">--</p>';
            deleteParent += '</div>';
            deleteParent += '<div class="comment-content">';
            deleteParent += '<p class="text-muted">삭제된 댓글입니다.</p>';
            deleteParent += '</div>';
            deleteParent += '</div>';
            deleteParent += '</div>';

            firstReply.before(deleteParent);
        }
    });
}

/******************** 이벤트 **********************/
$(document).on('click', '.btn-reply', (evt) => {
    evt.stopPropagation();
    fnSelectReply($(evt.currentTarget), true);
});

$('.btn-comment-undo').on('click', (evt) => {
    evt.stopPropagation();
    fnSelectReply($(evt.currentTarget), false);
});

$('.btn-comment').on('click', () => {
    fnWriteComment($('.selected-comment'));
});

$(document).on('click', '.icon-comment-update', (evt) => {
    evt.stopPropagation();
    fnSetUpdateComment($(evt.currentTarget));
});

$('.btn-comment-update').on('click', () => {
    fnUpdateComment();
});

$(document).on('click', '.icon-comment-delete', (evt) => {
    evt.stopPropagation();
    if(confirm("댓글을 삭제하시겠습니까?")) {
        fnDeleteComment($(evt.currentTarget));
    }
});

export { fnShowCommentList, fnShowComment, insertDeleteParentComment };