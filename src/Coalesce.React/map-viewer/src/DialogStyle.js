import React from 'react';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';
import { DialogOptions, DialogMessage } from 'coalesce-components/lib/components/dialogs';
import { PromptDropdown } from './prompt-dropdown.js'
import { StyleSelection } from './style'

/**
 * Dialog to display messages.
 */
export class DialogStyle extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      data: null
    };
  }


  handleChange = (value) => {
      this.setState({data: value});
  };


  handleSubmit = () => {
    this.props.onClick(this.state.data);
  }

  render() {

    return (
      <DialogMessage
        title="Select Style"
        opened={true}
        onClose={this.props.onClose}
        onClick={this.handleSubmit}
        confirmation
      >
        <StyleSelection onChange={this.handleChange} presets={this.props.styles} data={this.props.styles[0]}/>
      </DialogMessage>
    )
  }
}


