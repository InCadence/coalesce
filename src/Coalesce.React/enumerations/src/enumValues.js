import React from 'react'
import { SearchResults } from './results.js'
import IconButton from 'common-components/lib/components/IconButton'

const valueCols = [
  {
    key: "values.ordinal",
    Header: 'Ordinal',
    accessor: 'ordinal'
  },{
    key: "values.value",
    Header: 'Value',
    accessor: 'value'
  },{
    key: "values.description",
    Header: 'Description',
    accessor: 'description'
  }
];

export class EnumValues extends React.PureComponent {

  render() {
    return (

        <div>
          <SearchResults
            title="Values"
            data={this.props.data.hits}
            properties={valueCols}

            createButtons={(row) => {
              return [
                <IconButton icon="/images/svg/view.svg" title="View Associated Values" onClick={() => this.props.loadAssociatedValues(this.props.enumKey, row._original.associatedValues)}/>,
              ];
            }}
          />
          <div className='form-buttons'>
            <IconButton icon="/images/svg/back.svg" title="Back" onClick={() => {this.props.loadEnumerations()}} />
            <IconButton icon="/images/svg/edit.svg" title="Edit" onClick={() => {window.open("/entityeditor/?entitykey=" + this.props.enumKey)}} />
          </div>
        </div>

    )
  }
}
