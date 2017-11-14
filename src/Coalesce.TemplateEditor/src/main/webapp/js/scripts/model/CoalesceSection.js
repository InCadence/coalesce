function CoalesceSection (name, recordsets){
	this.name=name;
	this.recordsetsAsList=recordsets;
	this.objType=CoalesceObjectType.SECTION;

	this.addRecordset = function (recordset){
		recordsets.push(recordset);
	}
	this.getName = function (){
		return this.name;
	}

	this.setName = function (name){
		this.name = name;
	}

	this.getRecordsetsAsList = function (){
		return this.recordsetsAsList;
	}

	this.getCoalesceType = function(){
			return this.objType;
	}

	this.initialize = function (obj){
		if(obj.objType == CoalesceObjectType.SECTION){
			this.name=obj.name;
			this.objType=obj.objType;
			this.recordsetsAsList=obj.recordsetsAsList;
			return true;
		}else{
			return false;
		}
	}
}
