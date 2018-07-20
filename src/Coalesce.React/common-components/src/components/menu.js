import React from 'react';
import { Navbar, NavItem, Nav } from 'react-bootstrap';
import { withTheme } from '@material-ui/core/styles';
import { IconButton } from 'common-components/lib/components';
import { Image } from 'common-components/lib/components/image'

require('common-components/bootstrap/css/bootstrap.min.css');

export class Menu extends React.PureComponent {

  constructor(props) {
    super(props);

    this.renderNavItem = this.renderNavItem.bind(this);
  }

  renderNavItem(item) {
    if (this.props.isTextOnly) {

      if (item.onClick != null) {
        return (
            <NavItem key={item.id} eventKey={item.id} href="#">
              <div onClick={item.onClick}>{item.name}</div>
            </NavItem>
          )
      } else {
        return (
            <NavItem key={item.id} eventKey={item.id} href={item.url}>
              <div>{item.name}</div>
            </NavItem>
          )
      }

    } else {

      if (item.onClick != null) {
        return (
            <NavItem key={item.id} eventKey={item.id} href="#">
              <IconButton icon={item.img} title={item.name} onClick={item.onClick} size={40} />
            </NavItem>
          )
      } else {
        return (
            <NavItem key={item.id} eventKey={item.id} href={item.url}>
              <IconButton icon={item.img} title={item.name} size={40} />
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
            <Image
              icon={this.props.logoSrc}
              title="logo"
              size={40}
              theme={this.props.theme}
              style={{float: "left"}}
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
  home: '/home',
  isTextOnly: false,
}

export default withTheme()(Menu);
