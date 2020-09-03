package com.kgovt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kgovt.models.AdminUsers;
import com.kgovt.repositories.AdminUsersRepository;
import com.kgovt.utils.AppConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminUsersService extends AppConstants{

	@Autowired
	private AdminUsersRepository adminUsersRepository;
		
	public AdminUsers saveAdmin(AdminUsers admin) {
		return adminUsersRepository.save(admin);
	}

	public AdminUsers findByRegion(String region) {
		return adminUsersRepository.findByRegion(region);
	}

	
}
