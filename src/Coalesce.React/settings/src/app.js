import React from "react";
import SettingsView from './settings.js'
import { MuiThemeProvider } from '@material-ui/core/styles'
import Paper from '@material-ui/core/Paper'
import { DialogMessage, DialogPrompt } from 'common-components/lib/components/dialogs'

import {Menu} from 'common-components/lib/index.js'
import 'common-components/bootstrap/css/bootstrap.min.css'

import { saveProperties } from 'common-components/lib/js/propertyController'


import 'common-components/css/coalesce.css'

export class App extends React.Component {

  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleAddSetting = this.handleAddSetting.bind(this);

    this.state = {
      settings: this.props.data,
      add: false,
      error: null
    };
  }

  handleSave(data) {
    saveProperties(this.props.data).then((value) => {
      this.setState(() => { return {
        message: "Saved"
      }})
    }).catch((err) => {
      this.setState(() => { return {
        error: err.message
      }})
    })
  }

  handleAddSetting(name) {

      const { settings } = this.state;

      settings[name] = '';

      this.setState(() => {
        return {
          settings: settings,
          add: false
        }
      })

  }

  render() {

    const { settings } = this.state;

    return (
      <MuiThemeProvider theme={this.props.theme}>
          <Menu
            logoSrc={this.props.icon}
            title={this.props.title}
            isTextOnly={false}
            items={[
              {
                id: 'add',
                name: 'Add Setting',
                img: "/images/svg/add.svg",
                title: 'Add Setting',
                onClick: () => {
                  this.setState(() => {
                    return { add: true }
                  })
                }

              },
              {
                id: 'save',
                name: 'Save Settings',
                img: "/images/svg/save.svg",
                title: 'Add Setting',
                onClick: this.handleSave
              }
            ]}
          />
          <Paper style={{padding: '5px', margin: '10px'}}>
            <SettingsView settings={settings} />
          </Paper>
          {(this.state.error != null || this.state.message != null) &&
            <DialogMessage
              title={this.state.error ? "Error" : "" }
              opened={true}
              message={this.state.error ? this.state.error : this.state.message }
              onClose={() => {
                this.setState(() => {
                  return { error: null, message: null }
                })
              }}
            />
          }
          {this.state.add &&
            <DialogPrompt
              title="Enter Setting Name"
              opened={true}
              onClose={() => {this.setState(() => {return {
                add: false,
              }})}}
              value=""
              onSubmit={(value) => {this.handleAddSetting(value)}}
            />
          }
      </MuiThemeProvider>
    )
  }
}

export default App
