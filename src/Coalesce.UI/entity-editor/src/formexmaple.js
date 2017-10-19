import React from 'react';
import ReactDOM from 'react-dom';
import $ from 'jquery';
import './index.css';

// ========================================
// http://jamesknelson.com/learn-raw-react-no-jsx-flux-es6-webpack/



class ContactItem extends React.Component  {
  render() {
     return (
       <li>
         <h2>{this.props.name}</h2>
         <a href='mailto:{contact.email}'>{this.props.email}</a>
         <div>{this.props.description}</div>
       </li>
     );
  }
}

class ContactForm extends React.Component {
  constructor (props) {
    super();
    this.state = props.value;

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }
  render () {
    return (
      <form ref="form">
        <input type="text" ref="name" name="name" value={this.state.name} onChange={this.handleChange}/>
        <input type="email" name="email" value={this.state.email}  onChange={this.handleChange}/>
        <textarea name="description" value={this.state.description}  onChange={this.handleChange}/>
        <button type="submit" >Add Contact</button>
      </form>
    )
  }
  handleSubmit(e) {
    console.log('Submitting: ' + this.state.name);
  }

  handleChange (e){
    const name = e.target.name;
    const value = e.target.value;

    this.setState({[name]: value});
  }
}

class ContactView extends React.Component {

  constructor() {
    super();
    $.ajax({
			url : 'http://localhost:8181/cxf/data/entity/123',
		}).then(
				function(data) {

					console.log(data);

				});

  }

  render() {
    var contactItemElements = this.props.contacts
      .filter(function(contact) { return contact.email; })
      .map(function(contact) { return React.createElement(ContactItem, contact); });

    var form = React.createElement(ContactForm, {
      value: this.props.newContact,
      onChange: this.props.onNewContactChange,
    });

    return (
      <div>
        <h1>Contacts</h1>
        <ul>
          {contactItemElements}
        </ul>
        {form}
      </div>
    );
  }

};

function updateNewContact(contact) {
  setState({ newContact: contact });
}

var state = {};

function setState(changes) {
  Object.assign(state, changes);

  ReactDOM.render(
    React.createElement(ContactView, Object.assign({}, state, {
      onNewContactChange: updateNewContact,
    })),
    document.getElementById('root')
  );
}

setState({
  contacts: [
    {key: 1, name: "Derek Clemenzi", email: "dclemenzi@incadencecorp.com"},
    {key: 2, name: "dclemenzi", email: "dclemenzi@incadencecorp.com", description: "test"},
    {key: 3, name: "no Email"}
  ],
  newContact: {name: "AA", email: "AA", description: "AA"},
});
// ========================================
/*
function setState(changes) {
  Object.assign(state, changes);

  ReactDOM.render(
    React.createElement(ContactView, Object.assign({}, state, {
      onNewContactChange: updateNewContact,
    })),
    document.getElementById('react-app')
  );
}
*/
/*
ReactDOM.render(
  React.createElement(ContactView, {contacts: contacts}), //<Game />,
  document.getElementById('root')
);
*/
