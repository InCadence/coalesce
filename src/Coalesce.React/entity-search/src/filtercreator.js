import React from 'react';
import ReactTable from 'react-table'
import { ReactTableDefaults } from 'react-table'
import IconButton from 'common-components/lib/components/IconButton'
import FieldInput from 'common-components/lib/components/FieldInput'
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelActions from '@material-ui/core/ExpansionPanelActions';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';
import { OPERATORS } from 'common-components/lib/js/searchController.js';

Object.assign(ReactTableDefaults, {
  defaultPageSize: 5,
  minRows: 3,
  // etc...
})

const options = [
  {
    enum: "AND",
    label: "AND"
  },{
    enum: "OR",
    label: "OR"
  }
]

class FilterCreator extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      data: props.data,
      isOpened: true,
      currentkey: 0,
      promptProperties: false,
      properties: createPropertyList(props.recordsets)
    }

    this.handleUpdate = this.handleUpdate.bind(this);
    this.handleAddCriteria = this.handleAddCriteria.bind(this);
    this.handleAddGroup = this.handleAddGroup.bind(this);
    this.handleRemoveGroup = this.handleRemoveGroup.bind(this);
  }

  shouldComponentUpdate(nextProps, nextState) {
    if (this.props.data !== nextProps.data) {
      nextState.data = nextProps.data;
      nextState.properties = createPropertyList(nextProps.recordsets);
      nextState.selectedColumns = [];
    }

    return true;
  }

  render() {
    const { data } = this.state;

    var that = this;

    return (
        <ExpansionPanel  defaultExpanded>
           <ExpansionPanelSummary style={{padding: '5px', height: '32px'}} expandIcon={<ExpandMoreIcon />}>
             <Typography variant="headline">
               {this.props.label} Criteria
             </Typography>
           </ExpansionPanelSummary>
           <div style={{padding: '5px'}}>
             {!this.props.isChild &&
               <FieldInput
                 field={{
                   key: "columns",
                   name: "Columns",
                   label: "Columns",
                   value: this.state.selectedColumns,
                   showLabels: true
                 }}
                 dataType="ENUMERATION_LIST_TYPE"
                 attr="value"
                 options={this.state.properties}
                 showLabels={true}
                 onChange={(value) => {
                   this.setState({selectedColumns: value});
                   this.props.handleUpdate(this.state.data, value);
                 }}
               />
             }
             <FieldInput
                label="Operator"
                field={data}
                attr="operator"
                dataType="ENUMERATION_TYPE"
                options={options}
                onChange={(value) => {
                  const { data } = this.state;

                  data.operator = value;

                  this.handleUpdate(data);
                }}
              />
               <ReactTable
                 style={{width: '100%'}}
                 pageSize={this.props.maxRows}
                 data={data.criteria}
                 showPagination={false}
                 columns={createColumns(this, this.props.recordsets, this.state.currentkey)}
               />
               <div style={{padding: '5px', backgroundColor: "#CCCCCC"}}>
              {
                data.groups.map(function(group) { return(
                  <FilterCreator
                    maxRows={that.props.maxRows}
                    recordsets={that.props.recordsets}
                    data={group}
                    handleRemoveGroup={that.props.handleRemoveGroup}
                    handleError={that.props.handleError}
                    isChild
                  />
                )})
              }
              </div>
            </div>
            <Divider />
            <ExpansionPanelActions  style={{padding: '5px'}}>
              <IconButton icon="/images/svg/add.svg" title="Add Criteria" onClick={this.handleAddCriteria} />
              <IconButton icon="/images/svg/new.svg" title="Add Sub Grouping" onClick={this.handleAddGroup}/>
              {this.props.isChild &&
              <IconButton icon="/images/svg/remove.svg" title="Remove Grouping" onClick={this.props.handleRemoveGroup} />
              }
            </ExpansionPanelActions>
          </ExpansionPanel>
    )
  }

  handleUpdate(data) {
      this.setState({data: data});

      const { selectedColumns } = this.state;

      if (this.props.handleUpdate) {
        this.props.handleUpdate(data, selectedColumns);
      }
  }

  onRecordsetChange(key, value) {
    //console.log("On record set change key is", key, "e is", e);
    const { data } = this.state;
    var row = this.getRow(data.criteria, key);
    var defaultField;

    for (var ii=0; ii<this.props.recordsets.length; ii++) {
      if (this.props.recordsets[ii].name === value) {
        defaultField = this.props.recordsets[ii].definition[0].name;
        break;
      }
    };

    row.recordset = value;
    row.field = defaultField;
    row.operator = 'EqualTo';
    row.value = '';

    this.handleUpdate(data)
  }

  onFieldChange(key, value) {
    const {data} = this.state;
    var row = this.getRow(data.criteria, key);

    var dataType = getDataType(this.props.recordsets, row);

    row.field = value;
    row.operator = OPERATORS[dataType][0];
    row.value = '';

    this.handleUpdate(data)
  }

  onOperatorChange(key, e) {
    const {data} = this.state;
    var row = this.getRow(data.criteria, key);

    row.operator = e.target.value;

    this.handleUpdate(data)
  }

  onValueChange(key, e) {
    const {data} = this.state;
    var row = this.getRow(data.criteria, key);

    row.value = e.target.value;

    this.handleUpdate(data)
  }

  onMatchCaseChange(key, e) {
    const {data} = this.state;
    var row = this.getRow(data.criteria, key);

    row.matchCase = e.target.checked;

    this.handleUpdate(data)
  }

  onAndOrChange(e) {
    const { data } = this.state;

    data.operator = e.target.value;
    //Want to set the props to
    this.handleUpdate(data);
  }

  handleRemoveCriteria(key, e) {

    const { data } = this.state;
    // Delete Row
    for (var ii=0; ii<data.criteria.length; ii++) {
      if (data.criteria[ii].key === key) {
          data.criteria.splice(ii, 1);
          break;
      }
    }

    // Save State
    this.handleUpdate(data)
  }

  handleAddGroup(){
    const { data } = this.state;
    var tempTable = {
                     operator:'AND',
                     criteria: [{key:0,
                     recordset: 'CoalesceEntity',
                     field: 'name',
                     operator: 'EqualTo',
                     value: '',
                     matchCase: false}],
                     groups:[]
                    };
    data.groups.push(tempTable);

    this.handleUpdate(data)
  }

  handleRemoveGroup() {
    this.props.handleError("(Coming Soon!!!) This will allow you to remove a criteria group.");
  }

  handleAddCriteria() {
    const {currentkey, data} = this.state;
    var keyvalue = currentkey + 1;

    if (data.criteria.length < this.props.maxRows) {
      // Create New Data Row
      data.criteria.push({
        key: keyvalue,
        recordset: this.props.recordsets[0].name, //Assuming we always start with an existing recordset when we instantiate a filtercreator, always match with that
        field: this.props.recordsets[0].definition[0].name,
        operator: 'EqualTo',
        value: '',
        matchCase: false
      });

      // Save State
      this.setState({currentkey: keyvalue });

      this.handleUpdate(data)
    } else {
      alert("Row limit reached");
    }
  }

  getRow(data, key) {
    var result;

    for (var ii=0; ii<data.length; ii++) {
      if (data[ii].key === key) {
          result = data[ii];
          break;
      }
    }

    return result;
  }
}

