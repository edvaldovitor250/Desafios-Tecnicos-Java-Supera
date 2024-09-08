package br.com.edvaldo.todolist.domain.dto.chat;

import java.util.List;

import br.com.edvaldo.todolist.domain.model.MessageModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseData {
    
    private List<Choice> choices;

    @Data
    public static class Choice {
        private MessageModel message;
        private int index;
    }
}
