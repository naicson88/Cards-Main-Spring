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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LogginFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogginFilter.class);
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
		
		//LocalDateTime startTime = LocalDateTime.now();
		filterChain.doFilter(requestWrapper, responseWrapper);
		LocalDateTime timeTaken = LocalDateTime.now();
		
		String requestBody = getStringValue(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
//		String responseBody = getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());
//		RESPONSE={}; responseBody
		LOGGER.info(
				"FINISHED PROCESSING \n METHOD={}; REQUEST_URI={}; REQUEST_PAYLOAD={}; \n RESPONSE_CODE={}, TIME_TAKEN={}",
				 request.getMethod(), request.getRequestURI(), requestBody, response.getStatus(), timeTaken
				);
	
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
