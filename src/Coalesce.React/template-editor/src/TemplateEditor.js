import React, { Component } from 'react';
import SortableTree from 'react-sortable-tree';
import {
  FormGroup, InputGroup, FormControl, Panel,
  Nav, NavDropdown, MenuItem, Navbar, Accordion, Grid,
  Row, Col, Glyphicon, Button, Tooltip, OverlayTrigger, ButtonGroup, DropdownButton,ControlLabel
} from 'react-bootstrap';
import Icon from 'react-icons-kit';
import { menu3 } from 'react-icons-kit/icomoon/menu3';
import EditModal from './EditModal';
import { TemplateObject, TemplateObjectTypes } from './TemplateObjects.js';
import {Tabs, Tab} from 'material-ui/Tabs';
import {List, ListItem} from 'material-ui/List';
import ReactTable from 'react-table';
import TextField from 'material-ui/TextField';
import { stack } from 'react-icons-kit/icomoon/stack';

import { Section } from './TemplateSection'

class TemplateEditor extends Component {

  constructor(props) {
    super(props);

    this.state = {template: props.template};

    this.handleChange = this.handleChange.bind(this);

  }

  createSection(template) {

  }

  handleChange(attr, value) {
    const {template} = this.state;
    template[attr] = value;
    this.setState({
      template: template
    })
  }

  render() {

    const { template } = this.state;

    return (
      <Panel  className="ui-widget-content" id={template.key} style={{'overflowY': 'hidden'}}>
        <TextField
          fullWidth={true}
          floatingLabelText="Name"
          value={template.name}
          onChange={(event, value) => {this.handleChange("name", value);}}
        />
        <Row>
          <Col xs={7}>
            <TextField
              fullWidth={true}
              floatingLabelText="Source"
              value={template.source}
              onChange={(event, value) => this.handleChange("source", value)}
            />
          </Col>
          <Col xs={5}>
            <TextField
              fullWidth={true}
              floatingLabelText="Version"
              value={template.version}
              onChange={(event, value) => this.handleChange("version", value)}
            />
          </Col>
        </Row>
        <Section data={template} />
      </Panel>
    );
  }
}

export default TemplateEditor;
