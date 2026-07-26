package com.example.todo.todo;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createRequestTrimsTitleAndRejectsBlankValues() {
        CreateTodoRequest request = new CreateTodoRequest("   ", null, "MEDIUM");
        Set<ConstraintViolation<CreateTodoRequest>> violations = validator.validate(request);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Title is required.");
    }

    @Test
    void updateRequestRequiresAtLeastOneField() {
        UpdateTodoRequest request = new UpdateTodoRequest(null, null, null, null);
        Set<ConstraintViolation<UpdateTodoRequest>> violations = validator.validate(request);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Provide a title, completed value, due date, priority, category, or description.");
    }

    @Test
    void updateRequestAcceptsValidPartialUpdates() {
        UpdateTodoRequest request = new UpdateTodoRequest("  Keep me  ", true, null, null);
        Set<ConstraintViolation<UpdateTodoRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
        assertThat(request.title()).isEqualTo("Keep me");
    }
}
