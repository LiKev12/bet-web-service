package com.bet.betwebservice.authentication;

import lombok.Data;

@Data
public class JwtTokenWrapper {
   private String jwtToken;
   private String jwtTokenSubject;
}