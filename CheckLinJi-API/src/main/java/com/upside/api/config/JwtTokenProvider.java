package com.upside.api.config;

import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.upside.api.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.jwt.secretKey}")
    private String secretKey;
    
    
    private long tokenValidTime = 1000L * 60 * 60; // 토큰은 무한정으로 사용되면 안되기에 만료 시간 60분
    private long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 7; // 7일
        
    private final UserService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    
    /**
     * 토큰 생성 로직
     * @param email
     * @return
     */
    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email); // 토큰의 키가 되는 Subject를 중복되지 않는 고유한 값인 Email 로 지정한다.
        Date now = new Date();
        return Jwts.builder()        		
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // 만료시간은 지금 시간으로부터 30분을 설정한다.
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명할 때 사용되는 알고리즘은 HS256, 키는 위에서 지정한 값으로 진행한다.
                .compact();
    }
    /**
     * 토큰 재발행
     * @return
     */
    public String createRefreshToken() {    	
        Date now = new Date();
        return Jwts.builder()        		
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    
    /**
     * 토큰으로 인증 객체(Authentication)을 얻기 위한 메소드.
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    
    /**
     * ID를 얻기 위해 실제로 토큰을 디코딩하는 부분이다.
     * @param token
     * @return
     */
    public String getEmail(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject(); // 지정한 Secret Key를 통해 서명된 JWT를 해석하여 Subject를 끌고와 리턴하여 이를 통해 인증 객체를 끌고올 수 있다.
        } catch(ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (MalformedJwtException e) { 
        	log.error("토큰 이메일 값을 얻기위한 메소드 ----->" + e);
            return null;
        }
		
    }
    /**
     * 토큰을 사용하기 위해 실제로 Header에서 꺼내오는 메소드이다.
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
    	
        return request.getHeader("Authorization");
    }
    
    /**
     * 토큰이 만료되었는 지를 확인해주는 메소드이다.
     * @param token
     * @return
     */
    public boolean validateTokenExceptExpiration(String token) {
        try {        	
        	        	        	
        	ValueOperations<String, String> redis = redisTemplate.opsForValue(); // Redis Map 객체 생성	
        	
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            
            String email = getEmail(token);
            
            // token 값이 null 이거나 redis에 저장된 값과 다를경우 false
            if(redis.get("accessToken_"+email) == null || !redis.get("accessToken_"+email).equals(token)) {             	
            	return false;
            }
            
            // 이전과 같이 token을 디코딩하여 만료시간을 끌고와 현재시간과 비교해 확인해준다.
            return !claims.getBody().getExpiration().before(new Date()); 
        } catch(Exception e) {        	
            return false;
        }
    }
    
    /**
     * 리프레쉬토큰이 만료되었는 지를 확인해주는 메소드이다.
     * @param token
     * @return
     */
    public boolean validateRefreshTokenExceptExpiration(String token) {
        try {        	        	        	        	        	
        	
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            
            return !claims.getBody().getExpiration().before(new Date()); // 이전과 같이 token을 디코딩하여 만료시간을 끌고와 현재시간과 비교해 확인해준다.
        } catch(Exception e) {
        	log.error("리프레쉬 토큰 확인 중 에러" , e);
            return false;
        }
    }    
    
    /**
     * 토큰을 만료시키는 메소드 토큰 기한을 1분전으로 만든다.
     * @param token
     */
    public void deleteToken(String token) throws Exception {
    	    	       	    	
        String email = getEmail(token);
        
        redisTemplate.delete("accessToken_"+email);
        redisTemplate.delete("refreshToken_"+email);
    	
        log.info("사용자 인증 토큰 , 리프레쉬 토큰 삭제 ");
    }
      
}
