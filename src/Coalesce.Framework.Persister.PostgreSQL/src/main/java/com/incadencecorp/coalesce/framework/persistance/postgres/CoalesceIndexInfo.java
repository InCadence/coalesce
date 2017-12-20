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

package com.incadencecorp.coalesce.framework.persistance.postgres;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;

/**
 * Class used to create index information for a provided {@link CoalesceObject}.
 * 
 * @author n78554
 */
public final class CoalesceIndexInfo {

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private String tableName;
    private CoalesceEntity entity;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Determines the index table name and the containing entity for a given coalesce element.
     * 
     * @param node
     *            {@link CoalesceObject}
     */
    public CoalesceIndexInfo(CoalesceObject node) {

        String objectName = node.getName();

        // Get root node.
        while (node.getParent() != null) {
            node = node.getParent();
        }

        // Is Coalesce Entity?
        if (node instanceof CoalesceEntity) {
            entity = (CoalesceEntity) node;
        } 

        setTableName(objectName);

    }

    /**
     * @param recordsetName
     *            {@link CoalesceObject#getName()}
     */
    private CoalesceIndexInfo(final String recordsetName) {

        setTableName(recordsetName);

    }

    /*--------------------------------------------------------------------------
    Public Factory Methods
    --------------------------------------------------------------------------*/

    /**
     * Utility function for formatting the index table name from a record set name.
     * 
     * @param recordsetName
     *            Name of the record set.
     * @return the name of the table containing flattened base object records
     */
    public static String getIndexTableName(final String recordsetName) {

        CoalesceIndexInfo info = new CoalesceIndexInfo(recordsetName);
        return info.getTableName();

    }

    /*--------------------------------------------------------------------------
    Getters / Setters
    --------------------------------------------------------------------------*/

    /**
     * @return Returns the procedure name to be used for creating stored procedures.
     */
    public String getProcedureName() {
        return getTableName() + "_insertorupdate";
    }

    /**
     * @return Returns the index table name.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return Returns parent entity.
     */
    public CoalesceEntity getEntity() {
        return entity;
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private String normalize(final String value) {
        return value.replace(" ", "_").replaceAll("[^a-zA-Z0-9_\\s]", "").toLowerCase();
    }

    private void setTableName(final String objectName) {

        // If name exceeds 63 (PostGresQL MAX label length) - 22 [3x1 (additional _) - 4
        // ("pkey" for the constraint) - 15 (size of _insertorupdate which is appended to
        // the stored procedure)]
        int maxLen = 63 - 22;

        tableName = normalize(objectName);

        if (tableName.length() > maxLen) {
            // Truncate
            tableName = tableName.substring(0, maxLen) + "_";
        }

    }

}
