import React from 'react';
import Dialog from 'material-ui/Dialog';
import { HashLoader } from 'react-spinners';

/**
 * Dialog to display error messages.
 */
export class DialogLoader extends React.PureComponent {

  render() {
    return (
        <Dialog
          title={this.props.title}
          modal={true}
          open={this.props.opened}
        >
          <center>
          <HashLoader
            color={'#FF9900'}
            loading={this.props.opened}
          />
          </center>
        </Dialog>
    );
  }
}
