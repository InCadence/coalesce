package Coalesce.Framework.DataModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import Coalesce.Common.Helpers.FileHelper;
import Coalesce.Common.Helpers.GUIDHelper;
import Coalesce.Common.Helpers.JodaDateTimeHelper;
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

public class XsdField extends XsdFieldBase {

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private boolean _suspendHistory = false;
    protected Field _entityField;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates an XsdField based off of an XsdFieldDefinition and ties it to its parent XsdRecord.
     * 
     * @param parent            XsdRecord
     * @param fieldDefinition   XsdFieldDefinition
     * 
     * @return                  XsdField
     */
    public static XsdField create(XsdRecord parent, XsdFieldDefinition fieldDefinition)
    {

        Field newEntityField = new Field();
        parent.getEntityFields().add(newEntityField);

        XsdField newField = new XsdField();
        if (!newField.initialize(parent, newEntityField)) return null;

        newField.setSuspendHistory(true);

        newField.setName(fieldDefinition.getName());
        newField.setDataType(fieldDefinition.getDataType());
        newField.setValue(fieldDefinition.getDefaultValue());
        newField.setClassificationMarking(fieldDefinition.getDefaultClassificationMarking());
        newField.setLabel(fieldDefinition.getLabel());
        newField.setNoIndex(fieldDefinition.getNoIndex());

        newField.setSuspendHistory(false);

        // Boolean Type? If so then default initial value to false if not a boolean default value.
        if (fieldDefinition.getDataType() == ECoalesceFieldDataTypes.BooleanType
                && !(newField.getValue().equalsIgnoreCase("true") || newField.getValue().equalsIgnoreCase("false")))
        {
            newField.setValue("false");
        }

        // Add to Parent's Child Collection
        if (!(parent._childDataObjects.containsKey(newField.getKey())))
        {
            parent._childDataObjects.put(newField.getKey(), newField);
        }

        return newField;

    }

    /**
     * Initializes a previously new Field and ties it to its parent XsdRecord.
     * 
     * @param parent    XsdRecord
     * @param field     Field
     * 
     * @return          boolean
     */
    public boolean initialize(XsdRecord parent, Field field)
    {

        // Set References
        _parent = parent;
        _entityField = field;

        super.initialize();

        for (Fieldhistory entityFieldHistory : _entityField.getFieldhistory())
        {

            XsdFieldHistory fieldHistory = new XsdFieldHistory();
            fieldHistory.initialize(this, entityFieldHistory);

            // Add to Child Collection
            _childDataObjects.put(fieldHistory.getKey(), fieldHistory);
        }

        return true;

    }

    // -----------------------------------------------------------------------//
    // Public Properties
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectKey()
    {
        return _entityField.getKey();
    }

    @Override
    public void setObjectKey(String value)
    {
        _entityField.setKey(value);
    }

    @Override
    public String getName()
    {
        return getStringElement(_entityField.getName());
    }

    @Override
    public void setName(String value)
    {
        _entityField.setName(value);
    }

    @Override
    public String getType()
    {
        return "field";
    }

    @Override
    public String getValue()
    {
        return _entityField.getValue();
    }

    @Override
    public void setValue(String value)
    {
        String oldValue = _entityField.getValue();

        setChanged(oldValue, value);
        _entityField.setValue(value);
    }

    @Override
    public ECoalesceFieldDataTypes getDataType()
    {
        return ECoalesceFieldDataTypes.getTypeForCoalesceType(_entityField.getDatatype());
    }

    @Override
    public void setDataType(ECoalesceFieldDataTypes value)
    {
        _entityField.setDatatype(value.getLabel());
    }

    @Override
    public String getLabel()
    {
        return getStringElement(_entityField.getLabel());
    }

    @Override
    public void setLabel(String value)
    {
        _entityField.setLabel(value);
    }

