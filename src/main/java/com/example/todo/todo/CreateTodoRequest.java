package com.example.todo.todo;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The JSON shape accepted when a client creates a todo.
 */
public record CreateTodoRequest(
        @NotBlank(message = "Title is required.") @Size(max = 120, message = "Title must be at most 120 characters.") String title,
        LocalDate dueDate,
        @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Priority must be LOW, MEDIUM, or HIGH.") String priority,
        String category,
        String description) {

    public CreateTodoRequest(String title, LocalDate dueDate, String priority) {
        this(title, dueDate, priority, null, null);
    }

    public CreateTodoRequest {
        if (title != null) {
            title = title.strip();
        }
        if (priority == null) {
            priority = "MEDIUM";
        } else {
            priority = priority.toUpperCase();
        }
        if (category != null) {
            category = category.strip();
        }
        if (description != null) {
            description = description.strip();
        }
    }
}
