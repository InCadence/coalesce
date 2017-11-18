package com.incadencecorp.coalesce.services.search.service.rest.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldDefinition;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.services.search.service.data.controllers.TemplateDataController;
import com.incadencecorp.coalesce.services.search.service.rest.controllers.api.ITemplateDataSpring;

@RestController
public class TemplateDataControllerSpring extends TemplateDataController implements ITemplateDataSpring {

    private static final Map<String, CoalesceEntity> TEMPLATES = new HashMap<String, CoalesceEntity>();
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateDataController.class);

    /**
     * Default Constructor (Used for Testing)
     */
    public TemplateDataControllerSpring()
    {
        this(null);
        
        // TODO Remove this code
        if (TEMPLATES.size() == 0)
        {
            LOGGER.warn("Creating Mock Templates");

            CoalesceEntity template1 = CoalesceEntity.create("Template1", "Test", "1.0", null, null);

            CoalesceSection section = CoalesceSection.create(template1, "Test1 Section");
            CoalesceRecordset recordset = CoalesceRecordset.create(section, "Test1 RS");

            CoalesceFieldDefinition.create(recordset, "field1", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
            CoalesceFieldDefinition.create(recordset, "field2", ECoalesceFieldDataTypes.STRING_TYPE);

            TEMPLATES.put(template1.getKey(), template1);

            CoalesceEntity template2 = CoalesceEntity.create("Template2", "Test", "1.0", null, null);
            CoalesceSection section2 = CoalesceSection.create(template2, "Test2 Section");
            CoalesceRecordset recordset2 = CoalesceRecordset.create(section2, "Test2 RS");

            CoalesceFieldDefinition.create(recordset2, "field3", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
            CoalesceFieldDefinition.create(recordset2, "field4", ECoalesceFieldDataTypes.STRING_TYPE);

            CoalesceRecordset recordset3 = CoalesceRecordset.create(section2, "Test3 RS");

            CoalesceFieldDefinition.create(recordset3, "field5", ECoalesceFieldDataTypes.BOOLEAN_TYPE);
            CoalesceFieldDefinition.create(recordset3, "field6", ECoalesceFieldDataTypes.STRING_TYPE);

            TEMPLATES.put(template2.getKey(), template2);
        }


    }

    public TemplateDataControllerSpring(ICoalescePersistor persister)
    {
        super(persister);
    }

}
