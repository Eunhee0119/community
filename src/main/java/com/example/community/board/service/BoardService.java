package com.example.community.board.service;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.BoardLike;
import com.example.community.board.domain.Image;
import com.example.community.board.domain.dto.BoardDto;
import com.example.community.board.domain.dto.BoardSearchDto;
import com.example.community.board.exception.NoSuchBoardException;
import com.example.community.board.factory.ImageFactory;
import com.example.community.board.repository.BoardRepository;
import com.example.community.board.repository.ImageRepository;
import com.example.community.board.repository.BoardLikeRepository;
import com.example.community.board.service.request.BoardCreateServiceRequest;
import com.example.community.board.service.request.BoardUpdateServiceRequest;
import com.example.community.board.service.response.BoardListResponse;
import com.example.community.board.service.response.BoardResponse;
import com.example.community.category.domain.Category;
import com.example.community.category.exception.BadRequestCategoryException;
import com.example.community.category.exception.NoSuchCategoryFoundException;
import com.example.community.category.respository.CategoryRepository;
import com.example.community.common.file.service.FileService;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final BoardLikeRepository boardLikeRepository;

    private final FileService fileService;

    @Transactional
    public BoardResponse registerBoard(BoardCreateServiceRequest boardRequest) {
        Member member = memberRepository.findByEmail(boardRequest.getWriter())
                .orElseThrow(() -> new IllegalIdentifierException("회원 정보가 잘못되었습니다."));

        if(isNull(boardRequest.getCategoryId()) || boardRequest.getCategoryId() < 1){
            throw new BadRequestCategoryException("카테고리를 선택해주세요.");
        }

        Category category = categoryRepository.findById(boardRequest.getCategoryId())
                .orElseThrow(() -> new NoSuchCategoryFoundException("존재하지 않는 카테고리입니다."));

        List<Image> images = uploadImages(boardRequest.getImages());
        Board savedBoard = boardRepository.save(boardRequest.toEntity(member,category,images));
        imageRepository.saveAll(savedBoard.getImages());

        return BoardResponse.of(savedBoard);
    }

    @Transactional
    public BoardResponse getBoardDetails(Long id) {
        Board board = boardRepository.findByIdWhitMember(id).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 게시글입니다."));
        board.hitCountUp();
        return BoardResponse.of(board);
    }


    @Transactional
    public void updateBoards(Long boardId, BoardUpdateServiceRequest updateRequest, String memberEmail) {
        Board board = boardRepository.findByIdWhitMember(boardId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 게시글입니다."));
        if(!board.getMember().getEmail().equals(memberEmail)) throw new AccessDeniedException("게시글 수정 권한이 없습니다.");

        if(board.getCategory().getId() != updateRequest.getCategoryId()){
            Category category = categoryRepository.findById(updateRequest.getCategoryId())
                    .orElseThrow(() -> new NoSuchCategoryFoundException("존재하지 않는 카테고리입니다."));
            board.changeCategory(category);
        }

        board.changeDetails(updateRequest.getTitle(),updateRequest.getContent());

        if(!isNull(updateRequest.getDeletedImages()) && updateRequest.getDeletedImages().size() != 0){
            List<Image> deletedImages = imageRepository.findAllById(updateRequest.getDeletedImages());
            deleteImageFiles(deletedImages);
            board.deleteImages(deletedImages);
            imageRepository.deleteAll(deletedImages);
        }

        List<Image> images = uploadImages(updateRequest.getAddedImages());
        board.addImages(images);
        imageRepository.saveAll(images);
    }


    @Transactional
    public void deleteBoard(Long boardId, String memberEmail) {
        Board board = getAuthorizedBoard(boardId, memberEmail);
        List<Image> images = board.getImages();
        boardRepository.delete(board);

        images.stream().forEach(image->fileService.delete(image.getOriginName()));
        imageRepository.deleteAll(images);
    }


    @Transactional
    public int clickBoardLikeCount(Long boardId, String memberEmail) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 게시글입니다."));

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalIdentifierException("회원 정보가 잘못되었습니다."));

        Optional<BoardLike> like = boardLikeRepository.findByMemberAndBoard(member, board);
        if (!like.isPresent()) {
            board.likeCountUp();
            boardLikeRepository.save(new BoardLike(member, board));
        }
        else {
            board.likeCountDown();
            boardLikeRepository.delete(like.get());
        }
        return board.getLikeCount();
    }


    public BoardListResponse getBoardList(Pageable page, BoardSearchDto searchDto) {
        Page<BoardDto> pageBoard = boardRepository.getSearchBoardList(page, searchDto);

        List<BoardResponse> boards = pageBoard.getContent().stream().map(BoardResponse::of).collect(Collectors.toList());
        BoardListResponse boardListResponse = new BoardListResponse(boards, pageBoard.getTotalPages());

        return boardListResponse;
    }

    private List<Image> uploadImages(List<MultipartFile> fileImages) {
        List<Image> images = ImageFactory.createImages(fileImages);
        IntStream.range(0, images.size()).forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getSaveName()));
        return images;
    }


    private void deleteImageFiles(List<Image> deletedImages) {
        deletedImages.stream().forEach(it->fileService.delete(it.getOriginName()));
    }

    private Board getAuthorizedBoard(Long boardId, String memberEmail) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NoSuchBoardException("존재하지 않는 게시글입니다."));
        if(!board.getMember().getEmail().equals(memberEmail)) throw new AccessDeniedException("게시글 삭제 권한이 없습니다.");
        return board;
    }

}
