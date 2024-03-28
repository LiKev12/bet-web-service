package com.bet.betwebservice.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Component
public class CustomizedCommonsRequestLoggingFilter extends AbstractRequestLoggingFilter {

    @Override
    protected void beforeRequest(HttpServletRequest httpServletRequest, String message) {
        this.logger.debug(message);
    }

    @Override
    protected boolean shouldLog(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRequestURI() != null && !httpServletRequest.equals("/actuator");
    }

    @Override
    protected void afterRequest(HttpServletRequest httpServletRequest, String message) {

    }
}