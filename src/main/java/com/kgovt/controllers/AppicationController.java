package com.kgovt.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.kgovt.models.ApplicationDetailes;
import com.kgovt.models.Status;
import com.kgovt.services.AdminUsersService;
import com.kgovt.services.ApplicationDetailesService;
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
	private StatusService statusService;

	@Autowired
	private AdminUsersService adminUsersService;

	@GetMapping("/")
	public String getApplicationHome(Model model) {
		log.info("Hellooooo");
		return "index";
	}

	@GetMapping("/index")
	public String getIndex(Model model) {
		return "index";
	}
	
	@GetMapping("/policy")
	public String getPolicy(Model model) {
		return "/policy";
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
				return "redirect:/viewAppStatus?applicantNumber=" + applicationDetailes.applicantNumber;
			}
		} catch (Exception e) {
			logger.error("Exception while saving aapplication", e);
			model.addAttribute("error",
					"Ooops Unexpected Error occured while saving Application, Please contact System Administrator !");
			return "failure";
		}
		
	}

	@GetMapping("/viewAppStatus")
	public String viewAppStatus(Model model, @RequestParam String applicantNumber) {
		try {
			Status newStatus = statusService.findByApplicantNumber(Long.valueOf(applicantNumber));
			if (null == newStatus) {
				model.addAttribute("status", null);
			}else {
				model.addAttribute("comment", newStatus.getComment());
				model.addAttribute("status", newStatus.getStatus());
			}
			model.addAttribute("applicantNo", applicantNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "statusprogress";
	}
}
