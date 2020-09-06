package com.kgovt.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kgovt.models.AdminUsers;
import com.kgovt.models.ApplicationDetailes;
import com.kgovt.models.Status;
import com.kgovt.services.AdminUsersService;
import com.kgovt.services.ApplicationDetailesService;
import com.kgovt.services.StatusService;
import com.kgovt.utils.AppConstants;
import com.kgovt.utils.AppUtilities;

@Controller
@RequestMapping("/admin")
public class AdminController extends AppConstants {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private AdminUsersService adminUsersService;
	
	@Autowired
	private ApplicationDetailesService appicationService;
	
	@Autowired
	private StatusService statusService;
	
	@Autowired
    private ServletContext servletContext;
	
	@GetMapping("/applicationList")
	public String applicationList() {
		return "application_list";
	}
	
	@GetMapping("/")
	public String adminHome() {
		return "adminlogin";
	}
	
	@GetMapping("/login")
	public String adminLogin() {
		return "adminlogin";
	}
	
	@GetMapping("/logout")
	public String adminLogout() {
		return "redirect:/login";
	}
	
	@GetMapping("/viewAppData")
	public String viewAppData(Model model, @RequestParam String applicantNumber) {
		try {
			ApplicationDetailes appDetails = appicationService.findByApplicantNumber(Long.valueOf(applicantNumber));
			model.addAttribute("applicationDetailes", appDetails);
		} catch (Exception e) {
		}
		return "applicationview";
	}
	
	@PostMapping(value = "/acceptViewData", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Map appectedData(@RequestParam Long adminId, @RequestParam Long applicantNo,@RequestParam String status,@RequestParam String comment) {
		HashMap<String, Object> returnData = new HashMap<>();
		returnData.put("ERROR", "0");
		returnData.put("TYPE", status);
		try {
			// update aap details
			ApplicationDetailes appDetails = appicationService.findByApplicantNumber(applicantNo);
			appDetails.setApplicationStatus(status);
			appicationService.saveApplicationDetailes(appDetails);
			//update status
			Status stat=statusService.findByApplicantNumber(applicantNo);
			stat.setStatus(status);
			stat.setComment(comment);
			stat.setAdminId(adminId);
			statusService.saveStatus(stat);			
		}catch(Exception e) {
			returnData.put("ERROR", e);
		}
		return returnData;
	}
	
	@GetMapping("/saveAdmin")
	public String saveAdmin() {
		AdminUsers admin =new AdminUsers();
		admin.setPassword("admin123");
		admin.setRegion("Dharwad");
		admin.setRole("ROLE_ADMIN");
		adminUsersService.saveAdmin(admin);
		return "";
	}
	

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	@ResponseBody
	public  ResponseEntity<InputStreamResource>  downloadFile(@Param(value = "photoFile") String photoFile)throws IOException {
		if (AppUtilities.isNotNullAndNotEmpty(photoFile)) {
			String[] fileNames = photoFile.split("_");
			String path = fileNames[0];
			String rootPath = System.getProperty("/var/apito-s3");
			String folderStored = rootPath + File.separator +"Uploads"+ File.separator+ path + File.separator + photoFile;
			 File downloadFile= new File(folderStored); 
			 //return new FileSystemResource(new File(folderStored));
			 
			 InputStreamResource resource = new InputStreamResource(new FileInputStream(downloadFile));
			 MediaType mediaType = AppUtilities.getMediaTypeForFileName(this.servletContext, downloadFile.getName());
		        return ResponseEntity.ok()
		                // Content-Disposition
		                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + downloadFile.getName())
		                // Content-Type
		                .contentType(mediaType)
		                // Contet-Length
		                .contentLength(downloadFile.length()) //
		                .body(resource);
		} else {
			return null;
		}

	}

}
