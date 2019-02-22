import React from 'react';
import FieldInput from 'common-components/lib/components/FieldInput';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TextField from '@material-ui/core/TextField';
import Paper from '@material-ui/core/Paper';

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
      name: props.name,
      recName: props.recName,
      recordSet: props.recordSet,
      tKey: props.tKey,
      json: {},
      split: props.split,
    };
    this.handleChange = this.handleChange.bind(this)

  }

  handleChange(value) {
    this.props.onChange(this.props.index, this.state.json)
  }

  render() {

    const {name, recName, recordSet, json} = this.state;
    const {split, index} = this.props;

    const enume = split.map(function(value, index) {
      return(
        {enum: value, label: value}
      )
    })


    const that = this;

    return (
          <Paper elevation={1}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>{name+'.'+recName}</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {
                  recordSet.definition.map(function(field, i) {
                    return <TableRow>
                            <FieldInput label={field.name} onChange={that.handleChange} dataType="ENUMERATION_TYPE" field={that.state.json} options={enume} attr={`${i}`}/>
                          </TableRow>
                  })
                }
              </TableBody>
            </Table>
          </Paper>
    )
  }
}
