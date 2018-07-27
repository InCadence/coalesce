import React from 'react';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemExpandable from '../ListItemExpandable';

/**
 * Dialog to display selections.
 */
export class DialogOptions extends React.Component {

  render() {

    return (
      <Dialog
        open={this.props.open}
        scroll="paper"
        onClose={this.props.onClose}
        aria-labelledby="scroll-dialog-title"
        PaperProps={{style: {width: '100%'}}}
      >
        <DialogTitle id="scroll-dialog-title">{this.props.title}</DialogTitle>
        <DialogContent>
          <List dense>
            {this.props.options && this.props.options.map((item) => {return (
              <ListItemExpandable
                key={item.key}
                primary={item.name}
                //secondary={}
                details={item.description}
                onClick={item.onClick ? item.onClick : () => this.props.onClick(item.key)}
              />
            )})}
            {this.props.children}
          </List>
        </DialogContent>
        <DialogActions>
          <Button
            color="primary"
            onClick={this.props.onClose}
          >
            Cancel
          </Button>
        </DialogActions>
      </Dialog>
    )
  }

}

DialogOptions.defaultProps = {
  open: true,
  title: null,
  options: [],
  onClose: undefined
}

export default DialogOptions;
