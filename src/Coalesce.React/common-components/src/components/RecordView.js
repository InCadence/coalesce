import React from 'react';
import { withTheme } from '@material-ui/core/styles';

import {FieldInput} from 'coalesce-components/lib/components'

export class RecordView extends React.PureComponent {

  render() {
    const {record, definition} = this.props;

    return (
      <React.Fragment>
        {
          definition.map((fd) => {
            return this.renderField(record, fd)
          })
        }
      </React.Fragment>
    );
  }

  renderField(record, fd) {

    if (record.hasOwnProperty(fd.name))
    {
      return (
        <FieldInput
          key={fd.name}
          field={record}
          attr={fd.name}
          isNullable
          label={fd.label}
          hint={fd.description ? fd.description : undefined}
          dataType={fd.dataType}
        />
      )
    }

  }

}

export default withTheme(RecordView);
