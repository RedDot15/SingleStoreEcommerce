package com.example.project_economic.validation.validator;

import com.example.project_economic.validation.annotation.AllOrNone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Objects;
import java.util.stream.Stream;

public class AllOrNoneValidator implements ConstraintValidator<AllOrNone, Object> {
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private String[] fields;

    @Override
    public void initialize(AllOrNone constraintAnnotation) {
        fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        long notNull = Stream.of(fields)
                .map(field -> PARSER.parseExpression(field).getValue(value))
                .filter(Objects::nonNull)
                .count();
        return notNull == 0 || notNull == fields.length;
    }
}
