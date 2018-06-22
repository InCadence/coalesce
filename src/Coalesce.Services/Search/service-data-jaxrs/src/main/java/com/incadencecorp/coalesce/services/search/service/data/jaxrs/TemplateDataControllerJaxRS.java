package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import com.incadencecorp.coalesce.framework.CoalesceFramework;
import com.incadencecorp.coalesce.services.search.service.data.controllers.TemplateDataController;

/**
 * JaxRs Implementation
 *
 * @author Derek Clemenzi
 */
public class TemplateDataControllerJaxRS extends TemplateDataController implements ITemplateDataControllerJaxRS {

    /**
     * Production Constructor
     *
     * @param framework
     */
    public TemplateDataControllerJaxRS(CoalesceFramework framework) {
        super(framework);
    }

}
