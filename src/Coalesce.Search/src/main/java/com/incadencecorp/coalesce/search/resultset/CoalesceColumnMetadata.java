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

package com.incadencecorp.coalesce.search.resultset;

/**
 * Metadata for Coalesce columns.
 * 
 * @author n78554
 *
 */
public class CoalesceColumnMetadata {

    private String name;
    private String typeName;
    private int dataType;

    public CoalesceColumnMetadata(String name, String typeName, int dataType)
    {
        this.name = name;
        this.typeName = typeName;
        this.dataType = dataType;
    }

    public String getName()
    {
        return name;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public int getDataType()
    {
        return dataType;
    }

    public String toString()
    {
        return name + ":" + typeName;
    }

}
