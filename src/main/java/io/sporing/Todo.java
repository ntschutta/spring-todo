package io.sporing;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    public Todo(String todo, boolean completed) {
        this.todo = todo;
        this.completed = completed;
    }
}
