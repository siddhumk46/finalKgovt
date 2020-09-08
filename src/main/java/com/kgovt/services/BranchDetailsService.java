package com.kgovt.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kgovt.models.BranchDetails;
import com.kgovt.repositories.BranchDetailsRepository;
import com.kgovt.utils.AppConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BranchDetailsService extends AppConstants {

	@Autowired
	private BranchDetailsRepository branchDetailsRepository;

	public BranchDetails saveBranchDetails(BranchDetails branchDetails) {
		branchDetails = branchDetailsRepository.save(branchDetails);
		return branchDetails;
	}

	public  BranchDetails findByBranch(String branch) {
		return branchDetailsRepository.findByBranch(branch);
	}

	public  List<BranchDetails> findAll() {
		return branchDetailsRepository.findAll();
	}
}
