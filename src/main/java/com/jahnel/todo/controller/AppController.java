package com.jahnel.todo.controller;

import com.jahnel.todo.model.Task;
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
    public String addTask(Model model) {
        return "redirect:tasks";
    }
    
}