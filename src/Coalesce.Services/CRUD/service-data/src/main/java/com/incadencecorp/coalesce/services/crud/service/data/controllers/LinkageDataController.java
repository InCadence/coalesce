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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.EnumHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceLinkage;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.datamodel.ELinkTypes;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import com.incadencecorp.coalesce.search.api.SearchResults;
import com.incadencecorp.coalesce.search.factory.CoalescePropertyFactory;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;
import com.incadencecorp.coalesce.services.api.crud.ELinkAction;
import com.incadencecorp.coalesce.services.common.CoalesceRemoteException;
import com.incadencecorp.coalesce.services.common.ServiceBase;
import com.incadencecorp.coalesce.services.common.api.ILinkageDataController;
import com.incadencecorp.coalesce.services.common.controllers.datamodel.GraphLink;
import com.incadencecorp.coalesce.services.crud.service.data.controllers.jobs.UpdateDataObjectLinkagesJob;
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
public class LinkageDataController extends ServiceBase<CoalesceFramework> implements ILinkageDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkageDataController.class);

    /**
     * @param framework used for modifying / retrieving linkages
     */
    public LinkageDataController(CoalesceSearchFramework framework)
    {
        super(framework, framework.getExecutorService());
    }

    @Override
    public void unlink(List<GraphLink> links) throws RemoteException
    {
        List<DataObjectLinkType> tasks = new ArrayList<>();

        for (GraphLink link : links)
        {
            verifyNonNullArgument("Type", link.getType());
            verifyNonNullArgument("Source", link.getSource());
            verifyNonNullArgument("Target", link.getTarget());

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

        verify(performJob(new UpdateDataObjectLinkagesJob(createDataObjectLinkRequest(false, tasks))).getResult());
    }

    @Override
    public void link(List<GraphLink> links) throws RemoteException
    {
        List<DataObjectLinkType> tasks = new ArrayList<>();

        for (GraphLink link : links)
        {
            verifyNonNullArgument("Type", link.getType());
            verifyNonNullArgument("Source", link.getSource());
            verifyNonNullArgument("Target", link.getTarget());

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
                reverse.setLinkType(task.getLinkType().getReciprocalLinkType());
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

        verify(performJob(new UpdateDataObjectLinkagesJob(createDataObjectLinkRequest(false, tasks))).getResult());
    }

    @Override
    public List<GraphLink> retrieveLinkages(String key) throws RemoteException
    {
        List<GraphLink> response = new ArrayList<>();

        if (!(getTarget() instanceof CoalesceSearchFramework))
        {
            try
            {
                CoalesceEntity entity = getTarget().getCoalesceEntity(key);

                for (CoalesceLinkage linkage : entity.getLinkages().values())
                {
                    if (!linkage.isMarkedDeleted())
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
            }
            catch (CoalescePersistorException e)
            {
                throw new CoalesceRemoteException(e.getMessage(), e);
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
                properties.add(CoalescePropertyFactory.getLastModified());

                Query query = new Query("coalesce", CoalescePropertyFactory.getEntityKey(key));

                query.setProperties(properties);
                query.setStartIndex(0);
                query.setMaxFeatures(200);
                query.setSortBy(new SortBy[] {
                        new SortByImpl(CoalescePropertyFactory.getLastModified(), SortOrder.DESCENDING)
                });

                SearchResults results = ((CoalesceSearchFramework) getTarget()).search(query);

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
                            link.setStatus(EnumHelper.stringToECoalesceObjectStatus(rowset.getString(4)));
                            link.setType(ELinkTypes.getTypeForLabel(rowset.getString(5)));

                            if (!StringHelper.isNullOrEmpty(link.getTarget()))
                            {
                                response.add(link);
                            }
                        }
                        while (rowset.next());
                    }
                }
            }
            catch (CoalesceException | SQLException | InterruptedException e)
            {
                // Rethrow
                throw new RemoteException(e.getMessage(), e);
            }
        }
        return response;
    }

    private DataObjectLinkRequest createDataObjectLinkRequest(final boolean async, final List<DataObjectLinkType> tasks)
    {
        DataObjectLinkRequest request = new DataObjectLinkRequest();

        request.getLinkagelist().addAll(tasks);
        request.setAsyncCall(async);

        return request;
    }

    private void verifyNonNullArgument(String name, Object argument) throws RemoteException
    {
        if (argument == null)
        {
            throw new RemoteException(String.format(CoalesceErrors.INVALID_INPUT_REASON, name, "Cannot be null"));
        }
    }

}
