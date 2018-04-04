import React from 'react';
import ReactTable from 'react-table'
import { ReactTableDefaults } from 'react-table'
import {FieldInput} from './FieldInput.js'
import { IconButton } from 'common-components/lib/components/IconButton.js'
import uuid from 'uuid';

Object.assign(ReactTableDefaults, {
  defaultPageSize: 5,
  minRows: 3,
  // etc...
})

const status_enum = [{enum: 'ACTIVE', label: "Active"}, {enum: 'READONLY', label: 'Read Only'}, {enum: 'DELETED', label: 'Deleted'}];

export class RecordsetView extends React.Component
{
  constructor(props) {
    super(props);
    this.state = {
      data: props.data
    };
    this.createRow = this.createRow.bind(this);
    this.toggleShowAll = this.toggleShowAll.bind(this);
  }

  componentWillReceiveProps(nextProps) {
    this.setState({
      data: nextProps.data
    })
  }

  render() {
    const {data, showAll} = this.state;
    const { recordset } = this.props;

    var that = this;
    var columns = [{Header: 'key', accessor: 'key', show: false}];

    // Buttons Creation
    var buttons = {};
    buttons['Header'] = 'Status';
    buttons['accessor'] = 'record';
    buttons['sortable'] = false;
    buttons['Cell'] = (cell) => (
      // TODO Pull options from an enumeration
      <FieldInput
        field={cell.value}
        dataType="ENUMERATION_TYPE"
        attr="status"
        options={status_enum}
        showLabels={false}
      />
    )

    columns.push(buttons);

    // Create Colunms
    recordset.fieldDefinitions.forEach(function(fd) {
      columns.push(that.createHeader(fd));
    });

    var tabledata = [];

    // Populate Data
    if (data != null && data.allRecords != null)
    {
      data.allRecords.forEach(function(record) {

        console.log(record.status);

        //if (showAll || record.status !== 'DELETED') {
          tabledata.push(that.createDataRow(record, recordset.fieldDefinitions));
        //}
      });
    }

    var label = recordset.name.toProperCase() + " Recordset";
    return (
      <div id={recordset.key} key={recordset.key} className="ui-widget-content section">
            <ReactTable
              data={tabledata}
              columns={columns}
              className="-striped -highlight"
            />
          <div className='form-buttons'>
            <input type='checkbox'  onClick={this.toggleShowAll} />
            <label>Show All</label>
            <IconButton icon="/images/svg/add.svg" title="Add Record" onClick={this.createRow} />
          </div>
        </div>
    )
  }

  createHeader(definition) {
    var col = {};

    col['Header'] = definition.name;
    col['accessor'] = definition.name;
    col['sortable'] = false;

    if (definition.status !== "READONLY") {
      col['Cell'] = (cell) => (
        <FieldInput
          field={cell.value}
          showLabels={false}
        />
      );
    }

    return col;
  }

  createDataRow(record, fieldDefinitions) {
    var row = {
      record: record
    };

    fieldDefinitions.forEach(function(fd) {

      for (var ii=0; ii<record.fields.length; ii++) {
        if (record.fields[ii].name === fd.name) {
          row[fd.name] = record.fields[ii];
          break;
        }
      }

    });

    console.log(JSON.stringify(row));

    return row;
  }

  createRow(e) {

    const { data } = this.state;
    const { recordset } = this.props;

    var fields = [];

    recordset.fieldDefinitions.forEach(function(fd) {

      var field = {};

      field = Object.assign({}, fd);

      field.value = fd.defaultValue;
      field.key = '';

      fields.push(field);
    });

    data.allRecords.push({
      key: uuid.v4(),
      status: 'ACTIVE',
      name: recordset.name + ' Record',
      type: 'record',
      fields: fields
    });

    console.log(data.allRecords[data.allRecords.length -1].key);

    this.setState({
      data: data,
      showAll: false
    });
  }

  changeStatus(recordkey, value) {

    const {data} = this.state;

console.log(value);
    data.allRecords.forEach(function (record) {
      if (record.key === recordkey) {
        record.status = value;
      }
    })

    this.setState({
      data: data
    })

  }

  toggleShowAll(e) {
    this.setState({showAll: e.target.checked})
  }

}
