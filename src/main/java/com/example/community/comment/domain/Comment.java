package com.example.community.comment.domain;

import com.example.community.board.domain.Board;
import com.example.community.comment.exception.BadRequestCommentException;
import com.example.community.common.BaseEntity;
import com.example.community.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    private int deleted;

    private int likeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(String content, Member member, Board board, Comment parent) {
        this.content = content;
        this.member = member;
        this.board = board;
        this.parent = parent;
        this.deleted = 0;
    }

    public void addReply(Comment comment) {
        this.children.add(comment);
        comment.parent = this;
    }

    public void updateContent(String content) {
        if(Objects.isNull(content)) throw new BadRequestCommentException("댓글 내용을 입력해주세요.");
        this.content = content;
    }

    public void deleted() {
        this.deleted = 1;
    }

    public void clickLike() {
        this.likeCount++;
    }

    public void cancelLike() {
        this.likeCount--;
    }
}
