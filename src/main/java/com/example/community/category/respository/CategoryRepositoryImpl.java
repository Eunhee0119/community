package com.example.community.category.respository;

import com.example.community.category.domain.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.example.community.category.domain.QCategory.category;

public class CategoryRepositoryImpl implements CategoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    public CategoryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Category> findAllOrderByParentIdAsc() {
        return queryFactory
                .selectFrom(category)
                .leftJoin(category.parent)
                .orderBy(
                        category.parent.id.asc().nullsFirst(),
                        category.createDateTime.asc()
                )
                .fetch();
    }
}
