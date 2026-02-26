package com.mindbowser.ksp.controller;

import com.mindbowser.ksp.dto.ApiResponse;
import com.mindbowser.ksp.model.Article;
import com.mindbowser.ksp.service.ArticleService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

	private final ArticleService articleService;

	// Create article
	@PostMapping
	public Mono<ResponseEntity<ApiResponse<Article>>> createArticle(@RequestBody Article article) {
		return articleService.saveArticle(article)
				.map(savedArticle -> ResponseEntity.ok(new ApiResponse<>(true, "Article created", savedArticle)));
	}

	
	
	// Update article
	@PutMapping("/{id}")
	public Mono<ResponseEntity<ApiResponse<Article>>> updateArticle(@PathVariable Long id, @RequestBody Article article) {
		article.setId(id);

		return articleService.saveArticle(article)
				.map(updatedArticle -> ResponseEntity.ok(new ApiResponse<>(true, "Article updated", updatedArticle)));
	}

	// Delete article
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteArticle(@PathVariable Long id) {
		articleService.deleteArticle(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Article deleted successfully", null));
	}

	// Get all articles
	@GetMapping
	public ResponseEntity<ApiResponse<List<Article>>> getAllArticles() {
		return ResponseEntity.ok(new ApiResponse<>(true, "OK", articleService.getAllArticles()));
	}
	
	// Get articles for current user
	@GetMapping("/my")
	public ResponseEntity<ApiResponse<List<Article>>> getMyArticles(Authentication authentication) {
		String email = authentication.getName();
		List<Article> articles = articleService.getArticlesByAuthorEmail(email);
		return ResponseEntity.ok(new ApiResponse<>(true, "OK", articles));
	}

	// Get article by ID
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Article>> getArticleById(@PathVariable Long id) {
		return ResponseEntity.ok(new ApiResponse<>(true, "OK", articleService.getArticleById(id)));
	}

	// Search articles by keyword
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<Article>>> searchArticles(@RequestParam("q") String keyword) {
		return ResponseEntity.ok(new ApiResponse<>(true, "OK", articleService.searchArticles(keyword)));
	}

	// Filter articles by category
	@GetMapping("/category/{category}")
	public ResponseEntity<ApiResponse<List<Article>>> getArticlesByCategory(@PathVariable String category) {
		return ResponseEntity.ok(new ApiResponse<>(true, "OK", articleService.getArticlesByCategory(category)));
	}

	/*
	 * // Improve article content using AI
	 * 
	 * @PostMapping("/{id}/improve") public Mono<ResponseEntity<Article>>
	 * improveArticle(@PathVariable Long id) { return
	 * articleService.improveArticleContent(id).map(ResponseEntity::ok); }
	 */
}