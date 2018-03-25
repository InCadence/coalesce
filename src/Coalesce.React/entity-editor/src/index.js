import "babel-polyfill";

import React from 'react';
import ReactDOM from 'react-dom';
import {EntityView} from './entity.js'
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import Popup from 'react-popup';
import {registerLoader, registerPrompt, registerTemplatePrompt, registerErrorPrompt} from 'common-components/lib/register.js'

import { loadTemplate, loadTemplates, createNewEntity, loadTemplateByName, loadTemplateByEntity } from 'common-components/lib/js/templateController'
import { saveEntity, loadEntity } from 'common-components/lib/js/entityController'


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

function handleSaveEntity(entity, isNew, view) {

  Popup.plugins().loader('Saving...');

  saveEntity(entity, isNew).then((value) => {
    Popup.close();
    view.setIsNew(false);
  }).catch((err) => {
    Popup.plugins().promptError("Saving: " + err);
  });

}

function renderEntity(key) {
  ReactDOM.unmountComponentAtNode(document.getElementById('entityview'));

  Popup.plugins().loader('Loading Entity...');

  loadEntity(key).then((entity) => {

      loadTemplateByEntity(entity).then((template) => {
        ReactDOM.render(
          React.createElement(EntityView, {
            data: entity,
            template: template,
            isNew: false,
            saveEntity: handleSaveEntity
          }),
          document.getElementById('entityview')
        );

        Popup.close();
      }).catch((err) => {
          Popup.plugins().promptError('Failed to load template (' + entity.name + ', ' + entity.source + ', ' + entity.version + ')');
      })

    }).catch(function(error) {
      Popup.plugins().promptError('Failed to load entity (' + key + ')');
    });
}

function renderNewEntity(key) {
  ReactDOM.unmountComponentAtNode(document.getElementById('entityview'));

  Popup.plugins().loader('Creating Entity...');

  loadTemplate(key).then((template) => {
    createNewEntity(key).then((entity) => {
      console.log('got here 2');
      ReactDOM.render(
        React.createElement(EntityView, {
          data: entity,
          template: template,
          isNew: true,
          saveEntity: handleSaveEntity
        }),
        document.getElementById('entityview')
      );

      Popup.close();
    }).catch((err) => {
      Popup.plugins().promptError('Failed to create new entity (' + key + ')');
    })
  }).catch((err) => {
    Popup.plugins().promptError('Failed to load template (' + key + ')');
  });
}

// Default Component
ReactDOM.render(
    <Popup />,
    document.getElementById('popupContainer')
);

/** Prompt plugin */
loadTemplates().then((data) => {

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
