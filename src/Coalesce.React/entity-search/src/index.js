import React from 'react';
import ReactDOM from 'react-dom';
import Popup from 'react-popup';
import {PromptTemplate} from 'common-components/lib/prompt-template.js'
import {Menu} from 'common-components/lib/menu.js'
import {FilterCreator} from './filtercreator.js'
import ReactTable from 'react-table'
import {Toggle} from 'common-components/lib/toggle.js'
import {Collapse} from 'react-collapse';


import $ from 'jquery'

import './index.css'
import 'common-components/css/popup.css'

var karafRootAddr = 'http://' + window.location.hostname + ':8181';

function promptForTemplate() {

  Popup.plugins().promptTemplate('load', 'Enumeration', function (value) {
    fetch(karafRootAddr + '/cxf/data/templates/' + value)
        .then(res => res.json())
        .then(template => {

          ReactDOM.unmountComponentAtNode(document.getElementById('main'));

          fetch(karafRootAddr + '/cxf/data/templates/' + value + '/recordsets/CoalesceEntity/fields')
              .then(res => res.json())
              .then(definition => {

                var recordsets = [];
                recordsets.push({name: 'CoalesceEntity', definition: definition});

                // Get Other Recordsets
                template.sectionsAsList.forEach(function(section) {
                  recordsets = recordsets.concat(getRecordsets(section));
                });

                // Add CoalesceEntity attributes as a recordset
                ReactDOM.render(
                    <FilterCreator
                      recordsets={recordsets}
                      onSearch={searchComplex}
                    />,
                    document.getElementById('main')
                );

              })

        })
  });

}

function getRecordsets(section) {

  var results = [];

  section.sectionsAsList.forEach(function(section) {
    results = results.concat(this.processrecordsets(section));
  });

  // Render Recordsets
  section.recordsetsAsList.forEach(function(recordset) {
    results.push({name: recordset.name, definition: recordset.fieldDefinitions});
  });

  return results;
}

function openEditor(key, e) {
  window.open(
    karafRootAddr + "/entityeditor/?entitykey=" + key,
    '_blank' // <- This is what makes it open in a new window.
  );
}



function searchOGC(data, e) {

  var properties = [];

  var body = [
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
    "<ogc:Filter xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\">"
  ];

  if (data.length > 1) {
    body.push("<ogc:And>");
  }

  data.forEach(function (criteria) {
    switch (criteria.comparer) {
      case "=":
        body.push("<ogc:PropertyIsEqualTo matchCase=\"" + criteria.matchCase + "\">");
        body.push(" <ogc:PropertyName>" + criteria.recordset + "." + criteria.field +  "</ogc:PropertyName>");
        body.push(" <ogc:Literal>" + criteria.value + "</ogc:Literal>");
        body.push("</ogc:PropertyIsEqualTo>");
        break;
      case "!=":
        body.push("<ogc:PropertyIsNotEqualTo matchCase=\"" + criteria.matchCase + "\">");
        body.push(" <ogc:PropertyName>" + criteria.recordset + "." + criteria.field +  "</ogc:PropertyName>");
        body.push(" <ogc:Literal>" + criteria.value + "</ogc:Literal>");
        body.push("</ogc:PropertyIsNotEqualTo>");
        break;
      default:

    }

    properties.push(criteria.recordset + "." + criteria.field);
  });

  if (data.length > 1) {
    body.push("</ogc:And>");
  }

  body.push("</ogc:Filter>")

  data = {
    "filter":body.join(""),
    "pageSize":200,
    "pageNumber":1,
    "includeHidden":false,
    "sortBy":[
      {
        "propertyName":"coalesceentity.name",
        "sortOrder":"ASC"
      }
    ],
    "propertyNames":properties
  };

  console.log(JSON.stringify(data));

  $.ajax({
          type : "POST",
          url : karafRootAddr + '/cxf/data/search/ogc',
          data : JSON.stringify(data),
          contentType : "application/json; charset=utf-8",
          success : function(data, status, jqXHR) {
            processSearchResults(data, properties);
          },
          error : function(jqXHR, status) {
            // error handler
            console.log(jqXHR);
          }
        });
}

function searchComplex(data, e) {

  var properties = [];
    data.forEach(function (criteria) {
    properties.push(criteria.recordset + "." + criteria.field);
  });

  var query = {
    "pageSize": 200,
    "pageNumber": 1,
    "propertyNames": properties,
    "group": {
      "booleanComparer": "AND",
      "criteria": data
    }
  };

  $.ajax({
  				type : "POST",
  				url : karafRootAddr + '/cxf/data/search/complex',
  				data : JSON.stringify(query),
  				contentType : "application/json; charset=utf-8",
  				success : function(data, status, jqXHR) {
            processSearchResults(data, properties);
  				},
  				error : function(jqXHR, status) {
  					// error handler
  					console.log(jqXHR);
  				}
  			});

  /*
  fetch(karafRootAddr + '/cxf/data/search', {
    method: 'POST',
    body: JSON.stringify(data),
    "Content-Type" : "application/json; charset=utf-8"
  })
    .then(res => res.json())
    .then(response => {
      alert(JSON.stringify(response));
  });
  */
}

function processSearchResults(data, properties) {


  var columns = [
    {
      Header: 'Key',
      accessor: 'entityKey'
    }
  ];

  properties.forEach(function (property) {

    var parts = property.split(".");

    columns.push({
      Header: parts[1],
      accessor: parts[1]
    })
  });

  columns.push({
    Header: '',
    accessor: 'button',
    Cell: (cell) => (
      <button className="form-control" title="Delete" onClick={openEditor.bind(this, cell.row.entityKey)}>
        View
      </button>
    )
  });


  if (data.result[0].status === "SUCCESS") {
    var tabledata;
    if (data.result[0].result.hits != null) {
      tabledata = data.result[0].result.hits;

      console.log(JSON.stringify(data.result[0].result));

      tabledata.forEach(function (hit) {
        for (var ii=1; ii<columns.length - 1; ii++) {
            hit[columns[ii].accessor] = hit.values[ii-1];
        }
      });

    }

    ReactDOM.render(
            <ReactTable
              data={tabledata}
              columns={columns}
            />,
      document.getElementById('results')
    );
  } else {
    alert(data.result[0].error);
  }
}

ReactDOM.render(
    <Popup />,
    document.getElementById('popupContainer')
);

ReactDOM.render(
    <Menu items={[
      {
        id: 'select',
        name: 'Select',
        onClick: promptForTemplate
      }, {
        id: 'load',
        name: 'Load',
        onClick: () => {
          alert("TODO: Not Implemented");
        }
      }, {
        id: 'save',
        name: 'Save',
        onClick: () => {
          alert("TODO: Not Implemented");
        }
      }, {
        id: 'reset',
        name: 'Reset',
        onClick: () => {
          alert("TODO: Not Implemented");
        }
      }
    ]}/>,
    document.getElementById('myNavbar')
);

/** Prompt plugin */
Popup.registerPlugin('promptTemplate', function (buttontext, defaultValue, callback) {
    let promptValue = null;
    let promptChange = function (value) {
        promptValue = value;
    };

    this.create({
        title: "Select Template",
        content: <PromptTemplate onChange={promptChange} value={defaultValue} />,
        buttons: {
            left: ['cancel'],
            right: [{
                text: buttontext,
                className: 'success',
                action: function () {
                    callback(promptValue);
                    Popup.close();
                }

            }]
        }
    });
});

promptForTemplate();
