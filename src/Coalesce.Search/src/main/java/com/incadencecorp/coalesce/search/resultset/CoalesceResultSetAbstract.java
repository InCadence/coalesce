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

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract disconnected implementation of ResultSet to be used with persistors
 * that do not have a JDBC connector.
 * 
 * @author n78554
 */
public abstract class CoalesceResultSetAbstract implements ResultSet {

    protected final static Logger LOGGER = LoggerFactory.getLogger(CoalesceResultSetAbstract.class);

    private boolean closed = false;
    private List<CoalesceColumnMetadata> columns;
    private String[] columnNames;
    private int cols;
    private boolean wasNull = false;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Creates a result set for the provided columns.
     * 
     * @param columns
     */
    public CoalesceResultSetAbstract(List<CoalesceColumnMetadata> columns)
    {
        this.cols = columns.size();
        this.columns = columns;
        this.columnNames = extractColumnNames(columns);
    }

    /**
     * Creates a result set for the provided columns as Strings.
     * 
     * @param columns
     */
    public CoalesceResultSetAbstract(String... columns)
    {
        this.cols = columns.length;
        this.columnNames = columns;
        this.columns = createMetadataFor(columns);
    }

    @Override
    public boolean wasNull() throws SQLException
    {
        return wasNull;
    }

    @Override
    public String toString()
    {
        return "Columns: " + Arrays.toString(columnNames);
    }

