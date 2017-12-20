function CoalesceRecordSet (name,min,max,fields){
	this.name=name;
	this.minRecords=min;
	this.maxRecords=max;
	this.fieldDefinitions=fields
	this.objType=CoalesceObjectType.RECORDSET;
	this.addField = function (coalesceField) {
		fields.push(coalesceField);
	}

	this.getName = function (){
		return this.name;
	}

	this.setName = function (name){
		this.name = name;
	}

	this.getFieldsDefinitions = function (){
		return this.fieldDefinitions;
	}

	this.getCoalesceType = function(){
			return this.objType;
	}

	this.initialize = function (obj){
		if(obj.objType == CoalesceObjectType.RECORDSET){
			this.name=obj.name;
			this.objType=obj.objType;
			this.fieldDefinitions=obj.fieldDefinitions;
			return true;
		}else{
			return false;
		}
	}
}
