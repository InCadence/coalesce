import React from 'react';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import FieldInput from 'common-components/lib/components/FieldInput';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TextField from '@material-ui/core/TextField';
import Paper from '@material-ui/core/Paper';


export default class Linkage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {

    };
    const created = "created"

    this.linkTypes = [];
    this.linkTypes.push({enum: created, label: created}, {enum: "test", label: "test"});
    this.onChange = this.onChange.bind(this);
  }

  onChange(value) {
    this.props.onChange(value, this.props.field)
  }

  render() {
    const {name, field, index, options} = this.props;
    return (
      <Paper elevation={1}>
        <ExpansionPanel>
          <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
            {name}
          </ExpansionPanelSummary>
          <ExpansionPanelDetails>
            <FieldInput label={"Link Type"} dataType="ENUMERATION_TYPE" field={field} options={this.linkTypes} attr={"linkType"}/>
            <FieldInput label={"Entity 2"} dataType="ENUMERATION_TYPE" onChange={this.onChange} field={field} options={options} attr={"entity2"}/>
          </ExpansionPanelDetails>
        </ExpansionPanel>
      </Paper>
    )
  }
}
