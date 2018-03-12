import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import registerServiceWorker from './registerServiceWorker';
import {Menu} from 'common-components/lib/index.js'

import 'common-components/bootstrap/css/bootstrap.min.css'
import 'common-components/css/coalesce.css'
import './index.css';

var pjson = require('../package.json');
document.title = pjson.title;

ReactDOM.render(<App />, document.getElementById('main'));

registerServiceWorker();
