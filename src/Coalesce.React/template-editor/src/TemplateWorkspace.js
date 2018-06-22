import React, { Component } from 'react';
//import ReactGridLayout from 'react-grid-layout';
import TemplateGraph from './TemplateGraph.js';
import TemplateEditor from './TemplateEditor.js';
import { Template } from './TemplateObjects.js';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import { getDefaultTheme } from 'common-components/lib/js/theme'
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import uuid from 'uuid';

import { loadTemplates, loadTemplate, saveTemplate,registerTemplate } from 'common-components/lib/js/templateController.js';
import { loadJSON } from 'common-components/lib/js/propertyController';
import {Menu} from 'common-components/lib/index.js';

import { DialogMessage, DialogLoader, DialogTemplateSelection } from 'common-components/lib/components/dialogs';
import RGL,{WidthProvider} from 'react-grid-layout';

import { getRootKarafUrl } from 'common-components/lib/js/common';

var pjson = require('../package.json');
const ReactGridLayout = WidthProvider(RGL);
class TemplateWorkspace extends Component {

  constructor(props) {
    super(props);

    this.state = {
      items: [],
      newCounter: 0,
      cols: 20,
      rowHeight: 30,
      showEditModal: false,
      promptTemplate: false,
      loading: null,
      error: null,
      emptySpace: false,
      removedItems: [],
      removedCount: 0,
      scaling: [1,2,3,4],
      theme: getDefaultTheme()
    }
    console.log(window.screen.availWidth, window.screen.availHeight, this.state.cols);

    this.handlePromptTemplate = this.handlePromptTemplate.bind(this);

    this.handleTemplateAdd = this.handleTemplateAdd.bind(this);
    this.handleTemplateLoad = this.handleTemplateLoad.bind(this);
    this.handleTemplateSave = this.handleTemplateSave.bind(this);
    this.handleTemplateRemove = this.handleTemplateRemove.bind(this);
    this.handleTemplateRegister = this.handleTemplateRegister.bind(this);
    this.handleTemplateClear = this.handleTemplateClear.bind(this);
    this.handleGraphAdd = this.handleGraphAdd.bind(this);
    this.handleEditModalToggle = this.handleEditModalToggle.bind(this);
  }

  componentDidMount() {

    var that = this;
    loadJSON('theme').then((value) => {
      that.setState({
        theme: getMuiTheme(value)
      })
    }).catch((err) => {
      console.log("Loading Theme: " + err);
    })
  }

  handleTemplateClear(){
    //If the state's list of items isn't 0, reset to blank array
    if(this.state.items.length !== 0){
      this.setState({items: [], removedItems: [], newCounter: 0, removedCount: 0, emptySpace: false});
    }
  }

  handleEditModalToggle() {
    this.setState({ showEditModal: !this.state.showEditModal })
  }

  handleTemplateAdd() {

    //console.log("adding template to workspace...");
    //Check to see if there was a previously occupied space
    //Set different template type between adding and loading
    var templateType = 0;
    if(!this.state.emptySpace){
      this.setState({
      // Add a new item.
        items: this.state.items.concat({
          i: "n" + this.state.newCounter,
          x: ((this.state.items.length * 5) % (this.state.cols || 20)),
          y: 0, // puts it at the bottom
          w: 5,
          h: 15,
          static: false,
          widgetType: "template",
          template: {key: uuid.v4(), sectionsAsList: []},
        }),
        // Increment the counter to ensure key is always unique.
        newCounter: this.state.newCounter + 1,
      });
    }
    //If there was, insert new item into that empty space first
    else{
      this.addInEmptySpace(templateType);
    }
    console.log("Adding Template", this.state.items);
    //this.debugItemPrint();
  }

  handlePromptTemplate() {
    var that = this;

    loadTemplates().then(function (templates) {
      // Prompt User
      that.setState({
        templates: templates,
        promptTemplate: true
      })

    }).catch((err) => {
      that.setState({
        error: "Loading Templates: " + err
      });
    });
  }

