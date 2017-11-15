import React from 'react';
import ReactTable from 'react-table'
import Popup from 'react-popup';
import { ReactTableDefaults } from 'react-table'

import 'react-table/react-table.css'

export class SettingsView extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;

    this.onAddSetting = this.onAddSetting.bind(this);
    this.onSaveSettings = props.saveCallback.bind(this, this.state.settings);
  }

  onValueChange(name, e) {
    const {settings} = this.state;

    settings[name] = e.target.value;

    this.setState({
      settings: settings
    })
  }

  onAddSetting() {

    var that = this;

    Popup.plugins().prompt('OK', 'Enter Setting Name', '', '', function(value) {
      const {settings} = that.state;

      settings[value] = '';

      that.setState({
        settings: settings
      })
    });
  }

  render() {

    const {settings} = this.state;

    var that = this;

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
        <div className="ui-widget">
        <div className="ui-widget-header">
          Settings
        </div>
        <div className="ui-widget-content">
          <ReactTable
            data={data}
            showPagination={true}
            columns={columns}
            sorted={[{'id': 'name', 'desc': false}]}
          />
          <div className="form-buttons">
            <img src={require('common-components/img/add.ico')} alt="Add" title="Add Setting" className="coalesce-img-button enabled" onClick={this.onAddSetting}/>
            <img src={require('common-components/img/save.ico')} alt="Save" title="Save Settings" className="coalesce-img-button enabled" onClick={this.onSaveSettings}/>
          </div>
          </div>
        </div>
    )
  }

}
