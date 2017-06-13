package io.sporing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by nschutta on 6/2/17.
 */
@RepositoryRestResource(collectionResourceRel = "todoList", path = "todoList")
public interface TodoListRepository extends JpaRepository <TodoList, Long>  {
}
