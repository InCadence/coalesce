import React from 'react';
import {LinkageView} from './linkagetable'
import {RecordsetView} from './recordset'
import {RecordView} from './record'
import { IconButton } from 'common-components/lib/components/IconButton.js'

import {Tabs, Tab} from 'material-ui/Tabs';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

import './index.css'
import 'react-table/react-table.css'

export class EntityView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
        data: props.data,
        isNew: props.isNew
    };
  }

  setIsNew(isNew) {
    this.setState({
      isNew: isNew
    })
  }

  onSave() {
    this.props.saveEntity(this.state.data, this.state.isNew, this);
  }

  render() {
    const {data} = this.state;

    var template = this.props.template;

    return (
      <MuiThemeProvider>
        <div>
          <div className="row">
            <label className="col-sm-2 col-form-label">Title</label>
            <div className="col-sm-4">{data != null ? data.title : ''}</div>
            <label className="col-sm-2 col-form-label">Name</label>
            <div className="col-sm-4">{template != null ? template.name : ''}</div>
          </div>
          <div className="row">
            <label className="col-sm-2 col-form-label">Created</label>
            <div className="col-sm-4">{data != null ? data.dateCreatedAsString : ''}</div>
            <label className="col-sm-2 col-form-label">Source</label>
            <div className="col-sm-4">{template != null ? template.source : ''}</div>
          </div>
          <div className="row">
            <label className="col-sm-2 col-form-label">Last Modified</label>
            <div className="col-sm-4">{data != null ? data.lastModifiedAsString : ''}</div>
            <label className="col-sm-2 col-form-label">Version</label>
            <div className="col-sm-4">{template != null ? template.version : ''}</div>
          </div>
          <div className="row">
            <label className="col-sm-2 col-form-label">Key</label>
            <div className="col-sm-4">{data != null ? data.key : ''}</div>
            <label className="col-sm-2 col-form-label">Revision</label>
            <div className="col-sm-4">{data != null ? data.objectVersion : ''}</div>
          </div>
        </div>
        { template != null && data != null &&
        <Tabs>
          {template.sectionsAsList.map((section) => {return renderSection(section, getElement(section.name, data.sectionsAsList))})}
          <Tab label="linkages">
            <LinkageView linkages={data.linkageSection.linkagesAsList} />
          </Tab>
        </Tabs>
        }
        <div className="form-buttons">
          <IconButton icon="/images/svg/save.svg" title="Add Record" onClick={this.onSave.bind(this)} />
        </div>
      </MuiThemeProvider>
    )
  }

};

EntityView.defaultProps = {
  isNew: true
}

function getElement(name, data)
{
  var result;

  if (data != null)
  {
    for (var ii=0; ii<data.length; ii++)
    {
      if (name === data[ii].name)
      {
        result = data[ii];
        break;
      }
    }
  }

  return result;
}

function renderSection(section, data) {

    var view;

    if (section.sectionsAsList.length === 0 && section.recordsetsAsList.length === 1)
    {
      view = renderRecordset(section.recordsetsAsList[0], data);

    } else {
      view = (
        <Tab key={section.key} objectkey={section.key} label={section.name}>
          <Tabs>
            {section.sectionsAsList.map((item) => {return renderSection(item, getElement(item.name, data.sectionsAsList))})}
            {section.recordsetsAsList.map((item) => {return renderRecordset(item, data)})}
          </Tabs>
        </Tab>
      );
    }

    return view;
}

function renderRecordset(recordset, data) {
  var recordsetdata = getElement(recordset.name, data.recordsetsAsList);

  if (recordset.minRecords === 1 && recordset.maxRecords === 1)
  {
    var record;

    // Use First Record
    if (recordsetdata != null && recordsetdata.allRecords != null && recordsetdata.allRecords.length >= 1) {
      record = recordsetdata.allRecords[0];
    }

    return (
      <Tab label={recordset.name}>
        <RecordView
          definition={recordset.fieldDefinitions}
          record={record}
          key={record.key}
        />
      </Tab>
    );
  }
  else
  {
    return (
      <Tab label={recordset.name}>
        <RecordsetView
          recordset={recordset}
          data={recordsetdata}
          key={recordset.key}
        />
      </Tab>
    );
  }
}

String.prototype.toProperCase = function () {
    return this.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
};
