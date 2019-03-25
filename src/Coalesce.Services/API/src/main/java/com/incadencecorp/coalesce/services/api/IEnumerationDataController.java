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

package com.incadencecorp.coalesce.services.api;

import com.incadencecorp.coalesce.datamodel.impl.pojo.entity.EnumerationPojoEntity;
import com.incadencecorp.coalesce.datamodel.impl.pojo.record.EnumMetadataPojoRecord;
import com.incadencecorp.coalesce.services.api.datamodel.EnumValuesRecord;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Interface for retrieving Coalesce enumerations.
 *
 * @author Derek Clemenzi
 */
public interface IEnumerationDataController {

    /**
     * @return a list of available enumerations.
     * @throws RemoteException on error
     */
    List<EnumMetadataPojoRecord> getEnumerations() throws RemoteException;

    /**
     * @param key of the enumeration to return
     * @return an enumeration in JSON format
     * @throws RemoteException on error
     */
    EnumerationPojoEntity getEnumeration(String key) throws RemoteException;

    /**
     * @param key of the enumeration's values to return
     * @return the values of the specified enumeration
     * @throws RemoteException on error
     */
    List<EnumValuesRecord> getEnumerationValues(String key) throws RemoteException;

    /**
     * @param key      of the enumeration
     * @param valuekey of the value
     * @return the key / value pairs associated with the enumeration's value.
     * @throws RemoteException on error
     */
    Map<String, String> getEnumerationAssociatedValues(String key, String valuekey) throws RemoteException;

}
