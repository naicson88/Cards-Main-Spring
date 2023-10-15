package com.naicson.yugioh.entity;

import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "tab_log_entity")
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private UUID id;
    @Column(length = 3000)
    private  String message;
    private  int httpStatusCode;
    private String httpStatus;
    private ZonedDateTime time;
    private String path;
    private String exceptionType;
    private String exceptionClassOccurred;
    private String exceptionMethodOccurred;

    public LogEntity(String message, int httpStatusCode, String httpStatus, ZonedDateTime time, String path, String exceptionType, String exceptionClassOccurred, String exceptionMethodOccurred) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.time = time;
        this.path = path;
        this.exceptionType = exceptionType;
        this.exceptionClassOccurred = exceptionClassOccurred;
        this.exceptionMethodOccurred = exceptionMethodOccurred;
    }

    public LogEntity(String message, String path, String exceptionType) {
        this.message = message;
        this.path = path;
        this.exceptionType = exceptionType;
        this.time = ZonedDateTime.now();
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        this.httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public UUID getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return httpStatusCode;
    }

    public void setStatusCode(int statusCode) {
        this.httpStatusCode = statusCode;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getExceptionClassOccurred() {
        return exceptionClassOccurred;
    }

    public void setExceptionClassOccurred(String exceptionClassOccurred) {
        this.exceptionClassOccurred = exceptionClassOccurred;
    }

    public String getExceptionMethodOccurred() {
        return exceptionMethodOccurred;
    }

    public void setExceptionMethodOccurred(String exceptionMethodOccurred) {
        this.exceptionMethodOccurred = exceptionMethodOccurred;
    }
}
