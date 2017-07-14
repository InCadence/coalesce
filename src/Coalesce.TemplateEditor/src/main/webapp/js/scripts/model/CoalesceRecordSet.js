function CoalesceRecordSet (name,fields){
	this.recordsetName=name;
	this.fields=fields
	this.objType=CoalesceObjectType.RECORDSET;
	this.addField = function (coalesceField) {
		fields.push(coalesceField);
	}

	this.getRecordsetName = function (){
		return this.recordsetName;
	}

	this.setRecordsetName = function (name){
		this.recordsetName = name;
	}

	this.getFields = function (){
		return this.fields;
	}

	this.getCoalesceType = function(){
			return this.objType;
	}

	this.initialize = function (obj){
		if(obj.objType == CoalesceObjectType.RECORDSET){
			this.recordsetName=obj.recordsetName;
			this.objType=obj.objType;
			this.fields=obj.fields;
			return true;
		}else{
			return false;
		}
	}
}