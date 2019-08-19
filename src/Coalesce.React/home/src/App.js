import React from "react";

import { withTheme } from '@material-ui/core/styles';

import Image from 'coalesce-components/lib/components/image'

import 'coalesce-components/css/coalesce.css'

class App extends React.PureComponent {

  constructor(props) {
    super(props);

    this.renderGroup = this.renderGroup.bind(this);
    this.renderCard = this.renderCard.bind(this);
  }

  render() {
    const { settings } = this.props;

    return (
      <center>
        <img alt="Coalesce" src={settings.banner} />
        {
            settings.groups.map(this.renderGroup)
        }
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

    const palette = this.props.theme.palette.primary;
    const iconPalette = this.props.theme.palette.icons ? this.props.theme.palette.icons : palette

    return (
      <a href={card.url} key={card.name}>
        <div className='card' style={{
          backgroundColor: palette.dark,
          borderColor: palette.light
        }}>
          <div >
            <Image icon={card.img} size={64} palette={iconPalette} class="shadow" />
          </div>
          <div style={{ color: palette.contrastText }}>
            {card.name}
          </div>
          <div>
            <div className="scroll-box">
              <p style={{ color: palette.contrastText }}>{card.desc}</p>
            </div>
          </div>
        </div>
      </a>
    )
  }
}

export default withTheme(App)