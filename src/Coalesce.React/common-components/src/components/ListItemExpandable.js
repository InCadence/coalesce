import React from 'react'
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import IconButton from '@material-ui/core/IconButton';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import Collapse from '@material-ui/core/Collapse';

export class ListItemExpandable extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      expanded: this.props.expanded
    }

    this.handleToggleExpand = this.handleToggleExpand.bind(this);
  }

  handleToggleExpand() {
    console.log('GOT HERE');
    this.setState(state => ({expanded: !state.expanded}));
  }

  render() {
    return (
      <div>
        <ListItem
          dense
          disableRipple
          key={this.key}
          button
        >
          {this.props.icon}
          <ListItemText
            primary={this.props.primary}
            secondary={this.props.secondary}
            onClick={this.props.onClick}
          />

          {this.props.details &&
            <IconButton
              style={this.props.secondary ? {} : {width: '24px', height: '24px'}} 
              onClick={this.handleToggleExpand}>
              {this.state.expanded ? <ExpandLess /> : <ExpandMore />}
            </IconButton>
          }
        </ListItem>
        {this.props.details &&
          <Collapse key={this.key + "_details"} in={this.state.expanded} timeout="auto" unmountOnExit>
            <List dense component="div" disablePadding>
              <ListItem button disableRipple dense onClick={this.props.onClick}>
                <ListItemText inset primary={this.props.details} />
              </ListItem>
            </List>
          </Collapse>
        }
      </div>
    )
  }

}

ListItemExpandable.defaultProps = {
  expanded: false
}

export default ListItemExpandable;
