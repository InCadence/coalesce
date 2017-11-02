import React from 'react';
import ReactTable from 'react-table'
import { ReactTableDefaults } from 'react-table'
import {Toggle} from 'common-components/lib/toggle.js'
import {Collapse} from 'react-collapse';

Object.assign(ReactTableDefaults, {
  defaultPageSize: 5,
  minRows: 3,
  // etc...
})

export class RecordsetView extends React.Component
{
  constructor(props) {
    super(props);
    this.state = props;
  }

  render() {
    const {recordset, data, isOpened, showAll} = this.state;

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
        <select onChange={that.changeStatus.bind(that, cell.row.key)} value={cell.row.status}>
          <option value='ACTIVE'>Active</option>
          <option value='READONLY'>Readonly</option>
          <option value='DELETED'>Deleted</option>
        </select>
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

    var label = recordset.name.toProperCase() + " Recordset";
    return (
      <div id={recordset.key} key={recordset.key} className="ui-widget">
        <Toggle
          ontext={label}
          offtext={label}
          isToggleOn={isOpened}
          onToggle={(value) => {
            this.setState({isOpened: value});
          }}
          />
          <Collapse className="ui-widget-content" isOpened={isOpened}>
            <ReactTable
              data={tabledata}
              columns={columns}
            />
          </Collapse>
          <div className='form-buttons'>
            <input type='checkbox'  onClick={this.toggleShowAll.bind(that)} />
            <label>Show All</label>
            <button id="add" className="mm-popup__btn mm-popup__btn--cancel" title="Add" onClick={this.createRow.bind(that)}>
              Add
            </button>
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
          <input style={{width:'100%'}} value={cell.value} onChange={that.handleChange.bind(that, cell.row.key, cell.column.Header)}/>
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

  changeStatus(recordkey, e) {

    const {data} = this.state;

    data.allRecords.forEach(function (record) {
      if (record.key === recordkey) {
        record.status = e.target.value;
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
  isOpened: true,
  newIdx: 0
}

export class RecordView extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;

    this.handleChange = this.handleChange.bind(this);
  }

  render() {
    const {record, definition, isOpened} = this.state;

    var fields = [];

    var that = this;
    definition.forEach(function(fd) {

      var field = that.getField(record, fd.name);

      fields.push(
        <div key={field.key} className="row">
          <label className="col-sm-2 col-form-label">{fd.name}</label>
          <div className="col-sm-6">
            <input id={field.key} className="form-control" value={field.value}  onChange={that.handleChange}/>
          </div>
        </div>
      )

    });

    return (
      <div id={record.key} key={record.key} className="ui-widget">
        <Toggle
          ontext={record.name}
          offtext={record.name}
          isToggleOn={isOpened}
          onToggle={(value) => {
            this.setState({isOpened: value});
          }}
          />
          <Collapse className="ui-widget-content" isOpened={isOpened}>
          {fields}
          </Collapse>
      </div>
    );

  }
/*
  <Accordion objectkey={record.key} label={record.name}>
    {fields}
  </Accordion>
*/
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

    field.value = value;
    this.setState({record: record});
  }
}

RecordView.defaultProps = {
  isOpened: true
}

String.prototype.toProperCase = function () {
    return this.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
};
