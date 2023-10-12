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

    public LogEntity(String message, int httpStatusCode, String httpStatus, ZonedDateTime time, String path, String exceptionType) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.httpStatusCode = httpStatusCode;
        this.time = time;
        this.path = path;
        this.exceptionType = exceptionType;
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
}
