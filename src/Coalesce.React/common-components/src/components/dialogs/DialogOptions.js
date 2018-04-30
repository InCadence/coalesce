import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import {List, ListItem } from 'material-ui/List';

export class DialogOptions extends React.Component {

  renderItem(item) {
    return (
      <ListItem
        key={item.key}
        id={item.key}
        primaryText={item.name}
        onClick={item.onClick}
      />
    )
  }

  render() {
    const actions = [
      <FlatButton
        label="Cancel"
        primary={true}
        onClick={this.props.onClose}
      />,
    ];

    return (
      <Dialog
        title={this.props.title}
        actions={actions}
        modal={false}
        open={this.props.open}
        onRequestClose={this.props.onClose}
        autoScrollBodyContent={true}
      >
        <List>
          {this.props.options.map((item) => {return this.renderItem(item);})}
        </List>
      </Dialog>
    )
  }

}
