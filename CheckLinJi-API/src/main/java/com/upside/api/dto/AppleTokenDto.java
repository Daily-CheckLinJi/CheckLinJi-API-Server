package com.upside.api.dto;

import lombok.Setter;


public class AppleTokenDto {

	@Setter
	public static class Request {
		// APP으로 부터 넘겨받은 Authorization Code 이다.
		private String code;
		// Apple Developer 페이지에 App Bundle ID를 말한다. ex) com.xxx.xxx 형식이다. application.properties -> sub
		private String client_id;
		// 생성한 secret jwt 토큰 
		private String client_secret;
		// grant_type -> authorization_code 값을 주면 된다
		private String grant_type;
		// 아직 모름
		private String refresh_token;

		public static Request of(String code, String clientId, String clientSecret, String grantType, String refreshToken) {
			Request request = new Request();
			request.code = code;
			request.client_id = clientId;
			request.client_secret = clientSecret;
			request.grant_type = grantType;
			request.refresh_token = refreshToken;
			return request;
		}
	}

	@Setter
	public static class Response {
		private String access_token;
		private String expires_in;
		private String id_token;
		private String refresh_token;
		private String token_type;
		private String error;

		public String getAccessToken() {
			return access_token;
		}

		public String getExpiresIn() {
			return expires_in;
		}

		public String getIdToken() {
			return id_token;
		}

		public String getRefreshToken() {
			return refresh_token;
		}

		public String getTokenType() {
			return token_type;
		}
		
		public String getError() {
			return error;
		}
	}

}
