import * as React from "react";
import { DialogOptions, DialogMessage } from 'common-components/lib/components/dialogs'
import {GraphView} from './graph.js'
import { getRootKarafUrl } from 'common-components/lib/js/common.js'
import {Menu} from 'common-components/lib/index.js'
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import { loadJSON } from 'common-components/lib/js/propertyController'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';

export class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      data: null
    };
  }

  componentDidMount() {

    const that = this;

    loadJSON('theme').then((value) => {
      that.setState({
        theme: getMuiTheme(value)
      })
    }).catch((err) => {
      console.log("Loading Theme: " + err);
    })

    this.loadBlueprint("rest-blueprint.xml");
  }

  render() {

    const { data, selected, error, theme, blueprints } = this.state;
    const that = this;

    return (
      <MuiThemeProvider muiTheme={this.state.theme}>
        <Menu logoSrc={this.props.icon} title={`${this.props.title} / ${selected}`} items={[
          {
            id: 'load',
            name: 'Load',
            img: "/images/svg/load.svg",
            title: 'Load Entity',
            onClick: () => {
              getBlueprintOptions().then(data => {

                  data.push("Network Diagram");

                  this.setState({
                    blueprints: data,
                    data: null
                  })

              }).catch(function(error) {
                that.setState({error: `Loading Blueprint: ${error}`});
              });
            }
          }]}/>
          { data != null &&
            <GraphView data={data} title={selected} theme={theme} />
          }
          <DialogMessage
            title="Error"
            opened={error != null}
            message={error}
            onClose={() => {this.setState({error: null})}}
          />
          {blueprints != null &&
            <DialogOptions
              title="Selection"
              open={data == null && this.state.error == null}
              onClose={() => {this.loadBlueprint(selected)}}
              // TODO These options should be pulled from a configuration file
              options={blueprints.map((blueprint) => {
                return {
                  key: blueprint,
                  name: blueprint,
                  onClick: () => {blueprint === 'Network Diagram' ? this.loadNetwork() : this.loadBlueprint(blueprint)}
                }
              })}

            />
          }
        </MuiThemeProvider>
    )
  }

  loadNetwork() {
    getNetwork().then((value) => {

      this.setState({
        selected: 'Network Diagram',
        data: this.formatData(value)
      })

    }).catch((err) => {
      this.setState({error: `Loading Network Diagram: ${err}`})
    })
  }

  loadBlueprint(blueprint) {

    getBlueprint(blueprint).then((value) => {

      this.setState({
        selected: blueprint,
        data: this.formatData(value)
      })

    }).catch((err) => {
      this.setState({error: `Loading ${blueprint}: ${err}`})
    })

  }

  formatData(data) {
    var totals = {
      'SERVER': 0,
      'CONTROLLER_ENDPOINT': 0,
      'ENDPOINT': 0,
      'FRAMEWORK': 0,
      'PERSISTER': 0,
      'SETTINGS': 0,
      'ENTITY': 0,
      'OTHER': 0,
      'node': 0,
      'role': 0
    };


    data.nodes.forEach(function(node) {

      // Consolidate Node Types
      if (node.type === 'CONTROLLER') {
          node.type = "ENDPOINT";
      } else if (node.type === 'CLIENT') {
        node.type = "PERSISTER";
      }

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
      if (totals[key] > 0) {
        counts[key] = {
          x: col++*colWidth,
          y: ((largest) / 2) - (totals[key] / 2) + 1
        }
      }
    });

    data.nodes.forEach(function(node) {

      node.x=counts[node.type].x;
      node.y=counts[node.type].y++ * rowWidth;

      //http://colorbrewer2.org/#type=sequential&scheme=Oranges&n=6
      switch (node.type) {
        case "SERVER":
        case "node":
          node.symbolType = 'square';
          node.color = '#084081';
          node.size = 800;
          node.strokeColor = '#FF9900';
          node.strokeWidth=1.5
          break;
        case "CONTROLLER_ENDPOINT":
          node.symbolType = 'wye';
          node.color = '#0868ac';
          node.size = 800;
          node.strokeColor = '#FF9900'
          break;
        case "ENDPOINT":
          node.symbolType = 'cross';
          node.color = '#2b8cbe';
          node.size = 800;
          node.strokeColor = '#FF9900'
          break;
        case "FRAMEWORK":
        case "role":
          node.symbolType = 'triangle';
          node.color = '#4eb3d3';
          node.size = 400;
          node.strokeColor = '#FF9900'
          break;
        case "PERSISTER":
          node.symbolType = 'star';
          node.color = '#7bccc4';
          node.size = 800;
          node.strokeColor = '#FF9900'
          break;
        case "SETTINGS":
          node.symbolType = 'circle';
          node.color = '#a8ddb5';
          node.size = 400;
          node.strokeColor = '#FF9900'
          break;
        case "ENTITY":
          node.symbolType = 'square';
          node.color = '#ccebc5';
          node.size = 100;
          node.strokeColor = '#FF9900'
          break;
        default:
          node.symbolType = 'square';
          node.color = '#ccebc5';
          node.size = 100;
          node.strokeColor = '#FF9900'
          break;
      }

    })

    return data;
  }
}

function getBlueprintOptions() {

  var karafRootAddr = getRootKarafUrl();

  return fetch(`${karafRootAddr}/blueprints`)
    .then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return res.json();
    }).catch(function(error) {
      throw Error(error);
    });
}

function getBlueprint(filename) {

  var karafRootAddr = getRootKarafUrl();

  return fetch(`${karafRootAddr}/blueprints/${filename}`)
    .then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return res.json();
    }).catch(function(error) {
      throw Error(error);
    });
}

function getNetwork() {

  var karafRootAddr = getRootKarafUrl();

  return fetch(`${karafRootAddr}/network`)
    .then(res => {
      if (!res.ok)
      {
        throw Error(res.statusText);
      }
      return res.json();
    }).catch(function(error) {
      throw Error(error);
    });
}
