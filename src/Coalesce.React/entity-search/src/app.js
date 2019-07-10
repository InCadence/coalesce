import React from 'react'
import Menu from 'common-components/lib/components/menu'
import { loadTemplates, loadTemplate } from 'common-components/lib/js/templateController.js';
import { getRootKarafUrl, timeDifference } from 'common-components/lib/js/common';
import { DialogMessage, DialogLoader, DialogOptions, DialogFieldLegend, DialogPrompt } from 'common-components/lib/components/dialogs'
import { searchComplex, loadHistory, loadSavedHistory, saveQuery } from 'common-components/lib/js/searchController.js';

import uuid from 'uuid';

import FilterCreator from './filtercreator.js'
import {SearchResults} from './results.js'

const karafRootAddr = getRootKarafUrl();
const DEFAULT = 'CoalesceEntity';

export class App extends React.Component {

  constructor(props) {
    super(props);

    var cache = {};

    cache[DEFAULT] = {
      recordsets: [],
      name: ''
    };

    this.state = {
      cache: cache,
      key: DEFAULT,
      results: null,
      query: this.createQuery()
    }
  }

  createQuery = (name) => {

    if (!name) {
      name = this.state && this.state.key ? this.state.cache[this.state.key].name : '' ;
    }

    return {
      type: name,
      pageSize: 200,
      pageNumber: 1,
      propertyNames: [],
      sortBy: [{"propertyName": undefined, "sortOrder": "ASC"}],
      group: {
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

  handleError = (message) => {
    this.setState(() => {return {
      error: message,
      loading: null,
      promptTemplate: false
    }});
  }

  handleUpdate = (data, properties) => {
    const { query } = this.state;

    query.group = data;
    query.propertyNames = properties;

    this.setState(() => {return {query: query}});
  }

  handleSortUpdate = (sortBy) => {
    const { query } = this.state;

    query.sortBy = sortBy;

    this.setState(() => {return {query: query}});
  }

  handleCapabilityUpdate = (value) => {
    this.setState(() => {return {capabilities: value}});
  }

  handlePageUpdate = (page) => {
    const { query } = this.state;

    query.pageNumber = page;

    this.setState(() => {return {query: query}});
  }

  handlePageSizeUpdate = (size) => {

    const { query } = this.state;

    query.pageSize = size;

    this.setState(() => {return {pageSize: size}});
  }

  handleTemplateLoad = (key) => {

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

    const { cache, key, results, query } = this.state;
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
            id: 'legend',
            name: 'Legend',
            img: "/images/svg/java-docs.svg",
            title: 'Display Legend',
            onClick: () => {
              this.setState({displayLegend: true})
            }
          }, {
            id: 'load',
            name: 'Load',
            img: "/images/svg/load.svg",
            title: 'Load Query',
            onClick: this.handleLoadSavedHistory
          }, {
            id: 'save',
            name: 'Save',
            img: "/images/svg/save.svg",
            title: 'Save Query',
            onClick: () => {
              this.setState(() => {return {promptTitle: true}})
            }
          }, {
            id: 'reset',
            name: 'Reset',
            img: "/images/svg/erase.svg",
            title: 'Reset Query',
            onClick: () => {
              this.setState(() => {return {query: this.createQuery()}})
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
                label={query.type}
                maxRows={10}
                recordsets={cache[key].recordsets}
                sortBy={query.sortBy}
                selectedColumns={query.propertyNames}
                data={query.group}
                handleError={this.handleError}
                handleUpdate={this.handleUpdate}
                handleSortUpdate = {this.handleSortUpdate}
                handleCapabilityUpdate = {this.handleCapabilityUpdate}
                handlePageUpdate={this.handlePageUpdate}
                handlePageSizeUpdate={this.handlePageSizeUpdate}
                handleSearch={this.handleSearch}
                pageNum={query.pageNumber}
                pageSize={query.pageSize}
              />
            }
            { results != null &&
              <SearchResults
                data={results}
                properties={query.propertyNames}
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
                sorted
                onClose={() => {this.setState({promptTemplate: false})}}
                onClick={this.handleTemplateLoad}
                options={this.state.templates}
              />
            }
            { this.state.displayLegend && this.state.key &&
              <DialogFieldLegend
                title={`${cache[key].name} Legend`}
                opened={true}
                data={cache[key].recordsets}
                onClose={() => {this.setState({displayLegend: null})}}
              />
            }
            { this.state.history &&
              <DialogOptions
                title="Select Query"
                open={this.state.showHistory}
                sorted
                onClose={() => {this.setState({history: undefined})}}
                onNew={this.state.showAllHistory ? this.handleLoadHistory : undefined}
                onNewTitle="All"
                onClick={this.handleLoadQuery}
                options={this.state.history}
              />
            }
            { this.state.promptTitle &&
              <DialogPrompt
                title="Enter Title"
                opened={true}
                onSubmit={this.handleSaveQuery}
                onClose={() => {this.setState(() => {return {promptTitle: false}})}}
              />
            }
          </div>
        </div>
    )
  }

  handleLoadQuery = (key) => {

    const { history, templates, cache } = this.state;
    var that = this;

    for (var ii=0; ii<history.length; ii++) {
      var historyRecord = history[ii].query;

      if (historyRecord.key === key) {

        if (historyRecord.sortBy.length == 0) {
          historyRecord.sortBy.push({"propertyName": undefined, "sortOrder": "ASC"})
        }

        var templateKey = DEFAULT;

        for (var jj=0; jj<templates.length; jj++) {
          if (templates[jj].name === historyRecord.type) {
            templateKey = templates[jj].key;
            break;
          }
        }

        if (cache[templateKey] == null) {
            loadTemplate(templateKey).then(template => {

                var recordsets = []

                // Get Other Recordsets
                template.sectionsAsList.forEach(function(section) {
                  recordsets = recordsets.concat(getRecordsets(section));
                });

                recordsets = recordsets.concat(cache[DEFAULT].recordsets);

                cache[templateKey] = {
                  recordsets: recordsets,
                  name: template.name
                };

                that.setState(() => {return {
                  cache: cache
                }});

            }).catch((err) => {
              that.handleError(`Failed Loading Template: ${key}`);
            })
        }

        historyRecord.pageNumber = 1;
        historyRecord.cql = undefined;
        historyRecord.name = undefined;

        this.setState(() => {return {
          key: templateKey,
          query: historyRecord,
          results: undefined,
          showHistory: false,
          showAllHistory: false
        }})

        break;
      }
    }

  }

  handleSpinner = (value) => {
    this.setState(() => {return {
      loading: value
    }})
  }

  handleLoadSavedHistory = () => {
    const that = this;

    loadSavedHistory().then((history) => {
      that.setState(() => {return {
          showHistory: true,
          showAllHistory: true,
          history: history.map((item) => {
              item.name = item.title && item.title !== "SearchQuery" ? item.title : item.query.cql;
              item.key = item.query.key;
              return item;
            })
      }});
    }).catch((err) => {
      that.handleError(`Loading History: ${err}`);
    })
  }

  handleLoadHistory = () => {
    const that = this;

    loadHistory().then((history) => {
      that.setState(() => {return {
          showHistory: true,
          showAllHistory: false,
          history: history.map((item) => {
              item.name = item.title && item.title !== "SearchQuery" ? item.title : item.query.cql;
              item.key = item.query.key;
              return item;
            })
      }});
    }).catch((err) => {
      that.handleError(`Loading History: ${err}`);
    })
  }

  handleSaveQuery = (title) => {

    const that = this;
    const { query } = this.state;

    query.type = this.state.key ? this.state.cache[this.state.key].name : undefined;

    if (!query.sortBy[0].propertyName) {
      query.sortBy[0].propertyName = "CoalesceEntity.LastModified";
    }

    that.setState(() => {return {loading: "Saving Query"}});

    saveQuery({query: query, title: title}).then(() => {
      that.setState(() => { return {
        promptTitle: false,
        loading: undefined
      } })
    }).catch((err) => {
      that.handleError(`Saving Search: ${err}`);
    })
  }

  handleSearch = () => {

    const that = this;

    const { query } = this.state;

    query.type = this.state.key ? this.state.cache[this.state.key].name : undefined;

    if (query.group.criteria.length === 0) {
      that.handleError("Please add one or more criteria.");
      return;
    }

    if (!query.sortBy[0].propertyName) {
      query.sortBy[0].propertyName = "CoalesceEntity.LastModified";
    }

    // Get Specified columns
    if (query.propertyNames && query.propertyNames.length <= 0) {
      query.group.criteria.forEach(function (criteria) {
        if (!query.propertyNames.includes(criteria.recordset + "." + criteria.field)) {
          query.propertyNames.push(criteria.recordset + "." + criteria.field);
        }
      })
    }

    // Display Spinner
    this.setState(() => {return {
      loading: "Searching..."
    }})

    // Submit Query
    searchComplex(query).then(response => {

      response.key = uuid.v4();

      that.setState(() => {return {
        results: response,
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
