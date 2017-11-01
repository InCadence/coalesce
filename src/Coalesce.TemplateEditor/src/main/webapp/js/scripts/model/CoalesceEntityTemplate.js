function CoalesceEntityTemplate (name, source, version, classname, sections){
	this.name=name;
	this.source=source;
	this.version=version;
	this.className=classname;
	this.sectionsAsList=sections;
	this.objType=CoalesceObjectType.ENTITY;
	this.addSection = function (section){
		sections.push(section);
	}

	this.getName = function (){
		return this.name;
	}

	this.setName = function (name){
		this.name = name;
	}

	this.getSectionsAsList = function (){
		return this.sectionsAsList;
	}

	this.getCoalesceType = function(){
			return this.objType;
	}

	this.initialize = function (obj){
		if(obj.objType == CoalesceObjectType.ENTITY){
			this.name=obj.name;
			this.source=obj.name;
			this.version=obj.name;
			this.objType=obj.objType;
			this.sectionsAsList=obj.sectionsAsList;
			this.classname=obj.classname;
			return true;
		}else{
			return false;
		}
	}
}
