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

    private CoalesceSearchFramework framework;

    public static final PropertyDescriptor TEMPLATE_JSON = new PropertyDescriptor.Builder().name("json").displayName("JSON Template").description(
            "Copy & Paste JSON template. Format is on the coalesce wiki").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

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
        List<CoalesceEntity> entities = new ArrayList<>();
        FlowFile flowFile = session.get();
        if (flowFile == null)
        {
            return;
        }

        //this file is for the actual csv
        String filename = flowFile.getAttribute("filename");
        String absolutePath = flowFile.getAttribute("absolute.path");

        String jsonString = context.getProperty(TEMPLATE_JSON).getValue();

        try
        {
            // TODO Replace this with a parameter thats passed in
            Class extractorClass = Thread.currentThread().getContextClassLoader().loadClass(FSI_EntityExtractor.class.getName());
            Object g = extractorClass.newInstance();
            if (g instanceof IExtractor)
            {
                //the token to split with, usually a comma (,)
                String splitToken = context.getProperty(CSV_SEPARATOR).getValue();


                //pass the json string to the extractor
                ((IExtractor) g).setProperties(getParameters(context));
                //get the templates, then pass them to the extractor,
                //  so the extractor doesn't need to create a persistor to get them
                getTemplates(jsonString, ((FSI_EntityExtractor) g));

                //read file
                File file = new File(absolutePath + filename);    //    /testfiles/whatever.csv
                BufferedReader b = new BufferedReader(new FileReader(file));

                String columnNames = b.readLine();
                String line;
                while ((line = b.readLine()) != null) {
                    List<CoalesceEntity> ent = ((FSI_EntityExtractor) g).extract(filename, line);
                    entities.addAll(ent);
                }
            }
            else
            {
                throw new ProcessException(String.format(CoalesceErrors.NOT_INITIALIZED, IExtractor.class.getSimpleName()));
            }
        }
        catch (IndexNotFoundException e)
        {
            getLogger().log(LogLevel.ERROR, "IndexNotFoundException: ", e);
        }
        catch (ClassNotFoundException e)
        {
            getLogger().log(LogLevel.ERROR, "classnotfound: ", e);
        }
        catch (IllegalAccessException e)
        {
            getLogger().log(LogLevel.ERROR, "IllegalAccessException: ", e);
        }
        catch (InstantiationException e)
        {
            getLogger().log(LogLevel.ERROR, "InstantiationException: ", e);
        }
        catch (FileNotFoundException e)
        {
            getLogger().log(LogLevel.ERROR, "FileNotFoundException: ", e);
        }
        catch (IOException e)
        {
            getLogger().log(LogLevel.ERROR, "IOException: ", e);
        }
        catch (CoalesceException e)
        {
            e.printStackTrace();
            getLogger().log(LogLevel.ERROR, "FUCK YOUCoalesceException: " + e);
        }

        this.framework = new CoalesceSearchFramework();
        String classpaths = context.getProperty(PERSISTOR_CLASSPATHS).getValue();
        String[] classpathsSplit = classpaths.split("\n");
        boolean authPersistorSet = false;
        Map<String, String> params = getParameters(context);
        for(String classpath : classpathsSplit) {
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

                if(!authPersistorSet) {
                    framework.setAuthoritativePersistor(persistor);
                    authPersistorSet = true;
                }
                else {
                    framework.setSecondaryPersistors(persistor);
                }

            }
            catch(InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                getLogger().log(LogLevel.ERROR, "Exception: ", e);
            }
        }
        try {
            this.framework.saveCoalesceEntity(entities.toArray(new CoalesceEntity[entities.size()]));
        }
        catch(CoalescePersistorException e) {
            getLogger().log(LogLevel.ERROR, "Exception: ", e);
        }


        session.transfer(flowFile, SUCCESS);
    }


    public void getTemplates(String jsonString, FSI_EntityExtractor g) {
        try
        {
            HashMap<String, String> templates = new HashMap<>();
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            JSONArray templatesArray = (JSONArray) json.get("templates");
            for (Object aTemplatesArray : templatesArray)
            {
                JSONObject template = (JSONObject) aTemplatesArray;
                String templateUri = (String) template.get("templateUri");

                String templateXml = getTemplateXml(templateUri);
                templates.put(templateUri, templateXml);
            }
            g.setTemplates(templates);
        }
        catch(ParseException e) {
            getLogger().log(LogLevel.ERROR, "ParseException: ", e);
        }
    }

    private String getTemplateXml(String templateUri) {
        try {
            URI uri = new URI(templateUri);
            switch(uri.getScheme()) {
            case "file":
                return IOUtils.toString(uri, StandardCharsets.UTF_8);
            case "http":
            case "https":
                HttpResponse response = getResponse(new HttpGet(templateUri));
                switch(response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    return EntityUtils.toString(response.getEntity());
                default:
                    break;
                }
            }
        }
        catch(URISyntaxException e) {
            getLogger().log(LogLevel.ERROR, "URISyntaxException: ", e);
        }
        catch(IOException e) {
            getLogger().log(LogLevel.ERROR, "IOException: ", e);
        }
        return "ERROR";
    }

    private HttpResponse getResponse(HttpUriRequest request) {
        HttpResponse response = null;
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            response = client.execute(request);
        }
        catch(IOException e) {
            getLogger().log(LogLevel.ERROR, "IOException: ", e);
        }
        return response;
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
