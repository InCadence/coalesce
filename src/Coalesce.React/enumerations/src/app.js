import React from 'react'
import { MuiThemeProvider } from '@material-ui/core/styles'
import Menu from 'common-components/lib/components/menu'
import { getEnumerationValues } from 'common-components/lib/js/enumerationController';
import { Enums } from './enums'
import { EnumValues } from './enumValues'
import { EnumAssociatedValues } from './enumAssociatedValues'
import { DialogMessage, DialogLoader } from 'common-components/lib/components/dialogs';


const enumCols = [
  {
    key: "metadata.enumname",
    Header: 'Name',
    accessor: 'values[0]'
  },{
    key: "metadata.description",
    Header: 'Description',
    accessor: 'values[1]'
  },{
    key: "CoalesceEntity.datecreated",
    Header: 'Created',
    accessor: 'values[2]'
  },{
    key: "CoalesceEntity.lastmodified",
    Header: 'Last Modified',
    accessor: 'values[3]'
  }
]

export class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      values: [],
      error: props.error
    }

    this.loadValues = this.loadValues.bind(this);
    this.loadAssociatedValues = this.loadAssociatedValues.bind(this);
    this.loadEnumerations = this.loadEnumerations.bind(this);
  }

  loadEnumerations() {
    this.setState(() => {return {
      associatedValues: null,
      enumKey: null
    }});
  }

  loadValues(key) {

    const that = this;
    const { values } = this.state;

    if (values[key] == null) {

      that.setState(() => {return {loading: 'Loading Values'}})
      getEnumerationValues(key).then((values) => {
        values[key] = {hits: values};

        that.setState(() => {return {
          associatedValues: null,
          loading: null,
          enumKey: key,
          values : values
        }});

      }).catch((err) => {
        this.setState(() => {return {error: err.message}})
      })

    } else {
      that.setState(() => {return {
        enumKey: key,
        associatedValues: null
      }});
    }
  }

  loadAssociatedValues(key, values) {

    this.setState(() => {return {
      associatedValues: values
    }});

  }

  render() {

    return (
      <MuiThemeProvider theme={this.props.theme}>
        <Menu logoSrc={this.props.icon} title={this.props.title} items={[/* No Options */]}/>
        { !this.state.enumKey &&
          <Enums
            data={this.props.enums}
            columns={enumCols}
            loadValues={this.loadValues}
            theme={this.props.theme}
          />
        }
        { this.state.enumKey && !this.state.associatedValues &&
          <EnumValues
            enumKey={this.state.enumKey}
            data={this.state.values[this.state.enumKey]}
            loadAssociatedValues={this.loadAssociatedValues}
            loadEnumerations={this.loadEnumerations}
            theme={this.props.theme}
           />
        }
        { this.state.enumKey && this.state.associatedValues &&
          <EnumAssociatedValues
            enumKey={this.state.enumKey}
            data={this.state.associatedValues}
            loadValues={this.loadValues}
            theme={this.props.theme}
          />
        }
        { this.state.error &&
          <DialogMessage
            title="Error"
            opened={true}
            message={this.state.error}
            onClose={() => {this.setState({error: null})}}
          />
        }
        { this.state.loading &&
          <DialogLoader
            title={this.state.loading}
            opened={true}
          />
        }
      </MuiThemeProvider>
    )
  }

}
