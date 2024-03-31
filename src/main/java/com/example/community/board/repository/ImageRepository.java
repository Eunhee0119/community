package com.example.community.board.repository;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
}
