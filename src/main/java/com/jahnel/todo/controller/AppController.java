package com.jahnel.todo.controller;

import com.jahnel.todo.model.Task;
import com.jahnel.todo.model.TaskList;
import com.jahnel.todo.repository.TaskListRepository;
import com.jahnel.todo.repository.TaskRepository;
import com.jahnel.todo.service.ITaskListService;
import com.jahnel.todo.service.ITaskService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.MultiValueMap;

@Controller
public class AppController {

    @Autowired
    private ITaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ITaskListService listService;

    @Autowired
    private TaskListRepository listRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/user")
    @ResponseBody
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping("/lists")
    public String taskLists(Model model) {
        String usrId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskList> lists = listService.findAllUser(usrId);
        model.addAttribute("lists", lists);
        return "lists";
    }

    @GetMapping("/tasks")
    public String findTasks(@RequestParam String listId, Model model) {
        TaskList list = listService.validateUserAndGetList(listId);
        if( list == null ){ return "accessDenied"; }
        var tasks = (List<Task>) taskService.findAllInList(list);

        model.addAttribute("list", list);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @PostMapping("/addList")
    public String addList(@RequestBody MultiValueMap<String,String> form) {
        String usrId = SecurityContextHolder.getContext().getAuthentication().getName();
        listRepository.save(new TaskList(form.get("listName").get(0), usrId));
        return "redirect:lists";
    }

    @PostMapping("/removeList")
    public String removeList(@RequestBody String json) {
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);

        TaskList list = listService.validateUserAndGetList(result.get("listId").toString());
        if( list == null ){ return "accessDenied"; }

        listRepository.deleteById(list.getListId());
        return "redirect:lists";
    }

    @PostMapping("/addTask")
    public String addTask(@RequestBody MultiValueMap<String,String> form) {
        taskRepository.save(new Task(form.get("task").get(0), false));
        return "redirect:tasks";
    }
    
    @PostMapping("/removeTask")
    public String removeTask(@RequestBody String json) {
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);
        taskRepository.deleteById(((Number) result.get("id")).longValue());
        return "redirect:tasks";
    }

    @PostMapping("/editTask")
    public String editTask(@RequestBody String json) {
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);
        Task target = taskRepository.findById(((Number) result.get("id")).longValue()).get();
        target.setTask(result.get("task").toString());
        taskRepository.save(target);
        return "redirect:tasks";
    }

    @PostMapping("/completeTask")
    public String completeTask(@RequestBody String json) {
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);
        Task target = taskRepository.findById(((Number) result.get("id")).longValue()).get();
        target.setComplete(!target.getComplete());
        taskRepository.save(target);
        return "redirect:tasks";
    }
}