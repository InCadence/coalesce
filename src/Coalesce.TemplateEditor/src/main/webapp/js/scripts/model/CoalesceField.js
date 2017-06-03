function CoalesceField (fieldName,type){
	this.fieldName=fieldName;
	this.fieldType=type;
	this.objType=CoalesceObjectType.FIELD_DEF;

	this.getFieldName = function (){
		return this.fieldName;
	}

	this.setFieldName = function (name){
		this.fieldName = name;
	}

	this.getFieldType = function (){
		return this.fieldType;
	}

	this.setFieldType = function (type){
		this.fieldType = type;
	}

	this.getCoalesceType = function(){
			return this.objType;
	}

	this.initialize = function (obj){
		if(obj.objType == CoalesceObjectType.FIELD_DEF){
			this.fieldName=obj.fieldName;
			this.fieldType=obj.fieldType;
			this.objType=obj.objType;
			return true;
		}else{
			return false;
		}
	}
}