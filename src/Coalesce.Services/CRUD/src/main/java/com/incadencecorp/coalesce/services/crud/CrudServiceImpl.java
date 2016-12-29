package com.incadencecorp.coalesce.services.crud;

import com.incadencecorp.coalesce.services.api.common.StringResponse;
import com.incadencecorp.coalesce.services.api.crud.CrudManager;
import com.incadencecorp.coalesce.services.api.crud.DataObjectKeyRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectLinkRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectUpdateStatusRequest;
import com.incadencecorp.coalesce.services.api.crud.DataObjectXmlRequest;
import com.incadencecorp.coalesce.services.common.ServiceBase;
import com.incadencecorp.coalesce.services.crud.jobs.CreateDataObjectJob;
import com.incadencecorp.coalesce.services.crud.jobs.LinkDataObjectJob;
import com.incadencecorp.coalesce.services.crud.jobs.RetrieveDataObjectJob;
import com.incadencecorp.coalesce.services.crud.jobs.UpdateDataObjectJob;
import com.incadencecorp.coalesce.services.crud.jobs.UpdateDataObjectStatusJob;

public class CrudServiceImpl extends ServiceBase implements CrudManager {

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
    public StringResponse linkDataObject(DataObjectLinkRequest request)
    {
        return (StringResponse) performJob(new LinkDataObjectJob(request));
    }

    @Override
    public StringResponse updateDataObject(DataObjectXmlRequest request)
    {
        return (StringResponse) performJob(new UpdateDataObjectJob(request));
    }

}
