// Program starts here. Creates a sample graph in the
	// DOM node with the specified ID. This function is invoked
	// from the onLoad event handler of the document (see below).
	
	var graph = null;
	
	function main(container) {
		// Checks if the browser is supported
		if (!mxClient.isBrowserSupported()) {
			// Displays an error message if the browser is not supported.
			mxUtils.error('Browser is not supported!', 200, false);
		} else {
			// Enables crisp rendering of rectangles in SVG
			mxConstants.ENTITY_SEGMENT = 20;

			// Creates the graph inside the given container
		    graph = new mxGraph(container);
			graph.setDropEnabled(true);

			// Disables global features
			graph.collapseToPreferredSize = false;
			graph.constrainChildren = false;
			graph.cellsSelectable = false;
			graph.extendParentsOnAdd = false;
			graph.extendParents = false;
			graph.border = 10;

			// Sets global styles
			var style = graph.getStylesheet().getDefaultEdgeStyle();
			style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
			style[mxConstants.STYLE_ROUNDED] = true;

			style = graph.getStylesheet().getDefaultVertexStyle();
			style[mxConstants.STYLE_FILLCOLOR] = '#ffffff';
			style[mxConstants.STYLE_SHAPE] = 'swimlane';
			style[mxConstants.STYLE_STARTSIZE] = 30;

			style = [];
			style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
			style[mxConstants.STYLE_STROKECOLOR] = 'none';
			style[mxConstants.STYLE_FILLCOLOR] = 'none';
			style[mxConstants.STYLE_FOLDABLE] = false;
			graph.getStylesheet().putCellStyle('column', style);

			// Installs auto layout for all levels
			var layout = new mxStackLayout(graph, true);
			layout.border = graph.border;
			var layoutMgr = new mxLayoutManager(graph);
			layoutMgr.getLayout = function(cell) {
				if (!cell.collapsed) {
					if (cell.parent != graph.model.root) {
						layout.resizeParent = true;
						layout.horizontal = false;
						layout.spacing = 10;
					} else {
						layout.resizeParent = true;
						layout.horizontal = true;
						layout.spacing = 40;
					}

					return layout;
				}

				return null;
			};

			// Resizes the container
			graph.setResizeContainer(true);

			// Gets the default parent for inserting new cells. This
			// is normally the first child of the root (ie. layer 0).
			var parent = graph.getDefaultParent();
		}
	};