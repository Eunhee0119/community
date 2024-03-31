package com.example.community.board.repository;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.dto.BoardDto;
import com.example.community.board.domain.dto.BoardSearchDto;
import com.example.community.category.domain.Category;
import com.example.community.category.respository.CategoryRepository;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.util.fixture.category.CategoryFixture.createCategory;
import static com.example.util.fixture.member.MemberFixture.createDefaultMember;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BoardRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    BoardRepository boardRepository;

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

    @DisplayName("게시글 아이디로 게시글과 작성자를 조회한다.")
    @Test()
    void findByIdWhitMemberTest(){
        //given
        String email = member.getEmail();
        Board board = Board.builder().category(category).member(member).build();
        Board savedBoard = boardRepository.save(board);
        em.flush();
        em.clear();

        //when
        Board findBoard = boardRepository.findByIdWhitMember(savedBoard.getId()).get();

        //then
        assertThat(findBoard).isNotNull();
        assertThat(findBoard.getMember().getEmail()).isEqualTo(email);
    }

    @DisplayName("게시글 리스트를 조회한다.")
    @Test()
    void getSearchBoardList(){
        //given
        List<Board> list = IntStream.range(1, 12).mapToObj(n -> Board.builder().title("title" + n).category(category).member(member).build()).collect(toList());
        boardRepository.saveAll(list);

        BoardSearchDto searchDto = BoardSearchDto.builder().build();

        //when
        Page<BoardDto> searchBoardList = boardRepository.getSearchBoardList(Pageable.ofSize(10), searchDto );

        //then
        assertThat(searchBoardList.getTotalPages()).isEqualTo(2);
        assertThat(searchBoardList.getContent().get(0)).extracting("title","categoryId","writer")
                .containsExactly("title11",category.getId(),member.getEmail());
    }

    @DisplayName("작성자를 조건으로 게시글 리스트를 조회한다.")
    @Test()
    void getSearchBoardListWhitSearchMember(){
        //given
        String email = member.getEmail();

        Member another = createDefaultMember("anotherUser@test.com", "password");
        Member anotherMember = memberRepository.save(another);

        List<Board> list1 = IntStream.rangeClosed(1, 5).mapToObj(n -> Board.builder().title("title" + n).category(category).member(member).build()).collect(toList());
        List<Board> list2 = IntStream.rangeClosed(1, 10).mapToObj(n -> Board.builder().title("another_title" + n).category(category).member(anotherMember).build()).collect(toList());
        boardRepository.saveAll(list1);
        boardRepository.saveAll(list2);

        BoardSearchDto searchDto = BoardSearchDto.builder().writer(email).build();

        //when
        Page<BoardDto> searchBoardList = boardRepository.getSearchBoardList(Pageable.ofSize(10), searchDto);

        //then
        assertThat(searchBoardList.getTotalPages()).isEqualTo(1);
        assertThat(searchBoardList.getContent().size()).isEqualTo(5);
        assertThat(searchBoardList.getContent().get(0)).extracting("title","writer")
                .containsExactly("title5",member.getEmail());
    }

    @DisplayName("카테고리를 조건으로 게시글 리스트를 조회한다.")
    @Test()
    void getSearchBoardListWhitSearchCategory(){
        //given
        Category searchCategory = categoryRepository.save(createCategory("조회카테고리", category));

        List<Board> list1 = IntStream.rangeClosed(1, 5).mapToObj(n -> Board.builder().title("category1" + n).category(category).member(member).build()).collect(toList());
        List<Board> list2 = IntStream.rangeClosed(1, 15).mapToObj(n -> Board.builder().title("category2" + n).category(searchCategory).member(member).build()).collect(toList());
        boardRepository.saveAll(list1);
        boardRepository.saveAll(list2);

        BoardSearchDto searchDto = BoardSearchDto.builder().categoryId(searchCategory.getId()).build();

        //when
        Page<BoardDto> searchBoardList = boardRepository.getSearchBoardList(Pageable.ofSize(10), searchDto);

        //then
        assertThat(searchBoardList.getTotalPages()).isEqualTo(2);
        assertThat(searchBoardList.getContent().size()).isEqualTo(10);
        assertThat(searchBoardList.getContent().get(0)).extracting("categoryId","categoryName")
                .containsExactly(searchCategory.getId(),searchCategory.getName());
    }

    @DisplayName("제목를 조건으로 게시글 리스트를 조회한다.")
    @Test()
    void getSearchBoardListWhitSearchTitle(){
        //given
        List<Board> list1 = IntStream.rangeClosed(1, 15).mapToObj(n -> Board.builder().title("title" + n).category(category).member(member).build()).collect(toList());
        boardRepository.saveAll(list1);

        BoardSearchDto searchDto = BoardSearchDto.builder().title("title1").build();

        //when
        Page<BoardDto> searchBoardList = boardRepository.getSearchBoardList(Pageable.ofSize(10), searchDto);

        //then
        assertThat(searchBoardList.getTotalPages()).isEqualTo(1);
        assertThat(searchBoardList.getContent().size()).isEqualTo(7);
        assertThat(searchBoardList.getContent().get(0).getTitle()).isEqualTo("title15");
    }

    @DisplayName("작성일을 조건으로 게시글 리스트를 조회한다.")
    @Test()
    void getSearchBoardListWhitSearchDate1(){
        //given
        List<Board> list1 = IntStream.rangeClosed(1, 5).mapToObj(n -> Board.builder().title("title" + n).category(category).member(member).build()).collect(toList());
        List<Board> boards = boardRepository.saveAll(list1);
        boards.stream().forEach(b-> ReflectionTestUtils.setField(b,"createDateTime", LocalDateTime.now().minusDays(3), LocalDateTime.class));

        List<Board> list2 = IntStream.rangeClosed(1, 5).mapToObj(n -> Board.builder().title("title" + n).category(category).member(member).build()).collect(toList());
        boardRepository.saveAll(list2);
        em.flush();
        em.clear();

        BoardSearchDto searchDto = BoardSearchDto.builder().startDate(LocalDateTime.now().minusDays(1)).build();

        //when
        Page<BoardDto> searchBoardList = boardRepository.getSearchBoardList(Pageable.ofSize(10), searchDto);

        //then
        assertThat(searchBoardList.getTotalPages()).isEqualTo(1);
        assertThat(searchBoardList.getContent().size()).isEqualTo(5);
    }

    @DisplayName("작성일을 조건으로 게시글 리스트를 조회한다.")
    @Test()
    void getSearchBoardListWhitSearchDate2(){
        //given
        List<Board> list1 = IntStream.rangeClosed(1, 5).mapToObj(n -> Board.builder().title("title" + n).category(category).member(member).build()).collect(toList());
        List<Board> boards = boardRepository.saveAll(list1);
        boards.stream().forEach(b-> ReflectionTestUtils.setField(b,"createDateTime", LocalDateTime.now().minusDays(3), LocalDateTime.class));

        List<Board> list2 = IntStream.rangeClosed(1, 5).mapToObj(n -> Board.builder().title("title" + n).category(category).member(member).build()).collect(toList());
        boardRepository.saveAll(list2);
        em.flush();
        em.clear();

        BoardSearchDto searchDto = BoardSearchDto.builder().startDate(LocalDateTime.now().minusDays(4)).endDate(LocalDateTime.now().minusDays(1)).build();

        //when
        Page<BoardDto> searchBoardList = boardRepository.getSearchBoardList(Pageable.ofSize(10), searchDto);

        //then
        assertThat(searchBoardList.getTotalPages()).isEqualTo(1);
        assertThat(searchBoardList.getContent().size()).isEqualTo(5);
    }
}