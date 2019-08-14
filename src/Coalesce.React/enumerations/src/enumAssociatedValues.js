import React from 'react'
import { CoalesceResults, IconButton } from 'coalesce-components/lib/components'


export class EnumAssociatedValues extends React.PureComponent {

  render() {

    var items = [];
    const { data } = this.props;

    Object.keys(data).forEach(function (key) {
      items.push({
        'entityKey': key,
        'value': data[key]
      });
    });

    return (

      <div>
          <CoalesceResults
            title="Associated Values"
            data={items}
            properties={[
                {
                  key: 'associated.key',
                  Header: 'Name',
                  accessor: 'entityKey'
                },{
                  key: 'associated.value',
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

    )
  }
}
