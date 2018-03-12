import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import RaisedButton from 'material-ui/RaisedButton';
import {List, ListItem, makeSelectable} from 'material-ui/List';
import Avatar from 'material-ui/Avatar';
import Subheader from 'material-ui/Subheader';

export class DialogTemplateSelection extends React.Component {

  handleClose = () => {
    this.props.onClose();
  };

  handleClick = (key) => {
    this.props.onClose();
    this.props.onClick(key);
  }

  renderTemplateItem(item) {
    return (
      <ListItem
        key={item.key}
        id={item.key}
        primaryText={item.name}
        onClick={this.handleClick.bind(this, item.key)}
      />
    )
  }

  render() {
    const actions = [
      <FlatButton
        label="Cancel"
        primary={true}
        onClick={this.handleClose}
      />,
    ];

    return (
      <Dialog
        title="Select a Template"
        actions={actions}
        modal={false}
        open={this.props.open}
        onRequestClose={this.handleClose}
      >
        <List>
          {this.props.templates != null && this.props.templates.map((item) => {return this.renderTemplateItem(item);})}
        </List>
      </Dialog>
    )
  }

}
