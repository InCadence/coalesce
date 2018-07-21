import React from 'react';
import {LinkageView} from './linkagetable'
import {RecordsetView} from './recordset'
import {RecordView} from './record'
import {Row, Col} from 'react-bootstrap'
import {Tabs, Tab} from 'material-ui/Tabs';
import {FieldInput} from './FieldInput.js'


export class EntityView extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
        data: props.data,
        isNew: props.isNew
    };
  }

  componentWillReceiveProps(nextProps) {
    this.setState({
      data: nextProps.data,
      isNew: nextProps.isNew
    })
  }

  setIsNew(isNew) {
    this.setState({
      isNew: isNew
    })
  }

  onSave() {
    this.props.saveEntity(this.state.data, this.state.isNew, this);
  }

  render() {
    const {data} = this.state;

    var template = this.props.template;

    return (
      <div>
        <Row>
          <Col xs={2}>
            <label>Title</label>
          </Col>
          <Col xs={4}>
            {data &&
              <FieldInput
                field={data}
                dataType="STRING_TYPE"
                attr="title"
                showLabels={false}
              />
            }
          </Col>
          <Col xs={2}>
            <label>Name</label>
          </Col>
          <Col xs={4}>
            {template != null ? template.name : ''}
          </Col>
        </Row>
        <Row>
          <Col xs={2}>
            <label>Created</label>
          </Col>
          <Col xs={4}>
            {data != null ? data.dateCreatedAsString : ''}
          </Col>
          <Col xs={2}>
            <label>Source</label>
          </Col>
          <Col xs={4}>
            {template != null ? template.source : ''}
          </Col>
        </Row>
        <Row>
          <Col xs={2}>
            <label>Last Modified</label>
          </Col>
          <Col xs={4}>
            {data != null ? data.lastModifiedAsString : ''}
          </Col>
          <Col xs={2}>
            <label>Version</label>
          </Col>
          <Col xs={4}>
            {template != null ? template.version : ''}
          </Col>
        </Row>
        <Row>
          <Col xs={2}>
            <label>Key</label>
          </Col>
          <Col xs={4}>
            {data != null ? data.key : ''}
          </Col>
          <Col xs={2}>
            <label>Revision</label>
          </Col>
          <Col xs={4}>
            {data != null ? data.objectVersion : ''}
          </Col>
        </Row>
        { template != null && data != null &&
        <Tabs>
          {template.sectionsAsList.map((section) => {return renderSection(section, getElement(section.name, data.sectionsAsList))})}
          <Tab label="linkages">
            <LinkageView linkages={data.linkageSection.linkagesAsList} />
          </Tab>
        </Tabs>
        }
      </div>
    )
  }

};

EntityView.defaultProps = {
  isNew: true
}

function getElement(name, data)
{
  var result;

  if (data != null)
  {
    for (var ii=0; ii<data.length; ii++)
    {
      if (name === data[ii].name)
      {
        result = data[ii];
        break;
      }
    }
  }

  return result;
}

function renderSection(section, data) {

    var view;

    if (section.sectionsAsList.length === 0 && section.recordsetsAsList.length === 1)
    {
      view = renderRecordset(section.recordsetsAsList[0], data);

    } else {
      view = (
        <Tab key={section.key} objectkey={section.key} label={section.name}>
          <Tabs>
            {section.sectionsAsList.map((item) => {return renderSection(item, getElement(item.name, data.sectionsAsList))})}
            {section.recordsetsAsList.map((item) => {return renderRecordset(item, data)})}
          </Tabs>
        </Tab>
      );
    }

    return view;
}

function renderRecordset(recordset, data) {
  var recordsetdata = getElement(recordset.name, data.recordsetsAsList);

  if (recordsetdata != null) {
    if (recordset.minRecords === 1 && recordset.maxRecords === 1)
    {
      var record;

      // Use First Record
      if (recordsetdata.allRecords != null && recordsetdata.allRecords.length >= 1) {
        record = recordsetdata.allRecords[0];
      }

      return (
        <Tab key={recordsetdata.key} label={recordset.name}>
          <RecordView
            definition={recordset.fieldDefinitions}
            record={record}
          />
        </Tab>
      );
    }
    else
    {
      return (
        <Tab key={recordsetdata.key} label={recordset.name}>
          <RecordsetView
            recordset={recordset}
            data={recordsetdata}
          />
        </Tab>
      );
    }
  }

}

String.prototype.toProperCase = function () {
    return this.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
};
