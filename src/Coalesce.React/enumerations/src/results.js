import React from 'react';
import ReactTable from 'react-table'

import 'react-table/react-table.css'

export class SearchResults extends React.PureComponent {

  render() {

    const {data, properties} = this.props;

    return (
      <ReactTable
        data={data.hits}
        columns={properties.concat({
          Header: '',
          width: 100,
          resizable: false,
          Cell: (cell) => (
            <div className="form-buttons">
              {this.props.createButtons(cell.row)}
            </div>
          )
        })}
      />
    )

  }


}

SearchResults.defaultProps = {
  data: [],
  properties: []
}
