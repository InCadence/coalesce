import React from 'react';
import {Tabs, Tab} from 'material-ui/Tabs';
import TextField from 'material-ui/TextField';
import { RecordSet } from './TemplateRecordset'
import { TabTextField } from './TabTextField'
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import RaisedButton from 'material-ui/RaisedButton';
import { DialogAddOptions } from './DialogAddOptions';
import uuid from 'uuid';

export class Section extends React.Component {

  constructor(props) {
    super(props);
    this.handleClick = this.handleClick.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleAdd = this.handleAdd.bind(this);
    this.handleAddSection = this.handleAddSection.bind(this);
    this.handleDeleteSection = this.handleDeleteSection.bind(this);
    this.handleAddRecordset = this.handleAddRecordset.bind(this);
    this.handleDeleteRecordset = this.handleDeleteRecordset.bind(this);
    this.handleEditToggle = this.handleEditToggle.bind(this);
    this.handleAddField = this.handleAddField.bind(this);

    this.state = {
      section: props.data,
      open: false,
      edit: false,
    };
  }

  handleClick() {
    this.setState({ open: !this.state.open })
  }

  handleChange(attr, value) {
    console.log('got here');
    const {section} = this.state;
    section[attr] = value;
    this.setState({
      section: section
    })
  }

  handleSectionChange(key, attr, value){
    const {section} = this.state;

    section.sectionsAsList.forEach(function (item) {
        if (item.key === key) {
          item[attr] = value;
        }
    });

    this.setState({
      section: section
    })
  }

  handleRecordsetChange(key, attr, value){
    const {section} = this.state;

    section.recordsetsAsList.forEach(function (item) {
        if (item.key === key) {
          item[attr] = value;
        }
    });

    this.setState({
      section: section
    })
  }

  handleAdd(key) {
    this.setState({editKey: key})
  }

  handleAddSection() {
    const {section} = this.state;

    for (var ii=0; ii<section.sectionsAsList.length; ii++) {

        var subSection = section.sectionsAsList[ii];

        if (subSection.key === this.state.editKey) {

          if (subSection.sectionsAsList == null) {
            subSection.sectionsAsList = [];
          }

          subSection.sectionsAsList.push(createSection());
          break;
        }
    };

    this.setState({
      section: section,
      editKey: null
    })
  }

  handleDeleteSection(key) {
    const {section} = this.state;

    console.log('Deleting Section: ' + key);

    for (var ii=0; ii<section.sectionsAsList.length; ii++) {
        if (section.sectionsAsList[ii].key === key) {
          section.sectionsAsList.splice(ii, 1);
          break;
        }
    };

    this.setState({
      section: section
    })
  }

  handleAddRecordset(name) {
    const {section} = this.state;

    for (var ii=0; ii<section.sectionsAsList.length; ii++) {

        var subSection = section.sectionsAsList[ii];

        if (subSection.key === this.state.editKey) {

          if (subSection.recordsetsAsList == null) {
            subSection.recordsetsAsList = [];
          }

          subSection.recordsetsAsList.push(createRecordset());
          break;
        }
    };

    this.setState({
      section: section,
      editKey: null
    })

  }

  handleDeleteRecordset(key) {
    const {section} = this.state;

    console.log('Deleting Recordset: ' + key);

    for (var ii=0; ii<section.recordsetsAsList.length; ii++) {
        if (section.recordsetsAsList[ii].key === key) {
          section.recordsetsAsList.splice(ii, 1);
          break;
        }
    };

    this.setState({
      section: section
    })
  }

  handleAddField(key) {
    const {section} = this.state;

    console.log('Add Recordset Field: ' + key);

    for (var ii=0; ii<section.recordsetsAsList.length; ii++) {
        if (section.recordsetsAsList[ii].key === key) {
          section.recordsetsAsList[ii].fieldDefinitions.push({
            key: uuid.v4(),
            name: "newField",
            dataType: "STRING_TYPE"
          })
          break;
        }
    };

    this.setState({
      section: section
    })
  }

  handleEditToggle(e) {
    this.setState({ edit: !this.state.edit })
  }

  render() {

    const { section } = this.state;

    return (
      <div>
        <Tabs>
          {section.sectionsAsList != null && section.sectionsAsList.map((item) => {return (
            <Tab key={item.key} label={<TabTextField
                                          item={item}
                                          onNameChange={this.handleSectionChange.bind(this)}
                                          onAdd={this.handleAdd}
                                          onDelete={this.handleDeleteSection}
                                        />}>
              <Section data={item}/>
            </Tab>
          )})}
          {section.recordsetsAsList != null && section.recordsetsAsList.map((item) => {return (
            <Tab key={item.key} label={<TabTextField
                                          item={item}
                                          onNameChange={this.handleRecordsetChange.bind(this)}
                                          onAdd={this.handleAddField}
                                          onDelete={this.handleDeleteRecordset}
                                        />}  >
              <RecordSet data={item}/>
            </Tab>
          )})}
        </Tabs>
        <DialogAddOptions
          title="Select Option"
          open={this.state.editKey != null}
          onClose={() => this.setState({editKey: null})}
          options={[
            {
              key: 'section',
              name: 'Section',
              onClick: this.handleAddSection
            },{
              key: 'recordset',
              name: 'Recordset',
              onClick: this.handleAddRecordset
            }
          ]}
      />
      </div>
    );
  }
}


// TODO Pull the below functions out into their own file in common which can be imported by others.

function createSection() {
  return {
    key: uuid.v4(),
    name: "ChangeMe",
    sectionsAsList: [],
    recordsetsAsList: []
  }
}

function createRecordset() {

  return {
    key: uuid.v4(),
    name: "ChangeMe2",
    fieldDefinitions: [],
    minRecords: 0,
    maxRecords: 1,
  }
}
