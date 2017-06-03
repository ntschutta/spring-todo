package io.sporing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

/**
 * Created by nschutta on 6/2/17.
 */
@RepositoryRestResource(collectionResourceRel = "todoList", path = "todoList")
public interface TodoListRepository extends JpaRepository <TodoList, Long>  {
    List<TodoList> findAll();
}
