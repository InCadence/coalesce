import React, { Component } from 'react';
//import ReactGridLayout from 'react-grid-layout';
import TemplateGraph from './TemplateGraph.js';
import TemplateEditor from './TemplateEditor.js';
import { Template } from './TemplateObjects.js';
import uuid from 'uuid';

import { loadTemplates, loadTemplate, saveTemplate,registerTemplate, loadTemplateAsXML } from 'common-components/lib/js/templateController';
import { saveFile } from 'common-components/lib/js/file';

import {Menu} from 'common-components/lib/index';

import { DialogMessage, DialogLoader, DialogOptions} from 'common-components/lib/components/dialogs';
import RGL,{WidthProvider} from 'react-grid-layout';

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
      removedItems: [],
      removedCount: 0,
      scaling: [1,2,3,4],
    }

    this.handlePromptTemplate = this.handlePromptTemplate.bind(this);

    this.handleTemplateAdd = this.handleTemplateAdd.bind(this);
    this.handleTemplateLoad = this.handleTemplateLoad.bind(this);
    this.handleTemplateSave = this.handleTemplateSave.bind(this);
    this.handleTemplateRemove = this.handleTemplateRemove.bind(this);
    this.handleTemplateRegister = this.handleTemplateRegister.bind(this);
    this.handleTemplateClear = this.handleTemplateClear.bind(this);
    this.handleTemplateDownload = this.handleTemplateDownload.bind(this);
    this.handleGraphAdd = this.handleGraphAdd.bind(this);
    this.handleEditModalToggle = this.handleEditModalToggle.bind(this);
    this.handleDuplicateTemplateLoad = this.handleDuplicateTemplateLoad.bind(this);
  }
