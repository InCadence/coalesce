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

package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.JodaDateTimeHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceTemplatePersister;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Derek Clemenzi
 */
public class AccumuloTemplatePersister implements ICoalesceTemplatePersister {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloPersistor.class);

    private Map<String, String> params;
    private boolean batchInProgress = false;
    private AccumuloDataConnector connector;
    private ICoalesceNormalizer normalizer;

    private static String coalesceTemplateColumnFamily = "Coalesce:Template";
    private static String coalesceTemplateXMLQualifier = "xml";

    public AccumuloTemplatePersister(Map<String, String> params)
    {
        this.params = params;
        LOGGER.debug("Zookeepers: {} ", params.get(AccumuloDataConnector.ZOOKEEPERS));
        LOGGER.debug("Instance: {} ", params.get(AccumuloDataConnector.INSTANCE_ID));
        LOGGER.debug("User: {} ", params.get(AccumuloDataConnector.USER));
        LOGGER.debug("Mock: {} ", params.get(AccumuloDataConnector.USE_MOCK));

        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run()
            {
                boolean inLoop = false;
                LOGGER.debug("Shutdown Hook Invoked");
                while (batchInProgress)
                {
                    if (!inLoop)
                        LOGGER.debug("Batch IO in progress waiting");
                    inLoop = true;
                    try
                    {
                        sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        LOGGER.warn(e.getMessage());
                    }
                }
            }
        });
    }

    public void setNormalizer(ICoalesceNormalizer normalizer)
    {
        this.normalizer = normalizer;
    }

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        for (CoalesceEntityTemplate template : templates)
        {
            BatchWriter writer = null;

            try
            {
                BatchWriterConfig config = new BatchWriterConfig();
                config.setMaxLatency(1, TimeUnit.SECONDS);
                config.setMaxMemory(10240);
                // config.setDurability(Durability.DEFAULT); // Requires Accumulo 1.7
                config.setMaxWriteThreads(10);

                /*
                 * SQL we are eumulating return conn.executeProcedure( "CoalesceEntityTemplate_InsertOrUpdate", new
                 * CoalesceParameter(UUID.randomUUID().toString(), Types.OTHER), new CoalesceParameter(template.getName()),
                 * new CoalesceParameter(template.getSource()), new CoalesceParameter(template.getVersion()), new
                 * CoalesceParameter(template.toXml()), new CoalesceParameter(JodaDateTimeHelper.nowInUtc().toString(),
                 * Types.OTHER), new CoalesceParameter(JodaDateTimeHelper.nowInUtc().toString(), Types.OTHER));
                 */
                String xml = template.toXml();
                String name = template.getName();
                String source = template.getSource();
                String version = template.getVersion();
                String time = JodaDateTimeHelper.nowInUtc().toString();

                // See if a template with this name, source, version exists
                String templateId = getEntityTemplateKey(name, source, version);

                boolean newTemplate = false;

                if (templateId == null)
                {
                    templateId = UUID.nameUUIDFromBytes((name + source + version).getBytes()).toString();
                    newTemplate = true;
                }

                template.setKey(templateId);

                writer = getDataConnector().getDBConnector().createBatchWriter(AccumuloDataConnector.COALESCE_TEMPLATE_TABLE,
                                                                               config);
                Mutation m = new Mutation(templateId);
                m.put(coalesceTemplateColumnFamily, coalesceTemplateXMLQualifier, new Value(xml.getBytes()));
                m.put(coalesceTemplateColumnFamily, CoalesceEntity.ATTRIBUTE_LASTMODIFIED, new Value(time.getBytes()));

                // Only update the name, source, version, created date if new.
                if (newTemplate)
                {
                    m.put(coalesceTemplateColumnFamily, CoalesceEntity.ATTRIBUTE_NAME, new Value(name.getBytes()));
                    m.put(coalesceTemplateColumnFamily, CoalesceEntity.ATTRIBUTE_SOURCE, new Value(source.getBytes()));
                    m.put(coalesceTemplateColumnFamily, CoalesceEntity.ATTRIBUTE_VERSION, new Value(version.getBytes()));
                    m.put(coalesceTemplateColumnFamily, CoalesceEntity.ATTRIBUTE_DATECREATED, new Value(time.getBytes()));
                    // Special Column Qualifier to so we can fetch key based on Name + Source + Version
                    m.put(coalesceTemplateColumnFamily,
                          template.getName() + template.getSource() + template.getVersion(),
                          new Value(templateId.getBytes()));
                }

                writer.addMutation(m);
                writer.flush();
                // Create the associated search features for this template if it is new
                // TODO: Figure out what to do for updates to templates. What to do with the data?
                //if (newTemplate)
                {
                    registerTemplate(template);
                }
            }
            catch (MutationsRejectedException | TableNotFoundException e)
            {
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_SAVED,
                                                                   "Template",
                                                                   template.getName(),
                                                                   e.getMessage()), e);
            }
            finally
            {
                if (writer != null)
                {
                    try
                    {
                        writer.close();
                    }
                    catch (MutationsRejectedException e)
                    {
                        throw new CoalescePersistorException("(FAILED) Closing Writer", e);
                    }
                }
            }
        }
    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        AccumuloRegisterIterator iterator = new AccumuloRegisterIterator(getNormalizer());

        for (CoalesceEntityTemplate template : templates)
        {
            try
            {
                iterator.register(template, getDataConnector());
            }
            catch (CoalesceException e)
            {
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_SAVED,
                                                                   "Template",
                                                                   template.getKey(),
                                                                   e.getMessage()), e);
            }
        }

    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        if (key == null)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
        }

        CoalesceEntityTemplate template = null;

        try (CloseableScanner scanner = new CloseableScanner(getDataConnector().getDBConnector(),
                                                             AccumuloDataConnector.COALESCE_TEMPLATE_TABLE,
                                                             Authorizations.EMPTY))
        {
            String xml = null;

            scanner.setRange(new Range(key));
            scanner.fetchColumn(new Text(coalesceTemplateColumnFamily), new Text(coalesceTemplateXMLQualifier));

            // TODO Add error handling if more than one row returned.
            if (scanner.iterator().hasNext())
            {
                xml = scanner.iterator().next().getValue().toString();
            }

            if (xml == null)
            {
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
            }

            template = CoalesceEntityTemplate.create(xml);
        }
        catch (TableNotFoundException | SAXException | IOException ex)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", key), ex);
        }

        return template;
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version)
            throws CoalescePersistorException
    {
        try
        {
            return getEntityTemplate(getEntityTemplateKey(name, source, version));
        }
        catch (CoalescePersistorException e)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND,
                                                               "Template",
                                                               "Name: " + name + " Source: " + source + " Version: "
                                                                       + version), e);
        }
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        String key = null;

        try (CloseableScanner keyscanner = new CloseableScanner(getDataConnector().getDBConnector(),
                                                                AccumuloDataConnector.COALESCE_TEMPLATE_TABLE,
                                                                Authorizations.EMPTY))
        {
            // Uses special columnqualifier that is a concat of name+source+version
            keyscanner.fetchColumn(new Text(coalesceTemplateColumnFamily), new Text(name + source + version));
            // TODO Add error handling if more than one row returned.
            if (keyscanner.iterator().hasNext())
            {
                key = keyscanner.iterator().next().getValue().toString();
            }
        }
        catch (TableNotFoundException ex)
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND,
                                                               "Template",
                                                               "Name: " + name + " Source: " + source + " Version: "
                                                                       + version), ex);
        }
        return key;
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        return null;
    }

    protected AccumuloDataConnector getDataConnector() throws CoalescePersistorException
    {
        if (connector == null)
        {
            connector = new AccumuloDataConnector(params);
        }

        return connector;
    }

    protected ICoalesceNormalizer getNormalizer()
    {
        if (normalizer == null)
        {
            normalizer = new AccumuloNormalizer();
        }

        return normalizer;
    }

}

