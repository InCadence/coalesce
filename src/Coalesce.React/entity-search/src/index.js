import React from 'react';
import ReactDOM from 'react-dom';
import Popup from 'react-popup';
import {Menu} from 'common-components/lib/menu.js'
import {FilterCreator} from './filtercreator.js'
import {SearchResults} from './results.js'
import {registerLoader, registerTemplatePrompt} from 'common-components/lib/register.js'

import './index.css'
import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'

var karafRootAddr;

if (window.location.port == 3000) {
  karafRootAddr  = 'http://' + window.location.hostname + ':8181';
} else {
  karafRootAddr  = 'http://' + window.location.hostname + ':' + window.location.port;
}

var cache = {};
var template = 'CoalesceEntity';

// Prompt user for template to populate the criteria controls
function promptForTemplate() {

  Popup.plugins().promptTemplate('load', 'Enumeration', function (value) {

    ReactDOM.unmountComponentAtNode(document.getElementById('main'));

    if (cache[value] == null)
    {
      fetch(karafRootAddr + '/cxf/data/templates/' + value)
          .then(res => res.json())
          .then(template => {

            var recordsets = [].concat(cache['CoalesceEntity']);

            // Get Other Recordsets
            template.sectionsAsList.forEach(function(section) {
              recordsets = recordsets.concat(getRecordsets(section));
            });

            cache[value] = recordsets;
            console.log('Size of client cache: ' + memorySizeOf(cache));

            // Add CoalesceEntity attributes as a recordset
            ReactDOM.render(
                <FilterCreator
                  recordsets={recordsets}
                  onSearch={searchComplex}
                />,
                document.getElementById('main')
            );

          })
    } else {
      ReactDOM.render(
          <FilterCreator
            recordsets={cache[value]}
            onSearch={searchComplex}
          />,
          document.getElementById('main')
      );
    }

    template = value;

  });

}

// TODO This is test code for logging the size of an object
function memorySizeOf(obj) {
    var bytes = 0;

    function sizeOf(obj) {
        if(obj !== null && obj !== undefined) {
            switch(typeof obj) {
            case 'number':
                bytes += 8;
                break;
            case 'string':
                bytes += obj.length * 2;
                break;
            case 'boolean':
                bytes += 4;
                break;
            case 'object':
                var objClass = Object.prototype.toString.call(obj).slice(8, -1);
                if(objClass === 'Object' || objClass === 'Array') {
                    for(var key in obj) {
                        if(!obj.hasOwnProperty(key)) continue;
                        sizeOf(obj[key]);
                    }
                } else bytes += obj.toString().length * 2;
                break;
              default:
              // Do Nothing
            }
        }
        return bytes;
    };

    function formatByteSize(bytes) {
        if(bytes < 1024) return bytes + " bytes";
        else if(bytes < 1048576) return(bytes / 1024).toFixed(3) + " KiB";
        else if(bytes < 1073741824) return(bytes / 1048576).toFixed(3) + " MiB";
        else return(bytes / 1073741824).toFixed(3) + " GiB";
    };

    return formatByteSize(sizeOf(obj));
};

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
  Popup.plugins().loader('Searching...');

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
      renderError("Executing Search: " + error);
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
        img: require('common-components/img/template.ico'),
        title: 'Select Template',
        onClick: promptForTemplate
      }, {
        id: 'load',
        name: 'Load',
        img: require('common-components/img/load.ico'),
        title: 'Load Saved Criteria Selection',
        onClick: () => {
          renderError("(Comming Soon!!!) This will allow you to load previously saved criteria.")
        }
      }, {
        id: 'save',
        name: 'Save',
        img: require('common-components/img/save.ico'),
        title: 'Save Criteria Selection',
        onClick: () => {
          renderError("(Comming Soon!!!) This will allow you to save criteria.")
        }
      }, {
        id: 'reset',
        name: 'Reset',
        img: require('common-components/img/erase.ico'),
        title: 'Reset Criteria',
        onClick: () => {

          ReactDOM.unmountComponentAtNode(document.getElementById('main'));

          ReactDOM.render(
              <FilterCreator
                recordsets={cache[template]}
                onSearch={searchComplex}
                />,
              document.getElementById('main')
          );
        }
      }
    ]}/>,
    document.getElementById('myNavbar')
);

registerLoader(Popup);

fetch(karafRootAddr + '/cxf/data/templates')
  .then(res => res.json())
  .then(data => {
    registerTemplatePrompt(Popup, karafRootAddr, data);
}).catch(function(error) {
    renderError("Loading Templates: " + error);
});

// Populate w/ Base fields that a common to all templates
// Because its common fields the GUID can be random (or hard coded)
fetch(karafRootAddr + '/cxf/data/templates/998b040b-2c39-4c98-9a9d-61d565b46e28/recordsets/CoalesceEntity/fields')
  .then(res => res.json())
  .then(definition => {

    var recordsets = [];
    recordsets.push({name: 'CoalesceEntity', definition: definition});

    cache['CoalesceEntity'] = recordsets;

    ReactDOM.render(
        <FilterCreator
          recordsets={recordsets}
          onSearch={searchComplex}
          />,
        document.getElementById('main')
    );

}).catch(function(error) {
    renderError("Loading Common Fields: " + error);
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

  Popup.plugins().loader('Searching...');

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
      renderError("Executing Search: " + error);
  });
}
