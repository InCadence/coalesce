import React from 'react';
import { ReactTableDefaults } from 'react-table'
import IconButton from 'coalesce-components/lib/components/IconButton'
import {FilterCreator} from './filtercreator.js'

Object.assign(ReactTableDefaults, {
  defaultPageSize: 5,
  minRows: 3,
  // etc...
})

export class SearchCreator extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      groupKey: 0,
      criteriaCount: 0,
      data: props.data
    }
  }

  addGroup(){
    const {group} = this.state;
    console.log("ADDING GROUP");
    var tempTable = [
      {
        operator:'AND',
        criteria: [
          {
            key:0,
            recordset: 'CoalesceEntity',
            field: 'name',
            operator: 'EqualTo',
            value: '',
            matchCase: false
          }
        ],
        groups:[]
      }
    ];
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
    if(group.length > 1)
    {
      group.splice(group.length-1,1);
      this.setState({group: group});
    }
  }

  runSearch(){
    this.props.onSearch(this.state.data);
  }

  render() {
    var that = this;
    return (
      <div>
      <IconButton icon="/images/svg/add.svg" title="Add Group" onClick={this.addGroup.bind(this)}/>
      <IconButton icon="/images/svg/search.svg" title="Execute Query" onClick={this.runSearch.bind(this)} />
      <IconButton icon="/images/svg/remove.svg" title="Delete Latest Group" onClick={this.deleteGroup.bind(this)}/>
      <FilterCreator
        maxRows={that.props.maxRows}
        recordsets={that.props.recordsets}
        data={this.state.data}
      />
    </div>
    )
  }

 }

 SearchCreator.defaultProps = {
   maxRows: 10
 }
