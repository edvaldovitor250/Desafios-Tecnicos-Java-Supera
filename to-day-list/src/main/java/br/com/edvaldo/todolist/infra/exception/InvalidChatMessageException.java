package br.com.edvaldo.todolist.infra.exception;


public class InvalidChatMessageException extends RuntimeException {
    public InvalidChatMessageException(String message) {
        super(message);
    }
}
