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
import { withTheme } from '@material-ui/core/styles'

export class DialogOptions extends React.Component {

  render() {

    return (
      <Dialog
        open={this.props.open}
        scroll="paper"
        onClose={this.props.onClose}
        aria-labelledby="scroll-dialog-title"
        style={{width: '100%'}}
      >
        <DialogTitle id="scroll-dialog-title">{this.props.title}</DialogTitle>
        <DialogContent>
          <List fullWidth>
            {this.props.options && this.props.options.map((item) => {return (
              <ListItem
                key={item.key}
                id={item.key}
                fullWidth
                button
                onClick={item.onClick ? item.onClick : () => this.props.onClick(item.key)}
                /*
                nestedItems={item.description ? [
                  <ListItem
                    key={item.key + "_description"}
                    primary={item.description}
                    onClick={(event) => this.props.onClick(item)}
                  />
                  ] : undefined}
                  */
              >
                <ListItemText primary={item.name} />
              </ListItem>
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

export default DialogOptions;
