import React from "react";
import ReactTable from "react-table";
import {ReactTableDefaults} from "react-table";
import IconButton from "common-components/lib/components/IconButton";
import FieldInput from "common-components/lib/components/FieldInput";
import ExpansionPanel from "@material-ui/core/ExpansionPanel";
import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary";
import ExpansionPanelActions from "@material-ui/core/ExpansionPanelActions";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Typography from "@material-ui/core/Typography";
import Divider from "@material-ui/core/Divider";
import {OPERATORS} from "common-components/lib/js/searchController.js";
import {Row, Col} from "react-bootstrap";
import uuid from "uuid";

Object.assign(ReactTableDefaults, {
  defaultPageSize: 5,
  minRows: 3
  // etc...
});

const options = [
  {
    enum: "AND",
    label: "AND"
  },
  {
    enum: "OR",
    label: "OR"
  }
];

class FilterCreator extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      data: props.data,
      properties: props.properties ? props.properties : createPropertyList(props.recordsets)
    };

  }

  shouldComponentUpdate(nextProps, nextState) {
    if (this.props.data !== nextProps.data) {
      nextState.data = nextProps.data;
      nextState.properties = createPropertyList(nextProps.recordsets);
      nextState.selectedCapabilities = [];
    }

    return true;
  }

  render() {
    const {data} = this.state;

    var that = this;

    return (
      <ExpansionPanel defaultExpanded>
        <ExpansionPanelSummary
          style={{padding: "5px", height: "32px"}}
          expandIcon={<ExpandMoreIcon />}
        >
          <Typography variant="headline">
            {this.props.label} Criteria
          </Typography>
        </ExpansionPanelSummary>
        <div style={{padding: "5px"}}>
          {!this.props.isChild && (
            <Row>
              <Col xs={10}>
                <FieldInput
                  field={{
                    key: "columns",
                    name: "Columns",
                    label: "Columns to Return",
                    value: this.props.selectedColumns,
                    showLabels: true
                  }}
                  dataType="ENUMERATION_LIST_TYPE"
                  attr="value"
                  sorted
                  options={this.state.properties}
                  showLabels={true}
                  onChange={value => {
                    this.props.handleUpdate(data, value);
                  }}
                />
              </Col>
              <Col xs={2}>
                <FieldInput
                  field={{
                    key: "capabilities",
                    name: "Capabilities",
                    label: "Capabilities",
                    value: this.state.selectedCapabilities,
                    showLabels: true
                  }}
                  dataType="ENUMERATION_LIST_TYPE"
                  attr="value"
                  options={[
                    {
                      enum: "HIGHLIGHT",
                      label: "Highlight"
                    },
                    {
                      enum: "LUCENE_SYNTAX",
                      label: "Lucene"
                    }
                  ]}
                  showLabels={true}
                  onChange={value => {
                    this.setState({selectedCapabilities: value});
                    this.props.handleCapabilityUpdate(value);
                  }}
                />
              </Col>
            </Row>
          )}

          <FieldInput
            label="Operator"
            field={data}
            attr="operator"
            dataType="ENUMERATION_TYPE"
            options={options}
            onChange={value => {
              const {data} = this.state;

              data.operator = value;

              this.handleUpdate(data);
            }}
          />
          <ReactTable
            style={{width: "100%"}}
            pageSize={this.props.maxRows}
            data={data.criteria}
            showPagination={false}
            columns={createColumns(this, this.props.recordsets)}
          />
          <div style={{padding: "5px", backgroundColor: "#CCCCCC"}}>
            {data.groups.map(function(group) {
              return (
                <FilterCreator
                  maxRows={that.props.maxRows}
                  properties={that.state.properties}
                  recordsets={that.props.recordsets}
                  data={group}
                  handleRemoveGroup={that.handleRemoveGroup}
                  handleError={that.props.handleError}
                  isChild
                />
              );
            })}
          </div>
        </div>
        <Divider />
        <ExpansionPanelActions style={{padding: "5px"}}>
          {!this.props.isChild && (
            <Row>
              <Col xs={3}>
                <FieldInput
                  label="Page"
                  field={{
                    key: "pageNum",
                    value: this.props.pageNum
                  }}
                  dataType="INTEGER_TYPE"
                  onChange={value => {
                    this.props.handlePageUpdate(value);
                  }}
                />
              </Col>
              <Col xs={3}>
                <FieldInput
                  label="Page Size"
                  field={{
                    key: "pageSize",
                    value: this.props.pageSize
                  }}
                  dataType="INTEGER_TYPE"
                  onChange={value => {
                    this.props.handlePageSizeUpdate(value);
                  }}
                />
              </Col>
              <Col xs={4}>
                <FieldInput
                  label="Sort BY"
                  field={this.props.sortBy[0]}
                  dataType="ENUMERATION_TYPE"
                  attr="propertyName"
                  options={this.props.selectedColumns}
                  showLabels={true}
                  onChange={value => {
                    const {sortBy} = this.props;
                    sortBy[0].propertyName = value;
                    this.props.handleSortUpdate(sortBy);
                  }}
                />
              </Col>
              <Col xs={2}>
                <FieldInput
                  label="Order"
                  field={this.props.sortBy[0]}
                  dataType="ENUMERATION_TYPE"
                  attr="sortOrder"
                  options={["ASC", "DESC"]}
                  showLabels={true}
                  onChange={value => {
                    const {sortBy} = this.props;
                    sortBy[0].sortOrder = value;
                    this.props.handleSortUpdate(sortBy);
                  }}
                />
              </Col>
            </Row>
          )}

          <IconButton
            square
            icon="/images/svg/add.svg"
            title="Add Criteria"
            onClick={this.handleAddCriteria}
          />
          <IconButton
            square
            icon="/images/svg/new.svg"
            title="Add Sub Grouping"
            onClick={this.handleAddGroup}
          />

          {!this.props.isChild && (
            <IconButton
              square
              icon="/images/svg/search.svg"
              title="Execute Search"
              onClick={this.props.handleSearch}
            />
          )}

          {this.props.isChild && (
            <IconButton
              square
              icon="/images/svg/remove.svg"
              title="Remove Grouping"
              onClick={() => {this.props.handleRemoveGroup(this.state.data.key)}}
            />
          )}
        </ExpansionPanelActions>
      </ExpansionPanel>
    );
  }

  handleUpdate = (data) => {
    this.setState({data: data});
  }

  onRecordsetChange = (key, value) => {
    //console.log("On record set change key is", key, "e is", e);
    const {data} = this.state;
    var row = this.getRow(data.criteria, key);
    var defaultField;

    for (var ii = 0; ii < this.props.recordsets.length; ii++) {
      if (this.props.recordsets[ii].name === value) {
        defaultField = this.props.recordsets[ii].definition[0].name;
        break;
      }
    }

    row.recordset = value;
    row.field = defaultField;
    row.operator = "EqualTo";
    row.value = "";

    this.handleUpdate(data);
  }

  onFieldChange = (key, value) => {
    const {data} = this.state;
    var row = this.getRow(data.criteria, key);

    var dataType = getFieldDefinition(this.props.recordsets, row).dataType;

    row.field = value;
    row.operator = OPERATORS[dataType][0];
    row.value = "";

    this.handleUpdate(data);
  }

  onOperatorChange = (key, value) => {
    const {data} = this.state;
    var row = this.getRow(data.criteria, key);

    row.operator = value;

    this.handleUpdate(data);
  }

  handleRemoveCriteria = (key, e) => {
    const {data} = this.state;
    // Delete Row
    for (var ii = 0; ii < data.criteria.length; ii++) {
      if (data.criteria[ii].key === key) {
        data.criteria.splice(ii, 1);
        break;
      }
    }

    // Save State
    this.handleUpdate(data);
  }

  handleAddGroup = () => {
    const {data} = this.state;
    var tempTable = {
      key: uuid.v4(),
      operator: "AND",
      criteria: [
        {
          key: uuid.v4(),
          recordset: "CoalesceEntity",
          field: "name",
          operator: "EqualTo",
          value: "",
          matchCase: false
        }
      ],
      groups: []
    };
    data.groups.push(tempTable);

    this.handleUpdate(data);
  }

  handleRemoveGroup = (key) => {

    const {data} = this.state;

    for (var ii=0; ii<data.groups.length; ii++) {
        if (data.groups[ii].key === key) {
          data.groups.splice(ii, 1);

          // Save State
          this.handleUpdate(data);
          break;
        }
    }


  }

  handleAddCriteria = () => {
    const {data} = this.state;

    if (data.criteria.length < this.props.maxRows) {
      // Create New Data Row
      data.criteria.push({
        key: uuid.v4(),
        recordset: this.props.recordsets[0].name, //Assuming we always start with an existing recordset when we instantiate a filtercreator, always match with that
        field: this.props.recordsets[0].definition[0].name,
        operator: "EqualTo",
        value: "",
        matchCase: false
      });

      this.handleUpdate(data);
    } else {
      alert("Row limit reached");
    }
  }

  getRow(data, key) {
    var result;

    for (var ii = 0; ii < data.length; ii++) {
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

  recordsets.forEach(recordset => {
    recordset.definition.forEach(definition => {
      if (definition.name !== "objectkey") {
        properties.push({
          enum: recordset.name + "." + definition.name,
          label: recordset.name + "." + definition.name
        });
      }
    });
  });

  return properties;
}

function getFieldDefinition(recordsets, criteria) {
  var fd = {dataType: "STRING_TYPE"};

  for (var ii = 0; ii < recordsets.length; ii++) {
    if (criteria.recordset === recordsets[ii].name) {
      for (var jj = 0; jj < recordsets[ii].definition.length; jj++) {
        var def = recordsets[ii].definition[jj];

        if (def.name === criteria.field) {
          fd = def;
          break;
        }
      }
      break;
    }
  }

  return fd;
}

function createColumns(that, recordsets) {
  var columns = [{Header: "key", accessor: "key", show: false}];
  if (recordsets != null) {
    columns.push({
      Header: "",
      accessor: "delete",
      resizable: false,
      sortable: false,
      width: 34,
      Cell: cell => (
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
      Header: "Recordset",
      accessor: "recordset",
      resizable: false,
      sortable: false,
      Cell: cell => {
        return (
          <FieldInput
            field={cell.original}
            dataType="ENUMERATION_TYPE"
            attr="recordset"
            showLabels={false}
            onChange={that.onRecordsetChange.bind(that, cell.original.key)}
            options={recordsets.map(recordset => {
              return {
                enum: recordset.name,
                label: recordset.name
              };
            })}
          />
        );
      }
    });

    columns.push({
      Header: "Field",
      accessor: "field",
      resizable: false,
      sortable: false,
      Cell: cell => {
        var options = [];
        for (var ii = 0; ii < recordsets.length; ii++) {
          if (cell.original.recordset === recordsets[ii].name) {
            recordsets[ii].definition.forEach(function(field) {
              options.push({
                enum: field.name,
                label: field.name
              });
              //<option key={field.name + cell.row.key} value={field.name}>{field.name}</option>);
            });
            break;
          }
        }

        return (
          <FieldInput
            field={cell.original}
            dataType="ENUMERATION_TYPE"
            attr="field"
            showLabels={false}
            options={options}
            onChange={that.onFieldChange.bind(that, cell.original.key)}
          />
        );
      }
    });

    columns.push({
      Header: "Not",
      accessor: "not",
      resizable: false,
      sortable: false,
      width: 34,
      Cell: cell => (
        <FieldInput
          field={cell.original}
          dataType="BOOLEAN_TYPE"
          attr="not"
          showLabels={false}
        />
      )
    });

    columns.push({
      Header: "",
      accessor: "operator",
      resizable: false,
      sortable: false,
      width: 120,
      Cell: cell => {
        var dataType = getFieldDefinition(recordsets, cell.original).dataType;

        return (
          <FieldInput
            field={cell.original}
            dataType="ENUMERATION_TYPE"
            attr="operator"
            showLabels={false}
            onChange={that.onOperatorChange.bind(that, cell.original.key)}
            options={OPERATORS[dataType].map(item => {
              return {
                enum: item,
                label: item
              };
            })}
          />
        );
      }
    });

    columns.push({
      Header: "Value",
      accessor: "value",
      resizable: false,
      sortable: false,
      Cell: cell => {
        var fd = getFieldDefinition(recordsets, cell.original);
        var dataType = fd.dataType;
        var hint = fd.description;

        dataType = dataType.replace("_LIST", "");

        switch (dataType) {
          case "DATE_TIME_TYPE":
            dataType = "STRING_TYPE";
            hint = "2018-09-21T00:00:00.000Z";
            break;
          case "POLYGON_TYPE":
          case "CIRCLE_TYPE":
          case "GEOCOORDINATE_TYPE":
          case "LINE_STRING_TYPE":
            hint = "POLYGON ((x1 y1 z1, x2 y2 z2, ...))";
            dataType = "STRING_TYPE";
            break;
          case "ENUMERATION_TYPE":
            dataType = "STRING_TYPE";
            break;
          default:
          // Do Nothing
        }

        if (
          cell.original.operator === "Between" ||
          cell.original.operator === "During"
        ) {
          cell.original.value = undefined;
          if (!cell.original.values || cell.original.values.length != 2) {
            cell.original.values = ["", ""];
          }
          return (
            <Row>
              <Col xs={6}>
                <FieldInput
                  field={{
                    key: cell.original.key + "_1",
                    value: cell.original.values[0]
                  }}
                  hint={hint}
                  dataType={dataType}
                  attr="value"
                  showLabels={false}
                  onChange={value => (cell.original.values[0] = value)}
                />
              </Col>
              <Col xs={6}>
                <FieldInput
                  field={{
                    key: cell.original.key + "_2",
                    value: cell.original.values[1]
                  }}
                  hint={hint}
                  dataType={dataType}
                  attr="value"
                  showLabels={false}
                  onChange={value => (cell.original.values[1] = value)}
                />
              </Col>
            </Row>
          );
        } else {
          cell.original.values = undefined;

          return (
            <FieldInput
              field={cell.original}
              dataType={dataType}
              hint={hint}
              attr="value"
              showLabels={false}
            />
          );
        }
      }
    });

    columns.push({
      Header: "Case",
      accessor: "case",
      resizable: false,
      sortable: false,
      width: 40,
      Cell: cell => (
        <FieldInput
          field={cell.original}
          dataType="BOOLEAN_TYPE"
          attr="matchCase"
          showLabels={false}
        />
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
