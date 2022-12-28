package com.naicson.yugioh.data.builders;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.util.ApiResponse;

@Component
public class ResponseBuilder {
	
	public ResponseEntity<ApiResponse<Object>> buildResponse(
			HttpHeaders header, int statusCode, String message, Object data, Map<String, Object> otherParams){
		
		return new ApiResponse.ApiResponseBuilder<>(statusCode, message)
				.withHttpHeader(header)
				.withData(data)
				.withOtherParams(otherParams)
				.build();
	}
	
    public ResponseEntity<ApiResponse<Object>> buildResponse(
            int httpStatusCode, String message, Object data, Map <String, Object> otherParams) {
        return new ApiResponse.ApiResponseBuilder <> (httpStatusCode, message)
                .withData(data).withOtherParams(otherParams).build();
    }

    public ResponseEntity <ApiResponse<Object>>buildResponse(
            int httpStatusCode, String message, Map <String, Object> otherParams) {
        return new ApiResponse.ApiResponseBuilder <> (httpStatusCode, message)
                .withOtherParams(otherParams).build();
    }

    public ResponseEntity <ApiResponse<Object>> buildResponse(
            HttpHeaders httpHeader, int httpStatusCode, String message, Object data) {
        return new ApiResponse.ApiResponseBuilder <> (httpStatusCode, message)
                .withHttpHeader(httpHeader).withData(data).build();
    }

    public ResponseEntity <ApiResponse<Object>> buildResponse(
            HttpHeaders httpHeader, int httpStatusCode, String message, Map <String, Object> otherParams) {
        return new ApiResponse.ApiResponseBuilder <> (httpStatusCode, message)
                .withHttpHeader(httpHeader).withOtherParams(otherParams).build();
    }

    public ResponseEntity <ApiResponse<Object>> buildResponse(
            HttpHeaders httpHeader, int httpStatusCode, String message) {
        return new ApiResponse.ApiResponseBuilder <> (httpStatusCode, message)
                .withHttpHeader(httpHeader).build();
    }

    public ResponseEntity <ApiResponse<Object>> buildResponse(
            int httpStatusCode, String message, Object data) {
        return new ApiResponse.ApiResponseBuilder <> (httpStatusCode, message)
                .withData(data).build();
    }
}
