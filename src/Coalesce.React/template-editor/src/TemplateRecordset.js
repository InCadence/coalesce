import React, { Component } from 'react';
import {Tabs, Tab} from 'material-ui/Tabs';
import {List, ListItem} from 'material-ui/List';
import Checkbox from 'material-ui/Checkbox';
import ReactTable from 'react-table';
import {
  Row, Col
} from 'react-bootstrap';
import TextField from 'material-ui/TextField';

import { Definitions } from './TemplateDefinitions'

export class RecordSet extends Component {

  constructor(props) {
    super(props);
    this.handleClick = this.handleClick.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleEditToggle = this.handleEditToggle.bind(this);

    this.state = {
      recordset: props.data,
      open: false,
      edit: false,
    };
  }

  handleClick() {
    this.setState({ open: !this.state.open })
  }

  handleChange(attr, value) {
    const { recordset } = this.state;
    recordset[attr] = value;
    this.setState({ recordset: recordset })
  }

  handleEditToggle(e) {
    this.setState({ edit: !this.state.edit })
  }

  render() {
    const { recordset } = this.state;

    console.log(JSON.stringify(recordset));

    return (
      <div className="ui-widget-content" style={{'overflowY': 'auto', 'overflowX': 'hidden','maxHeight': '500px'}}>
        <Row>
          <Col xs={4}>
            <Checkbox
              label="Singleton"
              checked={recordset.minRecords === 1 && recordset.maxRecords === 1}
              onCheck={(event, checked) => {
                this.handleChange("minRecords", checked ? 1 : 0);
                this.handleChange("maxRecords", checked ? 1 : 0);
              }}
            />
          </Col>
          <Col xs={4} style={{'text-align': 'right'}}>
            Min / Max
          </Col>
          <Col xs={2}>
            <TextField
              type='number'
              fullWidth={true}
              underlineShow={false}
              style={{height: '16px'}}
              value={recordset.minRecords}
              onChange={(event, value) => this.handleChange("minRecords", value)}
            />
          </Col>
          <Col xs={2}>
            <TextField
              type='number'
              fullWidth={true}
              underlineShow={false}
              style={{height: '16px'}}
              value={recordset.maxRecords}
              onChange={(event, value) => this.handleChange("maxRecords", value)}
            />
          </Col>
        </Row>
        <Definitions data={recordset.fieldDefinitions} />
      </div>
    );
  }
}
