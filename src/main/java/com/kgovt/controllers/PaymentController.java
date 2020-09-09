package com.kgovt.controllers;

import java.net.URI;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kgovt.models.ApplicationDetailes;
import com.kgovt.models.PaymentDetails;
import com.kgovt.models.PaymentDetails2;
import com.kgovt.models.RazorPayObject;
import com.kgovt.services.ApplicationDetailesService;
import com.kgovt.services.BranchDetailsService;
import com.kgovt.services.PaymentDetails2Service;
import com.kgovt.services.PaymentDetailsService;
import com.kgovt.services.RazorPayService;
import com.kgovt.utils.AppConstants;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/pay")
@Slf4j
public class PaymentController extends AppConstants {
	
	@Autowired
	private PaymentDetailsService paymentDetailsService;

	@Autowired
	private PaymentDetails2Service paymentDetails2Service;
	
	@Autowired
	private ApplicationDetailesService appicationService;
	
	@Autowired
	BranchDetailsService branchDetailsService;

	@Autowired
	RazorPayService razorPayService;
	
	@PostMapping(value = "/makeFirstPayment", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Map makeFirstPayment(@RequestParam Long applicationNumber) {
		log.info("Payment method one started::::::: for Application Number" + applicationNumber);
		HashMap<String, Object> returnData = new HashMap<>();
		returnData.put("ERROR", "0");
		try {
			ApplicationDetailes applicationDetailes = appicationService.findByApplicantNumber(applicationNumber);
			if(null != applicationDetailes) {
				PaymentDetails paymentDetails = appicationService.proceedFirstPayment(applicationDetailes);
				if (null == paymentDetails) {
					log.error("Payment Method One ::::::: Error Occured while making Payment!!! Please contact helpline number(s)" + applicationNumber);
					returnData.put("ERROR", "1");
				} else {
					log.info("Payment method one::::::: Payment details Successfully retireved" + applicationNumber);
					returnData.put("PaymentDetails", paymentDetails);
				}
			}else {
				log.error("Payment method one::::::: Application number is not retrived for application number "+ applicationNumber);
				returnData.put("ERROR", "2");
			}
		} catch (Exception e) {
			log.error("Error making first Payment", e.getMessage());
			returnData.put("ERROR", "3");
		}
		return returnData;
	}
	
	@PostMapping(value = "/chargePayment1/{applicationNumber}/", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	@ResponseBody
	public Object chargePayment1(@PathVariable("applicationNumber") Long applicationNumber,
			@RequestParam Map<String, String> formParams) {
		log.info("Payment method one started::::::: for Application Number");
		ApplicationDetailes applicationDetailes = appicationService.findByApplicantNumber(applicationNumber);
		if (null != applicationDetailes) {
			String paymentId = formParams.get("razorpay_payment_id");
			String razorpaySignature = formParams.get("razorpay_signature");
			String orderId = formParams.get("razorpay_order_id");
			if (StringUtils.isNotBlank(paymentId) && StringUtils.isNotBlank(razorpaySignature)
					&& StringUtils.isNotBlank(orderId)) {
				try {
					JSONObject options = new JSONObject();
					options.put("razorpay_payment_id", paymentId);
					options.put("razorpay_order_id", orderId);
					options.put("razorpay_signature", razorpaySignature);
					List<RazorPayObject> list = razorPayService.listRazorDetails();
					boolean isEqual = Utils.verifyPaymentSignature(options, list.get(0).getAccSecret());
					if (isEqual) {
						PaymentDetails paymentDetails = new PaymentDetails();
						paymentDetails.setRazorpayPaymentId(paymentId);
						paymentDetails.setRazorpayOrderId(orderId);
						paymentDetails.setOrderId(orderId);
						paymentDetails.setRazorpaySignature(razorpaySignature);
						String ret = paymentDetailsService.savePostPaymentDetails(paymentDetails, applicationDetailes);
						if(null != ret) {
							HttpHeaders headers = new HttpHeaders();
							String url = "/viewAppStatus?applicantNumber=" + applicationNumber;
							headers.setLocation(URI.create(url));
							return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
						}else {
							log.error("Payment failed and duleexception occured");
							HttpHeaders headers = new HttpHeaders();
							String url = "/error";
							headers.setLocation(URI.create(url));
							return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
						}
					}
				} catch (RazorpayException e) {
					log.error("Payment failed and exception occured", e.getMessage());
					HttpHeaders headers = new HttpHeaders();
					String url = "/error";
					headers.setLocation(URI.create(url));
					return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
				}
			}
		} else {
			log.error("No Data Present forn the application Number");
			HttpHeaders headers = new HttpHeaders();
			String url = "/error";
			headers.setLocation(URI.create(url));
			return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
		}
		HttpHeaders headers = new HttpHeaders();
		String url = "/error";
		headers.setLocation(URI.create(url));
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}

	@PostMapping(value = "/makeSecondPayment", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Map makeSecondPayment(@RequestParam Long applicationNumber) {
		log.info("Payment method second started::::::: for Application Number" + applicationNumber);
		HashMap<String, Object> returnData = new HashMap<>();
		returnData.put("ERROR", "0");
		try {
			ApplicationDetailes applicationDetailes = appicationService.findByApplicantNumber(applicationNumber);
			if(null != applicationDetailes) {
				PaymentDetails2 paymentDetails = appicationService.proceedSecondPayment(applicationDetailes);
				if (null == paymentDetails) {
					log.error("Payment Method Two ::::::: Error Occured while making Payment!!! Please contact helpline number(s)" + applicationNumber);
					returnData.put("ERROR", "1");
				} else {
					log.info("Payment method one::::::: Payment details Successfully retireved" + applicationNumber);
					returnData.put("PaymentDetails", paymentDetails);
				}
			}else {
				log.error("Payment method one::::::: Application number is not retrived for application number "+ applicationNumber);
				returnData.put("ERROR", "2");
			}
		} catch (Exception e) {
			log.error("Error making second Payment", e.getMessage());
			returnData.put("ERROR", "3");
		}
		return returnData;
	}
	
	@PostMapping(value = "/chargePayment2/{applicationNumber}/", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	@ResponseBody
	public Object chargePayment2(@PathVariable("applicationNumber") Long applicationNumber,
			@RequestParam Map<String, String> formParams) {
		log.info("Payment method Second started::::::: for Application Number");
		ApplicationDetailes applicationDetailes = appicationService.findByApplicantNumber(applicationNumber);
		if (null != applicationDetailes) {
			String paymentId = formParams.get("razorpay_payment_id");
			String razorpaySignature = formParams.get("razorpay_signature");
			String orderId = formParams.get("razorpay_order_id");
			if (StringUtils.isNotBlank(paymentId) && StringUtils.isNotBlank(razorpaySignature)
					&& StringUtils.isNotBlank(orderId)) {
				try {
					JSONObject options = new JSONObject();
					options.put("razorpay_payment_id", paymentId);
					options.put("razorpay_order_id", orderId);
					options.put("razorpay_signature", razorpaySignature);
					List<RazorPayObject> list = razorPayService.listRazorDetails();
					boolean isEqual = Utils.verifyPaymentSignature(options, list.get(0).getAccSecret());
					if (isEqual) {
						PaymentDetails2 paymentDetails = new PaymentDetails2();
						paymentDetails.setRazorpayPaymentId(paymentId);
						paymentDetails.setRazorpayOrderId(orderId);
						paymentDetails.setOrderId(orderId);
						paymentDetails.setRazorpaySignature(razorpaySignature);
						String ret = paymentDetails2Service.savePostPaymentDetails(paymentDetails, applicationDetailes);
						if(null != ret) {
							HttpHeaders headers = new HttpHeaders();
							String url = "/viewAppStatus?applicantNumber=" + applicationNumber;
							headers.setLocation(URI.create(url));
							return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
						}else {
							log.error("Payment failed and exception occured");
							HttpHeaders headers = new HttpHeaders();
							String url = "/error";
							headers.setLocation(URI.create(url));
							return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
						}
					}
				} catch (RazorpayException e) {
					log.error("Payment failed and exception occured", e.getMessage());
					log.error("Payment failed and exception occured");
					HttpHeaders headers = new HttpHeaders();
					String url = "/error";
					headers.setLocation(URI.create(url));
					return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
				}
			}
		} else {
			log.error("No Data Present forn the application Number");
			log.error("Payment failed and exception occured");
			HttpHeaders headers = new HttpHeaders();
			String url = "/error";
			headers.setLocation(URI.create(url));
			return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
		}
		log.error("Payment failed and exception occured");
		HttpHeaders headers = new HttpHeaders();
		String url = "/error";
		headers.setLocation(URI.create(url));
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
	
	
	@PostMapping(value = "/verification", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	@ResponseBody
	public void verification(@RequestParam Map<String, String> formParams) {
		log.info("Web hook verification called");
		log.info("return data " + formParams.toString());
	}

}
