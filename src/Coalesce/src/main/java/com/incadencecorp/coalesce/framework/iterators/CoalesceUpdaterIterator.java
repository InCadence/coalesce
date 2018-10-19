/*-----------------------------------------------------------------------------'
 Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.iterators;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.EnumerationProviderUtil;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceIntegerField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecord;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceStringListField;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

/**
 * This iterator makes compatible updates to existing entities base off the
 * templates it was constructed with.
 * 
 * @author n78554
 *
 */
public class CoalesceUpdaterIterator extends CoalesceIterator<CoalesceEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceUpdaterIterator.class);

    private CoalesceEntity template;
    private List<String> results;

    /**
     * Default Constructor
     * 
     * @param template
     */
    public CoalesceUpdaterIterator(CoalesceEntityTemplate template)
    {
        this.template = template.createNewEntity();
    }

    /**
     * Updates the provided entity in accordance with the template.
     * 
     * @param entity
     * @return an updated entity
     * @throws CoalesceException
     */
    public CoalesceEntity iterate(final CoalesceEntity entity) throws CoalesceException
    {
        LOGGER.debug("Processing ({}), ({}), ({}), ({}) => ({})",
                     entity.getKey(),
                     entity.getName(),
                     entity.getSource(),
                     entity.getVersion(),
                     template.getVersion());

        CoalesceEntity updated = null;
        results = new ArrayList<>();

        if (entity.getName().equalsIgnoreCase(template.getName())
                && entity.getSource().equalsIgnoreCase(template.getSource()))
        {
            // Clones the entity to avoid modifying the original
            updated = CoalesceEntity.create(entity.toXml());

            processAllElements(template, updated);

            // Need to recreate from XML to re-create fields to their new types
            updated = CoalesceEntity.create(updated.toXml());

            updated.setVersion(template.getVersion());
        }
        else
        {
            addError(CoalesceErrors.INVALID_INPUT_REASON, entity.getName() + ":" + entity.getSource(), template.getName()
                    + ":" + template.getSource());
        }

        return updated;
    }

    /**
     * @return a list of modifications made to the entity.
     */
    public List<String> getUpdates()
    {
        return results;
    }

    @Override
    protected boolean visitCoalesceSection(CoalesceSection section, CoalesceEntity param) throws CoalesceException
    {
        CoalesceSection sectionToUpdate = param.getCoalesceSectionForNamePath(section.getNamePath());

        if (sectionToUpdate == null)
        {
            if (section.getParent() instanceof CoalesceEntity)
            {
                sectionToUpdate = CoalesceSection.create(param, section.getName());
            }
            else
            {
                sectionToUpdate = CoalesceSection.create(param.getCoalesceSectionForNamePath(section.getParent().getNamePath()),
                                                         section.getName());
            }

            addResult("Created Section: (%s)", section.getNamePath());
        }

        return true;
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, CoalesceEntity param) throws CoalesceException
    {
        CoalesceRecordset recordsetToUpdate = param.getCoalesceRecordsetForNamePath(recordset.getNamePath());

        if (recordsetToUpdate == null)
        {
            recordsetToUpdate = CoalesceRecordset.create(param.getCoalesceSectionForNamePath(recordset.getParent().getNamePath()),
                                                         recordset.getName());

            // Add Field Definitions
            for (CoalesceFieldDefinition fd : recordset.getFieldDefinitions())
            {
                CoalesceFieldDefinition.create(recordsetToUpdate, fd.getName(), fd.getDataType());
            }

            addResult("Created Recordset: (%s)", recordset.getNamePath());
        }
        else
        {
            // Verify Field Definitions
            for (CoalesceFieldDefinition fd : recordset.getFieldDefinitions())
            {
                CoalesceFieldDefinition fdToUpdate = recordsetToUpdate.getFieldDefinition(fd.getName());

                if (fdToUpdate == null)
                {
                    // Create Definition
                    CoalesceFieldDefinition.create(recordsetToUpdate, fd.getName(), fd.getDataType());

                    // Verify Records
                    for (CoalesceRecord record : recordsetToUpdate.getRecords())
                    {
                        if (record.getFieldByName(fd.getName()) == null)
                        {
                            LOGGER.warn("(FAILED) Creating Field");
                        }
                        else
                        {
                            addResult("Created Field: (%s) (%s)", fd.getName(), fd.getDataType());
                        }
                    }
                }
                else if (fdToUpdate.getDataType() != fd.getDataType())
                {
                    convert(fdToUpdate, fd);
                }
            }
        }

        return false;
    }

    private void convert(CoalesceFieldDefinition from, CoalesceFieldDefinition to) throws CoalesceException
    {
        boolean isSuccessful;

        from.setDataType(to.getDataType());

        for (CoalesceRecord record : ((CoalesceRecordset) from.getParent()).getRecords())
        {
            CoalesceField<?> field = record.getFieldByName(from.getName());

            String original = field.getBaseValue();
            ECoalesceFieldDataTypes originalType = field.getDataType();

            if (isFromListType(originalType, to.getDataType()))
            {
                isSuccessful = convertFromList(field);
            }
            else if (isToListType(originalType, to.getDataType()))
            {
                isSuccessful = convertToList(field);
            }
            else
            {
                switch (to.getDataType()) {
                case ENUMERATION_TYPE:
                    isSuccessful = convertToEnumeration(field);
                    break;
                case ENUMERATION_LIST_TYPE:
                    isSuccessful = convertToEnumerationList(field);
                    break;
                case STRING_TYPE:
                    isSuccessful = convertToString(field);
                    break;
                default:
                    isSuccessful = false;
                }
            }

            if (isSuccessful)
            {
                field.setAttribute(CoalesceField.ATTRIBUTE_DATA_TYPE, to.getDataType().getLabel());

                addResult("Updated Field (%s}'s Type: (%s:%s) to (%s:%s)",
                          field.getName(),
                          original,
                          originalType,
                          field.getBaseValue(),
                          field.getDataType());
            }
            else
            {
                addError(CoalesceErrors.INVALID_DATA_TYPE,
                         field.getName(),
                         field.getDataType(),
                         field.getEntity().getKey(),
                         to.getDataType());
            }

        }
    }

    private boolean isToListType(ECoalesceFieldDataTypes from, ECoalesceFieldDataTypes to)
    {
        return !from.isListType() && to.isListType() && isSameType(to, from);
    }

    private boolean isFromListType(ECoalesceFieldDataTypes from, ECoalesceFieldDataTypes to)
    {
        return from.isListType() && !to.isListType() && isSameType(from, to);
    }

    private boolean isSameType(ECoalesceFieldDataTypes listtype, ECoalesceFieldDataTypes type)
    {
        String typeString = type.toString();

        return listtype.toString().startsWith(typeString.substring(0, typeString.indexOf("_TYPE")));
    }

    private boolean convertFromList(CoalesceField<?> field) throws CoalesceException
    {
        if (field.getBaseValues().length > 1)
        {
            addError("Field (%s:%s) contains more then one element and cannot be converted to (%s)",
                     field.getName(),
                     field.getDataType().getLabel(),
                     ECoalesceFieldDataTypes.ENUMERATION_TYPE);
        }
        return true;
    }

    private boolean convertToList(CoalesceField<?> field) throws CoalesceException
    {
        // Do Nothing
        return true;
    }

    private boolean convertToString(CoalesceField<?> field) throws CoalesceException
    {
        boolean isSuccessful;

        switch (field.getDataType()) {
        case ENUMERATION_TYPE:
            String enumeration = EnumerationProviderUtil.lookupEnumeration(field);
            field.setAttribute(CoalesceField.ATTRIBUTE_VALUE,
                               EnumerationProviderUtil.toString(null, enumeration, Integer.parseInt(field.getBaseValue())));
            isSuccessful = true;
            break;
        case ENUMERATION_LIST_TYPE:
            isSuccessful = false;
            break;
        default:
            // Do Nothing
            isSuccessful = true;
            break;
        }

        return isSuccessful;
    }

    private boolean convertToEnumerationList(CoalesceField<?> field) throws CoalesceException
    {
        boolean isSuccessful;
        String enumeration = EnumerationProviderUtil.lookupEnumeration(field);

        switch (field.getDataType()) {
        case STRING_LIST_TYPE:
            String[] newvalue = ((CoalesceStringListField) field).getValue();

            for (int ii = 0; ii < newvalue.length; ii++)
            {
                newvalue[ii] = Integer.toString(EnumerationProviderUtil.toPosition(null, enumeration, newvalue[ii]));
            }

            ((CoalesceStringListField) field).setValue(newvalue);
            isSuccessful = true;
            break;
        case INTEGER_LIST_TYPE:
            for (String value : field.getBaseValues())
            {
                if (!EnumerationProviderUtil.isValid(null, enumeration, Integer.parseInt(value)))
                {
                    addError(CoalesceErrors.INVALID_ENUMERATION_POSITION, value, enumeration);
                }
            }
            isSuccessful = true;
            break;
        case ENUMERATION_TYPE:
            // Do Nothing
            isSuccessful = true;
            break;
        default:
            isSuccessful = false;
        }

        return isSuccessful;
    }

    private boolean convertToEnumeration(CoalesceField<?> field) throws CoalesceException
    {
        boolean isSuccessful;
        String enumeration = EnumerationProviderUtil.lookupEnumeration(field);
        String newvalue = null;

        switch (field.getDataType()) {
        case STRING_TYPE:
            newvalue = Integer.toString(EnumerationProviderUtil.toPosition(null, enumeration, field.getBaseValue()));
            field.setAttribute(CoalesceField.ATTRIBUTE_VALUE, newvalue);
            isSuccessful = true;
            break;
        case INTEGER_TYPE:
            newvalue = field.getBaseValue();

            if (!EnumerationProviderUtil.isValid(null, enumeration, ((CoalesceIntegerField) field).getValue()))
            {
                addError(CoalesceErrors.INVALID_ENUMERATION_POSITION, newvalue, enumeration);
            }
            isSuccessful = true;
            break;
        default:
            isSuccessful = false;
        }

        return isSuccessful;
    }

    private void addError(String format, Object... params) throws CoalesceException
    {
        String msg = String.format(format, params);

        LOGGER.warn(msg);
        throw new CoalesceException(msg);
    }

    private void addResult(String format, Object... params)
    {
        String msg = String.format(format, params);

        results.add(msg);
        LOGGER.debug(msg);
    }
}
