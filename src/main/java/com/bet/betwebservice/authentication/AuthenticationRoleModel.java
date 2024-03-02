package com.bet.betwebservice.authentication;

import org.springframework.security.core.GrantedAuthority;

public class AuthenticationRoleModel implements GrantedAuthority {

    private String authority;

    public AuthenticationRoleModel(){
        super();
    }

    @Override
    public String getAuthority() {
        // TODO Auto-generated method stub
        return "USER";
    }
}
