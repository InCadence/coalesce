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

import com.incadencecorp.coalesce.api.ICoalesceMapper;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.util.CoalesceTemplateUtil;
import com.incadencecorp.coalesce.mapper.impl.JavaMapperImpl;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import org.opengis.filter.expression.PropertyName;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * This implementation is to be used with persistors that do not have a JDBC
 * connector.
 *
 * @author n78554
 */
public class CoalesceResultSet extends CoalesceResultSetAbstract {

    private static final JavaMapperImpl MAPPER_JAVA = new JavaMapperImpl();

    private Iterator<Object[]> data;
    private Object[] currentRow;
    private int row = -1;

    /**
     * @param resultset
     * @return the column containing the entity keys.
     * @throws SQLException
     */
    public static int getEntityKeyColumn(ResultSet resultset) throws SQLException
    {
        int keyIdx = -1;

        String entitykeyColumn = CoalescePropertyFactory.getColumnName(CoalescePropertyFactory.getEntityKey());

        for (int ii = 1; ii <= resultset.getMetaData().getColumnCount(); ii++)
        {
            String columnName = resultset.getMetaData().getColumnName(ii);

            if (columnName.replaceAll("[.]", "").equalsIgnoreCase(entitykeyColumn))
            {
                keyIdx = ii;
                break;
            }
        }

        return keyIdx;

    }

    /**
     * Creates a result sets for the provided data and columns.
     *
     * @param data
     * @param columns
     */
    public CoalesceResultSet(Iterator<Object[]> data, List<CoalesceColumnMetadata> columns)
    {
        super(columns);
        this.data = data;
        data.hasNext();
    }

    /**
     * Creates a result sets for the provided data and columns as Strings.
     *
     * @param data
     * @param columns
     */
    public CoalesceResultSet(Iterator<Object[]> data, String... columns)
    {
        super(columns);
        this.data = data;
        data.hasNext();
    }

    /**
     * Utility method for creating column headers from {@link PropertyName}
     *
     * @param props  list of properties to use as columns
     * @param mapper how Coalesce data types should be mapped to {@link Types}
     * @return a list of column headers.
     */
    public static List<CoalesceColumnMetadata> getColumns(List<PropertyName> props, ICoalesceMapper<Integer> mapper)
    {
        List<CoalesceColumnMetadata> columnList = new ArrayList<>();

        for (PropertyName entry : props)
        {
            ECoalesceFieldDataTypes type = CoalesceTemplateUtil.getDataType(entry.getPropertyName());

            if (type != null)
            {
                columnList.add(new CoalesceColumnMetadata(CoalescePropertyFactory.getColumnName(entry.getPropertyName()),
                                                          MAPPER_JAVA.map(type).getTypeName(),
                                                          mapper.map(type)));
            }
            else
            {
                columnList.add(new CoalesceColumnMetadata(CoalescePropertyFactory.getColumnName(entry.getPropertyName()),
                                                          String.class.getTypeName(),
                                                          Types.VARCHAR));
            }
        }

        return columnList;
    }

    @Override
    protected Object[] currentRow()
    {
        return currentRow;
    }

    @Override
    public boolean next() throws SQLException
    {
        if (hasNext())
        {
            currentRow = data.next();
            row++;
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void close() throws SQLException
    {
        try
        {
            if (data instanceof AutoCloseable)
            {
                ((AutoCloseable) data).close();
            }
            else if (data instanceof Closeable)
            {
                ((Closeable) data).close();
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Couldn't close resultset", e);
        }

        super.close();
    }

    private boolean hasNext()
    {
        return data.hasNext();
    }

    @Override
    public boolean isBeforeFirst() throws SQLException
    {
        return row == -1;
    }

    @Override
    public boolean isAfterLast() throws SQLException
    {
        return !hasNext();
    }

    @Override
    public boolean isFirst() throws SQLException
    {
        return row == 0;
    }

    @Override
    public boolean isLast() throws SQLException
    {
        return !hasNext();
    }

    @Override
    public void beforeFirst() throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public void afterLast() throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean first() throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean last() throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public int getRow() throws SQLException
    {
        return row;
    }

    @Override
    public boolean absolute(int i) throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean relative(int i) throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public boolean previous() throws SQLException
    {
        throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
    }

    @Override
    public void setFetchDirection(int i) throws SQLException
    {
        if (i != ResultSet.FETCH_FORWARD)
        {
            throw new SQLException("Result set type is TYPE_FORWARD_ONLY");
        }
    }

    @Override
    public int getFetchDirection() throws SQLException
    {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int i) throws SQLException
    {
    }

    @Override
    public int getFetchSize() throws SQLException
    {
        return 0;
    }

    @Override
    public int getType() throws SQLException
    {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        return super.isClosed();
    }

    @Override
    public String toString()
    {
        return super.toString() + " current row " + row + ": " + Arrays.toString(currentRow);
    }

}
