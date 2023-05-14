package com.upside.api.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
@Configuration
@EnableWebSecurity // Spring Security의 웹 보안을 사용하도록 설정
@RequiredArgsConstructor // 어노테이션은 final로 선언된 필드를 자동으로 주입.
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
     // antMatchers 부분도 deprecated 되어 requestMatchers로 대체
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/img/**");
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // rest api이므로 csrf 보안 미사용
        .cors().configurationSource(corsConfigurationSource()) // cors 설정 
        .and()
        .httpBasic().disable() // 기본 인증창(rest api이므로 기본설정 미사용)        
        .formLogin().disable() // rest api 폼 로그인 인증 방식을 비활성화
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // jwt로 인증하므로 세션 미사용        
        http.authorizeHttpRequests() // HTTP 요청에 대한 인가 규칙을 설정        		
        		.requestMatchers("/api/members/sign/**").permitAll()  // login 없이 접근 허용 하는 URL
        		.requestMatchers("/api/members/login/**").permitAll()
        		.requestMatchers("/api/members/email/**").permitAll()        		
        		.requestMatchers("/api/members/validateDuplicated/**").permitAll()
        		.requestMatchers("/api/social/login/**").permitAll()
        		.requestMatchers("/api/members/refreshToken/**").permitAll()          		
        		.requestMatchers("/api/external/**").permitAll()
        		.requestMatchers("/api/send/**").permitAll()
//        		.requestMatchers("/api/challenge/join/**").permitAll()
//        		.requestMatchers("/api/challenge/submit/**").permitAll()        		
//                .requestMatchers("/api/admin/**").hasRole("ADMIN") // admin 의 경우 ADMIN 권한이 있는 사용자만 접근이 가능
                .anyRequest().authenticated() // 그 외 모든 요청은 인증과정 필요 
//        		.anyRequest().permitAll() // 모든 요청에 대해 인증 생략
                .and()                
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // UsernamePassword 필터전에 JWT 인증 필터를 추가합니다.

        return http.build();
    }
     
    
    // CORS 허용 적용
    
    @Bean 
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

//        configuration.addAllowedOrigin("*");
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("http://localhost:8081");
        configuration.addAllowedOrigin("http://localhost:9999");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://15.164.49.235:3000");
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.addAllowedMethod("*"); // 모든 메서드허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
	/*
	 * .authorizeRequests() → .authorizeHttpRequests() .antMatchers() →
	 * .requestMatchers() .access("hasAnyRole('ROLE_A','ROLE_B')") →
	 * .hasAnyRole("A", "B")
	 */
}