import React from 'react';
import { Navbar, NavItem, Nav } from 'react-bootstrap';

export class Menu extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;
  }

  render () {

    var items = [];
    var home;
    var isTextOnly = this.props.isTextOnly;

    this.state.items.forEach(function (item) {
      if (isTextOnly) {
        items.push(
            <NavItem eventKey={item.id} href="#">
              <div onClick={item.onClick}>{item.name}</div>
            </NavItem>
          )
      } else {
        items.push(
            <NavItem eventKey={item.id} href="#">
              <img src={item.img} alt={item.name} title={item.title} className="coalesce-img-button enabled" onClick={item.onClick}/>
            </NavItem>
          )
      }
    });

    if (this.props.homeEnabled)
    {
      if (isTextOnly) {
        home = <div>Home</div>;
      } else {
        home = <img className="coalesce-img-button enabled" src="/images/svg/home.svg" alt='home' title='Home' />;
      }
    }

    return (
      <Navbar collapseOnSelect>
        <Navbar.Header>
          <Navbar.Brand>
            <img src={this.props.logoSrc} alt="logo" className="coalesce-img-button"  title="logo"  />
          </Navbar.Brand>
          <Navbar.Brand>
            <a id="templateName" href="#">{this.state.title}</a>
          </Navbar.Brand>
          <Navbar.Toggle />
        </Navbar.Header>
        <Navbar.Collapse>
          <Nav pullRight>
            <NavItem eventKey="home" href={this.state.home}>
              {home}
            </NavItem>
            {items}
          </Nav>
        </Navbar.Collapse>
      </Navbar>
    )
  }
}

Menu.defaultProps = {
  logoSrc: "",
  homeEnabled: true,
  home: '/',
  isTextOnly: false,
}
