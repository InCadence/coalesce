import * as React from "react";
import * as ReactDOM from "react-dom";
import Popup from 'react-popup';
import {registerErrorPrompt} from 'common-components/lib/register.js'

import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'

var rootUrl;

if (window.location.port == 3000) {
  rootUrl  = 'http://' + window.location.hostname + ':8181';
} else {
  rootUrl  = 'http://' + window.location.hostname + ':' + window.location.port;
}

registerErrorPrompt(Popup);

var defaultSettings = {
  'search.url' : 'search',
  'editor.url' : 'entityeditor',
  'enumerations.url' : 'enumerations',
  'templatecreator.url' : 'creator',
  'map.url' : 'map',
  'settings.url' : 'settings',
  'manager.url' : 'manager',

  'restapi.url' : 'https://github.com/InCadence/coalesce/wiki/REST-API',
  'deployment.url' : 'https://github.com/InCadence/coalesce/wiki/Karaf-Distribution',
  'javadocs.url' : 'javadocs',
  'source.url' : 'https://github.com/InCadence/coalesce',
}

class Main extends React.Component {

  constructor(props) {
      super(props);

      this.promptNotAvailable = this.promptNotAvailable.bind(this);

      Object.keys(defaultSettings).forEach(function (key) {
          if (props.settings[key] == "") {
            props.settings[key] = defaultSettings[key];
          }
      });

      this.state = props;
  }

  promptNotAvailable() {
    Popup.create({
        title: 'Not Availble',
        content: 'This service is still coalescing and will be available soon!!!',
        className: 'alert',
        buttons: {
            right: ['ok']
        }
    }, true);
  }

  renderCard(url, img, title, description) {
    //target={url !== '#' ? "_blank" : ""}
    return (
        <a href={url} >
          <div className='card'>
            <div className="row">
              <img src={img} alt={title} height="64"/>
            </div>
            <div className="row">
              <label>{title}</label>
            </div>
            <div className="row">
              <div class="scroll-box">
                <p>{description}</p>
              </div>
            </div>
          </div>
        </a>
    )
  }

  render() {
    const {settings} = this.state;

    return (
      <center>
        <h1 className="coalesce-banner">Coalesce Enterprise Data Broker</h1>
        <h2>Applications</h2>
        <div>
          {this.renderCard(settings['search.url'], require('common-components/img/search2.ico'), 'Search', 'Find Coalesce entities matching your criteria.')}
          {this.renderCard(settings['editor.url'], require('common-components/img/edit.ico'), 'Editor', 'Edit or create new Coalesce entities.')}
          {this.renderCard(settings['enumerations.url'], require('common-components/img/enum.ico'), 'Enumerations', 'Create and edit enumerations used by Coalesce.')}
          {this.renderCard(settings['templatecreator.url'], require('common-components/img/template.ico'), 'Templates', 'Editor or create new templates for Coalesce entities.')}
          {this.renderCard(settings['map.url'], require('common-components/img/map.ico'), 'Map', 'Visualize different layers provided by a Geo Server fed from a Coalesce database.')}
          {this.renderCard(settings['settings.url'], require('common-components/img/settings.ico'), 'Settings', 'Configure server defined client properties.')}
          {this.renderCard(settings['manager.url'], require('common-components/img/manager.ico'), 'Manager', 'Visualize how the services within this container are wired together.')}
        </div>
        <h2>Documentation</h2>
        <div>
          {this.renderCard(settings['restapi.url'], require('common-components/img/api.ico'), 'REST API', "View Coalesce's REST API to integrate your applications.")}
          {this.renderCard(settings['deployment.url'], require('common-components/img/deploy.ico'), 'Deployment', 'View how to deploy and run a Coalesce server.')}
          {this.renderCard(settings['javadocs.url'], require('common-components/img/java-docs.ico'), 'Java Docs', 'Generated code documentation.')}
          {this.renderCard(settings['source.url'], require('common-components/img/code.ico'), 'Source Code', 'Download and contribute to the open source project.')}
        </div>
      </center>
    )
  }
}

fetch(rootUrl + '/cxf/data/property', {
  method: "POST",
  body: JSON.stringify(Object.keys(defaultSettings)),
  headers: new Headers({
    'content-type': 'application/json; charset=utf-8'
  }),
})
  .then(res => res.json())
  .then(data => {
    ReactDOM.render(
      <Main settings={data} />,
      document.getElementById('main')
    );
}).catch(function(error) {
  Popup.plugins().promptError("Loading Properties: " + error);
});
