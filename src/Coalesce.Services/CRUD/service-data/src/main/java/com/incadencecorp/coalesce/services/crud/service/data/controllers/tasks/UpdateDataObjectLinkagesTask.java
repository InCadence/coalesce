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

package com.incadencecorp.coalesce.services.crud.service.data.controllers.tasks;

import com.incadencecorp.coalesce.api.EResultStatus;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.helpers.EntityLinkHelper;
import com.incadencecorp.coalesce.enums.ECrudOperations;
import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceObjectStatus;
import com.incadencecorp.coalesce.framework.tasks.AbstractFrameworkTask;
import com.incadencecorp.coalesce.framework.tasks.TaskParameters;
import com.incadencecorp.coalesce.framework.util.CoalesceNotifierUtil;
import com.incadencecorp.coalesce.services.api.common.ResultsType;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkType;

import java.util.HashMap;
import java.util.Map;

public class UpdateDataObjectLinkagesTask extends AbstractFrameworkTask<DataObjectLinkType[], ResultsType> {

    @Override
    protected ResultsType doWork(TaskParameters<CoalesceFramework, DataObjectLinkType[]> parameters) throws CoalesceException
    {
        ResultsType result = new ResultsType();
        CoalesceFramework framework = parameters.getTarget();
        DataObjectLinkType[] params = parameters.getParams();

        String user = parameters.getPrincipalName();
        String ip = parameters.getPrincipalIp();

        // TODO Implement Authorization
        // boolean isSysAdmin = UserValidator.isSysAdmin(userId);
        boolean isSysAdmin = true;

        Map<String, CoalesceEntity> map = new HashMap<>();

        // Determine complete list of keys
        for (DataObjectLinkType task : params)
        {
            // Same Source & Target?
            if (task.getDataObjectKeySource().equalsIgnoreCase(task.getDataObjectKeyTarget()))
            {
                throw new CoalesceException("Linking an object to itself is not allowed");
            }

            map.put(task.getDataObjectKeySource(), null);
            map.put(task.getDataObjectKeyTarget(), null);
        }

        // Retrieve Entities
        CoalesceEntity[] entities = framework.getCoalesceEntities(map.keySet().toArray(new String[0]));

        // Update Map with Entity
        for (CoalesceEntity entity : entities)
        {
            map.put(entity.getKey(), entity);
        }

        // Link Objects
        for (DataObjectLinkType task : params)
        {
            CoalesceEntity entity1 = map.get(task.getDataObjectKeySource());
            CoalesceEntity entity2 = map.get(task.getDataObjectKeyTarget());

            switch (task.getAction())
            {
            case MAKEREADONLY:
                EntityLinkHelper.linkEntitiesUniDirectional(entity1,
                                              task.getLinkType(),
                                              entity2,
                                              user,
                                              ip,
                                              task.getLabel(),
                                              true,
                                              ECoalesceObjectStatus.READONLY,
                                              isSysAdmin);
                break;
            case LINK:

                EntityLinkHelper.linkEntitiesUniDirectional(entity1,
                                              task.getLinkType(),
                                              entity2,
                                              user,
                                              ip,
                                              task.getLabel(),
                                              true,
                                              ECoalesceObjectStatus.ACTIVE,
                                              isSysAdmin);
                break;
            case UNLINK:
                EntityLinkHelper.unLinkEntities(entity1, entity2, task.getLinkType(), user, ip, isSysAdmin);
                break;
            }

        }

        // Save Objects
        if (framework.saveCoalesceEntity(entities))
        {
            result.setStatus(EResultStatus.SUCCESS);

            // Send Notifications
            for (DataObjectLinkType task : params)
            {
                CoalesceEntity entity1 = map.get(task.getDataObjectKeySource());
                CoalesceEntity entity2 = map.get(task.getDataObjectKeyTarget());

                switch (task.getAction())
                {
                case MAKEREADONLY:
                    CoalesceNotifierUtil.sendLinkage(getName(),
                                                     ECrudOperations.UPDATE,
                                                     entity1,
                                                     task.getLinkType(),
                                                     entity2);
                    break;
                case LINK:
                    CoalesceNotifierUtil.sendLinkage(getName(),
                                                     ECrudOperations.CREATE,
                                                     entity1,
                                                     task.getLinkType(),
                                                     entity2);
                    break;
                case UNLINK:
                    CoalesceNotifierUtil.sendLinkage(getName(),
                                                     ECrudOperations.DELETE,
                                                     entity1,
                                                     task.getLinkType(),
                                                     entity2);
                    break;
                }

            }
        }
        else
        {
            result.setStatus(EResultStatus.FAILED);
        }

        // TODO Since UpdateLinkageTask no longer always processes a single
        // task there is no longer a one to one relationship to what the
        // user requested and the result set they gets back. This means if
        // one of the task fails its impossible to determine what linkages
        // were involved in that task. This will need to be refactored to
        // return a list of results ( 1 / Linkage processed).

        return result;
    }

    @Override
    protected Map<String, String> getParameters(DataObjectLinkType[] params, boolean isTrace)
    {
        Map<String, String> results = new HashMap<>();

        for (DataObjectLinkType type : params)
        {
            results.put("source", type.getDataObjectKeySource());
            results.put("target", type.getDataObjectKeyTarget());
            results.put("label", type.getLabel());
            results.put("action", type.getAction().toString());
            results.put("link type", type.getLinkType() != null ? type.getLinkType().toString() : "");
        }

        return results;
    }

    @Override
    protected ResultsType createResult()
    {
        return new ResultsType();
    }

}
