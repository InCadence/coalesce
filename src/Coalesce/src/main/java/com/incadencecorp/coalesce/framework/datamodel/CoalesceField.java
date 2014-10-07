package com.incadencecorp.coalesce.framework.datamodel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.FileHelper;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.common.runtime.CoalesceSettings;
import com.incadencecorp.coalesce.framework.generatedjaxb.Entity.Section.Recordset.Record.Field;
import com.incadencecorp.coalesce.framework.generatedjaxb.Entity.Section.Recordset.Record.Field.Fieldhistory;
import com.vividsolutions.jts.geom.Coordinate;

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

public class CoalesceField<T> extends CoalesceFieldBase<T> {

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private boolean _suspendHistory = false;
    protected Field _entityField;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} based off of an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} will belong to
     * @param fieldDefinition {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} "template" that
     *            the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} will be based off of, for default
     *            values/settings
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}, belonging to the parent
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}, resulting from the fieldDefinition
     */
    protected static CoalesceField<?> create(CoalesceRecord parent, CoalesceFieldDefinition fieldDefinition)
    {

        Field newEntityField = new Field();
        parent.getEntityFields().add(newEntityField);

        CoalesceField<?> newField = createTypeField(fieldDefinition.getDataType());
        if (!newField.initialize(parent, newEntityField)) return null;

        newField.setSuspendHistory(true);

        newField.setName(fieldDefinition.getName());
        newField.setDataType(fieldDefinition.getDataType());
        newField.setBaseValue(fieldDefinition.getDefaultValue());
        newField.setClassificationMarking(fieldDefinition.getDefaultClassificationMarking());
        newField.setLabel(fieldDefinition.getLabel());
        newField.setNoIndex(fieldDefinition.getNoIndex());

        newField.setSuspendHistory(false);

        // Boolean Type? If so then default initial value to false if not a boolean default value.
        if (fieldDefinition.getDataType() == ECoalesceFieldDataTypes.BOOLEAN_TYPE
                && !(newField.getBaseValue().equalsIgnoreCase("true") || newField.getBaseValue().equalsIgnoreCase("false")))
        {
            newField.setBaseValue("false");
        }

        // Add to Parent's Child Collection
        if (!(parent._childDataObjects.containsKey(newField.getKey())))
        {
            parent._childDataObjects.put(newField.getKey(), newField);
        }

        return newField;

    }

    protected static CoalesceField<?> createTypeField(ECoalesceFieldDataTypes dataType)
    {
        switch (dataType) {

        case STRING_TYPE:
        case URI_TYPE:
            return new CoalesceStringField();

        case DATE_TIME_TYPE:
            return new CoalesceDateTimeField();

        case FILE_TYPE:
            return new CoalesceFileField();
            
        case BINARY_TYPE:
            return new CoalesceBinaryField();

        case BOOLEAN_TYPE:
            return new CoalesceBooleanField();

        case INTEGER_TYPE:
            return new CoalesceIntegerField();

        case GUID_TYPE:
            return new CoalesceGUIDField();

        case GEOCOORDINATE_TYPE:
            return new CoalesceCoordinateField();

        case GEOCOORDINATE_LIST_TYPE:
            return new CoalesceCoordinateListField();

        default:
            throw new NotImplementedException(dataType + " not implemented");
        }
    }

    /**
     * Returns an Field's value as type T.
     * 
     * @return Object base type to contain the field's data, which could be any data type
     * @throws CoalesceDataFormatException
     */
    @SuppressWarnings("unchecked")
    @Override
    public T getValue() throws CoalesceDataFormatException
    {

        switch (getDataType()) {
        case STRING_TYPE:
        case URI_TYPE:
            return (T) getBaseValue();

        case DATE_TIME_TYPE:
            return (T) getDateTimeValue();

        case FILE_TYPE:
        case BINARY_TYPE:
            return (T) getBinaryValue();

        case BOOLEAN_TYPE:
            return (T) getBooleanValue();

        case INTEGER_TYPE:
            return (T) getIntegerValue();

        case GUID_TYPE:
            return (T) getGuidValue();

        case GEOCOORDINATE_TYPE:
            return (T) getCoordinateValue();

        case GEOCOORDINATE_LIST_TYPE:
            return (T) getCoordinateListValue();

        default:
            throw new NotImplementedException(getDataType() + " not implemented");
        }
    }

    /**
     * Sets the Field's value as type T.
     * 
     * @throws CoalesceDataFormatException
     */
    @Override
    public void setValue(T value) throws CoalesceDataFormatException
    {
        ;

        switch (getDataType()) {
        case STRING_TYPE:
        case URI_TYPE:
            setTypedValue((String) value);
            break;

        case DATE_TIME_TYPE:
            setTypedValue((DateTime) value);
            break;

        case FILE_TYPE:
        case BINARY_TYPE:
            setTypedValue((byte[]) value);
            break;

        case BOOLEAN_TYPE:
            setTypedValue((Boolean) value);
            break;

        case INTEGER_TYPE:
            setTypedValue((Integer) value);
            break;

        case GUID_TYPE:
            setTypedValue((UUID) value);
            break;

        case GEOCOORDINATE_TYPE:
            setTypedValue((Coordinate) value);
            break;

        case GEOCOORDINATE_LIST_TYPE:
            setTypedValue((Coordinate[]) value);
            break;

        default:
            throw new NotImplementedException(getDataType() + " not implemented");
        }

    }

    /**
     * Initializes an existing Field and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}. The field may be new, but field history is tied
     * in, in the event that the field is not new.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} will belong to
     * @param field Field being initialized
     * 
     * @return boolean indicator of success/failure
     */
    protected boolean initialize(CoalesceRecord parent, Field field)
    {

        // Set References
        _parent = parent;
        _entityField = field;

        super.initialize();

        for (Fieldhistory entityFieldHistory : _entityField.getFieldhistory())
        {

            CoalesceFieldHistory fieldHistory = new CoalesceFieldHistory();
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
    public String getBaseValue()
    {
        return _entityField.getValue();
    }

    @Override
    protected void setBaseValue(String value)
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

    /**
     * Sets the value of the Field's DataType attribute
     * 
     * @param value ECoalesceFieldDataTypes to be the Field's DataType attribute
     */
    private void setDataType(ECoalesceFieldDataTypes value)
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
        if (StringHelper.isNullOrEmpty(prevHistKey))
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
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}'s suspendHistory value suspendHistory
     * value.
     * 
     * @return boolean, {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}'s suspendHistory value
     */
    public boolean getSuspendHistory()
    {
        return _suspendHistory;
    }

    /**
     * Sets the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}'s suspendHistory value.
     * 
     * @param value boolean, new value for the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}'s
     *            suspendHistory value
     */
    public void setSuspendHistory(boolean value)
    {
        _suspendHistory = value;
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}'s change history collection
     * 
     * @return ArrayList<{@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}> all modification history of this field
     */
    public ArrayList<CoalesceFieldHistory> getHistory()
    {

        ArrayList<CoalesceFieldHistory> historyList = new ArrayList<CoalesceFieldHistory>();

        // Return history items in the same order they are in the Entity
        for (Fieldhistory fh : _entityField.getFieldhistory())
        {
            CoalesceDataObject fdo = _childDataObjects.get(fh.getKey());

            if (fdo != null && fdo instanceof CoalesceFieldHistory)
            {
                historyList.add((CoalesceFieldHistory) _childDataObjects.get(fh.getKey()));
            }
        }

        return historyList;
    }

    /**
     * Returns an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}'s change history entry
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory} the modification history of this
     *         field with matching key
     */
    public CoalesceFieldHistory getHistoryRecord(String historyKey)
    {
        CoalesceFieldHistory historyRecord = (CoalesceFieldHistory) _childDataObjects.get(historyKey);

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
        return XmlHelper.serialize(_entityField);
    }

    /**
     * Returns the filename with directory path and file extension.
     * 
     * <code>NOTE:</code> This method relies on the configuration settings for both
     * {@link CoalesceSettings#getBinaryFileStoreBasePath()} and {@link CoalesceSettings#getSubDirectoryLength()} to build
     * the directory path.
     * 
     * @return String, full filename
     */
    public String getCoalesceFullFilename()
    {

        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            return "";
        }

        String baseFilename = FileHelper.getBaseFilenameWithFullDirectoryPathForKey(getKey());

        return baseFilename + "." + getExtension();

    }

    /**
     * Returns the filename with directory path and file extension for a thumbnail image.
     * 
     * <code>NOTE:</code> This method relies on the configuration settings for both
     * {@link CoalesceSettings#getBinaryFileStoreBasePath()} and {@link CoalesceSettings#getSubDirectoryLength()} to build
     * the directory path.
     *
     * @return String, full thumbnail filename
     */
    public String getCoalesceFullThumbnailFilename()
    {

        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            return "";
        }

        String baseFilename = FileHelper.getBaseFilenameWithFullDirectoryPathForKey(getKey());

        return baseFilename + "_thumb.jpg";

    }

    /**
     * Returns the filename with a long representation of last modified datetime (Name?lastmodifiedlong). Returns empty
     * string when filename does not exist. If an error is encountered, only the filename is returned.
     * 
     * @return String, full filename with LastModifiedTag appended
     */
    public String getCoalesceFilenameWithLastModifiedTag()
    {
        try
        {
            String fullPath = getCoalesceFullFilename();
            if (StringHelper.isNullOrEmpty(fullPath)) return "";

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
     * Returns the thumbnail filename with a long representation of last modified datetime (Name?lastmodifiedlong). Returns
     * empty string when filename does not exist. If an error is encountered, only the thumbnail filename is returned.
     * 
     * @return String, full thumbnail filename with LastModifiedTag appended
     */
    public String getCoalesceThumbnailFilenameWithLastModifiedTag()
    {
        try
        {
            String fullThumbPath = getCoalesceFullThumbnailFilename();
            if (StringHelper.isNullOrEmpty(fullThumbPath)) return "";

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
     * @return String, the filename and extension, without the path
     */
    public String getCoalesceFilename()
    {

        if (getDataType() == ECoalesceFieldDataTypes.FILE_TYPE)
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
     * @return String, the thumbnail's filename and extension, without the path
     */
    public String getCoalesceThumbnailFilename()
    {

        if (getDataType() == ECoalesceFieldDataTypes.FILE_TYPE)
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
     * Update the value and/or classification marking of the
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}.
     * 
     * @param value String, value contained by the field
     * @param marking classification marking of the field
     * @param user user making the change
     * @param ip user ip responsible for the change
     */
    public void change(String value, String marking, String user, String ip)
    {
        // Does the new value differ from the existing?
        if (!(getBaseValue().equals(value) && getClassificationMarking().equals(marking)))
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
                    setPreviousHistoryKey(CoalesceFieldHistory.create(this));
                }
            }

            // Change Values
            if (getDataType() == ECoalesceFieldDataTypes.DATE_TIME_TYPE && !StringHelper.isNullOrEmpty(value))
            {

                DateTime valueDate = JodaDateTimeHelper.fromXmlDateTimeUTC(value);

                setTypedValue(valueDate);

            }
            else
            {
                setBaseValue(value);
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
                case BINARY_TYPE:
                case FILE_TYPE:
                    // Don't Create History Entry for these types
                    break;
                default:
                    // Is Original Value?
                    if (getLastModified().compareTo(getDateCreated()) != 0)
                    {
                        // No; Create History Entry
                        setPreviousHistoryKey(CoalesceFieldHistory.create(this));
                    }
                }

                // Set LastModified
                setLastModified(JodaDateTimeHelper.nowInUtc().plusSeconds(1));
            }
            
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
