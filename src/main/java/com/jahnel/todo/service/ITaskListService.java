package com.jahnel.todo.service;

import java.util.List;

import com.jahnel.todo.model.TaskList;

public interface ITaskListService {
    List<TaskList> findAll();
    List<TaskList> findAllUser(String usr);
    TaskList validateUserAndGetList(String listId);
}