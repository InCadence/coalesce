import React from 'react';

export class KeyPress extends React.Component {

  constructor(props){
    super(props);

    this.keypress = this.keypress.bind(this);
    this.keyrelease = this.keyrelease.bind(this);
    this.handleClick = this.handleClick.bind(this);

    this.state = {pressed: false};
  }

  componentDidMount(){
    document.body.addEventListener("keydown", this.keypress, false);
    document.body.addEventListener("keyup", this.keyrelease, false);
  }

  componentWillUnmount(){
    document.body.removeEventListener("keydown", this.keypress, false);
    document.body.removeEventListener("keyup", this.keyrelease, false);
  }

  keypress(event) {
    if (event.keyCode === this.props.keyCode && !this.state.pressed) {
      this.setState((prevState, props) => {return {pressed: true}});
    }
    console.log(event.keyCode);
  }

  keyrelease(event) {
    if (event.keyCode === this.props.keyCode) {
      this.setState((prevState, props) => {return {pressed: false}});
    }
  }

  handleClick() {
    if (this.state.pressed) {
      this.props.onClick();
    }
  }

  render() {
    return (
      <div onClick={this.handleClick}>{this.props.children}</div>
    );
  }

}
