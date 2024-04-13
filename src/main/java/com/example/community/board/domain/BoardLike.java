package com.example.community.board.domain;

import com.example.community.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardLike {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    // MEMBER_ID

    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;


    public BoardLike(Member member, Board board) {
        this.member = member;
        this.board = board;
    }
}