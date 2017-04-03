package com.incadencecorp.coalesce.services.search.service.data.jaxrs;

import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
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
     * @param persister
     */
    public TemplateDataControllerJaxRS(ICoalescePersistor persister) {
        super(persister);
    }
    
}
