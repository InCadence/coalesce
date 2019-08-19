import React from 'react';
import ReactDOM from 'react-dom';
import { App } from './app'
import { loadJSON } from 'coalesce-components/lib/js/propertyController';
import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles'; // v1.x

import 'coalesce-components/bootstrap/css/bootstrap.min.css'
import 'coalesce-components/css/coalesce.css'
import 'react-table/react-table.css'

var pjson = require('../package.json');
document.title = pjson.title;

// TODO This is test code for logging the size of an object
/*
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
*/

function loadApplication(theme) {
  ReactDOM.render(
    <MuiThemeProvider theme={createMuiTheme(theme)}>
      <App pjson={pjson} />
    </MuiThemeProvider>,
    document.getElementById('main')
  );
}

loadJSON('theme').then((theme) => {
  loadApplication(createMuiTheme(theme));
}).catch((err) => {
  console.log("Loading Theme: " + err);
  loadApplication(createMuiTheme({}));
})

/* TODO Remove this code (Its an example of how to submit a OGC filter as XML)
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
    switch (criteria.operator) {
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
      Popup.plugins().promptError("Executing Search: " + error);
  });
}
//*/
