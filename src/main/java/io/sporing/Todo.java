package io.sporing;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by nschutta on 5/31/17.
 */

@Data
@Entity
@NoArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String todo;
    private boolean completed;

    @ManyToOne
    private TodoList todoList;

    public Todo(TodoList list, String todo, boolean completed) {
        this.todoList = list;
        this.todo = todo;
        this.completed = completed;
    }
}
