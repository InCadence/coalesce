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
import {Responsive, WidthProvider} from 'react-grid-layout';

var pjson = require('../package.json');
const ResponsiveReactGridLayout = WidthProvider(Responsive);
class TemplateWorkspace extends Component {

  constructor(props) {
    super(props);

    this.state = {
      items: [],
      newCounter: 0,
      //cols: {lg: 20, md: 15, sm: 10, xs: 5},
      //breakpoints: {lg: 1600, md: 996, sm: 768, xs: 480},
      rowHeight: 600,
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
    this.handleGraphAdd = this.handleGraphAdd.bind(this);
    this.handleEditModalToggle = this.handleEditModalToggle.bind(this);
    this.onLayoutChange = this.onLayoutChange.bind(this);
    this.onBreakpointChange = this.onBreakpointChange.bind(this);
  }

  componentDidMount() {

    var that = this;
    //window.addEventListener("resize", this.updateScaling.bind(this));
    loadJSON('theme').then((value) => {
      that.setState({
        theme: getMuiTheme(value)
      })
    }).catch((err) => {
      console.log("Loading Theme: " + err);
    })
  }

  handleEditModalToggle() {
    this.setState({ showEditModal: !this.state.showEditModal })
  }

  handleTemplateAdd() {

    //console.log("adding template to workspace...");
    //Check to see if there was a previously occupied space
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
    console.log(this.state.items.length*5, this.state.cols||20, window.screen.availWidth);
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
    // Load Template
    loadTemplate(key).then (function(template) {
      //Check to see if there is a space that was previously occupied
      //If not, add per usual
      if(!that.state.emptySpace){
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
    }).catch((err) => {
      that.setState({
        error: "Loading Template: " + key
      });

    });
  }

  handleTemplateSave() {
    const { items } = this.state;

    if (items.length > 0)
    {
      const that = this;
      var count = 0;

      this.setState({
        loading: "Saving ..."
      })

      items.forEach(function (item) {

        saveTemplate(item.template).then(function (result) {
          if (!result) {
            that.setState({
              loading: null,
              error: "Saving Template: " + item.template.key
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
            error: "Saving Template: " + item.template.key
          });
        })

      });
    }
  }

  handleTemplateRegister() {
    const { items } = this.state;

    if (items.length > 0)
    {
      const that = this;

      var count = 0;

      this.setState({
        loading: "Registering ..."
      })

      items.forEach(function (item) {

        registerTemplate(item.template.key).then(function (result) {

          if (!result) {
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

  updateScaling(){
    const {items} = this.state;
    var curSize = window.innerWidth/300;
    var scale = 0;
    var toUse = 0;
    if((Math.ceil(curSize)) > 3){
      toUse = 3;
    }
    else if((Math.ceil(curSize)) <= 3 && (Math.ceil(curSize)) > 2){
      toUse = 2;
    }
    else if((Math.ceil(curSize)) <= 2 && (Math.ceil(curSize)) > 1){
      toUse = 1;
    }
    else{
      toUse = 0;
    }
    scale = this.state.scaling[toUse];
    for(var ii = 0; ii < items.length; ii++){
      if(items[ii].x > 5*scale){
        items[ii].x = 5*scale;
        items[ii].y = 15+items[ii].y;
        console.log("Item", ii, "is", items[ii]);
      }
    }
    console.log("Finished with updating scaling and position");
    this.setState(items);
    this.setState({cols: 5*scale});
    this.debugItemPrint();
    this.render();
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
      console.log("Item", ii, "X is", this.state.items[ii].x, "Y is", this.state.items[ii].y, "W is", this.state.items[ii].w, "H is", this.state.items[ii].h);
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

  onBreakpointChange(breakpoint, cols){
    this.setState({breakpoint: breakpoint, cols: cols});
    console.log("Breakpoint has changed", breakpoint, cols);
  }

  onLayoutChange(layout){
    this.setState({layout: this.reCalculateLayout()});
    console.log("Layout has changed", layout);
    this.debugItemPrint();
  }

  reCalculateLayout(){
    var newLayout = this.state.items;
    for(var ii = 0; ii < newLayout.length; ii++){
      newLayout[ii] = {x: ((newLayout.length * 5) % (this.state.cols || 20)), y: 0, w: 5, h: 15}
    }
    console.log("reCalculateLayout", newLayout);
    return newLayout;
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
          }
        ]}/>
        <MuiThemeProvider muiTheme={this.state.theme}>
          <div>
          <ResponsiveReactGridLayout className="layout" layout={this.state.items} rowHeight={this.state.rowHeight} onLayoutChange={this.onLayoutChange} onBreakpointChange={this.onBreakpointChange} cols= {{lg: 20, md: 15, sm: 10, xs: 5}} breakpoints={{lg: 1600, md: 996, sm: 768, xs: 480}} draggableCancel="input,textarea">
            {this.state.items.map((item) => this.createElement(item))}
          </ResponsiveReactGridLayout>
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
