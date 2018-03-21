import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import TextField from 'material-ui/TextField';

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
        label="Close"
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
      </Dialog>
    )
  }

}
