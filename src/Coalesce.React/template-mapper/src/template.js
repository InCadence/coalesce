import React from 'react';
import Button from '@material-ui/core/Button';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelActions from '@material-ui/core/ExpansionPanelActions';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import FieldInput from 'common-components/lib/components/FieldInput';
import Table from '@material-ui/core/Table';
import TableRow from '@material-ui/core/TableRow';
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
    this.handleDelete = this.handleDelete.bind(this);
  }

  handleChange(value) {
    this.props.onChange(this.props.index, this.state.json)
  }

  handleDelete() {
    this.props.handleDelete(this.props.index);
  }

  render() {

    const {name, recName, recordSet, field, split} = this.props;
    const enume = split.map(function(value, index) {
      return(
        {enum: value, label: value}
      )
    })

    const that = this;

    return (
          <Paper elevation={1}>
            <ExpansionPanel>
              <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                {name+'.'+recName}
              </ExpansionPanelSummary>
              <ExpansionPanelDetails>
                <Table>
                {
                  recordSet.definition.map(function(templateField, i) {
                    return <TableRow>
                            <FieldInput label={templateField.name} dataType="ENUMERATION_TYPE" field={field} options={enume} attr={`${i}`}/>
                           </TableRow>
                  })
                }
                </Table>
              </ExpansionPanelDetails>
              <ExpansionPanelActions>
                <Button size="small" color="secondary" onClick={that.handleDelete}>Delete</Button>
              </ExpansionPanelActions>
            </ExpansionPanel>
          </Paper>
    )
  }
}
