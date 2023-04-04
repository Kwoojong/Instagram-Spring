package com.numble.instagram.domain.post.service;

import com.numble.instagram.domain.post.entity.Comment;
import com.numble.instagram.domain.post.entity.Post;
import com.numble.instagram.domain.post.repository.CommentRepository;
import com.numble.instagram.domain.user.entity.User;
import com.numble.instagram.exception.badrequest.NotCommentWriterException;
import com.numble.instagram.exception.notfound.CommentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentWriteService {

    private final CommentRepository commentRepository;

    public Comment register(User user, Post post, String content) {
        Comment newComment = Comment.builder()
                .commentWriteUser(user)
                .post(post)
                .content(content)
                .build();
        return commentRepository.save(newComment);
    }

    public Comment edit(User user, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        checkCommentWriter(user, comment);
        comment.updateContent(content);
        return comment;
    }

    private static void checkCommentWriter(User user, Comment comment) {
        if (!comment.isCommentWriteUser(user)) {
            throw new NotCommentWriterException();
        }
    }
}
