import React from 'react';
import ReactTable from 'react-table'
import { ReactTableDefaults } from 'react-table'
import {Toggle} from 'common-components/lib/toggle.js'
import {Collapse} from 'react-collapse';

import 'react-table/react-table.css'

Object.assign(ReactTableDefaults, {
  defaultPageSize: 5,
  minRows: 3,
  // etc...
})

export class FilterCreator extends React.Component {

  constructor(props) {
    super(props);

    if (props.tabledata != null) {
      for (var ii=0; ii<props.tabledata.length; ii++) {
        props.tabledata[ii].key = ii;
      }
    }

    this.state = props;
  }

  componentDidMount() {

    const {recordsets, tabledata} = this.state;

    if (tabledata.length == 0 && recordsets.length > 0) {

      tabledata.push({
        key: 0,
        recordset: recordsets[0].name,
        field: recordsets[0].definition[0].name,
        comparer: '=',
        value: '',
        matchCase: false});

      this.setState({
        tabledata: tabledata,
      });
    }

    this.setState({
      currentkey: (tabledata.length - 1)
    })
  }

  render() {

    const {recordsets, tabledata, isOpened} = this.state;

    return (
      <div className="ui-widget">
        <Toggle
          ontext="Search Criteria"
          offtext="Search Criteria"
          isToggleOn={true}
          onToggle={(value) => {
            this.setState({isOpened: value});
          }}
          />
          <Collapse isOpened={isOpened}>
            <div className="ui-widget-content">
              <ReactTable
                pageSize={this.props.maxRows}
                data={tabledata}
                showPagination={false}
                columns={createColumns(this, recordsets)}
              />
              <div className="form-buttons">
                <img src="/images/svg/add.svg" alt="Add" title="Add Criteria" className="coalesce-img-button enabled" onClick={this.addRow.bind(this)}/>
                <img src="/images/svg/search.svg" alt="Search" title="Execute Query" className="coalesce-img-button enabled" onClick={this.props.onSearch.bind(this, this.state.tabledata)}/>
              </div>
            </div>
          </Collapse>
      </div>
    )
  }

  onRecordsetChange(key, e) {

    const {tabledata, recordsets} = this.state;
    var row = this.getRow(tabledata, key);
    var defaultField;

    for (var ii=0; ii<recordsets.length; ii++) {
      if (recordsets[ii].name === e.target.value) {
        defaultField = recordsets[ii].definition[0].name;
        break;
      }
    };

    row.recordset = e.target.value;
    row.field = defaultField;
    row.comparer = '=';
    row.value = '';

    this.setState({tabledata: tabledata});
  }

  onFieldChange(key, e) {
    const {tabledata} = this.state;
    var row = this.getRow(tabledata, key);

    row.field = e.target.value;
    row.comparer = '=';
    row.value = '';

    this.setState({tabledata: tabledata});
  }

  onComparerChange(key, e) {
    const {tabledata} = this.state;
    var row = this.getRow(tabledata, key);

    row.comparer = e.target.value;

    this.setState({tabledata: tabledata});
  }

  onValueChange(key, e) {
    const {tabledata} = this.state;
    var row = this.getRow(tabledata, key);

    row.value = e.target.value;

    this.setState({tabledata: tabledata});
  }

  onMatchCaseChange(key, e) {
    const {tabledata} = this.state;
    var row = this.getRow(tabledata, key);

    row.matchCase = e.target.checked;

    this.setState({tabledata: tabledata});
  }

  deleteRow(key, e) {

    const {tabledata} = this.state;

    // Delete Row
    for (var ii=0; ii<tabledata.length; ii++) {
      if (tabledata[ii].key === key) {
          tabledata.splice(ii, 1);
          break;
      }
    }

    // Save State
    this.setState({tabledata: tabledata});
  }

  addRow(e) {
    const {tabledata, currentkey, recordsets} = this.state;

    var keyvalue = currentkey + 1;

    if (tabledata.length < this.props.maxRows) {
      // Create New Data Row
      tabledata.push({
        key: keyvalue,
        recordset: recordsets[0].name,
        field: recordsets[0].definition[0].name,
        comparer: '=',
        value: '',
        matchCase: false
      });

      // Save State
      this.setState({
        tabledata: tabledata,
        currentkey: keyvalue
      });
    } else {
      alert("Row limit reached");
    }
  }

  getRow(data, key) {
    var result;

    for (var ii=0; ii<data.length; ii++) {
      if (data[ii].key === key) {
          result = data[ii];
          break;
      }
    }

    return result;
  }
}



function createColumns(that, recordsets) {

  var columns = [{Header: 'key', accessor: 'key', show: false}];

  if (recordsets != null) {
    columns.push({
      Header: '',
      accessor: 'delete',
      resizable: false,
      sortable: false,
      width: 50,
      Cell: (cell) => (
        <img src="/images/svg/remove.svg" alt="Remove" title="Remove Criteria" className="coalesce-img-button enabled" onClick={that.deleteRow.bind(that, cell.row.key)}/>
      )
    });

    columns.push({
      Header: 'Recordset',
      accessor: 'recordset',
      resizable: false,
      sortable: false,
      Cell: (cell) => {

        var recordsetOptions = [];
        recordsets.forEach(function (recordset) {
            recordsetOptions.push(<option key={recordset.name + cell.row.key} value={recordset.name}>{recordset.name}</option>);
        })

        return (
          <select className="form-control" value={cell.row.recordset} onChange={that.onRecordsetChange.bind(that, cell.row.key)}>
            {recordsetOptions}
          </select>
        )}
    });

    columns.push({
      Header: 'Field',
      accessor: 'field',
      resizable: false,
      sortable: false,
      Cell: (cell) => {
        var options = [];

        for (var ii=0; ii<recordsets.length; ii++) {
          if (cell.row._original.recordset === recordsets[ii].name) {
            recordsets[ii].definition.forEach(function (field) {
                options.push(<option key={field.name + cell.row.key} value={field.name}>{field.name}</option>);
            });
            break;

          }
        };

        return (
          <select className="form-control" value={cell.row.field} onChange={that.onFieldChange.bind(that, cell.row.key)}>
            {options}
          </select>)
        }
    });

    columns.push({
      Header: '',
      accessor: 'comparer',
      resizable: false,
      sortable: false,
      width: 80,
      Cell: (cell) => (
        <select className="form-control"  value={cell.row.comparer} onChange={that.onComparerChange.bind(that, cell.row.key)}>
          <option>=</option>
          <option>!=</option>
        </select>
      )
    });


    columns.push({
      Header: 'Value',
      accessor: 'value',
      resizable: false,
      sortable: false,
      Cell: (cell) => (
        <input type="text" className="form-control" value={cell.row.value} onChange={that.onValueChange.bind(that, cell.row.key)}/>
      )
    });

    columns.push({
      Header: 'Case',
      accessor: 'case',
      resizable: false,
      sortable: false,
      width: 50,
      Cell: (cell) => (
        <input type="checkbox" className="form-control" title="Match Case" checked={cell.row.matchCase} onChange={that.onMatchCaseChange.bind(that, cell.row.key)}/>
      )
    });
  }
  return columns;
}

FilterCreator.defaultProps = {
  maxRows: 10,
  isOpened: true,
  tabledata: [],
  currentkey: 0
}
