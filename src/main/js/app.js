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
        console.log("getTodoLists");

        client({method: 'GET', path: '/todoList'}).then(response => {
            console.log("getTodo lists");
            this.setState({
                todoList: response.entity._embedded.todoList,
                listLinks: response.entity._links
            });
        });

        console.log("todo list:");
        console.log(this.state.todoList);
        console.log("links");
        console.log(this.state.listLinks);
    }

    render() {
        console.log("render");
        console.log(todoList);
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
        this.state = {todos:[], links: {}, newTodo: "", completed: false, title: "Missing"}
    }
    componentDidMount() {
        this.getTodos();
    }

    getTodos() {
        console.log("getTodos");
        console.log(this.props.todoList);

        client({method: 'GET', path: this.props.todoList._links.todos.href,}).then(response => {
            console.log("getTodo lists");
            this.setState({
                todos: response.entity._embedded.todos,
                links: response.entity._links
            });
        });
    }

    render(){
        var todos = this.state.todos.map(todo =>
            <TodoItem key={todo._links.self.href} todo={todo} onDelete={this.props.onDelete} onUpdate={this.props.onUpdate} />
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