  handleTemplateLoad(key) {
    //Need to do this due to asynchronous nature of then command
    //Without it, this does not exist(?) in this scope after
    var templateType = 1;
    var that = this;
    var oldI, oldX, oldY;
    var index = -1;
    // Load Template
    loadTemplate(key).then (function(template) {
      //Check to see if template with desired key already exists
      for(var ii = 0; ii < that.state.items.length; ii++){
        if(that.state.items[ii].template.key === key){
          //If key exists, store location, i value, and index
          oldI = that.state.items[ii].i;
          oldX = that.state.items[ii].x;
          oldY = that.state.items[ii].y;
          index = ii;
        }
      }
      //If index is not default value, key exists
      if(index !== -1){
        //Dialog box alerting user that this will overwrite existing changes
        if(window.confirm("Template with matching key exists.  Continuing(OK) will overwrite any existing changes")){
          //Release existing template with matching key
          that.state.items.splice(index,1);
          //Place new template in old location
          that.setState({
            // Add a new item.
            items: that.state.items.concat({
              i: oldI+1, //Need to increment identifier otherwise widget doesn't update
              x: oldX,
              y: oldY,
              w: 5,
              h: 15,
              static: false,
              widgetType: "template",
              template: template,
            }),
            // Increment the counter to ensure key is always unique.
            newCounter: that.state.newCounter + 1,
          });
      }
      else{
        console.log("Do not overwrite changes");
        return;
      }
    }
      //Key does not already exist
      else{
        //Check to see if there is a previously vacated space to insert
        if(!that.state.emptySpace){
          //If no, insert as normal
          that.setState({
            // Add a new item.
            items: that.state.items.concat({
              i: "n" + that.state.newCounter,
              x: ((that.state.items.length * 5) % (that.state.cols || 20)),
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
        }
        //If there was a previously occupied space, insert there first
        else{
          that.addInEmptySpace(templateType, template);
        }
      }
    }).catch((err) => {
      that.setState({
        error: "Loading Template: " + key
      });

    });
    //this.debugItemPrint();
  }

  handleTemplateSave() {
    //Function to save templates
    const { items } = this.state;
    //this.debugItemPrint();
    var newItem = null;
    var newItems = [];
    if (items.length > 0)
    {
      const that = this;
      var count = 0;
      this.setState({
        loading: "Saving ..."
      })

      items.forEach(function (item) {
        //For each item in the array of items
        saveTemplate(item.template).then(function (result) {
          //If the result was null then save failed
          if (result === null) {
            console.log("Result was null");
            that.setState({
              loading: null,
              error: "Saving Template: " + item.template.key
            });
          }
          //Assuming save was successful
          else{
            //Make shallow copy of first item in array
            //We always grab first item because we are splicing and then concatenating so the end of the array updates
            //with new templates
            newItem = that.state.items.slice(0,1);
            //Update template key with the returned result which is a string of the server side key
            newItem[0].template.key = result;
            //Update i or else widget won't update
            newItem[0].i = newItem[0].i +1;
            //Remove first item in array, this is our current item being worked on
            that.state.items.splice(0,1);
            //Update state with item with new key
            that.setState({
              // Add a new item.
              items: that.state.items.concat(newItem[0])
            });
          }
          //If the count is greater than or equal to the number of items, we are done
          if (++count >= items.length) {
            console.log("Finished saving");
            that.setState({
              loading: null
            });
          }
        }).catch((err) => {
          that.setState({
            loading: null,
            error: "Saving Template: " + item.template.key
          });
        })

      });
    }
    //this.debugItemPrint();
  }

  handleTemplateRegister() {
    const { items } = this.state;
    console.log("Registering Templates");
    this.debugItemPrint()

    if (items.length > 0)
    {
      const that = this;

      var count = 0;

      this.setState({
        loading: "Registering ..."
      })

      items.forEach(function (item) {

        registerTemplate(item.template.key).then(function (result) {
          console.log(item.template.key);
          if (!result) {
            console.log("Fail 1");
            that.setState({
              loading: null,
              error: "Registering Template: " + item.template.key
            });
          }
          if (++count >= items.length) {
            that.setState({
              loading: null
            })
          }

        }).catch((err) => {
          that.setState({
            loading: null,
            error: "Registering Template: " + item.template.key
          });
        })
      });
    }
  }

  handleTemplateRemove(key) {

    console.log("Removing Template: " + key);
    var removedX = null;
    var removedY = null;
    const { items } = this.state;

    for (var ii=0; ii<items.length; ii++) {
      if (items[ii].template.key === key) {
        removedX = items[ii].x;
        removedY = items[ii].y;
        items.splice(ii, 1);
        this.setState({
        // Add a new item.
          removedItems: this.state.removedItems.concat({
            x: removedX,
            y: removedY, // puts it at the bottom
          }),
          removedCount: this.state.removedCount + 1,
        });
      }
    }
    this.setState({emptySpace: true});
    this.setState(items);

  }

  //Function to insert into previously occupied space
  addInEmptySpace(templateType, template){

    const {removedItems} = this.state;
    var toAddX = 0;
    var toAddY = 0;
    //Grab the x and y location from running array of previously removed items
    //Grabs the spot most recently freed up
    toAddX = removedItems[removedItems.length-1].x;
    toAddY = removedItems[removedItems.length-1].y;
    if(templateType === 0){
      this.setState({
        // Add a new item.
        items: this.state.items.concat({
          i: "n" + this.state.newCounter,
          x: toAddX, // puts it at the x position of the previous item
          y: toAddY, // puts it at the y position of previous item
          w: 5,
          h: 15,
          static: false,
          widgetType: "template",
          template: {key: uuid.v4(), sectionsAsList: []},
        }),
        // Increment the counter to ensure key is always unique.
        newCounter: this.state.newCounter + 1,
        // Decrement counter of removed items (may not be necessary)
        removedCount: this.state.removedCount -1
      });
    }
    else if(templateType === 1){
      this.setState({
        // Add a new item.
        items: this.state.items.concat({
          i: "n" + this.state.newCounter,
          x: toAddX, // puts it at the x position of the previous item
          y: toAddY, // puts it at the y position of previous item
          w: 5,
          h: 15,
          static: false,
          widgetType: "template",
          template: template,
        }),
        // Increment the counter to ensure key is always unique.
        newCounter: this.state.newCounter + 1,
        // Decrement counter of removed items (may not be necessary)
        removedCount: this.state.removedCount -1
      });
    }

    //Actually remove the item from the array
    removedItems.splice(removedItems.length-1,1);
    //If there are items in the array, we no longer have any previously
    //occupied positions and can add to workspace per usual
    if(removedItems.length === 0){
      this.setState({emptySpace: false});
    }
    //Update state with current state of removed items array
    this.setState(removedItems);

  }

  handleTemplateDownload(){
      var karafRootAddr = getRootKarafUrl();
      console.log("Download", this.state.items.length, this.state.items);
      for(var ii = 0; ii < this.state.items.length; ii++){
        window.open(`${karafRootAddr}` + '/templates/' + this.state.items[ii].template.key + '.xml', '_blank');
      }
  }

  handleGraphAdd() {

    this.setState({
      // Add a new item.
      items: this.state.items.concat({
        i: "n" + this.state.newCounter,
        x: ((this.state.items.length * 2) % (this.state.cols || 20)),
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

  debugItemPrint(){
    for(var ii =0 ; ii < this.state.items.length; ii++){
      console.log("Item", ii, "X is", this.state.items[ii].x, "Y is", this.state.items[ii].y, "W is", this.state.items[ii].w, "H is", this.state.items[ii].h, "Key is", this.state.items[ii].template.key);
    }
}

  createElement(item) {
    //console.log("New template id: " + item.template.key);

    var widget = null;

    if (item.widgetType === "template") {
      widget = (

          <TemplateEditor
            template={item.template}
            onSave={this.handleTemplateSave}
            onRemove={this.handleTemplateRemove}
          />

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
        <Menu logoSrc={pjson.icon} title={pjson.title} homeEnabled={false} items={[
          {
            id: 'home',
            name: 'Home',
            img: "/images/svg/home.svg",
            title: 'Home',
            onClick: () => {window.location.href = "/home"}
          },{
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
          },{
            id: 'clear',
            name: 'clear',
            img: "/images/svg/remove.svg",
            title: 'Clear Workspace',
            onClick: () => {this.handleTemplateClear()}
          },{
            id: 'download',
            name: 'download',
            img: "/images/svg/down.svg",
            title: 'Download Templates',
            onClick: () => {this.handleTemplateDownload()}
          }
        ]}/>
        <MuiThemeProvider muiTheme={this.state.theme}>
          <div>
          <ReactGridLayout className="layout" layout={this.state.items} rowHeight={this.state.rowHeight} cols= {this.state.cols} draggableCancel="input,textarea">
            {this.state.items.map((item) => this.createElement(item))}
          </ReactGridLayout>
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
        </MuiThemeProvider>
      </div>
    );

  }

}

export default TemplateWorkspace;
