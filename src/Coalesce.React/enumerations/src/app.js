import React from 'react'
import Paper from '@material-ui/core/Paper';

import Menu from 'coalesce-components/lib/components/menu'
import { getEnumerationValues } from 'coalesce-components/lib/js/enumerationController';
import { DialogMessage } from 'coalesce-components/lib/components/dialogs';
import { searchComplex } from 'coalesce-components/lib/js/searchController';

import { Enums } from './enums'
import { EnumValues } from './enumValues'
import { EnumAssociatedValues } from './enumAssociatedValues'


const enumCols = [
  {
    key: "enummetadata.enumname",
    Header: 'Name',
    accessor: 'values',
    index: 0,
  },{
    key: "enummetadata.description",
    Header: 'Description',
    accessor: 'values',
    index: 1
  },{
    key: "CoalesceEntity.datecreated",
    Header: 'Created',
    accessor: 'values',
    index: 2
  },{
    key: "CoalesceEntity.lastmodified",
    Header: 'Last Modified',
    accessor: 'values',
    index: 3
  }
]

export class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      values: [],
      error: props.error,
      enums: {}
    }

  }

  componentDidMount() {
    var query = {
      "pageSize": 200,
      "pageNumber": 1,
      "propertyNames": enumCols.map((item) => item.key),
      "group": {
        "operator": "AND",
        "criteria": [{
          'recordset': 'CoalesceEntity',
          'field': 'name',
          'operator': 'EqualTo',
          'value': 'Enumeration'
        },{
          'recordset': 'enummetadata',
          'field': 'enumname',
          'operator': 'NullCheck',
          'not': true
        }]
      },
      "sortBy": [
          {
            "propertyName": enumCols[0].key,
            "sortOrder": "ASC"
          }
        ],
    };

    searchComplex(query).then(response => {
      this.setState(() => {return {enums: response }})
    }).catch(err => {
      this.handleError(err);
    });
  }

  handleError = (err) => {
    this.setState(() => {return {error: err.message }})
  }

  clearError = () => {
    this.setState(() => {return {error: undefined }})
  }

  loadEnumerations = () => {
    this.setState(() => {return {
      associatedValues: undefined,
      enumKey: undefined
    }});
  }

  loadValues = (key) => {

    const that = this;
    const { values } = this.state;

    if (values[key] == null) {

      values[key] = {hits: undefined};

      that.setState(() => {return {
        enumKey: key,
        associatedValues: undefined,
        values : values
      }})
      getEnumerationValues(key).then((results) => {
        
        results.forEach(result => result.entityKey = result.key);

        values[key] = {hits: results};

        that.setState(() => {return {
          values : values
        }});

      }).catch((err) => {
        this.handleError(err);
      })

    } else {
      that.setState(() => {return {
        enumKey: key,
        associatedValues: undefined
      }});
    }
  }

  loadAssociatedValues = (key, values) => {

    this.setState(() => {return {
      associatedValues: values
    }});

  }

  render() {

    return (
      <React.Fragment>
        <Menu logoSrc={this.props.icon} title={this.props.title} items={[/* No Options */]}/>
        <Paper  style={{padding: '5px', margin: '10px'}}>
        { !this.state.enumKey &&
          <Enums
            data={this.state.enums}
            columns={enumCols}
            loadValues={this.loadValues}
          />
        }
        { this.state.enumKey && !this.state.associatedValues &&
          <EnumValues
            enumKey={this.state.enumKey}
            data={this.state.values[this.state.enumKey]}
            loadAssociatedValues={this.loadAssociatedValues}
            loadEnumerations={this.loadEnumerations}
           />
        }
        { this.state.enumKey && this.state.associatedValues &&
          <EnumAssociatedValues
            enumKey={this.state.enumKey}
            data={this.state.associatedValues}
            loadValues={this.loadValues}
          />
        }
        </Paper>
        { this.state.error &&
          <DialogMessage
            title="Error"
            opened={true}
            message={this.state.error}
            onClose={this.clearError}
          />
        }
      </React.Fragment>
    )
  }

}
