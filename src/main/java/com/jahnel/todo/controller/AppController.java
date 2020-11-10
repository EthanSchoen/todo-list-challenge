package com.jahnel.todo.controller;

import com.jahnel.todo.model.Task;
import com.jahnel.todo.repository.TaskRepository;
import com.jahnel.todo.service.ITaskService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.util.MultiValueMap;

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

    // @GetMapping("/error")
    // public String error() {
    //     return "error";
    // }

    @GetMapping("/tasks")
    public String findTasks(Model model) {
        var tasks = (List<Task>) taskService.findAll();
        model.addAttribute("tasks", tasks);
        return "showTasks";
    }

    @PostMapping("/add")
    public String addTask(@RequestBody MultiValueMap<String,String> form) {
        taskRepository.save(new Task(form.get("task").get(0), false));
        return "redirect:tasks";
    }
    
    @PostMapping("/remove")
    public String removeTask(@RequestBody String json) {
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);
        taskRepository.deleteById(((Number) result.get("id")).longValue());
        return "redirect:tasks";
    }

    @PostMapping("/edit")
    public String editTask(@RequestBody String json) {
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);
        Task target = taskRepository.findById(((Number) result.get("id")).longValue()).get();
        target.setTask(result.get("task").toString());
        taskRepository.save(target);
        return "redirect:tasks";
    }

    @PostMapping("/complete")
    public String completeTask(@RequestBody String json) {
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);
        Task target = taskRepository.findById(((Number) result.get("id")).longValue()).get();
        target.setComplete(!target.getComplete());
        taskRepository.save(target);
        return "redirect:tasks";
    }
}