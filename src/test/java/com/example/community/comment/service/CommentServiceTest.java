package com.example.community.comment.service;

import com.example.community.board.domain.Board;
import com.example.community.board.exception.NoSuchBoardException;
import com.example.community.board.repository.BoardRepository;
import com.example.community.category.domain.Category;
import com.example.community.category.respository.CategoryRepository;
import com.example.community.comment.domain.Comment;
import com.example.community.comment.exception.BadRequestCommentException;
import com.example.community.comment.exception.NoSuchCommentException;
import com.example.community.comment.repository.CommentRepository;
import com.example.community.comment.service.request.CommentCreateServiceRequest;
import com.example.community.comment.service.request.CommentUpdateServiceRequest;
import com.example.community.comment.service.response.CommentResponse;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.util.fixture.board.BoardFixture.createDefaultBoard;
import static com.example.util.fixture.category.CategoryFixture.createCategory;
import static com.example.util.fixture.comment.CommentFixture.createDefaultComment;
import static com.example.util.fixture.member.MemberFixture.createDefaultMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BoardRepository boardRepository;

    Member member;
    Category category;
    Board board;

    @BeforeEach
    void init() {
        member = memberRepository.save(createDefaultMember());

        category = categoryRepository.save(createCategory("category"));

        board = boardRepository.save(createDefaultBoard(category, member));
    }

    @DisplayName("댓글을 등록한다.")
    @Test()
    void registerCommentTest() {
        //given
        CommentCreateServiceRequest commentRequest = CommentCreateServiceRequest.builder()
                .boardId(board.getId())
                .content("content")
                .build();

        //when
        CommentResponse commentResponse = commentService.registerComment(commentRequest, member.getEmail());


        //then
        assertThat(commentResponse.getId()).isNotNull();
        assertThat(commentResponse).extracting("board.id", "content", "member.id", "deleted")
                .containsExactly(board.getId(), commentRequest.getContent(), member.getId(), 0);

    }

    @DisplayName("댓글 등록 시 게시글이 존재하지 않는 경우 에러가 발생한다.")
    @Test()
    void registerCommentWhenNotExistBoardTest() {
        //given
        CommentCreateServiceRequest commentRequest = CommentCreateServiceRequest.builder()
                .boardId(1000L)
                .content("content")
                .build();

        //when //then
        assertThatThrownBy(() -> commentService.registerComment(commentRequest, member.getEmail()))
                .isInstanceOf(NoSuchBoardException.class)
                .hasMessage("게시글 정보가 잘못되었습니다.");
    }

    @DisplayName("댓글 등록 시 게시글 정보가 없는 경우 에러가 발생한다.")
    @Test()
    void registerCommentWhenNullBoardIdTest() {
        //given
        CommentCreateServiceRequest commentRequest = CommentCreateServiceRequest.builder()
                .content("content")
                .build();

        //when //then
        assertThatThrownBy(() -> commentService.registerComment(commentRequest, member.getEmail()))
                .isInstanceOf(NoSuchBoardException.class)
                .hasMessage("게시글 정보가 잘못되었습니다.");
    }

    @DisplayName("댓글 등록 시 회원이 존재하지 않는 경우 에러가 발생한다.")
    @Test()
    void registerCommentWhenNotExistMemberTest() {
        //given
        CommentCreateServiceRequest commentRequest = CommentCreateServiceRequest.builder()
                .boardId(board.getId())
                .content("content")
                .build();

        //when //then
        assertThatThrownBy(() -> commentService.registerComment(commentRequest, "noExistMember"))
                .isInstanceOf(IllegalIdentifierException.class)
                .hasMessage("회원 정보가 잘못되었습니다.");
    }

    @DisplayName("댓글 등록 시 회원 정보가 없는 경우 에러가 발생한다.")
    @Test()
    void registerCommentWhenNullMemberTest() {
        //given
        CommentCreateServiceRequest commentRequest = CommentCreateServiceRequest.builder()
                .boardId(board.getId())
                .content("content")
                .build();

        //when //then
        assertThatThrownBy(() -> commentService.registerComment(commentRequest, null))
                .isInstanceOf(IllegalIdentifierException.class)
                .hasMessage("회원 정보가 잘못되었습니다.");
    }

    @DisplayName("대댓글을 등록한다.")
    @Test()
    void registerReplyTest() {
        //given
        Comment comment = commentRepository.save(createDefaultComment(board, "content", member));
        CommentCreateServiceRequest commentRequest = CommentCreateServiceRequest.builder()
                .boardId(board.getId())
                .parentId(comment.getId())
                .content("content")
                .build();

        //when
        CommentResponse commentResponse = commentService.registerComment(commentRequest, member.getEmail());


        //then
        assertThat(commentResponse.getId()).isNotNull();
        assertThat(commentResponse).extracting("board.id", "content", "member.id", "deleted")
                .containsExactly(board.getId(), commentRequest.getContent(), member.getId(), 0);
    }

    @DisplayName("대댓글을 등록 시 상위 댓글이 존재하지 않는 경우 에러가 발생한다.")
    @Test()
    void registerReplyWhenNotExistCommentTest() {
        //given
        CommentCreateServiceRequest commentRequest = CommentCreateServiceRequest.builder()
                .boardId(board.getId())
                .parentId(1000L)
                .content("content")
                .build();

        //when //then
        assertThatThrownBy(() -> commentService.registerComment(commentRequest, member.getEmail()))
                .isInstanceOf(NoSuchCommentException.class)
                .hasMessage("기존 댓글 정보가 잘못되었습니다.");
    }


    @DisplayName("게시글 아이디로 댓글 리스트를 부모 아이디, 아이디를 기준으로 조회한다.")
    @Test()
    void getCommentsByBoardIdTest() {
        //given
        Comment comment1 = createDefaultComment(board, "댓글1", member);
        Comment comment2 = createDefaultComment(board, "댓글2", member);
        Comment comment1_1 = createDefaultComment(board, comment1, "댓글111", member);
        Comment comment3 = createDefaultComment(board, "댓글3", member);
        Comment comment2_1 = createDefaultComment(board, comment2, "댓글222222", member);
        Comment comment1_2 = createDefaultComment(board, comment1, "댓글1111111", member);
        commentRepository.saveAll(List.of(comment1, comment2, comment1_1, comment3, comment2_1, comment1_2));

        //when
        List<CommentResponse> comments = commentService.getCommentListByBoardId(board.getId());

        // then
        assertThat(comments).hasSize(3)
                .extracting("id")
                .containsExactly(comment1.getId(), comment2.getId(), comment3.getId());
        assertThat(comments.get(0).getChildren()).hasSize(2)
                .extracting("id")
                .containsExactly(comment1_1.getId(), comment1_2.getId());
    }

    @DisplayName("댓글 리스트 조회 시 댓글이 존재하지 않는 경우 빈 리스트를 반환한다.")
    @Test()
    void getCommentsByBoardIdWhenNoExistCommentTest() {
        //given
        //when
        List<CommentResponse> comments = commentService.getCommentListByBoardId(board.getId());

        // then
        assertThat(comments).hasSize(0);
    }


    @DisplayName("댓글을 수정한다.")
    @Test()
    void updateCommentTest() {
        //given
        Comment comment = createDefaultComment(board, "댓글", member);
        commentRepository.save(comment);

        String updateContent = "댓글 내용 수정";
        CommentUpdateServiceRequest updateCommentRequest = CommentUpdateServiceRequest.builder()
                .commentId(comment.getId())
                .content(updateContent)
                .build();

        //when
        commentService.updateComment(updateCommentRequest, member.getEmail());
        Comment findComment = commentRepository.findById(comment.getId()).get();

        // then
        assertThat(findComment.getContent()).isEqualTo(updateContent);
    }

    @DisplayName("댓글 수정시 수정된 내용을 전달하지 않는 경우 에러가 발생한다.")
    @Test()
    void updateCommentWhenNullContentsTest() {
        //given
        Comment comment = createDefaultComment(board, "댓글", member);
        commentRepository.save(comment);

        CommentUpdateServiceRequest updateCommentRequest = CommentUpdateServiceRequest.builder()
                .commentId(comment.getId())
                .build();

        //when // then
        assertThatThrownBy(() -> commentService.updateComment(updateCommentRequest, member.getEmail()))
                .isInstanceOf(BadRequestCommentException.class)
                .hasMessage("댓글 내용을 입력해주세요.");
    }


    @DisplayName("댓글 수정시 댓글이 존재하지 않는 경우 에러가 발생한다.")
    @Test()
    void updateCommentWhenNoExistCommentTest() {
        //given
        String updateContent = "댓글 내용 수정";
        CommentUpdateServiceRequest updateCommentRequest = CommentUpdateServiceRequest.builder()
                .commentId(100L)
                .content(updateContent)
                .build();

        //when // then
        assertThatThrownBy(() -> commentService.updateComment(updateCommentRequest, member.getEmail()))
                .isInstanceOf(NoSuchCommentException.class)
                .hasMessage("존재하지 않는 댓글입니다.");
    }


    @DisplayName("댓글 수정시 권한이 없는 경우 에러가 발생한다.")
    @Test()
    void updateCommentWhenNoPermissionUserTest() {
        //given
        Member anotherUser = memberRepository.save(createDefaultMember("anotherUser@test.com", "test111!@#"));

        Comment comment = createDefaultComment(board, "댓글", member);
        commentRepository.save(comment);

        CommentUpdateServiceRequest updateCommentRequest = CommentUpdateServiceRequest.builder()
                .commentId(comment.getId())
                .content("댓글 내용 수정")
                .build();

        //when // then
        assertThatThrownBy(() -> commentService.updateComment(updateCommentRequest, anotherUser.getEmail()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("작성자만 수정할 수 있습니다.");
    }


    @DisplayName("댓글을 삭제한다.")
    @Test()
    void deleteCommentTest() {
        //given
        Comment comment = createDefaultComment(board, "댓글", member);
        commentRepository.save(comment);

        //when
        commentService.deletedComment(comment.getId(), member.getEmail());
        Comment findComment = commentRepository.findById(comment.getId()).get();

        // then
        assertThat(findComment.getDeleted()).isEqualTo(1);
    }


    @DisplayName("댓글을 삭제시 댓글이 존재하지 않는 경우 에러가 발생한다.")
    @Test()
    void deleteCommentWhenNotExistCommentTest() {
        //given //when // then
        assertThatThrownBy(() -> commentService.deletedComment(100L, member.getEmail()))
                .isInstanceOf(NoSuchCommentException.class)
                .hasMessage("존재하지 않는 댓글입니다.");
    }


    @DisplayName("댓글을 삭제시 작성자가 아닌 경우 에러가 발생한다.")
    @Test()
    void deleteCommentWhenNoPermissionUserTest() {
        //given
        Member anotherUser = memberRepository.save(createDefaultMember("anotherUser@test.com", "test111!@#"));

        Comment comment = createDefaultComment(board, "댓글", member);
        commentRepository.save(comment);

        //when // then
        assertThatThrownBy(() -> commentService.deletedComment(comment.getId(), anotherUser.getEmail()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("작성자만 삭제할 수 있습니다.");
    }

    @DisplayName("댓글에 좋아요를 누른다.")
    @Test()
    void likeCountUpTest() {
        //given
        Comment comment = createDefaultComment(board, "댓글", member);
        commentRepository.save(comment);

        //when
        commentService.clickCommentLike(comment.getId(), member.getEmail());
        Comment findComment = commentRepository.findById(comment.getId()).get();

        // then
        assertThat(findComment.getLikeCount()).isEqualTo(1);
    }

    @DisplayName("댓글에 좋아요를 두번 누르면 좋아요가 취소된다.")
    @Test()
    void likeCountDownTest() {
        //given
        Comment comment = createDefaultComment(board, "댓글", member);
        commentRepository.save(comment);

        //when
        commentService.clickCommentLike(comment.getId(), member.getEmail());
        commentService.clickCommentLike(comment.getId(), member.getEmail());
        Comment findComment = commentRepository.findById(comment.getId()).get();

        // then
        assertThat(findComment.getLikeCount()).isEqualTo(0);
    }

}