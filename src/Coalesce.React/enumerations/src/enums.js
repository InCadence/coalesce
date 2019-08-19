import React from 'react'
import { CoalesceResults, IconButton } from 'coalesce-components/lib/components'

export class Enums extends React.Component {

  render() {

    return (

      <CoalesceResults
        title="Enumerations"
        data={this.props.data.hits}
        properties={this.props.columns}
        createButtons={(row) => {
          return [
            <IconButton key={`${row._original.entityKey}_view`} icon="/images/svg/view.svg" title="View Values" onClick={() => {this.props.loadValues(row._original.entityKey)}} />,
            <IconButton key={`${row._original.entityKey}_edit`} icon="/images/svg/edit.svg" title="Edit Enumeration" onClick={() => {window.open("/entityeditor/?entitykey=" + row._original.entityKey)}} />
          ];
        }}
      />

    )
  }

}
