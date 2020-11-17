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
        // Pass User's name for display in page
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping("/lists")
    public String taskLists(Model model) {
        // get userid and use it to get User's lists
        String usrId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskList> lists = listService.findAllUser(usrId);
        // pass data to model
        model.addAttribute("lists", lists);
        return "lists";
    }

    @GetMapping("/tasks")
    public String findTasks(@RequestParam String listId, Model model) {
        // Use listId from URL param to validate User owns list
        TaskList list = listService.validateUserAndGetList(listId);
        // return access denied page if listId is invalid for user
        if( list == null ){ return "error"; }
        // Get list of tasks for requested list
        var tasks = (List<Task>) taskService.findAllInList(list);
        // pass data to model
        model.addAttribute("list", list);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @GetMapping("/listEmpty")
    @ResponseBody
    public Map<String, Object> checkList(@RequestParam String listId) {
        // Use listId from URL param to validate User owns list
        TaskList list = listService.validateUserAndGetList(listId);
        // return access denied page if listId is invalid for user
        if( list == null ){ Collections.singletonMap("empty", false); }
        // Return if list is empty
        return Collections.singletonMap("empty", taskService.findAllInList(list).isEmpty());
    }


    @PostMapping("/addList")
    public String addList(@RequestBody MultiValueMap<String,String> form) {
        // Get User's ID
        String usrId = SecurityContextHolder.getContext().getAuthentication().getName();
        // Create new list from User ID and form data
        listRepository.save(new TaskList(form.get("listName").get(0), usrId));
        return "redirect:lists";
    }

    @PostMapping("/removeList")
    public String removeList(@RequestBody String json) {
        // Parse JSON request
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);
        // Use listId from JSON data to validate User owns list
        TaskList list = listService.validateUserAndGetList(result.get("listId").toString());
        // return access denied page if listId is invalid for user
        if( list == null ){ return "error"; }
        // delete requested list
        listRepository.deleteById(list.getListId());
        return "redirect:lists";
    }

    @PostMapping("/addTask")
    public String addTask(@RequestBody MultiValueMap<String,String> form) {
        // Use listId from form data to validate User owns list
        TaskList list = listService.validateUserAndGetList(form.get("listId").get(0).toString());
        // return access denied page if listId is invalid for user
        if( list == null ){ return "error"; }
        // create new task for user on requested list
        taskRepository.save(new Task(form.get("task").get(0), false, list));
        return "redirect:tasks?listId=" + list.getListId();
    }
    
    @PostMapping("/removeTask")
    public String removeTask(@RequestBody String json) {
        // Parse JSON request
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);
        // Use listId from JSON data to validate User owns list
        TaskList list = listService.validateUserAndGetList(result.get("listId").toString());
        // return access denied page if listId is invalid for user
        if( list == null ){ return "error"; }
        // delete requested task
        taskRepository.deleteById(((Number) result.get("taskId")).longValue());
        return "redirect:tasks?listId=" + list.getListId();
    }

    @PostMapping("/editTask")
    public String editTask(@RequestBody String json) {
        // Parse JSON request
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);
        // Use listId from JSON data to validate User owns list
        TaskList list = listService.validateUserAndGetList(result.get("listId").toString());
        // return access denied page if listId is invalid for user
        if( list == null ){ return "error"; }
        // find requested task
        Task target = taskRepository.findById(((Number) result.get("taskId")).longValue()).get();
        // edit task according to JSON data
        target.setTask(result.get("task").toString());
        // commit changes to database
        taskRepository.save(target);
        return "redirect:tasks?listId=" + list.getListId();
    }

    @PostMapping("/completeTask")
    public String completeTask(@RequestBody String json) {
        // Parse JSON request
        Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(json);
        // Use listId from JSON data to validate User owns list
        TaskList list = listService.validateUserAndGetList(result.get("listId").toString());
        // return access denied page if listId is invalid for user
        if( list == null ){ return "error"; }
        // find target task
        Task target = taskRepository.findById(((Number) result.get("taskId")).longValue()).get();
        // mark task as opposite of current state
        target.setComplete(!target.getComplete());
        // commit changes to database
        taskRepository.save(target);
        return "redirect:tasks?listId=" + list.getListId();
    }
}