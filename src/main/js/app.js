'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
import update from 'immutability-helper';

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {todos:[], links: {}};
        this.onDelete = this.onDelete.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onAdd = this.onAdd.bind(this);
    }

    componentDidMount() {
        this.getTodos();
    }

    getTodos() {
        console.log("getTodos");
        client({method: 'GET', path: '/todos'}).then(response => {
            this.setState({
                todos: response.entity._embedded.todos,
                links: response.entity._links
            });
            console.log("todos:");
            console.log(this.state.todos);
            console.log("links");
            console.log(this.state.links);
        });
        console.log("todos:");
        console.log(this.state.todos);
        console.log("links");
        console.log(this.state.links);
    }

    render() {
        return (
            <TodoList todos={this.state.todos} onDelete={this.onDelete} onUpdate={this.onUpdate} onAdd={this.onAdd}/>
        )
    }
    onDelete(todo) {
        client({method: 'DELETE', path: todo._links.self.href}).then(response => {
            this.getTodos();
        });
    }
    onUpdate(todo) {
        client({
            method: 'PUT',
            path: todo._links.self.href,
            entity: todo,
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.getTodos();
        });
    }

    onAdd(newTodo) {
        console.log("in on Add")
        console.log(this.state.todos);
        client({
            method: 'POST',
            path: this.state.links.self.href,
            entity: newTodo,
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.getTodos();
        });
    }

}

class TodoList extends React.Component{
    constructor(props) {
        super(props);
        this.addTodo = this.addTodo.bind(this);
        this.handleChangeTodo = this.handleChangeTodo.bind(this);
        this.state = {newTodo: "", completed: false}
    }

    addTodo(event) {
        console.log("adding todo...");
        console.log(event);
        const newTodo = {todo: this.state.newTodo, completed: false}
        this.props.onAdd(newTodo);
        this.setState({newTodo: "", completed: false});
    }

    handleChangeTodo(event) {
        console.log("handle change todo");
        console.log(event);
        this.setState({newTodo: event.target.value, completed: false});
    }

    render() {
        var todos = this.props.todos.map(todo =>
            <TodoItem key={todo._links.self.href} todo={todo} onDelete={this.props.onDelete} onUpdate={this.props.onUpdate} />
        );
        return (
            <span>
            <table>
                <tbody>
                <tr>
                    <th>Todo</th>
                    <th>Completed?</th>
                </tr>
                {todos}
                </tbody>

            </table>
            <input
        placeholder="What needs to be done?"
        value={this.state.newTodo}
        // onKeyDown={this.handleNewTodoKeyDown}
        onChange={this.handleChangeTodo}
        autoFocus={true}
        name="newTodo"
            />
            <button onClick={this.addTodo}>Add</button>
            </span>
        )
    }
}

class TodoItem extends React.Component{
    constructor(props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this);
        this.handleUpdate = this.handleUpdate.bind(this);
        this.toggleCompleted = this.toggleCompleted.bind(this);
        this.state = {myTodo: this.props.todo};
    }

    handleDelete() {
        this.props.onDelete(this.state.myTodo);
    }

    handleUpdate(event) {
        const updatedTodo = update(this.state.myTodo, {
            todo: {$set: event.target.value}
        })
        this.setState({
            myTodo: updatedTodo
        })
        this.props.onUpdate(updatedTodo);
    }

    toggleCompleted(event) {
        const updatedTodo = update(this.state.myTodo, {
            completed: {$set: event.target.checked}
        })
        this.setState({
            myTodo: updatedTodo
        })
        this.props.onUpdate(updatedTodo);
    }

    render() {
        return (
            <tr>
                <td><input name="todo-text" type="text" value={this.state.myTodo.todo} onChange={this.handleUpdate}/> </td>
                <td>
                    <input
                    className="toggle"
                    type="checkbox"
                    checked={this.state.myTodo.completed}
                    onChange={this.toggleCompleted} />
                </td>
                <td>
                    <button onClick={this.handleDelete}>Delete</button>
                </td>
            </tr>
        )
    }
}
ReactDOM.render(
    <App />,
    document.getElementById('react')
)