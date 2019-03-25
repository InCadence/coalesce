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

package com.incadencecorp.coalece.services.crud.client.jaxrs.tests;

import com.incadencecorp.coalesce.datamodel.impl.pojo.record.EnumMetadataPojoRecord;
import com.incadencecorp.coalesce.services.api.datamodel.EnumValuesRecord;
import com.incadencecorp.coalesce.services.common.client.jaxrs.EnumerationClientJaxRS;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public class EnumerationClientJaxRSIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumerationClientJaxRSIT.class);

    @Test
    public void testClient() throws Exception
    {
        EnumerationClientJaxRS client = new EnumerationClientJaxRS("http://localhost:8181/cxf/data/enumerations");
        for (EnumMetadataPojoRecord enumeration : client.getEnumerations())
        {
            LOGGER.info(enumeration.getEnumname());

            for (EnumValuesRecord value : client.getEnumerationValues(enumeration.getKey()))
            {
                LOGGER.info("\t{}", value.getValue());

                for (Map.Entry<String, String> associated : client.getEnumerationAssociatedValues(enumeration.getKey(),
                                                                                                  value.getKey()).entrySet())
                {
                    LOGGER.info("\t\t{} = {}", associated.getKey(), associated.getValue());
                }
            }
        }

    }

}
