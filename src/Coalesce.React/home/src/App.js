import React from "react";
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';

import { withTheme } from '@material-ui/core/styles';

import Image from 'coalesce-components/lib/components/image'

import 'coalesce-components/css/coalesce.css'

class App extends React.PureComponent {

  constructor(props) {
    super(props);

    this.state = {
      tabindex: 0
    }
    this.renderGroup = this.renderGroup.bind(this);
    this.renderCard = this.renderCard.bind(this);
  }

  handleChange = (event, newValue) => {
    console.log(newValue);
    
    this.setState(() => { return {tabindex: newValue}})
  }

  render() {
    const { settings } = this.props;
    const { tabindex } = this.state;

    console.log(tabindex);
    

    return (
      <center>
        <img alt="Coalesce" src={settings.banner} />
        <Tabs
        value={tabindex}
        onChange={this.handleChange}
        indicatorColor="primary"
        textColor="primary"
        centered
      >
          {
          settings.groups.map(this.renderGroupTab)
          }
        </Tabs>
        { tabindex != undefined &&
          this.renderGroup(settings.groups[tabindex])
        }
      </center>
    )
  }

  renderGroupTab(group) {
    return (
      <Tab label={group.name} />
    )
  }

  renderGroup(group) {
    return (
      <div key={group.name} >
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
