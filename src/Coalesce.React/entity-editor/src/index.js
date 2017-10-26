import React from 'react';
import ReactDOM from 'react-dom';
import {EntityView, NewEntityView} from './entity.js'
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import Popup from 'react-popup';
import Prompt from 'common-components/lib/prompt.js'
import {PromptTemplate} from 'common-components/lib/prompt-template.js'
import $ from 'jquery'

import './index.css'
import 'common-components/css/popup.css'

class App extends React.Component {
  render() {
    return (
      <div>
        <Router>
          <Switch>
            <Route path="/entityeditor/:entitykey/edit" component={EditEntity}/>
            <Route path="/entityeditor/:entitykey/view" component={ViewEntity}/>
            <Route path="/entityeditor/:templateKey/new" component={NewEntity}/>
            <Route component={Default}/>
          </Switch>
        </Router>
      </div>
    )
  }
};

const Default = ({match}) => {
  var params={};window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(s,k,v){params[k]=v});

  if (params['entitykey'] != null) {
    return React.createElement(EntityView, {entitykey: params['entitykey']});
  } else if (params['templatekey'] != null) {
    return React.createElement(NewEntityView, {templatekey: params['templatekey']});
  } else {
    return (<div/>);
  }
}

const EditEntity = ({match}) => {
  return React.createElement(EntityView, {entitykey: match.params.entitykey});
}

const ViewEntity = ({match}) => {
  return React.createElement(EntityView, {entitykey: match.params.entitykey});
}

const NewEntity = ({match}) => {
  return React.createElement(NewEntityView, {templatekey: match.params.templateKey});
}

class MenuOptions extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;

    this.promptForTemplate = this.promptForTemplate.bind(this);
    this.promptForEntity = this.promptForEntity.bind(this);
    this.reset = this.reset.bind(this);
    this.save = this.save.bind(this);
  }

  promptForTemplate() {

    Popup.plugins().promptTemplate('create', 'Type your name', function (value) {

      ReactDOM.unmountComponentAtNode(document.getElementById('entityview'));

      ReactDOM.render(
        React.createElement(NewEntityView, {templatekey: value}),
        document.getElementById('entityview')
      );

      //window.location.href = "/entityeditor/" + value + "/new";
    });
  }

  promptForEntity() {
    Popup.plugins().prompt('Load', 'Entity Selection', '', 'Enter Entity Key', function (value) {

      ReactDOM.unmountComponentAtNode(document.getElementById('entityview'));

      ReactDOM.render(
        React.createElement(EntityView, {entitykey: value}),
        document.getElementById('entityview')
      );

      //window.location.href = "/entityeditor/" + value + "/edit";
    });
  }

  reset() {
    alert("TODO: Not Implemented");
  }

  save() {
    alert("TODO: Not Implemented (Need to invoke saveChanges(data))");
  }

  render () {
    return (
      <ul className="nav navbar-nav navbar-right">
          <li><a id="template-form" href="#"><div onClick={this.promptForTemplate}>New</div></a></li>
          <li><a id="load-form" href="#"><div onClick={this.promptForEntity}>Load</div></a></li>
          <li><a id="save-form" href="#"><div onClick={this.save}>Save</div></a></li>
          <li><a id="reset-form" href="#"><div onClick={this.reset}>Reset</div></a></li>
      </ul>
    )
  }
}

// Default Component
ReactDOM.render(
  React.createElement(App, {}),
  document.getElementById('entityview')
);

ReactDOM.render(
    <Popup />,
    document.getElementById('popupContainer')
);

ReactDOM.render(
    <MenuOptions />,
    document.getElementById('myNavbar')
);

function saveChanges(data) {

  $.ajax({
    type : "POST",
    data : JSON.stringify(data),
    contentType : "application/json; charset=utf-8",
    url : 'http://localhost:8181/entity',
    success : function (data) {
      alert('success');
    },
    error : function (data) {
      alert('failed');
    }
  });
}

/** Prompt plugin */
Popup.registerPlugin('promptTemplate', function (buttontext, defaultValue, callback) {
    let promptValue = null;
    let promptChange = function (value) {
        promptValue = value;
    };

    this.create({
        title: "Select Template",
        content: <PromptTemplate onChange={promptChange} value={defaultValue} />,
        buttons: {
            left: ['cancel'],
            right: [{
                text: buttontext,
                className: 'success',
                action: function () {
                    callback(promptValue);
                    Popup.close();
                }

            }]
        }
    });
});

/** Prompt plugin */
Popup.registerPlugin('prompt', function (buttontext, title, defaultValue, placeholder, callback) {
    let promptValue = null;
    let promptChange = function (value) {
        promptValue = value;
    };

    this.create({
        title: title,
        content: <Prompt onChange={promptChange} placeholder={placeholder} value={defaultValue} />,
        buttons: {
            left: ['cancel'],
            right: [{
                text: buttontext,
                className: 'success',
                action: function () {
                    callback(promptValue);
                    Popup.close();
                }
            }]
        }
    });
});
