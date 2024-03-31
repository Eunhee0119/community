package com.example.community.category.domain;

import com.example.community.category.exception.BadRequestCategoryException;
import com.example.community.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category parent;

    private final static String namePattern = "^[a-zA-Z가-힣][a-zA-Z가-힣0-9\\s]{1,20}$";

    @Builder
    private Category(String name, Category parent) {
        validCategoryName(name);

        this.name = name;
        this.parent = parent;
        if(!Objects.isNull(parent)) this.depth = parent.getDepth()+1;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changePosition(Category parent) {
        this.parent = parent;
        this.depth = parent.getDepth()+1;
    }

    public static void validCategoryName(String name){
        if(!name.matches(namePattern)) throw new BadRequestCategoryException("카테고리명은 영문,한글로 시작하며 영문,숫자,한글을 포함하여 20자를 넘어갈 수 없습니다.");
    }
}
