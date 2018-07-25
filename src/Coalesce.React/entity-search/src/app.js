import React from 'react'
import Menu from 'common-components/lib/components/Menu'
import { loadTemplates, loadTemplate } from 'common-components/lib/js/templateController.js';
import { DialogMessage, DialogLoader, DialogTemplateSelection } from 'common-components/lib/components/dialogs'

// Theme Imports
import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles'; // v1.x
import { MuiThemeProvider as V0MuiThemeProvider} from 'material-ui';
import { getDefaultTheme } from 'common-components/lib/js/theme'
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import { loadJSON } from 'common-components/lib/js/propertyController'

import {SearchCreator} from './searchcreator.js'
import {SearchResults} from './results.js'

// TODO Refactor this out
var karafRootAddr;

if (window.location.port == 3000) {
  karafRootAddr  = 'http://' + window.location.hostname + ':8181';
} else {
  karafRootAddr  = '';
}

export class App extends React.Component {

  constructor(props) {
    super(props);

    this.handleTemplateLoad = this.handleTemplateLoad.bind(this);

    this.state = {
      cache: [],
      key: null,
      results: null,
      properties: null,
      filterCriterias: [],
      filterkey: 0,
      criteriaKey: 0,
      //groups: []
    }

  }

  componentDidMount() {

    var that = this;

    loadJSON('theme').then((value) => {
      that.setState({
        theme: getMuiTheme(value)
      })
    }).catch((err) => {
      //console.log("Loading Theme: " + err);
    })

    fetch(karafRootAddr + '/cxf/data/templates/998b040b-2c39-4c98-9a9d-61d565b46e28/recordsets/CoalesceEntity/fields')
      .then(res => res.json())
      .then(definition => {

        const { cache } = this.state;

        var recordsets = [];
        recordsets.push({name: 'CoalesceEntity', definition: definition});

        cache['CoalesceEntity'] = {
          recordsets: recordsets,
          name: ''
        };

        this.setState({
          key: 'CoalesceEntity',
          cache: cache,
           groupData: {
                        operator: '!=',
                        criteria: [{
                           key: 'CoalesceEntity',
                           recordset: 'CoalesceEntity',
                           field: 'name',
                           operator: '!=',
                           value: '',
                           matchCase: false}]
                       }
        });

    }).catch(function(error) {
      this.setState({
        error: "Loading Common Fields: " + error
      });
    });

    loadTemplates().then((templates) => {
      that.setState({
        templates: templates,
      })
    }).catch(function(error) {
      this.setState({
        error: "Loading Templates: " + error
      });
    });
  }

  handleTemplateLoad(key) {

    const that = this;
    const { cache } = this.state;

    if (cache[key] == null)
    {
      console.log("Loading Template: " + key);

      loadTemplate(key).then(template => {

        var recordsets = [].concat(cache['CoalesceEntity'].recordsets);

        // Get Other Recordsets
        template.sectionsAsList.forEach(function(section) {
          recordsets = recordsets.concat(getRecordsets(section));
        });

        cache[key] = {
          recordsets: recordsets,
          name: template.name
        };

        console.log("APP Loading Template", recordsets);

        that.setState({
          key: key,
          groupData: {
              operator: '!=',
              criteria: [{
                 key: key,
                 recordset: 'CoalesceEntity',
                 field: 'name',
                 operator: '!=',
                 value: cache[key].name,
                 matchCase: false}]
             }
          });

        }).catch((err) => {
          that.setState({
            error: "Failed Loading Template: " + err
          })
        })
    } else {
      console.log("Loading Template (Cached): " + key);

      that.setState({
        key: key,
        });
    }
  }

  render() {

    const { cache, key, results, properties } = this.state;
   // console.log("App creating new filter creator", cache, key);
    return (
      <div>
        <MuiThemeProvider theme={createMuiTheme(this.state.theme)}>
          <V0MuiThemeProvider muiTheme={this.state.theme}>
        <Menu logoSrc={this.props.pjson.icon} title={this.props.pjson.title} items={[
          {
            id: 'select',
            name: 'Select',
            img: "/images/svg/template.svg",
            title: 'Select Template',
            onClick: () => {
              this.setState({promptTemplate: true})
            }
          }, {
            id: 'load',
            name: 'Load',
            img: "/images/svg/load.svg",
            title: 'Load Saved Criteria Selection',
            onClick: () => {
              this.setState({error: "(Comming Soon!!!) This will allow you to load previously saved criteria."});
            }
          }, {
            id: 'save',
            name: 'Save',
            img: "/images/svg/save.svg",
            title: 'Save Criteria Selection',
            onClick: () => {
              this.setState({error: "(Comming Soon!!!) This will allow you to save criteria."});
            }
          }, {
            id: 'reset',
            name: 'Reset',
            img: "/images/svg/erase.svg",
            title: 'Reset Criteria',
            onClick: () => {
              /*this.setState({
                query: [{
                    recordset: 'CoalesceEntity',
                    field: 'name',
                    operator: '=',
                    value: cache[key].name,
                    matchCase: true
                  }]
              })*/
              console.log("Reset Criteria");
            }
          }
        ]}/>
          <div>
            { cache[key] != null &&
                        <SearchCreator
                          recordsets={cache[key].recordsets}
                          onSearch={this.props.onSearch}
                          groupRecordSet = 'CoalesceEntity'
                          currentkey = {0}
                          criteriaKey = {0}
                          groupData = {this.state.groupData}
                          />
            }
            { results != null &&
              <SearchResults
                data={results}
                properties={properties}
                url={this.props.karafRootAddr}
              />
            }
            <DialogMessage
              title="Error"
              opened={this.state.error != null}
              message={this.state.error}
              onClose={() => {this.setState({error: null})}}
            />
            <DialogLoader
              title={this.state.loading}
              opened={this.state.loading != null}
            />
            <DialogTemplateSelection
              templates={this.state.templates}
              opened={this.state.promptTemplate}
              onClose={() => {this.setState({promptTemplate: false});}}
              onClick={this.handleTemplateLoad}
            />
            </div>
          </V0MuiThemeProvider>
          </MuiThemeProvider>

      </div>
    )
  }
}

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

