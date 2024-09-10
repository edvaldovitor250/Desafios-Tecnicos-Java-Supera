package br.com.edvaldo.todolist.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import br.com.edvaldo.todolist.domain.controller.ChatController;
import br.com.edvaldo.todolist.domain.dto.chat.ChatRequestData;
import br.com.edvaldo.todolist.domain.dto.chat.ChatResponseData;
import br.com.edvaldo.todolist.domain.model.MessageModel;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    @InjectMocks
    private ChatController chatController;

    @Mock
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    void testChatSuccess() throws Exception {
        MessageModel responseMessage = new MessageModel("assistant", "This is a response");
        ChatResponseData responseDTO = new ChatResponseData(List.of(new ChatResponseData.Choice()));

        when(restTemplate.postForObject(eq(apiUrl), any(ChatRequestData.class), eq(ChatResponseData.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"This is a test prompt\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("assistant"))
                .andExpect(jsonPath("$.content").value("This is a response"));
    }

    @Test
    void testChatInvalidPrompt() throws Exception {
        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Prompt cannot be null or empty"));
    }

    @Test
    void testChatInvalidResponse() throws Exception {
        when(restTemplate.postForObject(eq(apiUrl), any(ChatRequestData.class), eq(ChatResponseData.class)))
                .thenReturn(new ChatResponseData(List.of()));

        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"This is a test prompt\""))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Invalid chat response from external service"));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
