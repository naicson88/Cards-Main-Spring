package com.naicson.yugioh.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import brave.Tracer;

@Component
public class LogginFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogginFilter.class);
	
	 @Autowired
     Tracer tracer;
	 
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
//		try {
//
//		}catch (HttpMessageNotReadableException e){
//			logger.error(e.getMessage());
//		}
		String ip = request.getHeader("X-Forwarded-For");
		LOGGER.info("IP: {}", ip);

		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

//		//LocalDateTime startTime = LocalDateTime.now();
//		filterChain.doFilter(requestWrapper, responseWrapper);
//		LocalDateTime timeTaken = LocalDateTime.now();
//
//		String requestBody = getStringValue(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
////		String responseBody = getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());
////		RESPONSE={}; responseBody,
//		if(!request.getRequestURI().contains("consulta-usuario")) {
//			LOGGER.info(
//					"FINISHED PROCESSING \n METHOD={}; REQUEST_URI={}; REQUEST_PAYLOAD={}; RESPONSE_CODE={}, TIME_TAKEN={}",
//					 request.getMethod(), request.getRequestURI(), requestBody, response.getStatus(), timeTaken
//					);
//		}

		//responseWrapper.addHeader("Request-Id", tracer.currentSpan().context().traceIdString());
		filterChain.doFilter(requestWrapper, responseWrapper);

		responseWrapper.setHeader("Request-Id", tracer.currentSpan().context().traceIdString());
		responseWrapper.copyBodyToResponse();
	}
	
	private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
		try {
			return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return "";
	}

}
