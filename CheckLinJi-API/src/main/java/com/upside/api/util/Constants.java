package com.upside.api.util;

public class Constants {

//	4.1. 응답 코드
//	resCode Description
//	1.00 처리가 완료되지않았습니다.
//	1.01 잘못된 파라미터
//	1.02 중복된 아이디 입니다.
//	1.03 아이디 혹은 패스워드가 올바르지않습니다.
//	1.04 존재하지않는 아이디입니다.
//	1.05 만료된 토큰입니다.
//	1.06 올바르지않는 토큰입니다.
	
//	2.00 정상적으로 처리가 완료되었습니다.
//	2.01 회원가입으로
//	2.02 올바른 요청이나 처리하지 못함
//	2.03 지원하지 않는 URL(URL Not Support)
//	3.00 서버 오류(Server E
	
	

public static final String SUCCESS = "정상적으로 처리가 완료되었습니다.";
public static final String FAIL = "처리가 완료되지않았습니다.";
public static final String NOT_EXIST_PARAMETER = "값이 존재 하지않는 파라미터가 있습니다.";
public static final String NOT_EXIST_EMAIL = "존재하지않는 이메일입니다.";
public static final String DUPLICATE_EMAIL = "중복된 이메일 입니다.";
public static final String INBALID_EMAIL_PASSWORD = "이메일 혹은 패스워드가 올바르지않습니다";
public static final String EXPIRATION_TOKEN = "만료되었거나 올바르지 않은 토큰입니다.";
public static final String INBALID_TOKEN = "올바르지 않은 토큰입니다.";
public static final String INBALID_EMAIL = "이메일이 올바르지않습니다";
public static final String INBALID_PASSWORD = "패스워드가 올바르지않습니다";
public static final String SYSTEM_ERROR = "시스템 에러로 인해 처리가 완료 되지않았습니다.";
}
