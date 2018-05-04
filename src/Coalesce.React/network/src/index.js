import * as React from "react";
import * as ReactDOM from "react-dom";
import {GraphView} from './graph.js'

import {Menu} from 'common-components/lib/index.js'
import 'common-components/bootstrap/css/bootstrap.min.css'

import 'common-components/css/coalesce.css'

var pjson = require('../package.json');
document.title = pjson.title;

var rootUrl;

if (window.location.port == 3000) {
  rootUrl  = 'http://' + window.location.hostname + ':8181';
} else {
  rootUrl  = '';
}

function loadNetwork() {

  fetch(rootUrl + '/cxf/data/network/')
    .then(res => res.json())
    .then(data => {

      var totals = {
        'node': 0,
        'role': 0,
      };


      data.nodes.forEach(function(node) {
        if (totals[node.type] == null) {
          totals[node.type] = 1;
        } else {
          totals[node.type]++;
        }
      });

      var largest = 0;

      Object.keys(totals).forEach(function(key) {
        if (totals[key] > largest) {
          largest = totals[key];
        }
      });

      var colWidth = 200;
      var rowWidth = 100;

      var counts = {};
      var col = 1;

      Object.keys(totals).forEach(function(key) {

        counts[key] = {
          x: col++*colWidth,
          y: ((largest) / 2) - (totals[key] / 2) + 1
        }
      });

      ReactDOM.unmountComponentAtNode(document.getElementById('main'));

      data.nodes.forEach(function(node) {

        node.x=counts[node.type].x;
        node.y=counts[node.type].y++ * rowWidth;

        switch (node.type) {
          case "node":
            node.symbolType = 'square';
            node.color = '#084081';
            node.size = 800;
            node.strokeColor = '#FF9900';
            node.strokeWidth=1.5
            break;
          case "role":
            node.symbolType = 'circle';
            node.color = '#0868ac';
            node.size = 800;
            node.strokeColor = '#FF9900'
            break;
        }

      })

      ReactDOM.render(
      <GraphView data={data} title="Network" />,
        document.getElementById('main')
      );
  }).catch(function(error) {
    console.log("Loading Settings: " + error);
  });
}

loadNetwork()

ReactDOM.render(
    <Menu logoSrc={pjson.icon} title={pjson.title} items={[
      ]}/>,
    document.getElementById('myNavbar')
);
