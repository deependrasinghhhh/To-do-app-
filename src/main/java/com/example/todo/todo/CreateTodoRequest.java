package com.example.todo.todo;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The JSON shape accepted when a client creates a todo.
 */
public record CreateTodoRequest(
        @NotBlank(message = "Title is required.")
        @Size(max = 120, message = "Title must be at most 120 characters.")
        String title,
        LocalDate dueDate,
        @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Priority must be LOW, MEDIUM, or HIGH.")
        String priority) {

    public CreateTodoRequest {
        if (title != null) {
            title = title.strip();
        }
        if (priority == null) {
            priority = "MEDIUM";
        } else {
            priority = priority.toUpperCase();
        }
    }
}
