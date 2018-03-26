import React from 'react';
import ReactTable from 'react-table'
import { ReactTableDefaults } from 'react-table'
import {FieldInput} from './FieldInput.js'
import { IconButton } from 'common-components/lib/components/IconButton.js'
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';

Object.assign(ReactTableDefaults, {
  defaultPageSize: 5,
  minRows: 3,
  // etc...
})

const status_enum = ['ACTIVE', 'READONLY', 'DELETED'];

export class RecordsetView extends React.Component
{
  constructor(props) {
    super(props);
    this.state = props;
    this.createRow = this.createRow.bind(this);
    this.toggleShowAll = this.toggleShowAll.bind(this);
  }

  render() {
    const {recordset, data, showAll} = this.state;

    var that = this;
    var columns = [{Header: 'key', accessor: 'key', show: false}];

    // Buttons Creation
    var buttons = {};
    buttons['Header'] = 'Status';
    buttons['accessor'] = 'status';
    buttons['resizable'] = false;
    buttons['sortable'] = false;
    buttons['Cell'] = (cell) => {
      // TODO Pull o  ptions from an enumeration
      return (
        <SelectField
          value={cell.row.status}
          fullWidth={true}
          onChange={(event, key, payload) => that.changeStatus(cell.row.key, key)}
        >
          <MenuItem value={status_enum[0]} primaryText="Active" />
          <MenuItem value={status_enum[1]} primaryText="Readonly" />
          <MenuItem value={status_enum[2]} primaryText="Deleted" />
        </SelectField>
      )
    }

    columns.push(buttons);

    // Create Colunms
    recordset.fieldDefinitions.forEach(function(fd) {
      columns.push(that.createHeader(fd, that));
    });

    var tabledata = [];

    // Populate Data
    if (data != null && data.allRecords != null)
    {
      data.allRecords.forEach(function(record) {
        if (showAll || record.status !== 'DELETED') {
          tabledata.push(that.createDataRow(record, recordset.fieldDefinitions));
        }
      });
    }

/*
<Accordion id={recordset.key} key={recordset.key} label={label}>
  <ReactTable
    data={tabledata}
    columns={columns}
  />
  <div className='form-buttons'>
    <input type='checkbox'  onClick={this.toggleShowAll} />
    <label>Show All</label>
    <img src={require('common-components/img/add.ico')} alt="Add" title="Add Row" className="coalesce-img-button enabled" onClick={this.createRow}/>
  </div>
</Accordion>
*/

    var label = recordset.name.toProperCase() + " Recordset";
    return (
      <div id={recordset.key} key={recordset.key} className="ui-widget">
          <div className="ui-widget-content">
              <ReactTable
                data={tabledata}
                columns={columns}
              />
            <div className='form-buttons'>
              <input type='checkbox'  onClick={this.toggleShowAll} />
              <label>Show All</label>
              <IconButton icon="/images/svg/add.svg" title="Add Record" onClick={this.createRow} />
            </div>
          </div>
      </div>
    )
  }

  createHeader(definition, that) {
    var col = {};

    col['Header'] = definition.name;
    col['accessor'] = definition.name;

    if (definition.status !== "READONLY") {
      col['Cell'] = (cell) => (
        <FieldInput field={{dataType: definition.dataType, name: definition.name, key: definition.key, value: cell.value}} onChange={that.handleChange.bind(that, cell.row.key, cell.column.Header)} showLabels={false} />
        //  <input className="form-control" value={cell.value} onChange={that.handleChange.bind(that, cell.row.key, cell.column.Header)}/>
      );
    }

    return col;
  }

  createDataRow(record, fieldDefinitions) {
    var row = {
      key: record.key,
      status: record.status
    };

    fieldDefinitions.forEach(function(fd) {

      for (var ii=0; ii<record.fields.length; ii++) {
        if (record.fields[ii].name === fd.name) {

          if (record.fields[ii].value == null) {
            row[fd.name] = '';
          } else {
            row[fd.name] = record.fields[ii].value;
          }
          break;
        }
      }

    });

    return row;
  }

  createRow(e) {

    const {recordset, data, newIdx} = this.state;

    var fields = [];

    recordset.fieldDefinitions.forEach(function(fd) {

      var field = {};

      field = Object.assign({}, fd);

      field.value = fd.defaultValue;
      field.key = '';

      fields.push(field);
    });

    data.allRecords.push({
      key: newIdx,
      status: 'ACTIVE',
      name: recordset.name + ' Record',
      type: 'record',
      fields: fields
    });

    console.log(data.allRecords[data.allRecords.length -1].key);

    this.setState({
      data: data,
      newIdx: newIdx + 1,
      showAll: false
    });
  }

  changeStatus(recordkey, value) {

    const {data} = this.state;

    data.allRecords.forEach(function (record) {
      if (record.key === recordkey) {
        record.status = status_enum[value];
      }
    })

    this.setState({
      data: data
    })

  }

  toggleShowAll(e) {
    this.setState({showAll: e.target.checked})
  }

  handleChange (recordkey, fieldname, e){
    const value = e.target.value;
    const recordset = this.state.data;

    for (var ii=0; ii<recordset.allRecords.length; ii++) {

      var record = recordset.allRecords[ii];

      if (record.key === recordkey) {

        for (var jj=0; jj<record.fields.length; jj++) {

          var field = record.fields[jj];

          if (field.name === fieldname) {
            field.value = value;
            this.setState({data: recordset});
            break;
          }
        }
        break;
      }
    }
  }

}

RecordsetView.defaultProps = {

  newIdx: 0
}
