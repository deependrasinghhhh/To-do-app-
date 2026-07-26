package com.example.todo.todo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The service holds the application's use cases. Keeping this separate from
 * the controller makes the HTTP layer thin and the business rules reusable.
 */
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> findAll() {
        return todoRepository.findAllByOrderByIdDesc().stream()
                .map(TodoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TodoResponse findById(long id) {
        return TodoResponse.from(findTodo(id));
    }

    @Transactional
    public TodoResponse create(CreateTodoRequest request) {
        Todo savedTodo = todoRepository.save(new Todo(request.title(), request.dueDate(), request.priority()));
        return TodoResponse.from(savedTodo);
    }

    @Transactional
    public TodoResponse update(long id, UpdateTodoRequest request) {
        Todo todo = findTodo(id);
        todo.update(request.title(), request.completed(), request.dueDate(), request.priority());
        return TodoResponse.from(todoRepository.save(todo));
    }

    @Transactional
    public void delete(long id) {
        todoRepository.delete(findTodo(id));
    }

    private Todo findTodo(long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }
}
