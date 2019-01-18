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
package com.incadencecorp.coalesce.nifi.processors.entity.csv_extractor;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistorSearch;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchSettings;
import com.incadencecorp.coalesce.ingest.api.IExtractor;
import com.incadencecorp.coalesce.ingest.plugins.fsi.FSI_EntityExtractor;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.LogLevel;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.elasticsearch.index.IndexNotFoundException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Tags({ "weewoo dsfsafdsadf" })
@CapabilityDescription("New description")
@SeeAlso()
@ReadsAttributes({ @ReadsAttribute(attribute = "") })
@WritesAttributes({ @WritesAttribute(attribute = "") })
public class JsonCsvExtractor extends AbstractProcessor {

    public static final PropertyDescriptor TEMPLATE_JSON = new PropertyDescriptor.Builder().name("json").displayName("JSON Template").description(
            "Copy & Paste JSON template. Format is on the coalesce wiki").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public static final PropertyDescriptor PARAM_HAS_HEADERS = new PropertyDescriptor.Builder().name("hasHeaders").displayName("Data Contains Headers").description(
            "(Boolean) Indicates whether or not the data source contains headers").required(true).defaultValue("true").addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public static final PropertyDescriptor CSV_SEPARATOR = new PropertyDescriptor.Builder().name(FSI_EntityExtractor.PARAM_SPLIT).displayName("CSV Separator").description(
            "Default is a comma (,)").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public static final PropertyDescriptor PERSISTOR_CLASSPATHS = new PropertyDescriptor.Builder().name("classpaths").displayName("Persistor Classpaths").description(
            "One classpath per line (shift+enter is newline), first line is authoritative").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public static final Relationship SUCCESS = new Relationship.Builder().name("success").description(
            "Successfully created FlowFile from Coalesce.").build();

    public static final Relationship FAILURE = new Relationship.Builder().name("failure").description(
            "Horribly failing created FlowFile from Coalesce.").build();

    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context)
    {
        final List<PropertyDescriptor> descriptors = new ArrayList<>();
        descriptors.add(TEMPLATE_JSON);
        descriptors.add(CSV_SEPARATOR);
        descriptors.add(PERSISTOR_CLASSPATHS);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<>();
        relationships.add(SUCCESS);
        relationships.add(FAILURE);
        this.relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships()
    {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors()
    {
        return descriptors;
    }

    @Override
    protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(final String propertyDescriptorName) {
        return new PropertyDescriptor.Builder()
                .name(propertyDescriptorName)
                .required(false)
                .addValidator(Validator.VALID)
                .dynamic(true)
                .build();
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException
    {
        String classpaths = context.getProperty(PERSISTOR_CLASSPATHS).getValue();
        boolean hasHeaders = Boolean.parseBoolean(context.getProperty(PARAM_HAS_HEADERS).getValue());

        CoalesceSearchFramework framework = loadFramework(getParameters(context), classpaths.split("\n"));

        List<CoalesceEntity> entities = new ArrayList<>();
        FlowFile flowFile = session.get();
        if (flowFile != null)
        {
            //this file is for the actual csv
            String filename = flowFile.getAttribute("filename");
            Path absolutePath = Paths.get(flowFile.getAttribute("absolute.path"));

            try
            {
                // TODO Replace this with a parameter thats passed in
                Class extractorClass = Thread.currentThread().getContextClassLoader().loadClass(FSI_EntityExtractor.class.getName());
                Object extractor = extractorClass.newInstance();

                if (extractor instanceof IExtractor)
                {
                    //pass the json string to the extractor
                    ((IExtractor) extractor).setProperties(getParameters(context));
                    ((IExtractor) extractor).setFramework(framework);

                    boolean isFirstLine = true;

                    //read file
                    File file = absolutePath.resolve(filename).toFile();
                    try (BufferedReader reader = new BufferedReader(new FileReader(file)))
                    {
                        String line;
                        while ((line = reader.readLine()) != null)
                        {
                            if (!hasHeaders || !isFirstLine)
                            {
                                List<CoalesceEntity> ent = ((IExtractor) extractor).extract(filename, line);
                                entities.addAll(ent);
                            }

                            isFirstLine = false;
                        }
                    }
                }
                else
                {
                    throw new ProcessException(String.format(CoalesceErrors.NOT_INITIALIZED, IExtractor.class.getSimpleName()));
                }
            }
            catch (IndexNotFoundException | ClassNotFoundException | IllegalAccessException | InstantiationException | CoalesceException | IOException e)
            {
                getLogger().log(LogLevel.ERROR, e.getClass().getSimpleName() + ": " + e.getMessage() , e);
            }

            try
            {
                framework.saveCoalesceEntity(entities.toArray(new CoalesceEntity[entities.size()]));
            }
            catch (CoalescePersistorException e)
            {
                getLogger().log(LogLevel.ERROR, "Exception: ", e);
            }

            session.transfer(flowFile, SUCCESS);
        }
    }

    private CoalesceSearchFramework loadFramework(Map<String, String> params, String[] classpaths) {

        CoalesceSearchFramework framework = new CoalesceSearchFramework();

        for(String classpath : classpaths) {
            try {
                Class persistorClass = Thread.currentThread().getContextClassLoader().loadClass(classpath);
                Constructor c = persistorClass.getConstructor();
                ICoalescePersistor persistor;
                try {
                    c = persistorClass.getConstructor(Map.class);
                    persistor = (ICoalescePersistor)c.newInstance(params);
                }
                catch(NoSuchMethodException e) {
                    persistor = (ICoalescePersistor)(c.newInstance());
                }

                if(!framework.isInitialized()) {
                    framework.setAuthoritativePersistor(persistor);
                }
                else {
                    framework.setSecondaryPersistors(persistor);
                }

            }
            catch(InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                getLogger().log(LogLevel.ERROR, "Exception: ", e);
            }
        }

        return framework;
    }

    /**
     * @param context provided by NiFi
     * @return a mapping of parameters specified by the context.
     */
    private Map<String, String> getParameters(ProcessContext context)
    {
        Map<String, String> params = new HashMap<>();

        for (Map.Entry<PropertyDescriptor, String> entry : context.getProperties().entrySet())
        {
            params.put(entry.getKey().getName(), entry.getValue());
            //getLogger().log(LogLevel.ERROR, entry.getValue());
        }

        return params;
    }
}
