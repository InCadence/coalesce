import React from 'react';
import ReactTable from 'react-table'

import { IconButton } from 'coalesce-components/lib/components'
import { DialogMessage, DialogPrompt } from 'coalesce-components/lib/components/dialogs'
import { loadEntity } from 'coalesce-components/lib/js/entityController'

import uuid from 'uuid';
import { FieldInput } from './FieldInput'

import { linkage_enum, status_enum } from './enumerations.js'

export class LinkageView extends React.Component
{
  constructor(props) {
    super(props);
    this.state = {
      linkages: props.linkages,
      prompt: null,
      error: null
    };

    this.handleLink = this.handleLink.bind(this);
    this.handleEntity2KeyClick = this.handleEntity2KeyClick.bind(this);
  }

  componentWillReceiveProps(nextProps) {
    this.setState({
      linkages: nextProps.linkages
    })
  }

  handleEntity2KeyClick(key) {
    window.location.href = "/entityeditor/?entitykey=" + key;
  }

  handleLink(key) {
    var that = this;

    loadEntity(key).then((entity) => {

      const {linkages} = that.state;

      linkages.push({
          key: uuid.v4(),
          status: 'ACTIVE',
          entity2Key: entity.key,
          entity2Name: entity.name,
          entity2Source: entity.source,
          entity2Version: entity.version,
          entity2ObjectVersion: entity.objectVersion,
          linkType: linkage_enum[0].enum
      })

      that.setState({linkages: linkages});

    }).catch((err) => {
      this.setState({
        error: `Failed to retrieve entity's info: ${err.message}`
      })
    });
    this.setState({
      prompt: false
    })
  }

  toggleShowAll(e) {
    this.setState({showAll: e.target.checked})
  }

  render() {
    const {linkages} = this.state;

    var columns = [
      {Header: 'Key', accessor: 'linkage.key', show: false, 'sortable': false},
      {Header: 'Status', accessor: 'linkage', 'sortable': false, Cell: (cell) => (
          <FieldInput field={cell.value} dataType="ENUMERATION_TYPE" attr="status" options={status_enum} showLabels={false}/>
        )},
      {Header: 'entity2Key', accessor: 'linkage.entity2Key', show: false, 'sortable': false},
      {Header: 'Label', accessor: 'linkage', 'sortable': false, Cell: (cell) => (
          <FieldInput field={cell.value} dataType="STRING_TYPE" attr="label" showLabels={false}/>
        )},
      {Header: 'Name', accessor: 'linkage.entity2Name', 'sortable': false},
      {Header: 'Source', accessor: 'linkage.entity2Source', 'sortable': false},
      {Header: 'Type', accessor: 'linkage', 'sortable': false, Cell: (cell) => (
          <FieldInput field={cell.value} dataType="ENUMERATION_TYPE" attr="linkType" options={linkage_enum} showLabels={false}/>
        )
      }
    ];

    var buttons = {};
    buttons['Header'] = '';
    buttons['accessor'] = 'linkage.entity2Key';
    buttons['sortable'] = false;
    buttons['resizable'] = false;
    buttons['width'] = 34;
    buttons['Cell'] = (cell) => (
      <IconButton
        id={cell.row.key}
        icon='/images/svg/view.svg'
        title="View Entity"
        size="18px"
        onClick={() => {this.handleEntity2KeyClick(cell.value)}}
      />
    );

    columns.push(buttons);

    var data = [];

    if (linkages != null) {

      linkages.forEach(function(linkage) {
        //if (showAll || linkage.status !== 'DELETED') {
          data.push({
            linkage: linkage,
            options: ''
          })
        //}
      });
    }

    return (
      <div className="ui-widget-content section">
        <ReactTable columns={columns} data={data} className="-striped -highlight"/>

        <div className='form-buttons'>
          <input type='checkbox' />
          <label>Show All</label>
          <IconButton icon="/images/svg/add.svg" title="Add Record" onClick={() => {this.setState({prompt: true})}}/>
        </div>
        {this.state.prompt &&
          <DialogPrompt
            title="Enter Entity Key"
            value=''
            opened={true}
            onClose={() => {this.setState({prompt: false})}}
            onSubmit={this.handleLink}
          />
        }
        {this.state.error &&
          <DialogMessage
            title="Error"
            opened={true}
            message={this.state.error}
            onClose={() => {this.setState({error: null})}}
          />
        }
      </div>

    )
  }
}
