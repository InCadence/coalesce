import React, { Component } from 'react';
import mx from 'mxgraph-js';
import { Panel } from 'react-bootstrap';

class TemplateGraph extends Component {
	constructor(props) {
		super(props);
		this.state = { container: props.graphContainer };
	}

	componentDidMount() {

		// Checks if the browser is supported
		if (!mx.mxClient.isBrowserSupported()) {
			// Displays an error message if the browser is not supported.
			mx.mxUtils.error('Browser is not supported!', 200, false);
		}
		else {

			var container = document.getElementById('graphContainer');

			// Disables the built-in context menu

			mx.mxEvent.disableContextMenu(container);

			// Creates the graph inside the given container
			var graph = new mx.mxGraph(container);

			// Enables rubberband selection
			new mx.mxRubberband(graph);

			// Gets the default parent for inserting new cells. This
			// is normally the first child of the root (ie. layer 0).
			var parent = graph.getDefaultParent();

			// Adds cells to the model in a single step
			graph.getModel().beginUpdate();
			try {
				var v1 = graph.insertVertex(parent, null, 'Hello,', 20, 20, 80, 30);
				var v2 = graph.insertVertex(parent, null, 'World!', 200, 150, 80, 30);
				var e1 = graph.insertEdge(parent, null, '', v1, v2);
			}
			finally {
				// Updates the display
				graph.getModel().endUpdate();
			}
		}
	}

	render() {
		return (
			<Panel>
			<div id="graphContainer" />
			</Panel>
		);
	}
}
export default TemplateGraph;
