package com.upside.api.service;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@RequiredArgsConstructor
@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@Service
public class FileService {
		
	
	 
	
	 @Value("${file.upload-dir}")
	 private String uploadDir;
	 
	 @Value("${profile.upload-dir}")
	 private String profileDir;
	
	
	 	/**
	 	 * 파일 업로드 ( 외부에다 저장 ) - 인증 이미지 , 프로필 이미지
	 	 * @param image
	 	 * @param imageName
	 	 * @param email
	 	 * @return
	 	 * @throws IOException
	 	 */
		public String uploadFile(String image ,String imageName ,String email) throws IOException {
			
			String result = "N";
		  	LocalDate now = LocalDate.now();  
		  	
		  	try {
		  	    byte[] decodedData = Base64.getDecoder().decode(image); // base64로 인코딩된 문자열을 디코딩

		  	    String fileName = email + "_" + now + "_" + imageName; // 파일 이름: email_날짜

		  	    Path uploadPath = Path.of(uploadDir); // 저장할 파일 경로

		  	    // 파일 저장 경로가 없을 경우 생성
		  	    if (!Files.exists(uploadPath)) {
		  	        Files.createDirectories(uploadPath);
		  	    }

		  	    Path filePath = uploadPath.resolve(fileName).normalize(); // 파일 경로 이름과 함께 지정
		  	    
		  	    Files.write(filePath, decodedData, StandardOpenOption.CREATE_NEW); // 새로운 파일 생성

		  	    result = filePath.toString(); // DB에 저장될 경로

		  	    return result;
		  	} catch (Exception e) {
		  	    log.error("파일 업로드 ( 외부에다 저장 ) - 인증 이미지 , 프로필 이미지 ---> " + Constants.SYSTEM_ERROR , e);
		  	    result = "N";
		  	    return result;
		  	}
	    }
	 
	 
	/**
	 * 프로필 사진 업로드 
	 * @param profile
	 * @param profileName
	 * @param email
	 * @return
	 * @throws IOException
	 */
	public String uploadProfile (String profile , String profileName , String email) throws IOException {
		
		String result = "N";
	  	LocalDate now = LocalDate.now();  
	  	
	 	try {	 			 		
	 		
	 		byte[] decodedData = Base64.getDecoder().decode(profile); // base64로 인코딩된 파일을 디코딩
	 		
	 		
	 		String fileName = email + "_" + now + "_" + profileName; // 파일 이름: email_날짜_파일이름  
	 		
	 		
	 		Path uploadPath = Path.of(profileDir); // 저장할 파일 경로
	 			                	        
	  	    // 파일 저장 경로가 없을 경우 생성
	  	    if (!Files.exists(uploadPath)) {
	  	        Files.createDirectories(uploadPath);
	  	    }
	        	        
	  	    Path filePath = uploadPath.resolve(fileName).normalize(); // 파일 경로 이름과 함께 지정       	        
	        	        
	  	    // 저장경로에 파일 생성
	  	  	Files.write(filePath, decodedData, StandardOpenOption.CREATE_NEW); // 새로운 파일 생성
	        
	        // 반환 값 저장경로 스트링으로 변환 
	  	  	result = filePath.toString(); // DB에 저장될 경로	        	      
	        	        
		    return result;
		        
			} catch (Exception e) {
				log.error("프로필 사진 업로드 ---> " + Constants.SYSTEM_ERROR , e);
				result = "N";
				return result;
			}	 	
    }
		
	/**
	 * 저장된 파일을 Base64로 인코딩해서 클라이언트에게 응답 ( base64로 인코딩해야 용량이 줄어듬 | 프론트 측은 base64를 디코딩해서 사용자에게 표출 ) 
	 * @param fileUploadDto
	 * @return
	 */
	public String myAuthImage(String fileRoute) {
		
		log.info("저장된 파일을 Base64로 인코딩  ------> " + fileRoute);
		
		
		String encoded="N";
		try {		        			
		 // 파일 경로
        String filePath = fileRoute ;
        
        // 파일을 바이트 배열로 읽어옴
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        
        // Base64 인코딩
        encoded = Base64.getEncoder().encodeToString(fileContent);
         
        log.info("저장된 파일을 Base64로 인코딩  ------> " + "성공");
		
		} catch (IOException e) {			
			encoded = "N";
			log.error("저장된 파일을 Base64로 인코딩  ------> " + Constants.SYSTEM_ERROR , e);			
		}		
		 return encoded ;				 	 	    			    		   
	}
	
	/**
	 * 파일 삭제 ( 프로필 , 인증사진 등 )
	 * @param fileUploadDto
	 * @return
	 */
	public boolean deleteFile (String fileRoute) {
		
		log.info("파일 삭제 ------> " + fileRoute);
		
		
		boolean result = false ;
		try {		        			
		
			// 삭제할 파일의 경로나 식별자
			String filePath = fileRoute ;
			// 파일 객체 생성
			File file = new File(filePath);
			// 파일이 존재하는 경우 삭제
			if (file.exists()) {
			    result = file.delete();
			    if (result) {
			    	log.info("파일 삭제 ------> " + Constants.SUCCESS);
			    } else {
			    	log.info("파일 삭제 ------> " + Constants.FAIL);
			    }
			} else {
					result = true ;
					log.info("파일 삭제 ------> " + "삭제할 파일이 존재하지 않습니다.");
			}	
			                        		
		} catch (Exception e) {			
			result = false ;
			log.error("파일 삭제 ------> " + Constants.SYSTEM_ERROR , e);			
		}
		 return result ;				 	 	    			    		   
	}
	
	
	/**
	 * URL로 이미지 다운로드 Base64 인코딩 방식
	 * @param fileUploadDto
	 * @return
	 */
	public byte[] ImageUrlDownload(String imageUrl) {
		
		log.info("이미지 다운로드 (URL) ------> " + imageUrl);
		
		byte[] imageBytes = null;
		
		 try {
		        // 호출된 URL로 연결
		        URL url = new URL(imageUrl);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setRequestMethod("GET");

		        // 이미지 응답을 받아서 저장
		        InputStream inputStream = connection.getInputStream();
		        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		        byte[] buffer = new byte[4096];
		        int bytesRead;
		        while ((bytesRead = inputStream.read(buffer)) != -1) {
		            outputStream.write(buffer, 0, bytesRead);
		        }
		        imageBytes = outputStream.toByteArray();

		        // 이미지를 base64로 인코딩
//		        base64Image = Base64.getEncoder().encodeToString(imageBytes);		        		        	        		        		      

		        // 연결과 스트림 닫기
		        connection.disconnect();
		        inputStream.close();
		        outputStream.close();
		    } catch (Exception e) {
		        log.error("이미지 다운로드 (URL) -----> " + Constants.SYSTEM_ERROR);
		        imageBytes = null ;
		    }
		 return imageBytes;
	}
	
	/**
	 * 디코딩된 blob값을 다시 인코딩
	 * @param imageUrl
	 * @return
	 */
	public String encodingImageUrl(byte[] image) {
		
		log.info("디코딩된 blob 값을 다시 인코딩 ------> " + image);
		
		String encoding_image = "N";
		 try {		      
		        // 이미지를 base64로 인코딩
			 encoding_image = Base64.getEncoder().encodeToString(image);		        		        	        		        		      
		        // 연결과 스트림 닫기		      
		    } catch (Exception e) {
		    	log.error("디코딩된 blob 값을 다시 인코딩 ------> " + Constants.SYSTEM_ERROR);
		        encoding_image = "N" ;
		    }
		 return encoding_image;
	}
	
}
