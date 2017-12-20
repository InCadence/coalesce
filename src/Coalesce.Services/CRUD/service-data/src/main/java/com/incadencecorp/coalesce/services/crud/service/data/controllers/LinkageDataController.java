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

package com.incadencecorp.coalesce.services.crud.service.data.controllers;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;
import com.incadencecorp.coalesce.services.api.crud.ELinkAction;
import com.incadencecorp.coalesce.services.api.search.HitType;
import com.incadencecorp.coalesce.services.api.search.SearchDataObjectResponse;
import com.incadencecorp.coalesce.services.common.controllers.datamodel.GraphLink;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import com.incadencecorp.coalesce.services.search.api.ISearchClient;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for linking / un-linking entities
 *
 * @author Derek Clemenzi
 */
public class LinkageDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkageDataController.class);

    private ICrudClient crud;
    private ISearchClient search;

    /**
     * Default Constructor
     *
     * @param crud used for modifying / retrieving linkages
     */
    public LinkageDataController(ICrudClient crud)
    {
        this(crud, null);
    }

    /**
     * @param crud   used for modifying linkages
     * @param search used for retrieving linkages
     */
    public LinkageDataController(ICrudClient crud, ISearchClient search)
    {
        this.crud = crud;
        this.search = search;
    }

    public void unlink(List<GraphLink> links) throws RemoteException
    {
        DataObjectLinkType[] tasks = new DataObjectLinkType[links.size()];

        for (int ii = 0; ii < tasks.length; ii++)
        {
            GraphLink link = links.get(ii);

            DataObjectLinkType task = new DataObjectLinkType();
            task.setAction(ELinkAction.UNLINK);
            task.setDataObjectKeySource(link.getSource());
            task.setDataObjectKeyTarget(link.getTarget());
            task.setLinkType(link.getType());
            task.setLabel(link.getLabel());

            LOGGER.debug("(DELETE) ({})<-[{}:{}]->({})",
                         link.getSource(),
                         link.getType(),
                         link.getLabel(),
                         link.getTarget());
        }

        crud.updateLinkages(tasks);
    }

    public void link(List<GraphLink> links) throws RemoteException
    {
        DataObjectLinkType[] tasks = new DataObjectLinkType[links.size()];

        for (int ii = 0; ii < tasks.length; ii++)
        {
            GraphLink link = links.get(ii);

            DataObjectLinkType task = new DataObjectLinkType();
            task.setAction(link.getStatus() == ECoalesceObjectStatus.READONLY ? ELinkAction.MAKEREADONLY : ELinkAction.LINK);
            task.setDataObjectKeySource(link.getSource());
            task.setDataObjectKeyTarget(link.getTarget());
            task.setLinkType(link.getType());
            task.setLabel(link.getLabel());
            // TODO Not Implemented
            // link.setByDirectional(true);

            LOGGER.debug("(CREATE) ({})<-[{}:{}]->({})",
                         link.getSource(),
                         link.getType(),
                         link.getLabel(),
                         link.getTarget());
        }

        crud.updateLinkages(tasks);
    }

    public List<GraphLink> retrieveLinkages(String key) throws RemoteException
    {
        List<GraphLink> response = new ArrayList<>();

        if (search == null)
        {
            Results<CoalesceEntity> result = crud.retrieveDataObjects(key)[0];
            if (!result.isSuccessful())
            {
                throw new RemoteException(result.getError());
            }

            for (CoalesceLinkage linkage : result.getResult().getLinkages().values())
            {
                GraphLink link = new GraphLink();
                link.setSource(linkage.getEntity1Key());
                link.setTarget(linkage.getEntity2Key());
                link.setLabel(linkage.getLabel());
                link.setStatus(linkage.getStatus());
                link.setType(linkage.getLinkType());
                // TODO Not Implemented
                // link.setByDirectional(true);

                response.add(link);
            }
        }
        else
        {
            try
            {
                PropertyName[] properties = new PropertyName[] { CoalescePropertyFactory.getLinkageLabel(),
                                                                 CoalescePropertyFactory.getLinkageStatus(),
                                                                 CoalescePropertyFactory.getLinkageType()
                };

                SearchDataObjectResponse searchResult = search.search(CoalescePropertyFactory.getLinkageEntityKey(key),
                                                                      1,
                                                                      properties,
                                                                      null,
                                                                      true);

                if (searchResult.getStatus() != EResultStatus.SUCCESS)
                {
                    throw new RemoteException(searchResult.getError());
                }

                if (searchResult.getResult().get(0).getStatus() != EResultStatus.SUCCESS)
                {
                    throw new RemoteException(searchResult.getResult().get(0).getError());
                }

                for (HitType hit : searchResult.getResult().get(0).getResult().getHits())
                {
                    GraphLink link = new GraphLink();
                    link.setSource(key);
                    link.setTarget(hit.getEntityKey());
                    link.setLabel(hit.getValues().get(0));
                    link.setStatus(ECoalesceObjectStatus.values()[Integer.parseInt(hit.getValues().get(1))]);
                    link.setType(ELinkTypes.values()[Integer.parseInt(hit.getValues().get(2))]);

                    response.add(link);
                }

            }
            catch (CoalesceException e)
            {
                // Rethrow
                throw new RemoteException(e.getMessage(), e);
            }
        }
        return response;
    }

}
