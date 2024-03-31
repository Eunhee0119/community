package com.example.community.board.domain;

import com.example.community.category.domain.Category;
import com.example.community.common.BaseEntity;
import com.example.community.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    private String title;

    @Lob
    private String content;

    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private int hitCnt = 0;
    private int likeCount = 0;


    @Builder
    public Board(Category category, String title, String content, List<Image> images, Member member, int hitCnt, int likeCount) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.member = member;
        this.hitCnt = hitCnt;
        this.likeCount = likeCount;

        if(!Objects.isNull(images)) addImages(images);
    }


    public void changeCategory(Category category) {
        this.category = category;
    }

    public void changeDetails(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addImages(List<Image> addImages) {
        addImages.forEach(image->{
            this.images.add(image);
            image.initBoard(this);
        });
    }

    public void deleteImages(List<Image> deletedImages) {
        this.images.removeAll(deletedImages);
    }

    public void hitCountUp() {
        this.hitCnt++;
    }

    public void likeCountUp() {
        this.likeCount++;
    }

    public void likeCountDown() {
        this.likeCount--;
    }
}
