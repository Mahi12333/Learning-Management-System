package com.maven.neuto.exception.validetion;

import com.maven.neuto.annotation.CustomeValidetion;
import com.maven.neuto.annotation.OptionalField;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class CustomGenericDTOvalidetor implements ConstraintValidator<CustomeValidetion, Object> {
    @Override
    public void initialize(CustomeValidetion constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return false;
        }

        try {
            Class<?> clazz = dto.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(dto);

                // Ignore fields annotated with @OptionalField
                if (field.isAnnotationPresent(OptionalField.class)) {
                    continue;
                }

                // Check if value is null or empty (for String fields)
                if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                    log.info("GenericDTOValidator: {}", field.getName());
                    return buildViolation(context, field.getName(), field.getName() + " cannot be empty");
                }

                // Check if value is Long or Integer and must be greater than zero
                if ((value instanceof Long && (Long) value <= 0) ||
                        (value instanceof Integer && (Integer) value <= 0)) {
                    log.info("GenericDTOValidators: {}", field.getName());
                    return buildViolation(context, field.getName(), field.getName() + " must be greater than zero");
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean buildViolation(ConstraintValidatorContext context, String fieldName, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
        return false;
    }
}
