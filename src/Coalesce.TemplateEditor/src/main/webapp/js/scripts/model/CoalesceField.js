function CoalesceField (fieldName,type){
	this.name=fieldName;
	this.dataType=type;
	this.objType=CoalesceObjectType.FIELD_DEF;

	this.getName = function (){
		return this.name;
	}

	this.setName = function (name){
		this.name = name;
	}

	this.getDataType = function (){
		return this.dataType;
	}

	this.setDataType = function (type){
		this.dataType = type;
	}

	this.getCoalesceType = function(){
			return this.objType;
	}

	this.initialize = function (obj){
		if(obj.objType == CoalesceObjectType.FIELD_DEF){
			this.name=obj.name;
			this.dataType=obj.dataType;
			this.objType=obj.objType;
			return true;
		}else{
			return false;
		}
	}
}
