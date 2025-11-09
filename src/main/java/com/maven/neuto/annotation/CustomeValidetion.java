package com.maven.neuto.annotation;

import com.maven.neuto.exception.validetion.CustomGenericDTOvalidetor;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomGenericDTOvalidetor.class)
public @interface CustomeValidetion {
    String message() default "Please fill all details";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
