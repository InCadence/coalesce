import React, { Component } from 'react';
import { Panel, Row, Col } from 'react-bootstrap';
import TextField from '@material-ui/core/TextField';
import ContentClear from '@material-ui/icons/Clear';
import AvPlaylistAdd from '@material-ui/icons/PlaylistAdd';
import IconButton from '@material-ui/core/IconButton';
import Paper from '@material-ui/core/Paper';
import Tooltip from '@material-ui/core/Tooltip';
import Section from './TemplateSection'
import uuid from 'uuid';

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
        <Panel className="MuiPaper-root-27 MuiPaper-rounded-28 MuiPaper-elevation2-31" id={template.key} >
          <div className="input-row" style={{ 'display': 'table' }}>
            <div style={{ 'display': 'table-cell', width: '100%' }}>
              <TextField
                fullWidth={true}
                label="Name"
                value={template.name}
                onChange={(event) => { this.handleChange("name", event.target.value); }}
              />
            </div>
            <div style={{ 'display': 'table-cell', 'width': '24px' }}>
              <Tooltip title="Close Template" placement="bottom">
                <IconButton iconstyle={{ width: '24px', height: '24px', padding: '0px' }} style={{ width: '24px', height: '24px', padding: '2px' }}>
                  <ContentClear
                    color="primary"
                    onClick={this.handleRemove}
                  />
                </IconButton>
              </Tooltip>
            </div>
          </div>
          <div className="input-row" style={{ 'display': 'table' }}>
            <TextField
              fullWidth={true}
              label="Classname"
              value={template.className}
              onChange={(event) => { this.handleChange("className", event.target.value); }}
            />
          </div>
          <div className="input-row" style={{ 'display': 'table' }}>
            <div style={{ 'display': 'table-cell', width: '100%' }}>
              <Row>
                <Col xs={7}>
                  <TextField
                    fullWidth={true}
                    label="Source"
                    value={template.source}
                    onChange={(event) => this.handleChange("source", event.target.value)}
                  />
                </Col>
                <Col xs={5}>
                  <TextField
                    fullWidth={true}
                    label="Version"
                    value={template.version}
                    onChange={(event) => this.handleChange("version", event.target.value)}
                  />
                </Col>
              </Row>
            </div>
            <div style={{ 'display': 'table-cell', 'width': '24px' }}>
              <Tooltip title="Add Section" placement="bottom">
                <IconButton iconstyle={{ width: '24px', height: '24px', padding: '0px' }} style={{ width: '24px', height: '24px', padding: '2px' }}>
                  <AvPlaylistAdd
                    color="primary"
                    onClick={this.handleAddSection}
                  />
                </IconButton>
              </Tooltip>
            </div>
          </div>
          <div className="input-row" style={{ 'display': 'table' }}>
            <Section data={template} />
          </div>
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
