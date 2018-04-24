import React from 'react'
import {Card, CardActions, CardHeader, CardText} from 'material-ui/Card';
import FlatButton from 'material-ui/FlatButton';
import {List, ListItem} from 'material-ui/List';
import TextField from 'material-ui/TextField';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import {FieldInput} from 'common-components/lib/components/FieldInput.js'
import { Row, Col } from 'react-bootstrap';

import {FilterCriteria} from './filtercriteria'
var parse = require('wellknown');

const operator_enum = [{enum: 'OR', label: "Or"}, {enum: 'AND', label: 'And'}];
const recordset_enum = [{enum: 'RS1', label: "Recordset 1"}, {enum: 'RS2', label: 'Recordset 2'}];

export class FilterGroup extends React.Component {

  constructor(props) {
    super(props);

    console.log(JSON.stringify(props.recordsets));

    this.state = {
      data: props.data
    }
  }

  render() {

    const { data } = this.state;

    console.log(JSON.stringify(data));

    return (
      <Card>
        <CardHeader
          title={
            <FieldInput
              field={data}
              dataType="ENUMERATION_TYPE"
              attr="operator"
              options={operator_enum}
              showLabels={false}
            />
          }
          actAsExpander={false}
          showExpandableButton={false}
        />
        <CardText expandable={false}>
          <List>
            {data.criteria != null && data.criteria.map((item) => {
              return (<FilterCriteria criteria={item} recordsets={this.props.recordsets}/>)
            })}
          </List>
          {data.group != null && data.group.map((item) => {
            return (<FilterGroup data={item} recordsets={this.props.recordsets}/>)
          })}
        </CardText>
        <CardActions>
          <FlatButton label="Add Field" />
          <FlatButton label="Add Group" />
        </CardActions>
      </Card>
    )
  }

}
