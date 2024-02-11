package com.bet.betwebservice.controller;

import com.bet.betwebservice.config.UserAuthProvider;
import com.bet.betwebservice.model.UserModel;
import com.bet.betwebservice.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;

    @Autowired
    public AuthController(UserService userService, UserAuthProvider userAuthProvider) {
        this.userService = userService;
        this.userAuthProvider = userAuthProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<UserModel> login(@RequestBody JsonNode requestModel) {
        UserModel userModel = this.userService.login(requestModel);
        userModel.setToken(this.userAuthProvider.createToken(userModel.getUsername()));
        return ResponseEntity.ok(userModel);
    }

    @PostMapping("/register")
    public ResponseEntity<UserModel> register(@RequestBody JsonNode requestModel) {
        UserModel userModel = this.userService.register(requestModel);
        userModel.setToken(this.userAuthProvider.createToken(userModel.getUsername()));
        return ResponseEntity.ok(userModel);
    }
}