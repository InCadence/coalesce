package Coalesce.Framework.DataModel;

//import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Date;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;


public interface ICoalesceDataObject {
	
	/*************************************************************************
	 * All instance variable/property declarations are static final 
	 * which means that they can not be changed.
	 * ******************************************************************** */
    //// Properties
	//public Document DataObjectDocument = null;
    //public Node DataObjectNode = null;
    //public CoalesceDataObject Parent = null;
    //public String Key = null;
    //public String Tag = null;
    //public String Name = null;
    //public java.util.Date DateCreated = null;
    //public java.util.Date LastModified = null;
    //public boolean NoIndex = false;
    //public ECoalesceDataObjectStatus DataObjectStatus = null;

    // ReadOnly Properties 
    //TODO: Make these readonly
    //public String ObjectType = null;
    //public String NamePath = null;
    //public Map<String, ICoalesceDataObject> ChildDataObjects = null;
 
    
    //For Class Properties
    public Document GetDataObjectDocument();
    public Node GetDataObjectNode();
    public ICoalesceDataObject GetParent();

    public String GetKey();
    public void SetKey(String value);
    public String GetTag();
    public void SetTag(String value);
    public String GetName();
    public void SetName(String value);
    public Date GetDateCreated();
    public void SetDateCreated(Date value);
    public Date GetLastModified();
    public void SetLastModified(Date value);
    public boolean GetNoIndex();
    public void SetNoIndex(boolean value);
    public ECoalesceDataObjectStatus GetDataObjectStatus();
    public void SetDataObjectStatus(ECoalesceDataObjectStatus value);

    //For Read Only class properties
    public Map<String, ICoalesceDataObject> GetChildDataObjects();
    public String GetObjectType();
    public String GetNamePath();

}
