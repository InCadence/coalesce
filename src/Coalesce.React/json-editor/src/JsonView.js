import * as React from "react";
import ReactJson from 'react-json-view'

export default class JsonView extends React.Component {

  constructor(props) {
    super(props);

    this.state = {data: props.data};

    this.onEdit = this.onEdit.bind(this);
    this.onAdd = this.onAdd.bind(this);
    this.onDelete = this.onDelete.bind(this);
  }

  componentWillReceiveProps(nextProps) {
    this.setState({data: nextProps.data});
  }

  onEdit(update)
  {
    this.props.onChange(update.updated_src)
  }

  onAdd(update)
  {
    const { data } = this.state;
    update.updated_src = data;
    var pointer=data;

    for (var ii=0; ii<update.namespace.length; ii++)
    {
        pointer = pointer[update.namespace[ii]];
    }

    if (Array.isArray(pointer[update.name]))
    {
      if (update.existing_value.length > 0)
      {
        pointer[update.name].push(this.cloneKeys(update.existing_value[0]));
      }
      else
      {
        pointer[update.name].push(null);
      }
    }
    else
    {
      pointer[update.name] = update.new_value;
    }

    this.props.onChange(update.updated_src)

  }

  cloneKeys(obj)
  {
    var newObj={};

    switch (typeof(obj)) {
      case 'object':
        for (var key in obj)
        {
          if (Array.isArray(obj[key]))
          {
            newObj[key]=[this.cloneKeys(obj[key][0])];
          }
          else
          {
            newObj[key]='';
          }
        }
        break;
      case 'string':
      default:
        newObj='';
        break;
    }

    return newObj;
  }

  onDelete(update)
  {
    this.props.onChange(update.updated_src)
  }

  render() {

    const { data } = this.state;

    return (
      <ReactJson src={data} collapsed='2' onEdit={this.onEdit} onAdd={this.onAdd} onDelete={this.onDelete} iconStyle="square"/>
    )
  }

}
