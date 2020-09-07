package com.kgovt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kgovt.models.RazorPayObject;

public interface RazorPayRepository extends JpaRepository<RazorPayObject, Integer> {

}
