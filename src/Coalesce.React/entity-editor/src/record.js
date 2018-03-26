import React from 'react';
import {FieldInput} from './FieldInput.js'

export class  RecordView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {record: props.record};
    this.handleChange = this.handleChange.bind(this);
  }

  render() {
    const {record} = this.state;

    return (
      <div id={record.key} key={record.key} className="ui-widget">
        <div className="ui-widget-content">
          {this.props.definition.map(this.renderField.bind(this, record))}
        </div>
      </div>
    );

  }

  renderField(record, fd) {

    var field = this.getField(record, fd.name);
    field.dataType = fd.dataType;

    return (
      <FieldInput field={field} onChange={this.handleChange} />
    )

  }

  getField(record, name) {
    var result;

    for (var ii=0; ii<record.fields.length; ii++) {
      if (record.fields[ii].name === name) {
        result = record.fields[ii];
        break;
      }
    }

    return result;
  }

  getFieldByKey(record, key) {
    var result;

    for (var ii=0; ii<record.fields.length; ii++) {
      if (record.fields[ii].key === key) {
        result = record.fields[ii];
        break;
      }
    }

    return result;
  }

  handleChange (e){
    const value = e.target.value;
    const record = this.state.record;
    const field = this.getFieldByKey(record, e.target.id)

    console.log(e.target.id + " = " + e.target.value);

    field.value = value;
    this.setState({
      record: record
    });
  }
}
