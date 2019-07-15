import React from 'react';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';

export default class Enumeration extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      elements: this.createOptions()
    }

    this.handleOnChange = this.handleOnChange.bind(this);
    this.createOptions = this.createOptions.bind(this);
  }

  componentDidUpdate(prevProps) {
    if (this.props.options !== prevProps.options) {
      this.setState(() => {return {elements: this.createOptions()}})
    }
  }

  handleOnChange(event) {
    this.props.onChange(this.props.attr, event.target.value);
  }

  createOptions() {

    var elements = [];
    var options = this.props.options;

    if (options) {

      if (this.props.sorted) {
        options = options.sort(function(a, b){
            var x = a.label.toLowerCase();
            var y = b.label.toLowerCase();
            if (x < y) {return -1;}
            if (x > y) {return 1;}
            return 0;
        });
      }

      elements = options.map((option) => {
        if (option === Object(option)) {
          return (
            <MenuItem key={option.enum} value={option.enum}>{option.label}</MenuItem>
          )
        } else {
          return (
            <MenuItem key={option} value={option}>{option}</MenuItem>
          )
        }
      })
    }

    return elements;
  }

  render () {
    const {field, style, attr} = this.props;
    const { elements } = this.state;

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
          {elements}
        </Select>
      </FormControl>

    );
  }

}
