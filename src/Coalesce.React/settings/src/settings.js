import "babel-polyfill";

import React from 'react';
import ReactTable from 'react-table'

import 'react-table/react-table.css'

export class SettingsView extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      settings: props.settings
    };

  }

  onValueChange(name, e) {
    const {settings} = this.state;

    settings[name] = e.target.value;

    this.setState({
      settings: settings
    })
  }



  render() {

    const {settings} = this.state;

    var columns = [
      {
        Header: 'Name',
        accessor: 'name',
        resizable: true,
        sortable: true,
      },{
        Header: 'Value',
        accessor: 'value',
        resizable: true,
        sortable: true,
        Cell: (cell) => (
          <input type="text" className="form-control" value={cell.row.value} onChange={that.onValueChange.bind(that, cell.row.name)}/>
        )
      }
    ]

    var data = [];
    var that = this;

    Object.keys(settings).forEach(function (key) {
      data.push({name: key, value: settings[key]})
    })

    return (
      <ReactTable
        data={data}
        showPagination={true}
        columns={columns}
        sorted={[{'id': 'name', 'desc': false}]}
      />
    )
  }

}

export default SettingsView;