function createPropertyList(recordsets) {

  var properties = [];

  recordsets.forEach((recordset) => {

    recordset.definition.forEach((definition) => {
      properties.push({
          enum: recordset.name + '.' + definition.name,
          label: recordset.name + '.' + definition.name
      });
    })

  })

  return properties;

}

function getDataType(recordsets, criteria) {

  var dataType = 'STRING_TYPE';

  for (var ii=0; ii<recordsets.length; ii++) {
    if (criteria.recordset === recordsets[ii].name) {
      for (var jj=0; jj<recordsets[ii].definition.length; jj++) {
          var def = recordsets[ii].definition[jj];

          if (def.name === criteria.field) {
            dataType = def.dataType;
            break;
          }
      };
      break;
    }
  }

  return dataType;
}

function createColumns(that, recordsets, key) {

  var columns = [{Header: 'key', accessor: 'key', show: false}];
  if (recordsets != null) {
    columns.push({
      Header: '',
      accessor: 'delete',
      resizable: false,
      sortable: false,
      width: 34,
      Cell: (cell) => (
        <IconButton
          icon={"/images/svg/remove.svg"}
          title="Remove Criteria"
          size="20px"
          onClick={that.handleRemoveCriteria.bind(that, cell.row.key)}
          square
        />
      )
    });

    columns.push({
      Header: 'Recordset',
      accessor: 'recordset',
      resizable: false,
      sortable: false,
      Cell: (cell) => {
        return (
          <FieldInput
            field={cell.original}
            dataType="ENUMERATION_TYPE"
            attr="recordset"
            showLabels={false}
            onChange={that.onRecordsetChange.bind(that, cell.original.key)}
            options={recordsets.map((recordset) => {
              return ({
                enum: recordset.name,
                label: recordset.name
              });
            })}
          />
        )}
    });

    columns.push({
      Header: 'Field',
      accessor: 'field',
      resizable: false,
      sortable: false,
      Cell: (cell) => {
        var options = [];
        for (var ii=0; ii<recordsets.length; ii++) {
          if (cell.original.recordset === recordsets[ii].name) {
            recordsets[ii].definition.forEach(function (field) {
                options.push({
                  enum: field.name,
                  label: field.name
                });
                //<option key={field.name + cell.row.key} value={field.name}>{field.name}</option>);
            });
            break;

          }
        };

        return (
            <FieldInput
              field={cell.original}
              dataType="ENUMERATION_TYPE"
              attr="field"
              showLabels={false}
              options={options}
              onChange={that.onFieldChange.bind(that, cell.original.key)}
            />
          )
        }
    });

    columns.push({
      Header: 'Not',
      accessor: 'not',
      resizable: false,
      sortable: false,
      width: 34,
      Cell: (cell) => (
        <FieldInput field={cell.original} dataType="BOOLEAN_TYPE" attr="not" showLabels={false} />
      )
    });

    columns.push({
      Header: '',
      accessor: 'operator',
      resizable: false,
      sortable: false,
      width: 120,
      Cell: (cell) => {

        var dataType = getDataType(recordsets, cell.original);

        return (
          <FieldInput
            field={cell.original}
            dataType="ENUMERATION_TYPE"
            attr="operator"
            showLabels={false}
            options={
                      OPERATORS[dataType].map((item) => {
                        return {
                          enum: item,
                          label: item
                        }
                      })
                    }
          />
        )
      }
    });

    columns.push({
      Header: 'Value',
      accessor: 'value',
      resizable: false,
      sortable: false,
      Cell: (cell) => {
        var dataType = getDataType(recordsets, cell.original);

        switch (dataType) {
          case 'ENUMERATION_TYPE':
          case 'POLYGON_TYPE':
          case 'CIRCLE_TYPE':
          case 'GEOCOORDINATE_TYPE':
          case 'GEOCOORDINATE_LIST_TYPE':
          case 'LINE_STRING_TYPE':
          case 'DATE_TIME_TYPE':
            console.log(`${dataType} currently not supported`);
            dataType = 'STRING_TYPE';
            break;
          default:
           // Do Nothing
        }

        return (
          <FieldInput field={cell.original} dataType={dataType} attr="value" showLabels={false} />
        )
      }
    });

    columns.push({
      Header: 'Case',
      accessor: 'case',
      resizable: false,
      sortable: false,
      width: 40,
      Cell: (cell) => (
        <FieldInput field={cell.original} dataType="BOOLEAN_TYPE" attr="matchCase" showLabels={false} />
      )
    });

    /*columns.push({
      Header: 'AndOr',
      accessor: 'andor',
      resizable: false,
      sortable: false,
      width: 100,
      Cell: (cell) => (
        <select className="form-control" value={//need an array of and/or for each criteria.andor} onChange={that.onAndOrChange.bind(that, cell.row.key)}>
          <option>AND</option>
          <option>OR</option>
        </select>
      )
    });*/
  }
  return columns;
}

export default FilterCreator;
