package com.jahnel.todo.service;

import com.jahnel.todo.model.TaskList;
import com.jahnel.todo.repository.TaskListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class TaskListService implements ITaskListService {

    @Autowired
    private TaskListRepository repository;

    @Override
    public List<TaskList> findAll() {
        return (List<TaskList>) repository.findAll();
    }

    @Override
    public List<TaskList> findAllUser(String user) {
        List<TaskList> lists = (List<TaskList>) repository.findAll();
        for( Iterator<TaskList> it = lists.iterator(); it.hasNext(); ){
            TaskList l = it.next();
            if( !l.getUser().equals(user) ){
                it.remove();
            }
        }
        return lists;
    }

    @Override
    public TaskList validateUserAndGetList(String listId) {
        TaskList list = null;
        try {
            list = repository.findById(Long.parseLong(listId)).get();
            String usrId = SecurityContextHolder.getContext().getAuthentication().getName();
            if( !usrId.equals(list.getUser()) ){
                throw new Exception();
            }
        } catch (Exception e) {
            return null;
        }
        return list;
    }
}