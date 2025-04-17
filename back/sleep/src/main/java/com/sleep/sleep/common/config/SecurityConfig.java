package com.sleep.sleep.common.config;

import com.sleep.sleep.common.JWT.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Value("${cors.allowed-origin}")
    private List<String> origin;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // JWT 사용하므로 csrf 비활성화
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // iframe 허용
                .logout(AbstractHttpConfigurer::disable) // JWT로 로그아웃 구현하므로 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 허용(다른 도메인/포트 요청 허용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Spring Security는 디폴트로 세션을 사용하는데 JWT 사용하므로 무상태(세션 생성X)
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers( // 회원가입, 로그인, 중복 체크, 토큰 재발급은 로그인 없이 접근 가능
                                        "/api/member/join",
                                        "/api/member/login",
                                        "/api/member/check/**",
                                        "/api/member/reissue",
                                        "/api/shorts/health-check"
                                ).permitAll()
                                // 그 외 나머지 요청은 인증 필요
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 등록(Spring Security 기본 인증 필터 전에 실행)

        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 명시된 도메인만 허용
        corsConfiguration.setAllowedOriginPatterns(origin);
        // 모든 HTTP 메서드 허용
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 모든 요청 헤더 허용
        corsConfiguration.setAllowedHeaders(List.of("*"));
        // 인증 정보 포함한 요청 허용(쿠키 사용할 때 필요)
        // corsConfiguration.setAllowCredentials(true);
        // 브라우저 안전 확인 요청(preflight) 결과를 1시간 동안 캐시
        corsConfiguration.setMaxAge(3600L);
        // 위 설정을 모든 경로에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
