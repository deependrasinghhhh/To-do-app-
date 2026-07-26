package com.example.todo.todo;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The JSON shape accepted for a partial update. Both fields are optional,
 * but the request must contain at least one of them.
 */
public record UpdateTodoRequest(
        @Size(min = 1, max = 120, message = "Title must be between 1 and 120 characters.") String title,
        Boolean completed,
        LocalDate dueDate,
        @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Priority must be LOW, MEDIUM, or HIGH.") String priority,
        String category,
        String description) {

    public UpdateTodoRequest(String title, Boolean completed, LocalDate dueDate, String priority) {
        this(title, completed, dueDate, priority, null, null);
    }

    public UpdateTodoRequest {
        if (title != null) {
            title = title.strip();
        }
        if (priority != null) {
            priority = priority.toUpperCase();
        }
        if (category != null) {
            category = category.strip();
        }
        if (description != null) {
            description = description.strip();
        }
    }

    @AssertTrue(message = "Provide a title, completed value, due date, priority, category, or description.")
    public boolean isUpdateRequested() {
        return title != null || completed != null || dueDate != null || priority != null || category != null
                || description != null;
    }
}
