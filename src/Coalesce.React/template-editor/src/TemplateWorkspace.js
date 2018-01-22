import React, { Component } from 'react';
import ReactGridLayout from 'react-grid-layout';
import TemplateGraph from './TemplateGraph.js';
import TemplateOutline from './TemplateOutline.js';
import TemplateEditor from './TemplateEditor.js';
import TemplateNavBar from './TemplateNavBar.js';
import { Template } from './TemplateObjects.js';
import { Panel, Grid, Row, Col, Button } from 'react-bootstrap';
import EditModal from './EditModal.js';

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

    this.handleTemplateAdd = this.handleTemplateAdd.bind(this);
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
        x: ((this.state.items.length * 2) % (this.state.cols || 12)) + 1,
        y: Infinity, // puts it at the bottom
        w: 4,
        h: 10,
        static: false,
        widgetType: "template",
        template: new Template("n" + this.state.newCounter, null, "New Template"),
      }),
      // Increment the counter to ensure key is always unique.
      newCounter: this.state.newCounter + 1,

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
    console.log("New template id: " + item.i);

    var widget = null;

    if (item.widgetType === "template") {
      widget = (
  
          <TemplateEditor template={item.template} />
    
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

      <Grid fluid>
        <Row className="show-grid">
          <Col xs={3} md={3}>
            <div>
              <TemplateNavBar onTemplateAdd={this.handleTemplateAdd} onGraphAdd={this.handleGraphAdd}/>
            </div>
            <div>
              <EditModal showModal={this.state.showEditModal} onToggle={this.handleEditModalToggle} />
            </div>
          </Col>
          <Col xs={9} md={9}>
            <ReactGridLayout className="layout" layout={this.state.items} cols={this.state.cols} rowHeight={this.state.rowHeight} width={this.state.width}>
              {this.state.items.map((item) => this.createElement(item))}
            </ReactGridLayout>
          </Col>
        </Row>
      </Grid>

    );

  }

}

export default TemplateWorkspace;
