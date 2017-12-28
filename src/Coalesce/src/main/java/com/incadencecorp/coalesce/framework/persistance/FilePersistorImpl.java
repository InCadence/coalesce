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

package com.incadencecorp.coalesce.framework.persistance;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.api.ICoalesceComponent;
import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.FileHelper;
import com.incadencecorp.coalesce.framework.CoalesceComponentImpl;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * This implementation uses the file system to store and retrieve Coalesce
 * entities. This is idea for backing up and restoring entities.
 *
 * @author n78554
 */
public class FilePersistorImpl extends CoalesceComponentImpl implements ICoalescePersistor, ICoalesceComponent {

    private static final String TEMPLATE_DIRECTORY = "templates";
    private Path root = FilePersistorSettings.getDirectory();
    private int subDirLen = FilePersistorSettings.getSubDirectoryLength();

    /*--------------------------------------------------------------------------
    ICoalesceComponent Implementation
    --------------------------------------------------------------------------*/

    @Override
    public void setProperties(Map<String, String> params)
    {
        super.setProperties(params);

        // Last Successful Scan Configured?
        if (parameters.containsKey(CoalesceParameters.PARAM_DIRECTORY))
        {
            try
            {
                root = Paths.get(FileHelper.getFullPath(parameters.get(CoalesceParameters.PARAM_DIRECTORY)));

                if (!Files.exists(root))
                {
                    throw new IllegalArgumentException("Invalid Directory: " + root);
                }
            }
            catch (URISyntaxException e)
            {
                throw new IllegalArgumentException(CoalesceParameters.PARAM_DIRECTORY, e);
            }
        }

        if (parameters.containsKey(CoalesceParameters.PARAM_SUBDIR_LEN))
        {
            subDirLen = Integer.parseInt(parameters.get(CoalesceParameters.PARAM_SUBDIR_LEN));

            if (subDirLen < 0)
            {
                throw new IllegalArgumentException("Invalid Sub Directory Length: " + subDirLen);
            }
        }
    }

    @Override
    public List<String> getPropertyList()
    {
        List<String> properties = super.getPropertyList();

        properties.add(CoalesceParameters.PARAM_DIRECTORY);
        properties.add(CoalesceParameters.PARAM_SUBDIR_LEN);

        return properties;
    }

    /*--------------------------------------------------------------------------
    ICoalescePersistor Implementation
    --------------------------------------------------------------------------*/

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        for (CoalesceEntity entity : entities)
        {
            Path sub = root.resolve(entity.getKey().substring(0, subDirLen));
            Path filename = sub.resolve(entity.getKey());

            try
            {
                // Deleting?
                if (entity.isMarkedDeleted() && allowRemoval)
                {
                    // Delete File
                    if (Files.exists(filename))
                    {
                        Files.deleteIfExists(filename);
                    }

                    // Clean Up Sub-Directory
                    if (Files.exists(sub))
                    {
                        File directory = new File(sub.toString());
                        if (directory.list().length == 0 && !Files.isSameFile(sub, root))
                        {
                            if (!directory.delete())
                            {
                                throw new CoalescePersistorException(
                                        "(FAILED) Deleting Directory: " + directory.getAbsolutePath());
                            }
                        }
                    }
                }
                else
                {
                    // Ensure Sub-Directory Exists
                    if (!Files.exists(sub))
                    {
                        Files.createDirectories(sub);
                    }

                    try (FileWriter writer = new FileWriter(new File(filename.toString())))
                    {
                        writer.write(entity.toXml());
                    }
                    catch (IOException e)
                    {
                        throw new CoalescePersistorException("(FAILED) Saving Entity", e);
                    }
                }
            }
            catch (IOException e)
            {
                throw new CoalescePersistorException("Failed to Record Entity", e);
            }
        }

