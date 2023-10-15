package com.naicson.yugioh.util;

import cardscommons.exceptions.ApiExceptionsDTO;
import cardscommons.exceptions.ErrorMessage;
import com.naicson.yugioh.entity.LogEntity;
import com.naicson.yugioh.repository.LogEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @Autowired
    LogEntityRepository logRepository;

       public void saveLogEntity(Exception e, HttpStatus http, String url){
           logRepository.save(new LogEntity(e.getMessage(), http.value(), http.getReasonPhrase(),
                   ZonedDateTime.now(), url, e.getClass().toString(),e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName()));
       }

       @ExceptionHandler(value = {Exception.class})
		public ResponseEntity<ApiExceptionsDTO> handleExceptionError(Exception e, HttpServletRequest request) {
           ApiExceptionsDTO ex = new ApiExceptionsDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
                   ZonedDateTime.now(), request.getRequestURL().toString(), e.getClass().toString());

           saveLogEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURL().toString());

			logger.error(e.getMessage());
			return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR.value()).body(ex);
		}

    // Handler Exception for Spring Validation !!!!!! ///
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    ResponseEntity<Object> handleConstraintViolationException(MethodArgumentNotValidException em, HttpServletRequest request) {
        List<FieldError> fieldErrors = em.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.get(0).getDefaultMessage();
        ApiExceptionsDTO ex = new ApiExceptionsDTO(errorMessage, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(), request.getRequestURL().toString(), em.getClass().toString());
//
//        logRepository.save(new LogEntity(errorMessage, HttpStatus.BAD_REQUEST.value(),
//                HttpStatus.BAD_REQUEST.getReasonPhrase(), ZonedDateTime.now(), request.getRequestURL().toString(), em.getClass().toString()));

        saveLogEntity(em, HttpStatus.BAD_REQUEST, request.getRequestURL().toString());

        logger.error("MethodArgumentNotValidException: {}", errorMessage);
        return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleValiationException(IllegalArgumentException e, HttpServletRequest request) {
        ApiExceptionsDTO ex = new ApiExceptionsDTO(e.getMessage(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(), request.getRequestURL().toString(), e.getClass().toString());

//        logRepository.save(new LogEntity(e.getMessage(), HttpStatus.BAD_REQUEST.value(),
//                HttpStatus.BAD_REQUEST.getReasonPhrase(), ZonedDateTime.now(), request.getRequestURL().toString(), "IllegalArgumentException"));

        saveLogEntity(e, HttpStatus.BAD_REQUEST, request.getRequestURL().toString());

        logger.error(e.getMessage());
        return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    public ResponseEntity<Object> handleNotFoundlErros(NoSuchElementException e, HttpServletRequest request) {
        ApiExceptionsDTO ex = new ApiExceptionsDTO(e.getMessage(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND,
                ZonedDateTime.now(), request.getRequestURL().toString(), e.getClass().toString());

//        logRepository.save(new LogEntity(e.getMessage(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
//                ZonedDateTime.now(), request.getRequestURL().toString(), "NoSuchElementException"));

        saveLogEntity(e, HttpStatus.NOT_FOUND, request.getRequestURL().toString());

        logger.error("NoSuchElementException: {}", e.getMessage());
        return new ResponseEntity<>(ex, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundlErros(EntityNotFoundException e, HttpServletRequest request){
        ApiExceptionsDTO ex = new ApiExceptionsDTO(e.getMessage(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND,
                ZonedDateTime.now(), request.getRequestURL().toString(), e.getClass().toString());

//        logRepository.save(new LogEntity(e.getMessage(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
//                ZonedDateTime.now(), request.getRequestURL().toString(), "EntityNotFoundException"));
        saveLogEntity(e, HttpStatus.NOT_FOUND, request.getRequestURL().toString());
        logger.error(e.getMessage());
        return new ResponseEntity<>(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {SQLException.class})
    public ResponseEntity<Object> handleSQLException(SQLException sql, HttpServletRequest request) {
        ApiExceptionsDTO ex = new ApiExceptionsDTO(sql.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
                ZonedDateTime.now(), request.getRequestURL().toString(), sql.getClass().toString());

//        logRepository.save(new LogEntity(sql.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                ZonedDateTime.now(), request.getRequestURL().toString(), "SQLException"));
        saveLogEntity(sql, HttpStatus.NOT_FOUND, request.getRequestURL().toString());
        logger.error("SQLException: {}", ex.getMsg());
        return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {ErrorMessage.class})
    public ResponseEntity<Object> handleErrorMessage(ErrorMessage em, HttpServletRequest request) {
        ApiExceptionsDTO ex = new ApiExceptionsDTO(em.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
                ZonedDateTime.now(), request.getRequestURL().toString(), em.getClass().toString());

//        logRepository.save(new LogEntity(em.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                ZonedDateTime.now(), request.getRequestURL().toString(), "ErrorMessage"));
        saveLogEntity(em, HttpStatus.NOT_FOUND, request.getRequestURL().toString());
        logger.error("ErrorMessage: {}", em.getMessage());
        return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
