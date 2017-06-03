package io.sporing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by nschutta on 5/31/17.
 */
@Component
public class TodosLoader implements CommandLineRunner {
    private final TodoRepository repository;

    @Autowired
    public TodosLoader(TodoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {
        this.repository.save(new Todo("foo", false));
        this.repository.save(new Todo("bar", true));
    }
}
