package Coalesce.Framework.DataModel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
import Coalesce.Common.Helpers.StringHelper;
import Coalesce.Common.Helpers.XmlHelper;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field;
import Coalesce.Framework.GeneratedJAXB.Entity.Section.Recordset.Record.Field.Fieldhistory;

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

public class XsdFieldHistory extends XsdFieldBase {

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private Fieldhistory _entityFieldHistory;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    public static XsdFieldHistory Create(XsdFieldBase parent)
    {

        if (!(parent instanceof XsdField)) throw new ClassCastException("Must be of type XsdField");

        return Create((XsdField) parent);
    }

    public static XsdFieldHistory Create(XsdField parent)
    {
        try
        {

            // Set References
            XsdFieldHistory newFieldHistory = new XsdFieldHistory();
            if (!newFieldHistory.Initialize(parent)) return null;

            // Copy attributes from parent node
            newFieldHistory.SetAttributes(parent);

            newFieldHistory.SetPreviousHistoryKey(parent.GetPreviousHistoryKey());

            // Append to parent's child node collection
            parent.GetEntityFieldHistories().add(0, newFieldHistory._entityFieldHistory);

            // Add to Parent's Child Collection
            if (!parent._childDataObjects.containsKey(newFieldHistory.GetKey()))
            {
                parent._childDataObjects.put(newFieldHistory.GetKey(), newFieldHistory);
            }

            return newFieldHistory;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private boolean Initialize(XsdField parent) {
        
        Fieldhistory entityFieldHistory = new Fieldhistory();
        
        return Initialize(parent, entityFieldHistory);
        
    }
    public boolean Initialize(XsdField parent, Fieldhistory fieldHistory)
    {

        // Set References
        _parent = parent;
        _entityFieldHistory = fieldHistory;

        return super.Initialize();

    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    @Override
    protected String GetObjectKey()
    {
        return _entityFieldHistory.getKey();
    }

    @Override
    public void SetObjectKey(String value)
    {
        _entityFieldHistory.setKey(value);
    }

    @Override
    public String GetName()
    {
        return GetStringElement(_entityFieldHistory.getName());
    }

    @Override
    public void SetName(String value)
    {
        _entityFieldHistory.setName(value);
    }

    @Override
    public String getType()
    {
        return "fieldhistory";
    }
    
    public String GetValue()
    {
        return _entityFieldHistory.getValue();
    }

    public void SetValue(String value)
    {
        _entityFieldHistory.setValue(value);
    }

    public String GetDataType()
    {
        return GetStringElement(_entityFieldHistory.getDatatype());
    }

    public void SetDataType(String value)
    {
        _entityFieldHistory.setDatatype(value);
    }

    public String GetLabel()
    {
        return GetStringElement(_entityFieldHistory.getLabel());
    }

    public void SetLabel(String value)
    {
        _entityFieldHistory.setLabel(value);
    }

    public int GetSize()
    {
        try
        {
            return Integer.parseInt(_entityFieldHistory.getSize());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    public void SetSize(Integer value)
    {
        _entityFieldHistory.setSize(value.toString());
    }

    public String GetModifiedBy()
    {
        return GetStringElement(_entityFieldHistory.getModifiedby());
    }

    public void SetModifiedBy(String value)
    {
        _entityFieldHistory.setModifiedby(value);
    }

    public String GetModifiedByIP()
    {
        return GetStringElement(_entityFieldHistory.getModifiedbyip());
    }

    public void SetModifiedByIP(String value)
    {
        _entityFieldHistory.setModifiedbyip(value);
    }

    public String GetClassificationMarking()
    {
        return _entityFieldHistory.getClassificationmarking();
    }

    public void SetClassificationMarking(String value)
    {
        _entityFieldHistory.setClassificationmarking(value);
    }

    public String GetPreviousHistoryKey()
    {
        String prevHistKey = _entityFieldHistory.getPrevioushistorykey();
        if (StringHelper.IsNullOrEmpty(prevHistKey))
        {
            return "00000000-0000-0000-0000-000000000000";
        }
        else
        {
            return prevHistKey;
        }
    }

    public void SetPreviousHistoryKey(String value)
    {
        _entityFieldHistory.setPrevioushistorykey(value);
    }

    public String GetFilename()
    {
        return GetStringElement(_entityFieldHistory.getFilename());
    }

    public void SetFilename(String value)
    {
        _entityFieldHistory.setFilename(value);
    }

    public String GetExtension()
    {
        return GetStringElement(_entityFieldHistory.getExtension());
    }

    public void SetExtension(String value)
    {
        _entityFieldHistory.setExtension(value.replace(".", ""));
    }

    public String GetMimeType()
    {
        return GetStringElement(_entityFieldHistory.getMimetype());
    }

    public void SetMimeType(String value)
    {
        _entityFieldHistory.setMimetype(value);
    }

    public String GetHash()
    {
        return GetStringElement(_entityFieldHistory.getHash());
    }

    public void SetHash(String value)
    {
        _entityFieldHistory.setHash(value);
    }

    /*
     * public String GetInputLang(){ return _entityField.getInputlang(); } public void SetInputLang(String value){
     * _entityField.setInputlang(value); }
     */

    @Override
    public DateTime GetDateCreated()
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldHistory.getDatecreated());
        return _entityFieldHistory.getDatecreated();
    }

    @Override
    public void SetDateCreated(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityFieldHistory.setDatecreated(value);
    }

    @Override
    public DateTime GetLastModified()
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldHistory.getLastmodified());
        return _entityFieldHistory.getLastmodified();
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    public String ToXml()
    {
        return XmlHelper.Serialize(_entityFieldHistory);
    }

    // -----------------------------------------------------------------------//
    // protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected void SetObjectLastModified(DateTime value)
    {
        _entityFieldHistory.setLastmodified(value);
    }

    private CallResult SetAttributes(XsdField field)
    {
        try
        {

            Field entityField = field._entityField;
            
            _entityFieldHistory.setName(entityField.getName());
            _entityFieldHistory.setValue(entityField.getValue());
            _entityFieldHistory.setDatatype(entityField.getDatatype());
            _entityFieldHistory.setLabel(entityField.getLabel());
            _entityFieldHistory.setSize(entityField.getSize());
            _entityFieldHistory.setModifiedby(entityField.getModifiedby());
            _entityFieldHistory.setModifiedbyip(entityField.getModifiedbyip());
            _entityFieldHistory.setClassificationmarking(entityField.getClassificationmarking());
            _entityFieldHistory.setPrevioushistorykey(entityField.getPrevioushistorykey());
            _entityFieldHistory.setFilename(entityField.getFilename());
            _entityFieldHistory.setExtension(entityField.getExtension());
            _entityFieldHistory.setMimetype(entityField.getMimetype());
            _entityFieldHistory.setHash(entityField.getHash());
            _entityFieldHistory.setValue(entityField.getValue());
            _entityFieldHistory.setDatecreated(entityField.getDatecreated());
            _entityFieldHistory.setLastmodified(entityField.getLastmodified());
            _entityFieldHistory.setStatus(entityField.getStatus());

            Map<QName, String> otherAttributes = getAttributes();
            
            for (Map.Entry<QName, String> otherAttr : field.getAttributes().entrySet())
            {
                
                otherAttributes.put(otherAttr.getKey(), otherAttr.getValue());
            }
            
            return CallResult.successCallResult;

        }
        catch (Exception ex)
        {
            return new CallResult(CallResults.FAILED_ERROR, ex, this);
        }
    }

    @Override
    protected String GetObjectStatus()
    {
        return _entityFieldHistory.getStatus();
    }

    @Override
    protected void SetObjectStatus(String status)
    {
        _entityFieldHistory.setStatus(status);
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entityFieldHistory.getOtherAttributes();
    }
}
