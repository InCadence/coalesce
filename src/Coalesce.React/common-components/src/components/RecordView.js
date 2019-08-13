import React from 'react';
import {FieldInput} from './FieldInput.js'
import { withTheme } from '@material-ui/core/styles';

export class  RecordView extends React.PureComponent {

  render() {
    const {record, definition} = this.props;

    return (
      <div>
        {
          definition.map((fd) => {
            return this.renderField(record, fd)
          })
        }
      </div>
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

export default withTheme()(RecordView);
