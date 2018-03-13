import React from 'react';
import ReactDOM from 'react-dom';
import {EntityView} from './entity.js'
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import Popup from 'react-popup';
import {registerLoader, registerPrompt, registerTemplatePrompt, registerErrorPrompt} from 'common-components/lib/register.js'

import {Menu} from 'common-components/lib/index.js'
import 'common-components/bootstrap/css/bootstrap.min.css'

import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'
import './index.css'

var pjson = require('../package.json');
document.title = pjson.title;

var rootUrl;

if (window.location.port == 3000) {
  rootUrl  = 'http://' + window.location.hostname + ':8181';
} else {
  rootUrl  = '';
}

registerErrorPrompt(Popup);

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
    renderEntity(params['entitykey']);
  } else if (params['templatekey'] != null) {
    renderNewEntity(params['templatekey']);
  }

  return (<div/>);
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

function saveEntity(entity, isNew, view) {

  Popup.plugins().loader('Saving...');

  fetch(rootUrl + '/cxf/data/entity/' + entity.key, {
    method: ((isNew) ? "PUT" : "POST"),
    body: JSON.stringify(entity),
    headers: new Headers({
      'content-type': 'application/json; charset=utf-8'
    }),
  }).then(res => {
      Popup.close();

      if (!res.ok) {
        throw Error(res.statusText);
      }

      view.setIsNew(false);

  }).catch(function(error) {
      Popup.plugins().promptError("Saving: " + error);
  });
}

function renderEntity(key) {
  ReactDOM.unmountComponentAtNode(document.getElementById('entityview'));

  Popup.plugins().loader('Loading Entity...');

  fetch(rootUrl + '/cxf/data/entity/' + key)
    .then(res => res.json())
    .then(data => {

      fetch(rootUrl + '/cxf/data/templates/' + data.name + '/' + data.source + '/' + data.version)
        .then(res => res.json())
        .then(template => {


          ReactDOM.render(
            React.createElement(EntityView, {
              data: data,
              template: template,
              isNew: false,
              saveEntity: saveEntity
            }),
            document.getElementById('entityview')
          );

          Popup.close();

        }).catch(function(error) {
          Popup.plugins().promptError('Failed to load template (' + data.name + ', ' + data.source + ', ' + data.version + ')');
        });
    }).catch(function(error) {
      Popup.plugins().promptError('Failed to load entity (' + key + ')');
    });
}

function renderNewEntity(key) {
  ReactDOM.unmountComponentAtNode(document.getElementById('entityview'));

  Popup.plugins().loader('Creating Entity...');

  fetch(rootUrl + '/cxf/data/templates/' + key)
    .then(res => res.json())
    .then(template => {

      fetch(rootUrl + '/cxf/data/templates/' + key + "/new")
        .then(res => res.json())
        .then(data => {


          ReactDOM.render(
            React.createElement(EntityView, {
              data: data,
              template: template,
              isNew: true,
              saveEntity: saveEntity
            }),
            document.getElementById('entityview')
          );

          Popup.close();
        }).catch(function(error) {
          Popup.plugins().promptError('Failed to create new entity (' + key + ')');
        });
    }).catch(function(error) {
      Popup.plugins().promptError('Failed to load template (' + key + ')');
    });
}

// Default Component
ReactDOM.render(
    <Popup />,
    document.getElementById('popupContainer')
);

/** Prompt plugin */
fetch(rootUrl + '/cxf/data/templates')
  .then(res => res.json())
  .then(data => {
      registerTemplatePrompt(Popup, rootUrl, data);

      ReactDOM.render(
          <Menu logoSrc={pjson.icon} title={pjson.title} items={[
            {
              id: 'new',
              name: 'New',
              img: '/images/svg/new.svg',
              title: 'Create New Entity',
              onClick: () => {

                Popup.plugins().promptTemplate('create', 'Type your name', function (key) {
                  renderNewEntity(key);
                });
              }
            }, {
              id: 'load',
              name: 'Load',
              img: '/images/svg/load.svg',
              title: 'Load Entity',
              onClick: () => {
                Popup.plugins().prompt('Load', 'Entity Selection', '', 'Enter Entity Key', function (key) {
                  renderEntity(key);
                });
              }
            }
          ]}/>,
          document.getElementById('myNavbar')
      );

}).catch(function(error) {
    ReactDOM.render(
        <Menu logoSrc={pjson.icon} title={pjson.title} items={[]}/>,
        document.getElementById('myNavbar')
    );

    Popup.plugins().promptError("Loading Templates: " + error);
});

registerLoader(Popup);
registerPrompt(Popup);

// TODO Uncomment this line for debugging.
//renderNewEntity('086f6440-997e-30a4-90a4-d134076e1587');

ReactDOM.render(
  React.createElement(App, {}),
  document.getElementById('entityview')
);
