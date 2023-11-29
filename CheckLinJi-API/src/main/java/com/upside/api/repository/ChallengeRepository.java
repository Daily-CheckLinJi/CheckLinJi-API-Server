//package com.upside.api.repository;
//
//
//
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.upside.api.entity.ChallengeEntity;
//
//public interface ChallengeRepository extends JpaRepository<ChallengeEntity, String> {
//		 
////	
//	/**	  
//	 * 이 메소드는 startTime 필드가 before 인자보다 이전이고 endDateTime 필드가 after 인자보다 이후인 Challenge 엔티티의 리스트를 반환
//	 * @param before
//	 * @param after
//	 * @return
//	 */
//	List<ChallengeEntity> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime before, LocalDateTime after);
//	
//	Optional<ChallengeEntity> findByStartTimeAndEndTime (LocalDate startDate , LocalDate endDate);
//	
//	 Optional<ChallengeEntity> findByDescription (String challengeDescription);
//	 
//	 ChallengeEntity findByChallengeName (String challengeName);
//}
//
