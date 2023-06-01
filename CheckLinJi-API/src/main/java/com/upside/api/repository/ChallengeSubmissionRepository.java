package com.upside.api.repository;




import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.ChallengeSubmissionEntity;
import com.upside.api.entity.UserChallengeEntity;

public interface ChallengeSubmissionRepository extends JpaRepository<ChallengeSubmissionEntity, Long> {
		 	
//	
	/**
	 *  이 메소드는 user 인자와 completed 인자가 일치하는 UserChallenge 엔티티의 리스트를 반환합니다.
	 * @param userChallenge
	 * @return
	 */
	 List<ChallengeSubmissionEntity> findByUserChallengeOrderBySubmissionTimeDesc (UserChallengeEntity userChallenge);
	 
	 Optional<ChallengeSubmissionEntity> findByUserChallengeAndSubmissionTime (UserChallengeEntity userChallenge , LocalDateTime submissionTime);
	 	 
}

