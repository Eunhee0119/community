package com.example.community.comment.repository;

import com.example.community.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {
    List<Comment> getCommentList(Long id);
}
