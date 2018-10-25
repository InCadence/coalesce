import React from 'react'
import Menu from 'common-components/lib/components/menu'
import { loadTemplates, loadTemplate } from 'common-components/lib/js/templateController.js';
import { getRootKarafUrl } from 'common-components/lib/js/common';
import { DialogMessage, DialogLoader, DialogOptions } from 'common-components/lib/components/dialogs'
import { searchComplex } from 'common-components/lib/js/searchController.js';
import uuid from 'uuid';

import FilterCreator from './filtercreator.js'
import {SearchResults} from './results.js'

const karafRootAddr = getRootKarafUrl();
const DEFAULT = 'CoalesceEntity';

export class App extends React.Component {

  constructor(props) {
    super(props);

    this.handleTemplateLoad = this.handleTemplateLoad.bind(this);
    this.handleError = this.handleError.bind(this);
    this.handleUpdate = this.handleUpdate.bind(this);
    this.createQuery = this.createQuery.bind(this);
    this.handleSearch = this.handleSearch.bind(this);
    this.handleSpinner = this.handleSpinner.bind(this);

    var cache = {};

    cache[DEFAULT] = {
      recordsets: [],
      name: ''
    };

    this.state = {
      cache: cache,
      key: DEFAULT,
      results: null,
      properties: [],
      query: this.createQuery()
    }
  }

  createQuery(name) {

    if (!name) {
      name = this.state && this.state.key ? this.state.cache[this.state.key].name : '' ;
    }

    return {
        operator: 'AND',
        criteria: [
          {
            key: uuid.v4(),
            recordset: DEFAULT,
            field: 'name',
            operator: 'EqualTo',
            value: name,
            matchCase: false
          }
        ],
        groups: []
       }
     }

  componentDidMount() {

    var that = this;

    this.loadFields('CoalesceEntity');
    this.loadFields('CoalesceLinkage');

    loadTemplates().then((templates) => {
      that.setState(() => {
        return {templates: templates}
      })
    }).catch(function(error) {
      that.handleError("Loading Templates: " + error);
    });
  }

  loadFields(key, idx) {

    var that = this;

    fetch(`${karafRootAddr}/templates/998b040b-2c39-4c98-9a9d-61d565b46e28/recordsets/${key}/fields`)
      .then(res => res.json())
      .then(definition => {

        const { cache } = this.state;

        cache[DEFAULT].recordsets.push({name: key, definition: definition});

        that.setState(() => {
          return {cache: cache}
        });

    }).catch(function(error) {
      that.handleError("Loading Common Fields: " + error);
    });
  }

  handleError(message) {
    this.setState(() => {return {
      error: message,
      loading: null,
      promptTemplate: false
    }});
  }

  handleUpdate(data, properties) {
    this.setState(() => {return {query: data, properties: properties}});
  }

  handleTemplateLoad(key) {

    const that = this;
    const { cache } = this.state;

    if (cache[key] == null)
    {
      console.log("Loading Template: " + key);

      loadTemplate(key).then(template => {

        var recordsets = []

        // Get Other Recordsets
        template.sectionsAsList.forEach(function(section) {
          recordsets = recordsets.concat(getRecordsets(section));
        });

        recordsets = recordsets.concat(cache[DEFAULT].recordsets);

        cache[key] = {
          recordsets: recordsets,
          name: template.name
        };

        console.log("APP Loading Template", recordsets);

        that.setState({
          key: key,
          query: this.createQuery(cache[key].name),
          promptTemplate: false,
          properties: []
          }
        );

        }).catch((err) => {
          that.handleError(`Failed Loading Template: ${key}`);
        })
    } else {
      console.log("Loading Template (Cached): " + key);

      that.setState({
        key: key,
        query: this.createQuery(cache[key].name),
        promptTemplate: false
        });
    }
  }

  render() {

    const { cache, key, results, properties, query } = this.state;
   // console.log("App creating new filter creator", cache, key);
    return (
        <div>
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
              this.handleError("(Comming Soon!!!) This will allow you to load previously saved criteria.");
            }
          }, {
            id: 'save',
            name: 'Save',
            img: "/images/svg/save.svg",
            title: 'Save Criteria Selection',
            onClick: () => {
              this.handleError("(Comming Soon!!!) This will allow you to save criteria.");
            }
          }, {
            id: 'reset',
            name: 'Reset',
            img: "/images/svg/erase.svg",
            title: 'Reset Criteria',
            onClick: () => {
              this.setState(() => {return {query: this.createQuery(), properties: []}})
              console.log("Reset Criteria");
            }
          },{
            id: 'search',
            name: 'Search',
            img: "/images/svg/search.svg",
            title: 'Execute Search',
            onClick: this.handleSearch
          }
        ]}/>
          <div  style={{padding: '5px', margin: '10px'}}>
            { cache[key] != null && cache[key].recordsets.length >= 2 &&
              <FilterCreator
                label={this.state.key ? this.state.cache[this.state.key].name : undefined}
                maxRows={10}
                recordsets={cache[key].recordsets}
                data={query}
                handleError={this.handleError}
                handleUpdate={this.handleUpdate}
              />

            }
            { results != null &&
              <SearchResults
                data={results}
                properties={properties}
                handleError={this.handleError}
                handleSpinner={this.handleSpinner}
                url={this.props.karafRootAddr}
              />
            }
            { this.state.error &&
              <DialogMessage
                title="Error"
                opened={true}
                message={this.state.error}
                onClose={() => {this.setState({error: null})}}
              />
            }
            { this.state.loading  &&
              <DialogLoader
                title={this.state.loading}
                opened={true}
              />
            }
            { this.state.promptTemplate && this.state.templates &&
              <DialogOptions
                title="Select Template"
                open={true}
                onClose={() => {this.setState({promptTemplate: false})}}
                onClick={this.handleTemplateLoad}
                options={this.state.templates}
              />
            }
          </div>
        </div>
    )
  }

  handleSpinner(value) {
    this.setState(() => {return {
      loading: value
    }})
  }

  handleSearch() {

    const that = this;

    // Create Query
    var query = {
      "type": this.state.key ? this.state.cache[this.state.key].name : undefined,
      "pageSize": 200,
      "pageNumber": 1,
      "propertyNames": [],
      "group": this.state.query
    };

    if (this.state.query.criteria.length === 0) {
      that.handleError("Please add one or more criteria.");
      return;
    }

    // Get Specified columns
    if (this.state.properties && this.state.properties.length > 0) {
      this.state.properties.forEach(function (property) {
        query.propertyNames.push(property);
      });
    } else {
      this.state.query.criteria.forEach(function (criteria) {
        if (!query.propertyNames.includes(criteria.recordset + "." + criteria.field)) {
          query.propertyNames.push(criteria.recordset + "." + criteria.field);
        }
      })
    }

    this.state.query.criteria.forEach(function (criteria) {
      if (criteria.value2)
      {
        criteria.value = criteria.value + " " + criteria.value2;
        criteria.value2 = undefined;
      }
    })

    console.log("Index search", this.state.query);

    // Display Spinner
    this.setState(() => {return {
      loading: "Searching..."
    }})

    // Submit Query
    searchComplex(query).then(response => {

      response.key = uuid.v4();

      that.setState(() => {return {
        results: response,
        properties: query.propertyNames,
        loading: null
      }})
    }).catch(function(error) {
        that.handleError("Executing Search: " + error);
    });
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
