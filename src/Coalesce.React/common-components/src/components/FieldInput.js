import React from 'react';
import Checkbox from 'material-ui/Checkbox';
import TextField from 'material-ui/TextField';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import DatePicker from 'material-ui/DatePicker';
import TimePicker from 'material-ui/TimePicker';
import { IconButton } from 'common-components/lib/components'
import muiThemeable from 'material-ui/styles/muiThemeable';

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
        },
        floatingLabel: {
          color: 'rgba(0,0,0,0.5)'
        },
        floatingLabelFocus: {
          color: props.muiTheme ? props.muiTheme.textField.focusColor : 'rgba(0,0,0,0.5)'
        },
        underline: {
          borderColor: 'rgba(0,0,0,0.5)'
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
        },
        floatingLabel: {
        },
        floatingLabelFocus: {
        },
        underline: {
        }

      }
    }

    this.state = {
      field: props.field,
      style: style,
    };

    this.handleOnChange = this.handleOnChange.bind(this);
  }

  handleOnChange(attr, value) {
    const {field} = this.state;
    field[attr] = value;

    //console.log(`${attr}=${value}`);
    this.setState(field);

    if (this.props.onChange) {
      this.props.onChange(value);
    }
  }

  render() {

    const {field, style} = this.state;

    var options = [];

    if (this.props.options) {
      options = this.props.options.map((item) => {
        return (
          <MenuItem key={item.enum} value={item.enum} primaryText={item.label} />
        )
      })
    }

    var type = (this.props.dataType != null) ? this.props.dataType : field.dataType;
    var attr = (this.props.attr != null) ? this.props.attr : 'value';
    var label = this.props.showLabels ? this.props.label ? this.props.label : (field.label != null && field.label.length > 0 ? field.label : field.name) : null;
    var defaultValue = (this.props.defaultValue != null) ? this.props.defaultValue : field.defaultValue;
    var view;

    switch (type) {
      case 'ENUMERATION_LIST_TYPE':
        view = (
          <SelectField
            id={field.key}
            fullWidth={true}
            floatingLabelText={label}
            underlineShow={this.props.showLabels}
            multiple={true}
            value={field[attr] ? field[attr] : null}
            style={style.root}
            labelStyle={style.root}
            iconStyle={style.none}
            hintStyle={style.none}
            hintText={this.props.hint ? this.props.hint : ""}
            floatingLabelStyle={style.floatingLabel}
            floatingLabelFocusStyle={style.floatingLabelFocus}
            underlineStyle={style.underline}
            errorStyle={style.none}
            onChange={(event, index, values) => {this.handleOnChange(attr, values)}}
            onKeyDown={this.props.onKeyDown}
          >
            {options}
          </SelectField>
        )
        break;
      case 'ENUMERATION_TYPE':
        view = (
            <SelectField
              id={field.key}
              fullWidth={true}
              floatingLabelText={label}
              underlineShow={this.props.showLabels}
              style={style.root}
              labelStyle={style.root}
              iconStyle={style.none}
              hintStyle={style.none}
              hintText={this.props.hint ? this.props.hint : ""}
              floatingLabelStyle={style.floatingLabel}
              floatingLabelFocusStyle={style.floatingLabelFocus}
              underlineStyle={style.underline}
              errorStyle={style.none}
              value={field[attr] ? field[attr] : null}
              onChange={(event, value) => {this.handleOnChange(attr, this.props.options[value].enum)}}
              onKeyDown={this.props.onKeyDown}
            >
              {options}
            </SelectField>
        )
        break;
      case 'URI_TYPE':
      case 'STRING_TYPE':
        view = (
          <TextField
            id={field.key}
            fullWidth={true}
            underlineShow={this.props.showLabels}
            style={style.root}
            hintText={this.props.hint ? this.props.hint : ""}
            floatingLabelStyle={style.floatingLabel}
            floatingLabelFocusStyle={style.floatingLabelFocus}
            underlineStyle={style.underline}
            underlineStyle={style.underline}
            value={field[attr]}
            defaultValue={defaultValue}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case 'BOOLEAN_LIST_TYPE':
      case 'GUID_LIST_TYPE':
      case 'FLOAT_LIST_TYPE':
      case 'DOUBLE_LIST_TYPE':
      case 'LONG_LIST_TYPE':
      case 'INTEGER_LIST_TYPE':
      case 'STRING_LIST_TYPE':
        view = (
          <TextField
              id={field.key}
              fullWidth={true}
              floatingLabelText={label}
              hintText={`(CSV) ${this.props.hint ? this.props.hint : ""}`}
              underlineShow={this.props.showLabels}
              style={style.root}
              floatingLabelStyle={style.floatingLabel}
              floatingLabelFocusStyle={style.floatingLabelFocus}
              underlineStyle={style.underline}
              value={field[attr] ? field[attr].join() : ""}
              defaultValue={defaultValue}
              onChange={(event, value) => {this.handleOnChange(attr, value.split(","))}}
              onKeyDown={this.props.onKeyDown}
            />
          );
          break;
      case 'FLOAT_TYPE':
      case 'DOUBLE_TYPE':
      case 'LONG_TYPE':
        view = (
          <TextField
            id={field.key}
            type='number'
            step='0.01'
            fullWidth={true}
            floatingLabelText={label}
            underlineShow={this.props.showLabels}
            style={style.root}
            hintText={this.props.hint ? this.props.hint : ""}
            floatingLabelStyle={style.floatingLabel}
            floatingLabelFocusStyle={style.floatingLabelFocus}
            underlineStyle={style.underline}
            value={field[attr]}
            defaultValue={{
              defaultValue
            }}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case 'INTEGER_TYPE':
        view = (
          <TextField
            id={field.key}
            type='number'
            fullWidth={true}
            floatingLabelText={label}
            underlineShow={this.props.showLabels}
            style={style.root}
            hintText={this.props.hint ? this.props.hint : ""}
            floatingLabelStyle={style.floatingLabel}
            floatingLabelFocusStyle={style.floatingLabelFocus}
            underlineStyle={style.underline}
            value={field[attr]}
            defaultValue={defaultValue}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case 'BOOLEAN_TYPE':
      console.log(field);
        console.log(field[attr]);
        view = (
          <Checkbox
            id={field.key}
            label={label}
            style={style.root}
            checked={field[attr]}
            defaultChecked={defaultValue}
            onCheck={(event, checked) => {this.handleOnChange(attr, checked)}}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case 'DATE_TIME_TYPE':

        var dateTime

        if (field[attr] == null || field[attr] === "") {
          dateTime = null;
        } else {
          dateTime = new Date(field[attr]);
        }

        view = (
          <Row>
            <Col xs={6}>
              <DatePicker
                id={field.key + 'date'}
                floatingLabelText={this.props.showLabels ? label + " Date" : null}
                hintText={this.props.hint ? this.props.hint : ""}
                floatingLabelStyle={style.floatingLabel}
                floatingLabelFocusStyle={style.floatingLabelFocus}
                underlineStyle={style.underline}
                underlineShow={this.props.showLabels}
                fullWidth={true}
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
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
            <Col xs={6}>
              <TimePicker
                id={field.key + 'time'}
                floatingLabelText={this.props.showLabels ? "Time" : null}
                hintText={this.props.hint ? this.props.hint : ""}
                floatingLabelStyle={style.floatingLabel}
                floatingLabelFocusStyle={style.floatingLabelFocus}
                underlineStyle={style.underline}
                underlineShow={this.props.showLabels}
                fullWidth={true}
                style={style.root}
                value={dateTime}
                format="24hr"
                onChange={(tmp, date) => {
                  var newDateTime = dateTime != null ? dateTime : new Date();
                  newDateTime.setHours(date.getHours());
                  newDateTime.setMinutes(date.getMinutes());
                  this.handleOnChange(attr, newDateTime.toISOString());
                }}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
        </Row>
        );
        break;
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
        break;
      case 'LINE_STRING_TYPE':
          view = (
            <TextField
              id={field.key}
              fullWidth={true}
              floatingLabelText={label + " - LINESTRING (x1 y1 z1, x2 y2 z2, ...)"}
              hintText={this.props.hint ? this.props.hint : ""}
              floatingLabelStyle={style.floatingLabel}
              floatingLabelFocusStyle={style.floatingLabelFocus}
              underlineStyle={style.underline}
              underlineShow={this.props.showLabels}
              style={style.root}
              value={field[attr]}
              defaultValue={defaultValue}
              onChange={(event, value) => {this.handleOnChange(attr, value)}}
              onKeyDown={this.props.onKeyDown}
            />
          );
          break;
      case 'POLYGON_TYPE':
          view = (
            <TextField
              id={field.key}
              fullWidth={true}
              floatingLabelText={label + " - POLYGON ((x1 y1 z1, x2 y2 z2, ...))"}
              hintText={this.props.hint ? this.props.hint : ""}
              floatingLabelStyle={style.floatingLabel}
              floatingLabelFocusStyle={style.floatingLabelFocus}
              underlineStyle={style.underline}
              underlineShow={this.props.showLabels}
              style={style.root}
              value={field[attr]}
              defaultValue={defaultValue}
              onChange={(event, value) => {this.handleOnChange(attr, value)}}
              onKeyDown={this.props.onKeyDown}
            />
          );
          break;
      case 'GEOCOORDINATE_LIST_TYPE':
          view = (
            <TextField
              id={field.key}
              fullWidth={true}
              floatingLabelText={label + " - MULTIPOINT (x1 y1 z1, x2 y2 z2, ...)"}
              hintText={this.props.hint ? this.props.hint : ""}
              floatingLabelStyle={style.floatingLabel}
              floatingLabelFocusStyle={style.floatingLabelFocus}
              underlineStyle={style.underline}
              underlineShow={this.props.showLabels}
              style={style.root}
              value={field[attr]}
              defaultValue={defaultValue}
              onChange={(event, value) => {this.handleOnChange(attr, value)}}
              onKeyDown={this.props.onKeyDown}
            />
          );
          break;
      case 'GEOCOORDINATE_TYPE':

      var geo;

        if (field.value == null || field.value === "") {
          geo = {coordinates: [0, 0, 0]};
        } else {
          geo = parse(field.value);

          if (geo == null) {
              geo = {coordinates: [0, 0, 0]};
          }
        }

        view = (
            <Row>
              <Col xs={4}>
                <TextField
                  id={field.key + 'x'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? label + " Longitude" : null}
                  hintText={this.props.hint ? this.props.hint : ""}
                  floatingLabelStyle={style.floatingLabel}
                  floatingLabelFocusStyle={style.floatingLabelFocus}
                  underlineStyle={style.underline}
                  underlineShow={this.props.showLabels}
                  style={style.root}
                  fullWidth={true}
                  value={geo.coordinates[0]}
                  onChange={(event, value) => {this.handleOnChange(attr, `POINT(${value} ${geo.coordinates[1]} ${geo.coordinates[2]})`)}}
                  onKeyDown={this.props.onKeyDown}
                />
              </Col>
              <Col xs={4}>
                <TextField
                  id={field.key + 'y'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? "Latitude" : null}
                  hintText={this.props.hint ? this.props.hint : ""}
                  floatingLabelStyle={style.floatingLabel}
                  floatingLabelFocusStyle={style.floatingLabelFocus}
                  underlineStyle={style.underline}
                  underlineShow={this.props.showLabels}
                  style={style.root}
                  fullWidth={true}
                  value={geo.coordinates[1]}
                  onChange={(event, value) => {this.handleOnChange(attr, `POINT(${geo.coordinates[0]} ${value} ${geo.coordinates[2]})`)}}
                  onKeyDown={this.props.onKeyDown}
                />
              </Col>
              <Col xs={4}>
                <TextField
                  id={field.key + 'z'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? "Attitude" : null}
                  hintText={this.props.hint ? this.props.hint : ""}
                  floatingLabelStyle={style.floatingLabel}
                  floatingLabelFocusStyle={style.floatingLabelFocus}
                  underlineStyle={style.underline}
                  underlineShow={this.props.showLabels}
                  style={style.root}
                  fullWidth={true}
                  value={geo.coordinates[2]}
                  onChange={(event, value) => {this.handleOnChange(attr, `POINT(${geo.coordinates[0]} ${geo.coordinates[1]} ${value})`)}}
                  onKeyDown={this.props.onKeyDown}
                />
              </Col>
          </Row>
        );
        break;
      case 'CIRCLE_TYPE':

        var center;

        if (field.value == null || field.value === "") {
          center = {coordinates: [0, 0, 0]};
        } else {
          center = parse(field.value);
        }

        view = (
          <Row>
            <Col xs={3}>
              <TextField
                id={field.key + 'x'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? label + " Longitude" : null}
                hintText={this.props.hint ? this.props.hint : ""}
                floatingLabelStyle={style.floatingLabel}
                floatingLabelFocusStyle={style.floatingLabelFocus}
                underlineStyle={style.underline}
                underlineShow={this.props.showLabels}
                style={style.root}
                fullWidth={true}
                value={center.coordinates[0]}
                onChange={(event, value) => {this.handleOnChange(attr, `POINT(${value} ${center.coordinates[1]} ${center.coordinates[2]})`)}}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + 'y'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? "Latitude" : null}
                hintText={this.props.hint ? this.props.hint : ""}
                floatingLabelStyle={style.floatingLabel}
                floatingLabelFocusStyle={style.floatingLabelFocus}
                underlineStyle={style.underline}
                underlineShow={this.props.showLabels}
                style={style.root}
                fullWidth={true}
                value={center.coordinates[1]}
                onChange={(event, value) => {this.handleOnChange(attr, `POINT(${center.coordinates[0]} ${value} ${center.coordinates[2]})`)}}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + 'z'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? "Attitude" : null}
                hintText={this.props.hint ? this.props.hint : ""}
                floatingLabelStyle={style.floatingLabel}
                floatingLabelFocusStyle={style.floatingLabelFocus}
                underlineStyle={style.underline}
                underlineShow={this.props.showLabels}
                style={style.root}
                fullWidth={true}
                value={center.coordinates[2]}
                onChange={(event, value) => {this.handleOnChange(attr, `POINT(${center.coordinates[0]} ${center.coordinates[1]} ${value})`)}}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + 'radius'}
                type='number'
                step='0.01'
                value={field.radius}
                floatingLabelText={this.props.showLabels ? "Radius" : null}
                hintText={this.props.hint ? this.props.hint : ""}
                floatingLabelStyle={style.floatingLabel}
                floatingLabelFocusStyle={style.floatingLabelFocus}
                underlineStyle={style.underline}
                underlineShow={this.props.showLabels}
                style={style.root}
                fullWidth={true}
                onChange={(event, value) => {this.handleOnChange('radius', value)}}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
          </Row>
      );
      break;
      case 'GUID_TYPE':
        view = (
          <TextField
            id={field.key}
            fullWidth={true}
            floatingLabelText={label}
            hintText={this.props.hint ? this.props.hint : ""}
            floatingLabelStyle={style.floatingLabel}
            floatingLabelFocusStyle={style.floatingLabelFocus}
            underlineStyle={style.underline}
            underlineShow={this.props.showLabels}
            //inputProps={{ pattern: "[a-z]" }}
            style={style.root}
            value={field[attr]}
            defaultValue={defaultValue}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      default:
        view = (
          <TextField
            id={field.key}
            fullWidth={true}
            floatingLabelText={label + " (UI Not Implemented)"}
            hintText={this.props.hint ? this.props.hint : ""}
            floatingLabelStyle={style.floatingLabel}
            floatingLabelFocusStyle={style.floatingLabelFocus}
            underlineStyle={style.underline}
            underlineShow={this.props.showLabels}
            style={style.root}
            disabled
            value={field[attr]}
            defaultValue={defaultValue}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
            onKeyDown={this.props.onKeyDown}
          />
        )
        break;
    }

    if (this.props.isNullable) {
      return (
        <table className={this.props.css}>
          <tbody>
            <tr>
              <td width="100%">
                {view}
              </td>
              <td width="30px">
                <IconButton
                  id={field.key}
                  icon="/images/svg/clear.svg"
                  title={"Clear " + label}
                  onClick={() => this.handleOnChange(attr, "")}
                />
              </td>
            </tr>
          </tbody>
        </table>
      )
      /*
      return (<Row>
        <Col xs={10}>
          {view}
        </Col>
        <Col xs={2} style={{'marginTop': '30px'}}>
          <IconButton
            id={field.key}
            icon="/images/svg/clear.svg"
            title={"Clear " + label}
            onClick={() => this.handleOnChange(attr, "")}
          />
        </Col>
      </Row>
    )*/
    } else {
      return view;
    }
  }
}

FieldInput.defaultProps = {
  showLabels: true
}

export default muiThemeable()(FieldInput);
