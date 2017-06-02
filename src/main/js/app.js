'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {todos: []};
        this.onDelete = this.onDelete.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
    }

    componentDidMount() {
        this.getTodos();
    }

    getTodos() {
        client({method: 'GET', path: '/todos'}).then(response => {
            this.setState({todos: response.entity._embedded.todos});
        });
    }

    render() {
        return (
            <TodoList todos={this.state.todos} onDelete={this.onDelete}/>
        )
    }
    onDelete(todo) {
        client({method: 'DELETE', path: todo._links.self.href}).then(response => {
            this.getTodos();
        });
    }
    onUpdate(todo) {
        client({method: 'PUT', path: todo._links.self.href}).then(response => {
            this.getTodos();
        });
    }

}

class TodoList extends React.Component{
    constructor(props) {
        super(props);
    }

    render() {
        var todos = this.props.todos.map(todo =>
            <TodoItem key={todo._links.self.href} todo={todo} onDelete={this.props.onDelete}/>
        );
        return (
            <table>
                <tbody>
                <tr>
                    <th>Todo</th>
                    <th>Completed?</th>
                </tr>
                {todos}
                </tbody>
            </table>
        )
    }
}

class TodoItem extends React.Component{
    constructor(props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete() {
        this.props.onDelete(this.props.todo);
    }

    render() {
        return (
            <tr>
                <td>{this.props.todo.todo}</td>
                <td>{this.props.todo.completed.toString()}</td>
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