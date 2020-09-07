package com.kgovt.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kgovt.models.ApplicationDetailes;
import com.kgovt.models.PaymentDetails;
import com.kgovt.models.Status;
import com.kgovt.repositories.PaymentDetailsRepository;
import com.kgovt.utils.AppConstants;
import com.kgovt.utils.AppUtilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentDetailsService extends AppConstants{

	@Autowired
	private PaymentDetailsRepository paymentDetailsRepository;
	
	@Autowired
	private StatusService statusService;
		
	public PaymentDetails savePaymentDetails(PaymentDetails paymentDetails)  {
		paymentDetails = paymentDetailsRepository.save(paymentDetails);
		return paymentDetails;
	}
	
	public String savePostPaymentDetails(PaymentDetails paymentDetails, ApplicationDetailes applicationDetailes) {
		log.info("Proceed First Payment Application Number::: " + applicationDetailes.getApplicantNumber());
		try {
			paymentDetails.setMobile(applicationDetailes.getMobile());
			paymentDetails.setEmail(applicationDetailes.getEmail());
			paymentDetails.setAddress(applicationDetailes.getResAddress());
			paymentDetails.setReceiptNo(AppUtilities.generateReceptNo(applicationDetailes));
			paymentDetails.setApplicantNumber(applicationDetailes.getApplicantNumber());
			
			paymentDetails.setCreatedDate(new Date());
			paymentDetails.setStatus("Success");
			savePaymentDetails(paymentDetails);
			// save status
			Status appStatus = new Status();
			appStatus.setApplicantNumber(paymentDetails.getApplicantNumber());
			appStatus.setStatus("I");
			appStatus.setMobile(paymentDetails.getMobile());
			appStatus.setAppliedDate(new Date());
			statusService.saveStatus(appStatus);
		} catch (Exception e) {
			log.error("Error :::: Proceed First Payment::: ", e.getMessage());
			return null;
		}
		return "SUCCESS";
	}
	
}
