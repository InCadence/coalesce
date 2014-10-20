package com.incadencecorp.coalesce.framework.datamodel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.classification.Marking;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.FileHelper;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.common.helpers.LocaleConverter;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.helpers.XmlHelper;
import com.incadencecorp.coalesce.common.runtime.CoalesceSettings;
import com.incadencecorp.coalesce.framework.generatedjaxb.Field;
import com.incadencecorp.coalesce.framework.generatedjaxb.Fieldhistory;
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
    private Field _entityField;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} based off of an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord} that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} will belong to.
     * @param fieldDefinition {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition} "template" that
     *            the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} will be based off of, for default
     *            values/settings.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}, belonging to the parent
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}, resulting from the fieldDefinition.
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

        // Is Default Value Null?
        if (fieldDefinition.getDefaultValue() != null)
        {
            // No; set value; Otherwise leave null to indicate value has never been set (Used to determine if history should
            // be created).
            newField.setBaseValue(fieldDefinition.getDefaultValue());
        }

        newField.setClassificationMarking(fieldDefinition.getDefaultClassificationMarking());
        newField.setLabel(fieldDefinition.getLabel());
        newField.setNoIndex(fieldDefinition.getNoIndex());
        newField.setDisableHistory(fieldDefinition.isDisableHistory());

        newField.setSuspendHistory(false);

        parent.addChild(newField);

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

        case DOUBLE_TYPE:
            return new CoalesceDoubleField();

        case FLOAT_TYPE:
            return new CoalesceFloatField();

        default:
            throw new NotImplementedException(dataType + " not implemented");
        }
    }

    /**
     * Returns an Field's value as type T.
     * 
     * @return Object base type to contain the field's data, which could be any data type.
     * @throws CoalesceDataFormatException.
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

        case DOUBLE_TYPE:
            return (T) getDoubleValue();

        case FLOAT_TYPE:
            return (T) getFloatValue();

        default:
            throw new NotImplementedException(getDataType() + " not implemented");
        }
    }

    /**
     * Sets the Field's value as type T.
     * 
     * @throws CoalesceDataFormatException.
     */
    @Override
    public void setValue(T value) throws CoalesceDataFormatException
    {
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

        case DOUBLE_TYPE:
            setTypedValue((Double) value);

        case FLOAT_TYPE:
            setTypedValue((Float) value);

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
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField} will belong to.
     * @param field Field being initialized.
     * 
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceRecord parent, Field field)
    {

        // Set References
        setParent(parent);
        _entityField = field;

        super.initialize();

        for (Fieldhistory entityFieldHistory : _entityField.getFieldhistory())
        {

            CoalesceFieldHistory fieldHistory = new CoalesceFieldHistory();
            fieldHistory.initialize(this, entityFieldHistory);

            // Add to Child Collection
            setChildDataObject(fieldHistory.getKey(), fieldHistory);
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
        // Don't Allow null
        if (value == null) value = "";

        createHistory(_entityField.getValue(), value);

        _entityField.setValue(value);
    }

    @Override
    public Locale getInputLang()
    {
        return _entityField.getInputlang();
    }

    @Override
    public void setInputLang(Locale value)
    {
        _entityField.setInputlang(value);
    }

    @Override
    public ECoalesceFieldDataTypes getDataType()
    {
        return ECoalesceFieldDataTypes.getTypeForCoalesceType(_entityField.getDatatype());
    }

    /**
     * Sets the value of the Field's DataType attribute.
     * 
     * @param value ECoalesceFieldDataTypes to be the Field's DataType attribute.
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
        // Don't Allow null
        if (value == null) value = "";

        createHistory(_entityField.getClassificationmarking(), value);
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
        // Don't Allow null
        if (value == null) value = "";

        createHistory(_entityField.getFilename(), value);
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
        // Don't Allow null
        if (value == null) value = "";

        createHistory(_entityField.getExtension(), value);
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
        // Don't Allow null
        if (value == null) value = "";

        createHistory(_entityField.getHash(), value);
        _entityField.setHash(value);
    }

    /**
     * Returns the value indicating if history is disabled for this field. Unlike SuspendHistory, this value is persisted
     * with the object. This value overrides the SuspendHistory value.
     * 
     * @return <code>true</code> if history is to be disabled.
     */
    public boolean isDisableHistory()
    {
        return getBooleanElement(_entityField.isDisablehistory());
    }

    /**
     * Sets the value indicating if history will be disabled for this field. Unlike SuspendHistory, this value is persisted
     * with the object. The setting of this value overrides the SuspendHistory value.
     * 
     * @param disable the value to set the disable hsitory attribute to.
     */
    public void setDisableHistory(boolean disable)
    {
        if (disable)
        {
            _entityField.setDisablehistory(disable);
        }
        else
        {
            _entityField.setDisablehistory(null);
        }

        _suspendHistory = disable;

    }

    /**
     * <code>true<code> if history is currently being suspended for this field
     * value.
     * 
     * @return <code>true<code> if history is currently being suspended for this field.
     */
    public boolean isSuspendHistory()
    {
        return (_suspendHistory || isDisableHistory());
    }

    /**
     * Sets the value indicating if history should be maintained for changes made to this object instance of a field. This
     * setting is not persisted with the field. The value of the fields disablehistory attribute (
     * {@link CoalesceField#getDisableHistory()}) overrides this temporary suspension.
     * 
     * @param suspend the value indicating if history should be temporarily suspended.
     */
    public void setSuspendHistory(boolean suspend)
    {
        if (!isDisableHistory())
        {
            _suspendHistory = suspend;
        }
    }

    /**
     * Returns the {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}'s change history collection.
     * 
     * @return ArrayList<{@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}> all modification
     *         history of this field.
     */
    public ArrayList<CoalesceFieldHistory> getHistory()
    {

        ArrayList<CoalesceFieldHistory> historyList = new ArrayList<CoalesceFieldHistory>();

        // Return history items in the same order they are in the Entity
        for (Fieldhistory fh : _entityField.getFieldhistory())
        {

            CoalesceDataObject fdo = getChildDataObject(fh.getKey());

            if (fdo != null && fdo instanceof CoalesceFieldHistory)
            {
                historyList.add((CoalesceFieldHistory) getChildDataObject(fh.getKey()));
            }
        }

        return historyList;
    }

    /**
     * Returns an {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}'s change history entry.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory} the modification history of this
     *         field with matching key.
     */
    public CoalesceFieldHistory getHistoryRecord(String historyKey)
    {
        CoalesceFieldHistory historyRecord = (CoalesceFieldHistory) getChildDataObject(historyKey);

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
     * @return String, full filename.
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
     * @return String, full thumbnail filename.
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
     * @return String, full filename with LastModifiedTag appended.
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
     * @return String, full thumbnail filename with LastModifiedTag appended.
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
     * @return String, the filename and extension, without the path.
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
     * @return String, the thumbnail's filename and extension, without the path.
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
     * @param value String, value contained by the field.
     * @param marking classification marking of the field.
     * @param user user making the change.
     * @param ip user ip responsible for the change.
     * @throws CoalesceDataFormatException.
     */
    public void change(T value, Marking marking, String user, String ip) throws CoalesceDataFormatException
    {
        // Does the new value differ from the existing?
        if (!(getBaseValue().equals(value) && getClassificationMarking().equals(marking)))
        {
            // Change Values
            setValue(value);
            setClassificationMarking(marking);
            setModifiedBy(user);
            setModifiedByIP(ip);
        }
    }

    // -----------------------------------------------------------------------//
    // protected Override Methods
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
    // Protected Methods
    // -----------------------------------------------------------------------//

    protected Field getBaseField()
    {
        return _entityField;
    }

    protected void createHistory(Object oldValue, Object newValue)
    {
        // newValue cannot be null, because setBaseValue: 'if (value == null) value = "";'
        if (newValue == null) throw new IllegalArgumentException("newValue");

        // Has Value Changed?
        if (!newValue.equals(oldValue))
        {
            // Initial Value?
            if (oldValue != null)
            {
                // No; History Suspended?
                if (!isSuspendHistory())
                {
                    // No; Check Type
                    switch (getDataType()) {
                    case BINARY_TYPE:
                    case FILE_TYPE:
                        // Don't Create History Entry for these types
                        break;
                    default:
                        // Add History
                        setPreviousHistoryKey(CoalesceFieldHistory.create(this));
                    }
                }
            }

            // Update Last Modified
            setLastModified(JodaDateTimeHelper.nowInUtc());
        }
    }

    protected List<Fieldhistory> getEntityFieldHistories()
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
        
        if (_entityField.getInputlang() == null)
        {
            map.put(new QName("inputlang"), null);
        } else {
            map.put(new QName("inputlang"), _entityField.getInputlang().toString());
        }
        map.put(new QName("status"), _entityField.getStatus());
        return map;
    }

    @Override
    public boolean setAttribute(String name, String value)
    {
        switch (name.toLowerCase()) {
        case "key":
            setKey(value);
            return true;
        case "datecreated":
            setDateCreated(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "lastmodified":
            setLastModified(JodaDateTimeHelper.fromXmlDateTimeUTC(value));
            return true;
        case "name":
            setName(value);
            return true;
        case "datatype":
            setDataType(ECoalesceFieldDataTypes.getTypeForCoalesceType(value));
            return true;
        case "classificationmarking":
            setClassificationMarking(value);
            return true;
        case "label":
            setLabel(value);
            return true;
        case "value":
            setBaseValue(value);
            return true;
        case "inputlang":

            Locale inputLang = LocaleConverter.parseLocale(value);

            if (inputLang == null) return false;

            setInputLang(inputLang);

            return true;

        case "status":
            setStatus(ECoalesceDataObjectStatus.getTypeForLabel(value));
            return true;
        default:
            return setOtherAttribute(name, value);
        }
    }

    @Override
    protected Map<QName, String> getOtherAttributes()
    {
        return _entityField.getOtherAttributes();
    }

}
