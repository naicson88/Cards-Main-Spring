package com.naicson.yugioh.util;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "httpHeaders", "httpStatusCode", "message", "data", "otherParams" })
public class ApiResponse<T> {
	
	private final HttpHeaders httpHeaders;
	private final int httpStatusCode;
	private final String message;
	private final T data;
	private final Map<String, Object> otherParams;
	
    private ApiResponse(ApiResponseBuilder<T> builder) {
        this.httpHeaders = builder.httpHeaders;
        this.httpStatusCode = builder.httpStatusCode;
        this.message = builder.message;
        this.data = (T) builder.data;
        this.otherParams = builder.otherParams;
    }

	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}

	public Map<String, Object> getOtherParams() {
		return otherParams;
	}
	public static class ApiResponseBuilder<T> {
		private HttpHeaders httpHeaders = new HttpHeaders();
		private final int httpStatusCode;
        private final String message;
        private T data;
        private Map<String, Object> otherParams = Collections.emptyMap();
        
        public ApiResponseBuilder(int statusCode, String message) {
    		this.httpStatusCode = statusCode;
    		this.message = message;
    	}
        

        public ApiResponseBuilder<T> withHttpHeader(HttpHeaders httpHeader) {
            this.httpHeaders = httpHeader;
            return this;
        }

        public ApiResponseBuilder<T> withData(T data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder<T> withOtherParams(Map<String, Object> otherParams) {
            this.otherParams = otherParams;
            return this;
        }
        
        public ResponseEntity<ApiResponse<T>> build() {
        	ApiResponse<T> apiResponse = new ApiResponse<>(this);
        	
	        return	ResponseEntity
	        		.status(apiResponse.getHttpStatusCode())
	        		.headers(apiResponse.getHttpHeaders())
	        		.body(apiResponse);
         }
        
	}
    
}
