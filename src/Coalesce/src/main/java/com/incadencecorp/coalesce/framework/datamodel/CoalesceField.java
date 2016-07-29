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

package com.incadencecorp.coalesce.framework.datamodel;

import java.io.File;
import java.util.ArrayList;
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
import com.incadencecorp.coalesce.common.helpers.LocaleConverter;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Base class providing common functionality across the different data types.
 * 
 * @author n78554
 *
 * @param <T>
 */
public class CoalesceField<T> extends CoalesceFieldBase<T> implements ICoalesceObjectHistory {

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private boolean _suspendHistory = false;
    private Field _entityField;
    private CoalesceFieldDefinition _definition;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}
     * based off of an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     * and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}
     *            will belong to.
     * @param fieldDefinition
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition}
     *            "template" that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}
     *            will be based off of, for default values/settings.
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}
     *         , belonging to the parent
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}
     *         , resulting from the fieldDefinition.
     */
    protected static CoalesceField<?> create(CoalesceRecord parent, CoalesceFieldDefinition fieldDefinition)
    {

        Field newEntityField = new Field();
        parent.getEntityFields().add(newEntityField);

        CoalesceField<?> newField = createTypeField(fieldDefinition.getDataType());
        if (!newField.initialize(parent, fieldDefinition, newEntityField))
            return null;

        newField.setSuspendHistory(true);
        newField.setName(fieldDefinition.getName());
        newField.setDataType(fieldDefinition.getDataType());

        // Is Default Value Null?
        if (fieldDefinition.getDefaultValue() != null)
        {
            // No; set value; Otherwise leave null to indicate value has never
            // been set (Used to
            // determine if history should
            // be created).
            newField.setBaseValue(fieldDefinition.getDefaultValue());
        }

        newField.setClassificationMarking(fieldDefinition.getDefaultClassificationMarking());
        newField.setLabel(fieldDefinition.getLabel());
        newField.setNoIndex(fieldDefinition.getNoIndex());
        newField.setDisableHistory(fieldDefinition.isDisableHistory());

        newField.setSuspendHistory(false);

        parent.addChildCoalesceObject(newField);

        return newField;

    }

    /**
     * Factory class for initiating the correct template field.
     * 
     * @param dataType
     * @return
     */
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

        case BOOLEAN_LIST_TYPE:
            return new CoalesceBooleanListField();

        case INTEGER_TYPE:
            return new CoalesceIntegerField();

        case GUID_TYPE:
            return new CoalesceGUIDField();

        case GEOCOORDINATE_TYPE:
            return new CoalesceCoordinateField();

        case GEOCOORDINATE_LIST_TYPE:
            return new CoalesceCoordinateListField();

        case POLYGON_TYPE:
            return new CoalescePolygonField();

        case LINE_STRING_TYPE:
            return new CoalesceLineStringField();

        case CIRCLE_TYPE:
            return new CoalesceCircleField();

        case DOUBLE_TYPE:
            return new CoalesceDoubleField();

        case FLOAT_TYPE:
            return new CoalesceFloatField();

        case LONG_TYPE:
            return new CoalesceLongField();

        case DOUBLE_LIST_TYPE:
            return new CoalesceDoubleListField();

        case FLOAT_LIST_TYPE:
            return new CoalesceFloatListField();

        case GUID_LIST_TYPE:
            return new CoalesceGUIDListField();

        case INTEGER_LIST_TYPE:
            return new CoalesceIntegerListField();

        case LONG_LIST_TYPE:
            return new CoalesceLongListField();

        case STRING_LIST_TYPE:
            return new CoalesceStringListField();

        case ENUMERATION_TYPE:
            return new CoalesceEnumerationField();

        case ENUMERATION_LIST_TYPE:
            return new CoalesceEnumerationListField();

        default:
            throw new NotImplementedException(dataType + " not implemented");

        }
    }

    /**
     * Returns an Field's value as type T.
     * 
     * @return Object base type to contain the field's data, which could be any
     *         data type.
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

        case STRING_LIST_TYPE:
            return (T) getArray();

        case DATE_TIME_TYPE:
            return (T) getDateTimeValue();

        case FILE_TYPE:
        case BINARY_TYPE:
            return (T) getBinaryValue();

        case BOOLEAN_TYPE:
            return (T) getBooleanValue();

        case BOOLEAN_LIST_TYPE:
            return (T) getBooleanListValue();

        case ENUMERATION_TYPE:
        case INTEGER_TYPE:
            return (T) getIntegerValue();

        case ENUMERATION_LIST_TYPE:
        case INTEGER_LIST_TYPE:
            return (T) getIntegerListValue();

        case GUID_TYPE:
            return (T) getGuidValue();

        case GUID_LIST_TYPE:
            return (T) getGuidListValue();

        case GEOCOORDINATE_TYPE:
            return (T) getCoordinateValue();

        case GEOCOORDINATE_LIST_TYPE:
            return (T) getCoordinateListValue();

        case LINE_STRING_TYPE:
            return (T) getLineStringValue();

        case POLYGON_TYPE:
            return (T) getPolygonValue();

        case CIRCLE_TYPE:
            return (T) getCircleValue();

        case DOUBLE_TYPE:
            return (T) getDoubleValue();

        case DOUBLE_LIST_TYPE:
            return (T) getDoubleListValue();

        case FLOAT_TYPE:
            return (T) getFloatValue();

        case FLOAT_LIST_TYPE:
            return (T) getFloatListValue();

        case LONG_TYPE:
            return (T) getLongValue();

        case LONG_LIST_TYPE:
            return (T) getLongListValue();

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
        switch (getDataType()) {
        case STRING_TYPE:
        case URI_TYPE:
            setTypedValue((String) value);
            break;

        case STRING_LIST_TYPE:
            setTypedValue((String[]) value);
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

        case BOOLEAN_LIST_TYPE:
            setTypedValue((boolean[]) value);
            break;

        case ENUMERATION_TYPE:
        case INTEGER_TYPE:
            setTypedValue((Integer) value);
            break;

        case ENUMERATION_LIST_TYPE:
        case INTEGER_LIST_TYPE:
            setTypedValue((int[]) value);
            break;

        case GUID_TYPE:
            setTypedValue((UUID) value);
            break;

        case GUID_LIST_TYPE:
            setTypedValue((UUID[]) value);
            break;

        case GEOCOORDINATE_TYPE:
            setTypedValue((Coordinate) value);
            break;

        case GEOCOORDINATE_LIST_TYPE:
            setTypedValue((Coordinate[]) value);
            break;

        case LINE_STRING_TYPE:
            setTypedValue((LineString) value);
            break;

        case POLYGON_TYPE:
            setTypedValue((Polygon) value);
            break;

        case CIRCLE_TYPE:
            setTypedValue((CoalesceCircle) value);
            break;

        case DOUBLE_TYPE:
            setTypedValue((Double) value);
            break;

        case DOUBLE_LIST_TYPE:
            setTypedValue((double[]) value);
            break;

        case FLOAT_TYPE:
            setTypedValue((Float) value);
            break;

        case FLOAT_LIST_TYPE:
            setTypedValue((float[]) value);
            break;

        case LONG_TYPE:
            setTypedValue((Long) value);
            break;

        case LONG_LIST_TYPE:
            setTypedValue((long[]) value);
            break;

        }

    }

    /**
     * Initializes an existing Field and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}.
     * The field may be new, but field history is tied in, in the event that the
     * field is not new.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord}
     *            that the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}
     *            will belong to.
     * @param field Field being initialized.
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceRecord parent, CoalesceFieldDefinition definition, Field field)
    {

        // Set References
        setParent(parent);
        _entityField = field;
        _definition = definition;

        super.initialize(_entityField);

        for (Fieldhistory entityFieldHistory : _entityField.getFieldhistory())
        {

            CoalesceFieldHistory fieldHistory = new CoalesceFieldHistory();
            fieldHistory.initialize(this, entityFieldHistory);

            // Add to Child Collection
            addChildCoalesceObject(fieldHistory.getKey(), fieldHistory);
        }

        return true;

    }

    // -----------------------------------------------------------------------//
    // Public Properties
    // -----------------------------------------------------------------------//

    @Override
    public String getBaseValue()
    {
        return _entityField.getValue();
    }

    @Override
    protected void setBaseValue(String value)
    {
        // Don't Allow null
        if (value == null)
            value = "";

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
     * @return the field definition that created this field.
     */
    public CoalesceFieldDefinition getFieldDefinition()
    {
        return _definition;
    }

    /**
     * @return whether this field is a list type.
     */
    public boolean isListType()
    {
        return getDataType().toString().endsWith(CoalesceSettings.VAR_IS_LIST_TYPE)
                && getDataType() != ECoalesceFieldDataTypes.GEOCOORDINATE_LIST_TYPE;
    }

    /**
     * Sets the value of the Field's DataType attribute.
     * 
     * @param value ECoalesceFieldDataTypes to be the Field's DataType
     *            attribute.
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
    public String getClassificationMarkingAsString()
    {
        return _entityField.getClassificationmarking();
    }

    @Override
    public void setClassificationMarking(String value)
    {
        // Don't Allow null
        if (value == null)
            value = "";

        createHistory(_entityField.getClassificationmarking(), value);
        _entityField.setClassificationmarking(value);
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
        if (value == null)
            value = "";

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
        if (value == null)
            value = "";

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
        if (value == null)
            value = "";

        createHistory(_entityField.getHash(), value);
        _entityField.setHash(value);
    }

    @Override
    public boolean isDisableHistory()
    {
        return getBooleanElement(_entityField.isDisablehistory());
    }

    @Override
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

    @Override
    public boolean isSuspendHistory()
    {
        return (_suspendHistory || isDisableHistory());
    }

    @Override
    public void setSuspendHistory(boolean suspend)
    {
        if (!isDisableHistory())
        {
            _suspendHistory = suspend;
        }
    }

    @Override
    public CoalesceFieldHistory[] getHistory()
    {

        ArrayList<CoalesceFieldHistory> historyList = new ArrayList<CoalesceFieldHistory>();

        // Return history items in the same order they are in the Entity
        for (Fieldhistory fh : _entityField.getFieldhistory())
        {

            CoalesceObject fdo = getChildCoalesceObject(fh.getKey());

            if (fdo != null && fdo instanceof CoalesceFieldHistory)
            {
                historyList.add((CoalesceFieldHistory) getChildCoalesceObject(fh.getKey()));
            }
        }

        return historyList.toArray(new CoalesceFieldHistory[historyList.size()]);
    }

    @Override
    public void clearHistory()
    {
        _entityField.setPrevioushistorykey(null);
        _entityField.getFieldhistory().clear();
    }

    @Override
    public CoalesceFieldHistory getHistoryRecord(String historyKey)
    {
        CoalesceFieldHistory historyRecord = (CoalesceFieldHistory) getChildCoalesceObject(historyKey);

        return historyRecord;

    }

    /**
     * Returns the filename with directory path and file extension.
     * <code>NOTE:</code> This method relies on the configuration settings for
     * both {@link CoalesceSettings#getBinaryFileStoreBasePath()} and
     * {@link CoalesceSettings#getSubDirectoryLength()} to build the directory
     * path.
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
     * Returns the filename with directory path and file extension for a
     * thumbnail image. <code>NOTE:</code> This method relies on the
     * configuration settings for both
     * {@link CoalesceSettings#getBinaryFileStoreBasePath()} and
     * {@link CoalesceSettings#getSubDirectoryLength()} to build the directory
     * path.
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
     * Returns the filename with a long representation of last modified datetime
     * (Name?lastmodifiedlong). Returns empty string when filename does not
     * exist. If an error is encountered, only the filename is returned.
     * 
     * @return String, full filename with LastModifiedTag appended.
     */
    public String getCoalesceFilenameWithLastModifiedTag()
    {
        try
        {
            String fullPath = getCoalesceFullFilename();
            if (StringHelper.isNullOrEmpty(fullPath))
                return "";

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
     * Returns the thumbnail filename with a long representation of last
     * modified datetime (Name?lastmodifiedlong). Returns empty string when
     * filename does not exist. If an error is encountered, only the thumbnail
     * filename is returned.
     * 
     * @return String, full thumbnail filename with LastModifiedTag appended.
     */
    public String getCoalesceThumbnailFilenameWithLastModifiedTag()
    {
        try
        {
            String fullThumbPath = getCoalesceFullThumbnailFilename();
            if (StringHelper.isNullOrEmpty(fullThumbPath))
                return "";

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
     * @throws CoalesceDataFormatException
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

    /**
     * @return an array of values if the field is a list type, array of size 0
     *         if the field is null, or array of one with the basevalue if its a
     *         non list.
     */
    public String[] getBaseValues()
    {

        String[] results;

        if (isListType())
        {
            results = getArray();
        }
        else
        {
            if (!StringHelper.isNullOrEmpty(getBaseValue()))
            {
                results = new String[] {
                    getBaseValue()
                };
            }
            else
            {
                results = new String[0];
            }
        }

        return results;

    }

    // -----------------------------------------------------------------------//
    // Protected Methods
    // -----------------------------------------------------------------------//

    protected Field getBaseField()
    {
        return _entityField;
    }

    @Override
    public void createHistory(String user, String ip, Integer version)
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
                setPreviousHistoryKey(CoalesceFieldHistory.create(this).getKey());
                setModifiedBy(user);
                setModifiedByIP(ip);
                setObjectVersion(version);

                // Suspend Addition History
                setSuspendHistory(true);

            }

        }

    }

    protected void createHistory(Object oldValue, Object newValue)
    {
        // newValue cannot be null, because setBaseValue: 'if (value == null)
        // value = "";'
        if (newValue == null)
        {
            throw new IllegalArgumentException("newValue");
        }

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
                        _entityField.setModifiedby(null);
                        _entityField.setModifiedbyip(null);
                        _entityField.setObjectversion(null);

                    }
                }
            }

            updateLastModified();
        }
    }

    protected List<Fieldhistory> getEntityFieldHistories()
    {
        return _entityField.getFieldhistory();
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = super.getAttributes();

        map.put(new QName("datatype"), _entityField.getDatatype());
        map.put(new QName("classificationmarking"), _entityField.getClassificationmarking());
        map.put(new QName("label"), _entityField.getLabel());
        map.put(new QName("value"), _entityField.getValue());

        if (_entityField.getInputlang() == null)
        {
            map.put(new QName("inputlang"), null);
        }
        else
        {
            map.put(new QName("inputlang"), _entityField.getInputlang().toString());
        }

        return map;
    }

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        boolean isSuccessful = false;

        if (child instanceof Fieldhistory)
        {
            isSuccessful = _entityField.getFieldhistory().remove(child);
        }

        return isSuccessful;
    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {
        switch (name.toLowerCase()) {
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

            if (inputLang == null)
                return false;

            setInputLang(inputLang);

            return true;

        default:
            return setOtherAttribute(name, value);
        }
    }

}
