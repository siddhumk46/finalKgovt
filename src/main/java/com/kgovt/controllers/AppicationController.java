package com.kgovt.controllers;

import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kgovt.models.AdminUsers;
import com.kgovt.models.ApplicationDetailes;
import com.kgovt.models.PaymentDetails;
import com.kgovt.models.PaymentDetails2;
import com.kgovt.models.Status;
import com.kgovt.services.AdminUsersService;
import com.kgovt.services.ApplicationDetailesService;
import com.kgovt.services.PaymentDetails2Service;
import com.kgovt.services.PaymentDetailsService;
import com.kgovt.services.StatusService;
import com.kgovt.utils.AppConstants;
import com.kgovt.utils.AppUtilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class AppicationController extends AppConstants {

	private static final Logger logger = LoggerFactory.getLogger(AppicationController.class);

	@Autowired
	private ApplicationDetailesService appicationService;

	@Autowired
	private PaymentDetailsService paymentDetailsService;

	@Autowired
	private PaymentDetails2Service paymentDetails2Service;

	@Autowired
	private StatusService statusService;

	@Autowired
	private AdminUsersService adminUsersService;

	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

	@GetMapping("/")
	public String getApplicationHome(Model model) {
		return "index";
	}

	@GetMapping("/index")
	public String getIndex(Model model) {
		return "index";
	}

	@PostMapping(value = "/statusprogress", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Map getStatusprogress(Model model, @RequestParam String mobileNumber, @RequestParam String password)
			throws NullPointerException {
		HashMap<String, Object> returnData = new HashMap<>();
		returnData.put("ERROR", "0");
		ApplicationDetailes appDetails = appicationService.findByMobile(Long.valueOf(mobileNumber));
		if (null != appDetails) {
			if (AppUtilities.isNotNullAndNotEmpty(password)) {
				String mobile = String.valueOf(mobileNumber);
				String last4digits = mobile.substring(6, 10);

				DateFormat dateFormat = new SimpleDateFormat("yyyy");
				String strDate = dateFormat.format(appDetails.getDob());
				String concatInputs = last4digits + strDate.replace("-", "");

				if (concatInputs.equals(password)) {
					returnData.put("applicantNumber", appDetails.applicantNumber);
					//return "redirect:/viewAppStatus?applicantNumber=" + appDetails.applicantNumber;
				} else {
					returnData.put("ERROR", "1");//"Invalid Credentital"
				}
			} 
		} else {
			returnData.put("ERROR", "2");
		}
		return returnData;
	}

	@GetMapping("/contact")
	public String contactPage() {
		return "contact";
	}

	@GetMapping("/offline")
	public String offlinePage() {
		return "offline";
	}

	@GetMapping("/status")
	public String status(Model model) {
		Status status = new Status();
		model.addAttribute("status", status);
		return "status";
	}

	@GetMapping("/logout")
	public String logout() {
		return "redirect:/index";
	}

	@GetMapping("/new")
	public String applicationNew(Model model) {
		ApplicationDetailes appDetails = new ApplicationDetailes();
		model.addAttribute("applicationDetailes", appDetails);
		return "form";
	}

	@GetMapping("/redirectIndex")
	public String redirectIndex(Model model, PaymentDetails paymentDetails) {
		try {
			model.addAttribute("failure", "Payment Failed");
			appicationService.removeApplicationDetailes(paymentDetails.getApplicantNumber());
		} catch (Exception e) {
			logger.error("Exception while saving aapplication", e);
		}
		return "redirect:/indexFailure";
	}

	@GetMapping("/saveAdmin")
	public String saveAdmin() {
		AdminUsers admin = new AdminUsers();
		admin.setPassword("admin123");
		admin.setRegion("Dharwad");
		admin.setRole("ROLE_ADMIN");
		adminUsersService.saveAdmin(admin);
		return "";
	}

	@GetMapping("/indexFailure")
	public String indexPage(Model model) {
		model.addAttribute("failure", "Payment Failed");
		return "index";
	}

	@PostMapping("/validateMobile")
	@ResponseBody
	public String checkMobileExiastes(@RequestParam String mobileNumber) {
		Long mobileCount = appicationService.countByMobile(Long.valueOf(mobileNumber));
		if (mobileCount > 0) {
			return "1";
		} else {
			return "0";
		}
	}

	@GetMapping("/checkStatus")
	public String checkStatus(Model model, @RequestParam String mobileNumber, @RequestParam String password) {
		return "statusprogress";
	}

	@PostMapping("/save")
	public String saveApplication(Model model, ApplicationDetailes applicationDetailes, HttpServletRequest request,
			@RequestParam("sslcFile") MultipartFile sslcFile, @RequestParam("pucFile") MultipartFile pucFile,
			@RequestParam("ugFile") MultipartFile ugFile, @RequestParam("pgFile") MultipartFile pgFile,
			@RequestParam("photoFile") MultipartFile photoFile, @RequestParam("addressFile") MultipartFile addressFile,
			@RequestParam("certificateFile") MultipartFile certificateFile,
			@RequestParam("nocFile") MultipartFile nocFile,
			@RequestParam("signatureFile") MultipartFile signatureFile) {
		try {
			applicationDetailes = appicationService.saveApplicationAction(applicationDetailes, sslcFile, pucFile,
					ugFile, pgFile, photoFile, addressFile, certificateFile, nocFile, signatureFile);
			if (null == applicationDetailes) {
				model.addAttribute("error",
						"Ooops Unexpected Error occured while saving Application, Please contact System Administrator !");
				return "failure";
			} else {
//				model.addAttribute("STATUSTYPE", "SAVE");
//				model.addAttribute("applicationDetailes", applicationDetailes);
//				model.addAttribute("applicationNumber",applicationDetailes.getApplicantNumber()); 
//				model.addAttribute("applicationStatus",applicationDetailes.getApplicationStatus()); 
//				return "success";	
				model.addAttribute("applicantNo", applicationDetailes.getApplicantNumber());
				model.addAttribute("status", null);
				return "statusprogress";
			}
		} catch (Exception e) {
			logger.error("Exception while saving aapplication", e);
		}
		return "success";
	}

	@GetMapping("/makeFirstPayment")
	public String makeFirstPayment(Model model, @RequestParam Long applicationNumber) {
		try {
			ApplicationDetailes applicationDetailes = appicationService.findByApplicantNumber(applicationNumber);
			PaymentDetails paymentDetails = new PaymentDetails();
			paymentDetails.setAmount(PAYMENT1);
			paymentDetails.setMobile(applicationDetailes.getMobile());
			paymentDetails.setEmail(applicationDetailes.getEmail());
			paymentDetails.setAddress(applicationDetailes.getResAddress());
			paymentDetails.setReceiptNo(AppUtilities.generateReceptNo(applicationDetailes));
			paymentDetails.setDescription("A Payment for Application Submission");
			paymentDetails.setApplicantNumber(applicationDetailes.getApplicantNumber());
			paymentDetails = appicationService.proceedFirstPayment(paymentDetails);
			if (null == paymentDetails) {
				model.addAttribute("error", "Payment details are not retrived");
				return "failure";
			} else {
				model.addAttribute("paymentDetails", paymentDetails);
				return "payment";
			}

		} catch (Exception e) {
			log.error("Error making first Payment", e.getMessage());
			model.addAttribute("error", "Exception While making first payment, Please contact Administrator");
			return "failure";
		}
	}

	@PostMapping("/makeFirstPaymentComplete")
	public String makeFirstPaymentComplete(Model model, PaymentDetails paymentDetails) {
		String status = null;
		try {
			String data = paymentDetails.getRazorpayOrderId() + "|" + paymentDetails.getRazorpayPaymentId();
			status = calculateRFC2104HMAC(data, SECRET);
		} catch (SignatureException e1) {
			// TODO Auto-generated catch block
			log.error("Error making first Payment", e1.getMessage());
			model.addAttribute("error", "Error making first Payment");
			return "failure";
		}
		if (null != status && paymentDetails.getRazorpaySignature().equals(status)) {
			try {
				logger.info("Exception of Payement save started");
				paymentDetails.setCreatedDate(new Date());
				paymentDetails.setStatus("Success");
				paymentDetailsService.savePaymentDetails(paymentDetails);

				logger.info("Exception of Payement save started");
				Status appStatus = new Status();
				appStatus.setApplicantNumber(paymentDetails.getApplicantNumber());
				appStatus.setStatus("I");
				appStatus.setMobile(paymentDetails.getMobile());
				appStatus.setAppliedDate(new Date());
				// appStatus.setComment();
				statusService.saveStatus(appStatus);

				return "redirect:/viewAppStatus?applicantNumber=" + paymentDetails.getApplicantNumber();

			} catch (Exception e) {
				log.error("Exception while saving aapplication", e.getMessage());
				model.addAttribute("error", "Error making first Payment");
				return "failure";
			}
		} else {
			model.addAttribute("error",
					"Payment successful and did not validated, Please contact Service Administrator/helpline numbers");
			return "failure";
		}
	}

	@GetMapping("/makeSecondPayment")
	public String makeSecondPayment(Model model, @RequestParam Long applicationNumber) {
		try {
			ApplicationDetailes applicationDetailes = appicationService.findByApplicantNumber(applicationNumber);
			PaymentDetails2 paymentDetails = new PaymentDetails2();
			paymentDetails.setAmount(PAYMENT2);
			paymentDetails.setMobile(applicationDetailes.getMobile());
			paymentDetails.setEmail(applicationDetailes.getEmail());
			paymentDetails.setAddress(applicationDetailes.getResAddress());
			paymentDetails.setReceiptNo(AppUtilities.generateReceptNo(applicationDetailes));
			paymentDetails.setDescription("A Payment for Application Submission");
			paymentDetails.setApplicantNumber(applicationDetailes.getApplicantNumber());
			paymentDetails = appicationService.proceedSecondPayment(paymentDetails);
			if (null == paymentDetails) {
				model.addAttribute("error", "Payment details are not retrived");
				return "failure";
			} else {
				model.addAttribute("paymentDetails", paymentDetails);
				return "payment2";
			}
		} catch (Exception e) {
			log.error("Error making first Payment", e.getMessage());
			model.addAttribute("error", "Exception While making first payment, Please contact Administrator");
			return "failure";
		}
	}

	@PostMapping("/makeSecondPaymentComplete")
	public String makeSecondPaymentComplete(Model model, PaymentDetails2 paymentDetails) {
		String status = null;
		try {
			String data = paymentDetails.getRazorpayOrderId() + "|" + paymentDetails.getRazorpayPaymentId();
			status = calculateRFC2104HMAC(data, SECRET);
		} catch (SignatureException e1) {
			// TODO Auto-generated catch block
			log.error("Error making second Payment", e1.getMessage());
			model.addAttribute("error", "Error making second Payment");
			return "failure";
		}
		if (null != status && paymentDetails.getRazorpaySignature().equals(status)) {
			try {
				logger.info("Exception of Payement save started");
				paymentDetails.setCreatedDate(new Date());
				paymentDetails.setStatus("Success");
				paymentDetails2Service.savePaymentDetails(paymentDetails);
				logger.info("Exception of Payement save started");
				Status newStatus = statusService.findByApplicantNumber(paymentDetails.getApplicantNumber());
				newStatus.setStatus("C");
				statusService.saveStatus(newStatus);
				return "redirect:/viewAppStatus?applicantNumber=" + paymentDetails.getApplicantNumber();

			} catch (Exception e) {
				log.error("Exception while saving aapplication", e.getMessage());
				model.addAttribute("failure", "Error making second Payment");
				return "failure";
			}
		} else {
			model.addAttribute("error",
					"Payment successful and did not validated, Please contact Service Administrator/helpline numbers");
			return "failure";
		}
	}

	public static String calculateRFC2104HMAC(String data, String secret) throws java.security.SignatureException {
		String result;
		try {
			// get an hmac_sha256 key from the raw secret bytes
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA256_ALGORITHM);
			// get an hmac_sha256 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(signingKey);
			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());
			// base64-encode the hmac
			result = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();
		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}

	@GetMapping("/viewAppStatus")
	public String viewAppStatus(Model model, @RequestParam String applicantNumber) {
		try {
			Status newStatus = statusService.findByApplicantNumber(Long.valueOf(applicantNumber));
			if (null == newStatus) {
				model.addAttribute("status", null);
			} else {
				model.addAttribute("status", newStatus.getStatus());
			}
			model.addAttribute("applicantNo", applicantNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "statusprogress";
	}
}
