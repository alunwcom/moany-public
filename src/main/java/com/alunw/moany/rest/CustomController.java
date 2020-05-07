package com.alunw.moany.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;

@RestController
@Api(tags = "Custom Controller")
public class CustomController {
	@RequestMapping(value = "/custom", method = RequestMethod.POST)
	@ApiResponse(code = 200, message = "Success - of course!")
	public String custom(HttpServletRequest request) {
		return "custom";
	}
}