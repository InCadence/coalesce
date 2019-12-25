/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.framework;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceField;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;

/**
 * Default normalizer for creating keys.
 * 
 * @author n78554
 */
public class DefaultNormalizer implements ICoalesceNormalizer {

    /**
     * DefaultNormalizer's parameters
     * 
     * @author n78554
     */
    public enum EParameters
    {

        /**
         * Specifies the maximum length (integer) of the normalized keys
         */
        MAX_LENGTH("com.incadencecorp.defaultnormalizer.maxlength", "41"),
        SEPARATOR("com.incadencecorp.defaultnormalizer.separator", ".");

        private String name;
        private String defaultValue;

        EParameters(String paramName, String paramDefault)
        {
            name = paramName;
            defaultValue = paramDefault;
        }

        /**
         * @return the param's default name.
         */
        public String getName()
        {
            return name;
        }

        /**
         * @return the param's default value.
         */
        public String getDefaultValue()
        {
            return defaultValue;
        }

    }

    private final Map<String, String> params = new HashMap<>();

    /**
     * Use default parameters.
     */
    public DefaultNormalizer()
    {
        this(Collections.emptyMap());
    }

    /**
     * Specify parameters.
     * 
     * @param params
     */
    public DefaultNormalizer(final Map<String, String> params)
    {
        this.params.putAll(params);

        if (!this.params.containsKey(EParameters.MAX_LENGTH.getName()))
        {
            this.params.put(EParameters.MAX_LENGTH.getName(), EParameters.MAX_LENGTH.getDefaultValue());
        }

        if (!this.params.containsKey(EParameters.SEPARATOR.getName()))
        {
            this.params.put(EParameters.SEPARATOR.getName(), EParameters.SEPARATOR.getDefaultValue());
        }
    }

    /**
     * Sets an individual parameter.
     * 
     * @param param
     * @param value
     */
    public void setParameter(EParameters param, String value)
    {
        params.put(param.getName(), value);
    }

    @Override
    public String normalize(final CoalesceRecordset recordset, final CoalesceFieldDefinition definition)
    {
        return normalize(recordset.getName(), definition.getName());
    }

    @Override
    public String normalize(final CoalesceRecordset recordset, final CoalesceField<?> field)
    {
        return normalize(recordset.getName(), field.getName());
    }

    @Override
    public String normalize(final String recordsetname, final String fieldname)
    {
        return normalize(recordsetname) + params.get(EParameters.SEPARATOR.getName()) + normalize(fieldname);
    }

    @Override
    public String normalize(final String value)
    {
        int maxLen = Integer.parseInt(params.get(EParameters.MAX_LENGTH.getName()));

        String result = value.replace(" ", "_").replaceAll("[^a-zA-Z0-9_\\s]", "").toLowerCase();
        
        if (result.length() > maxLen)
        {
            // Truncate
            result = result.substring(0, maxLen) + "_";
        }
        
        return result;
    }

}
