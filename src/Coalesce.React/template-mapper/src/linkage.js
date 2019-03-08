import React from 'react';
import Button from '@material-ui/core/Button';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelActions from '@material-ui/core/ExpansionPanelActions';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import FieldInput from 'common-components/lib/components/FieldInput';
import Paper from '@material-ui/core/Paper';


export default class Linkage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      name: props.name
    };
    const created = "created"

    this.linkTypes = [];
    this.linkTypes.push({enum: created, label: created}, {enum: "test", label: "test"});
    this.onChange = this.onChange.bind(this);
    this.handleDelete = this.handleDelete.bind(this);
  }

  onChange(value) {
    var parentState = this.props.parent.state;
    var name = parentState.cache[parentState.templateKeys[parseInt(value)]].name;
    this.setState({name: name});
  }

  handleDelete() {
    this.props.handleDelete(this.props.index);
  }

  render() {
    const {field, options} = this.props;
    const {name} = this.state;

    const that = this;
    return (
      <Paper elevation={1}>
        <ExpansionPanel>
          <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
            {name}
          </ExpansionPanelSummary>
          <ExpansionPanelDetails>
            <FieldInput label={"Entity 1"} dataType="ENUMERATION_TYPE" onChange={that.onChange} field={field} options={options} attr={"entity1"}/>
            <FieldInput label={"Link Type"} dataType="ENUMERATION_TYPE" field={field} options={this.linkTypes} attr={"linkType"}/>
            <FieldInput label={"Entity 2"} dataType="ENUMERATION_TYPE" field={field} options={options} attr={"entity2"}/>
          </ExpansionPanelDetails>
            <ExpansionPanelActions>
              <Button size="small" color="secondary" onClick={that.handleDelete}>Delete</Button>
            </ExpansionPanelActions>
        </ExpansionPanel>
      </Paper>
    )
  }
}
