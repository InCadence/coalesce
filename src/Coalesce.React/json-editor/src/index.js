import * as React from "react";
import * as ReactDOM from "react-dom";
import { loadJSON, saveJSON } from 'common-components/lib/js/propertyController.js'
import {Menu} from 'common-components/lib/index.js'
import { DialogMessage, DialogOptions } from 'common-components/lib/components/dialogs'
import JsonView from './JsonView'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'

var pjson = require('../package.json');
document.title = pjson.title;

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
  }
]

class Main extends React.Component {

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

    const { name, data } = this.state;
    const that = this;

    this.setState({message: 'Saving...'});

    saveJSON(name, data).then((result) => {
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
      <MuiThemeProvider>
        <div>
        <Menu logoSrc={pjson.icon} title={(item == null || item.name === 'NA') ? pjson.title : `${pjson.title} - ${item.name}`} items={[
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
        {item != null && item.name !== 'NA'&&
          <div className="ui-widget">
            <div className="ui-widget-content" >
              <JsonView data={data} onChange={this.onChange} />
            </div>
          </div>
        }
        <DialogOptions
          title="JSON Configurations"
          open={item == null && this.state.error == null}
          onClose={() => {this.setState({item: {name: 'NA'}})}}
          // TODO These options should be pulled from a configuration file
          options={options.map((item) => {
            return {
              key: item.key,
              name: item.name,
              onClick: () => {this.loadConfiguration(item)}
            }
          })}

        />
        <DialogMessage
          title="Error"
          message={this.state.error}
          opened={this.state.error != null}
          onClose={() => {this.setState({error: null})}}
        />
        <DialogMessage
          title=""
          message={this.state.message}
          opened={this.state.message != null}
          onClose={() => {this.setState({message: null})}}
        />
        </div>
    </MuiThemeProvider>

    )
  }
};

ReactDOM.render(
  <Main />,
  document.getElementById('main')
);
