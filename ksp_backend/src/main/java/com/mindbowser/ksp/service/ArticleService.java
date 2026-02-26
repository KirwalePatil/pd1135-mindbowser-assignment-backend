package com.mindbowser.ksp.service;

import com.mindbowser.ksp.model.Article;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ArticleService {

	Mono<Article> saveArticle(Article article);

	Article getArticleById(Long id);

	List<Article> getAllArticles();

	void deleteArticle(Long id);

	List<Article> searchArticles(String keyword);

	List<Article> getArticlesByCategory(String category);

	List<Article> getArticlesByAuthorEmail(String email);


}