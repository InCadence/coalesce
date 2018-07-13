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
      maxRows: 10,
      matchCase: false,
      groupKey: 0,
      queryCount: 0,
      group: [{
                  operator:'AND',
                  criteria: [{key:0,
                  recordset: 'CoalesceEntity',
                  field: 'name',
                  operator: 'EqualTo',
                  value: 'aa',
                  matchCase: false}],
                  groups: []
                  }],
      isOpened: true
    }
  }

  addGroup(){
    const {group} = this.state;
    console.log("ADDING GROUP");
    var tempTable = [{
                      operator:'AND',
                      criteria: [{key:0,
                      recordset: 'CoalesceEntity',
                      field: 'name',
                      operator: '=',
                      value: 'aa',
                      matchCase: false}],
                      groups:[]
                      }];
    //let tempGroup = Object.assign({}, this.state.group);
    //console.log("TempGroup is", tempGroup);
    //group.push(tempTable);
    this.setState({
            group: group.concat(tempTable),
          // Increment the counter to ensure key is always unique.
          groupKey: this.state.groupKey+1,
          queryCount: this.state.queryCount + 1
     });
  }

  deleteGroup(){
    const {group} = this.state;
    let tempGroup = Object.assign({}, this.state.group);
    if(group.length > 1)
    {
      group.splice(group.length-1,1);
      this.setState({group: group});
    }
  }

  runSearch(){
    var that = this;
    this.state.group.map(function(groupData){
         that.props.onSearch(groupData);
    }
   )
  }

  render() {
    var that = this;
    return (
      <div>
      <IconButton icon="/images/svg/add.svg" title="Add Group" onClick={this.addGroup.bind(this)}/>
      <IconButton icon="/images/svg/search.svg" title="Execute Query" onClick={this.runSearch.bind(this)} />
      <IconButton icon="/images/svg/remove.svg" title="Delete Latest Group" onClick={this.deleteGroup.bind(this)}/>
      {this.state.group.map(function(groupData){
             return(
                <div>
                <FilterCreator maxRows={that.state.maxRows} recordsets={that.props.recordsets} queryData = {groupData.criteria} subGroups = {groupData.groups}/>
                {groupData.groups.map(function(table){
                          return(
                          <FilterCreator maxRows={that.state.maxRows} recordsets={that.props.recordsets} queryData = {table.criteria} subGroups = {table.groups}/>)
                  }
                 )

                }
                </div>
             )
        }
      )
     }
    </div>
    )
  }

 }