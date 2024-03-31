package com.example.util.fixture.board;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.Image;
import com.example.community.category.domain.Category;
import com.example.community.member.domain.Member;

import java.util.List;

public class BoardFixture {

    private final static String DEFAULT_TITLE = "default_title";
    private final static String DEFAULT_CONTENT = "default_content";
    public static Board createDefaultBoard(Category category, Member member) {
        return createCustomBoard(DEFAULT_TITLE,DEFAULT_CONTENT,category,member);
    }

    public static Board createDefaultBoard(Category category, Member member, Image image) {
        return createCustomBoard(DEFAULT_TITLE,DEFAULT_CONTENT,category,member,List.of(image));
    }

    public static Board createDefaultBoard(Category category, Member member, List<Image> images) {
        return createCustomBoard(DEFAULT_TITLE,DEFAULT_CONTENT,category,member,images);
    }

    public static Board createCustomBoard(String title, String content, Category category, Member member) {
        return Board.builder()
                .category(category)
                .title(title)
                .content(content)
                .member(member)
                .hitCnt(0)
                .likeCount(0)
                .build();
    }

    public static Board createCustomBoard(String title, String content, Category category, Member member, List<Image> images) {
        Board board = Board.builder()
                .category(category)
                .title(title)
                .content(content)
                .images(images)
                .member(member)
                .hitCnt(0)
                .likeCount(0)
                .build();

        images.stream().forEach(image ->image.initBoard(board));
        return board;
    }
}
