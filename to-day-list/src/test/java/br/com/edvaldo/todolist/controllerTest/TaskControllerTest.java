package br.com.edvaldo.todolist.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.edvaldo.todolist.domain.controller.TaskController;
import br.com.edvaldo.todolist.domain.dto.task.TaskRequestData;
import br.com.edvaldo.todolist.domain.dto.task.TaskResponseData;
import br.com.edvaldo.todolist.domain.dto.task.TaskUpdateData;
import br.com.edvaldo.todolist.domain.model.Priority;
import br.com.edvaldo.todolist.domain.model.TaskModel;
import br.com.edvaldo.todolist.domain.repository.ITaskRepository;
import br.com.edvaldo.todolist.infra.exception.TaskNotFoundException;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private ITaskRepository taskRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void testCreateTask() throws Exception {
        TaskRequestData requestData = new TaskRequestData("Test Title", "Test Description", LocalTime.of(9, 0), LocalTime.of(17, 0), Priority.BAIXA, "testUser");
        TaskModel task = new TaskModel(requestData);
        task.setId(1L);

        when(taskRepository.save(any(TaskModel.class))).thenReturn(task);

        mockMvc.perform(post("/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.startAt").value("09:00"))
                .andExpect(jsonPath("$.endAt").value("17:00"))
                .andExpect(jsonPath("$.priority").value("BAIXA"));
    }

    @Test
    void testListTasks() throws Exception {
        TaskModel task = new TaskModel(null, "Test Title", "Test Description", LocalTime.of(9, 0), LocalTime.of(17, 0), Priority.MEDIA, "testUser", null);
        task.setId(1L);
        List<TaskModel> tasks = List.of(task);

        when(taskRepository.findByUserLogin("testUser")).thenReturn(tasks);

        mockMvc.perform(get("/tasks/list/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].startAt").value("09:00"))
                .andExpect(jsonPath("$[0].endAt").value("17:00"))
                .andExpect(jsonPath("$[0].priority").value("MEDIA"));
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskUpdateData updateData = new TaskUpdateData(1L, "Updated Title", "Updated Description", LocalTime.of(10, 0), LocalTime.of(18, 0), Priority.ALTA);
        TaskModel existingTask = new TaskModel();
        existingTask.setId(1L);

        when(taskRepository.getReferenceById(1L)).thenReturn(existingTask);
        when(taskRepository.save(any(TaskModel.class))).thenReturn(existingTask);

        mockMvc.perform(put("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.startAt").value("10:00"))
                .andExpect(jsonPath("$.endAt").value("18:00"))
                .andExpect(jsonPath("$.priority").value("ALTA"));
    }

    @Test
    void testDeleteTask() throws Exception {
        TaskModel task = new TaskModel();
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTaskNotFound() throws Exception {
        when(taskRepository.findById(1L)).thenThrow(new TaskNotFoundException("Task não encontrada"));

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNotFound());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
