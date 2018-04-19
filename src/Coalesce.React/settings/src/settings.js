import "babel-polyfill";

import React from 'react';
import ReactTable from 'react-table'
import Popup from 'react-popup';
import { ReactTableDefaults } from 'react-table'

import { IconButton} from 'common-components/lib/index.js'
import 'react-table/react-table.css'

export class SettingsView extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      settings: props.settings
    };

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
            <IconButton icon="/images/svg/add.svg" title="Add Setting" onClick={this.onAddSetting} />
            <IconButton icon="/images/svg/save.svg" title="Save Settings" onClick={this.onSaveSettings} />
          </div>
          </div>
        </div>
    )
  }

}
