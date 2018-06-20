import React from 'react';
import MenuItem from 'material-ui/MenuItem';
import SelectField from 'material-ui/SelectField';


export default class Enumeration extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      list: this.props.list,
      field: this.props.field,
      style: this.props.style,
      label: this.props.label,
      showLabels: this.props.showLabels,
      attr: this.props.attr,
    };
  }

  handleOnChange(attr, values) {
    this.props.handleOnChange(attr, values);
  }

  render () {
    const {field, style, attr} = this.state;

    return(
    <SelectField
      id={field.key}
      fullWidth={true}
      floatingLabelText={this.state.label}
      underlineShow={this.props.showLabels}
      multiple={this.state.list}
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
    </SelectField>);
  }

}
