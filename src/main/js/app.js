'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const follow = require('./follow');
import update from 'immutability-helper';

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {todoList:[], listLinks:{}};
    }

    componentDidMount() {
        this.getTodoLists();
    }

    getTodoLists() {
        client({method: 'GET', path: '/todoList'}).then(response => {
            this.setState({
                todoList: response.entity._embedded.todoList,
                listLinks: response.entity._links
            });
        });
    }

    render() {
        var todoList = this.state.todoList.map((todoList, index) =>
            <TodoList key={todoList._links.self.href} todoList={this.state.todoList[index]} />
        );
        return (
            <span>
            <ul>
                {todoList}
            </ul>
            </span>
        )
    }
}

class TodoList extends React.Component{
    constructor(props) {
        super(props);
        this.onDelete = this.onDelete.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onAdd = this.onAdd.bind(this);
        this.addTodo = this.addTodo.bind(this);
        this.handleChangeTodo = this.handleChangeTodo.bind(this);
        this.state = {todos:[], links: {}, newTodo: "", completed: false, title: "Missing"}
    }
    componentDidMount() {
        this.getTodos();
    }

    onDelete(todo) {
        client({
            method: 'DELETE',
            path: todo._links.self.href
        }).then(response => {
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
        client({
            method: 'POST',
            path: '/todos',
            entity: newTodo,
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.getTodos();
        });
    }


    getTodos() {
        client({method: 'GET', path: this.props.todoList._links.todos.href,}).then(response => {
            this.setState({
                todos: response.entity._embedded.todos,
                links: response.entity._links
            });
        });
    }

    addTodo(event) {
        const newTodo = {todo: this.state.newTodo, completed: false, todoList: this.props.todoList._links.self.href}
        this.onAdd(newTodo);
        this.setState({newTodo: "", completed: false});
    }

    handleChangeTodo(event) {
        this.setState({newTodo: event.target.value, completed: false});
    }

    render(){
        var todos = this.state.todos.map(todo =>
            <TodoItem key={todo._links.self.href} todo={todo} onDelete={this.onDelete} onUpdate={this.onUpdate} />
        );
        return (
            <span>
                <h1>{this.props.todoList.title}</h1>
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
            <button onClick={this.addTodo}>Add Todo</button>
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
