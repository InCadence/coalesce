import React from 'react';
import ReactTable from 'react-table'
import IconButton from 'common-components/lib/components/IconButton'
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelActions from '@material-ui/core/ExpansionPanelActions';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';

export class SearchResults extends React.PureComponent {

  constructor(props) {
    super(props);

    this.handleDelete = this.handleDelete.bind(this);
    this.handleDownload = this.handleDownload.bind(this);
  }

  render() {

    const {data, properties} = this.props;

    var hits = data.hits;

    // Always show entity key
    var columns = [
      {
        Header: 'Key',
        accessor: 'entityKey'
      }
    ];

    // Add additional columns
    properties.forEach(function (property) {
      var parts = property.split(".");

      columns.push({
        Header: parts[1],
        accessor: parts[1]
      })
    });

    //window.open(this.props.url + "/entityeditor/?entitykey=" + cell.row.entityKey, '_blank')

    // Add button for viewing entity
    columns.push({
      Header: '',
      accessor: 'button',
      Cell: (cell) => (
        <div className="form-buttons">
          <IconButton icon="/images/svg/view.svg" size="24px" title="View" onClick={() => window.open(this.props.url + "/entityeditor/?entitykey=" + cell.row.entityKey)} />
        </div>
      )
    });

    var tabledata;

    if (hits != null) {

      tabledata = hits;

      // Add additional column data
      tabledata.forEach(function (hit) {
        for (var ii=1; ii<columns.length - 1; ii++) {
            hit[columns[ii].accessor] = hit.values[ii-1];
        }
      });

    }

    return (
      <ExpansionPanel  defaultExpanded>
         <ExpansionPanelSummary style={{padding: '5px', height: '32px'}} expandIcon={<ExpandMoreIcon />}>
           <Typography variant="headline">
             Results
           </Typography>
         </ExpansionPanelSummary>
         <div className="ui-widget-content">
          <ReactTable
            data={tabledata}
            columns={columns}
          />
        </div>
        <Divider />
        <ExpansionPanelActions  style={{padding: '5px', float: 'right'}}>
          <IconButton icon="/images/svg/download.svg" title="Download Results" onClick={this.handleDownload} />
          <IconButton icon="/images/svg/delete.svg" title="Delete Results" onClick={this.handleDelete} />
        </ExpansionPanelActions>
     </ExpansionPanel>
     )

  }

  handleDownload() {
    this.props.handleError("(Coming Soon!!!) This will allow you to download search results.");
  }

  handleDelete() {
    this.props.handleError("(Coming Soon!!!) This will allow you to mark search results as deleted.");
  }

}

SearchResults.defaultProps = {
  url: 'http://' + window.location.hostname + ':' + window.location.port,
  data: [],
  properties: []
}
