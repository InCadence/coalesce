import React from 'react'

import TextField from 'material-ui/TextField';
import AvPlaylistAdd from 'material-ui/svg-icons/av/playlist-add';
import EditorModeEditAdd from 'material-ui/svg-icons/editor/mode-edit';
import ContentRemoveCircle from 'material-ui/svg-icons/content/remove-circle';
import ContentDeleteSweep from 'material-ui/svg-icons/content/delete-sweep';
import {blue500, red500, greenA200} from 'material-ui/styles/colors';
import { Row, Col } from 'react-bootstrap'

const iconStyles = {
  marginRight: 2,
  width: 24
};

export class TabTextField extends React.PureComponent {

  render() {

    return (
      <div style={{'display': 'table'}}>
        <div style={{'display': 'table-cell'}}>
          <TextField
            fullWidth={true}
            underlineShow={false}
            inputStyle={{'color': 'rgb(255, 255, 255)', 'text-align': 'center'}}
            value={this.props.item.name}
            onChange={(event, value) => {this.props.onNameChange(this.props.item.key, "name", value);}}
          />
        </div>
        <div style={{'display': 'table-cell', 'width': '24px'}}>
          {this.props.onDelete != null &&
          <Row style={{height: '24px'}}>
            <ContentDeleteSweep
              style={iconStyles}
              color={red500}
              hoverColor={greenA200}
              onClick={() => this.props.onDelete(this.props.item.key)}
            />
          </Row>
          }
          {this.props.onAdd != null &&
          <Row style={{height: '24px'}}>
            <AvPlaylistAdd
              style={iconStyles}
              color={red500}
              hoverColor={greenA200}
              onClick={() => this.props.onAdd(this.props.item.key)}
            />
          </Row>
          }
        </div>
      </div>
    )
  }

}
