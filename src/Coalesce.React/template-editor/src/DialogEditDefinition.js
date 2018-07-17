import React from 'react';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions'
import DialogTitle from '@material-ui/core/DialogTitle'
import DialogContent from '@material-ui/core/DialogContent';
import FormControlLabel from '@material-ui/core/FormControlLabel'
import Button from '@material-ui/core/Button';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import TextField from '@material-ui/core/TextField';
import Checkbox from '@material-ui/core/Checkbox';
import { Row, Col } from 'react-bootstrap';
import ActionDelete from '@material-ui/icons/Delete';
import EditorModeEdit from '@material-ui/icons/ModeEdit';
import ListSubheader from '@material-ui/core/ListSubheader';

// TODO Move this to a common file

const dataTypes = [
  "STRING_TYPE",
  "STRING_LIST_TYPE",
  "DATE_TIME_TYPE",
  "URI_TYPE",
  "BINARY_TYPE",
  "BOOLEAN_TYPE",
  "BOOLEAN_LIST_TYPE",
  "INTEGER_TYPE",
  "INTEGER_LIST_TYPE",
  "GUID_TYPE",
  "GUID_LIST_TYPE",
  "GEOCOORDINATE_TYPE",
  "GEOCOORDINATE_LIST_TYPE",
  "LINE_STRING_TYPE",
  "POLYGON_TYPE",
  "CIRCLE_TYPE",
  "FILE_TYPE",
  "DOUBLE_TYPE",
  "DOUBLE_LIST_TYPE",
  "FLOAT_TYPE",
  "FLOAT_LIST_TYPE",
  "LONG_TYPE",
  "LONG_LIST_TYPE",
  "ENUMERATION_TYPE",
  "ENUMERATION_LIST_TYPE"
];

export class DialogEditDefinition extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      definition: this.props.definition
    };

    this.handleClose = this.handleClose.bind(this);
    this.handleChange = this.handleChange.bind(this);

  }

  handleClose() {
    this.props.onClose();
  };

  handleChange(attr, value) {
    const { definition } = this.state;

    definition[attr] = value;

    this.setState(definition);
  }

  render() {

    const { definition } = this.state;

    return (
      <Dialog
        title="Edit Field Definition"
        open={this.props.opened}
        onClose={this.handleClose}
      >
        <DialogTitle id="alert-dialog-title">Edit Field</DialogTitle>
        <DialogContent>
          <TextField
            autofocus
            fullWidth
            label="Name"
            value={definition.name}
            onChange={(event) => {this.handleChange("name", event.target.value);}}
          />
          <TextField
            fullWidth
            label="Label"
            value={definition.label}
            onChange={(event) => {this.handleChange("label", event.target.value);}}
          />
          <Select
            fullWidth
            label="Type"
            value={dataTypes.indexOf(definition.dataType)}
            onChange={(event) => {this.handleChange("dataType", dataTypes[event.target.value]);}}
          >
            {dataTypes.map((type) => {
              return (
                <MenuItem
                  key={type}
                  value={dataTypes.indexOf(type)}
                >
                  {type}
                </MenuItem>
              );
            })}
          </Select>
          <TextField
            fullWidth
            label="Default Value"
            value={definition.defaultValue}
            onChange={(event) => {this.handleChange("defaultValue", event.target.value);}}
          />
          <Row>
            <Col xs={4}>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={definition.flatten}
                    onChange={(event, checked) => {
                      this.handleChange("flatten", checked);
                    }}
                  />
                }
                label="Flatten"
              />
            </Col>
            <Col xs={4}>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={!definition.noIndex}
                    onChange={(event, checked) => {
                      this.handleChange("noIndex", !checked);
                    }}
                  />
                }
                label="Index"
              />
            </Col>
            <Col xs={4}>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={definition.disableHistory}
                    onChange={(event, checked) => {
                      this.handleChange("disableHistory", checked);
                    }}
                  />
                }
                label="Disable History"
              />
            </Col>
          </Row>
          <List>
            <ListSubheader>Constraints</ListSubheader>
            {definition.constraints.map((item) => {return (
                <ListItem key={item.key} >
                  <EditorModeEdit
                    color="primary"
                    onClick={() => {this.setState({selected: item})}}
                  />
                  <ListItemText primary={item.name} secondary={item.type} />
                  <ActionDelete
                    color="primary"
                    onClick={() => {this.handleDeleteDefinition(item.key)}}
                  />
                </ListItem>
            )})}
          </List>
        </DialogContent>
        <DialogActions>
          <Button
            color="primary"
            onClick={this.handleClose}
          >
            OK
          </Button>
        </DialogActions>
      </Dialog>
    )
  }

}
