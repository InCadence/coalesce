import React from 'react';
import Menu from 'common-components/lib/components/menu';
import { loadTemplates, loadTemplate } from 'common-components/lib/js/templateController.js';
import { getRootKarafUrl } from 'common-components/lib/js/common';
import { DialogMessage, DialogLoader, DialogOptions } from 'common-components/lib/components/dialogs'
import { searchComplex } from 'common-components/lib/js/searchController.js';
import Button from '@material-ui/core/Button';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import Template from'./template.js';
import TextField from '@material-ui/core/TextField';

import uuid from 'uuid';

import {SearchResults} from './results.js'

const karafRootAddr = getRootKarafUrl();
const DEFAULT = 'CoalesceEntity';

export class App extends React.Component {

  constructor(props) {
    super(props);

    this.handleNext = this.handleNext.bind(this);
    this.handleTemplateLoad = this.handleTemplateLoad.bind(this);
    this.handleError = this.handleError.bind(this);
    this.handleBack = this.handleBack.bind(this);
    this.handleCopy = this.handleCopy.bind(this);
    this.handleOnCsvChange = this.handleOnCsvChange.bind(this);

    var cache = {};

    cache[DEFAULT] = {
      recordsets: [],
      name: ''
    };

    this.state = {
      cache: cache,
      key: DEFAULT,
      results: null,
      activeStep: 0,
      templateKeys: [],
      templatesObject: {},
      linkagesObject: {},
      csv: "",
      split: [],
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
        that.state.templateKeys.push(key);
        that.setState({
          key: key,
          promptTemplate: false,
          templateKeys: that.state.templateKeys,
          }
        );

        }).catch((err) => {
          that.handleError(`Failed Loading Template: ${key}`);
        })
    } else {
      console.log("Loading Template (Cached): " + key);

      that.setState({
        key: key,
        promptTemplate: false
        });
    }
  }

  handleBack() {
    this.setState(state => ({
      activeStep: state.activeStep - 1,
    }));
  }

  handleCopy() {

  }

  handleOnCsvChange(event) {
    var str = event.target.value;
    var split = str.split(',');
    this.setState({
      csv: event.target.value,
      split: split,
    })
    console.log(split);
  }

  handleNext() {
    const activeStep = this.state.activeStep;
    this.setState({
      activeStep: activeStep + 1,
    });
  }

  render() {

    const { cache, results, activeStep, templateKeys, split } = this.state;
    const numSteps = 3;

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
          }
        ]}/>
          <div  style={{padding: '5px', margin: '10px'}}>
          <Stepper activeStep={activeStep}>
              <Step key="Templates">
                <StepLabel>Templates</StepLabel>
              </Step>
              <Step key="Linkages">
                <StepLabel>Linkages</StepLabel>
              </Step>
              <Step key="JSON">
                <StepLabel>JSON</StepLabel>
              </Step>
          </Stepper>

            <Button variant="contained"
                    onClick={this.handleBack}
                    color="primary"
                    disabled={activeStep == 0}
                    >
                    Back
            </Button>
            {activeStep != 2 &&
              <Button variant="contained"
                      onClick={this.handleNext}
                      color="primary"
              >
                      Next
              </Button>
            }
            {activeStep == 2 &&
              <Button variant="contained"
                      onClick={this.handleCopy}
                      color="primary"
              >
                      Copy
              </Button>
            }

            {activeStep == 0 && <div>
              <TextField
                id="standard-full-width"
                label="CSV"
                style={{ margin: 8 }}
                placeholder="one, two, three(etc)"
                helperText="Paste one line of CSV here"
                fullWidth
                onChange={this.handleOnCsvChange}
                margin="normal"
                value={this.state.csv}
                InputLabelProps={{
                  shrink: true,
                }}
              />
               </div>

            }
            {activeStep == 0 &&
              templateKeys.map(function(key, index) {
              var rec = cache[key].recordsets[0];
              var name = cache[key].name;
              var recName = rec.name;
              return(
                <Template
                  key={key}
                  index={index}
                  tKey={key}
                  recordSet={rec}
                  recName={recName}
                  name={name}
                  split={split}
                />
              )
            })}




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
