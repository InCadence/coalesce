import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import {List, ListItem} from 'material-ui/List';
import TextField from 'material-ui/TextField';
import Checkbox from 'material-ui/Checkbox';
import { Row, Col } from 'react-bootstrap';
import ActionDelete from 'material-ui/svg-icons/action/delete';
import EditorModeEdit from 'material-ui/svg-icons/editor/mode-edit';
import Subheader from 'material-ui/Subheader';

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

    const actions = [
      <FlatButton
        label="OK"
        primary={true}
        onClick={this.handleClose}
      />
    ];

    return (
      <Dialog
        title="Edit Field Definition"
        actions={actions}
        modal={false}
        open={this.props.opened}
        onRequestClose={this.handleClose}
      >
        <TextField
          fullWidth={true}
          floatingLabelText="Name"
          value={definition.name}
          onChange={(event, value) => {this.handleChange("name", value);}}
        />
        <TextField
          fullWidth={true}
          floatingLabelText="Label"
          value={definition.label}
          onChange={(event, value) => {this.handleChange("label", value);}}
        />
        <SelectField
          fullWidth={true}
          floatingLabelText="Type"
          value={dataTypes.indexOf(definition.dataType)}
          onChange={(event, key, payload) => {this.handleChange("dataType", dataTypes[payload]);}}
        >
          {dataTypes.map((type) => {
            return (<MenuItem key={type} value={dataTypes.indexOf(type)} primaryText={type} />);
          })}
        </SelectField>
        <TextField
          fullWidth={true}
          floatingLabelText="Default Value"
          value={definition.defaultValue}
          onChange={(event, value) => {this.handleChange("defaultValue", value);}}
        />
        <Row>
          <Col xs={4}>
            <Checkbox
              label="Flatten"
              checked={definition.flatten}
              onCheck={(event, checked) => {
                this.handleChange("flatten", checked);
              }}
            />
          </Col>
          <Col xs={4}>
            <Checkbox
              label="Index"
              checked={!definition.noIndex}
              onCheck={(event, checked) => {
                this.handleChange("noIndex", !checked);
              }}
            />
          </Col>
          <Col xs={4}>
            <Checkbox
              label="Disable History"
              checked={definition.disableHistory}
              onCheck={(event, checked) => {
                this.handleChange("disableHistory", checked);
              }}
            />
          </Col>
        </Row>
        <List>
          <Subheader>Constraints</Subheader>
          {definition.constraints.map((item) => {return (
              <ListItem
                key={item.key}
                primaryText={item.name}
                secondaryText={item.type}
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
      </Dialog>
    )
  }

}
