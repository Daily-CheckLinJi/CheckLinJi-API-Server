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
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
		 *  파일 업로드 ( 외부에다 저장 ) - 인증 이미지 , 프로필 이미지
		 * @param file
		 * @param fileUploadDto
		 * @return
		 * @throws IOException
		 */
		public String uploadFile(String image , String email) throws IOException {
			
			String result = "N";
		  	LocalDateTime now = LocalDateTime.now();  
		  	
		 	try {
		 		
		 		byte[] decodedData = Base64.getDecoder().decode(image); // base64로 인코딩되어 올라온 파일 다시 디코딩
		 		
		 		
		        String fileName = email+"_"+now ; // 파일이름 : email_날짜

		        // 파일 저장 경로 생성
		        Path uploadPath = Paths.get(uploadDir,fileName);
		        	        
		        // 파일 저장 경로가 없을 경우 생성
		        if (!Files.exists(uploadPath)) {
		            Files.createDirectories(uploadPath);
		        }
		        
		        Files.write(uploadPath, decodedData, StandardOpenOption.CREATE);
		        
			    return result;
			        
				} catch (IOException e) {
					result = "N";
					return result;
				}	 	
	    }
	 
	 
//	/**
//	 *  파일 업로드 ( 외부에다 저장 ) - 인증 이미지 , 프로필 이미지
//	 * @param file
//	 * @param fileUploadDto
//	 * @return
//	 * @throws IOException
//	 */
//	public String uploadFile(@RequestParam("file") MultipartFile file , String email) throws IOException {
//		
//		String result = "N";
//	  	LocalDate now = LocalDate.now();  
//	  	
//	 	try {
//	 		// 업로드된 파일 이름 가져오기
//	        String fileName = email+"_"+now+"_"+StringUtils.cleanPath(file.getOriginalFilename());
//
//	        // 파일 저장 경로 생성
//	        Path uploadPath = Paths.get(uploadDir);
//	        	        
//	        // 파일 저장 경로가 없을 경우 생성
//	        if (!Files.exists(uploadPath)) {
//	            Files.createDirectories(uploadPath);
//	        }
//
//	        // 파일 저장 경로와 파일 이름을 조합한 경로 생성
//	        Path filePath = uploadPath.resolve(fileName).normalize();		        		        
//	        
//	        // 문자열에서 백슬래시()는 이스케이프 문자(escape character)로 사용되기 때문에 사용할려면 \\ 두개로 해야 \로 인식
//	        String fileRoute = uploadPath.toString() + "/" + fileName ; 
//
//	        
//	        result = fileRoute;
//	        
//	        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//	        	        
//		    return result;
//		        
//			} catch (IOException e) {
//				result = "N";
//				return result;
//			}	 	
//    }
	
	/**
	 * 프로필 사진 업로드
	 * @param file
	 * @param email
	 * @return
	 * @throws IOException
	 */
	public String uploadProfile (@RequestParam("file") MultipartFile file , String email) throws IOException {
		
		String result = "N";
	  	LocalDate now = LocalDate.now();  
	  	
	 	try {	 			 		
	 		
	 		// 파일이름 : 현재날짜 + 이메일 + profile + 사진 이름
	        String fileName = now+"_"+email+"_"+"profile_"+StringUtils.cleanPath(file.getOriginalFilename());	        	        	        	       
	                
	        File uploadProfileDir = new File(profileDir);
	        if (!uploadProfileDir.exists()) {
	        	uploadProfileDir.mkdirs();
	        }
	        
	        // 파일 경로 설정 
	        String uploadedFilePath = uploadProfileDir + "/" + fileName;	       	        
	        	        
	        // 파일 저장 경로 생성
	        Path uploadPath = Paths.get(uploadedFilePath);
	        
	        // 반환 값 저장경로 스트링으로 변환 
	        result = uploadPath.toString();
	        
	        // 저장경로에 파일 생성
	        Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
	        	        
		    return result;
		        
			} catch (IOException e) {
				e.printStackTrace();
				result = "N";
				return result;
			}	 	
    }
		
	/**
	 * 파일 다운로드 Base64 인코딩 방식
	 * @param fileUploadDto
	 * @return
	 */
	public String myAuthImage(String fileRoute) {
		
		log.info("본인 인증 이미지  ------> " + fileRoute);
		
		
		String encoded="N";
		try {		        			
		 // 파일 경로
        String filePath = fileRoute ;
        
        // 파일을 바이트 배열로 읽어옴
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        
        // Base64 인코딩
         encoded = Base64.getEncoder().encodeToString(fileContent);
                        
		
		} catch (IOException e) {			
			encoded = "N";
			log.info("본인 인증 이미지  ------> " + "실패");
			e.printStackTrace();
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
			log.info("파일 삭제 ------> " + Constants.FAIL);
			e.printStackTrace();
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
		    } catch (IOException e) {
		        e.printStackTrace();
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
		        e.printStackTrace();
		        encoding_image = "N" ;
		    }
		 return encoding_image;
	}
	
}
