package com.mindbowser.ksp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import jakarta.servlet.http.HttpServletResponse;

import com.mindbowser.ksp.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtFilter;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:5173")); // your React origin
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	
	
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	    http
	        // CORS: allow React dev server to call backend (preflight + actual requests)
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        // CSRF disabled: stateless JWT + JSON APIs (no cookie-based session)
	        .csrf(AbstractHttpConfigurer::disable)
	        // Disable browser popups (no basic auth / no form login)
	        .httpBasic(AbstractHttpConfigurer::disable)
	        .formLogin(AbstractHttpConfigurer::disable)
	        // Stateless session: JWT only
	        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        // Always return 401 (not 403/500) when unauthenticated
	        .exceptionHandling(eh -> eh.authenticationEntryPoint(
	            (request, response, authException) ->
	                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
	        ))
	        .authorizeHttpRequests(auth -> auth
	            // OPTIONS preflight: must succeed for React browser requests
	            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
	            // Public endpoints (no JWT required)
	            .requestMatchers("/api/auth/**").permitAll()
	            .requestMatchers("/api/ai/summary").permitAll()
	            // Protected endpoints (JWT required)
	            .requestMatchers(HttpMethod.GET, "/api/articles/my").authenticated()
	            // Articles list/details are public
	            .requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
	            .requestMatchers(HttpMethod.POST, "/api/articles/**").authenticated()
	            .requestMatchers(HttpMethod.PUT, "/api/articles/**").authenticated()
	            .requestMatchers(HttpMethod.DELETE, "/api/articles/**").authenticated()
	            .requestMatchers(HttpMethod.POST, "/api/ai/improve").authenticated()
	            .anyRequest().authenticated()
	        )
	        // JWT filter must run before UsernamePasswordAuthenticationFilter
	        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}