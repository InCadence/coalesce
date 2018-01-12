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

class TemplateEditor extends Component {

  constructor(props) {
    super(props);

  }

  createSection(template) {

  }

  render() {

    return (
      <Panel id="editor">
        <MainHeader name={this.props.template.name} />
        <Section name="Section 1">
          <RecordSet name="Recordset 1">
            <Field name="Field 1" />
            <Field name="Field 2" />
            <Field name="Field 3" />
          </RecordSet>
        </Section>
      </Panel>
    );
  }
}

class Section extends Component {

  constructor(props) {
    super(props);
    this.handleClick = this.handleClick.bind(this);
    this.handlePropsChange = this.handlePropsChange.bind(this);
    this.handleEditToggle = this.handleEditToggle.bind(this);

    this.state = {
      name: "Section 1",
      open: false,
      edit: false,
    };
  }

  handleClick() {
    this.setState({ open: !this.state.open })
  }

  handlePropsChange(value) {
    this.setState({ name: value })
  }

  handleEditToggle(e) {
    this.setState({ edit: !this.state.edit })
  }

  render() {

    var icon;
    if (this.state.open) {
      icon = <Glyphicon glyph="triangle-bottom" />;
    } else {
      icon = <Glyphicon glyph="triangle-right" />;
    }

    const tooltip = (
      <Tooltip >Edit Section Properties</Tooltip>
    );

    return (
      <div id="section">
        <Divider />
        <ButtonGroup>
          <Button onClick={this.handleClick}>
            {icon}
          </Button>
          <DropdownButton title={this.props.name} id="bg-nested-dropdown">
            <MenuItem eventKey={3.1}>Rename</MenuItem>
            <MenuItem divider />
            <MenuItem eventKey={3.2}>Insert New Section</MenuItem>
            <MenuItem eventKey={3.3}>Insert New Recordset</MenuItem>
          </DropdownButton>
        </ButtonGroup>

        <Panel collapsible expanded={this.state.open} eventKey="1">
          {this.props.children}
        </Panel>
      </div>
    );
  }
}

class RecordSet extends Component {

  constructor(props) {
    super(props);
    this.handleClick = this.handleClick.bind(this);
    this.handlePropsChange = this.handlePropsChange.bind(this);
    this.handleEditToggle = this.handleEditToggle.bind(this);

    this.state = {
      name: "Record Set 1",
      open: false,
      edit: false,
    };
  }

  handleClick() {
    this.setState({ open: !this.state.open })
  }

  handlePropsChange(value) {
    this.setState({ name: value })
  }

  handleEditToggle(e) {
    this.setState({ edit: !this.state.edit })
  }

  render() {

    var icon;
    if (this.state.open) {
      icon = <Glyphicon glyph="triangle-bottom" />;
    } else {
      icon = <Glyphicon glyph="triangle-right" />;
    }

    const tooltip = (
      <Tooltip >Edit RecordSet Properties</Tooltip>
    );

    return (
      <div id="section">
        <ButtonGroup>
          <Button onClick={this.handleClick}>
            {icon}
          </Button>
          <DropdownButton title={this.props.name} id="bg-nested-dropdown">
            <MenuItem eventKey={3.1}>Rename</MenuItem>
            <MenuItem eventKey={3.2}>Edit RecordSet Properties</MenuItem>
            <MenuItem divider />
            <MenuItem eventKey={3.2}>Insert New Field</MenuItem>
          </DropdownButton>
        </ButtonGroup>
        <Panel collapsible expanded={this.state.open} eventKey="1">
          {this.props.children}
        </Panel>
      </div>
    );
  }
}

class Field extends Component {
  constructor(props) {
    super(props);

    this.handleClick = this.handleClick.bind(this);
    this.handlePropsChange = this.handlePropsChange.bind(this);
    this.handleEditToggle = this.handleEditToggle.bind(this);

    this.state = {
      type: "some type",
      doc: "This is where field documentation goes",
      open: "false",
    };
  }
  handleClick() {
    this.setState({ open: !this.state.open })
  }

  handlePropsChange(value) {
    this.setState({ name: value })
  }

  handleEditToggle(e) {
    this.setState({ edit: !this.state.edit })
  }

  render() {

    var icon;
    if (this.state.open) {
      icon = <Glyphicon glyph="triangle-bottom" />;
    } else {
      icon = <Glyphicon glyph="triangle-right" />;
    }

    const tooltip = (
      <Tooltip >Edit Field Properties</Tooltip>
    );

    return (
      <div id="section">
        <ButtonGroup>
          <Button onClick={this.handleClick}>
            {icon}
          </Button>
          <DropdownButton title={this.props.name} id="bg-nested-dropdown">
            <MenuItem eventKey={3.1}>Rename</MenuItem>
            <MenuItem eventKey={3.2}>Edit Field Properties</MenuItem>
            <MenuItem divider />
            <MenuItem eventKey={3.2}>Add New Constraint</MenuItem>
          </DropdownButton>
        </ButtonGroup>
        <Panel collapsible expanded={this.state.open} eventKey="1">
        <div>
        <b>Field Documentation</b>
          <Divider />
        {this.state.doc}
        <Divider />
        </div>
          <div>
            <FormGroup controlId="formControlsSelect">
              <ControlLabel>Field Type</ControlLabel>
              <FormControl componentClass="select" placeholder="String">
                <option value="String">String</option>
                <option value="Boolean">Boolean</option>
                <option value="Integer">Integer</option>
              </FormControl>
            </FormGroup>
          </div>
         
        </Panel>
      </div>
    );
  }
}


class MainHeader extends Component {

  render() {

    const tooltip = (
      <Tooltip >Edit Template Properties</Tooltip>
    );

    return (
      <div>
        <Divider />
        <Grid fluid >
          <Row className="show-grid">
            <Col xs={6} md={6}>
              <h4>{this.props.name}</h4>
            </Col>
            <Col xs={6} md={6}>
              <OverlayTrigger placement="bottom" overlay={tooltip}>
                <span className="pull-right">
                  <Glyphicon glyph="pencil" />
                </span>
              </OverlayTrigger>
            </Col>
          </Row>
        </Grid>
      </div>

      //const menuIcon = (<Icon icon={menu3} />);
      //<Navbar>
      // <Navbar.Header>
      //  <Navbar.Brand>
      //   TemplateName
      //  </Navbar.Brand>
      // </Navbar.Header>
      //  <Nav>
      //   <NavDropdown eventKey={3} title={menuIcon} id="basic-nav-dropdown" noCaret>
      //    <MenuItem eventKey={3.1}>Action</MenuItem>
      //   <MenuItem eventKey={3.2}>Another action</MenuItem>
      //  <MenuItem eventKey={3.3}>Something else here</MenuItem>
      //  <MenuItem divider />
      // <MenuItem eventKey={3.4}>Separated link</MenuItem>
      // </NavDropdown>
      // </Nav>
      // </Navbar>
    );
  }
}

class SmallHeader extends Component {

  constructor(props) {
    super(props);

  }

  render() {
    var icon;
    if (this.props.open) {
      icon = <Glyphicon glyph="triangle-bottom" />;
    } else {
      icon = <Glyphicon glyph="triangle-right" />;
    }

    const menuIcon = (<Icon icon={menu3} />);
    return (
      <Grid fluid >
        <Row className="show-grid">
          <Col xs={12} md={12}>
            {icon}
            {this.props.name}
          </Col>
        </Row>
      </Grid>
    );
  }
}

class Divider extends Component {
  render() {
    return (
      <hr className="editor-divider" />
    );
  }
}

export default TemplateEditor;