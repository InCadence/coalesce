import React from 'react';
import ReactTable from 'react-table'
import { ReactTableDefaults } from 'react-table'
import {Toggle} from 'common-components/lib/toggle.js'
import {Collapse} from 'react-collapse';
import {IconButton} from 'common-components/lib/components/IconButton.js'
import {FilterGroup} from './filtergroup'
import 'react-table/react-table.css'

import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import { getDefaultTheme } from 'common-components/lib/js/theme'
import getMuiTheme from 'material-ui/styles/getMuiTheme';

Object.assign(ReactTableDefaults, {
  defaultPageSize: 5,
  minRows: 3,
  // etc...
})

export class FilterCreator extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      tabledata: [{
                  key:0,
                  recordset: 'CoalesceEntity',
                  field: 'name',
                  operator: '=',
                  value: 'aa',
                  matchCase: true}],
      currentkey: 0,
      //queryDataKey: this.props.queryDataKey,
      //queryDataRecordSet: this.props.queryDataRecordSet,
      //queryDataField: this.props.queryDataField,
      //queryDataOperator: this.props.queryDataOperator,
      //queryDataValue: this.props.queryDataValue,
      //queryDataMatchCase: this.props.queryDataMatchCase,
      queryData: this.props.queryData,
      isOpened: true
    }
  }

  render() {

    //console.log("Filter Creator table data", JSON.stringify(tabledata));
    //console.log("Filter Creator recordsets", JSON.stringify(recordsets));
    return (
      <div className="ui-widget">
        <Toggle
          ontext= "Search Criteria"
          offtext="Search Criteria"
          isToggleOn={true}
          onToggle={(value) => {
            this.setState({isOpened: value});
          }}
         />
          <Collapse isOpened={this.state.isOpened}>
            <div className="ui-widget-content">
              <ReactTable
                pageSize={this.props.maxRows}
                data={this.state.queryData}
                showPagination={false}
                columns={createColumns(this, this.props.recordsets, this.state.currentkey)}
              />
              <div className="form-buttons">
                <IconButton icon="/images/svg/add.svg" title="Add Criteria" onClick={this.addRow.bind(this)} />
                <IconButton icon="/images/svg/add.svg" title="Add SubGroup" onClick={this.addSubGroup.bind(this)}/>
              </div>
            </div>
          </Collapse>
      </div>
    )
  }


  getTableData() {
    return(this.state.tabledata);
  }

  onRecordsetChange(key, e) {
    //console.log("On record set change key is", key, "e is", e);
    const {tabledata, queryData, queryDataRecordSet, queryDataField, queryDataKey, queryDataMatchCase, queryDataOperator, queryDataValue} = this.state;
    var row = this.getRow(queryData, key);
    //console.log("row is", row, tabledata, key);
    var defaultField;
    var tempGroupRecordSet;
    //console.log("recordsets.length is", recordsets.length, recordsets);
    for (var ii=0; ii<this.props.recordsets.length; ii++) {
      if (this.props.recordsets[ii].name === e.target.value) {
        defaultField = this.props.recordsets[ii].definition[0].name;
        //console.log("RecordSets", recordsets[ii], "Target value is", e.target.value);
        break;
      }
    };

    row.recordset = e.target.value;
    tempGroupRecordSet = e.target.value;
    row.field = defaultField;
    row.operator = 'EqualTo';
    row.value = 'aa';
    console.log("FilterCreator queryData is", queryData);
    this.setState({tabledata: tabledata,
                   queryDataRecordSet: e.target.value,
                   queryData: queryData});
  }

  onFieldChange(key, e) {
    const {tabledata, queryData} = this.state;
    //console.log("On field change", key);
    var row = this.getRow(queryData, key);

    row.field = e.target.value;
    row.operator = 'EqualTo';
    row.value = 'aa';

    this.setState({tabledata: tabledata, queryData: queryData});
  }

  onOperatorChange(key, e) {
    const {tabledata, queryData} = this.state;
    var row = this.getRow(queryData, key);

    row.operator = e.target.value;

    this.setState({tabledata: tabledata, queryData: queryData});
  }

  onValueChange(key, e) {
    const {tabledata, queryData} = this.state;
    var row = this.getRow(queryData, key);

    row.value = e.target.value;

    this.setState({tabledata: tabledata, queryData: queryData});
  }

  onMatchCaseChange(key, e) {
    const {tabledata, queryData} = this.state;
    var row = this.getRow(queryData, key);

    row.matchCase = e.target.checked;

    this.setState({tabledata: tabledata, queryData: queryData});
  }

  deleteRow(key, e) {

    const {tabledata, queryData} = this.state;
    //console.log("delete row", key);
    // Delete Row
    for (var ii=0; ii<queryData.length; ii++) {
      if (queryData[ii].key === key) {
          queryData.splice(ii, 1);
          break;
      }
    }

    // Save State
    this.setState({tabledata: tabledata, queryData: queryData});
  }
  addSubGroup(e){
    console.log("Adding SubGroup");
    const {queryData} = this.state;
    var tempTable = {
                     operator:'AND',
                     criteria: [{key:0,
                     recordset: 'CoalesceEntity',
                     field: 'name',
                     operator: '=',
                     value: 'aa',
                     matchCase: true}],
                     groups:[]
                    };
    queryData.groups.push(tempTable);
    this.setState({queryData: queryData});
  }
  addRow(e) {
    const {tabledata, currentkey, queryData} = this.state;
    //console.log("Adding row, recordsets is", recordsets, currentkey);
    var keyvalue = currentkey + 1;

    if (queryData.length < this.props.maxRows) {
      // Create New Data Row
      queryData.push({
        key: keyvalue,
        recordset: this.props.recordsets[0].name, //Assuming we always start with an existing recordset when we instantiate a filtercreator, always match with that
        field: this.props.recordsets[0].definition[0].name,
        operator: 'EqualTo',
        value: 'aa',
        matchCase: false
      });

      // Save State
      this.setState({
        tabledata: tabledata,
        currentkey: keyvalue,
        queryData: queryData
      });
    } else {
      alert("Row limit reached");
    }
    if(queryData === this.props.queryData){
      console.log("addRow queryData is equal", queryData, this.props.queryData);
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



function createColumns(that, recordsets, key) {

  var columns = [{Header: 'key', accessor: 'key', show: false}];
 // console.log("createColumns", key, that.state.currentkey);
  if (recordsets != null) {
    columns.push({
      Header: '',
      accessor: 'delete',
      resizable: false,
      sortable: false,
      width: 50,
      Cell: (cell) => (
        <IconButton icon={"/images/svg/remove.svg"} title="Remove Criteria" onClick={that.deleteRow.bind(that, cell.row.key)} enabled={true} />
      )
    });

    columns.push({
      Header: 'Recordset',
      accessor: 'recordset',
      resizable: false,
      sortable: false,
      Cell: (cell) => {
        return (
          <select className="form-control" value={cell.row.recordset} onChange={that.onRecordsetChange.bind(that, cell.row.key)}>
            {recordsets.map((recordset) => {
              //console.log("column push", cell.row.key, cell, recordset.name);
              return (<option key={recordset.name + cell.row.key} value={recordset.name}>{recordset.name}</option>);
            })}
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
        //console.log("recordsets", recordsets);
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
      accessor: 'operator',
      resizable: false,
      sortable: false,
      width: 80,
      Cell: (cell) => (
        <select className="form-control"  value={cell.row.operator} onChange={that.onOperatorChange.bind(that, cell.row.key)}>
          <option>EqualTo</option>
          <option>NotEqualTo</option>
          <option>Like</option>
          <option>Between</option>
          <option>GreaterThan</option>
          <option>GreaterThanOrEqualTo</option>
          <option>LessThan</option>
          <option>LessThanOrEqualTo</option>
          <option>During</option>
          <option>After</option>
          <option>Before</option>
          <option>BBOX</option>
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

