package com.example.community.board.service;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.Image;
import com.example.community.board.domain.dto.BoardDto;
import com.example.community.board.domain.dto.BoardSearchDto;
import com.example.community.board.exception.NoSuchBoardException;
import com.example.community.board.exception.UnsupportedImageFormatException;
import com.example.community.board.factory.ImageFactory;
import com.example.community.board.repository.BoardRepository;
import com.example.community.board.repository.ImageRepository;
import com.example.community.board.service.request.BoardCreateServiceRequest;
import com.example.community.board.service.request.BoardUpdateServiceRequest;
import com.example.community.board.service.response.BoardResponse;
import com.example.community.category.domain.Category;
import com.example.community.category.exception.BadRequestCategoryException;
import com.example.community.category.exception.NoSuchCategoryFoundException;
import com.example.community.category.respository.CategoryRepository;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import com.example.util.fixture.board.BoardFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.util.fixture.board.BoardFixture.createCustomBoard;
import static com.example.util.fixture.board.BoardFixture.createDefaultBoard;
import static com.example.util.fixture.category.CategoryFixture.createCategory;
import static com.example.util.fixture.member.MemberFixture.createDefaultMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BoardServiceTest {
    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MemberRepository memberRepository;

    Category category;

    Member member;

    @BeforeEach
    void beforeEach() {
        category = createCategory("루트카테고리");
        categoryRepository.save(category);
        member = createDefaultMember();
        memberRepository.save(member);
    }


    @DisplayName("게시글을 등록한다.")
    @Test()
    void registerBoard() {
        //given
        String title = "테스트 글 작성";
        String content = "테스트 글 내용 작성";
        BoardCreateServiceRequest boardRequest = BoardCreateServiceRequest.builder()
                .categoryId(category.getId())
                .title(title)
                .content(content)
                .writer(member.getEmail())
                .build();
        //when
        BoardResponse boardResponse = boardService.registerBoard(boardRequest);

        //then
        assertThat(boardResponse.getId()).isNotNull();
        assertThat(boardResponse).extracting("title", "content", "writer", "hitCount", "likeCount")
                .containsExactly(title, content, member.getEmail(), 0, 0);
    }


    @DisplayName("게시글을 등록 시 카테고리 아이디가 없을 경우 에러가 발생한다.")
    @Test()
    void registerBoardWhenNotContainCategoryIdTest() {
        //given
        String title = "테스트 글 작성";
        String content = "테스트 글 내용 작성";
        BoardCreateServiceRequest boardRequest = BoardCreateServiceRequest.builder()
                .categoryId(null)
                .title(title)
                .content(content)
                .writer(member.getEmail())
                .build();

        //when //then
        assertThatThrownBy(() -> boardService.registerBoard(boardRequest))
                .isInstanceOf(BadRequestCategoryException.class)
                .hasMessage("카테고리를 선택해주세요.");
    }


    @DisplayName("존재하지 않는 카테고리에 게시글을 등록할 경우 에러가 발생한다.")
    @Test()
    void registerBoardWhenNoExistCategoryTest() {
        //given
        String title = "테스트 글 작성";
        String content = "테스트 글 내용 작성";
        Long noExistCategoryId = 10000L;
        BoardCreateServiceRequest boardRequest = BoardCreateServiceRequest.builder()
                .categoryId(noExistCategoryId)
                .title(title)
                .content(content)
                .writer(member.getEmail())
                .build();

        //when //then
        assertThatThrownBy(() -> boardService.registerBoard(boardRequest))
                .isInstanceOf(NoSuchCategoryFoundException.class)
                .hasMessage("존재하지 않는 카테고리입니다.");
    }


    @DisplayName("게시글 등록 시 회원 정보가 없을 경우 에러가 발생한다.")
    @Test()
    void registerBoardWhitOutMemberInfo() {
        //given
        String title = "테스트 글 작성";
        String content = "테스트 글 내용 작성";
        Long noExistCategoryId = 10000L;
        BoardCreateServiceRequest boardRequest = BoardCreateServiceRequest.builder()
                .categoryId(noExistCategoryId)
                .title(title)
                .content(content)
                .build();

        //when //then
        assertThatThrownBy(() -> boardService.registerBoard(boardRequest))
                .hasMessage("회원 정보가 잘못되었습니다.");
    }


    @DisplayName("이미지가 포함된 게시글을 등록한다.")
    @Test()
    void registerBoardWhitImage() {
        //given
        String title = "테스트 글 작성";
        String content = "테스트 글 내용 작성";

        MockMultipartFile imageFile = new MockMultipartFile("image1", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "Hello, World!".getBytes());

        BoardCreateServiceRequest boardRequest = BoardCreateServiceRequest.builder()
                .categoryId(category.getId())
                .title(title)
                .content(content)
                .images(List.of(imageFile))
                .writer(member.getEmail())
                .build();

        //when
        BoardResponse boardResponse = boardService.registerBoard(boardRequest);

        // then
        assertThat(boardResponse.getId()).isNotNull();
        assertThat(boardResponse.getImages()).hasSize(1);
    }


    @DisplayName("이미지가 여러개 포함된 게시글을 등록한다.")
    @Test()
    void registerBoardWhitImage2() {
        //given
        String title = "테스트 글 작성";
        String content = "테스트 글 내용 작성";

        MockMultipartFile imageFile1 = new MockMultipartFile("image1", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "Hello, World!".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile("image2", "image2.jpg", MediaType.IMAGE_JPEG_VALUE, "Hello, World!".getBytes());

        BoardCreateServiceRequest boardRequest = BoardCreateServiceRequest.builder()
                .categoryId(category.getId())
                .title(title)
                .content(content)
                .images(List.of(imageFile1, imageFile2))
                .writer(member.getEmail())
                .build();

        //when
        BoardResponse boardResponse = boardService.registerBoard(boardRequest);

        // then
        assertThat(boardResponse.getId()).isNotNull();
        assertThat(boardResponse.getImages()).hasSize(2);
        assertThat(boardResponse.getImages().get(0).getSaveName()).isNotNull();
        assertThat(boardResponse.getImages().get(0).getOriginName()).isNotNull();
    }




    @DisplayName("게시글 상세 페이지를 조회한다.")
    @Test()
    void readBoardDetailTest() {
        //given
        Image image1 = ImageFactory.createImage("image1.jpg");
        Image image2 = ImageFactory.createImage("image2.jpg");
        List<Image> images = List.of(image1, image2);

        Board board = createDefaultBoard(category, member, images);
        Board savedBoard = boardRepository.save(board);
        imageRepository.saveAll(images);

        //when
        BoardResponse response = boardService.getBoardDetails(savedBoard.getId());

        //then
        assertThat(response).extracting("categoryId", "writer","hitCount")
                .containsExactly(category.getId(), member.getEmail(),1);
        assertThat(response.getImages().size()).isEqualTo(2);
    }


    @DisplayName("게시글 상세 페이지를 조회한다.")
    @Test()
    void readBoardDetailWithHitCountUpTest() {
        //given
        Board board = createDefaultBoard(category, member);
        Board savedBoard = boardRepository.save(board);

        //when  //then
        BoardResponse response = boardService.getBoardDetails(savedBoard.getId());
        assertThat(response.getHitCount()).isEqualTo(1);

        BoardResponse response2 = boardService.getBoardDetails(savedBoard.getId());
        assertThat(response2.getHitCount()).isEqualTo(2);

        BoardResponse response3 = boardService.getBoardDetails(savedBoard.getId());
        assertThat(response3.getHitCount()).isEqualTo(3);
    }


    @DisplayName("존재하지 않는 게시글 상세 페이지를 조회할 경우 에러가 발생한다.")
    @Test()
    void readBoardDetailWhenNoExistBoardTest() {
        assertThatThrownBy(() -> boardService.getBoardDetails(1000L))
                .isInstanceOf(NoSuchBoardException.class)
                .hasMessage("존재하지 않는 게시글입니다.");
    }


    @DisplayName("게시글에 좋아요를 누르면 좋아요 수가 증가한다.")
    @Test()
    void likeCountUpTest() {
        Member another = createDefaultMember("anotherUser@test.com", "password");
        Member anotherMember = memberRepository.save(another);

        Board board = createDefaultBoard(category, member);
        Board savedBoard = boardRepository.save(board);

        //when // then
        int likeCount = boardService.clickBoardLikeCount(savedBoard.getId(), member.getEmail());
        assertThat(likeCount).isEqualTo(1);


        int likeCount2 = boardService.clickBoardLikeCount(savedBoard.getId(), anotherMember.getEmail());
        assertThat(likeCount2).isEqualTo(2);
    }

    @DisplayName("게시글에 좋아요를 두번 누르면 좋아요가 취소된다.")
    @Test()
    void likeCountDownTest() {
        Board board = createDefaultBoard(category, member);
        Board savedBoard = boardRepository.save(board);

        //when // then
        int likeCount1 = boardService.clickBoardLikeCount(savedBoard.getId(), member.getEmail());
        assertThat(likeCount1).isEqualTo(1);
        int likeCount2 = boardService.clickBoardLikeCount(savedBoard.getId(), member.getEmail());
        assertThat(likeCount2).isEqualTo(0);
    }


    @DisplayName("게시글 제목과 내용을 수정한다.")
    @Test()
    void updateBoardDetailTest() throws IOException {
        //given
        String fileName ="image";
        String ext = ".jpg";
        Image image = saveImageFile(fileName, ext);
        List<Image> images = List.of(image);

        Board board = createDefaultBoard(category, member, images);
        Board savedBoard = boardRepository.save(board);
        imageRepository.saveAll(images);

        String updateTitle = "updateTitle";
        String updateContent = "updateTitle";
        BoardUpdateServiceRequest updateRequest = BoardUpdateServiceRequest.builder()
                .categoryId(category.getId())
                .title(updateTitle)
                .content(updateContent)
                .writer(member.getEmail())
                .build();

        boardService.updateBoards(savedBoard.getId(), updateRequest, member.getEmail());

        Board updateBoard = boardRepository.findById(savedBoard.getId()).get();
        assertThat(updateBoard).extracting("title", "content", "category.id", "member.email")
                .containsExactly(updateTitle, updateContent, category.getId(), member.getEmail());
        assertThat(updateBoard.getImages()).hasSize(1);
        assertThat(updateBoard.getImages().get(0).getOriginName()).isEqualTo(fileName + ext);
    }


    @DisplayName("게시글의 카테고리와 이미지를 수정한다.")
    @Test()
    void updateBoardCategoryAndImagesTest() throws IOException {
        Category updatePositionCategory = createCategory("게시글 위치를 변경할 카테고리", category);
        categoryRepository.save(updatePositionCategory);

        Image deleteImage = saveImageFile("image1", ".jpg");
        Image image = saveImageFile("image2", ".jpg");
        List<Image> images = List.of(deleteImage, image);

        Board board = createDefaultBoard(category, member,images);
        Board savedBoard = boardRepository.save(board);

        String addFileName = "addImage";
        String addFileExt = ".jpg";
        MockMultipartFile addImageFile = new MockMultipartFile(addFileName, addFileName +addFileExt, MediaType.IMAGE_JPEG_VALUE, "fileTest!!".getBytes());

        BoardUpdateServiceRequest updateRequest = BoardUpdateServiceRequest.builder()
                .categoryId(updatePositionCategory.getId())
                .title("title")
                .content("content")
                .addedImages(List.of(addImageFile))
                .deletedImages(List.of(deleteImage.getId()))
                .writer(member.getEmail())
                .build();

        boardService.updateBoards(savedBoard.getId(), updateRequest, member.getEmail());

        Board updateBoard = boardRepository.findById(savedBoard.getId()).get();
        assertThat(updateBoard).extracting("title", "category.id", "member.email")
                .containsExactly(board.getTitle(), updatePositionCategory.getId(), member.getEmail());
        assertThat(updateBoard.getImages()).hasSize(2)
                .extracting("originName")
                .containsExactly(image.getOriginName(),addFileName+addFileExt);
    }


    @DisplayName("게시글을 수정할 때 존재하지 않는 게시글일 경우 에러가 발생한다.")
    @Test()
    void updateBoardWithNotExistBoardTest() throws IOException {
        BoardUpdateServiceRequest updateRequest = BoardUpdateServiceRequest.builder()
                .categoryId(category.getId())
                .title("title")
                .content("content")
                .writer(member.getEmail())
                .build();

        assertThatThrownBy(()->boardService.updateBoards(100000L, updateRequest, member.getEmail()))
                .isInstanceOf(NoSuchBoardException.class)
                .hasMessage("존재하지 않는 게시글입니다.");
    }


    @DisplayName("게시글의 카테고리를 수정할 때 존재하지 않는 카테고리일 경우 에러가 발생한다.")
    @Test()
    void updateBoardWithNotExistCategoryTest() throws IOException {
        Board board = createDefaultBoard(category, member);
        Board savedBoard = boardRepository.save(board);

        BoardUpdateServiceRequest updateRequest = BoardUpdateServiceRequest.builder()
                .categoryId(10000L)
                .title("title")
                .content("content")
                .writer(member.getEmail())
                .build();

        assertThatThrownBy(()->boardService.updateBoards(savedBoard.getId(), updateRequest, member.getEmail()))
                .isInstanceOf(NoSuchCategoryFoundException.class)
                .hasMessage("존재하지 않는 카테고리입니다.");
    }


    @DisplayName("게시글을 수정할 때 본인의 글이 아닌 경우 에러가 발생한다.")
    @Test()
    void updateBoardWhenAccessDeniedExceptionTest() throws IOException {
        Member another = createDefaultMember("anotherUser@test.com", "password");
        Member anotherMember = memberRepository.save(another);

        Board board = createDefaultBoard(category, member);
        Board savedBoard = boardRepository.save(board);

        BoardUpdateServiceRequest updateRequest = BoardUpdateServiceRequest.builder()
                .categoryId(category.getId())
                .title("title")
                .content("content")
                .writer(anotherMember.getEmail())
                .build();

        assertThatThrownBy(()->boardService.updateBoards(savedBoard.getId(), updateRequest, anotherMember.getEmail()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("게시글 수정 권한이 없습니다.");
    }


    @DisplayName("게시글의 이미지를 수정할 때 지원하지 않은 포멧일 경우 에러가 발생한다.")
    @Test()
    void updateBoardWhitUnsupportedImageFormatTest() throws IOException {
        Board board = createDefaultBoard(category, member);
        Board savedBoard = boardRepository.save(board);

        String addFileName = "addFile";
        String addFileExt = ".txt";
        MockMultipartFile addImageFile = new MockMultipartFile(addFileName, addFileName +addFileExt, MediaType.IMAGE_JPEG_VALUE, "fileTest!!".getBytes());

        BoardUpdateServiceRequest updateRequest = BoardUpdateServiceRequest.builder()
                .categoryId(category.getId())
                .title("title")
                .content("content")
                .addedImages(List.of(addImageFile))
                .writer(member.getEmail())
                .build();

        assertThatThrownBy(()->boardService.updateBoards(savedBoard.getId(), updateRequest, member.getEmail()))
                .isInstanceOf(UnsupportedImageFormatException.class)
                .hasMessage("지원하지 않는 파일 형식입니다.");
    }




    @DisplayName("게시글을 삭제한다.")
    @Test()
    void deleteBoard() {
        //given
        Board board = createDefaultBoard(category, member);
        Board savedBoard = boardRepository.save(board);

        //when
        boardService.deleteBoard(savedBoard.getId(), member.getId());
        Optional<Board> findBoard = boardRepository.findById(savedBoard.getId());

        //then
        assertThat(findBoard.isEmpty()).isTrue();
    }


    @DisplayName("이미지가 포함된 게시글을 삭제한다.")
    @Test()
    void deleteBoardWhitImages() throws IOException {
        //given
        String fileName = "image" + LocalDateTime.now();
        Image image = saveImageFile(fileName,".jpg");

        Board board = BoardFixture.createDefaultBoard(category, member, image);

        Board savedBoard = boardRepository.save(board);
        imageRepository.save(image);

        //when
        boardService.deleteBoard(savedBoard.getId(), member.getId());
        Optional<Board> findBoard = boardRepository.findById(savedBoard.getId());

        //then
        assertThat(findBoard.isEmpty()).isTrue();
    }


    @DisplayName("존재하지 않는 게시글을 삭제할 경우 에러가 발생한다.")
    @Test()
    void deleteBoardWhenNoExistTest() {
        assertThatThrownBy(() -> boardService.deleteBoard(1000L, member.getId()))
                .isInstanceOf(NoSuchBoardException.class)
                .hasMessage("존재하지 않는 게시글입니다.");
    }


    @DisplayName("권한이 없는 게시글을 삭제할 경우 에러가 발생한다.")
    @Test()
    void deleteBoardWhenNoPermissionTest() {
        //given
        Board board = createDefaultBoard(category, member);
        Board savedBoard = boardRepository.save(board);

        Member anotherMember = memberRepository.save(createDefaultMember("newMember", "password"));

        //when//then
        assertThatThrownBy(() -> boardService.deleteBoard(savedBoard.getId(), anotherMember.getId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("게시글 삭제 권한이 없습니다.");
    }




    @DisplayName("게시글 리스트를 조회한다.")
    @Test()
    void getBoardList() {
        //given
        List<Board> boards = IntStream.rangeClosed(0, 15).mapToObj(n ->
                createCustomBoard("title", "content", category, member)
        ).collect(Collectors.toList());

        boardRepository.saveAll(boards);

        //when
        BoardSearchDto searchDto = BoardSearchDto.builder().build();
        Pageable pageable = Pageable.ofSize(10);
        Page<BoardDto> boardList = boardService.getBoardList(pageable, searchDto);

        //then
        assertThat(boardList.getTotalPages()).isEqualTo(2);
        assertThat(boardList.getContent().get(0)).extracting("title", "content", "categoryId", "writer")
                .containsExactly("title", "content", category.getId(), member.getEmail());
    }


    @DisplayName("게시글이 하나도 없을 때 게시글 리스트를 조회하면 빈 리스트를 반환한다.")
    @Test()
    void getBoardListWhenNoExistBoard() {
        //given
        //when
        BoardSearchDto searchDto = BoardSearchDto.builder().build();
        Pageable pageable = Pageable.ofSize(10);
        Page<BoardDto> boardList = boardService.getBoardList(pageable, searchDto);

        //then
        assertThat(boardList.getTotalPages()).isEqualTo(0);
        assertThat(boardList.getContent().size()).isEqualTo(0);
    }


    @DisplayName("조회 조건을 추가하여 게시글 리스트를 조회한다.")
    @Test()
    void getBoardListWhenPage() {
        //given
        List<Board> boards = IntStream.rangeClosed(0, 15).mapToObj(n ->
                createCustomBoard("title" + n, "content", category, member)
        ).collect(Collectors.toList());
        boardRepository.saveAll(boards);

        //when
        BoardSearchDto searchDto = BoardSearchDto.builder().title("title1").build();
        Pageable pageable = Pageable.ofSize(10);
        Page<BoardDto> boardList = boardService.getBoardList(pageable, searchDto);

        //then
        assertThat(boardList.getTotalPages()).isEqualTo(1);
        assertThat(boardList.getContent().size()).isEqualTo(7);
    }


    private Image saveImageFile(String fileName, String ext) throws IOException {
        MockMultipartFile imageFile = new MockMultipartFile(fileName, fileName + ext, MediaType.IMAGE_JPEG_VALUE, "fileTest!!".getBytes());
        Image image = ImageFactory.createImage(fileName+ext);
        imageFile.transferTo(new File("src/test/resources/files/" + image.getSaveName()));
        return image;
    }
}