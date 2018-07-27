import React from 'react';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import { DialogMessage } from './DialogMessage'

/**
 * Dialog to prompt a user
 */
export class DialogPrompt extends React.Component {

  constructor(props) {
    super(props);

    this.state = {value: props.value};

    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(value) {
    this.setState(() => {
      return {
        value: event.target.value
      }
    })
  }

  render() {

    return (
        <DialogMessage
          {...this.props}
          confirmation={true}
          onClick={() => {this.props.onSubmit(this.state.value)}}
        >
          <TextField
            autoFocus
            fullWidth={true}
            value={this.state.value}
            onChange={(event) => {this.handleChange(event.target.value)}}
            onKeyDown={ (e) => {
                  if (e.key === 'Enter') {
                    this.props.onSubmit(this.state.value);
                  }
                }}
          />
        </DialogMessage>

    );
  }
}
