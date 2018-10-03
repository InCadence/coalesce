package com.incadencecorp.coalesce.services.api.mappers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Configurable mapper to be used in blueprints
 */
public class CoalesceMapper extends ObjectMapper {

    public CoalesceMapper()
    {
        this.registerModule(new JodaModule());
        this.configure(com.fasterxml.jackson.databind.SerializationFeature.
                               WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public void setSerializationFeature(SerializationFeature... features)
    {
        for (SerializationFeature feature : features)
        {
            this.enable(feature);
        }
    }

    public void setDeserializationFeature(DeserializationFeature... features)
    {
        for (DeserializationFeature feature : features)
        {
            this.enable(feature);
        }
    }

    public void setMapperFeature(MapperFeature... features)
    {
        for (MapperFeature feature : features)
        {
            this.enable(feature);
        }
    }

    public void setMixInAnnotations(Class<?> target, Class<?> mixinSource)
    {
        this.setMixInAnnotations(target, mixinSource);
    }

}
