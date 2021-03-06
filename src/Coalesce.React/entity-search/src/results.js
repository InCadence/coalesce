import React from 'react';
import ReactTable from 'react-table'

import IconButton from 'coalesce-components/lib/components/IconButton'
import FieldInput from 'coalesce-components/lib/components/FieldInput'
import { deleteEntities } from 'coalesce-components/lib/js/entityController'
import { DialogMessage } from 'coalesce-components/lib/components/dialogs'
import { toCSV } from 'coalesce-components/lib/js/csv'
import { saveFile } from 'coalesce-components/lib/js/file'

import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelActions from '@material-ui/core/ExpansionPanelActions';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';

const MAX_LENGTH = undefined;
const COLUMN_BUTTON_WIDTH = 30;

export class SearchResults extends React.Component {

  constructor(props) {
    super(props);

    this.handleDelete = this.handleDelete.bind(this);
    this.handleDeleteConfirmed = this.handleDeleteConfirmed.bind(this);
    this.handleDeleteCancel = this.handleDeleteCancel.bind(this);

    this.handleDownload = this.handleDownload.bind(this);
    this.handleCheckAll = this.handleCheckAll.bind(this);
    this.handleCheck = this.handleCheck.bind(this);

    var meta = {allChecked: false};
    var columns = this.createColumns(this.props.properties, meta);

    this.state = {
        meta: meta,
        columns: columns,
        tabledata: this.createData(columns, this.props.data)
      }

  }

  componentWillReceiveProps(nextProps) {

    if (nextProps.data.key !== this.props.data.key) {

      var meta = {allChecked: false};
      var columns = this.createColumns(nextProps.properties, meta);

      this.setState(() => {return {
          meta: meta,
          columns: columns,
          tabledata: this.createData(columns, nextProps.data)
        }
      })
    }
  }

  render() {

    const {columns, tabledata} = this.state;

    return (
      <ExpansionPanel  defaultExpanded>
         <ExpansionPanelSummary style={{padding: '5px', height: '32px'}} expandIcon={<ExpandMoreIcon />}>
           <Typography variant="headline">
             Query Results
           </Typography>
         </ExpansionPanelSummary>
         <ReactTable
          className="-striped -highlight"
          data={tabledata}
          columns={columns}
        />
        <Divider />
        <ExpansionPanelActions  style={{padding: '5px', float: 'right'}}>
          <IconButton icon="/images/svg/download.svg" title="Download Results" onClick={this.handleDownload} />
          <IconButton icon="/images/svg/delete.svg" title="Delete Results" onClick={this.handleDelete} />
        </ExpansionPanelActions>
        { this.state.keysToDelete &&
          <DialogMessage
            title="Mark as Deleted"
            confirmation
            opened={true}
            message={`This will mark ${this.state.keysToDelete.length} Entities as deleted. This means they will no longer be discoverable using search, however using the key you can still load them in the Entity Editor.`}
            onClick={this.handleDeleteConfirmed}
            onClose={this.handleDeleteCancel}
          />
        }
     </ExpansionPanel>
     )

  }

  handleDownload() {

    const { tabledata, columns } = this.state;

    // Derive Headers from columns
    var headers = columns.filter(column => column.show !== false && column.width !== COLUMN_BUTTON_WIDTH).map((item) => item.accessor);

    // Map tabledata into CSV rows
    var data = tabledata.filter(item => item.checked).map((item) => {

      var row = {};

      for (var ii=0; ii<headers.length && ii<item.values.length; ii++) {
        row[headers[ii]] = item.values[ii];
      }

      return row
    });

    saveFile(new Blob([toCSV(data, headers, MAX_LENGTH)]), `query-results.csv`);

  }

  escapeDoubleQuotes = (value) => {

    if (value && typeof value === 'string' && value.includes('"')) {
      value = value.replace(/"/g, '""');
    }

    return value;
  }

  handleDeleteCancel() {
    this.setState(() => {return {keysToDelete: undefined}});
  }

  handleDeleteConfirmed() {

    this.props.handleSpinner(`Deleting ${this.state.keysToDelete.length} Entities`);

    deleteEntities(this.state.keysToDelete).then((value) => {
      this.props.handleSpinner(undefined);
    }).catch((err) => {
      this.props.handleError(`Failed to delete all or some of the entities`)
      this.props.handleSpinner(undefined);
    })

    this.setState(() => {return {keysToDelete: undefined}});
  }

  handleDelete() {

    const {tabledata} = this.state;

    var keysToDelete = [];

    tabledata.forEach(function (hit) {
      if (hit.checked) {
        keysToDelete.push(hit.entityKey)
      }
    })

    this.setState(() => {return {keysToDelete: keysToDelete}})
  }

  handleCheckAll(value) {
    const {tabledata, meta} = this.state;

    if (tabledata) {
      tabledata.forEach(function (hit) {
        hit.checked = value
      })

      meta.allChecked = value;
      this.setState(() => {return {meta: meta}} )
    }
  }

  handleCheck(value) {
    const { meta, tabledata } = this.state;
    var checked = value;

    for (var ii=0; ii<tabledata.length && checked; ii++) {
      checked = checked && tabledata[ii].checked;
    }

    if (checked !== meta.allChecked) {
      meta.allChecked = checked;
      this.setState(() => {return {meta: meta}});
    }
  }

  createColumns(properties, meta) {

    var columns = [
      {
        // Add CheckBox Column
        Header: (<FieldInput field={meta} dataType="BOOLEAN_TYPE" attr="allChecked" showLabels={false} onChange={this.handleCheckAll}/>),
        accessor: 'select',
        resizable: false,
        sortable: false,
        width: COLUMN_BUTTON_WIDTH,
        Cell: (cell) => (
          <FieldInput field={cell.original} dataType="BOOLEAN_TYPE" attr="checked" showLabels={false} onChange={this.handleCheck} />
        )
      },{
        // Add Entity Key Column
        Header: 'Key',
        accessor: 'entityKey',
        show: false
      }
    ];

    // Add Property Column(s)
    properties.forEach(function (property) {
      var parts = property.split(".");

      columns.push({
        Header: parts[1],
        accessor: parts[1]
      })
    });

    // Add Button Column
    columns.push({
      Header: '',
      accessor: 'button',
      resizable: false,
      sortable: false,
      width: COLUMN_BUTTON_WIDTH,
      Cell: (cell) => (
        <IconButton
          id={cell.row.key}
          icon='/images/svg/view.svg'
          title="View Entity"
          size="20px"
          onClick={() => this.props.onClick(cell.row)}
          square
        />
      )
    });

    return columns;
  }

  createData(columns, data) {
    var hits = data.hits;

    var tabledata;

    if (hits != null) {

      tabledata = hits;

      // Add additional column data
      tabledata.forEach(function (hit) {
        for (var ii=2; ii<columns.length - 1; ii++) {
            hit[columns[ii].accessor] = hit.values[ii-2];
        }
        hit.checked = false;
      });

    }

    return tabledata;
  }

}

SearchResults.defaultProps = {
  data: [],
  properties: [],
  editMode: true
}
