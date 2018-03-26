import "babel-polyfill";

import React from 'react';
import ReactDOM from 'react-dom';
import {EntityView} from './entity.js'
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import Popup from 'react-popup';
import {registerLoader, registerPrompt, registerTemplatePrompt, registerErrorPrompt} from 'common-components/lib/register.js'

import { loadTemplate, loadTemplates, createNewEntity, loadTemplateByEntity } from 'common-components/lib/js/templateController'
import { saveEntity, loadEntity } from 'common-components/lib/js/entityController'
import Paper from 'material-ui/Paper';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

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

  constructor(props) {
    super(props);

    if (this.props.entitykey != null) {
      this.renderEntity(this.props.entitykey);
    } else if (this.props.templatekey != null) {
      this.renderNewEntity(this.props.templatekey)
    }

    this.state = {
      entity: null,
      template: null,
      isNew: false
    }
  }

  handleSaveEntity() {
    const that = this;
    Popup.plugins().loader('Saving...');

    saveEntity(this.state.entity, this.state.isNew).then((value) => {
      Popup.close();

      that.setState({
        isNew: false
      })

    }).catch((err) => {
      Popup.plugins().promptError("Saving: " + err);
    });

  }

  renderEntity(key) {
  const that = this;
    Popup.plugins().loader('Loading Entity...');

    loadEntity(key).then((entity) => {

        loadTemplateByEntity(entity).then((template) => {

          that.setState({
            entity: entity,
            template: template,
            isNew: false
          })

          Popup.close();
        }).catch((err) => {
            Popup.plugins().promptError('Failed to load template (' + entity.name + ', ' + entity.source + ', ' + entity.version + ')');
        })

      }).catch(function(error) {
        Popup.plugins().promptError('Failed to load entity (' + key + ')');
      });
  }

  renderNewEntity(key) {

    var that = this;

    Popup.plugins().loader('Creating Entity...');

    loadTemplate(key).then((template) => {
      createNewEntity(key).then((entity) => {

        that.setState({
          entity: entity,
          template: template,
          isNew: true
        })

        Popup.close();
      }).catch((err) => {
        Popup.plugins().promptError('Failed to create new entity (' + key + ')');
      })
    }).catch((err) => {
      Popup.plugins().promptError('Failed to load template (' + key + ')');
    });
  }

  render() {

    const { entity, template, isNew } = this.state;
    const that = this;

    return (
      <div>
        <Menu logoSrc={pjson.icon} title={pjson.title} items={[
          {
            id: 'new',
            name: 'New',
            img: '/images/svg/new.svg',
            title: 'Create New Entity',
            onClick: () => {
              Popup.plugins().promptTemplate('create', 'Type your name', function (key) {
                that.renderNewEntity(key);
              });
            }
          },{
            id: 'load',
            name: 'Load',
            img: '/images/svg/load.svg',
            title: 'Load Entity',
            onClick: () => {
              Popup.plugins().prompt('Load', 'Entity Selection', '', 'Enter Entity Key', function (key) {
                that.renderEntity(key);
              });
            }
          },{
              id: 'save',
              name: 'Save',
              img: '/images/svg/save.svg',
              title: 'Save Entity',
              onClick: () => {that.handleSaveEntity();}
          }
        ]}/>
        <MuiThemeProvider>
          <Paper zDepth={1} style={{padding: '5px', margin: '10px'}}>
            <EntityView data={entity} template={template} isNew={isNew} />
          </Paper>
        </MuiThemeProvider>

      </div>
    )
  }
};


const Default = ({match}) => {
  var params={};

  window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(s,k,v){
    console.log(k + '=' + v);
    params[k]=v}
  );

  if (params['entitykey'] != null) {
    return (<App entitykey={params['entitykey']} />);
  } else if (params['templatekey'] != null) {
    return (<App templatekey={params['templatekey']} />);
  } else {
    return (<App />);
  }
}

// Default Component
ReactDOM.render(
    <Popup />,
    document.getElementById('popupContainer')
);

/** Prompt plugin */
loadTemplates().then((data) => {

      registerTemplatePrompt(Popup, rootUrl, data);

}).catch(function(error) {
    Popup.plugins().promptError("Loading Templates: " + error);
});

registerLoader(Popup);
registerPrompt(Popup);

// TODO Uncomment this line for debugging.
//renderNewEntity('086f6440-997e-30a4-90a4-d134076e1587');

class Main extends React.Component {

  render() {
    return (
      <Router>
        <Switch>
          <Route component={Default}/>
        </Switch>
      </Router>
    );
  }
}

ReactDOM.render(
  React.createElement((Main), {}),
  document.getElementById('entityview')
);
