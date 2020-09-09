package com.kgovt.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kgovt.models.ApplicationDetailes;

public interface ApplicationDetailesRepository extends JpaRepository<ApplicationDetailes, Integer> {

	ApplicationDetailes findByApplicantNumber(Long applicantNumber);
	
	Long countByMobile(Long mobile);
	
	@Query(value = "SELECT max(applicantNumber) from ApplicationDetailes")
	Long max();
	
	long deleteByApplicantNumber(long ApplicantNumber);
	
	@Query(value = "SELECT a from ApplicationDetailes a right join Status b on a.applicantNumber=b.applicantNumber where a.preOfCenter=?1")
	List<ApplicationDetailes> getByRegions(String preOfCenter);
	
	@Query(value = "SELECT a from ApplicationDetailes a right join Status b on a.applicantNumber=b.applicantNumber where a.preOfCenter=?1 and b.status=?2")
	List<ApplicationDetailes> getByNames(String preOfCenter, String applicationStatus);

	ApplicationDetailes findByMobile(Long mobile);
	
	
}