        return true;
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {
        CoalesceEntity[] results = new CoalesceEntity[keys.length];
        String[] xmls = getEntityXml(keys);

        for (int ii = 0; ii < xmls.length; ii++)
        {
            if (xmls[ii] != null)
            {
                results[ii] = CoalesceEntity.create(xmls[ii]);
            }
            else
            {
                results[ii] = null;
            }
        }

        return results;
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        List<String> results = new ArrayList<>();

        for (String key : keys)
        {
            Path sub = root.resolve(key.substring(0, subDirLen));
            Path filename = sub.resolve(key);

            if (Files.exists(filename))
            {
                try (BufferedReader br = new BufferedReader(new FileReader(new File(filename.toString()))))
                {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();

                    while (line != null)
                    {
                        sb.append(line);
                        line = br.readLine();
                    }

                    results.add(sb.toString());
                }
                catch (IOException e)
                {
                    throw new CoalescePersistorException("(FAILED) Reading Entity", e);
                }
            }
        }

        return results.toArray(new String[results.size()]);
    }

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        registerTemplate(templates);
    }

    @Override
    public void deleteTemplate(String... keys) throws CoalescePersistorException
    {
        throw new CoalescePersistorException("Not Implemented");
    }

    @Override
    public void unregisterTemplate(String... keys) throws CoalescePersistorException
    {
        throw new CoalescePersistorException("Not Implemented");
    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        for (CoalesceEntityTemplate template : templates)
        {
            Path sub = root.resolve(TEMPLATE_DIRECTORY);
            Path filename = sub.resolve(template.getKey());

            try
            {
                if (!Files.exists(sub))
                {
                    Files.createDirectories(sub);
                }

                // Delete File
                if (Files.exists(filename))
                {
                    Files.deleteIfExists(filename);
                }

                try (FileWriter writer = new FileWriter(new File(filename.toString())))
                {
                    writer.write(template.toXml());
                }
                catch (IOException e)
                {
                    throw new CoalescePersistorException("(FAILED) Saving Entity", e);
                }
            }
            catch (IOException e)
            {
                throw new CoalescePersistorException("Failed to Record Entity", e);
            }
        }
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        CoalesceEntityTemplate result;

        Path sub = root.resolve(TEMPLATE_DIRECTORY);
        Path filename = sub.resolve(key);

        if (Files.exists(filename))
        {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(filename.toString()))))
            {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null)
                {
                    sb.append(line);
                    line = br.readLine();
                }

                result = CoalesceEntityTemplate.create(sb.toString());
            }
            catch (IOException | SAXException e)
            {
                throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", key), e);
            }
        }
        else
        {
            throw new CoalescePersistorException(String.format(CoalesceErrors.NOT_FOUND, "Template", key));
        }

        return result;
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
        return UUID.nameUUIDFromBytes((name + source + version).getBytes()).toString();
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        List<ObjectMetaData> results = new ArrayList<>();

        try
        {
            Path directory = root.resolve(TEMPLATE_DIRECTORY);

            if (!Files.exists(directory))
            {
                Files.createDirectories(directory);
            }

            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try (BufferedReader br = new BufferedReader(new FileReader(new File(file.toString()))))
                    {
                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();

                        while (line != null)
                        {
                            sb.append(line);
                            line = br.readLine();
                        }

                        CoalesceEntityTemplate template = CoalesceEntityTemplate.create(sb.toString());

                        results.add(new ObjectMetaData(template.getKey(),
                                                       template.getName(),
                                                       template.getSource(),
                                                       template.getVersion()));
                    }
                    catch (SAXException e)
                    {
                        throw new IOException(e);
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e)
        {
            throw new CoalescePersistorException("(FAILED) Creating Template Metadata", e);
        }

        return results;
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        return EnumSet.of(EPersistorCapabilities.READ,
                          EPersistorCapabilities.CREATE,
                          EPersistorCapabilities.UPDATE,
                          EPersistorCapabilities.DELETE,
                          EPersistorCapabilities.READ_TEMPLATES);
    }

}
