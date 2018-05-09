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

    var style;

    if (this.props.showLabels) {
      style = {
        root: {
        },
        none: {
        }
      }
    } else {
      style = {
        root: {
          'height': '20px',
          'lineHeight': '20px',
          'top': '0px',
          'padding': '0px',
          'display': '',
          'backgroundColor': '#FFFFFF'
        },
        none: {
          'display': 'none'
        }

      }
    }

    this.state = {
      field: props.field,
      style: style
    };


    this.handleOnChange = this.handleOnChange.bind(this);
  }

  handleOnChange(attr, value) {
    const {field} = this.state;
    field[attr] = value;

    console.log(`${attr}=${value}`);
    this.setState(field);
  }

  render() {

    const {field, style} = this.state;

    var type = (this.props.dataType != null) ? this.props.dataType : field.dataType;
    var attr = (this.props.attr != null) ? this.props.attr : 'value';
    var label = this.props.showLabels ? (field.label != null && field.label.length > 0 ? field.label : field.name) : null;

    switch (type) {
      case 'ENUMERATION_LIST_TYPE':
        return (
          <SelectField
            id={field.key}
            fullWidth={true}
            floatingLabelText={label}
            underlineShow={this.props.showLabels}
            multiple={true}
            value={field[attr].toUpperCase()}
            style={style.root}
            labelStyle={style.root}
            iconStyle={style.none}
            hintStyle={style.none}
            floatingLabelStyle={style.none}
            errorStyle={style.none}
            onChange={(event, index, values) => {this.handleOnChange(attr, values)}}
          >
            {this.props.options && this.props.options.map((item) => {
              return (
                <MenuItem key={item.enum} value={item.enum} primaryText={item.label} />
              )
            })}
          </SelectField>
        )
      case 'ENUMERATION_TYPE':
        return (
          <SelectField
            id={field.key}
            fullWidth={true}
            floatingLabelText={label}
            underlineShow={this.props.showLabels}
            style={style.root}
            labelStyle={style.root}
            iconStyle={style.none}
            hintStyle={style.none}
            floatingLabelStyle={style.none}
            errorStyle={style.none}
            value={field[attr].toUpperCase()}
            onChange={(event, value) => {this.handleOnChange(attr, this.props.options[value].enum)}}
          >
            {this.props.options && this.props.options.map((item) => {
              return (
                <MenuItem key={item.enum} value={item.enum} primaryText={item.label} />
              )
            })}
          </SelectField>
        )
      case 'URI_TYPE':
      case 'STRING_TYPE':
        return (
          <TextField
            id={field.key}
            fullWidth={true}
            floatingLabelText={label}
            underlineShow={this.props.showLabels}
            style={style.root}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
          />
        );
      case 'BOOLEAN_LIST_TYPE':
      case 'GUID_LIST_TYPE':
      case 'FLOAT_LIST_TYPE':
      case 'DOUBLE_LIST_TYPE':
      case 'LONG_LIST_TYPE':
      case 'INTEGER_LIST_TYPE':
      case 'STRING_LIST_TYPE':
        return (
          <TextField
              id={field.key}
              fullWidth={true}
              floatingLabelText={this.props.showLabels ? label + " (CSV)" : label}
              underlineShow={this.props.showLabels}
              style={style.root}
              value={field[attr]}
              defaultValue={field.defaultValue}
              onChange={(event, value) => {this.handleOnChange(attr, value)}}
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
            floatingLabelText={label}
            underlineShow={this.props.showLabels}
            style={style.root}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
          />
        );
      case 'INTEGER_TYPE':
        return (
          <TextField
            id={field.key}
            type='number'
            fullWidth={true}
            floatingLabelText={label}
            underlineShow={this.props.showLabels}
            style={style.root}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
          />
        );
      case 'BOOLEAN_TYPE':
        return (
          <Checkbox
            id={field.key}
            label={label}
            style={style.root}
            checked={field[attr]}
            defaultChecked={field.defaultValue}
            onCheck={(event, checked) => {this.handleOnChange(attr, checked)}}
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
                floatingLabelText={this.props.showLabels ? label + " Date" : null}
                underlineShow={this.props.showLabels}
                style={style.root}
                mode="landscape"
                value={dateTime}
                onChange={(tmp, date) => {
                  var newDateTime = dateTime != null ? dateTime : new Date();
                  newDateTime.setFullYear(date.getFullYear());
                  newDateTime.setMonth(date.getMonth());
                  newDateTime.setDate(date.getDate());
                  this.handleOnChange(attr, newDateTime.toISOString());
                }}
              />
            </Col>
            <Col xs={6}>
              <TimePicker
                id={field.key + 'time'}
                floatingLabelText={this.props.showLabels ? "Time" : null}
                underlineShow={this.props.showLabels}
                style={style.root}
                value={dateTime}
                format="24hr"
                onChange={(tmp, date) => {
                  var newDateTime = dateTime != null ? dateTime : new Date();
                  newDateTime.setHours(date.getHours());
                  newDateTime.setMinutes(date.getMinutes());
                  this.handleOnChange(attr, newDateTime.toISOString());
                }}
              />
            </Col>
        </Row>
        );
      case 'BINARY_TYPE':
      case 'FILE_TYPE':
        return (
          <div>
            <IconButton
              id={field.key}
              icon="/images/svg/load.svg"
              title={"Download " + label}
              onClick={null}
            />
            {this.props.showLabel ? <label>Download {label}</label> : null}
          </div>
        );

      case 'LINE_STRING_TYPE':
          return (
            <TextField
              id={field.key}
              fullWidth={true}
              floatingLabelText={label + " - LINESTRING (x1 y1 z1, x2 y2 z2, ...)"}
              underlineShow={this.props.showLabels}
              style={style.root}
              value={field[attr]}
              defaultValue={field.defaultValue}
              onChange={(event, value) => {this.handleOnChange(attr, value)}}
            />
          );
      case 'POLYGON_TYPE':
          return (
            <TextField
              id={field.key}
              fullWidth={true}
              floatingLabelText={label + " - POLYGON ((x1 y1 z1, x2 y2 z2, ...))"}
              underlineShow={this.props.showLabels}
              style={style.root}
              value={field[attr]}
              defaultValue={field.defaultValue}
              onChange={(event, value) => {this.handleOnChange(attr, value)}}
            />
          );
      case 'GEOCOORDINATE_LIST_TYPE':
          return (
            <TextField
              id={field.key}
              fullWidth={true}
              floatingLabelText={label + " - MULTIPOINT (x1 y1 z1, x2 y2 z2, ...)"}
              underlineShow={this.props.showLabels}
              style={style.root}
              value={field[attr]}
              defaultValue={field.defaultValue}
              onChange={(event, value) => {this.handleOnChange(attr, value)}}
            />
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
              <Col xs={4}>
                <TextField
                  id={field.key + 'x'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? label + " Longitude" : null}
                  underlineShow={this.props.showLabels}
                  style={style.root}
                  fullWidth={true}
                  value={geo.coordinates[0]}
                  onChange={(event, value) => {this.handleOnChange(attr, `POINT(${value} ${geo.coordinates[1]} ${geo.coordinates[2]})`)}}
                />
              </Col>
              <Col xs={4}>
                <TextField
                  id={field.key + 'y'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? "Latitude" : null}
                  underlineShow={this.props.showLabels}
                  style={style.root}
                  fullWidth={true}
                  value={geo.coordinates[1]}
                  onChange={(event, value) => {this.handleOnChange(attr, `POINT(${geo.coordinates[0]} ${value} ${geo.coordinates[2]})`)}}
                />
              </Col>
              <Col xs={4}>
                <TextField
                  id={field.key + 'z'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? "Attitude" : null}
                  underlineShow={this.props.showLabels}
                  style={style.root}
                  fullWidth={true}
                  value={geo.coordinates[2]}
                  onChange={(event, value) => {this.handleOnChange(attr, `POINT(${geo.coordinates[0]} ${geo.coordinates[1]} ${value})`)}}
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
                floatingLabelText={this.props.showLabels ? label + " Longitude" : null}
                underlineShow={this.props.showLabels}
                style={style.root}
                fullWidth={true}
                value={center.coordinates[0]}
                onChange={(event, value) => {this.handleOnChange(attr, `POINT(${value} ${center.coordinates[1]} ${center.coordinates[2]})`)}}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + 'y'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? "Latitude" : null}
                underlineShow={this.props.showLabels}
                style={style.root}
                fullWidth={true}
                value={center.coordinates[1]}
                onChange={(event, value) => {this.handleOnChange(attr, `POINT(${center.coordinates[0]} ${value} ${center.coordinates[2]})`)}}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + 'z'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? "Attitude" : null}
                underlineShow={this.props.showLabels}
                style={style.root}
                fullWidth={true}
                value={center.coordinates[2]}
                onChange={(event, value) => {this.handleOnChange(attr, `POINT(${center.coordinates[0]} ${center.coordinates[1]} ${value})`)}}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + 'radius'}
                type='number'
                step='0.01'
                value={field.radius}
                floatingLabelText={this.props.showLabels ? "Radius" : null}
                underlineShow={this.props.showLabels}
                style={style.root}
                fullWidth={true}
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
            floatingLabelText={label}
            underlineShow={this.props.showLabels}
            //inputProps={{ pattern: "[a-z]" }}
            style={style.root}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
          />
        );
      default:
        return (
          <TextField
            id={field.key}
            fullWidth={true}
            floatingLabelText={label + " (UI Not Implemented)"}
            underlineShow={this.props.showLabels}
            style={style.root}
            disabled
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
          />
        );
    }

  }
}

FieldInput.defaultProps = {
  showLabels: true
}
