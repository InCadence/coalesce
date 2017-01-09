package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.IteratorSetting.Column;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;

public class CloseableBatchScanner implements BatchScanner, AutoCloseable {

	private final BatchScanner scanner;

	public CloseableBatchScanner(Connector dbConnector, String table, Authorizations auths, int threads)
			throws TableNotFoundException {
		this.scanner = dbConnector.createBatchScanner(table, auths, threads);
	}

	@Override
	public void addScanIterator(IteratorSetting arg0) {
		scanner.addScanIterator(arg0);
	}

	@Override
	public void clearColumns() {
		scanner.clearColumns();
	}

	@Override
	public void clearScanIterators() {
		scanner.clearScanIterators();
	}

	@Override
	public void fetchColumn(Text arg0, Text arg1) {
		scanner.fetchColumn(arg0, arg1);
	}

	@Override
	public void fetchColumnFamily(Text arg0) {
		scanner.fetchColumnFamily(arg0);
	}

	@Override
	public long getTimeout(TimeUnit arg0) {
		return scanner.getTimeout(arg0);
	}

	@Override
	public Iterator<Entry<Key, Value>> iterator() {
		return scanner.iterator();
	}

	@Override
	public void removeScanIterator(String arg0) {
		scanner.removeScanIterator(arg0);
	}

	@Override
	public void updateScanIteratorOption(String arg0, String arg1, String arg2) {
		scanner.updateScanIteratorOption(arg0, arg1, arg2);
	}

	@Override
	public void close() {
		scanner.close();
	}

	@Override
	public void setRanges(Collection<Range> arg0) {
		scanner.setRanges(arg0);
	}

	@Override
	public void setTimeout(long arg0, TimeUnit arg1) {
		scanner.setTimeout(arg0, arg1);
	}

    @Override
    public void fetchColumn(Column arg0)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Authorizations getAuthorizations()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
