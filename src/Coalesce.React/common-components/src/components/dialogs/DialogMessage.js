import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';

/**
 * Dialog to display error messages.
 */
export class DialogMessage extends React.PureComponent {

  render() {
    var actions = [];
    //Confirmation prop will be used to decide if user needs to make decision
    if(this.props.confirmation == null){
    //If confirmation is not set, this renders a simple message, one ok button input from user
    actions = [
      <FlatButton
        label="OK"
        primary={true}
        onClick={this.props.onClose}
      />
    ];
    }
    else{
      //If confirmation is set (to anything really), user is deciding between ok or cancel
      actions = [
      <div>
      <FlatButton
         label="OK"
         primary={true}
         onClick={this.props.onClick}
       />
       <FlatButton
         label="Cancel"
         primary={true}
         onClick={this.props.onClose}
       />
       </div>
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
