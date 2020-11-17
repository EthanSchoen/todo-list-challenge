package com.jahnel.todo;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnel.todo.model.Task;
import com.jahnel.todo.model.TaskList;
import com.jahnel.todo.repository.TaskListRepository;
import com.jahnel.todo.repository.TaskRepository;
import com.jahnel.todo.service.ITaskListService;
import com.jahnel.todo.service.ITaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TodoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TodoTest {
    
    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private ITaskListService listService;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private TaskListRepository listRepo;

    private boolean debugPrintAll = true;

    private static String usr1 = "1234567890";
    private static Map<String, Object> user1;
    static {
        user1 = new HashMap<>();
        user1.put("login", usr1);
        user1.put("email", "testing@test.org");
        user1.put("username", "xXtest_usernameXx");
        user1.put("name", "Test Users Name");
    }
    private static String usr2 = "0987654321";
    private static Map<String, Object> user2;
    static {
        user2 = new HashMap<>();
        user2.put("login", usr2);
        user2.put("email", "other@test.org");
        user2.put("username", "xXtest_otherXx");
        user2.put("name", "Other Users Name");
    }

    private static String[] lists = new String[]{"Test list 1", "Test list 2", "Test list 3", "Test list 4"};
    private static String[] tasks = new String[]{"Test task 1", "Test task 2", "Test task 3", "Test task 4"};

    private static String TOKEN_ATTR_NAME = "org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN";
    private HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
    private CsrfToken csrfToken = httpSessionCsrfTokenRepository.generateToken(new MockHttpServletRequest());

    @BeforeEach
    void setup() {
        taskRepo.deleteAll();
        listRepo.deleteAll();
        populateUser(usr1);
        populateUser(usr2);
    }

    @Test
    public void testAddList() throws Exception {
        boolean print = false;
        taskRepo.deleteAll();
        listRepo.deleteAll();
        if( print || debugPrintAll ) {
            System.out.println("Test Add Lists");
            System.out.println("-------------------------------------------------------------------");
        }
        MockHttpSession session = userSession(1);
        mockmvc.perform(MockMvcRequestBuilders.get("/lists").session(session))
            .andExpect(status().isOk());
        for( String t : lists ) {
            addList(t, session);
        }
        session = userSession(2);
        for( String t : lists ) {
            addList(t, session);
        }
        if( print || debugPrintAll ){
            printUser(usr1);
            printUser(usr2);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println();
        }
    }

    @Test
    public void testAddTask() throws Exception {
        boolean print = false;
        taskRepo.deleteAll();
        if( print || debugPrintAll ){
            System.out.println("Test Add Tasks");
            System.out.println("-------------------------------------------------------------------");
        }
        MockHttpSession session = userSession(1);
        addTasksToAllLists(tasks, usr1, session);
        session = userSession(2);
        addTasksToAllLists(tasks, usr2, session);
        if( print || debugPrintAll ){
            printUser(usr1);
            printUser(usr2);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println();
        }
    }

    @Test
    public void testEditTask() throws Exception {
        boolean print = false;
        if( print || debugPrintAll ){
            System.out.println("Test Edit Tasks");
            System.out.println("-------------------------------------------------------------------");
        }
        MockHttpSession session = userSession(1);
        for( TaskList l : listService.findAllUser(usr1) ){
            for ( Task t : taskService.findAllInList(l) ){
                mockmvc.perform(MockMvcRequestBuilders.post("/editTask")
                        .session(session)
                        .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                            Map.of("listId", l.getListId(), "taskId", t.getTaskId(), "task", t.getTask().toUpperCase())
                        )))
                    .andExpect(redirectedUrl("tasks?listId=" + l.getListId()))
                    .andExpect(status().is3xxRedirection());
            }
        }
        if( print || debugPrintAll ){
            printUser(usr1);
            printUser(usr2);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println();
        }
    }

    @Test
    public void testCompleteTask() throws Exception {
        boolean print = false;
        if( print || debugPrintAll ){
            System.out.println("Test Complete Tasks");
            System.out.println("-------------------------------------------------------------------");
        }
        MockHttpSession session = userSession(2);
        for( TaskList l : listService.findAllUser(usr2) ){
            for ( Task t : taskService.findAllInList(l) ){
                mockmvc.perform(MockMvcRequestBuilders.post("/completeTask")
                        .session(session)
                        .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                            Map.of("listId", l.getListId(), "taskId", t.getTaskId())
                        )))
                    .andExpect(redirectedUrl("tasks?listId=" + l.getListId()))
                    .andExpect(status().is3xxRedirection());
            }
        }
        if( print || debugPrintAll ){
            printUser(usr1);
            printUser(usr2);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println();
        }
    }

    @Test
    public void testRemoveTask() throws Exception {
        boolean print = false;
        if( print || debugPrintAll ){
            System.out.println("Test Remove Tasks");
            System.out.println("-------------------------------------------------------------------");
        }
        MockHttpSession session = userSession(1);
        for( TaskList l : listService.findAllUser(usr1) ){
            for ( Task t : taskService.findAllInList(l) ){
                mockmvc.perform(MockMvcRequestBuilders.post("/removeTask")
                        .session(session)
                        .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                            Map.of("listId", l.getListId(), "taskId", t.getTaskId())
                        )))
                    .andExpect(redirectedUrl("tasks?listId=" + l.getListId()))
                    .andExpect(status().is3xxRedirection());
            }
        }
        if( print || debugPrintAll ){
            printUser(usr1);
            printUser(usr2);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println();
        }
    }

    @Test
    public void testRemoveList() throws Exception {
        boolean print = false;
        if( print || debugPrintAll ){
            System.out.println("Test Remove Lists");
            System.out.println("-------------------------------------------------------------------");
        }
        MockHttpSession session = userSession(2);

        for( TaskList l : listService.findAllUser(usr2) ){
            mockmvc.perform(MockMvcRequestBuilders.post("/removeList")
                    .session(session)
                    .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                    .param(csrfToken.getParameterName(), csrfToken.getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(
                        Map.of("listId", l.getListId())
                    )))
                .andExpect(redirectedUrl("lists"))
                .andExpect(status().is3xxRedirection());
        }
        if( print || debugPrintAll ){
            printUser(usr1);
            printUser(usr2);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println();
        }
    }

    @Test
    public void testAuth() throws Exception {
        mockmvc.perform(MockMvcRequestBuilders.get("/"))
            .andExpect(status().isOk());
        mockmvc.perform(MockMvcRequestBuilders.get("/user"))
            .andExpect(status().is(401));
        mockmvc.perform(MockMvcRequestBuilders.get("/lists"))
            .andExpect(status().is(401));
        mockmvc.perform(MockMvcRequestBuilders.get("/tasks?listId=9999"))
            .andExpect(status().is(401));

        MockHttpSession session = userSession(1);

        mockmvc.perform(MockMvcRequestBuilders.get("/").session(session))
            .andExpect(status().isOk());
        mockmvc.perform(MockMvcRequestBuilders.get("/user").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test Users Name"));
        mockmvc.perform(MockMvcRequestBuilders.get("/lists").session(session))
            .andExpect(status().isOk());

        session = userSession(2);

        mockmvc.perform(MockMvcRequestBuilders.get("/user").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Other Users Name"));
    }

/****************************************************************************************************************
 * 
 *  Utility Methods
 * 
 ****************************************************************************************************************/

    private void addList(String name, MockHttpSession session) throws Exception {
        mockmvc.perform(MockMvcRequestBuilders.post("/addList")
                .session(session)
                .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                .param(csrfToken.getParameterName(), csrfToken.getToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(StandardCharsets.UTF_8.encode("listName=" + name).array()))
            .andExpect(redirectedUrl("lists"))
            .andExpect(status().is3xxRedirection());
    }

    private void addTask(Long id, String task, MockHttpSession session) throws Exception {
        mockmvc.perform(MockMvcRequestBuilders.post("/addTask")
                .session(session)
                .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                .param(csrfToken.getParameterName(), csrfToken.getToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(StandardCharsets.UTF_8.encode("listId=" + id + "&task=" + task).array()))
            .andExpect(redirectedUrl("tasks?listId=" + id))
            .andExpect(status().is3xxRedirection());
    }

    private void addTasksToAllLists(String[] tlist, String usr, MockHttpSession session) throws Exception {
        for( TaskList l : listService.findAllUser(usr) ){
            for( String t : tlist ){
                addTask(l.getListId(), t, session);
            }
        }
    }

    private void populateUser( String usr ){
        for( String l : lists ){
            TaskList tl = new TaskList(l, usr);
            listRepo.save(tl);
            for( String t : tasks ){
                taskRepo.save(new Task(t, false, tl));
            }
        }
    }

    private static MockHttpSession userSession(int u) {
        Map<String, Object> attributes = (u == 1) ? user1 : user2;

        List<GrantedAuthority> authorities = Collections.singletonList(new OAuth2UserAuthority("ROLE_USER", attributes));
        OAuth2User user = new DefaultOAuth2User(authorities, attributes, "login");
        OAuth2AuthenticationToken principal = new OAuth2AuthenticationToken(user, authorities, "whatever");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            new SecurityContextImpl(principal));
        return session;
    }

    private void printUser(String id) {
        // System.out.println("User: " + id );
        for( TaskList l : listService.findAllUser(id) ){
            System.out.println(l.getListId() + "\t" + l.getUser() + "\t" + l.getName() + ":");
            System.out.println("-----------------------------------------------------------");
            for ( Task t : taskService.findAllInList(l) ){
                System.out.println("\t\t\t" + " " + t.getTask() + " | " + ((t.getComplete()) ? "completed" : "incomplete"));
            }
            System.out.println();
        }
        System.out.println();
    }
}