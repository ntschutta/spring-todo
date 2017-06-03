package io.sporing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by nschutta on 5/31/17.
 */
@Component
public class TodosLoader implements CommandLineRunner {
    private final TodoRepository todoRepository;
    private final TodoListRepository listRepository;

    @Autowired
    public TodosLoader(TodoListRepository listRepository, TodoRepository repository) {
        this.listRepository = listRepository;
        this.todoRepository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {
        TodoList list1 = this.listRepository.save(new TodoList("List 1"));
        this.todoRepository.save(new Todo(list1, "foo", false));
        this.todoRepository.save(new Todo(list1, "bar", true));
        TodoList list2 = this.listRepository.save(new TodoList("List 2"));
        this.todoRepository.save(new Todo(list2, "Han", false));
        this.todoRepository.save(new Todo(list2, "Chewie", true));
    }
}
