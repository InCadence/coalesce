(function() {

	// getElementById
	function $id(id) {
		return document.getElementById(id);
	}

	// output information
	function Output(msg) {
		var m = $id("messages");
		m.innerHTML = msg + m.innerHTML;
	}

	// file drag hover
	function FileDragHover(e) {
		e.stopPropagation();
		e.preventDefault();
		e.target.className = (e.type == "dragover" ? "hover" : "");
	}

	// file selection
	function FileSelectHandler(e) {

		// cancel event and hover styling
		FileDragHover(e);

		// fetch FileList object
		var files = e.target.files || e.dataTransfer.files;

		// process all File objects
		for (var i = 0, f; f = files[i]; i++) {
			ParseFile(f);
		}

	}

	// output file information
	function ParseFile(file) {

		Output("<p>File information: <strong>" + file.name
				+ "</strong> type: <strong>" + file.type
				+ "</strong> size: <strong>" + file.size
				+ "</strong> bytes</p>");

		var reader = new FileReader();

		reader.onload = function(e) {
			console.log(e.target.result)

			CreateGraph(e.target.result)

		}
		reader.readAsText(file);

	}

	function CreateGraph(xml) {

		// Gets the default parent for inserting new cells. This
		// is normally the first child of the root (ie. layer 0).
		var parent = graph.getDefaultParent();

		var parser = new DOMParser();

		var xmlDoc = parser.parseFromString(xml, "text/xml");
		
		var entityName = $(xmlDoc).find('entity').attr("name");

		var sectionName = $(xmlDoc).find('entity').find('section').attr("name");

		var recordsetName = $(xmlDoc).find('entity').find('section').find('recordset').attr("name");
		// Adds cells to the model
		graph.getModel().beginUpdate();
		try {

			var col1 = graph.insertVertex(parent, null, '', 0, 0, 160, 0,
					'column');

			var entity = graph.insertVertex(col1, null, entityName, 0, 0, 140,
					30);
			entity.collapsed = true;

			var section = graph.insertVertex(entity, null, sectionName, 0, 0,
					120, 30);
			section.collapsed = true;

			var recordset = graph.insertVertex(section, null, recordsetName, 0,
					0, 100, 30);

			$(xmlDoc).find('entity').find('section').find('recordset')
					.children().each(function() {

						var name = $(this).attr("name");
						var datatype = $(this).attr("datatype");

						var value = name + ":" + datatype;
						
						graph.insertVertex(recordset,null,value,0,0,90,30);
						
						console.log(name + ":" + datatype);

					})

		} finally {
			// Updates the display
			graph.getModel().endUpdate();
		}

	}

	// initialize
	function Init() {

		var fileselect = $id("fileselect"), filedrag = $id("graphContainer"), submitbutton = $id("submitbutton");

		// file select
		fileselect.addEventListener("change", FileSelectHandler, false);

		// is XHR2 available?
		var xhr = new XMLHttpRequest();
		if (xhr.upload) {

			// file drop
			filedrag.addEventListener("dragover", FileDragHover, false);
			filedrag.addEventListener("dragleave", FileDragHover, false);
			filedrag.addEventListener("drop", FileSelectHandler, false);
			filedrag.style.display = "block";

			// remove submit button
			submitbutton.style.display = "none";
		}

	}

	// call initialization file
	if (window.File && window.FileList && window.FileReader) {
		Init();
	}

})();