    @Override
    public int getSize()
    {
        try
        {
            return Integer.parseInt(_entityField.getSize());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    @Override
    public void setSize(int value)
    {
        _entityField.setSize(Integer.toString(value));
    }

    @Override
    public String getModifiedBy()
    {
        return getStringElement(_entityField.getModifiedby());
    }

    @Override
    public void setModifiedBy(String value)
    {
        _entityField.setModifiedby(value);
    }

    @Override
    public String getModifiedByIP()
    {
        return getStringElement(_entityField.getModifiedbyip());
    }

    @Override
    public void setModifiedByIP(String value)
    {
        _entityField.setModifiedbyip(value);
    }

    @Override
    public String getClassificationMarkingAsString()
    {
        return _entityField.getClassificationmarking();
    }

    @Override
    public void setClassificationMarking(String value)
    {
        String oldValue = _entityField.getClassificationmarking();

        setChanged(oldValue, value);
        _entityField.setClassificationmarking(value);
    }

    @Override
    public String getPreviousHistoryKey()
    {
        String prevHistKey = _entityField.getPrevioushistorykey();
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
        _entityField.setPrevioushistorykey(value);
    }

    @Override
    public String getFilename()
    {
        return getStringElement(_entityField.getFilename());
    }

    @Override
    public void setFilename(String value)
    {
        String oldFilename = _entityField.getFilename();

        setChanged(oldFilename, value);
        _entityField.setFilename(value);
    }

    @Override
    public String getExtension()
    {
        return getStringElement(_entityField.getExtension());
    }

    @Override
    public void setExtension(String value)
    {
        String oldExtension = _entityField.getExtension();

        setChanged(oldExtension, value);
        _entityField.setExtension(value.replace(".", ""));
    }

    @Override
    public String getMimeType()
    {
        return getStringElement(_entityField.getMimetype());
    }

    @Override
    public void setMimeType(String value)
    {
        _entityField.setMimetype(value);
    }

    @Override
    public String getHash()
    {
        return getStringElement(_entityField.getHash());
    }

    @Override
    public void setHash(String value)
    {
        String oldHash = _entityField.getHash();

        setChanged(oldHash, value);
        _entityField.setHash(value);
    }

    /**
     * Returns the XsdField's suspendHistory value suspendHistory value.
     * 
     * @return boolean, XsdField's suspendHistory value
     */
    public boolean getSuspendHistory()
    {
        return _suspendHistory;
    }

    /**
     * Sets the XsdField's suspendHistory value.
     * 
     * @param value boolean, new value for the XsdField's suspendHistory value
     */
    public void setSuspendHistory(boolean value)
    {
        _suspendHistory = value;
    }

    /**
     * Returns the XsdField's change history collection
     * 
     * @return  ArrayList<XsdFieldHistory>
     */
    public ArrayList<XsdFieldHistory> getHistory()
    {

        ArrayList<XsdFieldHistory> historyList = new ArrayList<XsdFieldHistory>();

        // Return history items in the same order they are in the Entity
        for (Fieldhistory fh : _entityField.getFieldhistory())
        {
            XsdDataObject fdo = _childDataObjects.get(fh.getKey());

            if (fdo != null && fdo instanceof XsdFieldHistory)
            {
                historyList.add((XsdFieldHistory) _childDataObjects.get(fh.getKey()));
            }
        }

        return historyList;
    }

    /**
     * Returns an XsdField's change history entry 
     * 
     * @return  XsdFieldHistory
     */
    public XsdFieldHistory getHistoryRecord(String historyKey)
    {
        XsdFieldHistory historyRecord = (XsdFieldHistory) _childDataObjects.get(historyKey);

        return historyRecord;

    }

    @Override
    public DateTime getDateCreated()
    {

        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityField.getDatecreated());
        return _entityField.getDatecreated();
    }

    @Override
    public void setDateCreated(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityField.setDatecreated(value);
    }

    @Override
    public DateTime getLastModified()
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").parse(_entityField.getLastmodified());
        return _entityField.getLastmodified();
    }

    @Override
    protected void setObjectLastModified(DateTime value)
    {
        // SimpleDateFormat("yyyy-MMM-dd HH:mm:ssZ").format(value));
        _entityField.setLastmodified(value);
    }
    
