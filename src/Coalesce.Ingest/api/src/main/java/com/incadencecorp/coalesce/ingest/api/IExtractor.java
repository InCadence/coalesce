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

package com.incadencecorp.coalesce.ingest.api;

import com.incadencecorp.coalesce.api.ICoalesceComponent;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;

import java.util.List;

/**
 * Interface used for extracting CoalesceEntities from key / value pairs provided by a {@link IConsumer}.
 */
public interface IExtractor extends ICoalesceComponent {

    /**
     * @param key
     * @param value
     * @return a list of extracted entities from the provided key / value pair.
     * @throws CoalesceException on error
     */
    List<CoalesceEntity> extract(String key, String value) throws CoalesceException;

    /**
     * Sets the framework to be used to query the data store for information. Do NOT save the entities in this method.
     *
     * @param framework used for querying for additional information.
     */
    void setFramework(CoalesceSearchFramework framework);

    /**
     * @return a list of templates used by this extractor.
     */
    List<CoalesceEntityTemplate> getTemplatesUsed();

}
