/*-----------------------------------------------------------------------------'
 Copyright 2015 - InCadence Strategic Solutions Inc., All Rights Reserved

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

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.CoalesceIterator;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This iterator is responsible for merging Coalesce Entities. NOT Thread safe.
 *
 * @author n78554
 */
public class CoalesceIteratorMerge extends CoalesceIterator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceIteratorMerge.class);

    private int version;
    private CoalesceEntity original;
    private String user;
    private String ip;

    private List<String> childrenKeys;

    private boolean hasNewElements = false;
    private boolean hasChanges = false;
    private boolean hasEntityChanged = false;

    private static final List<String> ATTRIBUTES_TO_IGNORE = Arrays.asList("previoushistorykey",
                                                                           "modifiedby",
                                                                           "modifiedbyip",
                                                                           "objectversion",
                                                                           "key");

    /**
     * Merges the updated entity on top of the original.
     *
     * @param user     The user making the merge request
     * @param ip       The IP of the user making the merge request
     * @param original entity
     * @param updated  entity
     * @return the merged entity.
     * @throws CoalesceException if the entities don't share the same key or
     *                           there was an issue during the merge.
     */
    public CoalesceEntity merge(String user, String ip, CoalesceEntity original, CoalesceEntity updated)
            throws CoalesceException
    {
        LOGGER.debug("Merging: {}", original.getKey());

        // Same Object?
        if (!original.getKey().equalsIgnoreCase(updated.getKey()))
        {
            throw new CoalesceException("Cannot Merge Different Objects");
        }

        this.version = original.getObjectVersion() + 1;
        this.original = original;
        this.user = user;
        this.ip = ip;

        // Process Update
        processAllElements(updated);

        // Update has new Elements?
        if (hasNewElements)
        {
            original = addNewElements(original, updated);
        }

        if (!hasEntityChanged && (hasChanges || hasNewElements))
        {
            LOGGER.debug("Incremented {} Version to {}", original.getKey(), version);

            original.createHistory(user, ip, version);
        }

        return original;

    }

    /**
     * Promotes the updated entity on top of the original. Any elements in the
     * original that are not in the updated version are marked as deleted.
     *
     * @param user     The user making the merge request
     * @param ip       The IP of the user making the merge request
     * @param original entity
     * @param updated  entity
     * @return the promoted entity.
     * @throws CoalesceException if the entities don't share the same key or
     *                           there was an issue during the merge.
     */
    public CoalesceEntity promote(String user, String ip, CoalesceEntity original, CoalesceEntity updated)
            throws CoalesceException
    {
        LOGGER.debug("Promoting: {}", original.getKey());

        // Same Object?
        if (!original.getKey().equalsIgnoreCase(updated.getKey()))
        {
            throw new CoalesceException("Cannot Merge Different Objects");
        }

        this.version = original.getObjectVersion() + 1;
        this.original = original;
        this.user = user;
        this.ip = ip;

        // Populate List of Children
        childrenKeys = new ArrayList<>();
        populateChildrenKeys(original, childrenKeys);

        // Process Update
        processAllElements(updated);

        // Update has new Elements?
        if (hasNewElements)
        {
            // This Should Never Trigger on a Promote
            original = addNewElements(original, updated);
        }

        // Process Any Children Not Visited (Don't exists in the version being
        // promoted)
        for (String key : childrenKeys)
        {

            CoalesceObject object = original.getCoalesceObjectForKey(key);

            // Parent Queued for Deletion?
            if (object != null && !childrenKeys.contains(object.getParent().getKey()))
            {
                // No; Can Element Type be Marked as Deleted
                if (object instanceof ICoalesceObjectHistory && !(object instanceof CoalesceHistory
                        || object instanceof CoalesceFieldHistory))
                {
                    LOGGER.debug("Marking [{}:{}] as Deleted", object.getName(), object.getKey());

                    // Yes; Create History
                    ((ICoalesceObjectHistory) object).createHistory(user, ip, version);

                    // Mark as Deleted
                    object.setStatus(ECoalesceObjectStatus.DELETED);
                    object.updateLastModified();

                    hasChanges = true;
                }
            }
        }

        if (!hasEntityChanged && (hasChanges || hasNewElements))
        {
            original.createHistory(user, ip, version);
        }

        return original;
    }

    private void populateChildrenKeys(CoalesceObject object, List<String> keys)
    {

        keys.add(object.getKey());

        for (CoalesceObject child : object.getChildCoalesceObjects().values())
        {
            populateChildrenKeys(child, keys);
        }

    }

    @Override
    protected boolean visitCoalesceEntity(CoalesceEntity entity)
    {
        // Always Create History
        hasEntityChanged = mergeAttributes(original, entity);

        if (hasEntityChanged)
        {
            LOGGER.debug("Incremented {} Version to {}", original.getKey(), version);
        }

        // Process Children
        return true;
    }

    @Override
    protected boolean visitCoalesceLinkageSection(CoalesceLinkageSection section)
    {
        return mergeElement(section);
    }

    @Override
    protected boolean visitCoalesceLinkage(CoalesceLinkage linkage)
    {
        return mergeElement(linkage);
    }

    @Override
    protected boolean visitCoalesceSection(CoalesceSection section)
    {
        return mergeElement(section);
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset)
    {
        CoalesceRecordset originalRecordset = (CoalesceRecordset) original.getCoalesceObjectForKey(recordset.getKey());

        if (mergeElement(originalRecordset, recordset))
        {
            // Merge definitions based on the name and not the key since they
            // can change.
            for (CoalesceFieldDefinition fd : recordset.getFieldDefinitions())
            {
                mergeElement(originalRecordset.getFieldDefinition(fd.getName()), fd);
            }

            // Process records as normal
            for (CoalesceRecord record : recordset.getAllRecords())
            {
                CoalesceRecord originalRecord = (CoalesceRecord) originalRecordset.getCoalesceObjectForKey(record.getKey());

                if (mergeElement(originalRecord, record))
                {
                    // Merge fields based on the field name and not the key. When using
                    // ExIm the field keys are not saved with the data and therefore
                    // will always be different resulting in duplicate fields to be
                    // created.
                    for (CoalesceField<?> field : record.getFields())
                    {
                        mergeElement(originalRecord.getFieldByName(field.getName()), field);
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected boolean visitCoalesceRecord(CoalesceRecord record)
    {
        // Don't visit children
        return false;
    }

    @Override
    protected boolean visitCoalesceField(CoalesceField<?> field)
    {
        // Don't visit children
        return false;
    }

    @Override
    protected boolean visitCoalesceFieldDefinition(CoalesceFieldDefinition definition)
    {
        // Don't visit children
        return false;
    }

    private boolean mergeElement(CoalesceObject orig, CoalesceObject updated)
    {
        // New Element?
        if (orig != null)
        {
            // No; Merge
            mergeAttributes(orig, updated);
            return true;
        }
        else
        {
            LOGGER.debug("Adding [{}:{}] to {}", updated.getName(), updated.getKey(), original.getKey());

            // Yes; New Element
            hasNewElements = true;

            // Remove History of New Elements
            if (updated instanceof CoalesceObjectHistory)
            {
                clearHistory((CoalesceObjectHistory) updated);
            }

            // TODO Implement cloning here to reduce overhead of merging object
            // with new elements.

            return false;
        }
    }

    private boolean mergeElement(CoalesceObject updated)
    {
        return mergeElement(original.getCoalesceObjectForKey(updated.getKey()), updated);
    }

    private void clearHistory(ICoalesceObjectHistory updated)
    {
        updated.clearHistory();

        if (updated instanceof CoalesceObject)
        {
            for (CoalesceObject child : ((CoalesceObject) updated).getChildCoalesceObjects().values())
            {
                if (child instanceof ICoalesceObjectHistory)
                {
                    clearHistory((ICoalesceObjectHistory) child);
                }
            }
        }

    }

    private boolean mergeAttributes(CoalesceObject original, CoalesceObject updated)
    {
        boolean historyCreated = false;

        if (childrenKeys != null)
        {
            childrenKeys.remove(updated.getKey());
        }

        Map<QName, String> attributes = updated.getAttributes();

        // Remove attributes that should only be modified on an update.
        for (String attributeToIgnore : ATTRIBUTES_TO_IGNORE)
        {
            attributes.remove(new QName(attributeToIgnore));
        }

        // Suspend History
        if (original instanceof ICoalesceObjectHistory)
        {
            ((ICoalesceObjectHistory) original).setSuspendHistory(true);
        }

        for (Map.Entry<QName, String> attribute : attributes.entrySet())
        {
            String originalValue = original.getAttribute(attribute.getKey().toString());
            String updatedValue = attribute.getValue();

            // Modified?
            if ((originalValue != null && !originalValue.equals(updatedValue)) || (originalValue == null
                    && updatedValue != null))
            {
                // Yes; History Already Created? (Exclude last modified and date
                // created changes, these can change when using ExIm)
                if (!historyCreated && !attribute.getKey().toString().equalsIgnoreCase(CoalesceObject.ATTRIBUTE_LASTMODIFIED)
                        && !attribute.getKey().toString().equalsIgnoreCase(CoalesceObject.ATTRIBUTE_DATECREATED))
                {
                    LOGGER.debug("Attribute in {} Modified {} = {}", original.getName(), attribute.getKey(), updatedValue);

                    hasChanges = true;

                    if (original instanceof ICoalesceObjectHistory)
                    {
                        // Create History
                        ((ICoalesceObjectHistory) original).setSuspendHistory(false);
                        ((ICoalesceObjectHistory) original).createHistory(user, ip, version);
                        ((ICoalesceObjectHistory) original).setSuspendHistory(true);

                        historyCreated = true;
                    }
                }

                // Apply Change
                original.setAttribute(attribute.getKey().toString(), attribute.getValue());
            }
        }

        if (historyCreated)
        {
            original.updateLastModified();
        }

        // Resume History
        if (original instanceof ICoalesceObjectHistory)
        {
            ((ICoalesceObjectHistory) original).setSuspendHistory(false);
        }

        return historyCreated;
    }

    /**
     * This function will merge new elements and attribute changes.
     */
    private CoalesceEntity addNewElements(CoalesceEntity original, CoalesceEntity updated) throws CoalesceException
    {

        try
        {
            // Merge Additional Nodes
            SAXBuilder saxBuilder = new SAXBuilder();

            org.jdom2.Document originalDoc = saxBuilder.build(new InputSource(new StringReader(original.toXml())));
            org.jdom2.Document updatedDoc = saxBuilder.build(new InputSource(new StringReader(updated.toXml())));

            addNewElements(originalDoc.getRootElement(), updatedDoc.getRootElement());

            XMLOutputter xmlOutPutter = new XMLOutputter();
            String output = xmlOutPutter.outputString(originalDoc);

            return CoalesceEntity.create(output);
        }
        catch (JDOMException | IOException e)
        {
            throw new CoalesceException("mergeSyncEntity", e);
        }

    }

    /**
     * Recursive method that merges new elements and attribute changes.
     */
    private void addNewElements(Element original, Element updated)
    {

        // Get Children
        Element[] children = original.getChildren().toArray(new Element[original.getChildren().size()]);
        Element[] updatedChildren = updated.getChildren().toArray(new Element[updated.getChildren().size()]);

        // Merge Required Node's Children
        for (Element updatedChild : updatedChildren)
        {

            boolean childExists = false;

            // Compare keys
            for (Element child : children)
            {
                String key = child.getAttribute("key").getValue();

                if (key.equalsIgnoreCase(updatedChild.getAttribute("key").getValue()))
                {
                    addNewElements(child, updatedChild);

                    childExists = true;
                    break;
                }
            }

            // Evaluate
            if (!childExists && !updatedChild.getName().equalsIgnoreCase("fieldhistory"))
            {
                LOGGER.debug("Adding New Element: {}", updatedChild.getAttribute("name"));

                // We don't have this child; add the entire Child data object
                // from updatechild
                Element child = updatedChild.clone();
                if (user != null)
                {
                    child.setAttribute("modifiedby", user);
                }
                if (ip != null)
                {
                    child.setAttribute("modifiedbyip", ip);
                }
                child.setAttribute("objectversion", Integer.toString(version));

                original.getChildren().add(child);

            }

        }
    }

}
