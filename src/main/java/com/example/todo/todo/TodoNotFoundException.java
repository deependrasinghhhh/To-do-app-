package com.example.todo.todo;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(long id) {
        super("Todo with id " + id + " was not found.");
    }
}
