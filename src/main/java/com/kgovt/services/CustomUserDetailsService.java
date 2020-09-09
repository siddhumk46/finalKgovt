package com.kgovt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kgovt.models.AdminUsers;
import com.kgovt.models.CustomUserDetails;
import com.kgovt.repositories.AdminUsersRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

	
	@Autowired
	private AdminUsersRepository adminUsersRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 // first try loading from User table
		AdminUsers admin = adminUsersRepository.findByRegion(username);
        if (admin != null) {
            return new CustomUserDetails(admin.getRegion(), admin.getPassword(), admin.getRole(),admin.getRecordId());
        }
        throw new UsernameNotFoundException("User '" + username + "' not found");
	}

}
