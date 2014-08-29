package Coalesce.Framework.DataModel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import unity.core.runtime.CallResult;
import unity.core.runtime.CallResult.CallResults;
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
            Fieldhistory newFieldHistory = new Fieldhistory();
            parent.GetEntityFieldHistories().add(newFieldHistory);

            // TODO: What is this part?
            // Copy attributes from parent node
            XsdFieldHistory fieldHistory = new XsdFieldHistory();
            if (!fieldHistory.Initialize(parent, newFieldHistory)) return null;

            fieldHistory.SetAttributes(parent._entityField);

            fieldHistory.SetPreviousHistoryKey(parent.GetPreviousHistoryKey());

            // Append to parent's child node collection
            parent.GetEntityFieldHistories().add(fieldHistory._entityFieldHistory);

            // Add to Parent's Child Collection
            if (!parent._childDataObjects.containsKey(fieldHistory.GetKey()))
            {
                parent._childDataObjects.put(fieldHistory.GetKey(), fieldHistory);
            }

            return fieldHistory;

        }
        catch (Exception ex)
        {
            return null;
        }
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
        return _entityFieldHistory.getName();
    }

    @Override
    public void SetName(String value)
    {
        _entityFieldHistory.setName(value);
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
        return _entityFieldHistory.getDatatype();
    }

    public void SetDataType(String value)
    {
        _entityFieldHistory.setDatatype(value);
    }

    public String GetLabel()
    {
        return _entityFieldHistory.getLabel();
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
        return _entityFieldHistory.getModifiedby();
    }

    public void SetModifiedBy(String value)
    {
        _entityFieldHistory.setModifiedby(value);
    }

    public String GetModifiedByIP()
    {
        return _entityFieldHistory.getModifiedbyip();
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
        if (prevHistKey.equals(""))
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
        return _entityFieldHistory.getFilename();
    }

    public void SetFilename(String value)
    {
        String oldFilename = _entityFieldHistory.getFilename();

        _entityFieldHistory.setFilename(value);
        SetChanged(oldFilename, value);
    }

    public String GetExtension()
    {
        return _entityFieldHistory.getExtension();
    }

    public void SetExtension(String value)
    {
        String oldExtension = _entityFieldHistory.getExtension();

        _entityFieldHistory.setExtension(value.replace(".", ""));
        SetChanged(oldExtension, value);
    }

    public String GetMimeType()
    {
        return _entityFieldHistory.getMimetype();
    }

    public void SetMimeType(String value)
    {
        _entityFieldHistory.setMimetype(value);
    }

    public String GetHash()
    {
        return _entityFieldHistory.getHash();
    }

    public void SetHash(String value)
    {
        String oldHash = _entityFieldHistory.getHash();

        _entityFieldHistory.setHash(value);
        SetChanged(oldHash, value);
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

    private CallResult SetAttributes(Field field)
    {
        try
        {

            _entityFieldHistory.setKey(field.getKey());
            _entityFieldHistory.setName(field.getName());
            _entityFieldHistory.setValue(field.getValue());
            _entityFieldHistory.setDatatype(field.getDatatype());
            _entityFieldHistory.setLabel(field.getLabel());
            _entityFieldHistory.setSize(field.getSize());
            _entityFieldHistory.setModifiedby(field.getModifiedby());
            _entityFieldHistory.setModifiedbyip(field.getModifiedbyip());
            _entityFieldHistory.setClassificationmarking(field.getClassificationmarking());
            _entityFieldHistory.setPrevioushistorykey(field.getPrevioushistorykey());
            _entityFieldHistory.setFilename(field.getFilename());
            _entityFieldHistory.setExtension(field.getExtension());
            _entityFieldHistory.setMimetype(field.getMimetype());
            _entityFieldHistory.setHash(field.getHash());
            _entityFieldHistory.setValue(field.getValue());
            _entityFieldHistory.setDatecreated(field.getDatecreated());
            _entityFieldHistory.setLastmodified(field.getLastmodified());
            _entityFieldHistory.setStatus(field.getStatus());

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
