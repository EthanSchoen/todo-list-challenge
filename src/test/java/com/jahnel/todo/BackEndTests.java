package com.jahnel.todo;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TodoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BackEndTests {
    
    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ITaskListService listService;

    @Autowired
    private TaskListRepository listRepository;

    private static Map<String, Object> user1;
    static {
        user1 = new HashMap<>();
        user1.put("name", "1234567890");
        user1.put("email", "testing@test.org");
        user1.put("login", "xXtest_usernameXx");
        user1.put("username", "Test Users Name");
    }

    private static Map<String, Object> user2;
    static {
        user2 = new HashMap<>();
        user2.put("name", "0987654321");
        user2.put("email", "other@test.org");
        user2.put("login", "xXtest_otherXx");
        user2.put("username", "Other Users Name");
    }

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        listRepository.deleteAll();
    }

    @Test
    public void testAddList() throws Exception {
        MockHttpSession session = userSession(1);
        mockmvc.perform(MockMvcRequestBuilders.get("/lists").session(session))
            .andExpect(status().isOk());
        printLists();
        addList("Test list 1", session);
        printLists();
    }

    private static String TOKEN_ATTR_NAME = "org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN";
    private HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
    private CsrfToken csrfToken = httpSessionCsrfTokenRepository.generateToken(new MockHttpServletRequest());

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
            .andExpect(jsonPath("$.username").value("Test Users Name"));
        mockmvc.perform(MockMvcRequestBuilders.get("/lists").session(session))
            .andExpect(status().isOk());

        session = userSession(2);

        mockmvc.perform(MockMvcRequestBuilders.get("/user").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("Other Users Name"));
    }

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

    private void printLists() {
        System.out.println("All lists: " + listService.findAll());
        System.out.println("User 1 lists: " + listService.findAllUser("1234567890"));
        System.out.println("User 2 lists: " + listService.findAllUser("0987654321"));
    }
}