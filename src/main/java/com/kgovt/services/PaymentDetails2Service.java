package com.kgovt.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kgovt.models.ApplicationDetailes;
import com.kgovt.models.BranchDetails;
import com.kgovt.models.PaymentDetails;
import com.kgovt.models.PaymentDetails2;
import com.kgovt.models.Status;
import com.kgovt.repositories.PaymentDetails2Repository;
import com.kgovt.utils.AppConstants;
import com.kgovt.utils.AppUtilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentDetails2Service extends AppConstants{

	@Autowired
	private PaymentDetails2Repository paymentDetailsRepository;
	
	@Autowired
	private BranchDetailsService branchDetailsService;
	
	@Autowired
	private ApplicationDetailesService appicationService;
		
	public PaymentDetails2 savePaymentDetails(PaymentDetails2 paymentDetails)  {
		paymentDetails = paymentDetailsRepository.save(paymentDetails);
		return paymentDetails;
	}
	
	@Autowired
	private StatusService statusService;
	
	public String savePostPaymentDetails(PaymentDetails2 paymentDetails, ApplicationDetailes applicationDetailes) {
		log.info("Proceed Second Payment Application Number::: " + applicationDetailes.getApplicantNumber());
		try {
			paymentDetails.setMobile(applicationDetailes.getMobile());
			paymentDetails.setEmail(applicationDetailes.getEmail());
			paymentDetails.setAddress(applicationDetailes.getResAddress());
			paymentDetails.setReceiptNo(AppUtilities.generateReceptNo(applicationDetailes));
			paymentDetails.setApplicantNumber(applicationDetailes.getApplicantNumber());
			List<BranchDetails> branchDetailsList = branchDetailsService.findAll();
			if(null != branchDetailsList && !branchDetailsList.isEmpty()) {
				Optional<BranchDetails> branch = branchDetailsList.stream().filter(b -> AppUtilities.isNotNullAndNotEmpty(b.getBranch()) && b.getBranch().equalsIgnoreCase(applicationDetailes.getPreOfCenter()))
				.findFirst();
				if (branch.isPresent()) {
					paymentDetails.setAmount(Integer.valueOf(branch.get().getFirstAmount()));
				}
			}
			paymentDetails.setCreatedDate(new Date());
			paymentDetails.setStatus("Success");
			savePaymentDetails(paymentDetails);
			// save status
			Status newStatus = statusService.findByApplicantNumber(paymentDetails.getApplicantNumber());
			newStatus.setStatus("C");
			statusService.saveStatus(newStatus);
			
			applicationDetailes.setPaymentTwo("Yes");
			appicationService.saveApplicationDetailes(applicationDetailes);
		} catch (Exception e) {
			log.error("Error :::: Proceed second Payment::: ", e);
			return null;
		}
		return "SUCCESS";
	}

	
}
