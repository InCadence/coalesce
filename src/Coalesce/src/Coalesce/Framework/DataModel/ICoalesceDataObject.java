package Coalesce.Framework.DataModel;

import java.util.Map;

import org.joda.time.DateTime;

public interface ICoalesceDataObject {
	
    /*--------------------------------------------------------------------------
    Public Properties
    --------------------------------------------------------------------------*/

    // Status
    public ECoalesceDataObjectStatus GetStatus();
    public void SetStatus(ECoalesceDataObjectStatus value);

    // Parent 
    public XsdDataObject GetParent();
    public void SetParent(XsdDataObject parent);

    // Key
    public String GetKey();
    public void SetKey(String value);
    
    // Name
    public String GetName();
    public void SetName(String value);

    // Tag
    public String GetTag();
    public void SetTag(String value);
    
    // Flatten
    public boolean GetFlatten();
    public void SetFlatten(boolean value);
    
    //Date Created
    public DateTime GetDateCreated();
    public void SetDateCreated(DateTime value);
    
    // Last Modified
    public DateTime GetLastModified();
    public void SetLastModified(DateTime value);
    
    // No Index
    public boolean GetNoIndex();
    public void SetNoIndex(boolean value);
    
    /*--------------------------------------------------------------------------
    Public Read Only
    --------------------------------------------------------------------------*/

    public Map<String, XsdDataObject> GetChildDataObjects();
    public String GetNamePath();

}
