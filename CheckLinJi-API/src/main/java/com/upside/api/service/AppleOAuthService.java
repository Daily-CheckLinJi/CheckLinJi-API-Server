package com.upside.api.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upside.api.dto.ApplePublicKey;
import com.upside.api.dto.ApplePublicKeys;
import com.upside.api.dto.AppleTokenDto.Request;
import com.upside.api.dto.AppleTokenDto.Response;
import com.upside.api.util.Constants;

import feign.FeignException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleOAuthService {

	private final AppleHttpClient httpClient;
	
	@Value("${apple.kid}")
	private String kid ;
	
	@Value("${apple.alg}")
	private String alg ;
	
	@Value("${apple.issue}")
	private String issue ;
	
	@Value("${apple.aud}")
	private String aud ;
	
	@Value("${apple.subject}")
	private String subject ;

	@Value("${apple.secret.file}")
	private String secretFile ;
	
	@Value("${apple.secret.file.route.yn}")
	private String fileRouteYn ;
	
	
			
	
	public Map<String, String> appleLogin(String identityToken , String authorizationCode){
		
		log.info("-------------------- 애플 로그인 시작 --------------------");	
		        
		Map<String, String> result = new HashMap<String, String>();
							
        try {
            
        	// apple publicKey 목록 가져오기 
        	ApplePublicKeys publicKeyList = httpClient.getAppleAuthPublicKey();
        	        	        	        	
        	// identityToken(JWT)에서 헤더 부분 추출
        	String headerOfIdentityToken = identityToken.substring(0, identityToken.indexOf("."));        	         	         	 
        	Map<String, String> headerValue = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerOfIdentityToken), "UTF-8"), Map.class);
        	
        	String kid = headerValue.get("kid");
        	String alg = headerValue.get("alg");           	        	
        	
        	for(ApplePublicKey key : publicKeyList.getKeys()) {
        		if(kid.equals(key.getKid()) && alg.equals(key.getAlg())) {
        			        			           			
        			// n(modulus), e(exponent) 는 jwt 형식에 공개키 
        			byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
    	            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

    	            BigInteger n = new BigInteger(1, nBytes);
    	            BigInteger e = new BigInteger(1, eBytes);
        			
    	            // n, e 값을 통해 public key를 생성한뒤 public key로 Identity Token의 서명(signature)을 검증하면 된다.
    	            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
    	            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
    	            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
    	            
    	            Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(identityToken).getBody();
    	              	            
//    	            log.info(String.valueOf(claims));
    	                	                	            
    	            // Apple에 인증을 받기위한 secretKey(JWT) 생성
    	            String client_secret = makeClientSecretJwt();
  	                	                	                	               	            
    	            // 순서대로 APP으로 부터 넘겨받은 Authorization Code , 개발자 계정 팀명?(com.xxx) , clientSecret(jwt) , 인증 or 리프레쉬 여부 , 리프레쉬 토큰 아니기에 NONE 
    	            Response results = httpClient.getToken(Request.of(authorizationCode, subject, client_secret, "authorization_code","NONE"));
    	            
    	            log.info("result : " + results);
    	            log.info("AccessToken : " + results.getAccessToken());
    	            log.info("ExpiresIn : " + results.getExpiresIn());
    	            log.info("IdToken : " + results.getIdToken());
    	            log.info("TokenType : " + results.getTokenType());
    	            log.info("RefreshToken : " + results.getRefreshToken());
    	            log.info("Error : " + results.getError());
        			    	                	     
    	            result.put("key", String.valueOf(claims.get("sub")));
    	        	result.put("HttpStatus", "2.00");	    	
    		    	result.put("Msg", Constants.SUCCESS);
    	            
        			break;        		
        		}    		
        	}
        	
        	if(result.size() == 0 ) {        		
	        	result.put("HttpStatus", "1.00");	    	
		    	result.put("Msg", Constants.FAIL);
        	}
        	                         
        } catch (FeignException.BadRequest e) { 
        	log.error("-------------- 400 에러 애플 로그인 실패 -------------------- " , e);                
        	result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", "토큰이 잘못 되었습니다.");
        } catch (FeignException.InternalServerError e) { 
        	log.error("-------------- 500 에러 애플 로그인 실패 -------------------- " , e);                
        	result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", "애플 서버에 오류가 있는것 같습니다.");
        } catch (Exception e) {
        	log.error("--------------애플 로그인 실패 -------------------- " , e);        	
        	result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.SYSTEM_ERROR);
        }
		return result;			
		}	
	
	
	/**
	 * Apple에 인증을 받기위한 JWT 생성
	 * @return
	 * @throws IOException
	 */
	public String makeClientSecretJwt() throws Exception {
	    Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant()); // 30일?
	    return Jwts.builder()
	               .setHeaderParam("kid", kid)
	               .setHeaderParam("alg", alg)
	               .setIssuer(issue)
	               .setIssuedAt(new Date(System.currentTimeMillis()))
	               .setExpiration(expirationDate)
	               .setAudience(aud)
	               .setSubject(subject)
	               .signWith(SignatureAlgorithm.ES256, getPrivateKey())
	               .compact();
	    }	
	
	
	/**
	 * Apple 인증 파일 읽기
	 * Apple Developer 페이지에서 다운로드 받은 확장자가 .p8 인 파일을 사용하면된다.
	 * @return
	 * @throws IOException
	 */ 
	private PrivateKey getPrivateKey() throws Exception {
		
		if(fileRouteYn.equals("Y")) {
			String filePath = secretFile; // 실제 개인 키 파일 경로로 수정

		    File file = new File(filePath);
		    
		    // 파일이 존재하는지 확인
		    if (file.exists()) {
		        // 개인 키 파일이 존재하면 처리
		        String privateKey = new String(Files.readAllBytes(Paths.get(file.toURI())));
		        Reader pemReader = new StringReader(privateKey);
		        PEMParser pemParser = new PEMParser(pemReader);
		        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
		        return converter.getPrivateKey(object);
		    } else {
		        // 파일이 존재하지 않을 때의 처리
		        throw new FileNotFoundException("개인 키 파일이 존재하지 않습니다.");
		    }
		    
		}else {
		    ClassPathResource resource = new ClassPathResource(secretFile);
		    String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));
		    Reader pemReader = new StringReader(privateKey);
		    PEMParser pemParser = new PEMParser(pemReader);
		    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		    PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
		    return converter.getPrivateKey(object);
		}
		
	 
	}	
}
