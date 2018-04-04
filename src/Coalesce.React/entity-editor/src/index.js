import "babel-polyfill";

import React from 'react';
import ReactDOM from 'react-dom';
import {EntityView} from './entity.js'
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import Popup from 'react-popup';
import {registerLoader} from 'common-components/lib/register.js'
import { DialogMessage, DialogOptions, DialogPrompt } from 'common-components/lib/components/dialogs'
import { Menu } from 'common-components/lib/components'

import { loadTemplate, loadTemplates, createNewEntity, loadTemplateByEntity } from 'common-components/lib/js/templateController'
import { saveEntity, loadEntity } from 'common-components/lib/js/entityController'
import { loadJSON } from 'common-components/lib/js/propertyController'

import Paper from 'material-ui/Paper';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import { getDefaultTheme } from 'common-components/lib/js/theme'
import getMuiTheme from 'material-ui/styles/getMuiTheme';

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
      isNew: false,
      prompt: false,
      promptTemplate: false,
      error: null,
      theme: getDefaultTheme()
    }

    this.renderEntity = this.renderEntity.bind(this);
    this.renderNewEntity = this.renderNewEntity.bind(this);
  }

  componentDidMount() {
    var that = this;
    loadTemplates().then((value) => {
      that.setState({
        templates: value
      })
    }).catch((err) => {
      that.setState({
        error: "Loading Templates: " + err
      });
    })

    loadJSON('theme').then((value) => {
      that.setState({
        theme: getMuiTheme(value)
      })
    }).catch((err) => {
      console.log("Loading Theme: " + err);
    })
  }

  handleSaveEntity() {
    const that = this;

    if (this.state.entity != null) {
      Popup.plugins().loader('Saving...');

      saveEntity(this.state.entity, this.state.isNew).then((value) => {
        Popup.close();

        that.setState({
          isNew: false
        })

      }).catch((err) => {
        Popup.close();
        that.setState({
          error: "Saving: " + err
        });
      });
    }

  }

  renderEntity(key) {
  const that = this;

    this.setState({prompt: false});

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
          Popup.close();
          that.setState({
            error: `Failed to load template (${entity.name},${entity.source},${entity.version})`
          });
        })

      }).catch(function(error) {
        Popup.close();
        that.setState({
          error: `Failed to load entity (${key})`
        });
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
        Popup.close();
        that.setState({
          error: 'Failed to create new entity (' + key + ')'
        });
      })
    }).catch((err) => {
      Popup.close();
      that.setState({
        error: 'Failed to load template (' + key + ')'
      });
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
              this.setState({
                promptTemplate: true
              })
            }
          },{
            id: 'load',
            name: 'Load',
            img: '/images/svg/load.svg',
            title: 'Load Entity',
            onClick: () => {
              this.setState({prompt: true});
            }
          },{
              id: 'save',
              name: 'Save',
              img: '/images/svg/save.svg',
              title: 'Save Entity',
              onClick: () => {that.handleSaveEntity();}
          }
        ]}/>
        <MuiThemeProvider muiTheme={this.state.theme}>
          <Paper zDepth={1} style={{padding: '5px', margin: '10px'}}>
            <EntityView data={entity} template={template} isNew={isNew} />
            <DialogPrompt
              title="Enter Entity Key"
              value=''
              opened={this.state.prompt}
              onClose={() => {this.setState({prompt: false})}}
              onSubmit={this.renderEntity}
            />
            <DialogMessage
              title="Error"
              opened={this.state.error != null}
              message={this.state.error}
              onClose={() => {this.setState({error: null})}}
            />
            {this.state.templates != null &&
            <DialogOptions
              title="Select Template"
              open={this.state.promptTemplate}
              onClose={() => {this.setState({promptTemplate: false})}}
              options={this.state.templates.map((item) => {
                return {
                  key: item.key,
                  name: item.name,
                  onClick: () => {
                    this.setState({promptTemplate: false});
                    this.renderNewEntity(item.key);
                  }
                }
              })}

            />
          }
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

registerLoader(Popup);

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
