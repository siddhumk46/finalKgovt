package com.kgovt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kgovt.models.BranchDetails;

public interface BranchDetailsRepository extends JpaRepository<BranchDetails, Integer> {

	BranchDetails findByBranch(String branch);

}
