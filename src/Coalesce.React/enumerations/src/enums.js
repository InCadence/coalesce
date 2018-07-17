import React from 'react'
import {SearchResults} from './results.js'
import {IconButton} from 'common-components/lib/components'

export class Enums extends React.Component {

  render() {

    return (

        <div className="ui-widget">
        <div className="ui-widget-header">
        Enumerations
        </div>
        <div className="ui-widget-content">
          <SearchResults
            data={this.props.data}
            properties={this.props.columns}
            createButtons={(row) => {
              return [
                <IconButton icon="/images/svg/view.svg" title="View Values" onClick={() => {this.props.loadValues(row._original.entityKey)}} />,
                <IconButton icon="/images/svg/edit.svg" title="Edit Enumeration" onClick={() => {window.open("/entityeditor/?entitykey=" + row._original.entityKey)}} />
              ];
            }}
          />
        </div>
      </div>

    )
  }

}
