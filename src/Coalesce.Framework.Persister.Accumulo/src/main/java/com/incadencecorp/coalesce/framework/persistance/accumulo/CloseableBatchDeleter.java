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

package com.incadencecorp.coalesce.framework.persistance.accumulo;

import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@link BatchDeleter} wrapper that implements AutoCloseable.
 *
 * @author Derek Clemenzi
 */
public class CloseableBatchDeleter implements BatchDeleter, AutoCloseable {

    private final BatchDeleter deleter;

    public CloseableBatchDeleter(Connector dbConnector, String table, Authorizations auth, int threads, BatchWriterConfig config)
            throws TableNotFoundException
    {
        this.deleter = dbConnector.createBatchDeleter(table, auth, threads, config);
    }

    @Override
    public void delete() throws MutationsRejectedException, TableNotFoundException
    {
        deleter.delete();
    }

    @Override
    public void setRanges(Collection<Range> collection)
    {
        deleter.setRanges(collection);
    }

    @Override
    public void addScanIterator(IteratorSetting iteratorSetting)
    {
        deleter.addScanIterator(iteratorSetting);
    }

    @Override
    public void removeScanIterator(String s)
    {
        deleter.removeScanIterator(s);
    }

    @Override
    public void updateScanIteratorOption(String s, String s1, String s2)
    {
        deleter.updateScanIteratorOption(s, s1, s2);
    }

    @Override
    public void fetchColumnFamily(Text text)
    {
        deleter.fetchColumnFamily(text);
    }

    @Override
    public void fetchColumn(Text text, Text text1)
    {
        deleter.fetchColumn(text, text);
    }

    @Override
    public void fetchColumn(IteratorSetting.Column column)
    {
        deleter.fetchColumn(column);
    }

    @Override
    public void clearColumns()
    {
        deleter.clearColumns();
    }

    @Override
    public void clearScanIterators()
    {
        deleter.clearScanIterators();
    }

    @Override
    public Iterator<Map.Entry<Key, Value>> iterator()
    {
        return deleter.iterator();
    }

    @Override
    public void setTimeout(long l, TimeUnit timeUnit)
    {
        deleter.setTimeout(l, timeUnit);
    }

    @Override
    public long getTimeout(TimeUnit timeUnit)
    {
        return deleter.getTimeout(timeUnit);
    }

    @Override
    public void close()
    {
        deleter.close();
    }

    @Override
    public Authorizations getAuthorizations()
    {
        return deleter.getAuthorizations();
    }
}
