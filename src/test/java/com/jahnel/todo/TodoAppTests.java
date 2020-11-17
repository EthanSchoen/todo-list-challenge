package com.jahnel.todo;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnel.todo.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TodoAppTests {
    
    @Autowired
    private MockMvc mvc;

    @Autowired
    private TaskRepository taskRepo;

    @BeforeEach
    void setup() {
        taskRepo.deleteAll();
    }

    @Test
    void loadIndex() throws Exception {
        this.mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Show tasks")));
    }

    @Test
    void loadTasks() throws Exception {
        this.mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<th class=\"opcolumn\">Operation</th>")));
    }

    @Test
    void addTask() throws Exception {
        addTaskString("This is test task 1");
        addTaskString("This is test task 2");
        addTaskString("This is test task 3");
        addTaskString("This is test task 4");
    }

    @Test
    void removeTask() throws Exception {
        ArrayList<String> tasks = new ArrayList<String>();
        for( int i = 0; i < 10; i++ ){
            tasks.add("This is test task " + i);
        }
        HashMap<String, Integer> idMap = new HashMap<String, Integer>();
        for( String t : tasks) {
            idMap = addTaskString(t);
        }

        Random r = new Random( 123 );  // so tests are repeatable
        int s = tasks.size(), i;
        while(s != 0) {
            i = r.nextInt(tasks.size());
            idMap = removeTaskHelper(idMap, tasks, tasks.get(i));
            assert (idMap.size() == s-1);
            tasks.remove(i);
            s = idMap.size();
        }
    }

    @Test
    void editTask() throws Exception {
        addTaskString("Test task 0");
        HashMap<String, Integer> idMap = addTaskString("Test task 1");
        Map<String, Object> tmp = Map.of("id", idMap.get("Test task 1"), "task", "Test task 1 new string");

        this.mvc.perform(post("/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(tmp)))
                .andExpect(redirectedUrl("tasks"))
                .andExpect(status().is3xxRedirection());

        this.mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Test task 0")))
                .andExpect(content().string(containsString("Test task 1 new string")));
    }

    @Test
    void completeTask() throws Exception {
        addTaskString("Test task 0");
        HashMap<String, Integer> idMap = addTaskString("Test task 1");
        completeTaskHelper(idMap, "Test task 1", true);
        completeTaskHelper(idMap, "Test task 0", true);
        completeTaskHelper(idMap, "Test task 0", false);
        completeTaskHelper(idMap, "Test task 1", false);
    }

    HashMap<String, Integer> readTasks(ByteArrayOutputStream baos) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        String output = baos.toString();
        output = output.substring(output.indexOf("<html"), output.indexOf("</html>"));
        int index = 0;
        while (true) {
            index = output.indexOf("<tr id=\"", index);
            if(index == -1) { break; } else { index += 8; }
            int tmp = output.indexOf("<td class=\"taskcolumn\">",index) + 23;
            map.put(output.substring(tmp, output.indexOf("</td>", tmp)),
                    Integer.parseInt(output.substring(index, output.indexOf("\"",index))));
        };
        return map;
    }

    HashMap<String, Integer> addTaskString(String task) throws Exception {
        this.mvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(StandardCharsets.UTF_8.encode("task=" + task).array()))
                .andExpect(redirectedUrl("tasks"))
                .andExpect(status().is3xxRedirection());

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        this.mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(task)))
                .andDo(print(outStream));

        return readTasks(outStream);
    }

    HashMap<String, Integer> removeTaskHelper(HashMap<String, Integer> idMap, ArrayList<String> tasks, String target) throws Exception{
        Map<String, Integer> tmp = Map.of("id", idMap.get(target));
        this.mvc.perform(post("/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(tmp)))
                .andExpect(redirectedUrl("tasks"))
                .andExpect(status().is3xxRedirection());
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ResultActions temp = this.mvc.perform(get("/tasks")).andExpect(status().isOk()).andDo(print(outStream));
        for( String t : tasks ) {
            if( t != target) {
                temp.andExpect(content().string(containsString(t)));
            }
        }
        return readTasks(outStream);
    }

    void completeTaskHelper(HashMap<String, Integer> idMap, String target, boolean expected) throws Exception{
        Map<String, Object> tmp = Map.of("id", idMap.get(target));
        this.mvc.perform(post("/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(tmp)))
                .andExpect(redirectedUrl("tasks"))
                .andExpect(status().is3xxRedirection());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andDo(print(baos));

        String output = baos.toString();
        output = output.substring(output.indexOf("<html"), output.indexOf("</html>"));
        int temp = output.indexOf("<tr id=\"" + idMap.get(target) + "\">");
        output = output.substring(temp, output.indexOf("</tr>", temp));

        assert ((output.indexOf("style=\"text-decoration:line-through;\"") == -1) ^ expected);
    }
}
