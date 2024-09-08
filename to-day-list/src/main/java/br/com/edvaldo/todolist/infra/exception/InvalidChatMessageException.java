package br.com.edvaldo.todolist.infra.exception;

import br.com.edvaldo.todolist.domain.model.MessageModel;

public class InvalidChatMessageException extends RuntimeException {

    public InvalidChatMessageException(MessageModel messageModel) {

    }

}
