package com.example.todo.todo;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Maps HTTP requests to the todo use cases. The browser UI and any future
 * mobile/desktop client can call these same JSON endpoints.
 */
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponse> findAll() {
        return todoService.findAll();
    }

    @GetMapping("/{id}")
    public TodoResponse findById(@PathVariable long id) {
        return todoService.findById(id);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody CreateTodoRequest request) {
        TodoResponse todo = todoService.create(request);
        return ResponseEntity
                .created(URI.create("/api/todos/" + todo.id()))
                .body(todo);
    }

    @PatchMapping("/{id}")
    public TodoResponse update(
            @PathVariable long id,
            @Valid @RequestBody UpdateTodoRequest request) {
        return todoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
