package com.example.community.category.respository;

import com.example.community.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> ,CategoryRepositoryCustom {
}
