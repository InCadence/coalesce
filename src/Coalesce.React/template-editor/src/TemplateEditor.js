import React, { Component } from 'react';
import { Panel, Row, Col } from 'react-bootstrap';
import TextField from 'material-ui/TextField';
import ContentClear from 'material-ui/svg-icons/content/clear';
import AvPlaylistAdd from 'material-ui/svg-icons/av/playlist-add';
import IconButton from 'material-ui/IconButton';
import { Section } from './TemplateSection'
import uuid from 'uuid';
import baseTheme from 'material-ui/styles/baseThemes/lightBaseTheme';
import { fade } from 'material-ui/utils/colorManipulator'

class TemplateEditor extends Component {

  constructor(props) {
    super(props);

    this.state = { template: props.template };

    this.handleChange = this.handleChange.bind(this);
    this.handleRemove = this.handleRemove.bind(this);
    this.handleAddSection = this.handleAddSection.bind(this);

  }


  handleRemove() {
    this.props.onRemove(this.props.template.key);
  }

  handleChange(attr, value) {
    const { template } = this.state;
    template[attr] = value;
    this.setState({
      template: template
    })
  }

  handleAddSection() {
    const { template } = this.state;
    template.sectionsAsList.push(createSection());
    this.setState({
      template: template
    })
  }

  render() {

    const { template } = this.state;

    return (
        <Panel className="ui-widget-content" id={template.key} style={{ 'overflowY': 'hidden' }}>
          <div style={{ 'display': 'table' }}>
            <div style={{ 'display': 'table-cell', width: '100%' }}>
              <TextField
                fullWidth={true}
                floatingLabelText="Name"
                value={template.name}
                onChange={(event, value) => { this.handleChange("name", value); }}
              />
            </div>
            <div style={{ 'display': 'table-cell', 'width': '24px' }}>
              <IconButton iconStyle={{ width: '24px', height: '24px', padding: '0px' }} style={{ width: '24px', height: '24px', padding: '2px' }}>
                <ContentClear
                  color="#3d3d3c"
                  hoverColor="#FF9900"
                  onClick={this.handleRemove}
                />
              </IconButton>
            </div>
          </div>
          <TextField
            fullWidth={true}
            floatingLabelText="Classname"
            value={template.className}
            onChange={(event, value) => { this.handleChange("className", value); }}
          />

          <div style={{ 'display': 'table' }}>
            <div style={{ 'display': 'table-cell', width: '100%' }}>
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
            </div>
            <div style={{ 'display': 'table-cell', 'width': '24px' }}>
              <IconButton iconStyle={{ width: '24px', height: '24px', padding: '0px' }} style={{ width: '24px', height: '24px', padding: '2px' }}>
                <AvPlaylistAdd
                  color="#3d3d3c"
                  hoverColor="#FF9900"
                  onClick={this.handleAddSection}
                />
              </IconButton>
            </div>
          </div>
          <Section data={template} />
        </Panel>
    );
  }
}

function createSection() {
  return {
    key: uuid.v4(),
    name: "ChangeMe",
    sectionsAsList: [],
    recordsetsAsList: []
  }
}

export default TemplateEditor;
