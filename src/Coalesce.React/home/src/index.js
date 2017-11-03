import * as React from "react";
import * as ReactDOM from "react-dom";
import {Menu} from 'common-components/lib/menu.js'

import './index.css'

class Main extends React.Component {

  render() {

    return (
      <div>
        <div clasName='row'>
          {renderCard('search', 'Search', 'Description goes here')}
          {renderCard('entityeditor', 'Editor', 'Description goes here')}
        </div>
        <div clasName='row'>
          {renderCard('http://localhost:8080/template-creator/views/editor', 'Templates', 'Description goes here')}
          {renderCard('map', 'Map', 'Description goes here')}
        </div>
        <div clasName='row'>
          {renderCard('https://github.com/InCadence/coalesce/wiki/rest', 'REST API', 'Description goes here')}
          {renderCard('https://github.com/InCadence/coalesce/wiki/Karaf-Distribution', 'Deployment', 'Description goes here')}
        </div>
      </div>
    )
  }


}

function renderCard(url, title, description) {
  return (
    <div className='col-sm-6'>
      <a href={url} target="_blank">
        <div className='card'>
          {title}
        </div>
      </a>
      <span>{description}</span>
    </div>
  )
}

ReactDOM.render(
  <Main rootUrl='http://localhost:8181/' />,
  document.getElementById('main')
);

ReactDOM.render(
  <Menu items={[/*No Menu Items*/]}/>,
  document.getElementById('myNavbar')
);
