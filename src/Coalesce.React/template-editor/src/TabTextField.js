import React from 'react'

import TextField from '@material-ui/core/TextField'
import AvPlaylistAdd from '@material-ui/icons/PlaylistAdd'
import ContentDeleteSweep from '@material-ui/icons/DeleteSweep'
import { Row } from 'react-bootstrap'
import IconButton from '@material-ui/core/IconButton'

const iconStyles = {
  marginRight: 2,
  padding: "0px",
  width: '24px',
  height: '24px'
};

export class TabTextField extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      item: props.item
    }

    this.handleNameChange = this.handleNameChange.bind(this);
  }

  handleNameChange(value) {

    const { item } = this.state;

    item.name = value;

    this.setState(() => {return {item: item}});

  }

  render() {

    const { item } = this.state;

    return (
      <div style={{'display': 'table'}}>
        <div style={{'display': 'table-cell'}}>
          <TextField
            id={item.key}
            fullWidth={true}
            label="name"
            value={item.name}
            onChange={(event, value) => {this.handleNameChange(event.target.value);}}
          />
        </div>
        <div style={{'display': 'table-cell', 'width': '24px'}}>
          {this.props.onDelete != null &&
          <Row style={{height: '24px'}}>
              <ContentDeleteSweep
                style={iconStyles}
                color="primary"
                onClick={() => this.props.onDelete(this.props.item.key)}
              />
          </Row>
          }
          {this.props.onAdd != null &&
          <Row style={{height: '24px'}}>
              <AvPlaylistAdd
                style={iconStyles}
                color="primary"
                onClick={() => this.props.onAdd(this.props.item.key)}
              />
          </Row>
          }
        </div>
      </div>
    )
  }

}
