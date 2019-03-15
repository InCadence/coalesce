import React from 'react';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';
import Typography from '@material-ui/core/Typography';
import Toolbar from '@material-ui/core/Toolbar';

import 'react-table/react-table.css'

export class SearchResults extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      page: 0,
      rowsPerPage: 10
    }

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
  }

  handleChangePage = (event, page) => {
    this.setState(() => {return { page }});
  };

  handleChangeRowsPerPage = event => {
    this.setState(() => {return { rowsPerPage: event.target.value }});
  };

  render() {

    const {data, properties} = this.props;
    const {page, rowsPerPage} = this.state;

    return (

      <div>
        <Toolbar>
          <Typography variant="h6">
            {this.props.title}
          </Typography>
        </Toolbar>
        <Table>
          <TableHead>
            <TableRow>
              {properties && properties.map((item) => (
                <TableCell>{item.Header}</TableCell>
              ))}
              {this.props.createButtons &&
                <TableCell>Options</TableCell>
              }
            </TableRow>
          </TableHead>
          <TableBody>
            {data && data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(hit => (
              <TableRow key={hit.entityKey}>
                {properties.map((item) => {
                  if (Array.isArray(hit[item.accessor])) {
                    return (
                      <TableCell>{hit[item.accessor][item.index]}</TableCell>
                    )
                  } else {
                    return (
                      <TableCell>{hit[item.accessor]}</TableCell>
                    )
                  }
                })}
                {this.props.createButtons &&
                <TableCell align="right">
                  <div className="form-buttons">
                    {this.props.createButtons({_original: hit})}
                  </div>
                </TableCell>
                }
              </TableRow>
            ))}
          </TableBody>
        </Table>
        <TablePagination
          rowsPerPageOptions={[5, 10, 25]}
          component="div"
          count={data.length}
          rowsPerPage={rowsPerPage}
          page={page}

          backIconButtonProps={{
            'aria-label': 'Previous Page',
          }}
          nextIconButtonProps={{
            'aria-label': 'Next Page',
          }}
          onChangePage={this.handleChangePage}
          onChangeRowsPerPage={this.handleChangeRowsPerPage}

        />
      </div>
    )
  }

}

SearchResults.defaultProps = {
  data: [],
  properties: []
}
