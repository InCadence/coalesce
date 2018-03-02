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

package com.incadencecorp.coalesce.synchronizer.api.common.mocks;

import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.geotools.data.Query;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.persistance.CoalesceParameter;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractScan;

/**
 * Mock scanner implementation. The result set that is returned when executing a
 * scan is configurable.
 * 
 * @author n78554
 */
public class MockScanner extends AbstractScan {

    private String[] columns;
    private List<Object[]> rows;

    /**
     * Sets the columns returned when executing a scan.
     * 
     * @param columns
     */
    public void setColumns(List<String> columns)
    {
        this.columns = columns.toArray(new String[columns.size()]);
    }

    /**
     * Sets the data returned when executing a scan.
     * 
     * @param rows
     */
    public void setRows(List<Object[]> rows)
    {
        this.rows = rows;
    }

    @Override
    public CachedRowSet doScan(Query query) throws CoalesceException
    {
        CachedRowSet results;

        try
        {
            if (getSource() == null)
            {
                results = RowSetProvider.newFactory().createCachedRowSet();
                results.populate(new CoalesceResultSet(rows.iterator(), columns));
            }
            else
            {
                results = getSource().search(query).getResults();
            }
        }
        catch (SQLException e)
        {
            throw new CoalesceException("Failed", e);
        }

        return results;
    }
}
