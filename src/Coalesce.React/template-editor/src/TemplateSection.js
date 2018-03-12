import React from 'react';
import {Tabs, Tab} from 'material-ui/Tabs';
import TextField from 'material-ui/TextField';
import { RecordSet } from './TemplateRecordset'

export class Section extends React.Component {

  constructor(props) {
    super(props);
    this.handleClick = this.handleClick.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleAddSection = this.handleAddSection.bind(this);
    this.handleDeleteSection = this.handleDeleteSection.bind(this);
    this.handleAddRecordset = this.handleAddSection.bind(this);
    this.handleDeleteRecordset = this.handleDeleteSection.bind(this);
    this.handleEditToggle = this.handleEditToggle.bind(this);

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

  handleAddSection(name) {

  }

  handleDeleteSection(name) {

  }

  handleAddRecordset(name) {

  }

  handleDeleteRecordset(name) {

  }

  handleEditToggle(e) {
    this.setState({ edit: !this.state.edit })
  }

  render() {

    const { section } = this.state;

    return (
      <div>
        <Tabs>
          {section.sectionsAsList.map((item) => {return (
            <Tab key={item.key} label={<TextField
              fullWidth={true}
              underlineShow={false}
              inputStyle={{'color': 'rgb(255, 255, 255)', 'text-align': 'center'}}
              value={item.name}
              onChange={(event, value) => {this.handleSectionChange(item.key, "name", value);}}
            />}>
              <Section data={item}/>
            </Tab>
          )})}
          {section.recordsetsAsList.map((item) => {return (
            <Tab key={item.key} label={<TextField
              fullWidth={true}
              underlineShow={false}
              inputStyle={{'color': 'rgb(255, 255, 255)', 'text-align': 'center'}}
              value={item.name}
              onChange={(event, value) => {this.handleRecordsetChange(item.key, "name", value);}}
            />}  >
              <RecordSet data={item}/>
            </Tab>
          )})}
        </Tabs>
      </div>
    );
  }
}
