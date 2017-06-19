package io.sporing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by nschutta on 6/16/17.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class TodoRepositoryTest {
    @Autowired
    private TodoRepository repository;
    @Autowired
    private TodoListRepository listRepository;

    private TodoList todoList;
    private TodoList anotherTodoList;

    @Before
    public void setup() throws Exception {
        this.repository.deleteAllInBatch();
        this.listRepository.deleteAllInBatch();

        todoList = this.listRepository.save(new TodoList("Errands"));

        anotherTodoList = this.listRepository.save(new TodoList("Things To Do"));
        this.repository.save(new Todo(anotherTodoList, "get milk", false));
        this.repository.save(new Todo(anotherTodoList, "go to bank", true));
        this.repository.save(new Todo(anotherTodoList, "schedule pickup", false));

    }

    @Test
    public void testCreate() {
        Todo todoItem = new Todo();
        todoItem.setTodo("buy milk");
        todoItem.setTodoList(todoList);
        repository.save(todoItem);

        Todo loadedItem = repository.findByTodo("buy milk");
        assertThat(loadedItem.getTodo()).isEqualTo("buy milk");
        assertThat(loadedItem.isCompleted()).isFalse();
    }

    @Test
    public void testUpdate() {
        Todo loadedTodo = repository.findByTodo("get milk");
        assertThat(loadedTodo.isCompleted()).isFalse();
        loadedTodo.setCompleted(true);
        loadedTodo.setTodo("buy chocolate milk");
        repository.save(loadedTodo);

        Todo verifyTodo = repository.findByTodo("buy chocolate milk");
        assertThat(verifyTodo.isCompleted()).isTrue();
        assertThat(verifyTodo.getTodo()).isEqualTo("buy chocolate milk");
    }

    @Test
    public void testFindByTodo() {
        Todo loadedTodo = repository.findByTodo("get milk");
        assertThat(loadedTodo.isCompleted()).isFalse();
        assertThat(loadedTodo.getTodo()).isEqualTo("get milk");
    }

    @Test
    public void testDelete() {
        Todo loadedTodo = repository.findByTodo("get milk");
        repository.delete(loadedTodo);
        Todo verifyTodo = repository.findByTodo("get milk");
        assertThat(verifyTodo).isNull();

        List<Todo> allTodos = repository.findAll();
        assertThat(allTodos.size()).isEqualTo(2);
    }


}