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

package com.incadencecorp.coalesce.services.search.service.tasks;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.Query;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.jobs.metrics.StopWatch;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import com.incadencecorp.coalesce.search.api.ICoalesceSearchPersistor;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.search.filter.FilterUtil;
import com.incadencecorp.coalesce.search.resultset.CoalesceResultSet;
import com.incadencecorp.coalesce.services.api.search.HitType;
import com.incadencecorp.coalesce.services.api.search.QueryResultType;
import com.incadencecorp.coalesce.services.api.search.QueryResultsType;
import com.incadencecorp.coalesce.services.api.search.QueryType;

public class SearchDataObjectTask extends AbstractTask<QueryType, QueryResultsType, ICoalesceSearchPersistor> {

    @Override
    protected QueryResultsType doWork(TaskParameters<ICoalesceSearchPersistor, QueryType> parameters)
    {
        QueryResultsType result;

        StopWatch watch = new StopWatch();
        
        try
        {
            watch.start();
            
            List<String> properties = new ArrayList<String>();
            properties.add(CoalescePropertyFactory.getEntityKey().getPropertyName());
            properties.add(CoalescePropertyFactory.getName().getPropertyName());
            properties.add(CoalescePropertyFactory.getSource().getPropertyName());
            properties.add(CoalescePropertyFactory.getEntityTitle().getPropertyName());
            properties.addAll(parameters.getParams().getPropertyNames());

            watch.finish();
            System.out.println(watch.getWorkLife());
            watch.start();
            
            Query query = new Query(this.getId(), FilterUtil.fromXml(parameters.getParams().getFilter()));
            query.setPropertyNames(properties);

            watch.finish();
            System.out.println(watch.getWorkLife());
            watch.start();

            CachedRowSet rowset = parameters.getTarget().search(query);

            watch.finish();
            System.out.println(watch.getWorkLife());
            watch.start();

            QueryResultType results = new QueryResultType();

            if (rowset.first())
            {
                List<String> keys = new ArrayList<String>();

                int keyIdx = CoalesceResultSet.getEntityKeyColumn(rowset);

                if (keyIdx == -1)
                {
                    throw new IllegalArgumentException("Missing Column: "
                            + CoalescePropertyFactory.getEntityKey().getPropertyName());
                }

                // Obtain list of keys
                do
                {
                    int idx = 1;

                    HitType hit = new HitType();
                    hit.setEntityKey(rowset.getString(idx++));
                    hit.setName(rowset.getString(idx++));
                    hit.setSource(rowset.getString(idx++));
                    hit.setTitle(rowset.getString(idx++));

                    if (parameters.getParams().getPropertyNames() != null)
                    {
                        for (int ii = idx; ii < parameters.getParams().getPropertyNames().size() + idx; ii++)
                        {
                            hit.getValues().add(rowset.getString(ii));
                        }
                    }

                    results.getHits().add(hit);
                }
                while (rowset.next());
            }

            watch.finish();
            System.out.println(watch.getWorkLife());

            result = new QueryResultsType();
            result.setStatus(EResultStatus.SUCCESS);
            result.setResult(results);

        }
        catch (SAXException | IOException | ParserConfigurationException | CoalescePersistorException | SQLException e)
        {
            result = new QueryResultsType();
            result.setStatus(EResultStatus.FAILED);
            result.setError(e.getMessage());
        }

        return result;
    }

    @Override
    protected Map<String, String> getParameters(QueryType params, boolean isTrace)
    {
        // TODO Not Implemented
        return null;
    }

    @Override
    protected QueryResultsType createResult()
    {
        return new QueryResultsType();
    }
}
