import React from 'react';
import { Navbar, NavItem, Nav } from 'react-bootstrap';

import { withTheme } from '@material-ui/core/styles';
import { IconButton, Image } from 'coalesce-components/lib/components';

require('coalesce-components/bootstrap/css/bootstrap.min.css');

class Menu extends React.PureComponent {

  constructor(props) {
    super(props);

    this.renderNavItem = this.renderNavItem.bind(this);
  }

  renderNavItem(item) {
    if (this.props.isTextOnly) {

      if (item.onClick != null) {
        return (
            <NavItem key={item.id} eventKey={item.id} href="#">
              <div onClick={item.onClick}>{item.title ? item.title : item.name}</div>
            </NavItem>
          )
      } else {
        return (
            <NavItem key={item.id} eventKey={item.id} href={item.url}>
              <div>{item.title ? item.title : item.name}</div>
            </NavItem>
          )
      }

    } else {

      if (item.onClick != null) {
        return (
            <NavItem key={item.id} eventKey={item.id} href="#">
              <IconButton icon={item.img} title={item.title ? item.title : item.name} onClick={item.onClick} size={40} square/>
            </NavItem>
          )
      } else {
        return (
            <NavItem key={item.id} eventKey={item.id} href={item.url}>
              <IconButton icon={item.img} title={item.title ? item.title : item.name} size={40} square/>
            </NavItem>
          )
      }

    }
  }

  render () {

    var home;
    var palette;

    if (this.props.theme) {
      palette = this.props.theme.palette.icons ? this.props.theme.palette.icons : this.props.theme.palette.primary
    } else {
      palette = {
        contract: null,
        main: null,
        dark: null,
        light: null
      }
    }

    if (this.props.homeEnabled)
    {
      home = this.renderNavItem({
        name: "home",
        title: "Home",
        img: "/images/svg/home.svg",
        url: this.props.home
      })
    }

    return (
      <Navbar collapseOnSelect>
        <Navbar.Header>
          <Navbar.Brand>
            <Image
              icon={this.props.logoSrc}
              title={`v${process.env.REACT_APP_VERSION}`}
              size={40}
              palette={palette}
              style={{float: "left"}}
              square
            />
          </Navbar.Brand>
          <Navbar.Brand>
            <a id="templateName" href="#">{this.props.title}</a>
          </Navbar.Brand>
          <Navbar.Toggle />
        </Navbar.Header>
        <Navbar.Collapse>
          <Nav pullRight>
            {home}
            {this.props.items.map(this.renderNavItem)}
          </Nav>
        </Navbar.Collapse>
      </Navbar>
    )
  }
}

Menu.defaultProps = {
  logoSrc: 'set package.json.icon',
  title: 'set package.json.title',
  homeEnabled: true,
  home: '/home/',
  isTextOnly: false,
}

export default withTheme(Menu);
