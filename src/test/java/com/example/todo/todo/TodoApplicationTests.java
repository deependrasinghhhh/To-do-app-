package com.example.todo.todo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.todo.TodoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A small integration test: Spring starts, H2 is configured, and the service
 * can persist and retrieve a todo through the real repository.
 */
@SpringBootTest(classes = TodoApplication.class)
@AutoConfigureMockMvc
class TodoApplicationTests {

    @Autowired
    private TodoService todoService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndReadsATodo() {
        TodoResponse created = todoService.create(new CreateTodoRequest("Use the H2 database", null, "MEDIUM"));

        TodoResponse found = todoService.findById(created.id());

        assertThat(found).isEqualTo(created);
    }

    @Test
    void createsATodoThroughTheHttpApi() throws Exception {
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test the HTTP API\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/api/todos/")))
                .andExpect(jsonPath("$.title").value("Test the HTTP API"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void rejectsABlankTitle() throws Exception {
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title is required."));
    }

    @Test
    void rejectsAnEmptyPatchRequest() throws Exception {
        TodoResponse todo = todoService.create(new CreateTodoRequest("A todo to update", null, "MEDIUM"));

        mockMvc.perform(patch("/api/todos/{id}", todo.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Provide a title, completed value, due date, or priority."));
    }

    @Test
    void returnsNotFoundForAnUnknownTodo() throws Exception {
        mockMvc.perform(get("/api/todos/{id}", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Todo with id 999999 was not found."));
    }
}
