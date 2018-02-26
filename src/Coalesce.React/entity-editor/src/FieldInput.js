import React from 'react';

export class FieldInput extends React.PureComponent {

  constructor(props) {
    super(props);
  }

  render() {
    return this.createInput(
      this.props.field.dataType,
      this.props.field.key,
      this.props.field.value,
      this.props.onChange
    );
  }

  createInput(type, id, value, onChange) {
    console.log(type);
    switch (type) {
      case 'URI_TYPE':
      case 'STRING_TYPE':
        return (<input type="text" id={id} className="form-control" value={value}  onChange={onChange}/>);
        break;
      case 'FLOAT_TYPE':
      case 'DOUBLE_TYPE':
      case 'LONG_TYPE':
        return (<input type="number" id={id} className="form-control" value={value} step='0.01' onChange={onChange}/>);
        break;
      case 'INTEGER_TYPE':
        return (<input type="number" id={id} className="form-control" value={value}  onChange={onChange}/>);
        break;
      case 'BOOLEAN_TYPE':
        return (
          <input type="checkbox" id={id} className="form-control" value={value}  onChange={onChange}/>
        );
        break;
      case 'DATE_TIME_TYPE':
        return (<input type="datetime-local" id={id} className="form-control" value={value}  onChange={onChange}/>);
        break;
      case 'BINARY_TYPE':
      case 'FILE_TYPE':
        return (
          <div>
            <label>{value}</label>
            <img src='/images/svg/load.svg' alt="Download" title="Download" className="coalesce-img-button enabled"/>
          </div>
        );
        break;
      case 'GEOCOORDINATE_TYPE':
        return (
          <div className="row">
            <div className="form-group col-sm-2">
              <label for="{id + '_x'}" className="col-form-label">Lat</label>
              <input type="number" id={id + '_x'} className="form-control" value={value} step="0.01" />
            </div>
            <div className="form-group col-sm-2">
              <label for="{id + '_y'}" className="col-form-label">Long</label>
              <input type="number" id={id + '_y'} className="form-control" value={value} step="0.01" />
            </div>
          </div>
        )
        break;
      case 'CIRCLE_TYPE':
        return (
          <div className="row">
            <div className="form-group col-sm-2">
              <label for="{id + '_x'}" className="col-form-label">Lat</label>
              <input type="number" id={id + '_x'} className="form-control" value={value} step="0.01" />
            </div>
            <div className="form-group col-sm-2">
              <label for="{id + '_y'}" className="col-form-label">Long</label>
              <input type="number" id={id + '_y'} className="form-control" value={value} step="0.01" />
            </div>
            <div className="form-group col-sm-2">
              <label for="{id + '_radius'}" className="col-form-label">Radius</label>
              <input type="number" id={id + '_radius'} className="form-control" value={value} step="0.01"/>
            </div>
          </div>
        )
        break;
      case 'GUID_TYPE':
        return (<input type="text" id={id} className="form-control" value={value} pattern="[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}" onChange={onChange}/>);
        break;
      default:
      return (<input type="text" id={id} className="form-control" value={value}  disabled/>);
        break;
    }
  }

}
