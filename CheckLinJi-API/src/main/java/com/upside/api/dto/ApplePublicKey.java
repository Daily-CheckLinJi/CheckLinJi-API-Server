package com.upside.api.dto;

import lombok.Data;

@Data
public class ApplePublicKey {

	private String kty ;
	private String kid ;
	private String use ;
	private String alg ;
	private String n ;
	private String e ;
	private String identityToken ;
	private String authorizationCode ;
	
/** 여기서부턴 identityToken 을 parsing해서 나온값 **/
	
	// Apple Developer 페이지에 명시되어있는 Team ID 이다. (우측 상단에 있음.)
	private String iss ;
	// lient secret이 생성된 일시를 입력한다. (현재시간을 주면 된다)
	private String lat ;
	// client secret이 만료될 일시를 입력한다. (현재시간으로 부터 15777000초, 즉 6개월을 초과하면 안된다.)
	private String exp ;
	// aud는 "https://appleid.apple.com" 값을 입력한다.
	private String aud;
	// client_id 값을 입력한다. com.xxx.xxx 와 같은 형식이다.
	private String sub ;

}
