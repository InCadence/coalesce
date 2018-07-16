import React from 'react';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import TextField from '@material-ui/core/TextField';

import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Typography from '@material-ui/core/Typography';
import 'common-components/css/mapping.css';


export default class PointsTable extends React.Component {

  constructor(props) {
    super(props);
    this.id = 0;
    this.state = {
      feature: this.props.feature,
      coordsHashmap: this.initCoordsHashmap(),
      values: this.props.values || {}
    }

    this.handleInputFocus = this.handleInputFocus.bind(this);
    this.handleInputXY = this.handleInputXY.bind(this);
    this.handleOnBlur = this.handleOnBlur.bind(this);
    this.handleInputZ = this.handleInputZ.bind(this);
    this.handleOnBlurZ = this.handleOnBlurZ.bind(this);
  }

  initCoordsHashmap() {
    var coordsHashmap = this.props.coordsHashmap

    if (coordsHashmap.size == 0) {
      if (this.props.feature) {
        if (this.props.shape == 'Circle') {

          var center = this.props.feature.getGeometry().getCenter()
          var z = coordsHashmap.get(center) || 0
          coordsHashmap.set(center, z)
        }
        else if (this.props.shape == 'POINT') {
          var point = this.props.feature.getGeometry().getCoordinates()
          var z = coordsHashmap.get(point) || 0

          coordsHashmap.set(point, z)
        }
        else {
          var coordinates = this.getCoordinates()
          for (let i = 0; i < coordinates.length; i++) {
            var xy = coordinates[i] //array of format '[x, y]'
            var z = coordsHashmap.get(xy) || 0

            coordsHashmap.set(xy, z)
          }
        }

      }
    }

    return coordsHashmap;
  }

  getCoordinates() {
    var coordinates = this.props.feature.getGeometry().getCoordinates()
    if (this.props.shape == 'Polygon') {
      coordinates = coordinates[0]
    }
    return coordinates;
  }

  createTables()
  {

    var tables = []
    tables.push(
      <ExpansionPanel>
        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
          <Typography>Feature Coordinates</Typography>
        </ExpansionPanelSummary>
        <ExpansionPanelDetails>
          <Paper>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>X</TableCell>
                  <TableCell>Y</TableCell>
                  <TableCell>Z</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {this.createTableRows(this.props.feature)}
              </TableBody>
            </Table>
          </Paper>
        </ExpansionPanelDetails>
      </ExpansionPanel>
    )
    return tables;
  }

  createTableRows(feature) {
    //only attempt to create rows if a feature exists
    if(!feature) {
      return null;
    }

    var rows = [];

    if (this.props.shape == 'Circle') {
      var center = feature.getGeometry().getCenter()
      rows.push(this.createSingleRow(center))
    }
    else if (this.props.shape == 'POINT') {
      var point = feature.getGeometry().getCoordinates()
      rows.push(this.createSingleRow(point))
    }
    else {
      var coordinates = this.getCoordinates()
      var numCoordinates = coordinates.length
      if (this.props.shape == 'Polygon') {
        numCoordinates -= 1;
      }
      for (let j = 0; j < numCoordinates; j++) {

        var index = j
        var coords = coordinates[j]

        var x = coords[0];
        var y = coords[1];
        rows.push(
          <TableRow>
            <TableCell> {this.createTextField(x, this.handleInputFocus, this.handleInputXY, this.handleOnBlur, 'x' + index)} </TableCell>
            <TableCell> {this.createTextField(y, this.handleInputFocus, this.handleInputXY, this.handleOnBlur, 'y' + index)} </TableCell>
            <TableCell> {this.createTextField(this.props.coordsHashmap.get(coords), this.handleInputFocus, this.handleInputZ, this.handleOnBlurZ, 'z' + index)} </TableCell>
          </TableRow>
        )
      }
    }
    return rows;

  }

  createSingleRow(point) {

    var row = (
      <TableRow>
        <TableCell> {this.createTextField(point[0], this.handleInputFocus, this.handleInputXY, this.handleOnBlur, 'x0')} </TableCell>
        <TableCell> {this.createTextField(point[1], this.handleInputFocus, this.handleInputXY, this.handleOnBlur, 'y0')} </TableCell>
        <TableCell> {this.createTextField(this.props.coordsHashmap.get(point), this.handleInputFocus, this.handleInputZ, this.handleOnBlurZ, 'z0')} </TableCell>
      </TableRow>
    )
    return row;
  }

  createTextField(value, onInputFocus, onInputChange, onInputBlur, index) {
    const styles = {
      input: {
        fontSize: '1em',
        fontWeight: '600',
      }
    }
    var defaultValue = 0
    if (Object.keys(this.state.values).length == 0)
    {
      //if there is no input
      defaultValue = value
    }
    else {
      //if there is input, value = text input
      defaultValue = this.state.values[index]
    }

    return <TextField
      id={index}
      className="coordinateInput"
      onFocus={onInputFocus}
      onChange={(e) => onInputChange(e, index)}
      onBlur={(e) => onInputBlur(this, e, index)}
      style={this.props.textStyle}
      value={defaultValue}
    />
  }

  handleInputFocus(evt) {
    this.setState({safeValue: evt.target.value})
  }

  handleInputXY(evt, index) {
    this.state.values[index] = evt.target.value;
    this.setState({values: this.state.values})
    // var inputElem = document.getElementById(index)
    // inputElem.value = evt.target.value
  }

  handleOnBlur(self, evt, index) {
    const NOTHING = ''
    var inputValue = evt.target.value
    if (inputValue == NOTHING) {
      inputValue = 0;
    }
    //this ensures it doesn't update when the user didn't even change anything
    if(inputValue != self.state.safeValue && !isNaN(self.state.safeValue)) {
      self.props.updateFeature(self.state.values, this.state.coordsHashmap, index)
    }
    else {
      self.state.values[index] = self.state.safeValue
      self.setState({values: self.state.values})
    }
  }

  handleInputZ(evt, index) {
    //currently the same as handleInputXY function

    this.state.values[index] = evt.target.value

    this.setState({values: this.state.values})
    // var inputElem = document.getElementById(index)
    // inputElem.value = evt.target.value
  }

  handleOnBlurZ(self, evt, index) {
    //currently the same as handleOnBlur function
    const NOTHING = ''
    var inputValue = evt.target.value
    if (inputValue == NOTHING) {
      inputValue = 0;
    }
    //this ensures it doesn't update when the user didn't even change anything
    if(inputValue != self.state.safeValue && !isNaN(self.state.safeValue)) {
      self.props.updateFeature(self.state.values, this.state.coordsHashmap, index)
    }
    else {
      self.state.values[index] = self.state.safeValue
      self.setState({values: self.state.values})
    }
  }

  render() {
    return (
      <div>
        {this.createTables()}
      </div>
    );
  }
}
