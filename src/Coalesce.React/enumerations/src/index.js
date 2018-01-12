import * as React from "react";
import * as ReactDOM from "react-dom";
import Popup from 'react-popup';
import {registerLoader, registerErrorPrompt, registerPrompt} from 'common-components/lib/register.js'
import {Menu} from 'common-components/lib/menu.js'
import {SearchResults} from './results.js'
import ReactTable from 'react-table'

import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'

var enums;
var enumCols = ["metadata.enumname", "metadata.description", "CoalesceEntity.datecreated", "CoalesceEntity.lastmodified"];
var values = [];
var valueCols = [/*"values.ordinal",*/ "values.value", "values.description", "values.associatedkeys", "values.associatedvalues"];

var rootUrl;

if (window.location.port == 3000) {
  rootUrl  = 'http://' + window.location.hostname + ':8181';
} else {
  rootUrl  = 'http://' + window.location.hostname + ':' + window.location.port;
}

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
      "propertyNames": enumCols,
      "group": {
        "booleanComparer": "AND",
        "criteria": [{
          'recordset': 'CoalesceEntity',
          'field': 'name',
          'comparer': '=',
          'value': 'Enumeration'
        }]
      }
    };

    Popup.plugins().loader("Loading Enumerations...");

    fetch(rootUrl + '/cxf/data/search/complex', {
      method: "POST",
      body: JSON.stringify(query),
      headers: new Headers({
        'content-type': 'application/json; charset=utf-8'
      }),
    }).then(res => res.json())
      .then(data => {

        Popup.close();

        var results = data.result[0];

        if (results.status === 'SUCCESS') {

          enums = results.result;
          renderEnumerations(enums);
        } else {
          Popup.plugins().promptError("(FAILED) Loading Enumerations: " + results.error);
        }

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
              <img key={row.entityKey + '_view'} src="/images/svg/view.svg" alt="view" title="View Entity" className="coalesce-img-button small enabled" onClick={() => loadValues(row.entityKey)}/>,
              <img key={row.entityKey + '_edit'} src="/images/svg/edit.svg" alt="Edit" title="Edit Enumeration" className="coalesce-img-button small enabled" onClick={() => window.open(rootUrl + "/entityeditor/?entitykey=" + row.entityKey)}/>
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
      "propertyNames": valueCols,
      "group": {
        "booleanComparer": "AND",
        "criteria": [{
          'recordset': 'CoalesceEntity',
          'field': 'objectkey',
          'comparer': '=',
          'value': key
        }]
      }
    };

    Popup.plugins().loader("Loading Enumeration's Values...");

    fetch(rootUrl + '/cxf/data/search/complex', {
      method: "POST",
      body: JSON.stringify(query),
      headers: new Headers({
        'content-type': 'application/json; charset=utf-8'
      }),
    }).then(res => res.json())
      .then(data => {

        Popup.close();

        var results = data.result[0];

        values[key] = results.result;

        if (results.status === 'SUCCESS') {
          renderValues(key, results.result);

        } else {
          Popup.plugins().promptError("(FAILED) Loading Enumeration Values: " + results.error);
        }

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
          <img src="/images/svg/back.svg" alt="back" title="Back" className="coalesce-img-button enabled" onClick={() => loadEnumerations()}/>
          <img src="/images/svg/edit.svg" alt="Edit" title="Edit Enumeration" className="coalesce-img-button enabled" onClick={() => window.open(rootUrl + "/entityeditor/?entitykey=" + key)}/>
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
            <img src="/images/svg/back.svg" alt="back" title="Back" className="coalesce-img-button enabled" onClick={() => loadValues(key)}/>
            <img src="/images/svg/edit.svg" alt="Edit" title="Edit Enumeration" className="coalesce-img-button enabled" onClick={() => loadValues(key)}/>
          </div>
        </div>
      </div>,
      document.getElementById('main')
  );
}

// Default Component


ReactDOM.render(
    <Menu items={[/* No Options */]}/>,
    document.getElementById('myNavbar')
);

loadEnumerations();
