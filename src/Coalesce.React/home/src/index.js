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

class Main extends React.Component {

  constructor(props) {
      super(props);

      this.promptNotAvailable = this.promptNotAvailable.bind(this);
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
              <img src={img} alt={title} height="64" className="shadow"/>
            </div>
            <div className="row">
              <label>{title}</label>
            </div>
            <div className="row">
              <div className="scroll-box">
                <p>{description}</p>
              </div>
            </div>
          </div>
        </a>
    )
  }

  renderGroup(that, group) {

    var cards = [];

    group.cards.forEach(function (card) {
      cards.push(that.renderCard(card.url, card.img, card.name, card.desc));
    });

    return (
        <div>
          <h2>{group.name}</h2>
          {cards}
        </div>
    )
  }

  render() {
    const {settings} = this.state;
    const that = this;
    var groups = [];

    settings.groups.forEach(function (group) {
      groups.push(that.renderGroup(that, group));
    });

    return (
      <center>
        <img alt="Coalesce" src={settings.banner} />
        {groups}
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
