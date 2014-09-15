package Coalesce.Framework.DataModel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

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

            newFieldHistory.setPreviousHistoryKey(parent.getPreviousHistoryKey());

            // Append to parent's child node collection
            parent.GetEntityFieldHistories().add(0, newFieldHistory._entityFieldHistory);

            // Add to Parent's Child Collection
            if (!parent._childDataObjects.containsKey(newFieldHistory.getKey()))
            {
                parent._childDataObjects.put(newFieldHistory.getKey(), newFieldHistory);
            }

            return newFieldHistory;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private boolean Initialize(XsdField parent)
    {
        return Initialize(parent, new Fieldhistory());
    }

    public boolean Initialize(XsdField parent, Fieldhistory fieldHistory)
    {

        // Set References
        _parent = parent;
        _entityFieldHistory = fieldHistory;

        return super.initialize();

    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectKey()
    {
        return _entityFieldHistory.getKey();
    }

    @Override
    public void setObjectKey(String value)
    {
        _entityFieldHistory.setKey(value);
    }

    @Override
    public String getName()
    {
        return getStringElement(_entityFieldHistory.getName());
    }

    @Override
    public void setName(String value)
    {
        _entityFieldHistory.setName(value);
    }

    @Override
    public String getType()
    {
        return "fieldhistory";
    }

    @Override
    public String getValue()
    {
        return _entityFieldHistory.getValue();
    }

    @Override
    public void setValue(String value)
    {
        _entityFieldHistory.setValue(value);
    }

    @Override
    public ECoalesceFieldDataTypes getDataType()
    {
        return ECoalesceFieldDataTypes.GetTypeForCoalesceType(_entityFieldHistory.getDatatype());
    }

    @Override
    public void setDataType(ECoalesceFieldDataTypes value)
    {
        _entityFieldHistory.setDatatype(value.getLabel());
    }

    @Override
    public String getLabel()
    {
        return getStringElement(_entityFieldHistory.getLabel());
    }

    @Override
    public void setLabel(String value)
    {
        _entityFieldHistory.setLabel(value);
    }

    @Override
    public int getSize()
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

    @Override
    public void setSize(Integer value)
    {
        _entityFieldHistory.setSize(value.toString());
    }

    @Override
    public String getModifiedBy()
    {
        return getStringElement(_entityFieldHistory.getModifiedby());
    }

    @Override
    public void setModifiedBy(String value)
    {
        _entityFieldHistory.setModifiedby(value);
    }

    @Override
    public String getModifiedByIP()
    {
        return getStringElement(_entityFieldHistory.getModifiedbyip());
    }

    @Override
    public void setModifiedByIP(String value)
    {
        _entityFieldHistory.setModifiedbyip(value);
    }

    @Override
    public String getClassificationMarking()
    {
        return _entityFieldHistory.getClassificationmarking();
    }

    @Override
    public void setClassificationMarking(String value)
    {
        _entityFieldHistory.setClassificationmarking(value);
    }

    @Override
    public String getPreviousHistoryKey()
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

    @Override
    public void setPreviousHistoryKey(String value)
    {
        _entityFieldHistory.setPrevioushistorykey(value);
    }

    @Override
    public String getFilename()
    {
        return getStringElement(_entityFieldHistory.getFilename());
    }

    @Override
    public void setFilename(String value)
    {
        _entityFieldHistory.setFilename(value);
    }

    @Override
    public String getExtension()
    {
        return getStringElement(_entityFieldHistory.getExtension());
    }

    @Override
    public void setExtension(String value)
    {
        _entityFieldHistory.setExtension(value.replace(".", ""));
    }

    @Override
    public String getMimeType()
    {
        return getStringElement(_entityFieldHistory.getMimetype());
    }

    @Override
    public void setMimeType(String value)
    {
        _entityFieldHistory.setMimetype(value);
    }

    @Override
    public String getHash()
    {
        return getStringElement(_entityFieldHistory.getHash());
    }

    @Override
    public void setHash(String value)
    {
        _entityFieldHistory.setHash(value);
    }

    @Override
    public DateTime getDateCreated()
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldHistory.getDatecreated());
        return _entityFieldHistory.getDatecreated();
    }

    @Override
    public void setDateCreated(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityFieldHistory.setDatecreated(value);
    }

    @Override
    public DateTime getLastModified()
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityFieldHistory.getLastmodified());
        return _entityFieldHistory.getLastmodified();
    }

    // -----------------------------------------------------------------------//
    // public Methods
    // -----------------------------------------------------------------------//

    public String toXml()
    {
        return XmlHelper.Serialize(_entityFieldHistory);
    }

    // -----------------------------------------------------------------------//
    // protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected void setObjectLastModified(DateTime value)
    {
        _entityFieldHistory.setLastmodified(value);
    }

    private void SetAttributes(XsdField field)
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
    }

    @Override
    protected String getObjectStatus()
    {
        return _entityFieldHistory.getStatus();
    }

    @Override
    protected void setObjectStatus(ECoalesceDataObjectStatus status)
    {
        _entityFieldHistory.setStatus(status.toLabel());
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        return this._entityFieldHistory.getOtherAttributes();
    }

}
