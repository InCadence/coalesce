import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'

import { loadJSON } from 'common-components/lib/js/propertyController'

import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles'; // v1.x
import { MuiThemeProvider as V0MuiThemeProvider} from 'material-ui';
import getMuiTheme from 'material-ui/styles/getMuiTheme';

import App from './app'
import 'common-components/bootstrap/css/bootstrap.min.css'
import 'common-components/css/coalesce.css'
import 'react-table/react-table.css'

var pjson = require('../package.json');
document.title = pjson.title;

const Default = ({match}) => {
  var params={};

  window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(s,k,v){
    console.log(k + '=' + v);
    params[k]=v}
  );

  if (params['entitykey'] != null) {
    return (<App
      entitykey={params['entitykey']}
      icon={pjson.icon}
      title={pjson.title}
    />);
  } else if (params['templatekey'] != null) {
    return (<App
      templatekey={params['templatekey']}
      icon={pjson.icon}
      title={pjson.title}
    />);
  } else {
    return (<App
      icon={pjson.icon}
      title={pjson.title}
    />);
  }
}

// TODO Uncomment this line for debugging.
//renderNewEntity('086f6440-997e-30a4-90a4-d134076e1587');

class Main extends React.Component {

  render() {
    return (
      <Router>
        <Switch>
          <Route component={Default}/>
        </Switch>
      </Router>
    );
  }
}

function loadApplication(theme) {
  ReactDOM.render(
    <MuiThemeProvider theme={createMuiTheme(theme)}>
      <V0MuiThemeProvider muiTheme={getMuiTheme(theme)}>
        <Main />
      </V0MuiThemeProvider>
    </MuiThemeProvider>,
    document.getElementById('main')
  );
}

loadJSON('theme').then((theme) => {
  loadApplication(createMuiTheme(theme));
}).catch((err) => {
  console.log("Loading Theme: " + err);
  loadApplication(createMuiTheme({}));
})
