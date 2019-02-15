import React from 'react';
import Menu from 'common-components/lib/components/menu';
import { loadTemplates, loadTemplate } from 'common-components/lib/js/templateController.js';
import { getRootKarafUrl } from 'common-components/lib/js/common';
import { DialogMessage, DialogLoader, DialogOptions } from 'common-components/lib/components/dialogs'
import { searchComplex } from 'common-components/lib/js/searchController.js';
import Button from '@material-ui/core/Button';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import uuid from 'uuid';

import {SearchResults} from './results.js'

const karafRootAddr = getRootKarafUrl();
const DEFAULT = 'CoalesceEntity';
// {
//   "templateUri": "file:///src/test/resources/0d75e8ca-204f-3d20-a03d-7e43a889e93f",
//   "record": {
//     "name": "test1",
//     "fields": {
//       "0": "int",
//       "1": "boolean",
//       "2": "double"}
//     }
// }

export default class Template extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      usedCsv: [],
      name: props.name,
      recName: props.recName,
      recordSet: props.recordSet,
      tKey: props.tKey,
      json: {},
      split: props.split,
    };


  }

  componentDidMount() {

  }

  render() {

    const {split, name, recName} = this.state;

    return (
        <div style={{paddingTop: 25}}>
          <Paper elevation={1}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>{name+'.'+recName}</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
              <Select>
                {split.map(function(value, index) {
                  return(
                    <MenuItem value={value}>{value}</MenuItem>
                  )
              })}
              </Select>
              
              </TableBody>
            </Table>
          </Paper>
        </div>
    )
  }
}

function getRecordsets(section) {

  var results = [];

  section.sectionsAsList.forEach(function(section) {
    results = results.concat(getRecordsets(section));
  });

  // Render Recordsets
  section.recordsetsAsList.forEach(function(recordset) {
    results.push({name: recordset.name, definition: recordset.fieldDefinitions});
  });

  return results;
}
