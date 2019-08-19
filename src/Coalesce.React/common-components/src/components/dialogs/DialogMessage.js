import React from 'react';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';

/**
 * Dialog to display messages.
 */
export class DialogMessage extends React.PureComponent {

  render() {
    var actions = [] ;
    
    if (this.props.onOther) {
      actions.push(
        <Button onClick={this.props.onOther} color="primary">
          {this.props.onOtherText}
        </Button>
      )
    }

    if (this.props.confirmation) {
      actions.push(
        <Button onClick={this.props.onClose} color="secondary">
          {this.props.onCloseText}
        </Button>
      )
    }
    
    actions.push(
      <Button onClick={this.props.confirmation ? this.props.onClick : this.props.onClose} color="primary">
        {this.props.onClickText}
      </Button>
    )

    return (
      (this.props.opened &&
        <Dialog
          open={this.props.opened}
          onClose={this.props.onClose}
          scroll="paper"
          aria-labelledby="scroll-dialog-title"
          PaperProps={{ style: { width: '100%' } }}
          maxWidth={this.props.maxWidth}
        >
          <DialogTitle id="scroll-dialog-title">{this.props.title}</DialogTitle>
          <DialogContent>
            <DialogContentText>
              {this.props.message}
            </DialogContentText>
            {this.props.children}
          </DialogContent>
          <DialogActions>
            {actions}
          </DialogActions>
        </Dialog>
      )
    );
  }
}

DialogMessage.defaultProps = {
  opened: true,
  confirmation: false,
  title: null,
  message: null,
  onOther: undefined, 
  onOtherText: "Other",
  onClose: undefined,
  onCloseText: "Cancel",
  onClick: undefined,
  onClickText: "OK"
}
