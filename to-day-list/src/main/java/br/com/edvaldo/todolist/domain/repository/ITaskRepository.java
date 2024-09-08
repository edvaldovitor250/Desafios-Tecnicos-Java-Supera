package br.com.edvaldo.todolist.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.edvaldo.todolist.domain.model.TaskModel;

@Repository
public interface ITaskRepository extends JpaRepository<TaskModel, Long> {
    
    List<TaskModel> findByUserLogin(String login);

}