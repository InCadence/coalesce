import * as React from "react";
import * as ReactDOM from "react-dom";
import { searchComplex } from 'common-components/lib/js/searchController';
import { App } from './app'

import { createMuiTheme } from '@material-ui/core/styles'
import { loadJSON } from 'common-components/lib/js/propertyController'

import 'common-components/bootstrap/css/bootstrap.min.css'
import 'common-components/css/coalesce.css'

var pjson = require('../package.json');
document.title = pjson.title;

const enumCols = [
  {
    key: "enummetadata.enumname",
    Header: 'Name',
    accessor: 'values',
    index: 0,
  },{
    key: "enummetadata.description",
    Header: 'Description',
    accessor: 'values',
    index: 1
  },{
    key: "CoalesceEntity.datecreated",
    Header: 'Created',
    accessor: 'values',
    index: 2
  },{
    key: "CoalesceEntity.lastmodified",
    Header: 'Last Modified',
    accessor: 'values',
    index: 3
  }
]

function loadEnumerations(theme) {

    var query = {
      "pageSize": 200,
      "pageNumber": 1,
      "propertyNames": enumCols.map((item) => item.key),
      "group": {
        "operator": "AND",
        "criteria": [{
          'recordset': 'CoalesceEntity',
          'field': 'name',
          'operator': 'EqualTo',
          'value': 'Enumeration'
        },{
          'recordset': 'enummetadata',
          'field': 'enumname',
          'operator': 'NullCheck',
          'not': true
        }]
      },
      "sortBy": [
          {
            "propertyName": enumCols[0].key,
            "sortOrder": "ASC"
          }
        ],
    };

    searchComplex(query).then(response => {
      ReactDOM.render(
          <App icon={pjson.icon} title={pjson.title} theme={theme} enums={response}/>,
          document.getElementById('myNavbar')
      );
    }).catch(function(err) {
      ReactDOM.render(
          <App icon={pjson.icon} title={pjson.title} theme={theme} error={"Loading Enums: " + err.message}/>,
          document.getElementById('myNavbar')
      );
    });
}

loadJSON('theme').then((data) => {
  loadEnumerations(createMuiTheme(data));
}).catch((err) => {
  console.log(`Failed Loading Theme: ${err.message}`);
  loadEnumerations();
})
