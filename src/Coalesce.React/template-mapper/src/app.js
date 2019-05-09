import React from 'react';
import Menu from 'common-components/lib/components/menu';
import { loadTemplates, loadTemplate } from 'common-components/lib/js/templateController.js';
import { getRootKarafUrl } from 'common-components/lib/js/common';
import { DialogMessage, DialogLoader, DialogOptions } from 'common-components/lib/components/dialogs'
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import Input from '@material-ui/core/Input';
import Linkage from './linkage.js';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select'
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import Template from'./template.js';
import TextField from '@material-ui/core/TextField';
import ReactJson from 'react-json-view';
import {flatten} from './flat.js';

const karafRootAddr = getRootKarafUrl();
const DEFAULT = 'CoalesceEntity';

export class App extends React.Component {

  constructor(props) {
    super(props);

    this.handleNext = this.handleNext.bind(this);
    this.handleTemplateLoad = this.handleTemplateLoad.bind(this);
    this.handleTemplateDelete = this.handleTemplateDelete.bind(this);
    this.handleError = this.handleError.bind(this);
    this.handleBack = this.handleBack.bind(this);
    this.handleOnCsvChange = this.handleOnCsvChange.bind(this);
    this.handleJsonChange = this.handleJsonChange.bind(this);
    this.handleLinkageLoad = this.handleLinkageLoad.bind(this);
    this.handleLinkageDelete = this.handleLinkageDelete.bind(this);
    this.handleLinkageChange = this.handleLinkageChange.bind(this);
    this.handleJsonLoad = this.handleJsonLoad.bind(this);
    this.createJson = this.createJson.bind(this);
    this.handleOnXmlChange = this.handleOnXmlChange.bind(this);
    this.handleTypeChange = this.handleTypeChange.bind(this);

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
      value: "",
      split: [],
      type: 0,

    }
    this.templates = [];
    this.links = [];
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
        that.templates.push({});
        that.setState({
          promptTemplate: false,
          templateKeys: that.state.templateKeys,
        });

        }).catch((err) => {
          that.handleError(`Failed Loading Template: ${key}`);
        })
    } else {
      console.log("Loading Template (Cached): " + key);

      that.setState({
        promptTemplate: false
      });
    }
  }

  handleTemplateDelete(index) {
    var key = this.state.templateKeys[index];
    delete this.state.cache[key];
    this.state.templateKeys.splice(index, 1);
    this.templates.splice(index, 1);
    this.setState({cache: this.state.cache})
  }

  handleLinkageLoad(entityIndex) {
    var link = {
      entity1: ""+entityIndex,
      linkType: 'created',
      entity2: ""+entityIndex,
    }
    this.links.push(link);
    this.setState({promptLinkage: false});
  }

  handleLinkageDelete(index) {
    this.links.splice(index, 1);
  }

  handleLinkageChange(linkageIndex, newJson) {
    //this.links[linkageIndex] = newJson;
    console.log(newJson)
  }

  handleBack() {
    this.setState(state => ({
      activeStep: state.activeStep - 1,
    }));
  }

  handleOnCsvChange(event) {
    var str = event.target.value;
    var split = str.split(',');
    this.setState({
      value: str,
      split: split,
    })
  }

  handleOnXmlChange(event) {
    var str = event.target.value;
    var flat = flatten(1, str);
    this.setState({
      value: str,
      split: flat
    })
  }

  handleNext() {
    const activeStep = this.state.activeStep;
    this.setState({
      activeStep: activeStep + 1,
    });
    this.createJson()
  }

  handleJsonChange(index, newJson) {
      this.templates[index] = newJson;
  }

  handleJsonLoad() {
    var jsonString = this.state.jsonInput;
    var json = JSON.parse(jsonString);
    var templates = json.templates;
    var links = json.linkages;

    this.templates = [];
    this.links = [];

    var templateKeys = [];

    //add in templates
    for(var i = 0; i < templates.length; i++) {
      var template = templates[i];
      console.log(template);
      var key = template["templateUri"].split("/").slice(-1)[0] //get last element, which is just the key
      templateKeys.push(key);
      this.handleTemplateLoad(key);

      this.templates.push(template.record.fields);
    }

    //add in linkages
    for(i = 0; i < links.length; i++) {
      this.links.push(links[i]);
    }

    this.setState({promptJson: false});
  }

  createJson() {
    const {templateKeys, cache} = this.state;
    this.json = {templates: [], linkages: []};

    for(var i = 0; i < templateKeys.length; i++) {
      var rec = cache[templateKeys[i]].recordsets[0]
      var template = {
        templateUri: getRootKarafUrl()+"/templates/"+templateKeys[i],
        record: {
          name: rec.name,
          fields: this.templates[i]
        }
      }
      this.json["templates"][i] = template
    }

    for(i = 0; i < this.links.length; i++) {
      this.json["linkages"][i] = this.links[i];
    }

    return this.json;
  }

  handleTypeChange(event) {
    var type = event.target.value;
    var event = {target: {value:this.state.value}}
    console.log(event.target.value)
    var split = null;
    if(type === 0) {
      this.handleOnCsvChange(event)
    }
    else if(type === 1) {
      this.handleOnXmlChange(event)
      console.log("xml")
    }

    this.setState({"type": type});
  }

  render() {
    const { cache, activeStep, templateKeys, split, type } = this.state;
    var json = "";
    const that = this;
    var items = [];

    var value = null;
    var onChange = null;
    if(type == 0) {
      value = that.state.value;
      onChange = that.handleOnCsvChange;
    }
    else if(type == 1) {
      value = that.state.value;
      onChange = that.handleOnXmlChange;
    }
    if(activeStep === 0) {
      items.push({
        id: 'select',
        name: 'Select',
        img: "/images/svg/template.svg",
        title: 'Add Template',
        onClick: () => {
          this.setState({promptTemplate: true})
        }
      },
      {
        id: 'json',
        name: 'JSON',
        img: "/images/svg/edit.svg",
        title: "Paste in JSON",
        onClick: () => {
          this.setState({promptJson: true})
        }
      }
    );
    }
    else if(activeStep === 1) {
      items.push({
        id: 'linkage',
        name: 'Linkage',
        img: "/images/svg/add.svg",
        title: 'Add Linkage',
        onClick: () => {
          this.setState({promptLinkage: true})
        }
      });
    }
    else if(activeStep === 2) {
      json = this.createJson();
    }

    var linkList = []; //not a linked list, its a list of linkages
    if(activeStep === 1) {
      for(var i = 0; i < this.state.templateKeys.length; i++) {
        linkList.push({name: cache[this.state.templateKeys[i]].name, key: i, enum: i.toString(), label: cache[this.state.templateKeys[i]].name});
      }
    }

    // console.log("App creating new filter creator", cache, key);
    return (
        <div>
        <Menu logoSrc={this.props.pjson.icon} title={this.props.pjson.title} items={items}/>
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
                    disabled={activeStep === 0}
                    >
                    Back
            </Button>
            {activeStep !== 2 &&
              <Button variant="contained"
                      onClick={this.handleNext}
                      color="primary"
              >
                      Next
              </Button>
            }
            {activeStep === 2 &&
              <Button variant="contained"
                      onClick={this.handleCopy}
                      color="primary"
              >
                      Copy
              </Button>
            }

            {activeStep === 0 &&
              <div>
                <TextField
                  id="standard-full-width"
                  label="Input"
                  style={{ margin: 8 }}
                  placeholder=""
                  helperText="Paste input here"
                  fullWidth
                  onChange={onChange}
                  margin="normal"
                  value={value}
                  InputLabelProps={{
                    shrink: true,
                  }}
                />
                <Select
                  value={that.state.type}
                  onChange={that.handleTypeChange}
                  name="Input Type"
                  input={<Input name="age" id="age-helper" />}
                >
                  <MenuItem value={0}>CSV</MenuItem>
                  <MenuItem value={1}>XML</MenuItem>
                </Select>
              </div>
            }
            {activeStep === 0 &&
              templateKeys.map(function(key, index) {
              var rec = cache[key].recordsets[0];
              var name = cache[key].name;
              var recName = rec.name;
              return(
                <div>
                <Template
                  type={type}
                  key={key}
                  index={index}
                  tKey={key}
                  recordSet={rec}
                  onChange={that.handleJsonChange}
                  recName={recName}
                  name={name}
                  split={split}
                  field={that.templates[index]}
                  handleDelete={that.handleTemplateDelete}
                />
                <hr/>
                </div>
              )
            })
            }
            {activeStep === 1 &&
              that.links.map(function(link_, index) {
              var name = cache[templateKeys[parseInt(link_.entity1)]];
              if(name) {
                name = name.name;
              }

              return(
                <Linkage
                  field={link_}
                  index={index}
                  options={linkList}
                  parent={that}
                  onChange={that.handleLinkageChange}
                  name={name}
                  handleDelete={that.handleLinkageDelete}
                />
              )
              })
            }

            {activeStep === 2 &&
              <ReactJson enableClipboard src={json}/>
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
            { this.state.promptLinkage &&
              <DialogOptions
                title="Select the template to link FROM"
                open={true}
                onClose={() => {this.setState({promptLinkage: false})}}
                onClick={this.handleLinkageLoad}
                options={linkList}
              />
            }
            { this.state.promptJson &&
              <Dialog fullWidth maxWidth='lg' open={true} onClose={() => {this.setState({promptJson: false})}}>
                <DialogContent>
                <TextField
                  onChange={(e) => that.setState({jsonInput: e.target.value})}
                  fullWidth
                  id="json-input"
                  label="JSON"
                  multiline
                  rowsMax="100"
                  value={this.state.jsonInput}
                  margin="normal"
                  helperText="paste json here"
                  variant="outlined"
                />
                <Button variant="contained"
                  onClick={() => {this.setState({promptJson: false, jsonInput: ""});}}
                  color="secondary"
                >
                  Cancel
                </Button>
                <Button variant="contained"
                  onClick={this.handleJsonLoad}
                  color="primary"
                >
                  Load
                </Button>
                </DialogContent>

              </Dialog>
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
