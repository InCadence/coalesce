package com.incadencecorp.coalesce.framework.datamodel;

import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;

import com.incadencecorp.coalesce.common.helpers.LocaleConverter;

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

public class CoalesceFieldHistory extends CoalesceFieldBase<String> implements ICoalesceHistory {

    // -----------------------------------------------------------------------//
    // protected Member Variables
    // -----------------------------------------------------------------------//

    private Fieldhistory _entityFieldHistory;

    // -----------------------------------------------------------------------//
    // Factory and Initialization
    // -----------------------------------------------------------------------//

    /**
     * Creates an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}
     * and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}
     *            , the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}
     *            's parent.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}
     *         , resulting history created from the
     *         {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}
     *         .
     */
    public static CoalesceFieldHistory create(CoalesceField<?> parent)
    {
        try
        {
            // Set References
            CoalesceFieldHistory newFieldHistory = new CoalesceFieldHistory();
            if (!newFieldHistory.initialize(parent))
                return null;

            // Copy attributes from parent node
            newFieldHistory.setAttributes(parent);
            newFieldHistory.setPreviousHistoryKey(parent.getPreviousHistoryKey());

            // Append to parent's child node collection
            parent.getEntityFieldHistories().add(0, newFieldHistory._entityFieldHistory);
            parent.addChildCoalesceObject(newFieldHistory);

            return newFieldHistory;

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * Initializes a brand new
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}
     * and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}
     *            , the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}
     *            's parent.
     * 
     * @return boolean indicator of success/failure.
     */
    private boolean initialize(CoalesceField<?> parent)
    {
        return initialize(parent, new Fieldhistory());
    }

    /**
     * Initializes a previously new
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}
     * and ties it to its parent
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}.
     * 
     * @param parent {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}
     *            , the
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}
     *            's parent.
     * @param fieldHistory for which this
     *            {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}
     *            will be based on.
     * 
     * @return boolean indicator of success/failure.
     */
    protected boolean initialize(CoalesceField<?> parent, Fieldhistory fieldHistory)
    {

        // Set References
        setParent(parent);
        _entityFieldHistory = fieldHistory;
        if (_entityFieldHistory.getLastmodified() == null)
        {
            _entityFieldHistory.setLastmodified(parent.getLastModified());
        }

        return super.initialize(_entityFieldHistory);

    }

    // -----------------------------------------------------------------------//
    // public Properties
    // -----------------------------------------------------------------------//

    @Override
    public String getValue()
    {
        return getBaseValue();
    }

    @Override
    protected String getBaseValue()
    {
        return _entityFieldHistory.getValue();
    }

    @Override
    public void setValue(String value)
    {
        setBaseValue(value);
    }

    @Override
    protected void setBaseValue(String value)
    {
        _entityFieldHistory.setValue(value);
    }

    @Override
    public Locale getInputLang()
    {
        return _entityFieldHistory.getInputlang();
    }

    @Override
    public void setInputLang(Locale value)
    {
        _entityFieldHistory.setInputlang(value);
    }

    @Override
    public ECoalesceFieldDataTypes getDataType()
    {
        return ECoalesceFieldDataTypes.getTypeForCoalesceType(_entityFieldHistory.getDatatype());
    }

    /**
     * Sets the value of the Field's DataType attribute.
     * 
     * @param value ECoalesceFieldDataTypes to be the Field's DataType
     *            attribute.
     */
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
    public void setSize(int value)
    {
        _entityFieldHistory.setSize(Integer.toString(value));
    }

    @Override
    public String getClassificationMarkingAsString()
    {
        return _entityFieldHistory.getClassificationmarking();
    }

    @Override
    public void setClassificationMarking(String value)
    {
        _entityFieldHistory.setClassificationmarking(value);
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

    // -----------------------------------------------------------------------//
    // protected Methods
    // -----------------------------------------------------------------------//

    private void setAttributes(CoalesceField<?> field)
    {
        Field entityField = field.getBaseField();

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
        _entityFieldHistory.setDatecreated(entityField.getDatecreated());
        _entityFieldHistory.setLastmodified(entityField.getLastmodified());
        _entityFieldHistory.setInputlang(entityField.getInputlang());
        _entityFieldHistory.setStatus(entityField.getStatus());
        _entityFieldHistory.setObjectversion(entityField.getObjectversion());

        for (Map.Entry<QName, String> otherAttr : entityField.getOtherAttributes().entrySet())
        {
            _entityFieldHistory.getOtherAttributes().put(otherAttr.getKey(), otherAttr.getValue());
        }
    }

    @Override
    protected boolean prune(CoalesceObjectType child)
    {
        // This element has no children
        return false;
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
            setValue(value);
            return true;
        case "inputlang":

            Locale inputLang = LocaleConverter.parseLocale(value);

            if (inputLang == null)
                return false;

            setInputLang(inputLang);

            return true;

        case "previoushistorykey":
            setPreviousHistoryKey(value);
            return true;
        default:
            return setOtherAttribute(name, value);
        }
    }

    @Override
    protected Map<QName, String> getAttributes()
    {
        Map<QName, String> map = super.getAttributes();

        map.put(new QName("datatype"), _entityFieldHistory.getDatatype());
        map.put(new QName("classificationmarking"), _entityFieldHistory.getClassificationmarking());
        map.put(new QName("label"), _entityFieldHistory.getLabel());
        map.put(new QName("value"), _entityFieldHistory.getValue());

        if (_entityFieldHistory.getInputlang() == null)
        {
            map.put(new QName("inputlang"), null);
        }
        else
        {
            map.put(new QName("inputlang"), _entityFieldHistory.getInputlang().toString());
        }

        map.put(new QName("previoushistorykey"), _entityFieldHistory.getPrevioushistorykey());
        return map;
    }

}
