import React from 'react';
import ReactTable from 'react-table'

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
    const {linkages} = this.state;

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
    buttons['Header'] = '';
    buttons['accessor'] = 'entity2Key';
    buttons['Cell'] = (cell) => (
      <img id={cell.row.key} src='/images/svg/view.svg' alt="view" title="View Linked Entity" className="coalesce-img-button small enabled" onClick={this.handleEntity2KeyClick.bind(this, cell.row.entity2Key)}/>
    );

    columns.push(buttons);

    var data = [];

    if (linkages != null) {

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
      <div className="ui-widget-content section">
        <ReactTable columns={columns} data={data} />
      </div>
    )
  }
}

LinkageView.defaultProps = {
  isOpened: false
}
