import React from "react";
import {withTheme} from "@material-ui/core/styles";

import Toolbar from "@material-ui/core/Toolbar";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";

import MenuIcon from "@material-ui/icons/Menu";
import ActionInput from "@material-ui/icons/Input";
import AvPlaylistAdd from "@material-ui/icons/PlaylistAdd";
import ContentDeleteSweep from "@material-ui/icons/DeleteSweep";

import {DialogPrompt} from "coalesce-components/lib/components/dialogs";

class TabTextField extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      item: props.item,
      prompt: props.item.name === "",
      anchorEl: null
    };

    this.handleRename = this.handleRename.bind(this);
    this.handleRenamePrompt = this.handleRenamePrompt.bind(this);
    this.handleAdd = this.handleAdd.bind(this);
    this.handleDelete = this.handleDelete.bind(this);
  }

  handleRenamePrompt() {
    this.setState(() => {
      return {prompt: true, anchorEl: null};
    });
  }

  handleRename(value) {
    const {item} = this.state;

    item.name = value;

    this.setState(() => {
      return {item: item, prompt: false};
    });
  }

  handleAdd() {
    this.props.onAdd(this.props.item.key);
    this.handleClose();
  }

  handleDelete() {
    this.props.onDelete(this.props.item.key);
    this.handleClose();
  }

  handleClick = event => {
    this.setState({anchorEl: event.currentTarget});
  };

  handleClose = () => {
    this.setState({anchorEl: null});
  };

  render() {
    const {item, anchorEl} = this.state;
    const open = Boolean(anchorEl);

    return (
      <div>
        <Toolbar variant="dense">
          <Typography variant="h6">{item.name}</Typography>
          <IconButton
            color="inherit"
            aria-label="More"
            aria-owns={open ? `${item.key}-menu` : undefined}
            aria-haspopup="true"
            onClick={this.handleClick}
          >
            <MenuIcon />
          </IconButton>
        </Toolbar>
        <Menu
          id={`${item.key}-menu`}
          anchorEl={anchorEl}
          open={open}
          onClose={this.handleClose}
        >
          <MenuItem onClick={this.handleRenamePrompt}>
            <ListItemIcon>
              <ActionInput />
            </ListItemIcon>
            <Typography>Rename</Typography>
          </MenuItem>
          <MenuItem onClick={this.handleAdd}>
            <ListItemIcon>
              <AvPlaylistAdd />
            </ListItemIcon>
            <Typography>Add</Typography>
          </MenuItem>
          <MenuItem onClick={this.handleDelete}>
            <ListItemIcon>
              <ContentDeleteSweep />
            </ListItemIcon>
            <Typography>Delete</Typography>
          </MenuItem>
        </Menu>
        <DialogPrompt
          title="Enter Name"
          value={item.name}
          opened={this.state.prompt}
          onClose={() => {
            this.setState({prompt: false});
          }}
          onSubmit={this.handleRename}
        />
      </div>
    );
  }
}

export default withTheme(TabTextField);
