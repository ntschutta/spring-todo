package io.sporing;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Created by nschutta on 6/2/17.
 */
@Data
@Entity
@NoArgsConstructor
public class TodoList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    @OneToMany(
            mappedBy = "todoList",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private List<Todo> todos;

    public TodoList(String title) {
        this.title = title;
    }

}
