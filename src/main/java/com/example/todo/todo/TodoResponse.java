package com.example.todo.todo;

import java.time.LocalDate;

/**
 * The JSON shape returned by the API. Returning a DTO instead of the entity
 * gives the API a stable, intentional contract.
 */
public record TodoResponse(Long id, String title, boolean completed, LocalDate dueDate, String priority,
        String category, String description) {

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted(), todo.getDueDate(),
                todo.getPriority(), todo.getCategory(), todo.getDescription());
    }
}
