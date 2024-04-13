package com.example.community.comment.domain;

import com.example.community.board.domain.Board;
import com.example.community.category.domain.Category;
import com.example.community.member.domain.Member;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.util.fixture.board.BoardFixture.createDefaultBoard;
import static com.example.util.fixture.category.CategoryFixture.createCategory;
import static com.example.util.fixture.comment.CommentFixture.createDefaultComment;
import static com.example.util.fixture.member.MemberFixture.createDefaultMember;
import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    Member member;

    Category category;
    Board board;

    @BeforeEach
    void beforeEach() {
        member = createDefaultMember();
        category = createCategory("category");
        board = createDefaultBoard(category, member);
    }

    @DisplayName("대댓글을 작성한다.")
    @Test()
    void addReplyTest() {
        //given
        Comment parent = createDefaultComment(board, "parent", member);

        //when
        Comment reply = createDefaultComment(board, "reply", member);
        parent.addReply(reply);

        //then
        assertThat(reply.getParent()).isEqualTo(parent);
        assertThat(parent.getChildren()).hasSize(1)
                .extracting("id", "content")
                .containsExactlyInAnyOrder(Tuple.tuple(reply.getId(), reply.getContent()));

    }

    @DisplayName("댓글을 수정한다.")
    @Test()
    void updateContentTest() {
        //given
        Comment comment = createDefaultComment(board, "comment", member);
        String updateContent = "updateContent";

        //when
        comment.updateContent(updateContent);

        //then
        assertThat(comment.getContent()).isEqualTo(updateContent);
    }

    @DisplayName("댓글을 삭제한다.")
    @Test()
    void deletedTest() {
        //given
        Comment comment = createDefaultComment(board, "comment", member);

        //when
        comment.deleted();

        //then
        assertThat(comment.getDeleted()).isEqualTo(1);
    }

    @DisplayName("댓글에 좋아요를 누른다.")
    @Test()
    void likeCountUpTest() {
        //given
        Comment comment = createDefaultComment(board, "comment", member);

        //when
        comment.clickLike();

        //then
        assertThat(comment.getLikeCount()).isEqualTo(1);
    }

    @DisplayName("댓글에 좋아요를 취소한다.")
    @Test()
    void likeCountDownTest() {
        //given
        Comment comment = createDefaultComment(board, "comment", member);
        comment.clickLike();

        //when
        comment.cancelLike();

        //then
        assertThat(comment.getLikeCount()).isEqualTo(0);
    }
}