package Coalesce.Framework.DataModel;

import java.util.Map;

import org.joda.time.DateTime;

public interface ICoalesceDataObject {
	
    /*--------------------------------------------------------------------------
    Public Properties
    --------------------------------------------------------------------------*/

    // Status
    public ECoalesceDataObjectStatus getStatus();
    public void setStatus(ECoalesceDataObjectStatus value);

    // Parent 
    public XsdDataObject getParent();
    public void setParent(XsdDataObject parent);

    // Key
    public String getKey();
    public void setKey(String value);
    
    // Name
    public String getName();
    public void setName(String value);

    // Tag
    public String getTag();
    public void setTag(String value);
    
    // Flatten
    public boolean getFlatten();
    public void setFlatten(boolean value);
    
    //Date Created
    public DateTime getDateCreated();
    public void setDateCreated(DateTime value);
    
    // Last Modified
    public DateTime getLastModified();
    public void setLastModified(DateTime value);
    
    // No Index
    public boolean getNoIndex();
    public void setNoIndex(boolean value);
    
    /*--------------------------------------------------------------------------
    Public Read Only
    --------------------------------------------------------------------------*/

    public Map<String, XsdDataObject> getChildDataObjects();
    public String getType();
    public String getNamePath();
    

}
