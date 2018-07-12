import React from 'react';
import ReactTable from 'react-table'
import { ReactTableDefaults } from 'react-table'
import {Toggle} from 'common-components/lib/toggle.js'
import {Collapse} from 'react-collapse';
import {IconButton} from 'common-components/lib/components/IconButton.js'
import {FilterGroup} from './filtergroup.js'
import {FilterCreator} from './filtercreator.js'
import 'react-table/react-table.css'

import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import { getDefaultTheme } from 'common-components/lib/js/theme'
import getMuiTheme from 'material-ui/styles/getMuiTheme';

Object.assign(ReactTableDefaults, {
  defaultPageSize: 5,
  minRows: 3,
  // etc...
})

export class SearchCreator extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      //recordSetSelection: 0,
      //fieldSelection: 0,
      //operatorSelection: 0,
      //valueSelection: 0,
      maxRows: 10,
      matchCase: false,
      groupKey: 0,
      queryCount: 0,
      group: {
                  operator:'AND',
                  criteria: [{key:0,
                  recordset: 'CoalesceEntity',
                  field: 'name',
                  operator: 'EqualTo',
                  value: 'aa',
                  matchCase: false}],
                  groups: []
                  },
      //tableDataSelection: props.tabledata,
      //onSearch: props.onSearch,
      //groupRecordSet: props.groupRecordSet,
      //criteriakey: props.criteriakey,
      isOpened: true
    }
    //this.setState({groups: this.state.groups.concat(this.state.tabledata)});
   // this.addRow.bind(this);
   // this.onAddGroupCheck.bind(this, this.state.tabledata.queryKey );
    console.log("SearchCreator", this.state.group.criteria);
  }

  addGroup(){
    const {group} = this.state;
    var tempTable = {
                      operator:'AND',
                      criteria: [{key:0,
                      recordset: 'CoalesceEntity',
                      field: 'name',
                      operator: '=',
                      value: 'aa',
                      matchCase: false}],
                      groups:[]
                      };
    console.log("Adding Group", group.groups);
    console.log("Adding Group", group, this.state.groupKey);
    let tempGroup = Object.assign({}, this.state.group);
   // console.log("tempGroup is", tempGroup, "Default table is", this.state.defaultTable);
    //console.log("Adding Group Concat", tempGroup[0][0].groups.concat(this.state.defaultTable), tempGroup);
    console.log("Temp table is", tempTable);
    tempGroup.groups.push(tempTable);
    console.log("Adding Group concat", tempGroup.groups);
    this.setState({
            group: tempGroup,
          // Increment the counter to ensure key is always unique.
          groupKey: this.state.groupKey+1,
          queryCount: this.state.queryCount + 1
     });
      console.log("Group now are", this.state.group, this.state.queryCount, this.state.group.length);
    }

  //componentWillReceiveProps(nextProps) {
    //this.state = nextProps;
  //  console.log("Next props", nextProps);
 // }

  render() {

    //const {recordsets, tabledata, isOpened, currentkey} = this.state;
    //console.log("SearchCreator recordsets", this.props.recordsets);
    //console.log("Filter Creator recordsets", JSON.stringify(recordsets));
    //console.log("SearchCreator isOpened", this.state.isOpened);
    //console.log("SearchCreator tableData", this.state.tabledata);
    //console.log("SearchCreator tableData length", this.state.tabledata.length);
    console.log("SearchCreator render", this.state.group, this.state.group.groups);
    var that = this;
    return (
      <div>
      <IconButton icon="/images/svg/add.svg" title="Add Group" onClick={this.addGroup.bind(this)}/>
      <IconButton icon="/images/svg/search.svg" title="Execute Query" onClick={this.props.onSearch.bind(this, this.state.group)} />
      <FilterCreator maxRows={this.state.maxRows} recordsets={this.props.recordsets} queryData = {this.state.group.criteria} />
     // <FilterGroup data={this.state.group} recordsets={this.props.recordsets}/>
      {this.state.group.groups.map(function(table)
        {return(
               <FilterCreator maxRows={that.state.maxRows} recordsets={that.props.recordsets} queryData = {table.criteria} />
         )
        }
      )
    }
    </div>
    )
  }

  createSearchQuery(){
    console.log("SearchCreator queryData is", this.state.group);
   // this.state.groups.forEach(function(query){
   //   this.state.})
    //Loop over filtercreators and return table data of each filter creator instance
  }
  /*onRecordsetChange(key, table, e) {
    console.log("On record set change key is", key, "e is", e, table);

    var row = this.getRow(table, key);
    console.log("Row is", row, key);
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

    this.setState({groups: this.state.groups});
  }

  onDeleteGroup(table, e) {
    console.log("On deleteGroup");
    for(var ii=0; ii<this.state.groups.length; ii++){
      if(table.queryKey === this.state.groups[ii].queryKey){
        this.state.groups.splice(ii,1)
      }
    }
    this.setState({groups: this.state.groups});
  }

  onFieldChange(key, table, e) {
    const {tabledata} = this.state;
    //console.log("On field change", key);
    var row = this.getRow(table, key);

    row.field = e.target.value;
    row.operator = 'EqualTo';
    row.value = '';

    this.setState({groups: this.state.groups});
  }

  onOperatorChange(key, table, e) {
    const {tabledata} = this.state;
    var row = this.getRow(table, key);

    row.operator = e.target.value;

    this.setState({groups: this.state.groups});
  }

  onValueChange(key, table, e) {
    const {tabledata} = this.state;
    var row = this.getRow(table, key);

    row.value = e.target.value;

    this.setState({groups: this.state.groups});
  }

  onAddGroupCheck(key, e){
    console.log("Add this tabledata (query) to group");
    }

  onMatchCaseChange(key, table, e) {
    const {tabledata} = this.state;
    var row = this.getRow(table, key);

    row.matchCase = e.target.checked;

    this.setState({groups: this.state.groups});
  }

  onAndOrChange(key, table, e) {
    const {tabledata} = this.state;
    var row = this.getRow(table, key);
    row.andOr = e.target.value;

    this.setState({groups: this.state.groups});

  }

  deleteRow(key, table, e) {

    const {tabledata} = this.state;
    if(table != null){
    console.log("delete row", key, table, e);
    // Delete Row
    for (var ii=0; ii<this.state.groups.length; ii++) {
      if (table.rowKey === key) {
          table.splice(ii, 1);
          break;
      }
    }
    }

    // Save State
    this.setState({groups: this.state.groups});
  }

  addRow(table,e) {
    const {tabledata, currentkey} = this.state;
    console.log("Adding row", table, table[0].key, table.length, this.state.maxRows);
    //console.log("Adding row, recordsets is", this.props.recordsets[0], this.props.recordsets[0].definition[0]);

    var keyvalue = table[table.length-1].key + 1;
    if (table.length < this.state.maxRows) {
      // Create New Data Row
      table.push({
        key: keyvalue,
        recordset: this.props.recordsets[0].name,
        field: this.props.recordsets[0].definition[0].name,
        operator: 'EqualTo',
        value: '',
        matchCase: false,
        andOr: ''
      });
      // Save State
      this.setState({
        groups:this.state.groups,
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
  }*/
}



