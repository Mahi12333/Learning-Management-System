package com.maven.neuto.exception;

//import static java.lang.StringTemplate.STR;  //! Java 21

public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;
    String message;
    Object fieldValue;

    public ResourceNotFoundException(String resourceName, String field, String fieldName) {
        super(STR." \{resourceName} not found with \{field}: \{fieldName}");
        this.field = field;
        this.fieldName = fieldName;
        this.resourceName = resourceName;
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

    public ResourceNotFoundException(String fieldName, String message) {
        super(String.format("%s%s", fieldName, message));
        this.fieldName = fieldName;
        this.message = message;
    }
    public ResourceNotFoundException(String message) {
        //super(String.format("%s", message));
        super(STR."\{message}");
        this.message = message;
    }

}
