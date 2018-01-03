import * as React from "react";
import { Graph } from 'react-d3-graph';
import Popup from 'react-popup';

export class GraphView extends React.Component {

  constructor(props) {
    super(props);
    this.state = props;
  }

  handleResize(that) {
    const {config} = this.state;

    config.width = window.innerWidth-50,
    config.height = window.innerHeight-150,

    this.setState({
      config: config
    })
  }

  componentDidMount() {
    this.handleResize(this);
    window.addEventListener("resize", this.handleResize.bind(this));
  }

  componentWillUnmount() {
    window.removeEventListener("resize", this.handleResize.bind(this));
  }

  componentWillUpdate() {

  }

  componentDidUpdate() {
    console.log('updated');
  }

  // Graph event callbacks
  onClickNode = function(nodeId) {

    const {data} = this.state;

    data.nodes.forEach(function (node) {
      if (node.id === nodeId) {
        Popup.plugins().nodeDetails(node);
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

    const {data, title} = this.state;

    return (
      <center>
        <div className="ui-widget">
          <div className="ui-widget-header">
          {title}
          </div>
          <div ref={graph => this.graph = graph} className="ui-widget-content">
            <Graph
               id='graph-id' // id is mandatory, if no id is defined rd3g will throw an error
               data={data}
               config={this.state.config}
               onClickNode={this.onClickNode.bind(this)}
               onClickLink={this.onClickLink}
               onMouseOverNode={this.onMouseOverNode}
               onMouseOutNode={this.onMouseOutNode} />
           </div>
         </div>
      </center>
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

Popup.registerPlugin('nodeDetails', function (node) {
  Popup.close();
  Popup.create({
      title: 'Details',
      content: node.classname,
      className: 'alert',
      buttons: {
          right: ['ok']
      }
  }, true);
});