    @Override
    public String toXml()
    {
        return XmlHelper.Serialize(_entityField);
    }

    /**
     * Returns the filename with directory path and file extension.
     * 
     * @return  String, full filename
     */
    public String getCoalesceFullFilename()
    {

        if (getDataType() != ECoalesceFieldDataTypes.FileType)
        {
            return "";
        }

        String baseFilename = FileHelper.getBaseFilenameWithFullDirectoryPathForKey(getKey());

        return baseFilename + "." + getExtension();

    }

    /**
     * Returns the filename with directory path and file extension for a thumbnail image.
     * 
     * @return
     */
    public String getCoalesceFullThumbnailFilename()
    {

        if (getDataType() != ECoalesceFieldDataTypes.FileType)
        {
            return "";
        }

        String baseFilename = FileHelper.getBaseFilenameWithFullDirectoryPathForKey(getKey());

        return baseFilename + "_thumb.jpg";

    }

    /**
     * Returns the filename with a long representation of last modified datetime (Name?lastmodifiedlong).
     * Returns empty string when filename does not exist. If an error is encountered, only the filename is returned.
     * 
     * @return String
     */
    public String getCoalesceFilenameWithLastModifiedTag()
    {
        try
        {
            String fullPath = getCoalesceFullFilename();
            if (StringHelper.IsNullOrEmpty(fullPath)) return "";

            File theFile = new File(fullPath);
            long lastModifiedTicks = theFile.lastModified();

            return theFile.getName() + "?" + lastModifiedTicks;

        }
        catch (Exception ex)
        {
            return getCoalesceFilename();
        }
    }

    /**
     * Returns the tumbnail filename with a long representation of last modified datetime (Name?lastmodifiedlong).
     * Returns empty string when filename does not exist. If an error is encountered, only the thumbnail filename is returned.
     * 
     * @return String
     */
    public String getCoalesceThumbnailFilenameWithLastModifiedTag()
    {
        try
        {
            String fullThumbPath = getCoalesceFullThumbnailFilename();
            if (StringHelper.IsNullOrEmpty(fullThumbPath)) return "";

            File theFile = new File(fullThumbPath);
            long lastModifiedTicks = theFile.lastModified();

            return theFile.getName() + "?" + lastModifiedTicks;

        }
        catch (Exception ex)
        {
            return getCoalesceThumbnailFilename();
        }
    }

    /**
     * Returns the base filename and extension.
     * 
     * @return String
     */
    public String getCoalesceFilename()
    {

        if (getDataType() == ECoalesceFieldDataTypes.FileType)
        {

            String baseFilename = getKey();
            baseFilename = GUIDHelper.removeBrackets(baseFilename);

            return baseFilename + "." + getExtension();

        }
        else
        {
            return "";
        }
    }

    /**
     * Returns the thumbnail base filename and extension.
     * 
     * @return String
     */
    public String getCoalesceThumbnailFilename()
    {

        if (getDataType() == ECoalesceFieldDataTypes.FileType)
        {

            String baseFilename = getKey();
            baseFilename = GUIDHelper.removeBrackets(baseFilename);

            return baseFilename + "_thumb.jpg";

        }
        else
        {
            return "";
        }
    }

