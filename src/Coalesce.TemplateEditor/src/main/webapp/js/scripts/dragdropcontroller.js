var app = angular.module( 'myApp', [] );

//TODO this is the worst javascript I have ever written. Needs major refactoring...

var xLoc = null;
var yLoc = null;

app.run( function ( $rootScope , $http, graphUtils, graphHolder) {

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
		
		//$( content ).on("select_node.jstree", function (e, data) { alert("node_id: " + data.node.id); });
		 
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
		
		graphHolder.setGraph($rootScope.graph);
		
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
			xLoc = event.pageX;
			$rootScope.currentMousePos.y = event.pageY;
			yLoc = event.pageY;
		} );
		
		$("#templates").hide();

	}

	// Function to create the entries in the popupmenu
	function createPopupMenu ( graph, menu, cell, evt ) {
		if ( cell != null ) {

//			//TODO this should not be here
//			if(cell.coalesceObj.className==null){
//				cell.coalesceObj.className="com.entity.name";
//			}
//			
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

//			 menu.addSeparator();

			 menu.addItem( 'Download', '', function () {

				var className = cell.coalesceObj.className;
				
				cell.coalesceObj.className = className.replace(/\./g, '-');
				
var jsonData = JSON.stringify(cell.coalesceObj);
				 
			 url = 'http://localhost:8080/template-creator/data/download/' + jsonData;
			 window.open(url);
			 } );
			 
			 var className = cell.coalesceObj.className;
			 
			 cell.coalesceObj.className = className.replace(/-/g, '.');
			 
			 menu.addItem( 'save xml', '', function () {
				 
				 var root = "http://localhost:8181/cxf/data/";
				 
				 var cellData = JSON.stringify(cell.coalesceObj);
				 
//				 var xml =    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entity classname=\"\" datecreated=\"\" entityid=\"\" entityidtype=\"\" key=\"\" lastmodified=\"\" name=\"Student Object\" source=\"DSS\" title=\"\" version=\"1.0\">\r\n" + 
//			        "    <linkagesection datecreated=\"\" key=\"\" lastmodified=\"\" name=\"Linkages\"/>\r\n" + 
//			        "    <section datecreated=\"\" key=\"\" lastmodified=\"\" name=\"Section\">\r\n" + 
//			        "        <recordset datecreated=\"\" key=\"\" lastmodified=\"\" maxrecords=\"\" minrecords=\"\" name=\"Recordset\">\r\n" + 
//			        "            <fielddefinition datatype=\"string\" datecreated=\"\" defaultclassificationmarking=\"\" key=\"\" lastmodified=\"\" name=\"firstName\"/>\r\n" + 
//			        "            <fielddefinition datatype=\"string\" datecreated=\"\" defaultclassificationmarking=\"\" key=\"\" lastmodified=\"\" name=\"lastName\"/>\r\n" + 
//			        "            <fielddefinition datatype=\"double\" datecreated=\"\" defaultclassificationmarking=\"\" key=\"\" lastmodified=\"\" name=\"id\"/>\r\n" + 
//			        "            <fielddefinition datatype=\"integer\" datecreated=\"\" defaultclassificationmarking=\"\" key=\"\" lastmodified=\"\" name=\"grade\"/>\r\n" + 
//			        "        </recordset>\r\n" + 
//			        "    </section>\r\n" + 
//			        "</entity>";
				 
//					$.ajax({
//						type : "POST",
//						url : "http://localhost:8080/template-creator/data/template/",
//						data : JSON.stringify(cell.coalesceObj),
//						contentType : "application/json; charset=utf-8",
//						crossDomain : true
//					}).then(
//							function(xml) {
//								
								$.ajax({
									type : "POST",
									url : root + "templates",
									data : JSON.stringify(cell.coalesceObj),// now data come in this function
									contentType : "application/json; charset=utf-8",
									crossDomain : true,
									success : function(data, status, jqXHR) {
										mxUtils.alert("Template saved");
									},

									error : function(jqXHR, status) {
										// error handler
										console.log(jqXHR);
										mxUtils.alert('Template failed to save. Error: ' + status.code);
									}
								});
							 
								
//							});
			 });
		} else {
			menu.addItem( 'Create new template', '', function () {
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
			
			menu.addItem( 'Retrieve template from database', '', function () {
				
//				var root = "/template-creator/data/";
				
				var root = "http://localhost:8181/cxf/data/";
				
				var options = [{"text":"Select template", "value":""}];
				
				$.ajax({
					url : root + "templates",
				}).then(
						function(data) {

							data.forEach(function(item) {
								
								console.log(item);
								
								options.push({"text":item.name, "value":item.key});

							});

							bootbox.prompt({
							    title: "Retrieve template from database",
							    inputType: 'select',
							    inputOptions: options,
							    callback: function (result) {
							    	
							    	if(result){
							    		
								    	$.ajax({
											url : root + "templates/" + result,
										}).then(
												function(data) {
													
													var x = xLoc;
													var y =yLoc;
													
													console.log(data.coalesceObjectDocument);
													
													graphUtils.createEntityNode ( data.coalesceObjectDocument,  graphHolder.getGraph(), x, y );
													
												});
							    		
							    	}
							    }
							});
						});
				
//				bootbox.dialog({
//				    title: 'A custom dialog with dropdown',
//				    message: control
//				});
//				console.log(options);
				

			});
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
		
		var className = cell.coalesceObj.className;
		
		if(entityName == null){
			entityName = 'New Template';
		}
		
		//remove spaces for id
		var entityNameNoSpace = entityName.replace(/\s/g, '');

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
		
		$( content ).attr('id',entityNameNoSpace);
		
		$( content ).jstree({
		    "core": {
		        "check_callback": true
		      },
		      "plugins" : [ "contextmenu" ],
		      'contextmenu': {
		            'items': editMenu
		        }
		    });
					
					var entityId = $( '#' + entityNameNoSpace ).jstree('create_node',"#",{
					    "text": entityName,
					    "coalesceType": CoalesceObjectType.ENTITY,
					    "classname": className
					  },"last");
		
					var sections = coalesceEntityTemplate.getSections();
		
					for (i = 0; i < sections.length; i++) {
		
						var coalesceSection = new CoalesceSection(null,null);
						coalesceSection.initialize(sections[i]);
		
						var sectionName = coalesceSection.getSectionName();

						var sectionId = $( '#' + entityNameNoSpace ).jstree('create_node',entityId,{
						    "text": sectionName,
						    "coalesceType": CoalesceObjectType.SECTION
						  },"last");
		
						var recordsets = coalesceSection.getRecordsets();
		
						for (j = 0; j < recordsets.length; j++) {
		
							var coalesceRecordSet = new CoalesceRecordSet();
							coalesceRecordSet.initialize(recordsets[j]);
		
							var recordsetName = coalesceRecordSet.getRecordsetName();
							
							var recordsetId = $( '#' + entityNameNoSpace ).jstree('create_node',sectionId,{
							    "text": recordsetName,
							    "coalesceType": CoalesceObjectType.RECORDSET
							  },"last");
		
							var fields = coalesceRecordSet.getFields();
		
							for (k = 0; k < fields.length; k++) {
		
								var coalesceField = new CoalesceField();
								coalesceField.initialize(fields[k]);
		
								var name = coalesceField.getFieldName();
								var fieldtype = coalesceField.getFieldType();
		
								var value = name + ":" + fieldtype;
		
								var fieldId = $( '#' + entityNameNoSpace ).jstree('create_node',recordsetId,{
								    "text": value,
								    "fieldType": fieldtype
								  },"last");
								
							}
		
					}
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

			var entityNode = $('#' + entityNameNoSpace).jstree(true).get_node(entityId);
			
			var className = entityNode.original.classname.replace(/\./g, '-');
			
			coalesceCell.setCoalesceObj(new coalesceEditCellToCoalesceObj(content, className));

			//console.log(coalesceCell.getCoalesceObj);
			
			//coalesceCell.coalesceObj.classname = entityNode.original.classname;
			
			//console.log(coalesceCell.coalesceObj);
			
			graph.getModel().beginUpdate();
			try {

//			coalesceCell.value = entityCell.getValue();
//
//			cell.value = entityCell.getValue();

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

function editMenu ( node ) {
	
	var items = {
			 "rename": {
				 "label": "Rename" , //Different label (defined above) will be shown depending on node type
	                "action": function (data) {
	                	
	                	var inst = $.jstree.reference(data.reference),
						obj = inst.get_node(data.reference);
					    inst.edit(obj);
	                }
			 }
	};
	
	switch ( node.original.coalesceType ) {
		case CoalesceObjectType.ENTITY:
			//Add Section
			items.addNode = {
				 "label": "Add Section" , //Different label (defined above) will be shown depending on node type
	                "action": function (obj) {
	                	addNode (obj, "New Section", CoalesceObjectType.SECTION);
	                }
		};
			items.editClassName = {
					 "label": "Edit ClassName" , //Different label (defined above) will be shown depending on node type
		                "action": function (obj) {
		                	bootbox.prompt({ 
		                		  size: "small",
		                		  title: "Edit ClassName",
		                		  value: node.original.classname,
		                		  callback: function(result){ 
		                			  
		                			  if(result != null){
		                				  node.original.classname = result
		                			  }
		                		  		}
		                		})
		                }
			};
			break;
		case CoalesceObjectType.SECTION:
			///Add RecordSet
			items.addNode = {
				 "label": "Add RecordSet" , //Different label (defined above) will be shown depending on node type
	                "action": function (obj) {
	                	addNode (obj, "New RecordSet", CoalesceObjectType.RECORDSET);
	                }
		};
			break;
		case CoalesceObjectType.RECORDSET:
			//Add Field Definition
			items.addNode = {
				 "label": "Add Field Definition" , //Different label (defined above) will be shown depending on node type
	                "action": function (obj) {
	                	addNode (obj, "New Field Definition:string", CoalesceObjectType.FIELD_DEF);
	                }
		};
		case CoalesceObjectType.FIELD_DEF:
			break;
		default:
			// do nothing
	}
	
	//add delete option for every type but ENTITY
	if(node.original.coalesceType != CoalesceObjectType.ENTITY){
		items.deleteNode = {
				"label": "Delete" , //Different label (defined above) will be shown depending on node type
                "action": function (data) {
					var inst = $.jstree.reference(data.reference),
					obj = inst.get_node(data.reference);
				if(inst.is_selected(obj)) {
					inst.delete_node(inst.get_selected());
				}
				else {
					inst.delete_node(obj);
				}
                }
		}
		
	}
	
	return items;
}

function addNode (data, name, type){
	
	var inst = $.jstree.reference(data.reference),
	obj = inst.get_node(data.reference);
    inst.create_node(obj, name, "last", function (new_node) {
	try {
		inst.edit(new_node);
	} catch (ex) {
		setTimeout(function () { inst.edit(new_node); },0);
	}
	
	new_node.original.coalesceType = type;
	
});

}
		

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


function coalesceEditCellToCoalesceObj(content, className){
	
	console.log(content);
	
	var rootnode = $( content ).jstree('get_node',"#");
	
	//console.log(rootnode);
	
//	console.log($( content ).jstree('get_json',rootnode));
	
	var tree = $( content ).jstree('get_json',rootnode);
	
	console.log(tree);
	
	var templateName = tree[0].text;

	var coalesceEntityTemplate = new CoalesceEntityTemplate(templateName,[]);

	if(className != null){
		coalesceEntityTemplate.className = className;
	}else{
		console.log("className is null, defaulting to template name");
		coalesceEntityTemplate.className = templateName.replace(/\s/g, '');
	} 
	
	var sectionCells = tree[0].children;

	if(sectionCells != null){
		for(i = 0; i < sectionCells.length; i++)  {

			// sections
			var sectionName = sectionCells[i].text;

			var section = new CoalesceSection(sectionName, []);

			var recordsetCells = sectionCells[i].children;

			if(recordsetCells !=null){

				for(j = 0; j < recordsetCells.length; j++) {
				// recordsets
					var recordsetName = recordsetCells[j].text;
					var recordset = new CoalesceRecordSet(recordsetName, []);

					var fieldDefCells = recordsetCells[j].children;

					if(fieldDefCells != null){
						for(k = 0; k < fieldDefCells.length; k++) {
							// fields
								//var coalesceEditCell = new CoalesceEditCell(fieldDefCells[k]);

								var name = fieldDefCells[k].text.split(":")[0];
								var datatype = fieldDefCells[k].text.split(":")[1];

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

	console.log(coalesceEntityTemplate);
	
	return coalesceEntityTemplate;
}

function xmlDocToCoalesceObj(xmlDoc){

// we dont need the Linkage Section
$( xmlDoc ).find( 'entity' ).find( 'linkagesection' ).remove();

var templateName = $( xmlDoc ).find( 'entity' ).attr( "name" );

var className = $( xmlDoc ).find( 'entity' ).attr( "classname" );

var coalesceEntityTemplate = new CoalesceEntityTemplate(templateName,[]);

coalesceEntityTemplate.className = className;

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

app.controller( 'draganddrop', function ( $scope, $rootScope, graphUtils ) {

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

			graphUtils.createEntityNode( e.target.result,  $rootScope.graph ,  xLoc ,  yLoc)

		}
		reader.readAsText( file );

	}



	function addEntityToNav (cell, xmlDoc ) {
//		var container = $rootScope.navigator;
//
//		var div = $( container );

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



app.service('graphUtils', function() {
    this.createEntityNode = function (xml, graph, x, y) {
    	// add a single node to graph
//    	var graph = $rootScope.graph;

    	console.log(x);
    	
    	console.log(y);
    	
    	var parent = graph.getDefaultParent();

    	var parser = new DOMParser();

    	var xmlDoc = parser.parseFromString( xml, "text/xml" );

    	var entityName = $( xmlDoc ).find( 'entity' ).attr( "name" );

    	// Adds cells to the model

    	graph.getModel().beginUpdate();
    	try {

    		var col1 = graph.insertVertex( parent, null, '', 0, 0, 160, 0, 'column' );

    		var cellY = y

    		var cellX = x

    		var entityCell = graph.insertVertex( parent, null, entityName, cellX - 140/2, cellY - 30/2, 140, 30 );

    		var coalesceCell = new CoalesceCell(entityCell);

    		coalesceCell.setCoalesceObj(new xmlDocToCoalesceObj(xmlDoc));

    		coalesceCell.setEditorOpen(false);

    		// update the nav tree
    		
//    		var cellID = entityCell.id;

//    		var entityName = $( xmlDoc ).find( 'entity' ).attr( "name" );
    		
    		$( '#navtree' ).jstree('create_node',"#",{
    		    "id": entityCell.id,
    		    "text": entityName
    		  },"last");
    		
    		
//    		addEntityToNav(entityCell, xmlDoc );

    	} finally {
    		// Updates the display
    		graph.getModel().endUpdate();
    	}
    }
    
    this.x = null;
    this.y = null;
    
    this.getX = function () {
    	$( document ).mousemove( function ( event ) {
    		 this.x = event.pageX;

		} );
    	
    	return this.x
    }
    this.setX = function (x) {
    	this.x = x;
    }
    this.getY = function () {
    	return this.y;
    }
    this.setY = function (y) {
    	this.y = y ;
    }
    
});

//TODO, this needs to be a service
//function createEntityNode ( xml,graph ) {

	// add a single node to graph
//	var graph = $rootScope.graph;

//	var parent = graph.getDefaultParent();
//
//	var parser = new DOMParser();
//
//	var xmlDoc = parser.parseFromString( xml, "text/xml" );
//
//	var entityName = $( xmlDoc ).find( 'entity' ).attr( "name" );

	// Adds cells to the model

//	graph.getModel().beginUpdate();
//	try {
//
//		var col1 = graph.insertVertex( parent, null, '', 0, 0, 160, 0, 'column' );
//
//		var cellY = $rootScope.currentMousePos.y
//
//		var cellX = $rootScope.currentMousePos.x
//
//		var entityCell = graph.insertVertex( parent, null, entityName, cellX - 140/2, cellY - 30/2, 140, 30 );
//
//		var coalesceCell = new CoalesceCell(entityCell);
//
//		coalesceCell.setCoalesceObj(new xmlDocToCoalesceObj(xmlDoc));
//
//		coalesceCell.setEditorOpen(false);
//
//		// update the nav tree
//		addEntityToNav(entityCell, xmlDoc );
//
//	} finally {
//		// Updates the display
//		graph.getModel().endUpdate();
//	}
//
//}

app.service('graphHolder', function() {
	
	this.graph = null;
	
	this.getGraph = function () {
		return this.graph;
	}
	
	this.setGraph = function (graph) {
		this.graph = graph;
	}
});
