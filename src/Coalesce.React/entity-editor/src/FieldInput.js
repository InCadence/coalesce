import React from 'react';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import TextField from '@material-ui/core/TextField';
import Point from './field-inputs/geo/Point.js'
import Multipoint from './field-inputs/geo/Multipoint.js';
import Shape from './field-inputs/shape/Shape.js'
import Enumeration from 'common-components/lib/components/fieldInputs/Enumeration'
import IconButton from 'common-components/lib/components/IconButton'
import InputAdornment from '@material-ui/core/InputAdornment'
import { withTheme } from '@material-ui/core/styles';
import { Row, Col } from 'react-bootstrap';

// TODO Replace Date / Time Pickers
import DatePicker from 'material-ui/DatePicker';
import TimePicker from 'material-ui/TimePicker';

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

  componentWillReceiveProps(nextProps) {
    this.setState({
      field: nextProps.field
    })
  }

  handleOnChange(attr, value) {
    const {field} = this.state;

    if (Array.isArray(value) && value.length === 1 && (value[0] === "" || value[0] === undefined)) {
      value = [];
    }

    field[attr] = value;

    //console.log(`${attr}=${value}`);
    this.setState(() => {return {field: field}});

    if (this.props.onChange) {
      this.props.onChange(value);
    }
  }

  render() {

    const {field, style} = this.state;

    var type = (this.props.dataType != null) ? this.props.dataType : field.dataType;
    var attr = (this.props.attr != null) ? this.props.attr : 'value';
    var label = this.props.showLabels ? this.props.label ? this.props.label : (field.label != null && field.label.length > 0 ? field.label : field.name) : null;
    var defaultValue = (this.props.defaultValue != null) ? this.props.defaultValue : field.defaultValue;
    var view;

    var opts = {
      label: label,
      attr: attr,
      field: field,
      style: style,
    }

    switch (type) {
      case 'ENUMERATION_LIST_TYPE':
        view = (
          <Enumeration
            list={true}
            field={field}
            style={style}
            label={label}
            showLabels={this.props.showLabels}
            attr={attr}
            options={this.props.options}
            onChange={this.handleOnChange}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;

      case 'ENUMERATION_TYPE':
        view = (
          <Enumeration
            list={false}
            field={field}
            dense
            style={style}
            label={label}
            showLabels={this.props.showLabels}
            attr={attr}
            options={this.props.options}
            onChange={this.handleOnChange}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;

      case 'URI_TYPE':
      case 'STRING_TYPE':
        view = (
          <TextField
            id={field.key}
            fullWidth
            label={label}
            style={style.root}
            helperText={this.props.hint}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={(event) => {this.handleOnChange(attr, event.target.value)}}
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
              fullWidth
              label={label}
              helperText={this.props.hint}
              InputProps={{
                  startAdornment: <InputAdornment position="start">(CSV)</InputAdornment>,
                }}
              style={style}
              value={field[attr]}
              defaultValue={field.defaultValue}
              onChange={(event) => {this.handleOnChange(attr, event.target.value)}}
              onKeyDown={this.props.onKeyDown}
            />
          );
          break;
      case 'FLOAT_TYPE':
      case 'DOUBLE_TYPE':
      case 'LONG_TYPE':
        view = (
          //pass these a "step" prop (.01 or 1)
          <TextField
            id={field.key}
            type='number'
            inputProps={{step: 0.01, style: style.root}}
            fullWidth
            label={label}
            helperText={this.props.hint}
            style={style.root}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={(event) => {this.handleOnChange(attr, event.target.value)}}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case 'INTEGER_TYPE':
        view = (
          <TextField
            id={field.key}
            type='number'
            inputProps={{step: 1, style: style.root}}
            fullWidth
            label={label}
            helperText={this.props.hint}
            style={style.root}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={(event) => {this.handleOnChange(attr, event.target.value)}}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case 'BOOLEAN_TYPE':
        view = (
          <FormControlLabel label={label} control={
              <Checkbox
                id={field.key}
                checked={field[attr] === true}
                style={style}
                disableRipple
                defaultChecked={defaultValue}
                onChange={(event) => {this.handleOnChange(attr, event.target.checked)}}
                onKeyDown={this.props.onKeyDown}
              />
            }
          />
        );
        break;

      case 'DATE_TIME_TYPE':

        var dateTime

        if (field.value == null || field.value === "") {
          dateTime = null;
        } else {
          dateTime = new Date(field.value);
        }

        view = (
          <Row>
            <Col xs={6}>
              <DatePicker
                id={field.key + 'date'}
                fullWidth
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
                fullWidth
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
        //break;    <- unreachable

      case 'LINE_STRING_TYPE':
          view = (
            <Shape
              shape='LineString'
              opts={opts}
              showLabels={this.props.showLabels}
              multi={false}
              handleOnChange={this.handleOnChange}
            />
          );
          break;
      case 'POLYGON_TYPE':
          view = (
            <Shape
              shape='Polygon'
              opts={opts}
              showLabels={this.props.showLabels}
              multi={false}
              handleOnChange={this.handleOnChange}
            />
          );
          break;
      case 'GEOCOORDINATE_LIST_TYPE':
          view = (
          <Multipoint
            opts={opts}
            showLabels={this.props.showLabels}
            handleOnChange={this.handleOnChange}
            />
          );
          break;
      case 'GEOCOORDINATE_TYPE':
        view = (
          <Point
            opts={opts}
            showLabels={this.props.showLabels}
            handleOnChange={this.handleOnChange}
                />
        );
        break;

      case 'CIRCLE_TYPE':

        view = (
          <Shape
            shape="Circle"
            opts={opts}
            showLabels={this.props.showLabels}
            multi={false}
            handleOnChange={this.handleOnChange}
              />
      );
      break;
      case 'GUID_TYPE':
        view = (
          <TextField
            id={field.key}
            fullWidth={true}
            label={label}
            helperText={this.props.hint}
            //inputProps={{ pattern: "[a-z]" }}
            style={style.root}
            value={field[attr]}
            defaultValue={defaultValue}
            onChange={(event, value) => {this.handleOnChange(attr, value)}}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case "LABEL":
        view = (
          <TextField
            id={field.key}
            fullWidth={true}
            label={label}
            style={style}
            inputProps={{style: { 'borderBottom': '1px solid rgba(0, 0, 0, 0.5)' }}}
            disabled
          />
        );
        break;
      default:
        view = (
          <TextField
            id={field.key}
            fullWidth={true}
            label={label + " (UI Not Implemented)"}
            helperText={this.props.hint}
            style={style}
            inputProps={this.props.inputProps}
            disabled
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={(event) => {this.handleOnChange(attr, event.target.value)}}
          />
        );
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
    } else {
      return view;
    }
  }
}

FieldInput.defaultProps = {
  showLabels: true
}

export default withTheme()(FieldInput);
