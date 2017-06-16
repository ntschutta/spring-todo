package io.sporing;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by nschutta on 6/16/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TodoRepositoryRESTTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository repository;
    @Autowired
    private TodoListRepository listRepository;

    private TodoList todoList;

    @Before
    public void setup() throws Exception {
        this.repository.deleteAllInBatch();
        this.listRepository.deleteAllInBatch();

        todoList = this.listRepository.save(new TodoList("Errands"));
        this.repository.save(new Todo(todoList, "get milk", false));
        this.repository.save(new Todo(todoList, "go to bank", true));
        this.repository.save(new Todo(todoList, "schedule pickup", false));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void getTodos() throws Exception {
        this.mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todos", hasSize(3)))
                .andExpect(jsonPath("$._embedded.todos[1].todo", is("go to bank")))
                .andExpect(jsonPath("$._embedded.todos[1].completed", is(true)));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void deleteTodo() throws Exception {
        this.mockMvc.perform(delete("/todos/5"));
        this.mockMvc.perform(get("/todos"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$._embedded.todos", hasSize(2)));
    }
}