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
