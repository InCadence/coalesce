package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import org.geotools.data.Query;
import org.geotools.filter.Capabilities;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.api.SearchResults;

public class ElasticSearchPersistorSearch extends ElasticSearchPersister2 implements ICoalesceSearchPersistor {

	@Override
	public SearchResults search(Query query) throws CoalescePersistorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Capabilities getSearchCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

}
