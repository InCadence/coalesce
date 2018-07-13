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
      isOpened: true
    }
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
    let tempGroup = Object.assign({}, this.state.group);
    tempGroup.groups.push(tempTable);
    this.setState({
            group: tempGroup,
          // Increment the counter to ensure key is always unique.
          groupKey: this.state.groupKey+1,
          queryCount: this.state.queryCount + 1
     });
  }


  render() {
    var that = this;
    return (
      <div>
      <IconButton icon="/images/svg/add.svg" title="Add Group" onClick={this.addGroup.bind(this)}/>
      <IconButton icon="/images/svg/search.svg" title="Execute Query" onClick={this.props.onSearch.bind(this, this.state.group)} />
      <FilterCreator maxRows={this.state.maxRows} recordsets={this.props.recordsets} queryData = {this.state.group.criteria} />
      {this.state.group.groups.map(function(table)
        {return(
               <FilterCreator maxRows={that.state.maxRows} recordsets={that.props.recordsets} queryData = {table.criteria} subGroups = {table.groups}/>
         )
        }
      )
    }
    </div>
    )
  }

 }