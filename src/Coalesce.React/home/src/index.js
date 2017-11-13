import * as React from "react";
import * as ReactDOM from "react-dom";
import Popup from 'react-popup';

import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'

class Main extends React.Component {

  constructor(props) {
      super(props);

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
    return (
        <a href={url} target={url !== '#' ? "_blank" : ""} >
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
          {this.renderCard('#', require('common-components/img/enum.ico'), 'Enumerations', '(Comming Soon!!!) Create and edit enumerations used by Coalesce.)')}
          {this.renderCard('http://localhost:8080/template-creator/views/editor', require('common-components/img/template.ico'), 'Templates', 'Editor or create new templates for Coalesce entities.')}
          {this.renderCard('map', require('common-components/img/map.ico'), 'Map', 'Visualize different layers provided by a Geo Server fed from a Coalesce database.')}
          {this.renderCard('#', require('common-components/img/manager.ico'), 'Manager', '(Comming Soon!!!) Connect services and databases.')}
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





ReactDOM.render(
  <Main />,
  document.getElementById('main')
);
