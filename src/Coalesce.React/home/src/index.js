import React from "react";
import ReactDOM from "react-dom";

import { withTheme, createMuiTheme, MuiThemeProvider } from '@material-ui/core/styles';

import { loadJSON } from 'common-components/lib/js/propertyController';
import { getRootKarafUrl } from 'common-components/lib/js/common';

import { Image } from 'common-components/lib/components/image'

import 'common-components/css/coalesce.css'

var rootUrl = getRootKarafUrl("") + '..';

class Main extends React.PureComponent {

  constructor(props) {
      super(props);

      this.renderGroup = this.renderGroup.bind(this);
      this.renderCard = this.renderCard.bind(this);
  }

  render() {
    const {settings} = this.props;

    return (
      <center>
        <img alt="Coalesce" src={rootUrl + settings.banner} />
          {settings.groups.map(this.renderGroup)}
      </center>
    )
  }

  renderGroup(group) {
    return (
        <div key={group.name}>
          <h2>{group.name}</h2>
          {group.cards.map(this.renderCard)}
        </div>
    )
  }

  renderCard(card) {
    return (
        <a href={card.url} key={card.name}>
          <div className='card' style={{
            backgroundColor: this.props.theme.palette.primary.dark,
            borderColor: this.props.theme.palette.primary.light
          }}>
            <div >
              <Image icon={card.img} size={64} class="shadow"/>
            </div>
            <div style={{color: this.props.theme.palette.primary.contrastText}}>
              {card.name}
            </div>
            <div>
              <div className="scroll-box">
                <p style={{color: this.props.theme.palette.primary.contrastText}}>{card.desc}</p>
              </div>
            </div>
          </div>
        </a>
    )
  }
}

const MainThemed = withTheme()(Main)

loadJSON('theme').then((theme) => {
  loadJSON("home").then((data) => {
    ReactDOM.render(
      <MuiThemeProvider theme={createMuiTheme(theme)}>
        <MainThemed settings={data} />
      </MuiThemeProvider>,
      document.getElementById('main')
    );
  })
}).catch((err) => {
  console.log("(FAILED) Loading Configuration");
})
