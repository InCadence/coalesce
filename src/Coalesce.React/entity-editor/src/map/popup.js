import React from 'react'

export default class Popup extends React.Component {

  constructor(props) {
    super(props)
  }

  componentWillUnmount() {
  }
  render() {
    return (
      <div id={"popup" + this.props.uniqueID} className="ol-popup">
        <p id={'lonlat' + this.props.uniqueID}>{this.props.coords[0]}</p>
        <p id={'hdms' + this.props.uniqueID}>{this.props.coords[1]}</p>
        <p id={'mgrs' + this.props.uniqueID}>{this.props.coords[2]}</p>
      </div>
    );
  }
}
