import React from 'react';
import Checkbox from 'material-ui/Checkbox';
import TextField from 'material-ui/TextField';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import DatePicker from 'material-ui/DatePicker';
import TimePicker from 'material-ui/TimePicker';
import { IconButton } from 'common-components/lib/components/IconButton.js'

import { Row, Col } from 'react-bootstrap';

var parse = require('wellknown');

export class FieldInput extends React.Component {

  constructor(props) {
    super(props);

    this.state = {field: props.field};

    this.handleOnChange = this.handleOnChange.bind(this);
  }

  handleOnChange(attr, value) {
    const {field} = this.state;
    field[attr] = value;
    this.setState(field);
  }

  render() {

    const {field} = this.state;

    switch (field.dataType) {
      case 'ENUMERATION_LIST_TYPE':
        return (
          <SelectField
            floatingLabelText={this.props.showLabels ? field.name : null}
            fullWidth={true}
            multiple={true}
            value={field.value}
            onChange={(event, index, values) => {this.handleOnChange('value', values)}}
          >
            <MenuItem value={0} primaryText="Option 1" />
            <MenuItem value={1} primaryText="Option 2" />
            <MenuItem value={2} primaryText="Option 3" />
          </SelectField>
        )
      case 'ENUMERATION_TYPE':
        return (
          <SelectField
            floatingLabelText={this.props.showLabels ? field.name : null}
            fullWidth={true}
            value={field.value}
            onChange={(event, value) => {this.handleOnChange('value', value)}}
          >
            <MenuItem value={0} primaryText="Option 1" />
            <MenuItem value={1} primaryText="Option 2" />
            <MenuItem value={2} primaryText="Option 3" />
          </SelectField>
        )
      case 'URI_TYPE':
      case 'STRING_TYPE':
        return (
          <TextField
            id={field.key}
            fullWidth={true}
            floatingLabelText={this.props.showLabels ? field.name : null}
            value={field.value}
            onChange={(event, value) => {this.handleOnChange('value', value)}}
          />
        );
      case 'FLOAT_TYPE':
      case 'DOUBLE_TYPE':
      case 'LONG_TYPE':
        return (
          <TextField
            id={field.key}
            type='number'
            step='0.01'
            fullWidth={true}
            floatingLabelText={this.props.showLabels ? field.name : null}
            value={field.value}
            onChange={(event, value) => {this.handleOnChange('value', value)}}
          />
        );
      case 'INTEGER_TYPE':
        return (
          <TextField
            id={field.key}
            type='number'
            fullWidth={true}
            floatingLabelText={this.props.showLabels ? field.name : null}
            value={field.value}
            onChange={(event, value) => {this.handleOnChange('value', value)}}
          />
        );
      case 'BOOLEAN_TYPE':
        return (
          <Checkbox
            id={field.key}
            label={this.props.showLabels ? field.name : null}
            checked={field.value}
            onCheck={(event, checked) => {this.handleOnChange('value', checked)}}
          />
        );
      case 'DATE_TIME_TYPE':

        var dateTime

        if (field.value == null || field.value === "") {
          dateTime = null;
        } else {
          dateTime = new Date(field.value);
        }

        return (
          <Row>
            <Col xs={6}>
              <DatePicker
                id={field.key + 'date'}
                floatingLabelText={this.props.showLabels ? field.name + " Date" : null}
                mode="landscape"
                value={dateTime}
                onChange={(tmp, date) => {
                  var newDateTime = dateTime != null ? dateTime : new Date();
                  newDateTime.setFullYear(date.getFullYear());
                  newDateTime.setMonth(date.getMonth());
                  newDateTime.setDate(date.getDate());
                  this.handleOnChange('value', newDateTime.toISOString());
                }}
              />
            </Col>
            <Col xs={6}>
              <TimePicker
                id={field.key + 'time'}
                floatingLabelText={this.props.showLabels ? "Time" : null}
                format="24hr"
                value={dateTime}
                onChange={(tmp, date) => {
                  var newDateTime = dateTime != null ? dateTime : new Date();
                  newDateTime.setHours(date.getHours());
                  newDateTime.setMinutes(date.getMinutes());
                  this.handleOnChange('value', newDateTime.toISOString());
                }}
              />
            </Col>
        </Row>
        );
      case 'BINARY_TYPE':
      case 'FILE_TYPE':
        return (
          <div>
            <IconButton id={field.key} icon="/images/svg/load.svg" title={"Download " + field.name} onClick={null} /> {this.props.showLabel ? <label>Download {field.name}</label> : null}
          </div>
        );
      case 'GEOCOORDINATE_TYPE':

      var geo;

        if (field.value == null || field.value === "") {
          geo = {coordinates: [0, 0, 0]};
        } else {
          geo = parse(field.value);
        }

        return (
            <Row>
              <Col xs={3}>
                <TextField
                  id={field.key + 'x'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? field.name + " Latitude" : null}
                  fullWidth={false}
                  value={geo.coordinates[0]}
                  onChange={(event, value) => {this.handleOnChange('value', `POINT(${value} ${geo.coordinates[1]} ${geo.coordinates[2]})`)}}
                />
              </Col>
              <Col xs={3}>
                <TextField
                  id={field.key + 'y'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? "Longitude" : null}
                  fullWidth={false}
                  value={geo.coordinates[1]}
                  onChange={(event, value) => {this.handleOnChange('value', `POINT(${geo.coordinates[0]} ${value} ${geo.coordinates[2]})`)}}
                />
              </Col>
              <Col xs={3}>
                <TextField
                  id={field.key + 'z'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? "Attitude" : null}
                  fullWidth={false}
                  value={geo.coordinates[2]}
                  onChange={(event, value) => {this.handleOnChange('value', `POINT(${geo.coordinates[0]} ${geo.coordinates[1]} ${value})`)}}
                />
              </Col>
          </Row>
        );
      case 'CIRCLE_TYPE':

        var center;

        if (field.value == null || field.value === "") {
          center = {coordinates: [0, 0, 0]};
        } else {
          center = parse(field.value);
        }

        return (
          <Row>
            <Col xs={3}>
              <TextField
                id={field.key + 'x'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? field.name + " Latitude" : null}
                fullWidth={false}
                value={center.coordinates[0]}
                onChange={(event, value) => {this.handleOnChange('value', `POINT(${value} ${center.coordinates[1]} ${center.coordinates[2]})`)}}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + 'y'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? "Longitude" : null}
                fullWidth={false}
                value={center.coordinates[1]}
                onChange={(event, value) => {this.handleOnChange('value', `POINT(${center.coordinates[0]} ${value} ${center.coordinates[2]})`)}}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + 'z'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? "Attitude" : null}
                fullWidth={false}
                value={center.coordinates[2]}
                onChange={(event, value) => {this.handleOnChange('value', `POINT(${center.coordinates[0]} ${center.coordinates[1]} ${value})`)}}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + 'radius'}
                type='number'
                step='0.01'
                value={field.radius}
                floatingLabelText={this.props.showLabels ? "Radius" : null}
                fullWidth={false}
                onChange={(event, value) => {this.handleOnChange('radius', value)}}
              />
            </Col>
          </Row>
      );
      case 'GUID_TYPE':
        return (
          <TextField
            id={field.key}
            fullWidth={true}
            floatingLabelText={this.props.showLabels ? field.name : null}
            //inputProps={{ pattern: "[a-z]" }}
            value={field.value}
            onChange={(event, value) => {this.handleOnChange('value', value)}}
          />
        );
      default:
        return (
          <TextField
            id={field.key}
            fullWidth={true}
            floatingLabelText={this.props.showLabels ? field.name : null}
            disabled
            value={field.value}
            onChange={(event, value) => {this.handleOnChange('value', value)}}
          />
        );
    }

  }
}

FieldInput.defaultProps = {
  showLabels: true
}
