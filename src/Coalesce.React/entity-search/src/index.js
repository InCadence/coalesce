import React from 'react';
import ReactDOM from 'react-dom';
import Popup from 'react-popup';
import {PromptTemplate} from 'common-components/lib/prompt-template.js'
import {Menu} from 'common-components/lib/menu.js'
import {FilterCreator} from './filtercreator.js'
import {SearchResults} from './results.js'
import { HashLoader } from 'react-spinners';

import './index.css'
import 'common-components/css/popup.css'

var karafRootAddr = 'http://' + window.location.hostname + ':8181';

var cache = {};

// Prompt user for template to populate the criteria controls
function promptForTemplate() {

  Popup.plugins().promptTemplate('load', 'Enumeration', function (value) {
    fetch(karafRootAddr + '/cxf/data/templates/' + value)
        .then(res => res.json())
        .then(template => {

          ReactDOM.unmountComponentAtNode(document.getElementById('main'));

          var recordsets = [];
          recordsets.push({name: 'CoalesceEntity', definition: cache['CoalesceEntity']});

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

  });

}

// Recursive (nested sections) method to pull recordsets from a section
function getRecordsets(section) {

  var results = [];

  section.sectionsAsList.forEach(function(section) {
    results = results.concat(getRecordsets(section));
  });

  // Render Recordsets
  section.recordsetsAsList.forEach(function(recordset) {
    results.push({name: recordset.name, definition: recordset.fieldDefinitions});
  });

  return results;
}

// Submits the user's selected criteria.
function searchComplex(data, e) {

  // Create Query
  var query = {
    "pageSize": 200,
    "pageNumber": 1,
    "propertyNames": [],
    "group": {
      "booleanComparer": "AND",
      "criteria": data
    }
  };

  // Get additional columns
  data.forEach(function (criteria) {
    query.propertyNames.push(criteria.recordset + "." + criteria.field);
  });

  // Display Spinner
  Popup.plugins().loader();

  // Submit Query
  fetch(karafRootAddr + '/cxf/data/search/complex', {
    method: "POST",
    body: JSON.stringify(query),
    headers: new Headers({
      'content-type': 'application/json; charset=utf-8'
    }),
  }).then(res => res.json())
    .then(response => {
      renderResults(response, query.propertyNames);
  }).catch(function(error) {
      renderError(error);
  });
}

function renderError(error) {
  Popup.close();
  Popup.create({
      title: 'Error',
      content: error,
      className: 'alert',
      buttons: {
          right: ['ok']
      }
  }, true);
}

function renderResults(data, properties) {
  Popup.close();
  if (data.result[0].status === "SUCCESS") {
    ReactDOM.render(
            <SearchResults
              data={data.result[0].result}
              properties={properties}
            />,
    document.getElementById('results'));
  } else {
    Popup.create({
        title: 'Error',
        content: data.result[0].error,
        className: 'alert',
        buttons: {
            right: ['ok']
        }
    }, true);
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

Popup.registerPlugin('loader', function () {

    this.create({
        content:
          <center className='sweet-loading'>
            <HashLoader
              color={'#cc6600'}
              loading={true}
            />
          </center>,
          closeOnOutsideClick: false
    });
});

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

// TODO Remove this code (Its an example of how to submit a OGC filter as XML)
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

  var query = {
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

  console.log(JSON.stringify(query));

  Popup.plugins().loader();

  fetch(karafRootAddr + '/cxf/data/search/ogc', {
    method: "POST",
    body: JSON.stringify(query),
    headers: new Headers({
      'content-type': 'application/json; charset=utf-8'
    }),
  }).then(res => res.json())
    .then(response => {
      renderResults(response, query.propertyNames);
  }).catch(function(error) {
      renderError(error);
  });
}

// Populate w/ Base fields that a common to all templates
// Because its common fields the GUID can be random (or hard coded)
fetch(karafRootAddr + '/cxf/data/templates/998b040b-2c39-4c98-9a9d-61d565b46e28/recordsets/CoalesceEntity/fields')
    .then(res => res.json())
    .then(definition => {

      var recordsets = [];
      recordsets.push({name: 'CoalesceEntity', definition: definition});

      cache['CoalesceEntity'] = recordsets;

      console.log(JSON.stringify(cache));

      // Add CoalesceEntity attributes as a recordset
      ReactDOM.render(
          <FilterCreator
            recordsets={recordsets}
            onSearch={searchComplex}
          />,
          document.getElementById('main')
      );

    })
