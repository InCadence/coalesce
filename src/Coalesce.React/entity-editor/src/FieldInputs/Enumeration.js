import React from 'react';
import FieldInput from '../FieldInput.js'

export class Enumeration extends FieldInput {
  constructor(props) {
    this.state = super(props);
  }

  handleOnChange(attr, value) {
    this.state = super.handleOnChange(attr, values);
  }

  render () {
    const {field, style} = this.state;

    <SelectField
      id={field.key}
      fullWidth={true}
      floatingLabelText={label}
      underlineShow={this.props.showLabels}
      multiple={this.state.list == 'true'}
      value={field[attr] ? field[attr].toUpperCase() : null}
      style={style.root}
      labelStyle={style.root}
      iconStyle={style.none}
      hintStyle={style.none}
      floatingLabelStyle={style.none}
      errorStyle={style.none}
      onChange={(event, index, values) => {this.handleOnChange(attr, values)}}
    >
      {this.props.options && this.props.options.map((item) => {
        return (
          <MenuItem key={item.enum} value={item.enum} primaryText={item.label} />
        )
      })}
    </SelectField>
  }

}
