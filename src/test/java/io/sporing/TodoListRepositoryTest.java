package io.sporing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by nschutta on 6/19/17.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class TodoListRepositoryTest {
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private TodoListRepository listRepository;

    private TodoList todoList;

    private String listTitle = "Things To Do";


    @Before
    public void setup() throws Exception {
        this.todoRepository.deleteAllInBatch();
        this.listRepository.deleteAllInBatch();

        Todo todo1 = new Todo(todoList, "get milk", false);
        Todo todo2 = new Todo(todoList, "go to bank", true);
        Todo todo3 = new Todo(todoList, "schedule pickup", false);

        this.todoRepository.save(todo1);
        this.todoRepository.save(todo2);
        this.todoRepository.save(todo3);
        List<Todo> todos = new ArrayList<>();
        todos.add(todo1);
        todos.add(todo2);
        todos.add(todo3);

        todoList = new TodoList(listTitle);
        todoList.setTodos(todos);
        this.listRepository.save(todoList);
    }

    @Test
    public void testCreate() {
        String newListTitle = "A New Todo List";
        TodoList newList = new TodoList(newListTitle);
        listRepository.save(newList);

        TodoList retrievedList = listRepository.findByTitle(newListTitle);
        assertThat(retrievedList).isNotNull();
        assertThat(retrievedList.getTitle()).isEqualTo(newListTitle);
    }

    @Test
    public void testFindByTitle() {
        TodoList retrievedList = listRepository.findByTitle(listTitle);
        assertThat(retrievedList.getTitle()).isEqualTo(listTitle);
        assertThat(retrievedList.getTodos().size()).isEqualTo(3);
    }

    @Test
    public void testDelete() {
        TodoList retrievedList = listRepository.findByTitle(listTitle);
        listRepository.delete(retrievedList);

        TodoList verifyList = listRepository.findByTitle(listTitle);
        assertThat(verifyList).isNull();

        List<TodoList> allTodoLists = listRepository.findAll();
        assertThat(allTodoLists.size()).isEqualTo(0);
    }

    @Test
    public void testUpdate() {
        TodoList retrievedList = listRepository.findByTitle(listTitle);
        String newTitle = "New Title";
        retrievedList.setTitle(newTitle);
        listRepository.save(retrievedList);

        TodoList verifyList = listRepository.findByTitle(listTitle);
        assertThat(verifyList).isNull();

        TodoList verifyListUpdated = listRepository.findByTitle(newTitle);
        assertThat(verifyListUpdated).isNotNull();
        assertThat(verifyListUpdated.getTitle()).isEqualTo(newTitle);
    }
}