package io.sporing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by nschutta on 6/16/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TodoRepositoryRESTTest {
    public static final String GO_TO_BANK = "go to bank";
    public static final String $_EMBEDDED_TODOS = "$._embedded.todos";
    public static final String TODOS_5555_URL = "/todos/5555";
    public static final String TODOS_URL = "/todos";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository repository;
    @Autowired
    private TodoListRepository listRepository;

    private TodoList todoList;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.repository.deleteAllInBatch();
        this.listRepository.deleteAllInBatch();

        todoList = this.listRepository.save(new TodoList("Errands"));
        this.repository.save(new Todo(todoList, "get milk", false));
        this.repository.save(new Todo(todoList, GO_TO_BANK, true));
        this.repository.save(new Todo(todoList, "schedule pickup", false));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void getTodos() throws Exception {
        this.mockMvc.perform(get(TODOS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_EMBEDDED_TODOS, hasSize(3)))
                .andExpect(jsonPath($_EMBEDDED_TODOS + "[1].todo", is(GO_TO_BANK)))
                .andExpect(jsonPath($_EMBEDDED_TODOS + "[1].completed", is(true)));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void deleteNonExistentTodo() throws Exception {
        this.mockMvc.perform(delete(TODOS_5555_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void getNonExistentTodo() throws Exception {
        this.mockMvc.perform(get(TODOS_5555_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void updateNonExistentTodo() throws Exception {
        this.mockMvc.perform(put(TODOS_5555_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void createTodo() throws Exception {
        TodoList myList = listRepository.findByTitle("Errands");
        String todoTitle = "feed cats";
        Todo newTodo = new Todo(myList, todoTitle, false);
        String newTodoJson = convertToJson(newTodo);

        this.mockMvc.perform(post(TODOS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newTodoJson))
                .andExpect(status().isCreated());
        this.mockMvc.perform(get(TODOS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_EMBEDDED_TODOS, hasSize(4)))
                .andExpect(jsonPath($_EMBEDDED_TODOS + "[3].todo", is(todoTitle)))
                .andExpect(jsonPath($_EMBEDDED_TODOS + "[3].completed", is(false)));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void updateTodo() throws Exception {
        Todo updateTodo = repository.findByTodo(GO_TO_BANK);
        String todoTitle = "go to credit union";
        updateTodo.setTodo(todoTitle);
        updateTodo.setCompleted(false);
        String updateTodoJson = convertToJson(updateTodo);

        this.mockMvc.perform(put("/todos/9")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateTodoJson))
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(get(TODOS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_EMBEDDED_TODOS, hasSize(3)))
                .andExpect(jsonPath($_EMBEDDED_TODOS + "[1].todo", is(todoTitle)))
                .andExpect(jsonPath($_EMBEDDED_TODOS + "[1].completed", is(false)));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void deleteTodo() throws Exception {
        this.mockMvc.perform(get(TODOS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_EMBEDDED_TODOS, hasSize(3)));
        this.mockMvc.perform(delete("/todos/25"))
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(get(TODOS_URL))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath($_EMBEDDED_TODOS, hasSize(2)));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void getOneTodo() throws Exception {
        this.mockMvc.perform(get("/todos/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todo", is(GO_TO_BANK)))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    protected String convertToJson(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}