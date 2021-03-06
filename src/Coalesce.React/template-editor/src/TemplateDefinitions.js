import React from "react";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import IconButton from "@material-ui/core/IconButton";
import ActionDelete from "@material-ui/icons/Delete";
import Create from "@material-ui/icons/Create";
import {DialogEditDefinition} from "./DialogEditDefinition";
import uuid from "uuid";

export class Definitions extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      selected: null
    };
  }

  handleDeleteDefinition(key) {
    this.props.onDelete(key);
  }

  render() {
    return (
      <div>
        <List>
          {this.props.data.map(item => {
            if (!item.key) {
              item.key = uuid.v4();
            }

            return (
              <ListItem key={item.key}>
                <IconButton
                  onClick={() => {
                    this.setState({selected: item});
                  }}
                >
                  <Create color="primary" />
                </IconButton>
                <ListItemText primary={item.name} secondary={item.dataType} />
                <IconButton
                  onClick={() => {
                    this.handleDeleteDefinition(item.key);
                  }}
                >
                  <ActionDelete color="primary" />
                </IconButton>
              </ListItem>
            );
          })}
        </List>
        {this.state.selected != null && (
          <DialogEditDefinition
            opened={true}
            definition={this.state.selected}
            onClose={() => {
              this.setState({selected: null});
            }}
            onDelete={this.props.onDelete}
          />
        )}
      </div>
    );
  }
}
