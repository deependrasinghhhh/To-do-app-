package com.example.todo.todo;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoController todoController;

    @Test
    void findAllReturnsAllTodosFromTheService() {
        TodoResponse first = new TodoResponse(1L, "First", false, null, "MEDIUM", null, null);
        TodoResponse second = new TodoResponse(2L, "Second", true, null, "LOW", null, null);
        given(todoService.findAll()).willReturn(List.of(first, second));

        List<TodoResponse> response = todoController.findAll();

        assertThat(response).containsExactly(first, second);
    }

    @Test
    void findByIdDelegatesToTheService() {
        TodoResponse response = new TodoResponse(7L, "Test", true, null, "HIGH", null, null);
        given(todoService.findById(7L)).willReturn(response);

        TodoResponse result = todoController.findById(7L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void createReturnsCreatedResponseWithLocationHeader() {
        CreateTodoRequest request = new CreateTodoRequest("Write tests", null, "MEDIUM");
        TodoResponse created = new TodoResponse(9L, "Write tests", false, null, "MEDIUM", null, null);
        given(todoService.create(any(CreateTodoRequest.class))).willReturn(created);

        ResponseEntity<TodoResponse> response = todoController.create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(created);
        assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create("/api/todos/9"));
    }

    @Test
    void updateDelegatesToTheService() {
        UpdateTodoRequest request = new UpdateTodoRequest("Updated", true, null, null);
        TodoResponse response = new TodoResponse(3L, "Updated", true, null, "MEDIUM", null, null);
        given(todoService.update(3L, request)).willReturn(response);

        TodoResponse result = todoController.update(3L, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void deleteReturnsNoContentAndInvokesService() {
        ResponseEntity<Void> response = todoController.delete(4L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(todoService).delete(4L);
    }
}
