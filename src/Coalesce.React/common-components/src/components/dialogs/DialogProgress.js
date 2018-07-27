import React from 'react';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';
import CircularProgress from '@material-ui/core/CircularProgress';

/**
 * Dialog to display progress.
 */
export class DialogProgress extends React.PureComponent {

  render() {

    const { total, completed } = this.props;

    var percent = (1 - (total - completed) / total)*100;

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
              variant="determinate"
              value={percent}
              size={this.props.size}
              thickness={7}
            />
          </center>
        </DialogContent>
        <DialogActions>
          <Button
            color="primary"
            onClick={this.props.onCancel}
          >
            Cancel
          </Button>
        </DialogActions>
      </Dialog>
    );
  }
}

DialogProgress.defaultProps = {
  opened: true,
  size: 60,
  title: null,
  total: 0,
  completed: 0,
  onCancel: undefined
}
