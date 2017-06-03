function CoalesceEditCell (cell){

	this.getType = function (){
		return cell.coalesceType;
	}

	this.setType = function (type){
	 cell.coalesceType = type;
	}

	this.getCoalesceName = function (){
		return cell.getValue();
	}

	this.getFieldType = function (){
		return cell.fieldType;
	}

	this.setFieldType = function (value){
		cell.fieldType = value;
	}

}