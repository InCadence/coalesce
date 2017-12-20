import React from 'react';
import ReactTable from 'react-table'

import 'react-table/react-table.css'

export class SearchResults extends React.PureComponent {

  render() {

    const {data, properties} = this.props;

    var hits = data.hits;

    // Always show entity key
    var columns = [
      {
        Header: 'Key',
        accessor: 'entityKey',
        show: false
      }
    ];

    // Add additional columns
    properties.forEach(function (property) {
      var parts = property.split(".");
      var name;

      switch(parts.length) {
        case 0:
          /* Should never get here */
          break;
        case 1:
          name = parts[0];
          break;
        default:
          name = parts[1];
      }

      columns.push({
        Header: name,
        accessor: name
      })
    });

    // Add button for viewing entity
    columns.push({
      Header: '',
      accessor: 'button',
      Cell: (cell) => (
        <div className="form-buttons">
          {this.props.createButtons(cell.row)}
        </div>
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
  data: [],
  properties: []
}
