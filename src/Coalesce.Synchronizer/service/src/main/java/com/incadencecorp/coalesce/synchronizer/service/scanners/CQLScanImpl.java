/*
 *  Copyright 2017 - InCadence Strategic Solutions Inc., All Rights Reserved
 *
 *  Notwithstanding any contractor copyright notice, the Government has Unlimited
 *  Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 *  of this work other than as specifically authorized by these DFARS Clauses may
 *  violate Government rights in this work.
 *
 *  DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 *  Unlimited Rights. The Government has the right to use, modify, reproduce,
 *  perform, display, release or disclose this computer software and to have or
 *  authorize others to do so.
 *
 *  Distribution Statement D. Distribution authorized to the Department of
 *  Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 *
 */

package com.incadencecorp.coalesce.synchronizer.service.scanners;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.synchronizer.api.common.AbstractScan;
import com.incadencecorp.coalesce.synchronizer.api.common.SynchronizerParameters;
import org.geotools.data.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.util.Map;

/**
 * This implementation returns results as specified by the CQL.
 *
 * @author n78554
 * @see SynchronizerParameters#PARAM_SCANNER_CQL
 */
public class CQLScanImpl extends AbstractScan {

    private static final Logger LOGGER = LoggerFactory.getLogger(CQLScanImpl.class);

    private int max = SynchronizerParameters.DEFAULT_SCANNER_MAX;

    @Override
    public CachedRowSet doScan(Query query) throws CoalesceException
    {
        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("Filter Specified: {}", query.getFilter().toString());
        }

        query.setStartIndex(0);
        query.setMaxFeatures(max);

            return getSource().search(query).getResults();
    }

    @Override
    public void setProperties(Map<String, String> properties)
    {
        super.setProperties(properties);

        // Max Configured?
        if (parameters.containsKey(SynchronizerParameters.PARAM_SCANNER_MAX))
        {
            max = Integer.parseInt(parameters.get(SynchronizerParameters.PARAM_SCANNER_MAX));
        }
        else if (loader != null)
        {
            String value = loader.getProperty(SynchronizerParameters.PARAM_SCANNER_MAX);

            if (!StringHelper.isNullOrEmpty(value))
            {
                max = Integer.parseInt(value);
            }
        }
    }

}
