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

package com.incadencecorp.coalesce.plugins.template2java;

import com.incadencecorp.coalesce.api.ICoalesceNormalizer;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceObject;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import com.incadencecorp.coalesce.mapper.impl.FieldMapperImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * TODO Nested Sections not supported
 *
 * @author Derek Clemenzi
 */
public class CoalesceCodeGeneratorIterator extends CoalesceIterator<List<CoalesceRecordset>> {

    private static final String FILE_EXT = "java";

    private VelocityEngine ve;
    private Path directory;
    private String packagename;

    private static final String[] entity_templates = new String[] { "api-entity.vm", "impl-coalesce-entity.vm",
                                                                    "impl-pojo-entity.vm"
    };
    private static final String[] record_templates = new String[] { "api-record.vm", "impl-coalesce-factory.vm",
                                                                    "impl-coalesce-record.vm", "impl-pojo-record.vm"
    };

    public CoalesceCodeGeneratorIterator(Path directory)
    {
        // Configure Engine to read templates from resources
        Properties p = new Properties();
        p.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        p.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        // p.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
        // Paths.get("src", "main", "resources").toString());

        ve = new VelocityEngine();
        ve.init(p);

        this.directory = directory;
    }

    public void generateCode(CoalesceEntityTemplate template) throws CoalesceException
    {
        generateCode(template.createNewEntity());
    }

    public void generateCode(CoalesceEntity entity) throws CoalesceException
    {
        if (StringHelper.isNullOrEmpty(entity.getClassName()))
        {
            entity.setAttribute(CoalesceEntity.ATTRIBUTE_CLASSNAME,
                                "com.incadencecorp.coalesce." + new ClassNameNormalizer().normalize(entity.getName()));
        }
        else if (!entity.getClassName().contains("."))
        {
            entity.setAttribute(CoalesceEntity.ATTRIBUTE_CLASSNAME, "com.incadencecorp.coalesce." + entity.getClassName());
        }

        packagename = entity.getClassName().substring(0, entity.getClassName().lastIndexOf("."));

        List<CoalesceRecordset> recordsets = new ArrayList<>();

        processAllElements(entity, recordsets);

        // Set Parameters
        VelocityContext context = new VelocityContext();
        context.put("entity", entity);
        context.put("recordsets", recordsets);

        for (String template : entity_templates)
        {
            createFile(template, entity, context);
        }
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, List<CoalesceRecordset> params)
            throws CoalesceException
    {
        params.add(recordset);

        // Set Parameters
        VelocityContext context = new VelocityContext();
        context.put("recordset", recordset);

        for (String template : record_templates)
        {
            createFile(template, recordset, context);
        }

        return false;
    }

    private void createFile(String filename, CoalesceObject object, VelocityContext context) throws CoalesceException
    {
        ClassNameNormalizer normalizer = new ClassNameNormalizer();

        String normalizedName = normalizer.normalize(object.getName());

        if (NumberUtils.isNumber(normalizedName.substring(0, 1)))
        {
            throw new CoalesceException("Recordset names must start with a character");
        }

        filename = filename.replaceAll(".vm", "");

        context.put("packagename_root", packagename);
        context.put("normalizer", normalizer);
        context.put("packagename_sub", filename.replaceAll("[-]", "."));
        context.put("classname", getClassName(filename, normalizedName, normalizer));
        context.put("fieldmapper", new FieldMapperImpl());
        context.put("typemapper", new ReturnTypeMapper());

        createFile(ve.getTemplate(filename + ".vm"), context);
    }

    private void createFile(Template t, VelocityContext context) throws CoalesceException
    {
        String filename = context.get("classname").toString() + "." + FILE_EXT;
        String packagename = context.get("packagename_root").toString() + "." + context.get("packagename_sub").toString();

        Path file = directory.resolve(Paths.get(".", packagename.split("[.]"))).resolve(filename);

        try (StringWriter writer = new StringWriter())
        {
            t.merge(context, writer);
            FileUtils.writeStringToFile(new File(file.toUri()), writer.toString());
        }
        catch (IOException e)
        {
            throw new CoalesceException(e);
        }
    }

    private String getClassName(String template_name, String object_name, ICoalesceNormalizer normalizer)
    {
        String result;
        String[] parts = template_name.split("[-]");

        if (template_name.startsWith("api"))
        {
            result = "I" + object_name + normalizer.normalize(parts[parts.length - 1]);
        }
        else
        {
            result = object_name + normalizer.normalize(parts[parts.length - 2]) + normalizer.normalize(parts[parts.length
                    - 1]);
        }

        return result;
    }
}
