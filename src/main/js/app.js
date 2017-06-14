'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
import update from 'immutability-helper';
import {Button} from 'react-bootstrap';
import {Table} from 'react-bootstrap';
import {PageHeader} from 'react-bootstrap';
import {Panel} from 'react-bootstrap';
import {Checkbox} from 'react-bootstrap';
import {FormControl} from 'react-bootstrap';
import {FormGroup} from 'react-bootstrap';

class App extends React.Component {

    constructor(props) {
        super(props);
        this.onAddTodoList = this.onAddTodoList.bind(this);
        this.addTodoList = this.addTodoList.bind(this);
        this.onDeleteTodoList = this.onDeleteTodoList.bind(this);
        this.onUpdateTodoList = this.onUpdateTodoList.bind(this);
        this.handleChangeTodoList = this.handleChangeTodoList.bind(this);
        this.state = {todoList: [], listLinks: {}, newTodoList: ""};
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

    onAddTodoList(newTodoList) {
        client({
            method: 'POST',
            path: this.state.listLinks.self.href,
            entity: newTodoList,
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.getTodoLists();
        });
    }

    onDeleteTodoList(todoList) {
        client({
            method: 'DELETE',
            path: todoList._links.self.href
        }).then(response => {
            this.getTodoLists();
        });
    }

    onUpdateTodoList(todoList) {
        client({
            method: 'PUT',
            path: todoList._links.self.href,
            entity: todoList,
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.getTodoLists();
        });
    }

    addTodoList(event) {
        const newTodoList = {title: this.state.newTodoList}
        this.onAddTodoList(newTodoList);
        this.setState({newTodoList: ""});
    }

    handleChangeTodoList(event) {
        this.setState({newTodoList: event.target.value});
    }

    render() {
        var todoList = this.state.todoList.map((todoList, index) =>
            <Panel key={todoList._links.self.href}>
                <TodoList key={todoList._links.self.href} todoList={this.state.todoList[index]}
                          onDeleteList={this.onDeleteTodoList} onUpdateList={this.onUpdateTodoList}/>
            </Panel>
        );
        return (
            <span>
                {todoList}
                <Panel>
                    <FormGroup>
                        <FormControl
                            placeholder="Name Your Todo List"
                            value={this.state.newTodoList}
                            onChange={this.handleChangeTodoList}
                            autoFocus={true}
                            name="newTodoList"
                        />
                        <Button bsStyle="primary" bsSize="large" onClick={this.addTodoList}>New Todo List</Button>
                    </FormGroup>
                </Panel>
            </span>
        )
    }
}

class TodoList extends React.Component {
    constructor(props) {
        super(props);
        this.onDelete = this.onDelete.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onAdd = this.onAdd.bind(this);
        this.addTodo = this.addTodo.bind(this);
        this.handleChangeTodo = this.handleChangeTodo.bind(this);
        this.handleDeleteList = this.handleDeleteList.bind(this);
        this.handleTitleUpdate = this.handleTitleUpdate.bind(this);
        this.state = {
            myList: this.props.todoList,
            todos: [],
            links: {},
            newTodo: "",
            completed: false,
            title: "Missing"
        }
    }

    componentDidMount() {
        this.getTodos();
    }

    onDelete(todo) {
        console.log("delete todo item");
        console.log(todo._links.self.href);
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

    handleDeleteList() {
        this.props.onDeleteList(this.props.todoList);
    }

    handleTitleUpdate(event) {
        const updatedTodoList = update(this.state.myList, {
            title: {$set: event.target.value}
        })
        this.setState({
            title: event.target.value
        })
        this.props.onUpdateList(updatedTodoList);
    }

    render() {
        var todos = this.state.todos.map(todo =>
            <TodoItem key={todo._links.self.href} todo={todo} onDelete={this.onDelete} onUpdate={this.onUpdate}/>
        );
        return (
            <span>
                <PageHeader><FormControl bsSize="lg" name="todo-list-title" type="text" value={this.props.todoList.title}
                                   onChange={this.handleTitleUpdate}/></PageHeader>
            <Table striped>
                <tbody>
                <tr>
                    <th>Todo</th>
                    <th>Completed?</th>
                    <th></th>
                </tr>
                {todos}
                </tbody>
            </Table>
                <FormGroup>
                <FormControl
                    placeholder="What needs to be done?"
                    value={this.state.newTodo}
                    // onKeyDown={this.handleNewTodoKeyDown}
                    onChange={this.handleChangeTodo}
                    name="newTodo"
                />
                <Button bsStyle="primary" onClick={this.addTodo}>Add Todo</Button>
                <div>
                    <Button bsStyle="danger" onClick={this.handleDeleteList}>Delete List</Button>
                </div>
                </FormGroup>
            </span>
        )
    }
}

class TodoItem extends React.Component {
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
                <td><FormControl name="todo-text" type="text" value={this.state.myTodo.todo} onChange={this.handleUpdate}/>
                </td>
                <td>
                    <Checkbox
                        checked={this.state.myTodo.completed}
                        onChange={this.toggleCompleted}/>
                </td>
                <td>
                    <Button bsStyle="danger" onClick={this.handleDelete}>Delete</Button>
                </td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)
