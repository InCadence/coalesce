import React from 'react'
import {IconButton} from 'common-components/lib/components'
import ReactTable from 'react-table'

export class EnumAssociatedValues extends React.PureComponent {

  render() {

    var items = [];
    const { data } = this.props;

    Object.keys(data).forEach(function (key) {
      items.push({
        'name': key,
        'value': data[key]
      });
    });

    return (

      <div className="ui-widget">
        <div className="ui-widget-header">
        Associated Values
        </div>
        <div className="ui-widget-content">
          <ReactTable
            data={items}
            columns={[
                {
                  Header: 'Name',
                  accessor: 'name'
                },{
                  Header: 'Value',
                  accessor: 'value'
                }
              ]}
          />
          <div className='form-buttons'>
            <IconButton icon="/images/svg/back.svg" title="Back" onClick={() => {this.props.loadValues(this.props.enumKey)}} />
            <IconButton icon="/images/svg/edit.svg" title="Edit" onClick={() => {window.open("/entityeditor/?entitykey=" + this.props.enumKey)}} />
          </div>
        </div>
      </div>

    )
  }
}
