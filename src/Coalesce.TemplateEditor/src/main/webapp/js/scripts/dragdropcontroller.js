var app = angular.module( 'myApp', [] );

app.run( function ( $rootScope , $http) {

	// Checks if the browser is supported
	if ( !mxClient.isBrowserSupported() ) {
		// Displays an error message if the browser is not supported.
		mxUtils.error( 'Browser is not supported!', 200, false );
	} else {

		var container = $( "#graphbox" )[ 0 ];

		// create a div element for the navigator window for the jstree
		var content = document.createElement( 'div' );
		
		$( content ).attr('id','navtree');
		
		$( content ).jstree({
		    "core": {
		        "check_callback": true
		      },
		      "plugins" : [ "contextmenu" ]
		    });
		
		$( content ).on("select_node.jstree", function (e, data) { alert("node_id: " + data.node.id); });
		 
		 console.log(content);
		
		mxEvent.disableContextMenu( content );

		var wnd = new mxWindow( 'List', content, 0, 0, 150, 600, true, true );

		wnd.setMaximizable( true );
		wnd.setResizable( true );
		wnd.setVisible( true );

		$rootScope.navigator = content;
		$rootScope.navigatorWnd = wnd;

		// // Disables the built-in context menu
		mxEvent.disableContextMenu( container );
		//
		// // Creates the graph inside the given container
		$rootScope.graph = new mxGraph( container );
		$rootScope.graph.setPanning( true );
		$rootScope.graph.setConnectable( true );
		//
		// // Enables rubberband selection
		new mxRubberband( $rootScope.graph );
		//
		// Gets the default parent for inserting new cells. This
		// is normally the first child of the root (ie. layer 0).
		var parent = $rootScope.graph.getDefaultParent();

		// Enables crisp rendering of rectangles in SVG
		mxConstants.ENTITY_SEGMENT = 20;

		// Creates the graph inside the given container
		// $rootScope.graph = new mxGraph( container );
		$rootScope.graph.setDropEnabled( true );

		// Disables global features
		$rootScope.graph.collapseToPreferredSize = false;
		$rootScope.graph.constrainChildren = false;
		$rootScope.graph.cellsSelectable = false;
		$rootScope.graph.extendParentsOnAdd = false;
		$rootScope.graph.extendParents = false;
		$rootScope.graph.border = 10;

		// Resizes the container
		$rootScope.graph.setResizeContainer( false );

		$rootScope.graph.popupMenuHandler.factoryMethod = function ( menu, cell, evt ) {
			return createPopupMenu( $rootScope.graph, menu, cell, evt );
		};

		$rootScope.graph.addListener( mxEvent.CELLS_REMOVED, function ( sender, evt ) {

			var cells = evt.properties;

			var cells = evt.properties.cells

			for ( var i = 0; i < cells.length; i++ ) {
				var cellid = cells[i].id;

				$($rootScope.navigatorWnd.content).find('#'+cellid).remove();

			}

		} );

		$rootScope.graph.addListener( mxEvent.LABEL_CHANGED, function ( sender, evt ) {


			var cellid = evt.properties.cell.id;
			var cellValue = evt.properties.cell.value

			var coalesceCell = new CoalesceCell(evt.properties.cell);
			coalesceCell.getCoalesceObj().setTemplateName(cellValue);
			
			var node = $( $rootScope.navigatorWnd.content ).jstree('get_node',cellid);
			
			$( $rootScope.navigatorWnd.content ).jstree('rename_node', node , cellValue );

		} );

		document.body.appendChild( mxUtils.button( '+', function () {
			$rootScope.graph.zoomIn();
		} ) );
		document.body.appendChild( mxUtils.button( '-', function () {
			$rootScope.graph.zoomOut();
		} ) );

		$rootScope.currentMousePos = {
			x : -1,
			y : -1
		};
		$( document ).mousemove( function ( event ) {
			$rootScope.currentMousePos.x = event.pageX;
			$rootScope.currentMousePos.y = event.pageY;
		} );

	}

	// Function to create the entries in the popupmenu
	function createPopupMenu ( graph, menu, cell, evt ) {
		if ( cell != null ) {

			menu.addItem( 'Delete', '', function () {

				graph.getModel().beginUpdate();
				try {

					graph.cellsRemoved( [
						cell
					] );

				} finally {
					// Updates the display
					graph.getModel().endUpdate();
				}

			} );

			menu.addItem( 'Edit', '', function () {

				if ( cell.isEditorOpen == true ) {

					mxUtils.alert( 'TemplateEditor is already open for this template' );

				} else {

					createEditWindow( cell );

				}

			} );

			 menu.addSeparator();

			 menu.addItem( 'Download', '', function () {


			 url = 'http://localhost:8080/template-creator/data/download/' + JSON.stringify(cell.coalesceObj);
			 window.open(url);
			 } );
		} else {
			menu.addItem( 'Create New Template', '', function () {
				createNewTemplate( $rootScope.currentMousePos.x, $rootScope.currentMousePos.y );

				function createNewTemplate ( x, y ) {

					var graph = $rootScope.graph;

					graph.getModel().beginUpdate();
					try {

						var parent = graph.getDefaultParent();

						var entityCell = graph.insertVertex( parent, null, "New Template", x-140/2, y-30/2, 140, 30 );

						var coalesceCell = new CoalesceCell(entityCell);

						coalesceCell.setCoalesceObj(new CoalesceEntityTemplate("New Template",[]));

						coalesceCell.setEditorOpen(false);

						// update nav
						var container = $rootScope.navigator;

						var cellID = entityCell.id;
						
						$( container ).jstree('create_node',"#",{
						    "id": cellID,
						    "text": "New Template"
						  },"last");

					} finally {
						// Updates the display
						graph.getModel().endUpdate();
					}

				};

			} );
		}
	}
	;

	function createEditWindow ( cell ) {

		// Gets the default parent for inserting new cells. This
		// is normally the first child of the root (ie. layer
		// 0).
		var coalesceCell = new CoalesceCell(cell);

		var coalesceEntityTemplate = new CoalesceEntityTemplate();
		coalesceEntityTemplate.initialize(coalesceCell.getCoalesceObj());

		var entityName = coalesceEntityTemplate.getTemplateName();

		var content = document.createElement( 'div' );

		$( content ).addClass( 'editor' );

		console.log( $( content )[ 0 ] );

		mxEvent.disableContextMenu( content );

		// setup
		// Enables crisp rendering of rectangles in SVG
		mxConstants.ENTITY_SEGMENT = 20;

		var graph = new mxGraph( content );

		var parent = graph.getDefaultParent();

		var wndY = $rootScope.currentMousePos.y - 250/2;

		var wndX = $rootScope.currentMousePos.x - 250/2;

		var wnd = new mxWindow( 'Editing ' + entityName, content, wndX, wndY, 250, 250, true, true );

		// Creates the graph inside the given container
		// $rootScope.graph = new mxGraph( container );
		graph.setDropEnabled( true );

		// Disables global features
		graph.collapseToPreferredSize = false;
		graph.constrainChildren = false;
		graph.cellsSelectable = false;
		graph.extendParentsOnAdd = false;
		graph.extendParents = false;
		graph.border = 10;

		// Sets global styles
		var style = graph.getStylesheet().getDefaultEdgeStyle();
		style[ mxConstants.STYLE_EDGE ] = mxEdgeStyle.EntityRelation;
		style[ mxConstants.STYLE_ROUNDED ] = true;

		style = graph.getStylesheet().getDefaultVertexStyle();
		style[ mxConstants.STYLE_FILLCOLOR ] = '#ffffff';
		style[ mxConstants.STYLE_SHAPE ] = 'swimlane';
		style[ mxConstants.STYLE_STARTSIZE ] = 30;

		style = [];
		style[ mxConstants.STYLE_SHAPE ] = mxConstants.SHAPE_RECTANGLE;
		style[ mxConstants.STYLE_STROKECOLOR ] = 'none';
		style[ mxConstants.STYLE_FILLCOLOR ] = 'none';
		style[ mxConstants.STYLE_FOLDABLE ] = false;
		graph.getStylesheet().putCellStyle( 'column', style );

		// Installs auto layout for all levels
		var layout = new mxStackLayout( graph, true );
		layout.border = graph.border;
		var layoutMgr = new mxLayoutManager( graph );
		layoutMgr.getLayout = function ( cell ) {
			if ( !cell.collapsed ) {
				if ( cell.parent != graph.model.root ) {
					layout.resizeParent = true;
					layout.horizontal = false;
					layout.resizeParentMax = true;
					// layout.fill = true;
					layout.spacing = 5;
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
		graph.setResizeContainer( false );

		graph.popupMenuHandler.factoryMethod = function ( menu, cell, evt ) {
			return createPopupEditMenu( graph, menu, cell, evt );
		};

		// Adds cells to the model
		graph.getModel().beginUpdate();
		try {

			var col1 = graph.insertVertex( parent, null, '', 0, 0, 160, 0, 'column' );

			if(entityName == null){
				entityName = 'New Template';
			}

			var entityCell = graph.insertVertex( col1, null, entityName, 0, 0, 200, 30 );
			entityCell.collapsed = true;
			entityCell.coalesceType = CoalesceObjectType.ENTITY;

			// $( xmlDoc ).find( 'entity' ).find( 'linkagesection' ).remove();

			var sections = coalesceEntityTemplate.getSections();

			for (i = 0; i < sections.length; i++) {

				var coalesceSection = new CoalesceSection(null,null);
				coalesceSection.initialize(sections[i]);

				var sectionName = coalesceSection.getSectionName();

				var sectionCell = graph.insertVertex( entityCell, null, sectionName, 0, 0, 180, 30 );
				sectionCell.collapsed = true;
				sectionCell.coalesceType = CoalesceObjectType.SECTION;

				var recordsets = coalesceSection.getRecordsets();

				for (j = 0; j < recordsets.length; j++) {

					var coalesceRecordSet = new CoalesceRecordSet();
					coalesceRecordSet.initialize(recordsets[j]);

					var recordsetName = coalesceRecordSet.getRecordsetName();

					var recordsetCell = graph.insertVertex( sectionCell, null, recordsetName, 0, 0, 160, 30 );
					recordsetCell.collapsed = true;
					recordsetCell.coalesceType = CoalesceObjectType.RECORDSET;

					var fields = coalesceRecordSet.getFields();

					for (k = 0; k < fields.length; k++) {

						var coalesceField = new CoalesceField();
						coalesceField.initialize(fields[k]);

						var name = coalesceField.getFieldName();
						var fieldtype = coalesceField.getFieldType();

						var value = name + ":" + fieldtype;

						var fieldCell = graph.insertVertex( recordsetCell, null, value, 0, 0, 140, 30 );

						fieldCell.fieldType = fieldtype;
					}

			}
		}

		} finally {
			// Updates the display
			graph.getModel().endUpdate();
		}

		wnd.setMaximizable( true );
		wnd.setScrollable( true );
		wnd.setResizable( true );
		wnd.setVisible( true );
		wnd.setClosable( true );
		cell.isEditorOpen = true;

		wnd.addListener( mxEvent.CLOSE, function ( sender, evt ) {

			console.log( "window closed!" );
			cell.isEditorOpen = false;

			coalesceCell.setCoalesceObj(new coalesceEditCellToCoalesceObj(entityCell));

			graph.getModel().beginUpdate();
			try {

			coalesceCell.value = entityCell.getValue();

			cell.value = entityCell.getValue();

			graph.fireEvent(new mxEventObject(mxEvent.LABEL_CHANGED));

			var cellid = cell.id;
			
            var node = $( $rootScope.navigatorWnd.content ).jstree('get_node',cellid);
			
			$( $rootScope.navigatorWnd.content ).jstree('rename_node', node , cell.value );
			
			} finally {
				// Updates the display
				graph.getModel().endUpdate();
			}

		} );

	}

} );



function createPopupEditMenu ( graph, menu, cell, evt ) {
	if ( cell != null ) {

		menu.addItem( 'Delete', '', function () {
			graph.cellsRemoved( [
				cell
			] );

		} );

		switch ( cell.coalesceType ) {
			case CoalesceObjectType.ENTITY:
				menu.addItem( 'Add Section', '', function () {
					addCoalesceCell( graph, cell, 'New Section', CoalesceObjectType.SECTION,180,30 );
				} );
				break;
			case CoalesceObjectType.SECTION:
				menu.addItem( 'Add RecordSet', '', function () {
					addCoalesceCell( graph, cell, 'New RecordSet', CoalesceObjectType.RECORDSET, 160,30 );
				} );
				break;
			case CoalesceObjectType.RECORDSET:
				menu.addItem( 'Add Field Definition', '', function () {
					var newField = addCoalesceCell( graph, cell, "new field:string", CoalesceObjectType.FIELD_DEF, 140,30 );
					newField.fieldType=CoalesceFieldType.STRING_TYPE;
					
				} );
				break;
			case CoalesceObjectType.FIELD_DEF:
				break;
			default:
				// do nothing
		}

	} else {
		// menu.addItem( 'Create New Template', '', function () {
		// createNewTemplate( graph , $rootScope.currentMousePos.x,$rootScope.currentMousePos.y );
		// } );
	}
	// menu.addSeparator();
	//
	// menu.addItem( 'Download', '', function () {
	// mxUtils.alert( '');
	// } );

};

function addCoalesceCell ( graph, cell, name, type, length, width ) {

	graph.getModel().beginUpdate();
	try {

		var newCoalesceCell = graph.insertVertex( cell, null, name, 0, 0, length, width );
		newCoalesceCell.coalesceType = type;
	} finally {
		// Updates the display
		graph.getModel().endUpdate();
	}
	
	return newCoalesceCell;
};


function coalesceEditCellToCoalesceObj(cell){
	var templateName = cell.getValue();

	var coalesceEntityTemplate = new CoalesceEntityTemplate(templateName,[]);

	var sectionCells = cell.children;

	if(sectionCells != null){
		for(i = 0; i < sectionCells.length; i++)  {

			// sections
			var sectionName = sectionCells[i].getValue();

			var section = new CoalesceSection(sectionName, []);

			var recordsetCells = sectionCells[i].children;

			if(recordsetCells !=null){

				for(j = 0; j < recordsetCells.length; j++) {
				// recordsets
					var recordsetName = recordsetCells[j].getValue();
					var recordset = new CoalesceRecordSet(recordsetName, []);

					var fieldDefCells = recordsetCells[j].children;

					if(fieldDefCells != null){
						for(k = 0; k < fieldDefCells.length; k++) {
							// fields
								var coalesceEditCell = new CoalesceEditCell(fieldDefCells[k]);

								var name = coalesceEditCell.getCoalesceName().split(":")[0];
								var datatype = coalesceEditCell.getCoalesceName().split(":")[1];

								var field = new CoalesceField(name, datatype)

								recordset.addField(field);
							}
					}
				section.addRecordset(recordset);
				}
			}
				coalesceEntityTemplate.addSection(section)
		}
	}

	return coalesceEntityTemplate;
}

function xmlDocToCoalesceObj(xmlDoc){

// we dont need the Linkage Section
$( xmlDoc ).find( 'entity' ).find( 'linkagesection' ).remove();

var templateName = $( xmlDoc ).find( 'entity' ).attr( "name" );

var coalesceEntityTemplate = new CoalesceEntityTemplate(templateName,[]);

if ( $( xmlDoc ).find( 'entity' ).find( 'section' ) != null ) {

	$( xmlDoc ).find( 'entity' ).children().each( function () {

		// sections
		var sectionName = $( this ).attr( "name" );
		var section = new CoalesceSection(sectionName, []);

		$( this ).children().each( function () {
			// recordsets
			var recordsetName = $( this ).attr( "name" );
			var recordset = new CoalesceRecordSet(recordsetName, []);

			$( this ).children().each( function () {
				// fields
				var name = $( this ).attr( "name" );
				var datatype = $( this ).attr( "datatype" );

				var field = new CoalesceField(name, datatype)

				recordset.addField(field);
			} )
			section.addRecordset(recordset);
		} )
			coalesceEntityTemplate.addSection(section)
	} )

}

return coalesceEntityTemplate;
}

function coalesceObjToXmlDoc(obj){


}

app.controller( 'draganddrop', function ( $scope, $rootScope ) {

	// getElementById
	function $id ( id ) {
		return document.getElementById( id );
	}

	// output information
	function Output ( msg ) {
		var m = $id( "messages" );
		m.innerHTML = msg + m.innerHTML;
	}

	// file drag hover
	function FileDragHover ( e ) {
		e.stopPropagation();
		e.preventDefault();
		e.target.className = ( e.type == "dragover" ? "hover" : "" );
	}

	// file selection
	function FileSelectHandler ( e ) {

		// cancel event and hover styling
		FileDragHover( e );

		// fetch FileList object
		var files = e.target.files || e.dataTransfer.files;

		// process all File objects
		for ( var i = 0, f; f = files[ i ]; i++ ) {
			ParseFile( f );
		}

	}

	// output file information
	function ParseFile ( file ) {

		var reader = new FileReader();

		reader.onload = function ( e ) {
			// console.log( e.target.result )

			createEntityNode( e.target.result )

		}
		reader.readAsText( file );

	}

	function createEntityNode ( xml ) {

		// add a single node to graph
		var graph = $rootScope.graph;

		var parent = graph.getDefaultParent();

		var parser = new DOMParser();

		var xmlDoc = parser.parseFromString( xml, "text/xml" );

		var entityName = $( xmlDoc ).find( 'entity' ).attr( "name" );

		// Adds cells to the model

		graph.getModel().beginUpdate();
		try {

			var col1 = graph.insertVertex( parent, null, '', 0, 0, 160, 0, 'column' );

			var cellY = $rootScope.currentMousePos.y

			var cellX = $rootScope.currentMousePos.x

			var entityCell = graph.insertVertex( parent, null, entityName, cellX - 140/2, cellY - 30/2, 140, 30 );

			var coalesceCell = new CoalesceCell(entityCell);

			coalesceCell.setCoalesceObj(new xmlDocToCoalesceObj(xmlDoc));

			coalesceCell.setEditorOpen(false);

			// update the nav tree
			addEntityToNav(entityCell, xmlDoc );

		} finally {
			// Updates the display
			graph.getModel().endUpdate();
		}

	}

	function addEntityToNav (cell, xmlDoc ) {
		var container = $rootScope.navigator;

		var div = $( container );

		var cellID = cell.id;

		var entityName = $( xmlDoc ).find( 'entity' ).attr( "name" );
		
		$( '#navtree' ).jstree('create_node',"#",{
		    "id": cellID,
		    "text": entityName
		  },"last");
		


	}

	// initialize
	function Init () {

		var filedrag = $id( "graphbox" );

		// is XHR2 available?
		var xhr = new XMLHttpRequest();
		if ( xhr.upload ) {

			// file drop
			filedrag.addEventListener( "dragover", FileDragHover, false );
			filedrag.addEventListener( "dragleave", FileDragHover, false );
			filedrag.addEventListener( "drop", FileSelectHandler, false );
			filedrag.style.display = "block";

		}

	}

	// call initialization file if browser supports
	if ( window.File && window.FileList && window.FileReader ) {
		Init();
	}

} );
