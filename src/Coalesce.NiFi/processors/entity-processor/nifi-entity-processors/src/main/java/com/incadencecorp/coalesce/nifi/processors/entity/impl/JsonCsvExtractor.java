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
package com.incadencecorp.coalesce.nifi.processors.entity.impl;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistorSearch;
import com.incadencecorp.coalesce.search.CoalesceSearchFramework;
import org.elasticsearch.index.IndexNotFoundException;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.logging.LogLevel;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.util.StandardValidators;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.persistance.elasticsearch.ElasticSearchPersistor;
import com.incadencecorp.coalesce.ingest.api.IExtractor;
import com.incadencecorp.coalesce.ingest.plugins.fsi.FSI_EntityExtractor;



import java.io.*;
import java.util.*;

@Tags({"weewoo petard"})
@CapabilityDescription("Provide a description")
@SeeAlso()
@ReadsAttributes({@ReadsAttribute(attribute="")})
@WritesAttributes({@WritesAttribute(attribute="")})
public class JsonCsvExtractor extends AbstractProcessor {

    private ElasticSearchPersistor persistor;
    private CoalesceSearchFramework csf;

    public static final PropertyDescriptor IS_AUTHORITATIVE = new PropertyDescriptor
            .Builder().name("Is Authoritative?")
            .description("Is it authoritative or not?")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor CLUSTER_NAME = new PropertyDescriptor
            .Builder().name("Cluster Name")
            .description("Specify the name of the Cluster")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor HOSTS = new PropertyDescriptor
            .Builder().name("Hosts")
            .description("Specify the IP:PORT (ex: localhost:9300)")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor HOST = new PropertyDescriptor
            .Builder().name("Persister Host IP")
            .description("Specify the Host IP (ex: localhost)")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor PORT = new PropertyDescriptor
            .Builder().name("Persister Port")
            .description("Specify the port for the persister (ex: 9200)")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor CACHE_ENABLED = new PropertyDescriptor
            .Builder().name("Cache Enabled")
            .description("true if cache enabled, false if otherwise")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor SSL_ENABLED = new PropertyDescriptor
            .Builder().name("SSL Enabled")
            .description("true if ssl enabled, false if otherwise")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor SSL_REJECT_UNAUTHORIZED = new PropertyDescriptor
            .Builder().name("SSL Reject Unauthorized")
            .description("true if reject unauthorized, false if otherwise")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

//    public static final PropertyDescriptor TEMPLATE = new PropertyDescriptor
//            .Builder().name("Template GUID")
//            .description("")
//            .required(true)
//            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
//            .build();

    public static final PropertyDescriptor TEMPLATE_JSON = new PropertyDescriptor
            .Builder().name("JSON Template")
            .description("Copy & Paste JSON template. Format is on the coalesce wiki")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor CSV_SEPARATOR = new PropertyDescriptor
            .Builder().name("CSV Separator")
            .description("Default is a comma (,)")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();


    public static final Relationship SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Successfully created FlowFile from Coalesce.")
            .build();

