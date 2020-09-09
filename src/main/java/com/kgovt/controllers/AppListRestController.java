package com.kgovt.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kgovt.datatable.paging.Page;
import com.kgovt.datatable.paging.PagingRequest;
import com.kgovt.models.ApplicationDetailes;
import com.kgovt.services.ApplicationDetailesService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AppListRestController {
	@Autowired
	private ApplicationDetailesService appicationService;
	
	@PostMapping("/allApplicationData")
	public Page<ApplicationDetailes> list(@RequestBody PagingRequest pagingRequest,@RequestParam String region, String status) {
		return appicationService.getApplicationDetailess(pagingRequest,region, status);
	}
	
	@PostMapping("/myWebhook")
	  public void getMyJson(@RequestBody Map<String, Object> json) {
	  System.out.println("WebHook collected JSON: " + json);
	  log.info("Web hook verification called5 rest");
		log.info("return data " + json.toString());
	}
}
