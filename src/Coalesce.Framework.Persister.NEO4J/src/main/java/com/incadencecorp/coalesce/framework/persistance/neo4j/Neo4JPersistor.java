/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework.persistance.neo4j;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFileField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.persistance.CoalesceDataConnectorBase;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.framework.persistance.CoalescePersistorBase;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * Neo4j Implementation. Extend and override {@link #getGroups(CoalesceEntity)}
 * and {@link #getClassification(CoalesceEntity)} to add security handling.
 *
 * @author n78554
 */
public class Neo4JPersistor extends CoalescePersistorBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4JPersistor.class);
    private static final Neo4jTypeMapper MAPPER = new Neo4jTypeMapper();

    /**
     * @see CoalescePropertyFactory#getEntityKey()
     */
    public static final String KEY = getName(CoalescePropertyFactory.getEntityKey());
    /**
     * @see CoalescePropertyFactory#getName()
     */
    public static final String NAME = getName(CoalescePropertyFactory.getName());
    /**
     * @see CoalescePropertyFactory#getName()
     */
    public static final String SOURCE = getName(CoalescePropertyFactory.getSource());
    /**
     * @see CoalescePropertyFactory#getEntityType()
     */
    public static final String TYPE = getName(CoalescePropertyFactory.getEntityType());
    /**
     * @see CoalescePropertyFactory#getEntityTitle()
     */
    public static final String TITLE = getName(CoalescePropertyFactory.getEntityTitle());
    /**
     * @see CoalescePropertyFactory#getLastModified()
     */
    public static final String LASTMODIFIED = getName(CoalescePropertyFactory.getLastModified());
    public static final String LASTMODIFIED_BY = getName(CoalescePropertyFactory.getLastModifiedBy());
    /**
     * @see CoalescePropertyFactory#getDateCreated()
     */
    public static final String DATECREATED = getName(CoalescePropertyFactory.getDateCreated());
    public static final String CREATOR = getName(CoalescePropertyFactory.getCreatedBy());
    /**
     * @see CoalescePropertyFactory#getEntityXml()
     */
    public static final String ENTITYXML = getName(CoalescePropertyFactory.getEntityXml());


    public static final String ENTITYID = getName(CoalescePropertyFactory.getEntityId());
    public static final String ENTITYID_TYPE = getName(CoalescePropertyFactory.getEntityIdType());

    private static final String CYPHER_MERGE =
            "MERGE (Entity:%1$s {" + KEY + ": {2}})" + " ON CREATE SET Entity.deleted=%3$s, Entity.groups = %4$s, Entity."
                    + NAME + " = {1}," + " Entity." + KEY + " = {2}%2$s"
                    + " ON MATCH SET Entity.deleted=%3$s, Entity.groups = %4$s%2$s";

    private static final String CYPHER_CREATE_PLACEHOLDER =
            "MERGE (Entity:%1$s {" + KEY + ": {2}})" + " ON CREATE SET Entity." + NAME + " = {1}," + " Entity." + KEY
                    + " = {2}," + " Entity." + SOURCE + " = {3}";

    private static final String CYPHER_LINK_CLASSIFICATION =
            "MATCH (n:%s {" + KEY + ": {1}}), (cls:CLASSIFICATION_LEVEL {name: {2}}) "
                    + "OPTIONAL MATCH n-[r:CLEARED_TO]->() DELETE r " + "CREATE UNIQUE n-[:CLEARED_TO]->(cls)";

    private static final String CYPHER_DELETE_NODE = "MATCH (n:%s {" + KEY + ": {1}}) detach delete n";

    private static final String CYPHER_LINK =
            "MATCH (n1:%s {" + KEY + ": {1}}), (n2:%s {" + KEY + ": {2}}) CREATE UNIQUE (n1)-[:%s {key: {3} %s}]->(n2)";

    private static final String CYPHER_UNLINK =
            "OPTIONAL MATCH (n1:%s {" + KEY + ": {1}})-[rel:%s {key: {3}}]->(n2:%s {" + KEY + ": {2}}) DELETE rel";

    private static final String CYPHER_CONSTRAINT = "CREATE CONSTRAINT ON (n:%s) ASSERT n." + KEY + " IS UNIQUE";

    private static final String[] linkAttributes = { CoalesceLinkage.ATTRIBUTE_LINKTYPE, CoalesceLinkage.ATTRIBUTE_LABEL,
                                                     CoalesceLinkage.ATTRIBUTE_DATECREATED,
                                                     CoalesceLinkage.ATTRIBUTE_LASTMODIFIED,
                                                     CoalesceLinkage.ATTRIBUTE_MODIFIEDBY,
                                                     CoalesceLinkage.ATTRIBUTE_MODIFIEDBYIP, "classificationmarking"
    };

    /*--------------------------------------------------------------------------
    Constructor
    --------------------------------------------------------------------------*/

    /**
     * Default Constructor
     */
    public Neo4JPersistor()
    {
        setConnectionSettings(Neo4jSettings.getServerConn());
    }

    /*--------------------------------------------------------------------------
    Override Methods
    --------------------------------------------------------------------------*/

    @Override
    protected CoalesceDataConnectorBase getDataConnector() throws CoalescePersistorException
    {
        return new Neo4JDataConnector(getConnectionSettings());
    }

    @Override
    protected boolean flattenObject(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        return flattenCore(allowRemoval, entities);
    }

    @Override
    protected boolean flattenCore(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {

        boolean isSuccessful = false;
        int tries = Neo4jSettings.getRetryAttempts();

        try (CoalesceDataConnectorBase conn = getDataConnector())
        {

            while (!isSuccessful)
            {

                try
                {
                    conn.openConnection(false);

                    // Reset start-time
                    conn.rollback();

                    for (CoalesceEntity entity : entities)
                    {
                        if (entity.getStatus() != ECoalesceObjectStatus.DELETED || !Neo4jSettings.isAllowDelete())
                        {
                            // Persist Entity
                            persistEntityObject(entity, conn);

                            // Persist Security Linkages
                            linkClassificationLevel(entity, conn);

                            // Persist Linkages
                            for (CoalesceLinkage linkage : entity.getLinkages().values())
                            {
                                persistLinkageObject(linkage, conn);
                            }
                        }
                        else
                        {
                            deleteEntity(entity, conn);
                        }
                    }

                    conn.commit();

                    isSuccessful = true;

                }
                catch (SQLException e)
                {

                    // Release Locks
                    conn.rollback();

                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.warn("Neo4j: Flattening Object", e);
                        LOGGER.debug("Cypher Attempt {} out of {}",
                                     Neo4jSettings.getRetryAttempts() - tries + 1,
                                     Neo4jSettings.getRetryAttempts());
                    }

                    // Max Tried Reached?
                    if (tries-- <= 0)
                    {
                        // Yes; Throw an error
                        throw new CoalescePersistorException("Saving Entity: " + e.getMessage(), e);
                    }

                    // Randomize Back Off Interval (Prevents threads from
                    // awakening in sync)
                    int millis =
                            new Random().nextInt(Neo4jSettings.getBackoffInterval()) + Neo4jSettings.getBackoffInterval();

                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Sleeping ({}) for {} ms", Thread.currentThread().getId(), millis);
                    }

                    // Back off to allow other threads to close their locks.
                    Thread.sleep(millis);

                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Awaken ({})", Thread.currentThread().getId());
                    }

                }
            }

        }
        catch (InterruptedException e1)
        {
            throw new CoalescePersistorException("Attempt Aborted", e1);
        }

        return isSuccessful;

    }

    @Override
    public void saveTemplate(CoalesceDataConnectorBase conn, CoalesceEntityTemplate... templates)
            throws CoalescePersistorException
    {

        boolean isSuccessful = false;
        int tries = Neo4jSettings.getRetryAttempts();

        try
        {

            while (!isSuccessful)
            {

                try
                {
                    conn.openConnection(false);

                    // Reset start-time
                    conn.rollback();

                    for (CoalesceEntityTemplate template : templates)
                    {
                        conn.executeUpdate(String.format(CYPHER_CONSTRAINT, normalizeName(template.getName())));
                    }

                    conn.commit();

                    isSuccessful = true;

                }
                catch (SQLException e)
                {

                    // Release Locks
                    conn.rollback();

                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.warn("Neo4j: Creating Constraint", e);
                        LOGGER.debug("Cypher Attempt {} out of {}",
                                     Neo4jSettings.getRetryAttempts() - tries + 1,
                                     Neo4jSettings.getRetryAttempts());
                    }

                    // Max Tries Reached?
                    if (tries-- <= 0)
                    {
                        // Yes; Throw an error
                        throw new CoalescePersistorException("Creating Constraint", e);
                    }

                    // Randomize Back Off Interval (Prevents threads from
                    // awakening in sync)
                    int millis =
                            new Random().nextInt(Neo4jSettings.getBackoffInterval()) + Neo4jSettings.getBackoffInterval();

                    LOGGER.debug("Sleeping ({}) for {} ms", Thread.currentThread().getId(), millis);

                    // Back off to allow other threads to close their locks.
                    Thread.sleep(millis);

                    LOGGER.debug("Awaken ({})", Thread.currentThread().getId());

                }
            }

        }
        catch (InterruptedException e1)
        {
            throw new CoalescePersistorException("Attempt Aborted", e1);
        }

    }

    @Override
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {
        throw new CoalescePersistorException("Not Implemented");
    }

    @Override
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {
        throw new CoalescePersistorException("Not Implemented");
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        List<String> results = new ArrayList<>();

        if (Neo4jSettings.isXMLEnabled())
        {
            try (CoalesceDataConnectorBase conn = new Neo4JDataConnector(Neo4jSettings.getServerConn()))
            {
                conn.openConnection(false);

                // Reset start-time
                conn.rollback();

                StringBuilder sb = new StringBuilder("MATCH (n) WHERE n." + KEY + " IN [");

                CoalesceParameter[] parameters = new CoalesceParameter[keys.length];

                for (int ii = 0; ii < keys.length; ii++)
                {
                    parameters[ii] = new CoalesceParameter(keys[ii]);
                    sb.append("?");

                    if (ii + 1 != keys.length)
                    {
                        sb.append(",");
                    }
                }

                sb.append("] RETURN n." + ENTITYXML);

                ResultSet rowset = conn.executeQuery(sb.toString(), parameters);

                while (rowset.next())
                {
                    results.add(rowset.getString(1));
                }
            }
            catch (SQLException e)
            {
                throw new CoalescePersistorException("getEntityXml", e);
            }
        }
        else
        {
            throw new NotImplementedException("getEntityXml");
        }

        return results.toArray(new String[results.size()]);
    }

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * @param query
     * @param parameters
     * @return a row set containing the nodes that match the query.
     */
    public CachedRowSet executeQuery(String query, CoalesceParameter... parameters)
    {
        if (LOGGER.isErrorEnabled())
        {
            LOGGER.debug("Executing Graph query: {}", query);
        }

        CachedRowSet rowset = null;

        try (CoalesceDataConnectorBase conn = new Neo4JDataConnector(Neo4jSettings.getServerConn()))
        {
            rowset = RowSetProvider.newFactory().createCachedRowSet();

            if (parameters != null)
            {
                rowset.populate(conn.executeQuery(query, parameters));
            }
            else
            {
                rowset.populate(conn.executeQuery(query));
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            // Do Nothing (ResultSet was empty)
        }
        catch (CoalescePersistorException | SQLException e)
        {
            throw new RuntimeException("Neo4j: Executing Query", e);
        }

        return rowset;
    }

    /*--------------------------------------------------------------------------
    Protected Methods
    --------------------------------------------------------------------------*/

    protected boolean persistEntityObject(CoalesceEntity entity, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Obtain a list of all field values
        Map<String, CoalesceParameter> fieldValues = new HashMap<>();

        // Add Coalesce Entity's Fields
        fieldValues.put(TITLE, new CoalesceParameter(entity.getTitle(), Types.CHAR));
        fieldValues.put(SOURCE, new CoalesceParameter(entity.getSource(), Types.CHAR));
        fieldValues.put(LASTMODIFIED,
                        new CoalesceParameter(entity.getAttribute(CoalesceObject.ATTRIBUTE_LASTMODIFIED), Types.DATE));
        fieldValues.put(LASTMODIFIED_BY,
                        new CoalesceParameter(entity.getModifiedBy(), Types.CHAR));
        fieldValues.put(DATECREATED,
                        new CoalesceParameter(entity.getAttribute(CoalesceObject.ATTRIBUTE_DATECREATED), Types.DATE));
        fieldValues.put(CREATOR,
                        new CoalesceParameter(entity.getCreatedBy(), Types.CHAR));

        fieldValues.put(ENTITYID,
                        new CoalesceParameter(entity.getEntityId(), Types.CHAR));
        fieldValues.put(ENTITYID_TYPE,
                        new CoalesceParameter(entity.getEntityIdType(), Types.CHAR));

        if (Neo4jSettings.isXMLEnabled())
        {
            fieldValues.put(ENTITYXML, new CoalesceParameter(entity.toXml(), Types.CHAR));
        }

        getFieldValues(entity, fieldValues);

        String groupsString = null;
        Set<String> groups = getGroups(entity);

        if (groups != null && groups.size() != 0)
        {
            groupsString = "[\"" + StringUtils.join(groups, "\",\"") + "\"]";
            groupsString = groupsString.toUpperCase();
        }

        CoalesceParameter[] parameters = new CoalesceParameter[fieldValues.size() + 2];

        int idx = 0;

        parameters[idx++] = new CoalesceParameter(entity.getName(), Types.CHAR);
        parameters[idx++] = new CoalesceParameter(entity.getKey(), Types.CHAR);

        StringBuilder sb = new StringBuilder("");

        // Add Field Values
        for (Entry<String, CoalesceParameter> entry : fieldValues.entrySet())
        {

            parameters[idx++] = entry.getValue();
            sb.append(", Entity." + entry.getKey().toLowerCase() + " = {" + idx + "}");

        }

        String query = String.format(CYPHER_MERGE,
                                     normalizeName(entity),
                                     sb.toString(),
                                     entity.isMarkedDeleted(),
                                     groupsString);

        return conn.executeUpdate(query, parameters) > 0;
    }

    protected void createPlaceHolder(CoalesceLinkage linkage, CoalesceDataConnectorBase conn) throws SQLException
    {

        String query = String.format(CYPHER_CREATE_PLACEHOLDER, normalizeName(linkage.getEntity2Name()));

        conn.executeUpdate(query,
                           new CoalesceParameter(linkage.getEntity2Name()),
                           new CoalesceParameter(linkage.getEntity2Key()),
                           new CoalesceParameter(linkage.getEntity2Source()));

    }

    protected boolean persistLinkageObject(CoalesceLinkage linkage, CoalesceDataConnectorBase conn) throws SQLException
    {
        String query;

        if (linkage.isMarkedDeleted())
        {
            // Delete Link
            query = String.format(CYPHER_UNLINK,
                                  normalizeName(linkage.getEntity1Name()),
                                  linkage.getLinkType(),
                                  normalizeName(linkage.getEntity2Name()));
        }
        else
        {
            createPlaceHolder(linkage, conn);

            // Set up the other attributes of the link. These are the ones
            // always there. Wrap them all in quotes so that empty values and
            // values with : will be legal for the query
            StringBuilder attributes = new StringBuilder();

            for (String attribute : linkAttributes)
            {
                String value = linkage.getAttribute(attribute);
                if (!StringHelper.isNullOrEmpty(value))
                {
                    attributes.append(String.format(",%s: \"%s\"", attribute, value));
                }
                else
                {
                    LOGGER.trace("No value for attribute: {}", attribute);
                }
            }

            // Add / Update Link
            query = String.format(CYPHER_LINK,
                                  normalizeName(linkage.getEntity1Name()),
                                  normalizeName(linkage.getEntity2Name()),
                                  linkage.getLinkType(),
                                  attributes.toString());
        }

        // Execute Query
        return conn.executeUpdate(query,
                                  new CoalesceParameter(linkage.getEntity1Key()),
                                  new CoalesceParameter(linkage.getEntity2Key()),
                                  new CoalesceParameter(linkage.getKey())) > 0;
    }

    protected Set<String> getGroups(CoalesceEntity entity) throws SQLException
    {
        return new HashSet<>();
    }

    protected String getClassification(CoalesceEntity entity) throws SQLException
    {
        return null;
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private static String getName(PropertyName property)
    {
        String[] tokens = property.getPropertyName().split("\\.");
        return tokens[tokens.length - 1];
    }

    private void deleteEntity(CoalesceEntity entity, CoalesceDataConnectorBase conn) throws SQLException
    {
        // Delete Node
        String query = String.format(CYPHER_DELETE_NODE, normalizeName(entity));
        conn.executeUpdate(query, new CoalesceParameter(entity.getKey()));
    }

    private void linkClassificationLevel(CoalesceEntity entity, CoalesceDataConnectorBase conn) throws SQLException
    {
        String cls = getClassification(entity);

        if (cls != null)
        {
            // Add / Update Link
            String query = String.format(CYPHER_LINK_CLASSIFICATION, normalizeName(entity));
            conn.executeUpdate(query,
                               new CoalesceParameter(entity.getKey()),
                               new CoalesceParameter(getClassification(entity)));
        }
    }

    private String normalizeName(CoalesceEntity entity)
    {
        return normalizeName(entity.getName());
    }

    private String normalizeName(String name)
    {
        return name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private void getFieldValues(CoalesceObject coalesceObject, Map<String, CoalesceParameter> results)
    {
        // Is Active?
        if (coalesceObject.isActive() && coalesceObject.isFlatten())
        {
            // Yes; Is a CoalesceField?
            if (coalesceObject.getType().equalsIgnoreCase("field"))
            {
                // Yes; Check Data Type
                CoalesceField<?> field = (CoalesceField<?>) coalesceObject;

                if (field.getBaseValue() != null || field.getDataType() == ECoalesceFieldDataTypes.FILE_TYPE)
                {
                    // TODO Replace this with the normalize API
                    String name = normalizeName(field.getName());

                    switch (field.getDataType())
                    {
                    case BINARY_TYPE:
                        // Ignore these types.
                        break;
                    case FILE_TYPE:
                        results.put(name,
                                    new CoalesceParameter(((CoalesceFileField) field).getFilename(),
                                                          MAPPER.map(field.getDataType())));
                        break;
                    default:
                        // Add field value to results
                        results.put(name, new CoalesceParameter(field.getBaseValue(), MAPPER.map(field.getDataType())));
                        break;
                    }
                }
            }

            // Recurse Through Children
            for (CoalesceObject child : coalesceObject.getChildCoalesceObjects().values())
            {
                getFieldValues(child, results);
            }
        }
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        EnumSet<EPersistorCapabilities> enumSet = EnumSet.of(EPersistorCapabilities.CREATE,
                                                             EPersistorCapabilities.DELETE,
                                                             EPersistorCapabilities.UPDATE);

        if (Neo4jSettings.isXMLEnabled())
        {
            enumSet.add(EPersistorCapabilities.READ);
        }

        return enumSet;
    }

    /*--------------------------------------------------------------------------
    Not Implemented
    --------------------------------------------------------------------------*/

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        throw new NotImplementedException("getEntityTemplate");
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version)
            throws CoalescePersistorException
    {
        throw new NotImplementedException("getEntityTemplate");
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        throw new NotImplementedException("getEntityTemplateKey");
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        return new ArrayList<>();
    }
}
