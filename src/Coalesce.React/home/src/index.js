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
  rootUrl  = '';
}

registerErrorPrompt(Popup);

class Main extends React.PureComponent {

  constructor(props) {
      super(props);
  }

  render() {
    const {settings} = this.props;

    return (
      <center>
        <img alt="Coalesce" src={rootUrl + settings.banner} />
        {settings.groups.map(renderGroup)}
      </center>
    )
  }
}

fetch(rootUrl + '/cxf/data/property/home.json', {
  method: "GET",
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

function renderGroup(group) {
  return (
      <div>
        <h2>{group.name}</h2>
        {group.cards.map(renderCard)}
      </div>
  )
}

function renderCard(card) {
  return (
      <a href={card.url} >
        <div className='card'>
          <div className="card-row">
            <img src={rootUrl + card.img} alt={card.name} height="64" className="shadow"/>
          </div>
          <div className="card-row">
            <label>{card.name}</label>
          </div>
          <div className="card-row">
            <div className="scroll-box">
              <p>{card.desc}</p>
            </div>
          </div>
        </div>
      </a>
  )
}
