import React from "react";
import {withTheme} from "@material-ui/core/styles";
import Checkbox from "@material-ui/core/Checkbox";
import TextField from "@material-ui/core/TextField";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import {Row, Col} from "react-bootstrap";

import {Definitions} from "./TemplateDefinitions";

class RecordSet extends React.Component {
  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
    this.handleDeleteDefinition = this.handleDeleteDefinition.bind(this);

    this.state = {
      recordset: props.data
    };
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.data.key !== this.props.data.key) {
      this.setState(() => {return {
        recordset: nextProps.data,
        tabIndex: 0,
        edit: false
      }})
    }
  }

  handleChange(attr, value) {
    const {recordset} = this.state;
    recordset[attr] = value;
    this.setState({recordset: recordset});
  }

  handleDeleteDefinition(key) {
    const {recordset} = this.state;

    for (var ii = 0; ii < recordset.fieldDefinitions.length; ii++) {
      if (recordset.fieldDefinitions[ii].key === key) {
        recordset.fieldDefinitions.splice(ii, 1);
      }
    }

    this.setState({recordset: recordset});
  }

  render() {
    const {recordset} = this.state;

    return (
      <div>
        <Row>
          <Col xs={4}>
            <FormControlLabel
              label="Singleton"
              control={
                <Checkbox
                  checked={
                    recordset.minRecords === 1 && recordset.maxRecords === 1
                  }
                  onChange={event => {
                    this.handleChange(
                      "minRecords",
                      event.target.checked ? 1 : 0
                    );
                    this.handleChange(
                      "maxRecords",
                      event.target.checked ? 1 : 0
                    );
                  }}
                />
              }
            />
          </Col>
          <Col xs={4}>
            <TextField
              id={recordset.key + "_min"}
              label="min"
              type="number"
              fullWidth
              value={recordset.minRecords}
              onChange={event =>
                this.handleChange("minRecords", event.target.value)
              }
            />
          </Col>
          <Col xs={4}>
            <TextField
              id={recordset.key + "_max"}
              label="max"
              type="number"
              fullWidth
              value={recordset.maxRecords}
              onChange={event =>
                this.handleChange("maxRecords", event.target.value)
              }
            />
          </Col>
        </Row>
        <Definitions
          data={recordset.fieldDefinitions}
          onDelete={this.handleDeleteDefinition}
        />
      </div>
    );
  }
}

export default withTheme()(RecordSet);
