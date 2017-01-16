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

package com.incadencecorp.coalesce.services.crud.service;

import java.util.concurrent.ExecutorService;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.CrudManager;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectUpdateStatusRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectXmlRequest;
import com.incadencecorp.coalesce.services.common.ServiceBase;
import com.incadencecorp.coalesce.services.crud.service.jobs.CreateDataObjectJob;
import com.incadencecorp.coalesce.services.crud.service.jobs.UpdateDataObjectLinkagesJob;
import com.incadencecorp.coalesce.services.crud.service.jobs.RetrieveDataObjectJob;
import com.incadencecorp.coalesce.services.crud.service.jobs.UpdateDataObjectJob;
import com.incadencecorp.coalesce.services.crud.service.jobs.UpdateDataObjectStatusJob;

public class CrudServiceImpl extends ServiceBase implements CrudManager {

    public CrudServiceImpl(CoalesceFramework framework) {
        super(framework);
    }
    
    @Override
    public StringResponse retrieveDataObject(DataObjectKeyRequest request)
    {
        return (StringResponse) performJob(new RetrieveDataObjectJob(request));
    }

    @Override
    public StringResponse createDataObject(DataObjectXmlRequest request)
    {
        return (StringResponse) performJob(new CreateDataObjectJob(request));
    }

    @Override
    public StringResponse updateDataObjectStatus(DataObjectUpdateStatusRequest request)
    {
        return (StringResponse) performJob(new UpdateDataObjectStatusJob(request));
    }

    @Override
    public StringResponse updateDataObjectLinkages(DataObjectLinkRequest request)
    {
        return (StringResponse) performJob(new UpdateDataObjectLinkagesJob(request));
    }

    @Override
    public StringResponse updateDataObject(DataObjectXmlRequest request)
    {
        return (StringResponse) performJob(new UpdateDataObjectJob(request));
    }

}
