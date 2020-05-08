package com.alunw.moany.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Account API", description= "Access accounts")
public class CustomController {
	@RequestMapping(value = "/custom", method = RequestMethod.POST)
	@ApiOperation(value = "API to GET Test list String", notes = "Get all string list")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "List retrieve success", response = List.class),
		@ApiResponse(code = 404, message = "Fail!", response = String.class)
	})
	public String custom(HttpServletRequest request) {
		return "custom";
	}
}