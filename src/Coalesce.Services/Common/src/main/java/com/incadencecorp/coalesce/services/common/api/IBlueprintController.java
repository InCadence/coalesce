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

import com.incadencecorp.coalesce.services.api.datamodel.graphson.Graph;

import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Derek Clemenzi
 */
public interface IBlueprintController {

    /**
     * @return a list of xml documents within the configured directory.
     */
    List<String> getBlueprints();

    /**
     * @param name filename to render.
     * @return nodes and linkages of the bean for the specified xml document.
     * @throws RemoteException on error
     */
    Graph getBlueprint(String name) throws RemoteException;

    /**
     * @param name filename to edit
     * @param changes changes to be written to file
     */
    void editBlueprint(String name, String changes) throws Exception;

    /**
     * Return raw XML from rest-blueprints.xml
     * @param id ID of xml NODE to returned
     * @return
     */
    String getXML(String filename, String id) throws Exception;

    /**
     * remove bean from XML file
     * @param json JSON of id key
     * @param name filename
     * @throws Exception
     */
    void removeBean(String name, String json) throws Exception;
}
