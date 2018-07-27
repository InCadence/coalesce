import React from 'react';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import { HashLoader } from 'react-spinners';

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
            <HashLoader
              color={'#FF9900'}
              size={this.props.size}
              loading={this.props.opened}
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
