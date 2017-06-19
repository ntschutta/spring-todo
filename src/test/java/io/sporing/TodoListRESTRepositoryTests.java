package io.sporing;

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
 * Created by nschutta on 6/19/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TodoListRESTRepositoryTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void getTodoList() throws Exception {
        this.mockMvc.perform(get("/todoList"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.todoList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.todoList[0].title", is("List 1")));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void deleteTodo() throws Exception {
        this.mockMvc.perform(delete("/todoList/1"));
        this.mockMvc.perform(get("/todoList"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$._embedded.todoList", hasSize(1)));
    }

}
