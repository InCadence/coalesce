import React, { Component } from 'react';
import ReactGridLayout from 'react-grid-layout';
import TemplateGraph from './TemplateGraph.js';
import TemplateOutline from './TemplateOutline.js';
import TemplateEditor from './TemplateEditor.js';
import TemplateNavBar from './TemplateNavBar.js';
import { Template } from './TemplateObjects.js';
import { Panel, Grid, Row, Col, Button } from 'react-bootstrap';
import EditModal from './EditModal.js';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

import { loadTemplates, loadTemplate, saveTemplate,registerTemplate } from './fetch';
import {Menu} from 'common-components/lib/index.js'

import {DialogTemplateSelection} from './DialogTemplateSelection'

var pjson = require('../package.json');

class TemplateWorkspace extends Component {

  constructor(props) {
    super(props);

    this.state = {
      items: [],
      newCounter: 0,
      cols: 12,
      rowHeight: 30,
      width: 1200,
      showEditModal: false,

    }

    this.handlePromptTemplate = this.handlePromptTemplate.bind(this);

    this.handleTemplateAdd = this.handleTemplateAdd.bind(this);
    this.handleTemplateLoad = this.handleTemplateLoad.bind(this);
    this.handleTemplateSave = this.handleTemplateSave.bind(this);
    this.handleTemplateRegister = this.handleTemplateRegister.bind(this);
    this.handleGraphAdd = this.handleGraphAdd.bind(this);
    this.handleEditModalToggle = this.handleEditModalToggle.bind(this);
  }

  handleEditModalToggle() {
    this.setState({ showEditModal: !this.state.showEditModal })
  }

  handleTemplateAdd() {

    console.log("adding template to workspace...");

    this.setState({
      // Add a new item.
      items: this.state.items.concat({
        i: "n" + this.state.newCounter,
        x: ((this.state.items.length * 5) % (this.state.cols || 12)) + 1,
        y: Infinity, // puts it at the bottom
        w: 5,
        h: 10,
        static: false,
        widgetType: "template",
        template: new Template("n" + this.state.newCounter, null, "New Template"),
      }),
      // Increment the counter to ensure key is always unique.
      newCounter: this.state.newCounter + 1,

    });

  }

  handlePromptTemplate() {
    var that = this;

    loadTemplates().then(function (templates) {
      // Prompt User
      that.setState({
        templates: templates,
        promptTemplate: true
      })

    });
  }

  handleTemplateLoad(key) {

    var that = this;

    // Load Template
    loadTemplate(key).then (function(template) {

      that.setState({
        // Add a new item.
        items: that.state.items.concat({
          i: "n" + that.state.newCounter,
          x: ((that.state.items.length * 5) % (that.state.cols || 12)) + 1,
          y: 0,
          w: 5,
          h: 15,
          static: false,
          widgetType: "template",
          template: template,
        }),
        // Increment the counter to ensure key is always unique.
        newCounter: that.state.newCounter + 1,

      });
    });
  }

  handleTemplateSave() {
    const { items } = this.state;

    items.forEach(function (item) {
      saveTemplate(item.template).then(function (result) {
        if (!result) {
          console.log("(FAILED) Saving " + item.template.key);
        }
      })
    });

  }

  handleTemplateRegister() {
    const { items } = this.state;

    items.forEach(function (item) {
      registerTemplate(item.template.key).then(function (result) {
        if (!result) {
          console.log("(FAILED) Registering " + item.template.key);
        }
      })
    });

  }
  handleGraphAdd() {

    this.setState({
      // Add a new item.
      items: this.state.items.concat({
        i: "n" + this.state.newCounter,
        x: ((this.state.items.length * 2) % (this.state.cols || 12)) + 1,
        y: Infinity, // puts it at the bottom
        w: 4,
        h: 10,
        static: false,
        widgetType: "graph",
        template: new Template("n" + this.state.newCounter, null, "New Template"),
      }),
      // Increment the counter to ensure key is always unique.
      newCounter: this.state.newCounter + 1,

    });

  }

  createElement(item) {
    console.log("New template id: " + item.template.key);

    var widget = null;

    if (item.widgetType === "template") {
      widget = (

          <TemplateEditor onSave={this.handleTemplateSave} template={item.template} />

      );
    } else {
      widget = (

          <TemplateGraph />

      );
    }

    return (
      <div key={item.i} >
      { widget }
      </div>
    );
  }

  render() {

    return (
      <div>
        <Menu logoSrc={pjson.icon} title={pjson.title} items={[
          {
            id: 'select',
            name: 'Select',
            img: "/images/svg/load.svg",
            title: 'Load Template',
            onClick: () => {this.handlePromptTemplate()}
          },{
            id: 'new',
            name: 'new',
            img: "/images/svg/add.svg",
            title: 'New Template',
            onClick: () => {this.handleTemplateAdd()}
          },{
            id: 'save',
            name: 'save',
            img: "/images/svg/save.svg",
            title: 'Save Templates',
            onClick: () => {this.handleTemplateSave()}
          },{
            id: 'register',
            name: 'register',
            img: "/images/svg/template.svg",
            title: 'Register Template',
            onClick: () => {this.handleTemplateRegister()}
          }
        ]}/>
        <MuiThemeProvider>
          <ReactGridLayout className="layout" layout={this.state.items} cols={this.state.cols} rowHeight={this.state.rowHeight} width={this.state.width} draggableCancel="input,textarea">
            {this.state.items.map((item) => this.createElement(item))}
          </ReactGridLayout>
          <DialogTemplateSelection
            templates={this.state.templates}
            open={this.state.promptTemplate}
            onClose={() => {this.setState({promptTemplate: false});}}
            onClick={this.handleTemplateLoad}
          />
        </MuiThemeProvider>
      </div>
    );

  }

}

export default TemplateWorkspace;
