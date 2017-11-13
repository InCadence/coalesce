import React from 'react';

export class Menu extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;
  }

  render () {

    var items = [];

    this.state.items.forEach(function (item) {
      if (item.img != null) {
        items.push(<li key={item.id}><img src={item.img} alt={item.name} title={item.title} className="coalesce-img-button enabled" onClick={item.onClick}/></li>);
      } else {
        items.push(<li key={item.id}><a  href="#"><div onClick={item.onClick}>{item.name}</div></a></li>);
      }
    });

    return (
      <div className="container">
        <div className="navbar-header">
            <button type="button" className="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                <span className="icon-bar"></span>
                <span className="icon-bar"></span>
                <span className="icon-bar"></span>
            </button>
            <img className="navbar-icon" src={require('common-components/img/logo.ico')} alt='InCadence' /><a id="templateName" className="navbar-brand" href="#">{this.state.title}</a>
        </div>
        <div className="collapse navbar-collapse" id="myNavbar">
          <ul className="nav navbar-nav navbar-right">
            {items}
          </ul>
        </div>
      </div>
    )
  }
}
