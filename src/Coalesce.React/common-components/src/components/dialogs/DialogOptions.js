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
import Checkbox from '@material-ui/core/Checkbox';

/**
 * Dialog to display selections.
 */
export class DialogOptions extends React.Component {

  constructor(props) {
    super(props);

    var items = props.options;

    if (items && props.sorted) {
      items = items.sort(function(a, b){
          var x = a.name.toLowerCase();
          var y = b.name.toLowerCase();
          if (x < y) {return -1;}
          if (x > y) {return 1;}
          return 0;
      })
    }

    this.state = {
      items: items
    }

    this.handledSelect = this.handleSelect.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentDidUpdate(prevProps) {
    // Typical usage (don't forget to compare props):
    if (this.props.options !== prevProps.options) {

      var items = this.props.options;

      if (items && this.props.sorted) {
        items = items.sort(function(a, b){
            var x = a.name.toLowerCase();
            var y = b.name.toLowerCase();
            if (x < y) {return -1;}
            if (x > y) {return 1;}
            return 0;
        })
      }

      this.setState(() => {return {items: items}})
    }
  }

  handleSelect(key) {
      const { items } = this.state;
      const that = this;

      items.forEach((item) => {
        if (item.key === key) {
          item.selected = !item.selected;

        }
      });

      that.setState(() => {return {
        items: items
      }});
  }

  handleSubmit() {

    const { items } = this.state;

    this.props.onClick(items.filter((item) => item.selected).map((item) => item.key));

  }

  render() {

    const { items } = this.state;

    return (
      <Dialog
        open={this.props.open}
        scroll="paper"
        onClose={this.props.onClose}
        aria-labelledby="scroll-dialog-title"
        PaperProps={{style: {width: '100%'}}}
        maxWidth={this.props.maxWidth}
      >
        <DialogTitle id="scroll-dialog-title">{this.props.title}</DialogTitle>
        <DialogContent>
          <List dense>
            {items && items.map((item) => {return (
              <ListItemExpandable
                key={item.key}
                selected={item.selected}
                primary={item.name}
                details={item.description}
                onClick={!this.props.multi ? item.onClick ? item.onClick : () => this.props.onClick(item.key) : () => this.handleSelect(item.key)}
              />
            )})}
            {this.props.children}
          </List>
        </DialogContent>
        <DialogActions>
          {this.props.onNew &&
            <Button
              color="secondary"
              onClick={this.props.onNew}
            >
              {this.props.onNewTitle}
            </Button>
          }
          <Button
            color={this.props.multi ? "secondary" : "primary"}
            onClick={this.props.onClose}
          >
            Cancel
          </Button>
          { this.props.multi &&
            <Button
              color="primary"
              onClick={this.handleSubmit}
            >
              OK
            </Button>
          }
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
