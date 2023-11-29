package com.upside.api.repository;




import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.UserChallengeEntity;

public interface UserChallengeRepository extends JpaRepository<UserChallengeEntity, Long> {
		 
//	List<ChallengeEntity> findByStartDateTimeBeforeAndEndDateTimeAfter(LocalDateTime before, LocalDateTime after);
//	
	/**
	 * 이 메소드는 member 인자와 completed 인자가 일치하는 UserChallenge 엔티티의 리스트를 반환합니다.
	 * @param member
	 * @param completed
	 * @return
	 */
//	List<UserChallengeEntity> findByMemberEntityAndCompleted (MemberEntity member , boolean completed);
//	
//	List<UserChallengeEntity> findByMemberEntity(MemberEntity memberEntity);
//	
	Optional<UserChallengeEntity> findByEmail (String email);
}

