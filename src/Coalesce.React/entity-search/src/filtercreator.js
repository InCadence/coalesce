import React from 'react';
import ReactTable from 'react-table'
import { ReactTableDefaults } from 'react-table'

import 'react-table/react-table.css'

Object.assign(ReactTableDefaults, {
  defaultPageSize: 5,
  minRows: 3,
  showPagination: false,
  // etc...
})

function getRecordsets(section) {

  var results = [];

  section.sectionsAsList.forEach(function(section) {
    results = results.concat(results, this.processrecordsets(section));
  });

  // Render Recordsets
  section.recordsetsAsList.forEach(function(recordset) {
    results.push({name: recordset.name, definition: recordset.fieldDefinitions});
  });

  return results;
}


export class FilterCreator extends React.Component {

  constructor(props) {
    super(props);

    this.state = props;
  }

  componentDidMount() {
    var data = [];

    this.props.template.sectionsAsList.forEach(function(section) {
      data = data.concat(data, getRecordsets(section));
    });

    var tabledata = [{
      key: 0,
      recordset: data[0].name,
      field: 'boolean', //data[0].definition[0].name,
      comparer: '=',
      value: 'false',
      matchCase: false}];

    this.setState({
      recordsets: data,
      tabledata: tabledata,
      currentkey: 0
    });

  }

  render() {

    const {recordsets, tabledata} = this.state;

    return (
      <div>
        <ReactTable
          pageSize={this.props.maxRows}
          data={tabledata}
          columns={createColumns(this, recordsets)}
        />
        <button onClick={this.addRow.bind(this)}>add</button>
        <button onClick={this.props.onSearch.bind(this, this.state.tabledata)} className='mm-popup__btn mm-popup__btn--success'>search</button>
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
        <button className="form-control" title="Delete" onClick={that.deleteRow.bind(that, cell.row.key)}>
          -
        </button>
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
  maxRows: 10
}
