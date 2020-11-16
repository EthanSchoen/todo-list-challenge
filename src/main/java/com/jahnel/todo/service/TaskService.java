package com.jahnel.todo.service;

import com.jahnel.todo.model.TaskList;

import java.util.Iterator;
import java.util.List;

import com.jahnel.todo.model.Task;
import com.jahnel.todo.repository.TaskRepository;
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

    @Override
    public List<Task> findAllInList(TaskList list) {
        List<Task> tasks = (List<Task>) repository.findAll();
        for( Iterator<Task> it = tasks.iterator(); it.hasNext(); ){
            Task t = it.next();
            if( !t.getTaskList().equals(list) ){
                it.remove();
            }
        }
        return tasks;
    }
}