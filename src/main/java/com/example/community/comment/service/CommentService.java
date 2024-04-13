package com.example.community.comment.service;

import com.example.community.board.domain.Board;
import com.example.community.board.exception.NoSuchBoardException;
import com.example.community.board.repository.BoardRepository;
import com.example.community.comment.domain.Comment;
import com.example.community.comment.domain.CommentLike;
import com.example.community.comment.exception.NoSuchCommentException;
import com.example.community.comment.repository.CommentLikeRepository;
import com.example.community.comment.repository.CommentRepository;
import com.example.community.comment.service.request.CommentCreateServiceRequest;
import com.example.community.comment.service.request.CommentUpdateServiceRequest;
import com.example.community.comment.service.response.CommentResponse;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    private final MemberRepository memberRepository;

    private final BoardRepository boardRepository;

    private final CommentLikeRepository commentLikeRepository;


    @Transactional
    public CommentResponse registerComment(CommentCreateServiceRequest commentRequest, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalIdentifierException("회원 정보가 잘못되었습니다."));

        if (isNull(commentRequest.getBoardId())) throw new NoSuchBoardException("게시글 정보가 잘못되었습니다.");

        Board board = boardRepository.findById(commentRequest.getBoardId())
                .orElseThrow(() -> new NoSuchBoardException("게시글 정보가 잘못되었습니다."));


        Comment comment = commentRequest.toEntity(board, member);

        if (!isNull(commentRequest.getParentId()) && commentRequest.getParentId() > 0) {
            Comment parent = commentRepository.findById(commentRequest.getParentId())
                    .orElseThrow(() -> new NoSuchCommentException("기존 댓글 정보가 잘못되었습니다."));

            parent.addReply(comment);
        }

        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.of(savedComment);
    }

    public List<CommentResponse> getCommentListByBoardId(Long boardId) {
        return CommentResponse.of(commentRepository.getCommentList(boardId));
    }

    @Transactional
    public void updateComment(CommentUpdateServiceRequest updateCommentRequest, String memberEmail) {
        Comment comment = commentRepository.findById(updateCommentRequest.getCommentId())
                .orElseThrow(() -> new NoSuchCommentException("존재하지 않는 댓글입니다."));

        if(!comment.getMember().getEmail().equals(memberEmail)) throw new AccessDeniedException("작성자만 수정할 수 있습니다.");

        comment.updateContent(updateCommentRequest.getContent());
    }

    @Transactional
    public void deletedComment(Long commentId, String memberEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchCommentException("존재하지 않는 댓글입니다."));

        if(!comment.getMember().getEmail().equals(memberEmail)) throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");

        comment.deleted();
    }

    @Transactional
    public int clickCommentLike(Long commentId, String memberEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchCommentException("존재하지 않는 댓글입니다."));

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalIdentifierException("회원 정보가 잘못되었습니다."));

        Optional<CommentLike> like = commentLikeRepository.findByMemberAndComment(member,comment);

        if (!like.isPresent()) {
            comment.clickLike();
            commentLikeRepository.save(new CommentLike(member, comment));
        }
        else {
            comment.cancelLike();
            commentLikeRepository.delete(like.get());
        }

        return comment.getLikeCount();
    }
}
