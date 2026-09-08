package com.bcbs.member.service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestCorrelationFilter extends OncePerRequestFilter {
    private static final String REQUEST_ID = "requestId";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final Logger log = LoggerFactory.getLogger(RequestCorrelationFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException{
        String requestId = request.getHeader(REQUEST_ID_HEADER);

        if(requestId == null || requestId.isBlank()){
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(REQUEST_ID, requestId);

        response.setHeader(REQUEST_ID_HEADER, requestId);
        log.info("Request correlation initialized requestId={}", requestId);
        try{
            filterChain.doFilter(request, response);
        } finally{
            MDC.remove(REQUEST_ID);
        }
    }
}
