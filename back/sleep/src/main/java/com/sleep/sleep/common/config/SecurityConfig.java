package com.sleep.sleep.common.config;

import com.sleep.sleep.common.JWT.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/").hasRole("USER")
                                .requestMatchers("/api/member/reissue").permitAll()
                                .requestMatchers("/api/member/check/**").permitAll()
                                .requestMatchers("/api/member/join").permitAll()
                                .requestMatchers("/api/member/login").permitAll()
                                .requestMatchers("/api/shorts/**").permitAll()
                                .requestMatchers("/api/s3/**").permitAll()
                                .requestMatchers("/swagger-resources/**",
                                        "/v3/api-docs/**", "/swagger-ui/**").permitAll()


                                .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .headers((headerConfig) -> headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .logout(AbstractHttpConfigurer::disable)
                //jwt로 로그인 로그아웃 처리할 것이므로 disable
                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //기본적으로 session을 이용하게 되는데 redis를 이용할 것이니까 STATELESS
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
        ;


        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //Make the below setting as * to allow connection from any hos
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
