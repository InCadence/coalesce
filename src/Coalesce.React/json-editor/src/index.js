import * as React from "react";
import * as ReactDOM from "react-dom";
import Popup from 'react-popup';
import {registerLoader, registerErrorPrompt, registerPrompt} from 'common-components/lib/register.js'
import ReactJson from 'react-json-view'

import {Menu, IconButton} from 'common-components/lib/index.js'

import 'common-components/css/coalesce.css'
import 'common-components/css/popup.css'

var rootUrl;

if (window.location.port == 3000) {
  rootUrl  = 'http://' + window.location.hostname + ':8181';
} else {
  rootUrl  = '';
}

var pjson = require('../package.json');
document.title = pjson.title;

registerLoader(Popup);
registerErrorPrompt(Popup);
registerPrompt(Popup);

class Main extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      data: props.data
    }
  }

  onEdit(update)
  {
     this.setState({
       data: update.updated_src.data
     })
  }

  onAdd(update)
  {
    const data=this.state;
    update.updated_src.data=data.data;
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
        newObj='';
        break;
    }

    return newObj;
  }

  onDelete(update)
  {
    if (isInteger(update.name))
    {
       this.setState({
         data: update.updated_src.data
       })
    }
    else
    {
        return true;
    }
  }

  onSave() {
    saveConfiguration(this.props.name, this.state.data);
  }

  render() {
    const data=this.state;

    return (
      <div className="ui-widget">
        <div className="ui-widget-header">
          {this.props.name}
        </div>
        <div className="ui-widget-content" >
          <ReactJson src={data} collapsed='3' onEdit={this.onEdit.bind(this)} onAdd={this.onAdd.bind(this)} onDelete={this.onDelete.bind(this)} iconStyle="square"/>
          <div className="form-buttons">
            <IconButton icon="/images/svg/save.svg" title="Save Changes" onClick={this.onSave.bind(this)} />
          </div>
        </div>
      </div>
    )
  }
};


function isInteger(value)
{
  var x;
  return (x = parseInt(value), (0 | x) === x);
}

function saveConfiguration(name, data) {

  Popup.plugins().loader('Saving...');

  fetch(rootUrl + '/cxf/data/property/' + name, {
    method: "PUT",
    body: JSON.stringify(data),
    headers: new Headers({
      'content-type': 'application/json; charset=utf-8'
    }),
  }).then(res => {
      Popup.close();
  }).catch(function(error) {
      Popup.plugins().promptError("Saving: " + error);
  });
}

function loadConfiguration(name) {
  fetch(rootUrl + '/cxf/data/property/' + name)
    .then(res => res.json())
    .then(data => {
      ReactDOM.unmountComponentAtNode(document.getElementById('main'));
      ReactDOM.render(
        <Main name={name} data={data}/>,
        document.getElementById('main')
      );
  }).catch(function(error) {
    Popup.plugins().promptError("Loading Settings: " + error);
  });
}

// Default Component
ReactDOM.render(
    <Popup />,
    document.getElementById('popupContainer')
);

ReactDOM.render(
  <Menu logoSrc={pjson.icon} title={pjson.title} items={[
    {
      id: 'load',
      name: 'Load',
      img: '/images/svg/load.svg',
      title: 'Load JSON',
      onClick: () => {
        loadConfiguration('home.json');
      }
    },{
      id: 'load',
      name: 'Load',
      img: '/images/svg/spider.svg',
      title: 'Load JSON',
      onClick: () => {
        loadConfiguration('filter.json');
      }
    }
  ]}/>,
  document.getElementById('myNavbar')
);
