function CoalesceEntityTemplate (name, sections){
	this.templateName=name;
	this.sections=sections;
	this.objType=CoalesceObjectType.ENTITY;
	this.addSection = function (section){
		sections.push(section);
	}

	this.getTemplateName = function (){
		return this.templateName;
	}

	this.setTemplateName = function (name){
		this.templateName = name;
	}

	this.getSections = function (){
		return this.sections;
	}

	this.getCoalesceType = function(){
			return this.objType;
	}

	this.initialize = function (obj){
		if(obj.objType == CoalesceObjectType.ENTITY){
			this.templateName=obj.templateName;
			this.objType=obj.objType;
			this.sections=obj.sections;
			this.className=obj.className;
			return true;
		}else{
			return false;
		}
	}
}