    @Override
    public String getString(int i) throws SQLException
    {
        Object value = get(i);

        if (value == null)
        {
            return null;
        }
        final Class<?> type = value.getClass();
        if (String.class.equals(type))
        {
            return (String) value;
        }
        if (type.isPrimitive() || Number.class.isAssignableFrom(type))
        {
            return value.toString();
        }
        try
        {
            return OBJECT_MAPPER.writeValueAsString(value);
        }
        catch (Exception e)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Couldn't convert value " + value + " of type " + type + " to JSON " + e.getMessage());
            }
        }
        return value.toString();

    }

    @Override
    public boolean getBoolean(int i) throws SQLException
    {
        return (Boolean) get(i);
    }

    @Override
    public byte getByte(int i) throws SQLException
    {
        return getNumber(i).byteValue();
    }

    @Override
    public short getShort(int i) throws SQLException
    {
        return getNumber(i).shortValue();
    }

    @Override
    public int getInt(int i) throws SQLException
    {
        return getNumber(i).intValue();
    }

    @Override
    public long getLong(int i) throws SQLException
    {
        return getNumber(i).longValue();
    }

    @Override
    public float getFloat(int i) throws SQLException
    {
        return getNumber(i).floatValue();
    }

    @Override
    public double getDouble(int i) throws SQLException
    {
        return getNumber(i).doubleValue();
    }

    @Override
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException
    {
        return (BigDecimal) get(i);
    }

    @Override
    public byte[] getBytes(int i) throws SQLException
    {
        return (byte[]) get(i);
    }

    @Override
    public Date getDate(int i) throws SQLException
    {
        return new Date(getTimeInMillis(i));
    }

    @Override
    public Time getTime(int i) throws SQLException
    {
        return new Time(getTimeInMillis(i));
    }

    @Override
    public Timestamp getTimestamp(int i) throws SQLException
    {
        return new Timestamp(getTimeInMillis(i));
    }

    @Override
    public InputStream getAsciiStream(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("Type AsciiStream");
    }

    @Override
    public InputStream getUnicodeStream(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("Type UnicodeStream");
    }

    @Override
    public InputStream getBinaryStream(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("Type BinaryStream");
    }

    @Override
    public String getString(String s) throws SQLException
    {
        return getString(findColumn(s));
    }

    @Override
    public boolean getBoolean(String s) throws SQLException
    {
        return getBoolean(findColumn(s));
    }

    @Override
    public byte getByte(String s) throws SQLException
    {
        return getByte(findColumn(s));
    }

    @Override
    public short getShort(String s) throws SQLException
    {
        return getShort(findColumn(s));
    }

    @Override
    public int getInt(String s) throws SQLException
    {
        return getInt(findColumn(s));
    }

    @Override
    public long getLong(String s) throws SQLException
    {
        return getLong(findColumn(s));
    }

    @Override
    public float getFloat(String s) throws SQLException
    {
        return getFloat(findColumn(s));
    }

    @Override
    public double getDouble(String s) throws SQLException
    {
        return getDouble(findColumn(s));
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException
    {
        return getBigDecimal(findColumn(s));
    }

    @Override
    public byte[] getBytes(String s) throws SQLException
    {
        return getBytes(findColumn(s));
    }

    @Override
    public Date getDate(String s) throws SQLException
    {
        return getDate(findColumn(s));
    }

    @Override
    public Time getTime(String s) throws SQLException
    {
        return getTime(findColumn(s));
    }

    @Override
    public Timestamp getTimestamp(String s) throws SQLException
    {
        return getTimestamp(findColumn(s));
    }

    @Override
    public InputStream getAsciiStream(String s) throws SQLException
    {
        return getAsciiStream(findColumn(s));
    }

    @Override
    public InputStream getUnicodeStream(String s) throws SQLException
    {
        return getUnicodeStream(findColumn(s));
    }

    @Override
    public InputStream getBinaryStream(String s) throws SQLException
    {
        return getBinaryStream(findColumn(s));
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException
    {
    }

    @Override
    public String getCursorName() throws SQLException
    {
        return null;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        return new CoalesceResultSetMetadata(columns);
    }

    @Override
    public Object getObject(int i) throws SQLException
    {
        return get(i);
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException
    {
        return type.cast(getObject(columnIndex));
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException
    {
        return type.cast(getObject(columnLabel));
    }

    @Override
    public Object getObject(String s) throws SQLException
    {
        return getObject(findColumn(s));
    }

    @Override
    public int findColumn(String column) throws SQLException
    {
        if (column != null)
        {
            for (int i = 0; i < cols; i++)
            {
                if (column.equals(columnNames[i]))
                {
                    return i + 1;
                }
            }
        }
        throw new SQLException("No such column:" + column);
    }

    @Override
    public Reader getCharacterStream(int i) throws SQLException
    {
        return new StringReader(getString(i));
    }

    @Override
    public Reader getCharacterStream(String s) throws SQLException
    {
        return new StringReader(getString(s));
    }

    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException
    {
        final double d = getDouble(i);
        final long l = getLong(i);
        return l == d ? BigDecimal.valueOf(l) : BigDecimal.valueOf(d);
    }

    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException
    {
        return getBigDecimal(findColumn(s));
    }

    @Override
    public void setFetchDirection(int i) throws SQLException
    {
    }

    @Override
    public int getFetchDirection() throws SQLException
    {
        return FETCH_FORWARD;
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
        return TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException
    {
        return CONCUR_READ_ONLY;
    }

    @Override
    public boolean rowUpdated() throws SQLException
    {
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException
    {
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException
    {
        return false;
    }

    @Override
    public void updateNull(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateBoolean(int i, boolean b) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateByte(int i, byte b) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateShort(int i, short i1) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateInt(int i, int i1) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateLong(int i, long l) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateFloat(int i, float v) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateDouble(int i, double v) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateString(int i, String s) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateDate(int i, Date date) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateTime(int i, Time time) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateObject(int i, Object o, int i1) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateObject(int i, Object o) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateNull(String s) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateBoolean(String s, boolean b) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateByte(String s, byte b) throws SQLException
    {
    }

    @Override
    public void updateShort(String s, short i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateInt(String s, int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateLong(String s, long l) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateFloat(String s, float v) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateDouble(String s, double v) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateString(String s, String s1) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateDate(String s, Date date) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateTime(String s, Time time) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateObject(String s, Object o, int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateObject(String s, Object o) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void insertRow() throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void updateRow() throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void deleteRow() throws SQLException
    {
        throw new SQLFeatureNotSupportedException("update");
    }

    @Override
    public void refreshRow() throws SQLException
    {
    }

    @Override
    public void cancelRowUpdates() throws SQLException
    {
    }

    @Override
    public void moveToInsertRow() throws SQLException
    {
        throw new SQLFeatureNotSupportedException("move to insert row");
    }

    @Override
    public void moveToCurrentRow() throws SQLException
    {
        throw new SQLFeatureNotSupportedException("move to current row");
    }

    @Override
    public Statement getStatement() throws SQLException
    {
        return null; // todo
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> typeMap) throws SQLException
    {
        return null;
    }

    @Override
    public Ref getRef(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("get " + "ref");
    }

    @Override
    public Blob getBlob(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("get " + "blob");
    }

    @Override
    public Clob getClob(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("get " + "clob");
    }

    @Override
    public Array getArray(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("get " + "array");
    }

    @Override
    public Object getObject(String s, Map<String, Class<?>> typeMap) throws SQLException
    {
        return getObject(findColumn(s), typeMap);
    }

    @Override
    public Ref getRef(String s) throws SQLException
    {
        return getRef(findColumn(s));
    }

    @Override
    public Blob getBlob(String s) throws SQLException
    {
        return getBlob(findColumn(s));
    }

    @Override
    public Clob getClob(String s) throws SQLException
    {
        return getClob(findColumn(s));
    }

    @Override
    public Array getArray(String s) throws SQLException
    {
        return getArray(findColumn(s));
    }

    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException
    {
        return new Date(getCalendarTimeInMillis(i, calendar));
    }

    private long getCalendarTimeInMillis(int i, Calendar calendar) throws SQLException
    {
        calendar.setTimeInMillis(getTimeInMillis(i));
        return calendar.getTimeInMillis();
    }

    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException
    {
        return getDate(findColumn(s), calendar);
    }

    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException
    {
        return new Time(getCalendarTimeInMillis(i, calendar));
    }

    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException
    {
        return getTime(findColumn(s), calendar);
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException
    {
        return new Timestamp(getCalendarTimeInMillis(i, calendar));
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException
    {
        return getTimestamp(findColumn(s), calendar);
    }

    @Override
    public URL getURL(int i) throws SQLException
    {
        String url = getString(i);
        try
        {
            return new URL(url);
        }
        catch (MalformedURLException ex)
        {
            throw new SQLDataException("Malformed url " + url);
        }
    }

    @Override
    public URL getURL(String s) throws SQLException
    {
        return getURL(findColumn(s));
    }

    @Override
    public void updateRef(int i, Ref ref) throws SQLException
    {
        String type = "ref";
        notSupportedUpdate(type);
    }

    private void notSupportedUpdate(String type) throws SQLFeatureNotSupportedException
    {
        throw new SQLFeatureNotSupportedException("update " + type);
    }

    @Override
    public void updateRef(String s, Ref ref) throws SQLException
    {
        notSupportedUpdate("ref");
    }

    @Override
    public void updateBlob(int i, Blob blob) throws SQLException
    {
        notSupportedUpdate("blob");
    }

    @Override
    public void updateBlob(String s, Blob blob) throws SQLException
    {
        notSupportedUpdate("blob");
    }

    @Override
    public void updateClob(int i, Clob clob) throws SQLException
    {
        notSupportedUpdate("clob");
    }

    @Override
    public void updateClob(String s, Clob clob) throws SQLException
    {
        notSupportedUpdate("clob");
    }

    @Override
    public void updateArray(int i, Array array) throws SQLException
    {
        notSupportedUpdate("array");
    }

    @Override
    public void updateArray(String s, Array array) throws SQLException
    {
        notSupportedUpdate("array");
    }

    @Override
    public RowId getRowId(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("get " + "rowid");
    }

    @Override
    public RowId getRowId(String s) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("get " + "rowid");
    }

    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException
    {
        notSupportedUpdate("rowid");
    }

    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException
    {
        notSupportedUpdate("rowid");
    }

    @Override
    public int getHoldability() throws SQLException
    {
        return CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public void updateNString(int i, String s) throws SQLException
    {
        notSupportedUpdate("nstring");
    }

    @Override
    public void updateNString(String s, String s1) throws SQLException
    {
        notSupportedUpdate("nstring");
    }

    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException
    {
        notSupportedUpdate("nclob");
    }

    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException
    {
        notSupportedUpdate("nclob");
    }

    @Override
    public NClob getNClob(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("get " + "nclob");
    }

    @Override
    public NClob getNClob(String s) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("get " + "nclob");
    }

    @Override
    public SQLXML getSQLXML(int i) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("get " + "sqlxml");
    }

    @Override
    public SQLXML getSQLXML(String s) throws SQLException
    {
        throw new SQLFeatureNotSupportedException("get " + "sqlxml");
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException
    {
        notSupportedUpdate("sqlxml");
    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException
    {
        notSupportedUpdate("sqlxml");
    }

    @Override
    public String getNString(int i) throws SQLException
    {
        return getString(i);
    }

    @Override
    public String getNString(String s) throws SQLException
    {
        return getString(s);
    }

    @Override
    public Reader getNCharacterStream(int i) throws SQLException
    {
        return getCharacterStream(i);
    }

    @Override
    public Reader getNCharacterStream(String s) throws SQLException
    {
        return getCharacterStream(s);
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException
    {
        notSupportedUpdate("ncharacterstream");
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException
    {
        notSupportedUpdate("ncharacterstream");
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException
    {
        notSupportedUpdate("asciistream");
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException
    {
        notSupportedUpdate("binarystream");
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException
    {
        notSupportedUpdate("characterstream");
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException
    {
        notSupportedUpdate("asciistream");
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException
    {
        notSupportedUpdate("binarystream");
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException
    {
        notSupportedUpdate("characterstream");
    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException
    {
        notSupportedUpdate("blob");
    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException
    {
        notSupportedUpdate("blob");
    }

    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException
    {
        notSupportedUpdate("clob");
    }

    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException
    {
        notSupportedUpdate("clob");
    }

    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException
    {
        notSupportedUpdate("nclob");
    }

    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException
    {
        notSupportedUpdate("nclob");
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException
    {
        notSupportedUpdate("ncharacterstream");
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException
    {
        notSupportedUpdate("ncharacterstream");
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException
    {
        notSupportedUpdate("asciistream");
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException
    {
        notSupportedUpdate("binarystream");
    }

    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException
    {
        notSupportedUpdate("characterstream");
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException
    {
        notSupportedUpdate("asciistream");
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException
    {
        notSupportedUpdate("binarystream");
    }

    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException
    {
        notSupportedUpdate("characterstream");
    }

    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException
    {
        notSupportedUpdate("blob");
    }

    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException
    {
        notSupportedUpdate("blob");
    }

    @Override
    public void updateClob(int i, Reader reader) throws SQLException
    {
        notSupportedUpdate("clob");
    }

    @Override
    public void updateClob(String s, Reader reader) throws SQLException
    {
        notSupportedUpdate("clob");
    }

    @Override
    public void updateNClob(int i, Reader reader) throws SQLException
    {
        notSupportedUpdate("nclob");
    }

    @Override
    public void updateNClob(String s, Reader reader) throws SQLException
    {
        notSupportedUpdate("nclob");
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException
    {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException
    {
        return false;
    }

    @Override
    public void close() throws SQLException
    {
        closed = true;
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        return closed;
    }

    protected List<CoalesceColumnMetadata> createMetadataFor(String... columns)
    {
        List<CoalesceColumnMetadata> result = new ArrayList<CoalesceColumnMetadata>(columns.length);
        for (String column : columns)
        {
            result.add(new CoalesceColumnMetadata(column, "String", Types.VARCHAR));
        }
        return result;
    }

    protected abstract Object[] currentRow();

    private String[] extractColumnNames(List<CoalesceColumnMetadata> columns)
    {
        final String[] result = new String[columns.size()];
        for (int i = 0; i < cols; i++)
        {
            result[i] = columns.get(i).getName();
        }
        return result;
    }

    private Number getNumber(int i) throws SQLException
    {
        Object value = get(i);
        if (value == null)
            return 0;
        if (!(value instanceof Number))
            throw new SQLDataException("Value is not a number" + value);
        return ((Number) value);
    }

    private Object get(int column) throws SQLDataException
    {
        if (column < 1 || column > cols)
        {
            throw new SQLDataException("Column " + column + " is invalid");
        }
        Object value = currentRow()[column - 1];
        wasNull = value == null;
        return value;
    }

    private long getTimeInMillis(int i) throws SQLException
    {
        Number number = getNumber(i);
        return number.longValue();
    }

}
