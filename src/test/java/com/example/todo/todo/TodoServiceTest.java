package com.example.todo.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void createsAnIncompleteTodoWithATrimmedTitle() {
        given(todoRepository.save(any(Todo.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        TodoResponse created = todoService.create(new CreateTodoRequest("  Learn Spring Boot  ", null, "MEDIUM"));

        assertThat(created.title()).isEqualTo("Learn Spring Boot");
        assertThat(created.completed()).isFalse();
    }

    @Test
    void updatesOnlyTheFieldsProvidedByAPatchRequest() {
        Todo todo = new Todo("Write a test");
        given(todoRepository.findById(7L)).willReturn(Optional.of(todo));
        given(todoRepository.save(todo)).willReturn(todo);

        TodoResponse updated = todoService.update(7L, new UpdateTodoRequest(null, true, null, null));

        assertThat(updated.title()).isEqualTo("Write a test");
        assertThat(updated.completed()).isTrue();
    }

    @Test
    void storesDueDateAndPriorityWhenProvided() {
        given(todoRepository.save(any(Todo.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        TodoResponse created = todoService.create(new CreateTodoRequest("Plan launch", LocalDate.of(2026, 8, 10), "HIGH"));

        assertThat(created.title()).isEqualTo("Plan launch");
        assertThat(created.dueDate()).isEqualTo(LocalDate.of(2026, 8, 10));
        assertThat(created.priority()).isEqualTo("HIGH");
    }

    @Test
    void throwsAHelpfulExceptionForAnUnknownTodo() {
        given(todoRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.findById(99L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessage("Todo with id 99 was not found.");
    }
}
