package com.bet.betwebservice.controller;

import com.bet.betwebservice.model.AuthenticationModel;
import com.bet.betwebservice.service.AuthenticationService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// @CrossOrigin("${service.client}")
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/Register")
    public AuthenticationModel register(@RequestBody JsonNode requestModel) throws Exception {
        return this.authenticationService.register(requestModel);
    }
    
    @PostMapping("/Login")
    public AuthenticationModel login(@RequestBody JsonNode requestModel) throws Exception {
       return this.authenticationService.login(requestModel);
    }

    @PostMapping("/GetForgotPasswordCode")
    public void getForgotPasswordCode(@RequestBody JsonNode requestModel) throws Exception {
       this.authenticationService.getForgotPasswordCode(requestModel);
    }

    @PostMapping("/ResetPassword")
    public void resetPassword(@RequestBody JsonNode requestModel) throws Exception {
       this.authenticationService.resetPassword(requestModel);
    }

    @PostMapping("/GetForgottenUsername")
    public void getForgottenUsername(@RequestBody JsonNode requestModel) throws Exception {
       this.authenticationService.getForgottenUsername(requestModel);
    }
}