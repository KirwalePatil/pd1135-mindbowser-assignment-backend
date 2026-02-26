package com.mindbowser.ksp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindbowser.ksp.model.Article;
import com.mindbowser.ksp.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {


	
	private final ArticleRepository articleRepository;
	private final HuggingFaceAIServiceImpl aiService;

	@Override
	public Mono<Article> saveArticle(Article article) {
		return aiService.generateSummary(article.getContent()).map(summary -> {
			article.setSummary(summary);
			return articleRepository.save(article);
		});
	}

	@Override
	public Article getArticleById(Long id) {
		return articleRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Article not found with id: " + id));
	}

	@Override
	public List<Article> getAllArticles() {
		return articleRepository.findAll();
	}

	@Override
	public void deleteArticle(Long id) {
		Article article = getArticleById(id);
		articleRepository.delete(article);
	}

	@Override
	public List<Article> searchArticles(String keyword) {
		return articleRepository.searchArticles(keyword);
	}

	@Override
	public List<Article> getArticlesByCategory(String category) {
		return articleRepository.findByCategory(category);
	}

	@Override
	public List<Article> getArticlesByAuthorEmail(String email) {
		return articleRepository.findByAuthorEmail(email);
	}


}