import React from 'react'

export default class Number extends React.Component {

  constructor(props) {
    super(props);
  }

  handleOnChange(attr, value) {
  }


  render() {
    return(
      <TextField
        id={field.key}
        type='number'
        step={this.props.step}
        fullWidth={true}
        floatingLabelText={label}
        underlineShow={this.props.showLabels}
        style={style.root}
        value={field[attr]}
        defaultValue={field.defaultValue}
        onChange={(event, value) => {this.handleOnChange(attr, value)}}
      />);

  }
}
