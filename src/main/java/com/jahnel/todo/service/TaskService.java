package com.jahnel.todo.service;

import com.jahnel.todo.model.Task;
import com.jahnel.todo.repository.TaskRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService implements ITaskService {

    @Autowired
    private TaskRepository repository;

    @Override
    public List<Task> findAll() {
        return (List<Task>) repository.findAll();
    }
}