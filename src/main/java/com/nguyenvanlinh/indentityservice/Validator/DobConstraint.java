package com.nguyenvanlinh.indentityservice.Validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.validation.Validator;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = { DobValidator.class }
)
public @interface DobConstraint  {
    String message() default "Invalid Date of Birth";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
