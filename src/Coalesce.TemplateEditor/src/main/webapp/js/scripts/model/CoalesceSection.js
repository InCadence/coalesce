function CoalesceSection (name, recordsets){
	this.sectionName=name;
	this.recordsets=recordsets;
	this.objType=CoalesceObjectType.SECTION;

	this.addRecordset = function (recordset){
		recordsets.push(recordset);
	}
	this.getSectionName = function (){
		return this.sectionName;
	}

	this.setSectionName = function (name){
		this.sectionName = name;
	}

	this.getRecordsets = function (){
		return this.recordsets;
	}

	this.getCoalesceType = function(){
			return this.objType;
	}

	this.initialize = function (obj){
		if(obj.objType == CoalesceObjectType.SECTION){
			this.sectionName=obj.sectionName;
			this.objType=obj.objType;
			this.recordsets=obj.recordsets;
			return true;
		}else{
			return false;
		}
	}
}