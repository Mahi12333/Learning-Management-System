package com.maven.neuto.exception;

import org.springframework.http.HttpStatus;

public class APIException extends RuntimeException{
    private static final long serialVersionUID = 1;
    private final HttpStatus status;

    public HttpStatus getStatus(){
        return status;
    }

    public APIException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
