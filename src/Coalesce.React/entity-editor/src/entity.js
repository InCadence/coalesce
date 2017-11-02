import React from 'react';
import {Accordion} from 'common-components/lib/accordion.js'
import {LinkageView} from './linkagetable.js'
import {RecordsetView, RecordView} from './recordset.js'
import $ from 'jquery'

import './index.css'
import 'react-table/react-table.css'

export class EntityView extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;
    }

  componentDidMount() {

    const {isNew} = this.state;
    const {objectkey, rootUrl} = this.props;

    if (isNew) {
      fetch(rootUrl + '/cxf/data/templates/' + objectkey)
        .then(res => res.json())
        .then(template => {

          fetch(rootUrl + '/cxf/data/templates/' + objectkey + "/new")
            .then(res => res.json())
            .then(data => {
              this.setState({data: data})
              this.setState({template: template})
            }).catch(function(error) {
              alert('Failed to create new entity (' + objectkey + ')');
            });
        }).catch(function(error) {
            alert('Failed to load template (' + objectkey + ')');
        });
    } else {
      fetch(rootUrl + '/cxf/data/entity/' + objectkey)
        .then(res => res.json())
        .then(data => {

          fetch(rootUrl + '/cxf/data/templates/' + data.name + '/' + data.source + '/' + data.version)
            .then(res => res.json())
            .then(template => {
              this.setState({data: data})
              this.setState({template: template})
            }).catch(function(error) {
              alert('Failed to load template (' + data.name + ', ' + data.source + ', ' + data.version + ')');
            });
        }).catch(function(error) {
          alert('Failed to load entity (' + objectkey + ')');
        });
    }
  }

  onSave() {
    const {data, isNew} = this.state;
    const {rootUrl} = this.props.objectkey;

    $.ajax({
      type : ((isNew) ? "PUT" : "POST"),
      url: rootUrl + '/cxf/data/entity/' + data.key,
      data : JSON.stringify(data),
      contentType : "application/json; charset=utf-8",
      success : function(data, status, jqXHR) {
        alert('Saved');
      },
      error : function(jqXHR, status) {
        alert('Failed: ' + JSON.stringify(jqXHR));
        // error handler
        console.log(jqXHR);
      }
    });

    this.setState({
      isNew: false
    })
  }

  render() {
    const {data, template} = this.state;

    var sections = [];
    var linkages;

    if (template != null) {

      // Render Sections
      template.sectionsAsList.forEach(function(section) {
        sections.push(renderSection(section, getElement(section.name, data.sectionsAsList)));
      });

      if (data != null) {
        linkages = (<LinkageView linkages={data.linkageSection.linkagesAsList} />);
      }
    }

    return (
      <div>
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
        {linkages}
        {sections}
        <div className="form-buttons">
          <button className='mm-popup__btn mm-popup__btn--success' onClick={this.onSave.bind(this)}>save</button>
        </div>
      </div>
    )
  }

};

EntityView.defaultProps = {
  isNew: true,
  rootUrl: 'http://' + window.location.hostname + ':8181'
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

    var sections = [];

    // Render Nested Sections
    section.sectionsAsList.forEach(function(childSection) {
      sections.push(renderSection(childSection, getElement(section.name, data.sectionsAsList)));
    });

    var recordsets = [];

    // Render Recordsets
    section.recordsetsAsList.forEach(function(recordset) {
      var view;
      var recordsetdata = getElement(recordset.name, data.recordsetsAsList);

      if (recordset.maxRecords === 1)
      {
        var record;

        // Use First Record
        if (recordsetdata != null && recordsetdata.allRecords != null && recordsetdata.allRecords.length >= 1) {
          record = recordsetdata.allRecords[0];
        }

        view = React.createElement(RecordView, {
          record: record,
          definition: recordset.fieldDefinitions,
          key: record.key
        });
      }
      else
      {
        view = React.createElement(RecordsetView, {
          recordset: recordset,
          data: recordsetdata,
          key: recordset.key
        });
      }

      recordsets.push(view);
    });

    var label = section.name.toProperCase() + ' Section';

    return (
      <Accordion key={section.key} objectkey={section.key} label={label}>
        <div className="section">
          <div>{sections}</div>
          <div>{recordsets}</div>
        </div>
      </Accordion>
    )
}

String.prototype.toProperCase = function () {
    return this.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
};
