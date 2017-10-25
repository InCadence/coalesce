import React from 'react';
import ReactTable from 'react-table'
import {Accordion} from './accordion.js'
import {Toggle} from './toggle.js'
import {Collapse} from 'react-collapse';

export class LinkageView extends React.Component
{

  constructor(props) {
    super(props);
    this.state = props;
  }

  handleEntity2KeyClick(entitykey, e) {
    window.location.href = "/entityeditor/?entitykey=" + entitykey;
  }

  render() {
    const {linkages, isOpened} = this.state;

    var columns = [
      {Header: 'Key', accessor: 'key', show: false},
      {Header: 'entity2Key', accessor: 'entity2Key', show: false},
      {Header: 'Label', accessor: 'label'},
      {Header: 'Name', accessor: 'entity2Name'},
      {Header: 'Source', accessor: 'entity2Source'},
      {Header: 'Type', accessor: 'linkType'},
      {Header: 'Created', accessor: 'dateCreatedAsString'},
      {Header: 'LastModified', accessor: 'lastModifiedAsString'}
    ];

    var buttons = {};
    buttons['Header'] = 'Key';
    buttons['accessor'] = 'entity2Key';
    buttons['Cell'] = (cell) => (
      <button id={cell.row.key} style={{width:'90%'}} onClick={this.handleEntity2KeyClick.bind(this, cell.row.entity2Key)}>=></button>
    );

    columns.push(buttons);

    var data = [];

    if (linkages != null) {

      var that = this;

      linkages.forEach(function(linkage) {

        data.push({
          key: linkage.key,
          label: linkage.label,
          entity2Key: linkage.entity2Key,
          entity2Name: linkage.entity2Name,
          entity2Source: linkage.entity2Source,
          linkType: linkage.linkType,
          dateCreatedAsString: linkage.dateCreatedAsString,
          lastModifiedAsString: linkage.lastModifiedAsString,
          options: ''
        })
      });

    }

    return (
      <div className="ui-widget">
        <Toggle
          ontext="Linkage Section"
          offtext="Linkage Section"
          isToggleOn={isOpened}
          onToggle={(value) => {
            this.setState({isOpened: value});
          }}
          />
          <Collapse className="ui-widget-content" isOpened={isOpened}>
            <div className="section">
              <ReactTable columns={columns} data={data} />
            </div>
          </Collapse>
      </div>
    )
  }
}
/*
<Accordion label="Linkage Section" isOpened={false}>
  <div className="section">
    <ReactTable columns={columns} data={data} />
  </div>
</Accordion>
*/
LinkageView.defaultProps = {
  isOpened: false
}