/*
  componentDidMount() {
    this.handleTemplateLoad(['0d75e8ca-204f-3d20-a03d-7e43a889e93f', '07ba3b60-9480-3388-aca6-eb6fa349f0c3']);
  }
//*/
  handleTemplateClear(){
    //If the state's list of items isn't 0, reset to blank array
    if(this.state.items.length !== 0){
      this.setState({items: [], removedItems: [], newCounter: 0, removedCount: 0});
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
    if(this.state.removedItems.length === 0){
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
          template: {
            className: "",
            key: uuid.v4(),
            name: "",
            source: "",
            version: "",
            sectionsAsList: []
          },
        }),
        // Increment the counter to ensure key is always unique.
        newCounter: this.state.newCounter + 1,
      });
    }
    //If there was, insert new item into that empty space first
    else{
      this.addInEmptySpace(templateType);
    }
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
      console.log(err);
      that.setState({
        error: "Loading Templates: " + err
      });
    });
  }


  handleDuplicateTemplateLoad(){

    const { items, duplicates } = this.state;
    var { newCounter  } = this.state;
    var updatedItems = [];

    duplicates.sort((a, b) => b.idx - a.idx);

    for (var jj=0; jj < duplicates.length; jj++) {

        var duplicate = duplicates[jj];

        //Release existing template
        var replaced = items.splice(duplicate.idx, 1)[0];

        if (replaced) {

          //Place new template in old location
          updatedItems.push({
             i: "n" + (newCounter++),
             x: replaced.x,
             y: replaced.y,
             w: replaced.w,
             h: replaced.h,
             static: replaced.static,
             widgetType: replaced.widgetType,
             template: duplicate.template
           })
         }
    }

    this.setState(() => {return {
      duplicates: [],
      newCounter: newCounter,
      items: items.concat(updatedItems)
    }})
  }

  handleTemplateLoad(keys) {

    const { items } = this.state;
    const duplicates = [];

    //Need to do this due to asynchronous nature of then command
    //Without it, this does not exist(?) in this scope after
    var templateType = 1;
    var that = this;

    this.setState({promptTemplate: false})

    keys.forEach((key) => {
      // Load Template
      loadTemplate(key).then (function(template) {

        var isDuplicate = false;

        //Check to see if template with desired key already exists
        for(var ii = 0; ii < items.length; ii++){
          if(items[ii].template.key === template.key){
            duplicates.push({
              idx: ii,
              template: template
            });
            isDuplicate = true;

            that.setState(() => { return {duplicates: duplicates}})
            break;
          }
        }
        //If index is not default value, key exists
        if(!isDuplicate){
          //Check to see if there is a previously vacated space to insert
          if(that.state.removedItems.length === 0){
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
    });

  }

  handleTemplateSave() {
    //Function to save templates
    const { items } = this.state;
    //this.debugItemPrint();
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
            that.setState({
              loading: null,
              error: "Saving Template: " + item.template.key
            });
          }
          //Assuming save was successful
          else{
            item.template.key = result;
          }

          //If the count is greater than or equal to the number of items, we are done
          if (++count >= items.length) {
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

    //Update state with current state of removed items array
    this.setState(removedItems);

  }

  handleTemplateDownload(){
      this.state.items.forEach(function (item) {
        loadTemplateAsXML(item.template.key).then((xml) => {
          saveFile(new Blob([xml]), item.template.key + ".xml");
        })
      })
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

    const { duplicates } = this.state;

    return (
      <div className="App">
        <Menu logoSrc={this.props.icon} title={this.props.title} homeEnabled={false} items={[
          {
            id: 'home',
            name: 'Home',
            img: "/images/svg/home.svg",
            title: 'Home',
            onClick: () => {window.location.href = "/home"}
          },{
            id: 'new',
            name: 'new',
            img: "/images/svg/new.svg",
            title: 'Create New Template',
            onClick: this.handleTemplateAdd
          },{
            id: 'select',
            name: 'Select',
            img: "/images/svg/load.svg",
            title: 'Load Saved Template',
            onClick: this.handlePromptTemplate
          },{
            id: 'save',
            name: 'save',
            img: "/images/svg/save.svg",
            title: 'Save All Loaded Templates',
            onClick: this.handleTemplateSave
          },{
            id: 'clear',
            name: 'clear',
            img: "/images/svg/clear.svg",
            title: 'Remove All Loaded Templates',
            onClick: this.handleTemplateClear
          },{
            id: 'download',
            name: 'download',
            img: "/images/svg/download.svg",
            title: 'Download All Loaded Templates',
            onClick: this.handleTemplateDownload
          },{
            id: 'register',
            name: 'register',
            img: "/images/svg/template.svg",
            title: 'Register All Loaded Templates',
            onClick: this.handleTemplateRegister
          }
        ]}/>
          <div>
          <ReactGridLayout className="layout" layout={this.state.items} rowHeight={this.state.rowHeight} cols= {this.state.cols} draggableCancel="input,textarea">
            {this.state.items.map((item) => this.createElement(item))}
          </ReactGridLayout>
          { this.state.error &&
            <DialogMessage
              title="Error"
              opened={true}
              message={this.state.error}
              onClose={() => {this.setState({error: null})}}
            />
          }
          { this.state.loading &&
            <DialogLoader
              title={this.state.loading}
              opened={true}
            />
          }
          {duplicates && duplicates.length > 0 &&
            <DialogMessage
              title="Duplicate"
              confirmation="true"
              opened={true}
              message={`${duplicates.length} templates with matching key exists.  Continuing(OK) will overwrite any unsaved changes`}
              onClose={() => {this.setState({duplicates: []});}}
              onClick={this.handleDuplicateTemplateLoad}
            />
          }
          {this.state.promptTemplate &&
            <DialogOptions
              title="Select Template"
              open={true}
              multi={true}
              onClose={() => {this.setState({promptTemplate: false})}}
              onClick={this.handleTemplateLoad}
              options={this.state.templates}
            />
          }
          </div>
      </div>
    );

  }

}

export default TemplateWorkspace;
