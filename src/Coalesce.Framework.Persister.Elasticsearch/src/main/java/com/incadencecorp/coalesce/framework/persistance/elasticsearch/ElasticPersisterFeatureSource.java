package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import java.io.IOException;

import org.geotools.data.Query;
import org.geotools.data.store.ContentEntry;

import mil.nga.giat.data.elasticsearch.ElasticFeatureSource;
import mil.nga.giat.data.elasticsearch.ElasticRequest;

public class ElasticPersisterFeatureSource extends ElasticFeatureSource {

	public ElasticPersisterFeatureSource(ContentEntry arg0, Query arg1) throws IOException {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	public static ElasticRequest getESRequestFromQuery(Query query) {
		return null;
	}

}
