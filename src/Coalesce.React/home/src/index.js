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

class Main extends React.Component {

  constructor(props) {
      super(props);

      this.state = props;
      this.promptNotAvailable = this.promptNotAvailable.bind(this);
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

    return (
      <center>
        <h1>Applications</h1>
        <div>
          {this.renderCard('search', require('common-components/img/search2.ico'), 'Search', 'Find Coalesce entities matching your criteria.')}
          {this.renderCard('entityeditor', require('common-components/img/edit.ico'), 'Editor', 'Edit or create new Coalesce entities.')}
          {this.renderCard('enumerations', require('common-components/img/enum.ico'), 'Enumerations', 'Create and edit enumerations used by Coalesce.)')}
          {this.renderCard(this.state.templatecreator_url, require('common-components/img/template.ico'), 'Templates', 'Editor or create new templates for Coalesce entities.')}
          {this.renderCard('map', require('common-components/img/map.ico'), 'Map', 'Visualize different layers provided by a Geo Server fed from a Coalesce database.')}
          {this.renderCard('settings', require('common-components/img/settings.ico'), 'Settings', 'Configure server defined client properties.')}
          {this.renderCard('manager', require('common-components/img/manager.ico'), 'Manager', 'Connect services and databases.')}
        </div>
        <h1>Documentation</h1>
        <div>
          {this.renderCard('https://github.com/InCadence/coalesce/wiki/REST-API', require('common-components/img/api.ico'), 'REST API', "View Coalesce's REST API to integrate your applications.")}
          {this.renderCard('https://github.com/InCadence/coalesce/wiki/Karaf-Distribution', require('common-components/img/deploy.ico'), 'Deployment', 'View how to deploy and run a Coalesce server.')}
          {this.renderCard('#', require('common-components/img/java-docs.ico'), 'Java Docs', '(Coming Soon!!!) Code documentation.')}
          {this.renderCard('https://github.com/InCadence/coalesce', require('common-components/img/code.ico'), 'Source Code', 'Download and contribute to the open source project.')}
        </div>
      </center>
    )
  }
}

fetch(rootUrl + '/cxf/data/property/templatecreator.url')
  .then(res => res.text())
  .then(data => {
    ReactDOM.render(
      <Main templatecreator_url={data} />,
      document.getElementById('main')
    );
}).catch(function(error) {
  Popup.plugins().promptError("Saving: " + error);
});
