import React from 'react';
import TextField from '@material-ui/core/TextField';
import { DialogMessage } from "coalesce-components/lib/components/dialogs";

/**
 * Dialog to prompt a user
 */
export class DialogPrompt extends React.Component {

  constructor(props) {
    super(props);

    this.state = {value: props.value};

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleKeyDown = this.handleKeyDown.bind(this);
  }

  handleChange(event) {
    const value = event.target.value;

    this.setState(() => {
      return {
        value: value
      }
    })
  }

  handleKeyDown(event) {
    if (event.key === 'Enter') {
      this.props.onSubmit(this.state.value);
    }
  }

  handleSubmit() {
    this.props.onSubmit(this.state.value);
  }

  render() {

    return (
        <DialogMessage
          {...this.props}
          confirmation={true}
          onClick={this.handleSubmit}
        >
          <TextField
            autoFocus
            fullWidth={true}
            value={this.state.value}
            onChange={this.handleChange}
            onKeyDown={this.handleKeyDown}
          />
        </DialogMessage>

    );
  }
}