/*function createColumns(that, recordsets, key, table) {

  var columns = [{Header: 'key', accessor: 'key', show: false}];
  console.log("createColumns", key, recordsets, table);
  if (recordsets != null) {
    columns.push({
      Header: '',
      accessor: 'delete',
      resizable: false,
      sortable: false,
      width: 50,
      Cell: (cell) => (
        <IconButton icon={"/images/svg/remove.svg"} title="Remove Criteria" onClick={that.deleteRow.bind(that, cell.row.key, table)} enabled={true} />
      )
    });

    columns.push({
      Header: 'Recordset',
      accessor: 'recordset',
      resizable: false,
      sortable: false,
      Cell: (cell) => {
        return (
          <select className="form-control" value={cell.row.recordset} onChange={that.onRecordsetChange.bind(that, cell.row.key, table)}>
            {recordsets.map((recordset) => {
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
          <select className="form-control" value={cell.row.field} onChange={that.onFieldChange.bind(that, cell.row.key, table)}>
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
        <select className="form-control"  value={cell.row.operator} onChange={that.onOperatorChange.bind(that, cell.row.key, table)}>
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
        <input type="text" className="form-control" value={cell.row.value} onChange={that.onValueChange.bind(that, cell.row.key, table)}/>
      )
    });

    columns.push({
      Header: 'Case',
      accessor: 'case',
      resizable: false,
      sortable: false,
      width: 50,
      Cell: (cell) => (
        <input type="checkbox" className="form-control" title="Match Case" checked={cell.row.matchCase} onChange={that.onMatchCaseChange.bind(that, cell.row.key, table)}/>
      )
    });

    columns.push({
      Header: 'AndOr',
      accessor: 'andOr',
      resizable: false,
      sortable: false,
      width: 100,
      Cell: (cell) => (
       <select className="form-control"  value={cell.row.andOr} onChange={that.onAndOrChange.bind(that, cell.row.key, table)}>
       <option></option>
       <option>And</option>
       <option>Or</option>
       </select>
      )});
  }
  return columns;
}*/

//SearchCreator.defaultProps = {
//  maxRows: 10,
//  isOpened: false,
//  tabledata: [],
 // currentkey: 0
//}
