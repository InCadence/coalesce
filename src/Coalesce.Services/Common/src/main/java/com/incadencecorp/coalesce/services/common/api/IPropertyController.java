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

package com.incadencecorp.coalesce.services.common.api;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * @author Derek Clemenzi
 */
public interface IPropertyController {

    /**
     * @param name of file without the extension.
     * @return the json of the file specified.
     */
    String getJsonConfiguration(String name) throws RemoteException;

    /**
     * @param name of file without the extension.
     * @param json to override the content of the filename specified.
     */
    void setJsonConfiguration(String name, String json) throws RemoteException;

    /**
     * @param name property's name to return
     * @return a single property's value.
     */
    String getProperty(String name) throws RemoteException;

    /**
     * Sets a single property value.
     *
     * @param name  property's name
     * @param value property's value
     */
    void setProperty(String name, String value) throws RemoteException;

    /**
     * @return all the properties and their values that are handled by this controller.
     */
    Map<String, String> getProperties() throws RemoteException;

    /**
     * @return specified properties and their values.
     */
    Map<String, String> getProperties(String[] names) throws RemoteException;

    /**
     * Sets multiple property's values. If the connector is readonly then this method wont do anything. Also this should be restricted to privileged users.
     *
     * @param values to set.
     */
    void setProperties(Map<String, String> values) throws RemoteException;

}
