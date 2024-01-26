//package com.upside.api.service;
//
//import java.security.PublicKey;
//import java.util.Map;
//
//import org.springframework.stereotype.Component;
//
//import com.upside.api.dto.ApplePublicKeys;
//import com.upside.api.util.JwtParser;
//
//import io.jsonwebtoken.Claims;
//
//@Component
//public class AppleOAuthClient implements OAuthClient {
//
//    private final JwtParser jwtParser;
//    private final AppleApiCaller appleApiCaller;
//    private final AppleOAuthPublicKeyGenerator appleOAuthPublicKeyGenerator;
//    private final AppleJwtClaimValidator appleJwtClaimValidator;
//
//    // 생성자
//
//    @Override
//    public String getOAuthMemberId(String idToken) {
//        Map<String, String> tokenHeaders = jwtParser.parseHeaders(idToken); // 1
//        ApplePublicKeys applePublicKeys = appleApiCaller.getPublicKeys(); // 2
//        PublicKey publicKey = appleOAuthPublicKeyGenerator.generatePublicKey(tokenHeaders, applePublicKeys); //3
//        Claims claims = jwtParser.parseClaims(idToken, publicKey); //4
//        validateClaims(claims); // 5
//        return claims.getSubject();
//    }
