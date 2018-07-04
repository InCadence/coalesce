import React from 'react';
import Checkbox from 'material-ui/Checkbox';
import TextField from 'material-ui/TextField';
import DatePicker from 'material-ui/DatePicker';
import TimePicker from 'material-ui/TimePicker';
import { Row, Col } from 'react-bootstrap';
import { IconButton } from 'common-components/lib/components/IconButton.js'
import Enumeration from './field-inputs/Enumeration.js'
import Point from './field-inputs/geo/Point.js'
import Multipoint from './field-inputs/geo/Multipoint.js';
import Shape from './field-inputs/shape/Shape.js'

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

    //console.log(`${attr}=${value}`);
    this.setState(field);

  }

  render() {

    const {field, style} = this.state;

    var type = (this.props.dataType != null) ? this.props.dataType : field.dataType;
    var attr = (this.props.attr != null) ? this.props.attr : 'value';
    var label = this.props.showLabels ? (field.label != null && field.label.length > 0 ? field.label : field.name) : null;

    var opts = {
      label: label,
      attr: attr,
      field: field,
      style: style,
    }
    switch (type) {
      case 'ENUMERATION_LIST_TYPE':
        return (
          <Enumeration
            list={true}
            field={field}
            style={style}
            label={label}
            showLabels={this.props.showLabels}
            attr={attr}
            options={this.props.options}
            handleOnChange={this.handleOnChange}/>
        )
      case 'ENUMERATION_TYPE':
        return (
          <Enumeration
            list={false}
            field={field}
            style={style}
            label={label}
            showLabels={this.props.showLabels}
            attr={attr}
            handleOnChange={this.handleOnChange}/>
        );
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
          //pass these a "step" prop (.01 or 1)
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
            step='1'
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
            <Shape
              shape='LineString'
              opts={opts}
              showLabels={this.props.showLabels}
              multi={false}/>
          );
      case 'POLYGON_TYPE':
          return (
            <Shape
              shape='Polygon'
              opts={opts}
              showLabels={this.props.showLabels}
              multi={false}/>
          );
      case 'GEOCOORDINATE_LIST_TYPE':
        return (
          <Multipoint
            opts={opts}
            showLabels={this.props.showLabels}
            />
        );
      case 'GEOCOORDINATE_TYPE':
        return (
          <Point
            opts={opts}
            showLabels={this.props.showLabels}/>
        );

      case 'CIRCLE_TYPE':

        return (
          <Shape
            shape="Circle"
            opts={opts}
            showLabels={this.props.showLabels}
            multi={false}/>
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
