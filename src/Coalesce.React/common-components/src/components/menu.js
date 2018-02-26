import React from 'react';
import { Navbar, NavItem, Nav } from 'react-bootstrap';
//import { IconButton } from 'common-components/lib/components/InconButton.js';

require('common-components/bootstrap/css/bootstrap.min.css');

var rootUrl;

if (window.location.port == 3000) {
  rootUrl  = 'http://' + window.location.hostname + ':8181';
} else {
  rootUrl  = '';
}

export class Menu extends React.PureComponent {

  constructor(props) {
    super(props);
  }

  renderNavItem(item) {
    if (this.props.isTextOnly) {

      if (item.onClick != null) {
        return (
            <NavItem eventKey={item.id} href="#">
              <div onClick={item.onClick}>{item.name}</div>
            </NavItem>
          )
      } else {
        return (
            <NavItem eventKey={item.id} href={item.url}>
              <div>{item.name}</div>
            </NavItem>
          )
      }

    } else {

      if (item.onClick != null) {
        return (
            <NavItem eventKey={item.id} href="#">
              <img src={rootUrl + item.img} alt={item.name} title={item.title} className="coalesce-img-button enabled" onClick={item.onClick}/>
            </NavItem>
          )
      } else {
        return (
            <NavItem eventKey={item.id} href={item.url}>
              <img src={rootUrl + item.img} alt={item.name} title={item.title} className="coalesce-img-button enabled" />
            </NavItem>
          )
      }

    }
  }

  render () {

    var home;
    var isTextOnly = this.props.isTextOnly;

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
            <img src={rootUrl + this.props.logoSrc} alt="logo" className="coalesce-img-button"  title="logo"  />
          </Navbar.Brand>
          <Navbar.Brand>
            <a id="templateName" href="#"><div>{this.props.title}</div></a>
          </Navbar.Brand>
          <Navbar.Toggle />
        </Navbar.Header>
        <Navbar.Collapse>
          <Nav pullRight>
            {home}
            {this.props.items.map(this.renderNavItem.bind(this))}
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
  home: '/',
  isTextOnly: false,
}
