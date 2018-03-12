import React from 'react';
import Checkbox from 'material-ui/Checkbox';
import TextField from 'material-ui/TextField';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import DatePicker from 'material-ui/DatePicker';
import TimePicker from 'material-ui/TimePicker';
import { IconButton } from 'common-components/lib/components/IconButton.js'

import { Row, Col } from 'react-bootstrap';

export class FieldInput extends React.Component {

  render() {
    return this.createInput(
      this.props.field.dataType,
      this.props.field.name,
      this.props.field.key,
      this.props.field.value,
      this.props.onChange
    );
  }

  createInput(type, name, id, value, onChange) {
    switch (type) {
      case 'ENUMERATION_LIST_TYPE':
        return (
          <SelectField
            floatingLabelText={this.props.showLabels ? name : null}
            fullWidth={true}
            multiple={true}
            value={value}
            onChange={(event, index, values) => onChange({target: {id: id, value: values}})}
          >
            <MenuItem value={0} primaryText="Option 1" />
            <MenuItem value={1} primaryText="Option 2" />
            <MenuItem value={2} primaryText="Option 3" />
          </SelectField>
        )
      case 'ENUMERATION_TYPE':
        return (
          <SelectField
            floatingLabelText={this.props.showLabels ? name : null}
            fullWidth={true}
            value={value}
            onChange={(event, value) => onChange({target: {id: id, value: value}})}
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
            id={id}
            fullWidth={true}
            floatingLabelText={this.props.showLabels ? name : null}
            value={value}
            onChange={(event, value) => onChange({target: {id: id, value: value}})}
          />
        );
      case 'FLOAT_TYPE':
      case 'DOUBLE_TYPE':
      case 'LONG_TYPE':
        return (
          <TextField
            id={id}
            type='number'
            step='0.01'
            fullWidth={true}
            floatingLabelText={this.props.showLabels ? name : null}
            value={value}
            onChange={(event, value) => onChange({target: {id: id, value: value}})}
          />
        );
      case 'INTEGER_TYPE':
        return (
          <TextField
            id={id}
            type='number'
            fullWidth={true}
            floatingLabelText={this.props.showLabels ? name : null}
            value={value}
            onChange={(event, value) => onChange({target: {id: id, value: value}})}
          />
        );
      case 'BOOLEAN_TYPE':
        return (
          <Checkbox
            id={id}
            label={this.props.showLabels ? name : null}
            checked={value}
            onCheck={(event, checked) => onChange({target: {id: id, value: checked}})}
          />
        );
      case 'DATE_TIME_TYPE':
        return (
          <Row>
            <Col xs={6}>
              <DatePicker
                id={id + 'date'}
                floatingLabelText={this.props.showLabels ? name + " Date" : null}
                mode="landscape"
              />
            </Col>
            <Col xs={6}>
              <TimePicker
                id={id + 'time'}
                floatingLabelText={this.props.showLabels ? "Time" : null}
                format="24hr"
              />
            </Col>
        </Row>
        );
      case 'BINARY_TYPE':
      case 'FILE_TYPE':
        return (
          <div>
            <IconButton id={id} icon="/images/svg/load.svg" title={"Download " + name} onClick={null} /> {this.props.showLabel ? <label>Download {name}</label> : null}
          </div>
        );
      case 'GEOCOORDINATE_TYPE':
        return (
            <Row>
              <Col xs={6}>
                <TextField
                  id={id + 'lat'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? name + " Latitude" : null}
                  fullWidth={false}
                  onChange={(event, value) => onChange({target: {id: id, value: value}})}
                />
              </Col>
              <Col xs={6}>
                <TextField
                  id={id + 'long'}
                  type='number'
                  step='0.01'
                  floatingLabelText={this.props.showLabels ? "Longitude" : null}
                  fullWidth={false}
                  onChange={(event, value) => onChange({target: {id: id, value: value}})}
                />
              </Col>
          </Row>
        );
          /*
          <div className="row">
            <div className="form-group col-sm-2">
              <label for="{id + '_x'}" className="col-form-label">Lat</label>
              <input type="number" id={id + '_x'} className="form-control" value={value} step="0.01" />
            </div>
            <div className="form-group col-sm-2">
              <label for="{id + '_y'}" className="col-form-label">Long</label>
              <input type="number" id={id + '_y'} className="form-control" value={value} step="0.01" />
            </div>
          </div>
        )
        */
      case 'CIRCLE_TYPE':
        return (
          <Row>
            <Col xs={4}>
              <TextField
                id={id + 'lat'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? name + " Latitude" : null}
                fullWidth={false}
                onChange={(event, value) => onChange({target: {id: id, value: value}})}
              />
            </Col>
            <Col xs={4}>
              <TextField
                id={id + 'long'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? "Longitude" : null}
                fullWidth={false}
                onChange={(event, value) => onChange({target: {id: id, value: value}})}
              />
            </Col>
            <Col xs={4}>
              <TextField
                id={id + 'radius'}
                type='number'
                step='0.01'
                floatingLabelText={this.props.showLabels ? "Radius" : null}
                fullWidth={false}
                onChange={(event, value) => onChange({target: {id: id, value: value}})}
              />
            </Col>
          </Row>
      );
      /*
          <div className="row">
            <div className="form-group col-sm-2">
              <label for="{id + '_x'}" className="col-form-label">Lat</label>
              <input type="number" id={id + '_x'} className="form-control" value={value} step="0.01" />
            </div>
            <div className="form-group col-sm-2">
              <label for="{id + '_y'}" className="col-form-label">Long</label>
              <input type="number" id={id + '_y'} className="form-control" value={value} step="0.01" />
            </div>
            <div className="form-group col-sm-2">
              <label for="{id + '_radius'}" className="col-form-label">Radius</label>
              <input type="number" id={id + '_radius'} className="form-control" value={value} step="0.01"/>
            </div>
          </div>
        )
        */
      case 'GUID_TYPE':
        return (
          <TextField
            id={id}
            fullWidth={true}
            floatingLabelText={this.props.showLabels ? name : null}
            inputProps={{ pattern: "[a-z]" }}
            onChange={(event, value) => onChange({target: {id: id, value: value}})}
          />
        );
      default:
        return (
          <TextField
            id={id}
            fullWidth={true}
            floatingLabelText={this.props.showLabels ? name : null}
            disabled
            onChange={(event, value) => onChange({target: {id: id, value: value}})}
          />
        );
    }
  }

}

FieldInput.defaultProps = {
  showLabels: true
}
