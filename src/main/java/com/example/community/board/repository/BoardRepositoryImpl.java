package com.example.community.board.repository;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.dto.BoardDto;
import com.example.community.board.domain.dto.BoardSearchDto;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.community.board.domain.QBoard.board;
import static com.example.community.member.domain.QMember.member;
import static io.micrometer.common.util.StringUtils.isEmpty;
import static java.util.Objects.isNull;

public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BoardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Board> findByIdWhitMember(Long boardId) {
        return Optional.ofNullable(queryFactory.select(board)
                .from(board)
                .join(board.member, member)
                .where(board.id.eq(boardId))
                .fetchFirst());
    }

    @Override
    public Page<BoardDto> getSearchBoardList(Pageable page, BoardSearchDto boardSearch) {

        List<BoardDto> content = queryFactory
                .select(Projections.fields(BoardDto.class,
                        board.id
                        , board.title
                        , board.content
                        , board.category.id.as("categoryId")
                        , board.category.name.as("categoryName")
                        , board.member.email.as("writer")
                        , board.hitCnt
                        , board.likeCount))
                .from(board)
                .join(board.member, member)
                .where(
                        titleLike(boardSearch.getTitle())
                        , emailEq(boardSearch.getWriter())
                        , categoryEq(boardSearch.getCategoryId())
                        , betweenDate(boardSearch.getStartDate(), boardSearch.getEndDate())
                ).offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(board.createDateTime.desc())
                .fetch();

        long total = queryFactory
                .select(board.count())
                .from(board)
                .join(board.member, member)
                .where(
                        titleLike(boardSearch.getTitle())
                        , emailEq(boardSearch.getWriter())
                        , categoryEq(boardSearch.getCategoryId())
                        , betweenDate(boardSearch.getStartDate(), boardSearch.getEndDate())
                ).fetchOne();

        return new PageImpl<>(content, page, total);
    }


    private BooleanExpression emailEq(String email) {
        return isEmpty(email) ? null : member.email.eq(email);
    }

    private BooleanExpression titleLike(String title) {
        return isEmpty(title) ? null : board.title.like("%" + title + "%");
    }

    private BooleanExpression categoryEq(Long categoryId) {
        return (isNull(categoryId) || categoryId <= 0) ? null : board.category.id.eq(categoryId);
    }

    private Predicate betweenDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (isNull(startDate) && isNull(endDate)) return null;
        if (!isNull(startDate) && isNull(endDate)) return board.createDateTime.goe(startDate);
        if (isNull(startDate) && !isNull(endDate)) return board.createDateTime.loe(endDate);
        return board.createDateTime.between(startDate, endDate);
    }
}
