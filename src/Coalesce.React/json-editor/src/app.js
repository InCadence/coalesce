import React from "react";
import { loadJSON, saveJSON } from 'coalesce-components/lib/js/propertyController.js'
import {Menu} from 'coalesce-components/lib/components'
import { DialogMessage, DialogOptions } from 'coalesce-components/lib/components/dialogs'
import JsonView from './JsonView'

import { MuiThemeProvider } from '@material-ui/core/styles'
import Paper from '@material-ui/core/Paper'

import 'coalesce-components/css/coalesce.css'

// TODO These options should be pulled from a configuration file
const options=[
  {
    key: 'home',
    name: 'Home Page Configuration',
  },{
    key: 'filter',
    name: 'Spider Configruation',
  },{
    key: 'nlpconfig',
    name: 'NLP Configuration',
  },{
    key: 'theme',
    name: 'Theme Configuration',
  }
]

export class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      data: props.data
    }

    this.saveConfiguration = this.saveConfiguration.bind(this);
    this.loadConfiguration = this.loadConfiguration.bind(this);
    this.onChange = this.onChange.bind(this);

  }

  saveConfiguration() {

    const { item, data } = this.state;
    const that = this;

    this.setState({message: 'Saving...'});

    saveJSON(item.key, data).then((result) => {
      this.setState({message: 'Saved'});
    }).catch(function(error) {
      that.setState({error: `Saving: ${error}`, message: null})
    });

  }

  loadConfiguration(item) {

    const that = this;

    this.setState({item: item });

    loadJSON(item.key).then((json) => {
      that.setState({
        data: json
      })
    }).catch(function(error) {
      that.setState({
        item: null,
        error: `Loading Settings: ${error}`
      })
    });
  }

  onChange(json) {
    this.setState({
      data: json
    })
  }

  render() {
    const {data, item} = this.state;

    return (
      <MuiThemeProvider theme={this.props.theme}>
        <div>
        <Menu logoSrc={this.props.icon} title={(item == null || item.name === 'NA') ? this.props.title : `${this.props.title} - ${item.name}`} items={[
          {
            id: 'load',
            name: 'Load',
            img: '/images/svg/load.svg',
            title: 'Load JSON',
            onClick: () => {
              this.setState({item: null, data: null});
            }
          },{
            id: 'Save',
            name: 'Save',
            img: '/images/svg/save.svg',
            title: 'Save JSON',
            onClick: () => {
              this.saveConfiguration();
            }
          }
        ]}/>
        {data != null &&
          <Paper style={{padding: '5px', margin: '10px'}}>
              <JsonView data={data} onChange={this.onChange} />
          </Paper>

        }
        {item == null && this.state.error == null &&
          <DialogOptions
            title="Load JSON Configurations"
            open={true}
            onClose={() => {this.setState({item: {name: 'NA'}})}}
            options={options.map((item) => {
              return {
                key: item.key,
                name: item.name,
                onClick: () => {this.loadConfiguration(item)}
              }
            })}
          >
          </DialogOptions>
        }
        {this.state.error != null &&
          <DialogMessage
            title="Error"
            message={this.state.error}
            opened={true}
            onClose={() => {this.setState({error: null})}}
          />
        }
        { this.state.message != null &&
          <DialogMessage
            title=""
            message={this.state.message}
            opened={true}
            onClose={() => {this.setState({message: null})}}
          />
        }
      </div>
    </MuiThemeProvider>

    )
  }
};

export default App
