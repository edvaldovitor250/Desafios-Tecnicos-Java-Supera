package br.com.edvaldo.todolist.domain.dto.task;

import java.time.LocalTime;

import br.com.edvaldo.todolist.domain.model.Priority;
import br.com.edvaldo.todolist.domain.model.TaskModel;

public record TaskResponseData(
    String title, String description, LocalTime startAt, LocalTime endAt, Priority priority) {

    public TaskResponseData(TaskModel task) {
        this(task.getTitle(), task.getDescription(), task.getStartAt(), task.getEndAt(), task.getPriority());
    }
}