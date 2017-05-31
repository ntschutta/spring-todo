package io.sporing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

/**
 * Created by nschutta on 5/31/17.
 */
@RepositoryRestResource(collectionResourceRel = "todos", path = "todos")
public interface TodoListRepository extends JpaRepository <Todo, Long> {
    List<Todo> findAll();
}
