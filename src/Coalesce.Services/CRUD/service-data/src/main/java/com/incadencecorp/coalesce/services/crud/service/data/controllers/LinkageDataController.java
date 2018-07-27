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

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.api.Results;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;
import com.incadencecorp.coalesce.services.api.crud.ELinkAction;
import com.incadencecorp.coalesce.services.common.controllers.datamodel.GraphLink;
import com.incadencecorp.coalesce.services.crud.api.ICrudClient;
import org.geotools.data.Query;
import org.geotools.filter.SortByImpl;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.rmi.RemoteException;
import java.sql.SQLException;
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
    private CoalesceSearchFramework search;

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
    public LinkageDataController(ICrudClient crud, CoalesceSearchFramework search)
    {
        this.crud = crud;
        this.search = search;
    }

    public void unlink(List<GraphLink> links) throws RemoteException
    {
        List<DataObjectLinkType> tasks = new ArrayList<>();

        for (GraphLink link : links)
        {
            DataObjectLinkType task = new DataObjectLinkType();
            task.setAction(ELinkAction.UNLINK);
            task.setDataObjectKeySource(link.getSource());
            task.setDataObjectKeyTarget(link.getTarget());
            task.setLinkType(link.getType());
            task.setLabel(link.getLabel());

            tasks.add(task);

            LOGGER.debug("(DELETE) ({})<-[{}:{}]->({})",
                         link.getSource(),
                         link.getType(),
                         link.getLabel(),
                         link.getTarget());
        }

        crud.updateLinkages(tasks.toArray(new DataObjectLinkType[tasks.size()]));
    }

    public void link(List<GraphLink> links) throws RemoteException
    {
        List<DataObjectLinkType> tasks = new ArrayList<>();

        for (GraphLink link : links)
        {
            DataObjectLinkType task = new DataObjectLinkType();
            task.setAction(link.getStatus() == ECoalesceObjectStatus.READONLY ? ELinkAction.MAKEREADONLY : ELinkAction.LINK);
            task.setDataObjectKeySource(link.getSource());
            task.setDataObjectKeyTarget(link.getTarget());
            task.setLinkType(link.getType());
            task.setLabel(link.getLabel());

            tasks.add(task);

            if (link.isBiDirectional())
            {
                // Swap Source & Target
                DataObjectLinkType reverse = new DataObjectLinkType();
                reverse.setAction(task.getAction());
                reverse.setDataObjectKeySource(link.getTarget());
                reverse.setDataObjectKeyTarget(link.getSource());
                reverse.setLinkType(task.getLinkType());
                reverse.setLabel(task.getLabel());

                tasks.add(reverse);

                LOGGER.debug("(CREATE) ({})<-[{}:{}]->({})",
                             link.getSource(),
                             link.getType(),
                             link.getLabel(),
                             link.getTarget());
            }
            else
            {
                LOGGER.debug("(CREATE) ({})-[{}:{}]->({})",
                             link.getSource(),
                             link.getType(),
                             link.getLabel(),
                             link.getTarget());
            }

        }

        crud.updateLinkages(tasks.toArray(new DataObjectLinkType[tasks.size()]));
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

                response.add(link);
            }
        }
        else
        {
            try
            {
                List<PropertyName> properties = new ArrayList<>();
                properties.add(CoalescePropertyFactory.getLinkageEntityKey());
                properties.add(CoalescePropertyFactory.getLinkageLabel());
                properties.add(CoalescePropertyFactory.getLinkageStatus());
                properties.add(CoalescePropertyFactory.getLinkageType());

                Query query = new Query("coalesce", CoalescePropertyFactory.getEntityKey(key));

                query.setProperties(properties);
                query.setStartIndex(0);
                query.setMaxFeatures(200);
                query.setSortBy(new SortBy[] {
                        new SortByImpl(CoalescePropertyFactory.getLastModified(), SortOrder.DESCENDING)
                });

                SearchResults results = search.search(query);

                if (!results.isSuccessful())
                {
                    throw new RemoteException(results.getError());
                }

                try (CachedRowSet rowset = results.getResults())
                {
                    if (rowset.first())
                    {
                        do
                        {
                            GraphLink link = new GraphLink();
                            link.setSource(key);
                            link.setTarget(rowset.getString(2));
                            link.setLabel(rowset.getString(3));
                            link.setStatus(ECoalesceObjectStatus.fromValue(rowset.getString(4)));
                            link.setType(ELinkTypes.getTypeForLabel(rowset.getString(5)));

                            response.add(link);
                        }
                        while (rowset.next());
                    }
                }
            }
            catch (CoalesceException | SQLException e)
            {
                // Rethrow
                throw new RemoteException(e.getMessage(), e);
            }
        }
        return response;
    }

}
