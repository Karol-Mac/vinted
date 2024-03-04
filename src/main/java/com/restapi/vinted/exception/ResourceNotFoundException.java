package com.restapi.vinted.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resource;
    private final String fieldName;
    private final long value;

    public ResourceNotFoundException(String resource, String fieldName, long value) {
        super(resource+" not found with "+fieldName+" = "+ value);
        this.resource = resource;
        this.fieldName = fieldName;
        this.value = value;
    }
}
