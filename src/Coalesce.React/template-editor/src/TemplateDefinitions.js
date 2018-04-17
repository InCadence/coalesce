import React from 'react';
import {List, ListItem} from 'material-ui/List';
import ActionDelete from 'material-ui/svg-icons/action/delete';
import EditorModeEdit from 'material-ui/svg-icons/editor/mode-edit';
import { DialogEditDefinition } from './DialogEditDefinition';

export class Definitions extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      selected: null,
    };
  }

  handleDeleteDefinition(key) {
    this.props.onDelete(key);
  }

  render() {

    return (
      <div>
        <List>
          {this.props.data.map((item) => {return (
              <ListItem
                key={item.key}
                primaryText={item.name}
                secondaryText={item.dataType}
                leftIcon={
                  <EditorModeEdit
                    color="#3d3d3c"
                    hoverColor="#FF9900"
                    onClick={() => {this.setState({selected: item})}}
                  />
                }
                rightIcon={
                  <ActionDelete
                    color="#3d3d3c"
                    hoverColor="#FF9900"
                    onClick={() => {this.handleDeleteDefinition(item.key)}}
                  />
                }
              />
          )})}
        </List>
        {this.state.selected != null &&
          <DialogEditDefinition
            opened={true}
            definition={this.state.selected}
            onClose={() => {this.setState({selected: null})}}
            onDelete={this.props.onDelete}
          />
        }
      </div>
    );
  }
}
