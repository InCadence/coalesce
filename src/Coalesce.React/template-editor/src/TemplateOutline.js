import React, { Component } from 'react';
import { Panel } from 'react-bootstrap';
import { Button } from 'react-bootstrap';
import { Glyphicon } from 'react-bootstrap';
import { Grid } from 'react-bootstrap';
import { Col } from 'react-bootstrap';
import { Row } from 'react-bootstrap';

class TemplateOutline extends Component {

  constructor(props) {
    super(props);
  }

  render() {

    const templateNames = this.props.templateNames;
    const listItems = templateNames.map((templateName) =>
      <Row className="show-grid">
        <Col xs={1} md={1}>{templateName} </Col>
      </Row>
    );

    const title = (
      <h3>Templates</h3>
    );

    return (

        <Panel header={title}>
          <Grid>{listItems}</Grid>
        </Panel>

    );
  }
}

export default TemplateOutline;
