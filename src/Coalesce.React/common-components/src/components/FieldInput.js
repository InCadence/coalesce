import React from "react";
import {Row, Col} from "react-bootstrap";

import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import TextField from "@material-ui/core/TextField";
import InputAdornment from "@material-ui/core/InputAdornment";
import {withTheme} from "@material-ui/core/styles";
import Tooltip from "@material-ui/core/Tooltip";

import Enumeration from "common-components/lib/components/fieldInputs/Enumeration";
import IconButton from "common-components/lib/components/IconButton";

// TODO Replace Date / Time Pickers
//import DatePicker from 'material-ui/DatePicker';
//import TimePicker from 'material-ui/TimePicker';

var parse = require("wellknown");

export class FieldInput extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      field: props.field,
      style: this.createStyle(props)
    };

    this.handleOnChange = this.handleOnChange.bind(this);
  }

  componentDidUpdate(prevProps) {
    if (this.props.field != prevProps.field ||
        this.props.dataType != prevProps.dataType ||
        this.props.field.dataType != prevProps.field.dataType) {
      this.setState({
        field: this.props.field,
        style: this.createStyle(this.props)
      });
    }
  }

  createStyle(props) {
    var type = props.dataType ? props.dataType : props.field.dataType;

    if (this.props.showLabels) {
      return {
        root: {},
        none: {},
        floatingLabel: {
          color: "rgba(0,0,0,0.5)"
        },
        floatingLabelFocus: {
          color: props.muiTheme
            ? props.muiTheme.textField.focusColor
            : "rgba(0,0,0,0.5)"
        },
        underline: {
          borderColor: "rgba(0,0,0,0.5)"
        }
      };
    } else {
      return {
        root: {
          width: type === "BOOLEAN_TYPE" ? "20px" : undefined,
          lineHeight: "20px",
          height: type === "BOOLEAN_TYPE" ? "20px" : undefined,
          backgroundColor: type === "BOOLEAN_TYPE" ? undefined : "#FFFFFF"
        },
        none: {
          display: "none"
        },
        floatingLabel: {},
        floatingLabelFocus: {},
        underline: {}
      };
    }
  }

  handleOnChange(attr, value) {
    const {field} = this.state;

    if (
      Array.isArray(value) &&
      value.length === 1 &&
      (value[0] === "" || value[0] === undefined)
    ) {
      value = [];
    }

    field[attr] = value;

    //console.log(`${attr}=${value}`);
    this.setState(() => {
      return {field: field};
    });

    if (this.props.onChange) {
      this.props.onChange(value);
    }
  }

  render() {
    const {field, style} = this.state;

    var type = this.props.dataType ? this.props.dataType : field.dataType;
    var attr = this.props.attr ? this.props.attr : "value";
    var label = this.props.showLabels
      ? this.props.label
        ? this.props.label
        : field.label && field.label.length > 0
        ? field.label
        : field.name
      : null;
    var defaultValue = this.props.defaultValue
      ? this.props.defaultValue
      : field.defaultValue;
    var view;

    switch (type) {
      case "ENUMERATION_LIST_TYPE":
        view = (
          <Enumeration
            {...this.props}
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

      case "ENUMERATION_TYPE":
        view = (
          <Enumeration
            {...this.props}
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

      case "URI_TYPE":
      case "STRING_TYPE":
        view = (
          <TextField
            {...this.props}
            id={field.key}
            fullWidth
            label={label}
            style={style.root}
            //helperText={this.props.showLabels ? this.props.hint : undefined}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={event => {
              this.handleOnChange(attr, event.target.value);
            }}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case "BOOLEAN_LIST_TYPE":
      case "GUID_LIST_TYPE":
      case "FLOAT_LIST_TYPE":
      case "DOUBLE_LIST_TYPE":
      case "LONG_LIST_TYPE":
      case "INTEGER_LIST_TYPE":
      case "STRING_LIST_TYPE":
        view = (
          <TextField
            id={field.key}
            fullWidth
            label={label}
            //helperText={this.props.showLabels ? this.props.hint : undefined}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">(CSV)</InputAdornment>
              )
            }}
            style={style}
            defaultValue={field.defaultValue}
            value={field[attr] ? Array.isArray(field[attr]) ? field[attr].join() : field[attr] : ""}
            onChange={event => {
              this.handleOnChange(attr, event.target.value.split(","));
            }}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case "FLOAT_TYPE":
      case "DOUBLE_TYPE":
      case "LONG_TYPE":
        view = (
          //pass these a "step" prop (.01 or 1)
          <TextField
            id={field.key}
            type="number"
            inputProps={{step: 0.01, style: style.root}}
            fullWidth
            label={label}
            //helperText={this.props.showLabels ? this.props.hint : undefined}
            style={style.root}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={event => {
              this.handleOnChange(attr, event.target.value);
            }}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case "INTEGER_TYPE":
        view = (
          <TextField
            id={field.key}
            type="number"
            inputProps={{step: 1, style: style.root}}
            fullWidth
            label={label}
            //helperText={this.props.showLabels ? this.props.hint : undefined}
            style={style.root}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={event => {
              this.handleOnChange(attr, event.target.value);
            }}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case "BOOLEAN_TYPE":
          view = (
            <Checkbox
              id={field.key}
              checked={field[attr] === true || field[attr] === "true"}
              style={style.root}
              disableRipple
              defaultChecked={defaultValue}
              onChange={event => {
                this.handleOnChange(attr, event.target.checked);
              }}
              onKeyDown={this.props.onKeyDown}
            />
          );

          if (label) {
            view = (
              <FormControlLabel
                label={label}
                control={view}
              />
            );
          }
        break;

      case "BINARY_TYPE":
      case "FILE_TYPE":
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
      case "LINE_STRING_TYPE":
        view = (
          <TextField
            id={field.key}
            fullWidth={true}
            label={label + " - LINESTRING (x1 y1 z1, x2 y2 z2, ...)"}
            //helperText={this.props.showLabels ? this.props.hint : undefined}
            style={style.root}
            value={field[attr]}
            defaultValue={defaultValue}
            onChange={event => {
              this.handleOnChange(attr, event.target.value);
            }}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case "POLYGON_TYPE":
        view = (
          <TextField
            id={field.key}
            fullWidth={true}
            label={label + " - POLYGON ((x1 y1 z1, x2 y2 z2, ...))"}
            //helperText={this.props.showLabels ? this.props.hint : undefined}
            style={style.root}
            value={field[attr]}
            defaultValue={defaultValue}
            onChange={event => {
              this.handleOnChange(attr, event.target.value);
            }}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case "GEOCOORDINATE_LIST_TYPE":
        view = (
          <TextField
            id={field.key}
            fullWidth={true}
            label={label + " - MULTIPOINT (x1 y1 z1, x2 y2 z2, ...)"}
            //helperText={this.props.showLabels ? this.props.hint : undefined}
            style={style.root}
            value={field[attr]}
            defaultValue={defaultValue}
            onChange={event => {
              this.handleOnChange(attr, event.target.value);
            }}
            onKeyDown={this.props.onKeyDown}
          />
        );
        break;
      case "GEOCOORDINATE_TYPE":
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
                id={field.key + "x"}
                type="number"
                step="0.01"
                label={this.props.showLabels ? label + " Longitude" : null}
                //helperText={this.props.showLabels ? this.props.hint : undefined}
                style={style.root}
                fullWidth={true}
                value={geo.coordinates[0]}
                onChange={event => {
                  this.handleOnChange(
                    attr,
                    `POINT(${event.target.value} ${geo.coordinates[1]} ${
                      geo.coordinates[2]
                    })`
                  );
                }}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
            <Col xs={4}>
              <TextField
                id={field.key + "y"}
                type="number"
                step="0.01"
                label={this.props.showLabels ? "Latitude" : null}
                //helperText={this.props.showLabels ? this.props.hint : undefined}
                style={style.root}
                fullWidth={true}
                value={geo.coordinates[1]}
                onChange={event => {
                  this.handleOnChange(
                    attr,
                    `POINT(${geo.coordinates[0]} ${event.target.value} ${
                      geo.coordinates[2]
                    })`
                  );
                }}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
            <Col xs={4}>
              <TextField
                id={field.key + "z"}
                type="number"
                step="0.01"
                label={this.props.showLabels ? "Attitude" : null}
                //helperText={this.props.showLabels ? this.props.hint : undefined}
                style={style.root}
                fullWidth={true}
                value={geo.coordinates[2]}
                onChange={event => {
                  this.handleOnChange(
                    attr,
                    `POINT(${geo.coordinates[0]} ${
                      geo.coordinates[1]
                    } ${event.target.value})`
                  );
                }}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
          </Row>
        );
        break;
      case "CIRCLE_TYPE":
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
                id={field.key + "x"}
                type="number"
                step="0.01"
                label={this.props.showLabels ? label + " Longitude" : null}
                //helperText={this.props.showLabels ? this.props.hint : undefined}
                style={style.root}
                fullWidth={true}
                value={center.coordinates[0]}
                onChange={event => {
                  this.handleOnChange(
                    attr,
                    `POINT(${event.target.value} ${center.coordinates[1]} ${
                      center.coordinates[2]
                    })`
                  );
                }}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + "y"}
                type="number"
                step="0.01"
                label={this.props.showLabels ? "Latitude" : null}
                //helperText={this.props.showLabels ? this.props.hint : undefined}
                style={style.root}
                fullWidth={true}
                value={center.coordinates[1]}
                onChange={event => {
                  this.handleOnChange(
                    attr,
                    `POINT(${center.coordinates[0]} ${event.target.value} ${
                      center.coordinates[2]
                    })`
                  );
                }}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + "z"}
                type="number"
                step="0.01"
                label={this.props.showLabels ? "Attitude" : null}
                //helperText={this.props.showLabels ? this.props.hint : undefined}
                style={style.root}
                fullWidth={true}
                value={center.coordinates[2]}
                onChange={event => {
                  this.handleOnChange(
                    attr,
                    `POINT(${center.coordinates[0]} ${
                      center.coordinates[1]
                    } ${event.targetvalue})`
                  );
                }}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
            <Col xs={3}>
              <TextField
                id={field.key + "radius"}
                type="number"
                step="0.01"
                value={field.radius}
                label={this.props.showLabels ? "Radius" : null}
                //helperText={this.props.showLabels ? this.props.hint : undefined}
                style={style.root}
                fullWidth={true}
                onChange={event => {
                  this.handleOnChange("radius", event.target.value);
                }}
                onKeyDown={this.props.onKeyDown}
              />
            </Col>
          </Row>
        );
        break;
      case "GUID_TYPE":
        view = (
          <TextField
            id={field.key}
            fullWidth
            label={label}
            //helperText={this.props.showLabels ? this.props.hint : undefined}
            //inputProps={{ pattern: "[a-z]" }}
            style={style.root}
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={event => {
              this.handleOnChange(attr, event.target.value);
            }}
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
            inputProps={{style: {borderBottom: "1px solid rgba(0, 0, 0, 0.5)"}}}
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
            //helperText={this.props.showLabels ? this.props.hint : undefined}
            style={style.root}
            inputProps={this.props.inputProps}
            disabled
            value={field[attr]}
            defaultValue={field.defaultValue}
            onChange={event => {
              this.handleOnChange(attr, event.target.value);
            }}
          />
        );
        break;
    }

    if (this.props.hint) {
      view = (
        <Tooltip
          title={this.props.hint}
          placement={
            this.props.titlePosition ? this.props.titlePosition : "bottom"
          }
        >
          {view}
        </Tooltip>
      );
    }

    if (this.props.isNullable) {
      view = (
        <table className={this.props.css}>
          <tbody>
            <tr>
              <td width="100%">{view}</td>
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
      );
    }

    return view;
  }
}

FieldInput.defaultProps = {
  showLabels: true
};

export default withTheme()(FieldInput);
