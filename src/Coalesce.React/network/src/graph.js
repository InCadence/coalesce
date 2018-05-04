import * as React from "react";
import { Graph } from 'react-d3-graph';
import Paper from 'material-ui/Paper';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import { DialogMessage } from 'common-components/lib/components/dialogs'
import { loadJSON } from 'common-components/lib/js/propertyController'
import getMuiTheme from 'material-ui/styles/getMuiTheme';

export class GraphView extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;
  }

  handleResize(that) {
    const {config} = this.state;

    config.width = window.innerWidth-50;
    config.height = window.innerHeight-150;

    this.setState({
      config: config
    })
  }

  componentDidMount() {

    const that = this;
    this.handleResize(this);
    window.addEventListener("resize", this.handleResize.bind(this));

    loadJSON('theme').then((value) => {
      that.setState({
        theme: getMuiTheme(value)
      })
    }).catch((err) => {
      console.log("Loading Theme: " + err);
    })
  }

  componentWillUnmount() {
    window.removeEventListener("resize", this.handleResize.bind(this));
  }

  componentWillUpdate() {

  }

  componentDidUpdate() {
    //console.log('updated');
  }

  // Graph event callbacks
  onClickNode = function(nodeId) {

    const {data} = this.state;
    const that = this;

    data.nodes.forEach(function (node) {
      if (node.id === nodeId) {
        that.setState({selected: node});
      }
    })

  };

  onMouseOverNode = function(nodeId) {
      // window.alert('Mouse over node', nodeId);
  };

  onMouseOutNode = function(nodeId) {
      // window.alert('Mouse out node', nodeId);
  };

  onClickLink = function(source, target) {
       //window.alert(`Clicked link between ${source} and ${target}`);
  };

  render() {

    const {data, selected} = this.state;

    return (
      <MuiThemeProvider muiTheme={this.state.theme}>
        <Paper zDepth={1} style={{padding: '5px', margin: '10px'}}>
          <Graph
               id='graph-id' // id is mandatory, if no id is defined rd3g will throw an error
               data={data}
               config={this.state.config}
               onClickNode={this.onClickNode.bind(this)}
               onClickLink={this.onClickLink}
               onMouseOverNode={this.onMouseOverNode}
               onMouseOutNode={this.onMouseOutNode} />
             <DialogMessage
               title="Details"
               opened={selected != null}
               message={selected != null ? selected.ips : ""}
               onClose={() => {this.setState({selected: null})}}
             />
          </Paper>
      </MuiThemeProvider>
    )
  }

}

GraphView.defaultProps = {
  config: {
      staticGraph: true,
      highlightBehavior: true,
      node: {
          color: '#3d3d3c',
          size: 1000,
          highlightStrokeColor: '#FF9900',
          highlightOpacity: 0.8,
          highlightFontSize: 20,
          highlightFontWeight: 'bold',
          labelProperty: 'label',
          fontSize: 12,

      },
      link: {
          highlightColor: '#FF9900',
          color: '#000000'
      }
  }
}
