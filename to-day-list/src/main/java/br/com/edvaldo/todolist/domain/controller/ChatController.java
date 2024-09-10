package br.com.edvaldo.todolist.domain.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import br.com.edvaldo.todolist.domain.dto.chat.ChatRequestData;
import br.com.edvaldo.todolist.domain.dto.chat.ChatResponseData;
import br.com.edvaldo.todolist.domain.model.MessageModel;
import br.com.edvaldo.todolist.infra.exception.InvalidChatMessageException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS })
public class ChatController {

    private final RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @PostMapping("/chat")
    public ResponseEntity<MessageModel> chat(@RequestBody String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new InvalidChatMessageException("Prompt cannot be null or empty");
        }

        ChatRequestData requestDTO = new ChatRequestData(model, prompt);
        ChatResponseData responseDTO = restTemplate.postForObject(apiUrl, requestDTO, ChatResponseData.class);

        if (responseDTO == null || responseDTO.getChoices() == null || responseDTO.getChoices().isEmpty()) {
            throw new InvalidChatMessageException("Invalid chat response from external service");
        }

        MessageModel message = responseDTO.getChoices().get(0).getMessage();
        return ResponseEntity.ok(message);
    }
}
