package Coalesce.Common.Helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Coalesce.Framework.DataModel.*;
import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;

/*-----------------------------------------------------------------------------'
Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

Notwithstanding any contractor copyright notice, the Government has Unlimited
Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
of this work other than as specifically authorized by these DFARS Clauses may
violate Government rights in this work.

DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
Unlimited Rights. The Government has the right to use, modify, reproduce,
perform, display, release or disclose this computer software and to have or
authorize others to do so.

Distribution Statement D. Distribution authorized to the Department of
Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
-----------------------------------------------------------------------------*/

public class EntityLinkHelper {
	
    /*
	private static String MODULE = "Coalesce.Common.Helpers.EntityLinkHelper";

	// Make static class
	private EntityLinkHelper() { }

	public static void main(String[] args) {
		
	}
	//TODO: Entire class
	public CallResult LinkEntities(XsdEntity Entity1, ELinkTypes LinkType, XsdEntity Entity2, Boolean UpdateExisting){ 
		
		CallResult rst = new CallResult();

	    try{
	    	
	        return LinkEntities(Entity1, LinkType, Entity2, "U", "", "en-US", UpdateExisting);
	      
	    }catch (Exception ex){
	        // return Failed Error
	    	rst = new CallResult(CallResults.FAILED_ERROR, ex, EntityLinkHelper.MODULE);
	        return rst;
	    }
	}

	public CallResult LinkEntities(XsdEntity Entity1, ELinkTypes LinkType, XsdEntity Entity2, String ClassificationMarking, String ModifiedBy, String InputLang, Boolean UpdateExisting) {
	    try{
			CallResult rst = new CallResult();
			CoalesceLinkageSection LinkageSection1 = new CoalesceLinkageSection();
			CoalesceLinkage Linkage1 = new CoalesceLinkage();
			CoalesceLinkageSection LinkageSection2 = new CoalesceLinkageSection();
			CoalesceLinkage Linkage2 = new CoalesceLinkage();
			Boolean Linkage1AlreadyExists = false;
			Boolean Linkage2AlreadyExists = false;
	
	        // Get the LinkageSections for each Entity.  Create if not found.
	
	        // For Entity 1...
	        rst = Entity1.GetLinkageSection(LinkageSection1);
	
	        // Evaluate
	        if (rst.getIsFailed()){
	            if (rst.getMessage() == "Object not found."){
	                // Create
	                rst = Entity1.CreateLinkageSection(LinkageSection1);
	            }
	            else{
	                // return Failed
	                return rst;
	            }
	        }
	
	        // For Entity 2...
	        rst = Entity2.GetLinkageSection(LinkageSection2);
	
	        // Evaluate
	        if (rst.getIsFailed()){
	            if (rst.getMessage() == "Object not found."){
	                // Create
	                rst = Entity2.CreateLinkageSection(LinkageSection2);
	            }
	            else{
	                // return Failed
	                return rst;
	            }
	        }

	        //for (ICoalesceDataObject cdo : LinkageSection1.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection1.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage"){
	            	if (((CoalesceLinkage)cdo).Entity1Key == Entity1.GetKey() &&
	            			ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)cdo).GetLinkType())) == LinkType &&
	            			((CoalesceLinkage)cdo).Entity2Key == Entity2.GetKey()) {
	            		// Found; Use Existing Linkage
	            		Linkage1AlreadyExists = true;
	            		Linkage1 = (CoalesceLinkage) cdo;
	            		break;
		            }
	            }
	        }
	        
	        // Do we already have the Reciprocal Linkage made? (Same Entities and Same LinkType)?                
	        Linkage2AlreadyExists = false;
	        //for (ICoalesceDataObject cdo : LinkageSection2.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection2.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage") {
                // Switch Entities since we're looking at the Reciprocal.
	            	if (((CoalesceLinkage)cdo).Entity1Key == Entity2.GetKey() &&
	            			ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)cdo).GetLinkType())) == ELinkTypes.GetReciprocalLinkType(LinkType) &&
	                		//((CoalesceLinkage)cdo).LinkType == ELinkTypes.GetReciprocalLinkType(LinkType) &&
	                		((CoalesceLinkage)cdo).Entity2Key == Entity1.GetKey()) {
	                    // Found; Use Existing Linkage
	                    Linkage2AlreadyExists = true;
	                    Linkage2 = (CoalesceLinkage)cdo;
	                    break;
	                }
	            }
	        }
	
	        // Update/Populate Linkage 1         
	        if (Linkage1AlreadyExists){
	            // Update/Populate Existing
	            rst = Linkage1.EstablishLinkage(Entity1, LinkType, Entity2, ClassificationMarking, ModifiedBy, InputLang);
	        	
	        }else{
	            // Create
	            rst = LinkageSection1.CreateLinkage(Linkage1);
	
	            // Update/Populate
	            rst = Linkage1.EstablishLinkage(Entity1, LinkType, Entity2, ClassificationMarking, ModifiedBy, InputLang);
	        }
	        	
	
	        // Update/Populate Linkage 2
	        if (Linkage2AlreadyExists){
	            if (UpdateExisting){
	            	// Update/Populate Existing
	                rst = Linkage2.EstablishLinkage(Entity2, ELinkTypes.GetReciprocalLinkType(LinkType), Entity1, ClassificationMarking, ModifiedBy, InputLang);
	            }
	        }else{
	            // Create
	            rst = LinkageSection2.CreateLinkage(Linkage2);
	
	            // Update/Populate
	            rst = Linkage2.EstablishLinkage(Entity2, ELinkTypes.GetReciprocalLinkType(LinkType), Entity1, ClassificationMarking, ModifiedBy, InputLang);
	        }
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch (Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, EntityLinkHelper.MODULE);
	    }
	}

    public CallResult UnLinkEntities(XsdEntity Entity1, XsdEntity Entity2){
	    try{
	        return UnLinkEntities(Entity1, Entity2, "U", "", "en-US");
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, EntityLinkHelper.MODULE);
	    }
	}

	public CallResult UnLinkEntities(XsdEntity Entity1, XsdEntity Entity2, ELinkTypes LinkType) {
	    try{
	        return UnLinkEntities(Entity1, Entity2, "U", "", "en-US", LinkType);
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, EntityLinkHelper.MODULE);
	    }
	}

	public CallResult UnLinkEntities(XsdEntity Entity1, XsdEntity Entity2, String ClassificationMarking, String ModifiedBy, String InputLang){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection1 = null;
	        CoalesceLinkageSection LinkageSection2 = null;
	
	        // Get the LinkageSections for each Entity.  Exit if not found.
	
	        // For Entity 1...
	        rst = Entity1.GetLinkageSection(LinkageSection1);
	        if (!(rst.getIsSuccess())) return new CallResult(CallResults.FAILED);
	
	        // For Entity 2...
	        rst = Entity2.GetLinkageSection(LinkageSection2);
	        if (!(rst.getIsSuccess())) return new CallResult(CallResults.FAILED);
	
	        // Mark linkage as deleted
            //for(ICoalesceDataObject cdo : LinkageSection1.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection1.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage") {
	            	if( ((CoalesceLinkage) cdo).GetEntity1Key() == Entity1.GetKey() &&
	            		((CoalesceLinkage) cdo).GetEntity2Key() == Entity2.GetKey()) {
	            		((CoalesceLinkage) cdo).SetDataObjectStatus(ECoalesceDataObjectStatus.DELETED);
	                    break;
	                }
	            }
	        }
	
	        // Mark linkage as deleted
            //for(ICoalesceDataObject cdo : LinkageSection2.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection2.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage") {
	                // Switch Entities since we're looking at the Reciprocal.
	            	if( ((CoalesceLinkage) cdo).GetEntity1Key() == Entity2.GetKey() &&
	            		((CoalesceLinkage) cdo).GetEntity2Key() == Entity1.GetKey()) {
	            		((CoalesceLinkage) cdo).SetDataObjectStatus(ECoalesceDataObjectStatus.DELETED);
	                    break;
	                }
	            }
	        }
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, EntityLinkHelper.MODULE);
	    }
	}

	public CallResult UnLinkEntities(XsdEntity Entity1, XsdEntity Entity2, String ClassificationMarking, String ModifiedBy, String InputLang, ELinkTypes LinkType){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection1 = null;
	        CoalesceLinkageSection LinkageSection2 = null;
	
	
	        // Get the LinkageSections for each Entity.  Exit if not found.
	
	        // For Entity 1...
	        rst = Entity1.GetLinkageSection(LinkageSection1);
	        if (!(rst.getIsSuccess())) return new CallResult(CallResults.FAILED);
	
	        // For Entity 2...
	        rst = Entity2.GetLinkageSection(LinkageSection2);
	        if (!(rst.getIsSuccess())) return new CallResult(CallResults.FAILED);
	
	        // Mark linkage as deleted
	        //for(ICoalesceDataObject cdo : LinkageSection1.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection1.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage") {
        			//((CoalesceLinkage) cdo).GetLinkType() == LinkType && 
	            	if( ((CoalesceLinkage) cdo).GetEntity1Key() == Entity1.GetKey() &&
	            			ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)cdo).GetLinkType())) == LinkType &&
		            		((CoalesceLinkage) cdo).GetEntity2Key() == Entity2.GetKey() ) {
		            	((CoalesceLinkage) cdo).SetDataObjectStatus(ECoalesceDataObjectStatus.DELETED);
	                    break;
	                }
	            }
	        }
	
	        // Mark linkage as deleted
	        //for(ICoalesceDataObject cdo : LinkageSection2.ChildDataObjects.Values){
        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection2.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
        		CoalesceDataObject cdo = (CoalesceDataObject) iterator;
	            if (cdo.GetObjectType() == "linkage") {
	                // Switch Entities since we're looking at the Reciprocal.
        			//((CoalesceLinkage) cdo).GetLinkType() == ELinkTypes.GetReciprocalLinkType(LinkType) && 
	            	if( ((CoalesceLinkage) cdo).GetEntity1Key() == Entity2.GetKey() &&
	            			ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)cdo).GetLinkType())) == ELinkTypes.GetReciprocalLinkType(LinkType) &&
		            		((CoalesceLinkage) cdo).GetEntity2Key() == Entity1.GetKey() ) {
	                    cdo.SetDataObjectStatus(ECoalesceDataObjectStatus.DELETED);
	                    break;
	                }
	            }
	        }
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, EntityLinkHelper.MODULE);
	    }
	}

    public CallResult GetLinkages(XsdEntity Entity, Map<String, XsdEntity> Linkages){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection  = null;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = Entity.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
	            //for(CoalesceDataObject Obj : LinkageSection.GetChildDataObjects().values()){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.ObjectType == "linkage") {
	                    // Yes; Add to the Dictionary
	                    Results.put(Obj.GetKey(), (CoalesceLinkage) Obj);
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}

	public CallResult GetLinkages(CoalesceEntity Entity, String ForEntityName, Map<String, CoalesceLinkage> Linkages){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = null;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = Entity.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
	            //for(CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.GetObjectType() == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                    if (((CoalesceLinkage)Obj).GetEntity2Name().toLowerCase() == ForEntityName.toLowerCase()) {
	                        // Yes; Add to the Dictionary
	                        Results.put(Obj.GetKey(), (CoalesceLinkage)Obj);
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult GetLinkages(CoalesceEntity Entity, ELinkTypes ForLinkType, String ForEntityName, Map<String, CoalesceLinkage> Linkages){
		ELinkTypes[] ForLinkTypes = {ForLinkType};
	
	    return GetLinkages(Entity, ForLinkTypes, ForEntityName, Linkages);
	}
	
	public CallResult GetLinkages(CoalesceEntity Entity, ELinkTypes[] ForLinkTypes, String ForEntityName, Map<String, CoalesceLinkage> Linkages){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection= null;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = Entity.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
	            //for(CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.GetObjectType() == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                	if( ((CoalesceLinkage) Obj).GetEntity2Name() == ForEntityName ) {
	                		boolean typeContained = false;
	                		String type = ((CoalesceLinkage) Obj).GetObjectType();
	                		for (int i = 0; i < ForLinkTypes.length; i++){
	                			if(ELinkTypes.GetELinkTypeLabelForType(ForLinkTypes[i]) == type){
	                				typeContained = true;
	                				break;
	                			}
	                		}
	                		//if (ForLinkTypes.contains(((CoalesceLinkage) Obj).GetLinkType()) ){
	                		if(typeContained){
		                        // Yes; Add to the Dictionary
		                        if (Obj.GetDataObjectStatus() != ECoalesceDataObjectStatus.DELETED) {
		                            Results.put(Obj.GetKey(), (CoalesceLinkage)Obj);
		                        }
	                		}
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult GetLinkages(CoalesceEntity Entity, ELinkTypes ForLinkType, String ForEntityName, String ForEntitySource, Map<String, CoalesceLinkage> Linkages){ 
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = null;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = Entity.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
	            //for(CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.ObjectType == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                	//((CoalesceLinkage) Obj).LinkType == ForLinkType &&
	                	if ( ((CoalesceLinkage) Obj).Entity2Name == ForEntityName &&
	                		ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)Obj).GetLinkType())) == ForLinkType &&
	                		((CoalesceLinkage) Obj).Entity2Source == ForEntitySource &&
	                		((CoalesceLinkage) Obj).DataObjectStatus != ECoalesceDataObjectStatus.DELETED) {
	                			// Yes; Add to the Dictionary
	                			Results.put(Obj.GetKey(), (CoalesceLinkage)Obj);
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	
	public CallResult GetLinkages(CoalesceEntity Entity, ELinkTypes ForLinkType, Map<String, CoalesceLinkage> Linkages){
	    try{
	        CallResult rst;
	        CoalesceLinkageSection LinkageSection = null;
	        Map<String, CoalesceLinkage> Results = new HashMap<String, CoalesceLinkage>();
	
	        // Get Linkage Section
	        rst = Entity.GetLinkageSection(LinkageSection);
	
	        // Evaluate
	        if (rst.getIsSuccess() && (LinkageSection != null)) {
	            //for(CoalesceDataObject Obj : LinkageSection.ChildDataObjects.Values){
	        	for (Iterator<ICoalesceDataObject> iterator = LinkageSection.GetChildDataObjects().values().iterator(); iterator.hasNext();) {
	        		CoalesceDataObject Obj = (CoalesceDataObject) iterator;
	                // Is the child data object a Linkage?
	                if (Obj.GetObjectType() == "linkage") {
	                    // Yes; Is it the link type we're looking for?
	                	if (ELinkTypes.GetELinkTypeTypeForCode(ELinkTypes.GetELinkTypeCodeForLabel(((CoalesceLinkage)Obj).GetLinkType())) == ForLinkType){

	                        // Yes; Add to the Dictionary
	                        if (Obj.DataObjectStatus != ECoalesceDataObjectStatus.DELETED) {
	                            Results.put(Obj.GetKey(), (CoalesceLinkage)Obj);
	                        }
	                    }
	                }
	            }
	        }
	
	        // Set return Value
	        Linkages = Results;
	        //Linkages = new HashMap<String, CoalesceLinkage>();
	        //Linkages.putAll(Results);
	        		//= Results;
	
	        // return Success
	        return CallResult.successCallResult;
	
	    }catch(Exception ex){
	        // Set to Empty Dictionary
	        Linkages = new HashMap<String, CoalesceLinkage>();
	
	        // return Failed Error
	        return new CallResult(CallResults.FAILED_ERROR, ex, this);
	    }
	}
	*/

}
