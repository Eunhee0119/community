package com.example.community.comment.repository;

import com.example.community.board.domain.Board;
import com.example.community.board.repository.BoardRepository;
import com.example.community.category.domain.Category;
import com.example.community.category.respository.CategoryRepository;
import com.example.community.comment.domain.Comment;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.util.fixture.board.BoardFixture.createDefaultBoard;
import static com.example.util.fixture.category.CategoryFixture.createCategory;
import static com.example.util.fixture.comment.CommentFixture.createDefaultComment;
import static com.example.util.fixture.member.MemberFixture.createDefaultMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@Transactional
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BoardRepository boardRepository;

    private Member member;
    private Category category;
    private Board board;

    @BeforeEach
    void init() {
        member = createDefaultMember();
        memberRepository.save(member);

        category = createCategory("category");
        categoryRepository.save(category);

        board = createDefaultBoard(category, member);
        boardRepository.save(board);
    }

    @DisplayName("댓글을 등록한다.")
    @Test()
    void registerCommentsTest(){
        //given
        Comment comment =  createDefaultComment(board,  "댓글 등록 테스트",member);

        //when
        Comment savedComment = commentRepository.save(comment);

        //then
        assertThat(savedComment.getId()).isNotNull();
    }

    @DisplayName("게시글 아이디로 댓글 리스트를 부모 아이디, 아이디를 기준으로 조회한다.")
    @Test()
    void getCommentTest(){
        //given
        Comment comment1 = createDefaultComment(board,  "댓글1", member);
        Comment comment2 = createDefaultComment(board,  "댓글2", member);
        Comment comment1_1 = createDefaultComment(board, comment1,"댓글111", member);
        Comment comment3 = createDefaultComment(board,  "댓글3", member);
        Comment comment2_1 = createDefaultComment(board, comment2, "댓글222222", member);
        Comment comment1_2 = createDefaultComment(board, comment1, "댓글1111111", member);

        commentRepository.saveAll(List.of(comment1,comment2,comment1_1,comment3,comment2_1,comment1_2));

        //when
        List<Comment> comments = commentRepository.getCommentList(board.getId());

        //then
        assertThat(comments).extracting("id","parent.id")
                .containsExactly(tuple(comment1.getId(),null)
                        ,tuple(comment2.getId(),null)
                        ,tuple(comment3.getId(),null)
                        ,tuple(comment1_1.getId(),comment1.getId())
                        ,tuple(comment1_2.getId(),comment1.getId())
                        ,tuple(comment2_1.getId(),comment2.getId()));
    }


}