    public static final Relationship FAILURE = new Relationship.Builder()
            .name("success")
            .description("Horribly failing created FlowFile from Coalesce.")
            .build();



    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        final List<PropertyDescriptor> descriptors = new ArrayList<>();
        descriptors.add(IS_AUTHORITATIVE);
        descriptors.add(CLUSTER_NAME);
        descriptors.add(HOSTS);
        descriptors.add(HOST);
        descriptors.add(PORT);
        descriptors.add(CACHE_ENABLED);
        descriptors.add(SSL_ENABLED);
        descriptors.add(SSL_REJECT_UNAUTHORIZED);
        descriptors.add(TEMPLATE_JSON);
        descriptors.add(CSV_SEPARATOR);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<>();
        relationships.add(SUCCESS);
        relationships.add(FAILURE);
        this.relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    private void updatePersistor(final ProcessContext context) {
        Map<String, String> params = new HashMap<>();

        params.put("elastic.isAuthoritative", context.getProperty(IS_AUTHORITATIVE).getValue());
        params.put("elastic.clustername", context.getProperty(CLUSTER_NAME).getValue());
        params.put("elastic.hosts", context.getProperty(HOSTS).getValue());
        params.put("elastic.http.host", context.getProperty(HOST).getValue());
        params.put("elastic.http.port", context.getProperty(PORT).getValue());
        params.put("elastic.datastore.cache.enabled", context.getProperty(CACHE_ENABLED).getValue());
        params.put("ssl.enabled", context.getProperty(SSL_ENABLED).getValue());
        params.put("ssl.reject_unauthorized", context.getProperty(SSL_REJECT_UNAUTHORIZED).getValue());

        this.persistor = new ElasticSearchPersistorSearch(params);
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();
        if (flowFile == null) {
            return;
        }

        if(this.persistor == null) {
            this.updatePersistor(context);
            this.csf = new CoalesceSearchFramework();
            this.csf.setAuthoritativePersistor(this.persistor);
            this.csf.refreshCoalesceTemplateUtil();
        }

        String filename = flowFile.getAttribute("filename");
        String absolutePath = flowFile.getAttribute("absolute.path");

        getLogger().debug(filename);
        List<CoalesceEntity> entities;

//        try {
//
//            entities =
//                    //this.persistor.getEntityTemplate(context.getProperty(TEMPLATE).getValue()).createNewEntity();
//            //entity.setTitle(filename);
//
//            CoalesceRecordset crs = entity.getCoalesceRecordsetForNamePath("Enumeration/enumeration/metadata");
//            getLogger().warn("RECORDS: " + crs.getRecords().size());
//            crs.getRecords().get(0).getFieldByName("enumname").setAttribute("value", filename);
//
//            persistor.saveEntity(false, entity);
//        }
//        catch(CoalescePersistorException e) {
//            getLogger().log(LogLevel.ERROR, "CoalescePersistorException", e);
//        }

        try {
            Class extractorClass = Thread.currentThread().getContextClassLoader().loadClass(
                    "com.incadencecorp.coalesce.ingest.plugins.fsi.FSI_EntityExtractor");
            Object g = extractorClass.newInstance();
            if(g instanceof IExtractor) {

                String jsonString = context.getProperty(TEMPLATE_JSON).getValue();
                String splitToken = context.getProperty(CSV_SEPARATOR).getValue();

                Map<String, String> properties = new HashMap<>();
                properties.put("json", jsonString);
                properties.put("split", splitToken);
                ((IExtractor) g).setFramework(this.csf);
                ((IExtractor) g).setProperties(properties);

                File file = new File(absolutePath+filename);    //    /testfiles/whatever.csv
                BufferedReader b = new BufferedReader(new FileReader(file));


                String columnNames = b.readLine();
                String line;
                while((line = b.readLine()) != null) {
                    //getLogger().log(LogLevel.ERROR, line);
                    entities = ((FSI_EntityExtractor)g).extract(filename, String.join(splitToken, line));
                    csf.saveCoalesceEntity(entities.toArray(new CoalesceEntity[entities.size()]));
                }
            }
        }
        catch (IndexNotFoundException e) {
            getLogger().log(LogLevel.ERROR, "IndexNotFoundException: ", e);
        }
        catch (ClassNotFoundException e) {
            getLogger().log(LogLevel.ERROR, "classnotfound: ", e);
        }
        catch (IllegalAccessException e) {
            getLogger().log(LogLevel.ERROR, "IllegalAccessException: ", e);
        }
        catch (InstantiationException e) {
            getLogger().log(LogLevel.ERROR, "InstantiationException: ", e);
        }
        catch (FileNotFoundException e) {
            getLogger().log(LogLevel.ERROR, "FileNotFoundException: ", e);
            getLogger().log(LogLevel.ERROR, absolutePath);
        }
        catch (IOException e) {
            getLogger().log(LogLevel.ERROR, "IOException: ", e);
        }
        catch (CoalesceException e) {
            getLogger().log(LogLevel.ERROR, "CoalesceException: " + e);
        }



        session.transfer(flowFile, SUCCESS);


    }
}
