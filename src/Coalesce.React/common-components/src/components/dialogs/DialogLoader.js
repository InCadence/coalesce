import React from 'react';
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import CircularProgress from '@material-ui/core/CircularProgress';

/**
 * Dialog to display when waiting on a request.
 */
export class DialogLoader extends React.PureComponent {

  render() {
    return (
      <Dialog
        open={this.props.opened}
        scroll="paper"
        aria-labelledby="scroll-dialog-title"
      >
        <DialogTitle id="scroll-dialog-title">{this.props.title}</DialogTitle>
        <DialogContent>
        <center>
            <CircularProgress
              variant="indeterminate"
              size={this.props.size}
              thickness={7}
            />
          </center>
        </DialogContent>
      </Dialog>
    );
  }
}

DialogLoader.defaultProps = {
  opened: true,
  size: 60,
  title: null
}
