package com.example.community.init;

import com.example.community.board.domain.Board;
import com.example.community.board.repository.BoardRepository;
import com.example.community.category.domain.Category;
import com.example.community.category.respository.CategoryRepository;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component("jpaInitData")
@RequiredArgsConstructor
public class JpaInitData implements InitData{

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final BoardRepository boardRepository;

    @Override
    public void init() {
        System.out.println("========JpaInitData=======");
        List<Member> members = initMember();
        List<Category> categories = initCategory();
        List<Board> boards = initBoardCount(members.get(0), categories.get(0));
    }


    private List<Member> initMember() {
        List<Member> members = IntStream.range(0, INSERT_MEMBER_COUNT)
                .mapToObj(n -> Member.builder()
                        .email("test" + n + "@test.com")
                        .password("testPassword123!@#")
                        .name("test" + n).build()).toList();
        memberRepository.saveAll(members);
        return members;
    }

    private List<Category> initCategory() {
        List<Category> categories = IntStream.range(0, INSERT_CATEGORY_COUNT)
                .mapToObj(n -> Category.builder()
                        .name("category"+n).build()).toList();
        categoryRepository.saveAll(categories);
        return categories;
    }

    private List<Board> initBoardCount(Member member, Category category) {
        List<Board> boards = IntStream.range(0, INSERT_BOARD_COUNT)
                .mapToObj(n -> Board.builder()
                        .category(category)
                        .title("title"+n)
                        .content("content"+n)
                        .member(member)
                        .build()).toList();
        boardRepository.saveAll(boards);
        return boards;
    }
}
