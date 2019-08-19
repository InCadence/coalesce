import React from "react";
import ReactTable from "react-table";

import { DialogMessage } from "coalesce-components/lib/components/dialogs";

import AppBar from "@material-ui/core/AppBar";
import Tabs from "@material-ui/core/Tabs";
import Tab from "@material-ui/core/Tab";

export class DialogFieldLegend extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      tabIndex: 0
    };

    this.handleTabChange = this.handleTabChange.bind(this);
  }

  handleTabChange(event, tabIndex) {
    this.setState(() => {
      return {tabIndex: tabIndex};
    });
  }

  render() {
    const {tabIndex} = this.state;

    return (
      <DialogMessage
        title={this.props.title}
        opened={true}
        maxWidth="lg"
        message={
          <div>
            <AppBar position="static">
              <Tabs
                variant="fullWidth"
                value={tabIndex}
                onChange={this.handleTabChange}
              >
                {this.props.data.map(recordset => 
                  <Tab key={recordset.name} label={recordset.name} />
                )}
              </Tabs>
            </AppBar>

            <ReactTable
              data={this.props.data[tabIndex].definition}
              filterable
              columns={[
                {
                  Header: "Field",
                  id: "name",
                  maxWidth: 200,
                  accessor: "name"
                },
                {
                  Header: "Data Type",
                  id: "dataType",
                  maxWidth: 200,
                  accessor: "dataType"
                },
                {
                  Header: "Label",
                  id: "label",
                  maxWidth: 200,
                  accessor: "label"
                },
                {
                  Header: "Description",
                  id: "description",
                  accessor: "description"
                }
              ]}
              defaultSorted={[
                {
                  id: "url",
                  desc: false
                }
              ]}
              showPageSizeOptions={false}
              defaultPageSize={10}
              minRows={10}
              className="-striped -highlight"
            />
          </div>
        }
        onClose={this.props.onClose}
      />
    );
  }
}
