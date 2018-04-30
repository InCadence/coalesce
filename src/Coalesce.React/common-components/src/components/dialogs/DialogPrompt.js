import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import TextField from 'material-ui/TextField';

/**
 * Dialog to display error messages.
 */
export class DialogPrompt extends React.Component {

  constructor(props) {
    super(props);

    this.state = {value: props.value};
  }

  render() {
    const actions = [
      <FlatButton
        label="Cancel"
        primary={true}
        onClick={this.props.onClose}
      />,
      <FlatButton
        label="OK"
        primary={true}
        onClick={() => {this.props.onSubmit(this.state.value)}}
      />
    ];

    return (
        <Dialog
          title={this.props.title}
          actions={actions}
          modal={false}
          open={this.props.opened}
          onRequestClose={this.props.onClose}
          autoScrollBodyContent={true}
        >
          <TextField
            autoFocus
            fullWidth={true}
            value={this.state.value}
            onChange={(event, value) => {this.setState({value: value})}}
          />
        </Dialog>
    );
  }
}
