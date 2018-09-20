import React from 'react';
import ReactTable from 'react-table'
import IconButton from 'common-components/lib/components/IconButton'
import FieldInput from 'common-components/lib/components/FieldInput'
import { deleteEntities } from 'common-components/lib/js/entityController'
import { DialogMessage } from 'common-components/lib/components/dialogs'
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelActions from '@material-ui/core/ExpansionPanelActions';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';

export class SearchResults extends React.Component {

  constructor(props) {
    super(props);

    this.handleDelete = this.handleDelete.bind(this);
    this.handleDeleteConfirmed = this.handleDeleteConfirmed.bind(this);
    this.handleDeleteCancel = this.handleDeleteCancel.bind(this);

    this.handleDownload = this.handleDownload.bind(this);
    this.handleCheckAll = this.handleCheckAll.bind(this);

    var columns = this.createColumns(this.props.properties);

    this.state = {
        editMode: this.props.editMode,
        selectAll: false,
        columns: columns,
        tabledata: this.createData(columns, this.props.data)
      }

  }

  componentWillReceiveProps(nextProps) {

    if (nextProps.data.key !== this.props.data.key) {
      var columns = this.createColumns(nextProps.properties);

      this.setState(() => {return {
          editMode: nextProps.editMode,
          selectAll: false,
          columns: columns,
          tabledata: this.createData(columns, nextProps.data)
        }
      })
    }
  }

  render() {

    const {editMode, columns, tabledata} = this.state;

    columns[0].show = editMode;

    return (
      <ExpansionPanel  defaultExpanded>
         <ExpansionPanelSummary style={{padding: '5px', height: '32px'}} expandIcon={<ExpandMoreIcon />}>
           <Typography variant="headline">
             Query Results
           </Typography>
         </ExpansionPanelSummary>
         <ReactTable
          className="ui-widget-content"
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
    this.props.handleError("(Coming Soon!!!) This will allow you to download search results.");
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
    const {tabledata} = this.state;

    if (tabledata) {
      tabledata.forEach(function (hit) {
        hit.checked = value
      })

      this.setState(() => {return {tabledata: tabledata}} )
    }
  }

  createColumns(properties) {
    var columns = [
      {
        // Add CheckBox Column
        Header: (<FieldInput field={{}} dataType="BOOLEAN_TYPE" attr="checked" showLabels={false} onChange={this.handleCheckAll}/>),
        accessor: 'select',
        resizable: false,
        sortable: false,
        width: 34,
        Cell: (cell) => (
          <FieldInput field={cell.original} dataType="BOOLEAN_TYPE" attr="checked" showLabels={false}/>
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
      width: 34,
      Cell: (cell) => (
        <IconButton
          id={cell.row.key}
          icon='/images/svg/view.svg'
          title="View Entity"
          size="20px"
          onClick={() => window.open(`${this.props.url}/entityeditor/?entitykey=${cell.row.entityKey}`)}
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
        for (var ii=2; ii<columns.length; ii++) {
            hit[columns[ii].accessor] = hit.values[ii-2];
        }
        hit.checked = false;
      });

    }

    return tabledata;
  }

}

SearchResults.defaultProps = {
  url: 'http://' + window.location.hostname + ':' + window.location.port,
  data: [],
  properties: [],
  editMode: true
}
