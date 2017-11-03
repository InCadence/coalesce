import React from 'react';
import ReactDOM from 'react-dom';
import {EntityView} from './entity.js'
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import Popup from 'react-popup';
import Prompt from 'common-components/lib/prompt.js'
import {PromptTemplate} from 'common-components/lib/prompt-template.js'
import {Menu} from 'common-components/lib/menu.js'

import './index.css'
import 'common-components/css/popup.css'

var rootUrl = 'http://' + window.location.hostname + ':8181'

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
    return React.createElement(EntityView, {
      objectkey: params['entitykey'],
      isNew: false,
      rootUrl: rootUrl
    });
  } else if (params['templatekey'] != null) {
    return React.createElement(EntityView, {
      objectkey: params['templatekey'],
      isNew: true,
      rootUrl: rootUrl
    });
  } else {
    return (<div/>);
  }
}

const EditEntity = ({match}) => {
  return React.createElement(EntityView, {
    objectkey: match.params.entitykey,
    isNew: false,
    rootUrl: rootUrl
  });
}

const ViewEntity = ({match}) => {
  return React.createElement(EntityView, {
    objectkey: match.params.entitykey,
    isNew: false,
    rootUrl: rootUrl
  });
}

const NewEntity = ({match}) => {
  return React.createElement(EntityView, {
    objectkey: match.params.templateKey,
    isNew: true,
    rootUrl: rootUrl
  });
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
    <Menu items={[
      {
        id: 'new',
        name: 'New',
        onClick: () => {

          Popup.plugins().promptTemplate('create', 'Type your name', function (value) {

            ReactDOM.unmountComponentAtNode(document.getElementById('entityview'));

            ReactDOM.render(
              React.createElement(EntityView, {
                objectkey: value,
                isNew: true,
                rootUrl: rootUrl
              }),
              document.getElementById('entityview')
            );
          });
        }
      }, {
        id: 'load',
        name: 'Load',
        onClick: () => {
          Popup.plugins().prompt('Load', 'Entity Selection', '', 'Enter Entity Key', function (value) {

            ReactDOM.unmountComponentAtNode(document.getElementById('entityview'));

            ReactDOM.render(
              React.createElement(EntityView, {
                objectkey: value,
                isNew: false,
                rootUrl: rootUrl
              }),
              document.getElementById('entityview')
            );
          });
        }
      }
    ]}/>,
    document.getElementById('myNavbar')
);

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
