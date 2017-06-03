function CoalesceCell (cell){

	this.getCoalesceObj = function (){
		return cell.coalesceObj;
	}

	this.setCoalesceObj = function (coalesceObj){
		cell.coalesceObj = coalesceObj;
	}

	this.isEditorOpen = function (){
		return cell.isEditorOpen;
	}

	this.setEditorOpen = function (value){
		 cell.isEditorOpen = value;
	}
}