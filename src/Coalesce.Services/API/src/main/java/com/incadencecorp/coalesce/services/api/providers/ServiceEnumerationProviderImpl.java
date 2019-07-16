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

package com.incadencecorp.coalesce.services.api.providers;

import com.incadencecorp.coalesce.datamodel.impl.pojo.record.EnumMetadataPojoRecord;
import com.incadencecorp.coalesce.framework.enumerationprovider.impl.AbstractEnumerationProvider;
import com.incadencecorp.coalesce.services.api.IEnumerationDataController;
import com.incadencecorp.coalesce.services.api.datamodel.EnumValuesRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

/**
 * @author Derek Clemenzi
 */
public class ServiceEnumerationProviderImpl extends AbstractEnumerationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEnumerationProviderImpl.class);

    private final IEnumerationDataController controller;

    public ServiceEnumerationProviderImpl(IEnumerationDataController controller)
    {
        this.controller = controller;
    }

    @Override
    public void populate(Principal principal)
    {
        try
        {
            for (EnumMetadataPojoRecord enumeration : controller.getEnumerations())
            {
                this.addEnumeration(principal,
                                    enumeration.getEnumname(),
                                    convert(controller.getEnumerationValues(enumeration.getKey())));
            }
        }
        catch (RemoteException e)
        {
            LOGGER.error("Failed to populate enumeration provider: {}", this.getClass().getSimpleName());
        }
    }

    @Override
    protected List<String> lookup(Principal principal, String enumeration)
    {
        List<String> results = null;
        try
        {
            for (EnumMetadataPojoRecord record : controller.getEnumerations())
            {
                if (record.getEnumname().equalsIgnoreCase(enumeration))
                {
                    results = convert(controller.getEnumerationValues(record.getKey()));
                }
            }
        }
        catch (RemoteException e)
        {
            LOGGER.error("Failed to populate enumeration provider: {}", this.getClass().getSimpleName());
        }

        return results;
    }

    private List<String> convert(List<EnumValuesRecord> records)
    {
        int maxOrdinal = 0;

        for (EnumValuesRecord value : records)
        {
            if (value.getOrdinal() > maxOrdinal)
            {
                maxOrdinal = value.getOrdinal();
            }
        }

        String[] values = new String[maxOrdinal];

        for (EnumValuesRecord value : records)
        {
            values[value.getOrdinal()] = value.getValue();
        }

        return Arrays.asList(values);

    }

}
