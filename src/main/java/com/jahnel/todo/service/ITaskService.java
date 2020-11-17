package com.jahnel.todo.service;

import com.jahnel.todo.model.TaskList;
import com.jahnel.todo.model.Task;
import java.util.List;

public interface ITaskService {
    List<Task> findAll();
    List<Task> findAllInList(TaskList list);
}