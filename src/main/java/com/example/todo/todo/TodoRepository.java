package com.example.todo.todo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data creates the database implementation for this interface at runtime.
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByOrderByIdDesc();
}
