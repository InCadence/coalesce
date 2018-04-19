import "babel-polyfill";

import * as React from "react";
import * as ReactDOM from "react-dom";
import Popup from 'react-popup';
import {registerLoader, registerErrorPrompt, registerPrompt} from 'common-components/lib/register.js'
import {SearchResults} from './results.js'
import ReactTable from 'react-table'
import { searchComplex } from 'common-components/lib/js/searchController';
import { getRootKarafUrl } from 'common-components/lib/js/common'
import {IconButton} from 'common-components/lib/components/IconButton'

import {Menu} from 'common-components/lib/index.js'
import 'common-components/bootstrap/css/bootstrap.min.css'

import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'

var pjson = require('../package.json');
document.title = pjson.title;

var enums;
var enumCols = [
  {
    key: "metadata.enumname",
    Header: 'Name',
    accessor: 'values[0]'
  },{
    key: "metadata.description",
    Header: 'Description',
    accessor: 'values[1]'
  },{
    key: "CoalesceEntity.datecreated",
    Header: 'Created',
    accessor: 'values[2]'
  },{
    key: "CoalesceEntity.lastmodified",
    Header: 'Last Modified',
    accessor: 'values[3]'
  }
]
//var enumCols = ["metadata.enumname", "metadata.description", "CoalesceEntity.datecreated", "CoalesceEntity.lastmodified"];
var values = [];
var valueCols = [
  {
    key: "values.value",
    Header: 'Value',
    accessor: 'values[0]'
  },{
    key: "values.description",
    Header: 'Description',
    accessor: 'values[1]'
  },{
    key: "values.associatedkeys",
    Header: 'Associated Keys',
    accessor: 'values[2]'
  },{
    key: "values.associatedvalues",
    Header: 'Associated Values',
    accessor: 'values[3]'
  }
];

var rootUrl = getRootKarafUrl();

registerLoader(Popup);
registerErrorPrompt(Popup);
registerPrompt(Popup);

ReactDOM.render(
    <Popup />,
    document.getElementById('popupContainer')
);

function loadEnumerations() {

  if (enums == null) {

    var query = {
      "pageSize": 200,
      "pageNumber": 1,
      "propertyNames": enumCols.map((item) => item.key),
      "group": {
        "operator": "AND",
        "criteria": [{
          'recordset': 'CoalesceEntity',
          'field': 'name',
          'operator': '=',
          'value': 'Enumeration'
        }]
      }
    };

    Popup.plugins().loader("Loading Enumerations...");

    searchComplex(query).then(response => {
        enums = response;

        Popup.close();
        renderEnumerations(response);
      }).catch(function(error) {
        Popup.plugins().promptError("(FAILED) Loading Enumerations: " + error);
    });
  } else {
    renderEnumerations(enums);
  }
}

function renderEnumerations(data) {
  ReactDOM.render(
    <div className="ui-widget">
      <div className="ui-widget-header">
      Enumerations
      </div>
      <div className="ui-widget-content">
        <SearchResults
          data={data}
          properties={enumCols}
          createButtons={(row) => {
            return [
              <IconButton icon="/images/svg/view.svg" title="View Values" onClick={() => {loadValues(row._original.entityKey)}} />,
              <IconButton icon="/images/svg/edit.svg" title="Edit Enumeration" onClick={() => {window.open("/entityeditor/?entitykey=" + row._original.entityKey)}} />
            ];
          }}
        />
      </div>
    </div>,
    document.getElementById('main'));
}

function loadValues(key) {

  if (values[key] == null) {

    var query = {
      "pageSize": 200,
      "pageNumber": 1,
      "propertyNames": valueCols.map((item) => item.key),
      "group": {
        "operator": "AND",
        "criteria": [{
          'recordset': 'CoalesceEntity',
          'field': 'objectkey',
          'operator': '=',
          'value': key
        }]
      }
    };

    Popup.plugins().loader("Loading Enumeration's Values...");

    searchComplex(query).then(response => {

        Popup.close();

          renderValues(key, response.hits);

    }).catch(function(error) {
      Popup.plugins().promptError("(FAILED) Loading Enumeration Values: " + error);
    });
  } else {
    renderValues(key, values[key]);
  }
}

function renderValues(key, data) {
  ReactDOM.render(
    <div className="ui-widget">
      <div className="ui-widget-header">
      Values
      </div>
      <div className="ui-widget-content">
        <SearchResults
          data={data}
          properties={valueCols}
          createButtons={(row) => {
            return [
              <img src="/images/svg/view.svg" alt="view" title="View Entity" className="coalesce-img-button small enabled" onClick={() => loadAssociatedValues(key, row.associatedkeys, row.associatedvalues)}/>,
            ];
          }}
        />
        <div className='form-buttons'>
          <IconButton icon="/images/svg/back.svg" title="Back" onClick={() => {loadEnumerations()}} />
          <IconButton icon="/images/svg/edit.svg" title="Edit" onClick={() => {window.open(rootUrl + "/entityeditor/?entitykey=" + key)}} />
        </div>
      </div>
    </div>,
    document.getElementById('main'));
}

function loadAssociatedValues(key, keys, values) {

  var data = [];

  if (keys != null && values != null)
  {
    var keyList = keys.split(',');
    var valueList = values.split(',');

    for (var ii=0; ii<keyList.length && ii<valueList.length; ii++) {
      data.push({
        'name': keyList[ii],
        'value': valueList[ii]
      });
    }
  }

  ReactDOM.render(
      <div className="ui-widget">
        <div className="ui-widget-header">
        Associated Values
        </div>
        <div className="ui-widget-content">
          <ReactTable
            data={data}
            columns={[
                {
                  Header: 'Name',
                  accessor: 'name'
                },{
                  Header: 'Value',
                  accessor: 'value'
                }
              ]}
          />
          <div className='form-buttons'>
            <IconButton icon="/images/svg/back.svg" title="Back" onClick={() => {() => loadValues(key)}} />,
            <IconButton icon="/images/svg/edit.svg" title="Edit" onClick={() => {() => loadValues(key)}} />,
          </div>
        </div>
      </div>,
      document.getElementById('main')
  );
}

// Default Component


ReactDOM.render(
    <Menu logoSrc={pjson.icon} title={pjson.title} items={[/* No Options */]}/>,
    document.getElementById('myNavbar')
);

loadEnumerations();
