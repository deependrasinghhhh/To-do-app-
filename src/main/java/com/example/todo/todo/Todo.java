package com.example.todo.todo;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity is a Java object that JPA maps to a database table.
 */
@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(length = 20)
    private String priority;

    // JPA needs a no-argument constructor to rebuild an entity from the database.
    protected Todo() {
    }

    public Todo(String title) {
        this(title, null, "MEDIUM");
    }

    public Todo(String title, LocalDate dueDate, String priority) {
        this.title = title;
        this.completed = false;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    /**
     * Applies only values that were supplied in a PATCH request.
     */
    public void update(String title, Boolean completed, LocalDate dueDate, String priority) {
        if (title != null) {
            this.title = title;
        }
        if (completed != null) {
            this.completed = completed;
        }
        if (dueDate != null) {
            this.dueDate = dueDate;
        }
        if (priority != null) {
            this.priority = priority;
        }
    }
}
