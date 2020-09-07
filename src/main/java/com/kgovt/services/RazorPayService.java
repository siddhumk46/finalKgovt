package com.kgovt.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kgovt.models.RazorPayObject;
import com.kgovt.repositories.RazorPayRepository;
import com.kgovt.utils.AppConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RazorPayService extends AppConstants {

	@Autowired
	private RazorPayRepository razorPayRepository;
	
	public List<RazorPayObject> listRazorDetails() {
		return razorPayRepository.findAll();
	}

}
