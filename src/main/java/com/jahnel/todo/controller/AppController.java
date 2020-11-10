package com.jahnel.todo.controller;

import com.jahnel.todo.model.Task;
import com.jahnel.todo.repository.TaskRepository;
import com.jahnel.todo.bean.TaskForm;
import com.jahnel.todo.service.ITaskService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AppController {

    @Autowired
    private ITaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/error")
    public String error() {
        return "<h1>Error Page.</h1>";
    }

    @GetMapping("/tasks")
    public String findTasks(Model model) {

        var tasks = (List<Task>) taskService.findAll();

        model.addAttribute("tasks", tasks);

        return "showTasks";
    }

    @PostMapping("/add")
    public String addTask(TaskForm task, Model model) {
        System.out.println(task.getTask());
        taskRepository.save(new Task(task.getTask(), false));
        return "redirect:tasks";
    }
    
    @PostMapping("/remove")
    public String removeTask() {
        return "redirect:tasks";
    }

    @PostMapping("/edit")
    public String editTask() {
        return "redirect:tasks";
    }

    @PostMapping("/complete")
    public String completeTask() {
        return "redirect:tasks";
    }
}