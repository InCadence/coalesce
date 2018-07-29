import React from 'react'
import { withTheme } from '@material-ui/core/styles';

import TextField from '@material-ui/core/TextField'
import AvPlaylistAdd from '@material-ui/icons/PlaylistAdd'
import ContentDeleteSweep from '@material-ui/icons/DeleteSweep'
import { Row } from 'react-bootstrap'
import Tooltip from '@material-ui/core/Tooltip';



class TabTextField extends React.Component {

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

    const iconStyles = {
      color: this.props.palette.contrastText
    };

    return (
      <div style={{'display': 'table', width: '100%'}}>
        <div style={{'display': 'table-cell'}}>
          <TextField
            id={item.key}
            fullWidth
            inputProps={{style: {color: this.props.palette.contrastText}}}
            InputLabelProps={{style: {color: this.props.palette.contrastText}}}
            label={this.props.label}
            value={item.name}
            onChange={(event, value) => {this.handleNameChange(event.target.value);}}
          />
        </div>
        <div style={{'display': 'table-cell', 'width': '24px'}}>
          {this.props.onDelete != null &&
          <Row style={{height: '24px', marginRight: '0px', marginLeft: '0px'}}>
            <Tooltip title="Delete" placement="bottom">
              <ContentDeleteSweep
                style={{iconStyles}}
                onClick={() => this.props.onDelete(this.props.item.key)}
              />
            </Tooltip>
          </Row>
          }
          {this.props.onAdd != null &&
          <Row style={{height: '24px', marginRight: '0px', marginLeft: '0px'}}>
            <Tooltip title="Add" placement="bottom">
              <AvPlaylistAdd
                style={iconStyles}
                onClick={() => this.props.onAdd(this.props.item.key)}
              />
            </Tooltip>
          </Row>
          }
        </div>
      </div>
    )
  }

}

export default withTheme()(TabTextField);
