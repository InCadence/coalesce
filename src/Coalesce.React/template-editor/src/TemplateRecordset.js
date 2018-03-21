import React, { Component } from 'react';
import Checkbox from 'material-ui/Checkbox';
import {
  Row, Col
} from 'react-bootstrap';
import TextField from 'material-ui/TextField';

import { Definitions } from './TemplateDefinitions'

export class RecordSet extends Component {

  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
    this.handleDeleteDefinition = this.handleDeleteDefinition.bind(this);

    this.state = {
      recordset: props.data,
    };
  }

  handleChange(attr, value) {
    const { recordset } = this.state;
    recordset[attr] = value;
    this.setState({ recordset: recordset })
  }

  handleDeleteDefinition(key) {
    const { recordset } = this.state;

    for (var ii=0; ii<recordset.fieldDefinitions.length; ii++) {
      if (recordset.fieldDefinitions[ii].key === key) {
        recordset.fieldDefinitions.splice(ii, 1);
      }
    }

    this.setState({ recordset: recordset })
  }

  render() {
    const { recordset } = this.state;

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
          <Col xs={4} style={{'textAlign': 'right'}}>
            Min / Max
          </Col>
          <Col xs={2}>
            <TextField
              id={recordset.key + "_min"}
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
              id={recordset.key + "_max"}
              type='number'
              fullWidth={true}
              underlineShow={false}
              style={{height: '16px'}}
              value={recordset.maxRecords}
              onChange={(event, value) => this.handleChange("maxRecords", value)}
            />
          </Col>
        </Row>
        <Definitions data={recordset.fieldDefinitions} onDelete={this.handleDeleteDefinition} />
      </div>
    );
  }
}
