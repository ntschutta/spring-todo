'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {todos: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/todos'}).then(response => {
            this.setState({todos: response.entity._embedded.todos});
        });
    }

    render() {
        return (
            <TodoList todos={this.state.todos}/>
        )
    }
}

class TodoList extends React.Component{
    render() {
        var todos = this.props.todos.map(todo =>
            <TodoItem key={todo._links.self.href} todo={todo}/>
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
    render() {
        return (
            <tr>
                <td>{this.props.todo.todo}</td>
                <td>{this.props.todo.completed.toString()}</td>
            </tr>
        )
    }
}
ReactDOM.render(
    <App />,
    document.getElementById('react')
)