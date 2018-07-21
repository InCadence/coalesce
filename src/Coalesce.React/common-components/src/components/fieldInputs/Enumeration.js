import React from 'react';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';

export default class Enumeration extends React.Component {
  constructor(props) {
    super(props);

    this.handleOnChange = this.handleOnChange.bind(this);
  }

  handleOnChange(event) {
    this.props.onChange(this.props.attr, event.target.value);
  }

  render () {
    const {field, style, attr} = this.props;

    var options = [];

    if (this.props.options) {
      options = this.props.options.map((item) => {
        return (
          <MenuItem key={item.enum} value={item.enum}>{item.label}</MenuItem>
        )
      })
    }

    return(
      <FormControl style={{width: "100%"}}>
        {this.props.showLabels &&
          <InputLabel htmlFor="node-selection-helper">{this.props.label}</InputLabel>
        }
        <Select
          id={field.key}
          fullWidth
          multiple={this.props.list}
          style={style.root}
          inputProps={{style: style.root}}
          SelectDisplayProps={{style: style.root}}
          value={field[attr] ? field[attr] : []}
          onChange={this.handleOnChange}
          onKeyDown={this.props.onKeyDown}
        >
          {options}
        </Select>
      </FormControl>

    );
  }

}
