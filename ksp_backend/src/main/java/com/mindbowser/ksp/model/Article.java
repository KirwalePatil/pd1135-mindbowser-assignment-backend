package com.mindbowser.ksp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "articles")
@Getter
@Setter
@ToString(callSuper = true)
public class Article extends BaseEntity {

	@Column(nullable = false)
	private String title;

	
	
	
	@Lob
	@Column(nullable = false)
	private String content;

	@Column(columnDefinition = "TEXT")
	private String summary;

	@Enumerated(EnumType.STRING)
	private Category category;

	private String tags;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User author;
}
