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

const example = {
    "pageSize":200,
    "pageNumber":1,
    "propertyNames":["CoalesceEntity.objectkey"],
    "group":{
      "operator":"AND",
      "criteria":[
          {
            "key":0,
            "recordset":"CoalesceEntity",
            "field":"objectkey",
            "operator":"PropertyIsNotEqualTo",
            "value":"aa",
            "matchCase":false
          },
        ],

        "groups":[{
          "operator":"AND",
          "criteria":[
              {
                "key":0,
                "recordset":"CoalesceEntity",
                "field":"name",
                "operator":"!=",
                "value":"aa",
                "matchCase":false
              },
            ]
          }
        ]

      }
    }

export class SearchCreator extends React.Component {

  constructor(props) {
    super(props);

    /*if (props.tabledata != null) {
      for (var ii=0; ii<props.tabledata.length; ii++) {
        props.tabledata[ii].key = ii;
      }
    }*/

    this.state = {
      //recordSetSelection: 0,
      //fieldSelection: 0,
      //operatorSelection: 0,
      //valueSelection: 0,
      maxRows: 10,
      matchCase: false,
      currentkey: 0,
      queryCount: 0,
      tabledata: [{
                   queryKey:0,
                   key: 0,
                   recordset: 'CoalesceEntity',
                   field: 'name',
                   operator: '=',
                   value: 'aa',
                   matchCase: true}],
       groups: [],
      //tableDataSelection: props.tabledata,
      //onSearch: props.onSearch,
      //groupRecordSet: props.groupRecordSet,
      //criteriakey: props.criteriakey,
      isOpened: true
    }
    this.setState({groups: this.state.groups.concat(this.state.tabledata)});
    console.log("SearchCreator", this.state.tabledata, this.state.groups);
  }

  addGroup(){
    console.log("Adding Group");
    this.setState({
            groups: this.state.groups.concat([{
                               queryKey: this.state.queryCount+1,
                               key: 0,
                               recordset: 'CoalesceEntity',
                               field: 'name',
                               operator: '=',
                               value: 'aa',
                               matchCase: true}]),
          // Increment the counter to ensure key is always unique.
          queryCount: this.state.queryCount + 1
     });
      console.log("Groups now are", this.state.groups);
    }

  //componentWillReceiveProps(nextProps) {
    //this.state = nextProps;
  //  console.log("Next props", nextProps);
 // }

  render() {

    //const {recordsets, tabledata, isOpened, currentkey} = this.state;
    console.log("SearchCreator recordsets", this.props.recordsets);
    //console.log("Filter Creator recordsets", JSON.stringify(recordsets));
    console.log("SearchCreator isOpened", this.state.isOpened);
    console.log("SearchCreator tableData", this.state.tabledata);
    console.log("SearchCreator tableData length", this.state.tabledata.length);
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
                pageSize={this.state.maxRows}
                data={this.state.tabledata}
                showPagination={false}
                columns={createColumns(this, this.props.recordsets, this.state.currentkey)}
              />
              <div className="form-buttons">
                <IconButton icon="/images/svg/add.svg" title="Add Criteria" onClick={this.addRow.bind(this)} />
                <IconButton icon="/images/svg/search.svg" title="Execute Query" onClick={this.props.onSearch.bind(this, this.state.tabledata)} />
                <input type="checkbox" title="Add to Group" onChange={this.onAddGroupCheck.bind(this, this.state.tabledata.queryKey )}/>
                <IconButton icon="/images/svg/add.svg" title="Add To Group" onClick={this.addGroup.bind(this)}/>
              </div>
            </div>
          </Collapse>
      </div>
      )
  }

  onRecordsetChange(key, e) {
    console.log("On record set change key is", key, "e is", e);

    var row = this.getRow(this.state.tabledata, key);
    var defaultField;
    for (var ii=0; ii<this.props.recordsets.length; ii++) {
      if (this.props.recordsets[ii].name === e.target.value) {
        defaultField = this.props.recordsets[ii].definition[0].name;
        break;
      }
    };

    row.recordset = e.target.value;
    //tempGroupRecordSet = e.target.value;
    row.field = defaultField;
    row.operator = 'EqualTo';
    row.value = '';

    this.setState({tabledata: this.state.tabledata});
  }

  onFieldChange(key, e) {
    const {tabledata} = this.state;
    //console.log("On field change", key);
    var row = this.getRow(tabledata, key);

    row.field = e.target.value;
    row.operator = 'EqualTo';
    row.value = '';

    this.setState({tabledata: tabledata});
  }

  onOperatorChange(key, e) {
    const {tabledata} = this.state;
    var row = this.getRow(tabledata, key);

    row.operator = e.target.value;

    this.setState({tabledata: tabledata});
  }

  onValueChange(key, e) {
    const {tabledata} = this.state;
    var row = this.getRow(tabledata, key);

    row.value = e.target.value;

    this.setState({tabledata: tabledata});
  }

  onAddGroupCheck(key, e){
    console.log("Add this tabledata (query) to group");
    }

  onMatchCaseChange(key, e) {
    const {tabledata} = this.state;
    var row = this.getRow(tabledata, key);

    row.matchCase = e.target.checked;

    this.setState({tabledata: tabledata});
  }

  onAndOrChange(key, e) {
    const {tabledata} = this.state;
    var row = this.getRow(tabledata, key);
    row.andOr = e.target.value;

    this.setState({tabledata: tabledata});

  }

  deleteRow(key, e) {

    const {tabledata} = this.state;
    if(tabledata != null){
    //console.log("delete row", key);
    // Delete Row
    for (var ii=0; ii<tabledata.length; ii++) {
      if (tabledata[ii].key === key) {
          tabledata.splice(ii, 1);
          break;
      }
    }
    }

    // Save State
    this.setState({tabledata: tabledata});
  }

  addRow(e) {
    const {tabledata, currentkey} = this.state;
    //console.log("Adding row, recordsets is", this.props.recordsets[0], this.props.recordsets[0].definition[0]);
    var keyvalue = currentkey + 1;

    if (tabledata.length < this.state.maxRows) {
      // Create New Data Row
      tabledata.push({
        key: keyvalue,
        recordset: this.props.recordsets[0].name,
        field: this.props.recordsets[0].definition[0].name,
        operator: 'EqualTo',
        value: '',
        matchCase: false,
        andOr: 'And'
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

    columns.push({
      Header: 'AndOr',
      accessor: 'andOr',
      resizable: false,
      sortable: false,
      width: 100,
      Cell: (cell) => (
       <select className="form-control"  value={cell.row.andOr} onChange={that.onAndOrChange.bind(that, cell.row.key)}>
       <option>And</option>
       <option>Or</option>
       </select>
      )});
  }
  return columns;
}

//SearchCreator.defaultProps = {
//  maxRows: 10,
//  isOpened: false,
//  tabledata: [],
 // currentkey: 0
//}
