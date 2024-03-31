package com.example.community.board.domain;

import com.example.community.board.exception.UnsupportedImageFormatException;
import com.example.community.board.factory.ImageFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageTest {

    @DisplayName("이미지 파일은 jpg, jpeg, gif, png 확장자만 지원한다.")
    @Test()
    void createImageTest(){
        //given
        String ext1 = ".jpg";
        String ext2 = ".jpeg";
        String ext3 = ".gif";
        String ext4 = ".png";

        //when
        ImageFactory.createImage("image" + ext1);
        ImageFactory.createImage("image" + ext2);
        ImageFactory.createImage("image" + ext3);
        ImageFactory.createImage("image" + ext4);

        //then
        assertTrue(true);
    }

    @DisplayName("이미지 파일은 jpg, jpeg, gif, png 확장자가 아닐 경우 에러가 발생한다.")
    @Test()
    void createImageWhenUnSupportedExtensionTest(){
        //given
        String ext = ".txt";

        //when // then
        Assertions.assertThatThrownBy(()->ImageFactory.createImage("image" + ext))
                .isInstanceOf(UnsupportedImageFormatException.class)
                .hasMessage("지원하지 않는 파일 형식입니다.");
    }

    @DisplayName("이미지 파일을 게시글에 등록한다.")
    @Test()
    void initBoardTest(){
        //given
        String ext1 = ".jpg";
        Image image = ImageFactory.createImage("image" + ext1);
        Board board = Board.builder().build();

        //when
        image.initBoard(board);

        // then
        Assertions.assertThat(image.getBoard()).isEqualTo(board);
    }

    @DisplayName("한번 게시글에 등록된 이미지는 다른 게시글로 변경할 수 없다.")
    @Test()
    void initBoardWhenAlreadyInitTest(){
        //given
        String ext1 = ".jpg";
        Image image = ImageFactory.createImage("image" + ext1);
        Board board = Board.builder().build();
        image.initBoard(board);

        Board board2 = Board.builder().build();

        //when
        image.initBoard(board2);

        // then
        Assertions.assertThat(image.getBoard()).isNotEqualTo(board2);
    }

}