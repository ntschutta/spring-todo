package io.sporing;

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
 * Created by nschutta on 6/19/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TodoListRESTRepositoryTests {
    public static final String TODO_LIST_URL = "/todoList";
    public static final String TODO_LIST_5555_URL = "/todoList/5555";
    public static final String $_EMBEDDED_TODO_LIST = "$._embedded.todoList";

    @Autowired
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private TodoListRepository listRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void getAllTodoLists() throws Exception {
        this.mockMvc.perform(get(TODO_LIST_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_EMBEDDED_TODO_LIST, hasSize(2)))
                .andExpect(jsonPath($_EMBEDDED_TODO_LIST + "[0].title", is("Things to Do")));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void createTodoList() throws Exception {
        TodoList newTodoList = new TodoList("List Title");
        String newTodoJson = convertToJson(newTodoList);

        this.mockMvc.perform(post(TODO_LIST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newTodoJson))
                .andExpect(status().isCreated());
        this.mockMvc.perform(get(TODO_LIST_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_EMBEDDED_TODO_LIST, hasSize(3)))
                .andExpect(jsonPath($_EMBEDDED_TODO_LIST + "[2].title", is("List Title")));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void updateTodo() throws Exception {
        TodoList updateTodoList = listRepository.findByTitle("List 2");
        String todoListTitle = "Things to Do";
        updateTodoList.setTitle(todoListTitle);
        String updateTodoLisstJson = convertToJson(updateTodoList);

        this.mockMvc.perform(put(TODO_LIST_URL + "/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateTodoLisstJson))
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(get(TODO_LIST_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_EMBEDDED_TODO_LIST, hasSize(3)))
                .andExpect(jsonPath($_EMBEDDED_TODO_LIST + "[1].title", is(todoListTitle)));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void deleteTodoList() throws Exception {
        this.mockMvc.perform(delete(TODO_LIST_URL + "/1"));
        this.mockMvc.perform(get(TODO_LIST_URL))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$._embedded.todoList", hasSize(2)));
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void deleteNonExistentTodoList() throws Exception {
        this.mockMvc.perform(delete(TODO_LIST_5555_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void getNonExistentTodoList() throws Exception {
        this.mockMvc.perform(get(TODO_LIST_5555_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void updateNonExistentTodoList() throws Exception {
        this.mockMvc.perform(put(TODO_LIST_5555_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="admin",roles={"ALL"})
    public void getOneTodoList() throws Exception {
        this.mockMvc.perform(get(TODO_LIST_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("List 1")));
    }

    protected String convertToJson(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
