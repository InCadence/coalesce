import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';

/**
 * Dialog to display error messages.
 */
export class DialogMessage extends React.PureComponent {

  render() {
    var actions = [];
    console.log("DialogMessage", this.props.confirmation);
    if(this.props.confirmation == null){
    actions = [
      <FlatButton
        label="OK"
        primary={true}
        onClick={this.props.onClose}
      />
    ];
    }
    else{
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
