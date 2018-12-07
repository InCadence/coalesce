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

package com.incadencecorp.coalesce.services.crud.service.jobs;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.tasks.AbstractTask;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;
import com.incadencecorp.coalesce.services.common.jobs.AbstractFrameworkServiceJob;
import com.incadencecorp.coalesce.services.crud.service.tasks.UpdateDataObjectLinkagesTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class UpdateDataObjectLinkagesJob extends AbstractFrameworkServiceJob<DataObjectLinkRequest, StringResponse, ResultsType> {

    public UpdateDataObjectLinkagesJob(DataObjectLinkRequest request)
    {
        super(request);
    }

    @Override
    protected Collection<AbstractTask<?, ResultsType, CoalesceFramework>> getTasks(DataObjectLinkRequest params) throws CoalesceException
    {
        List<AbstractTask<?, ResultsType, CoalesceFramework>> tasks = new ArrayList<AbstractTask<?, ResultsType, CoalesceFramework>>();
        
        for (DataObjectLinkBucket bucket : groupIntoBuckets(params.getLinkagelist()))
        {
            UpdateDataObjectLinkagesTask task = new UpdateDataObjectLinkagesTask();
            task.setParams(bucket.getItems());

            tasks.add(task);
        }

        return tasks;
    }

    @Override
    protected StringResponse createResponse()
    {
        return new StringResponse();
    }

    @Override
    protected ResultsType createResults()
    {
        return new ResultsType(); 
    }

    /**
     * Groups the linkage tasks that share keys into common buckets. This solves
     * two issues: (1) Performance have to load / save the same object multiple
     * time for different linkage task, (2) Read - Modify - Write conflicts
     * between treads. The second issue also has be handled by locking or
     * Optimistic Concurrency (OCC) at a lower level but this will prevent a
     * single request from a user from conflicting.
     *
     * @param linkages
     * @return
     */
    private List<DataObjectLinkBucket> groupIntoBuckets(List<DataObjectLinkType> linkages) throws CoalesceException
    {

        List<DataObjectLinkBucket> buckets = new ArrayList<DataObjectLinkBucket>();
        DataObjectLinkBucket container;

        // Create Tasks
        for (DataObjectLinkType linkage : linkages)
        {

            // Action Specified by Client?
            if (linkage.getAction() == null)
            {
                throw new CoalesceException(String.format(CoalesceErrors.NOT_SPECIFIED, "Link Action"));
            }

            // Reset
            container = null;

            // Iterate Existing Buckets
            for (DataObjectLinkBucket bucket : buckets)
            {

                // Belongs?
                if (bucket.contains(linkage))
                {

                    // Already Has Bucket?
                    if (container != null)
                    {

                        // Yes; Combine Buckets
                        container.combine(bucket);

                    }
                    else
                    {

                        // No; Add to Existing Bucket
                        container = bucket;

                        container.add(linkage);

                    }
                }
            }

            // Added to Bucket?
            if (container == null)
            {
                // No; Create New Bucket
                buckets.add(new DataObjectLinkBucket(linkage));
            }
        }

        return buckets;

    }

    /*--------------------------------------------------------------------------
    Private Classes
    --------------------------------------------------------------------------*/

    private static class DataObjectLinkBucket {

        private HashSet<String> keys;
        private List<DataObjectLinkType> linkages;

        /**
         * Creates a new bucket containing the linkage.
         *
         * @param linkage
         */
        public DataObjectLinkBucket(DataObjectLinkType linkage)
        {

            keys = new HashSet<String>();
            linkages = new ArrayList<DataObjectLinkType>();

            add(linkage);

        }

        public boolean contains(DataObjectLinkType linkage)
        {

            return keys.contains(linkage.getDataObjectKeySource()) || keys.contains(linkage.getDataObjectKeyTarget());

        }

        public void add(DataObjectLinkType linkage)
        {

            keys.add(linkage.getDataObjectKeySource());
            keys.add(linkage.getDataObjectKeyTarget());

            linkages.add(linkage);

        }

        public void combine(DataObjectLinkBucket bucket)
        {
            keys.addAll(bucket.keys);
            linkages.addAll(bucket.linkages);

            bucket.linkages.clear();
            bucket.keys.clear();
        }

        public DataObjectLinkType[] getItems()
        {
            return linkages.toArray(new DataObjectLinkType[linkages.size()]);
        }

    }
}
