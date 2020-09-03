package com.kgovt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kgovt.models.AdminUsers;

public interface AdminUsersRepository extends JpaRepository<AdminUsers, Integer> {

	public AdminUsers findByRegion(String region);

}
