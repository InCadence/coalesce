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
                      onSearch={search}
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
  window.location.href = karafRootAddr + "/entityeditor/?entitykey=" + key;
}

function search(data, e) {

  $.ajax({
  				type : "POST",
  				url : karafRootAddr + '/cxf/data/search',
  				data : JSON.stringify(data),
  				contentType : "application/json; charset=utf-8",
  				success : function(data, status, jqXHR) {

            var columns = [
              {
                Header: 'Key',
                accessor: 'entityKey'
              },
              {
                Header: 'Title',
                accessor: 'title'
              },
              {
                Header: 'Name',
                accessor: 'name'
              },
              {
                Header: 'Source',
                accessor: 'source'
              },
              {
                Header: '',
                accessor: 'button',
                Cell: (cell) => (
                  <button className="form-control" title="Delete" onClick={openEditor.bind(this, cell.row.entityKey)}>
                    View
                  </button>
                )
              },
            ];

            if (data.result[0].status === "SUCCESS") {
              var tabledata;
              if (data.result[0].result.hits != null) {
                tabledata = data.result[0].result.hits;
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

ReactDOM.render(
    <Popup />,
    document.getElementById('popupContainer')
);

ReactDOM.render(
    <Menu items={[
      {
        id: 'select',
        name: 'select',
        onClick: promptForTemplate
      }, {
        id: 'load',
        name: 'load',
        onClick: () => {
          alert("TODO: Not Implemented");
        }
      }, {
        id: 'save',
        name: 'save',
        onClick: () => {
          alert("TODO: Not Implemented");
        }
      }, {
        id: 'reset',
        name: 'reset',
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
