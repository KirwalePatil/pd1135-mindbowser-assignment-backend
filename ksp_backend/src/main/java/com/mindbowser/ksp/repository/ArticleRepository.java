package com.mindbowser.ksp.repository;

import com.mindbowser.ksp.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

	@Query("SELECT a FROM Article a WHERE " + "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
			+ "LOWER(a.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Article> searchArticles(@Param("keyword") String keyword);

	
	
	List<Article> findByCategory(String category);

	List<Article> findByAuthorEmail(String email);
}
