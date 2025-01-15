package com.example.project_economic.validation.annotation;

import com.example.project_economic.validation.validator.AllOrNoneValidator;
import com.example.project_economic.validation.validator.MatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MatchValidator.class)
public @interface Match {
    String[] fields();

    String message() default "These field must match.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
