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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * This implementation is disconnected from a connection.
 * 
 * @author n78554
 */
public class CoalesceResultSetMetadata implements ResultSetMetaData {

    private List<CoalesceColumnMetadata> columns;

    CoalesceResultSetMetadata(List<CoalesceColumnMetadata> columns)
    {
        this.columns = columns;
    }

    @Override
    public int getColumnCount() throws SQLException
    {
        return columns.size();
    }

    @Override
    public boolean isAutoIncrement(int i) throws SQLException
    {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int i) throws SQLException
    {
        return false;
    }

    @Override
    public boolean isSearchable(int i) throws SQLException
    {
        return false;
    }

    @Override
    public boolean isCurrency(int i) throws SQLException
    {
        return false;
    }

    @Override
    public int isNullable(int i) throws SQLException
    {
        return 0;
    }

    @Override
    public boolean isSigned(int i) throws SQLException
    {
        return false;
    }

    @Override
    public int getColumnDisplaySize(int i) throws SQLException
    {
        return 20;
    }

    @Override
    public String getColumnLabel(int i) throws SQLException
    {
        return columns.get(i - 1).getName();
    }

    @Override
    public String getColumnName(int i) throws SQLException
    {
        return columns.get(i - 1).getName();
    }

    @Override
    public String getSchemaName(int i) throws SQLException
    {
        return "Default";
    }

    @Override
    public int getPrecision(int i) throws SQLException
    {
        return 0;
    }

    @Override
    public int getScale(int i) throws SQLException
    {
        return 0;
    }

    @Override
    public String getTableName(int i) throws SQLException
    {
        return null;
    }

    @Override
    public String getCatalogName(int i) throws SQLException
    {
        return "Default";
    }

    @Override
    public int getColumnType(int i) throws SQLException
    {
        return columns.get(i - 1).getDataType();
    }

    @Override
    public String getColumnTypeName(int i) throws SQLException
    {
        return columns.get(i - 1).getTypeName();
    }

    @Override
    public boolean isReadOnly(int i) throws SQLException
    {
        return false;
    }

    @Override
    public boolean isWritable(int i) throws SQLException
    {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int i) throws SQLException
    {
        return false;
    }

    @Override
    public String getColumnClassName(int i) throws SQLException
    {
        return columns.get(i - 1).getTypeName();
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException
    {
        return (T) this;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException
    {
        return false;
    }
}
