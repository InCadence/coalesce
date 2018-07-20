import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';

/**
 * Dialog to display error messages.
 */
export class DialogMessage extends React.PureComponent {

  constructor(props) {
    super(props)
  }
  render() {

    var actions = [];
    //actions prop will be used to decide if user needs to make decision
    if(this.props.actions == null){
    //If actions is not set, this renders a simple message, one ok button input from user
      actions = [
        <FlatButton
          label="OK"
          primary={true}
          onClick={this.props.onClose}
        />
      ];
    }
    else if (this.props.actions === 'edit') {
      actions = [
        <FlatButton
          label="Edit"
          secondary={true}
          onClick={this.props.onEditToggle}
        />,
        <FlatButton
          label="OK"
          primary={true}
          onClick={this.props.onClose}
        />,
      ];
    }
    else if (this.props.actions === 'edited') {
      actions = [
        <FlatButton
          label="Cancel"
          secondary={true}
          onClick={this.props.onCancel}
        />,
        <FlatButton
          label="OK"
          primary={true}
          onClick={this.props.onClose}
        />,
      ];
    }

    return (
      <Dialog
        title={this.props.title}
        actions={actions}
        modal={false}
        open={this.props.opened}
        onRequestClose={this.props.onClose}
        autoScrollBodyContent={true}
      >
        {this.props.message}
      </Dialog>
    );
  }
}
