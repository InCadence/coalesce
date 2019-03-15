import React from 'react'
import { SearchResults } from './results.js'
import IconButton from 'common-components/lib/components/IconButton'

export class Enums extends React.Component {

  render() {

    return (

      <SearchResults
        title="Enumerations"
        data={this.props.data.hits}
        properties={this.props.columns}
        createButtons={(row) => {
          return [
            <IconButton icon="/images/svg/view.svg" title="View Values" onClick={() => {this.props.loadValues(row._original.entityKey)}} />,
            <IconButton icon="/images/svg/edit.svg" title="Edit Enumeration" onClick={() => {window.open("/entityeditor/?entitykey=" + row._original.entityKey)}} />
          ];
        }}
      />

    )
  }

}
