import React, { Component } from 'react';
import Icon from 'react-icons-kit';
import {List, ListItem} from 'material-ui/List';
import { stack } from 'react-icons-kit/icomoon/stack';
import TextField from 'material-ui/TextField';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';

export class Definitions extends Component {
  constructor(props) {
    super(props);

    this.handleClick = this.handleClick.bind(this);
    this.handlePropsChange = this.handlePropsChange.bind(this);
    this.handleEditToggle = this.handleEditToggle.bind(this);
    this.handleChange = this.handleChange.bind(this);

    this.state = {
      definitions: props.data,
      open: "false",
    };
  }
  handleClick() {
    this.setState({ open: !this.state.open })
  }

  handlePropsChange(value) {
    this.setState({ name: value })
  }

  handleEditToggle(e) {
    this.setState({ edit: !this.state.edit })
  }

  handleChange(attr, value) {

  }

  render() {

    const { definitions } = this.state;

    return (
        <List>
          {definitions.map((item) => {return (
              <ListItem
                key={item.key}
                primaryText={item.name}
                secondaryText={item.dataType}
                rightIcon={<Icon icon={stack} />}
              />
          )})}
        </List>
    );
  }
}
