import React from 'react';
import ReactTable from 'react-table'

export class SearchResults extends React.PureComponent {

  render() {

    const {data, properties} = this.props;

    var hits = data.hits;

    // Always show entity key
    var columns = [
      {
        Header: 'Key',
        accessor: 'entityKey'
      }
    ];

    // Add additional columns
    properties.forEach(function (property) {
      var parts = property.split(".");

      columns.push({
        Header: parts[1],
        accessor: parts[1]
      })
    });

    //window.open(this.props.url + "/entityeditor/?entitykey=" + cell.row.entityKey, '_blank')

    // Add button for viewing entity
    columns.push({
      Header: '',
      accessor: 'button',
      Cell: (cell) => (
        <img id={cell.row.key} src={require('common-components/img/view.ico')} alt="view" title="View Entity" className="coalesce-img-button small enabled" onClick={() => window.open(this.props.url + "/entityeditor/?entitykey=" + cell.row.entityKey)}/>
      )
    });

    var tabledata;

    if (hits != null) {

      tabledata = hits;

      // Add additional column data
      tabledata.forEach(function (hit) {
        for (var ii=1; ii<columns.length - 1; ii++) {
            hit[columns[ii].accessor] = hit.values[ii-1];
        }
      });

    }

    return (
      <div className="ui-widget-content">
        <ReactTable
          data={tabledata}
          columns={columns}
        />
      </div>
    )

  }


}

SearchResults.defaultProps = {
  url: 'http://' + window.location.hostname + ':' + window.location.port,
  data: [],
  properties: []
}