    /**
     * Update the value and/or classification marking of the XsdField.
     * 
     * @param value     String, value contained by the field
     * @param marking   classification marking of the field
     * @param user      user making the change
     * @param ip        user ip responsible for the change
     */
    public void change(String value, String marking, String user, String ip)
    {
        // Does the new value differ from the existing?
        if (!(getValue().equals(value) && getClassificationMarking().equals(marking)))
        {

            // Yes; should we create a FieldHistory entry to reflect the
            // change?
            // We create FieldHistory entry if History is not Suspended; OR
            // if DataType is binary; OR if DateCreated=LastModified and
            // Value is unset
            if (!getSuspendHistory())
            {

                // Does LastModified = DateCreated?
                if (getLastModified().compareTo(getDateCreated()) != 0)
                {
                    // CoalesceFieldHistory to create another.
                    setPreviousHistoryKey(XsdFieldHistory.create(this));
                }
            }

            // Change Values
            if (getDataType() == ECoalesceFieldDataTypes.DateTimeType && !StringHelper.IsNullOrEmpty(value))
            {

                DateTime valueDate = JodaDateTimeHelper.fromXmlDateTimeUTC(value);

                setTypedValue(valueDate);

            }
            else
            {
                setValue(value);
            }

            setClassificationMarking(marking);
            setModifiedBy(user);
            setModifiedByIP(ip);

            // Set LastModified
            DateTime utcNow = JodaDateTimeHelper.nowInUtc().plusSeconds(1);
            if (utcNow != null) setLastModified(utcNow);

        }
    }

    // -----------------------------------------------------------------------//
    // protected Methods
    // -----------------------------------------------------------------------//

    @Override
    protected String getObjectStatus()
    {
        return _entityField.getStatus();
    }

    @Override
    protected void setObjectStatus(ECoalesceDataObjectStatus status)
    {
        _entityField.setStatus(status.getLabel());

    }

    // -----------------------------------------------------------------------//
    // protected Methods
    // -----------------------------------------------------------------------//

    protected void setChanged(Object oldValue, Object newValue)
    {
        // Does the new value differ from the existing?
        if ((oldValue == null && newValue != null) || !oldValue.equals(newValue))
        {

            // Yes; should we create a FieldHistory entry to reflect the
            // change?
            // We create FieldHistory entry if History is not Suspended; OR
            // if DataType is binary; OR if DateCreated=LastModified and
            // Value is unset
            if (!getSuspendHistory())
            {

                switch (getDataType()) {

                case BinaryType:
                case FileType:
                    // Don't Create History Entry for these types
                    break;
                default:

                    // Does LastModified = DateCreated?
                    if (getLastModified().compareTo(getDateCreated()) != 0)
                    {

                        // No; Create History Entry
                        setPreviousHistoryKey(XsdFieldHistory.create(this));
                    }

                }

            }

            // Set LastModified
            DateTime utcNow = JodaDateTimeHelper.nowInUtc();
            if (utcNow != null) setLastModified(utcNow);

        }
    }

    protected List<Fieldhistory> GetEntityFieldHistories()
    {
        return _entityField.getFieldhistory();
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = new HashMap<QName, String>();
        map.put(new QName("key"), _entityField.getKey());
        map.put(new QName("datecreated"), JodaDateTimeHelper.toXmlDateTimeUTC(_entityField.getDatecreated()));
        map.put(new QName("lastmodified"), JodaDateTimeHelper.toXmlDateTimeUTC(_entityField.getLastmodified()));
        map.put(new QName("name"), _entityField.getName());
        map.put(new QName("datatype"), _entityField.getDatatype());
        map.put(new QName("classificationmarking"), _entityField.getClassificationmarking());
        map.put(new QName("label"), _entityField.getLabel());
        map.put(new QName("value"), _entityField.getValue());
        map.put(new QName("status"), _entityField.getStatus());
        return map;
    }

    @Override
    public boolean setAttribute(String name, String value)
    {
        switch (name) {
        case "key":
            _entityField.setKey(value);
            return true;
        case "datecreated":
            _entityField.setDatecreated(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            _entityField.setLastmodified(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "name":
            _entityField.setName(value);
            return true;
        case "datatype":
            _entityField.setDatatype(value);
            return true;
        case "classificationmarking":
            _entityField.setClassificationmarking(value);
            return true;
        case "label":
            _entityField.setLabel(value);
            return true;
        case "value":
            _entityField.setValue(value);
            return true;
        case "status":
            _entityField.setStatus(value);
            return true;
        default:
            this.setOtherAttribute(name, value);
            return true;
        }
    }

    @Override
    protected Map<QName, String> getOtherAttributes()
    {
        return _entityField.getOtherAttributes();
    }

}
