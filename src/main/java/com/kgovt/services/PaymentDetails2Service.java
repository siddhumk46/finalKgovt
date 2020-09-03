package com.kgovt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kgovt.models.PaymentDetails2;
import com.kgovt.repositories.PaymentDetails2Repository;
import com.kgovt.utils.AppConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentDetails2Service extends AppConstants{

	@Autowired
	private PaymentDetails2Repository paymentDetailsRepository;
		
	public PaymentDetails2 savePaymentDetails(PaymentDetails2 paymentDetails)  {
		paymentDetails = paymentDetailsRepository.save(paymentDetails);
		return paymentDetails;
	}
	

	
}
