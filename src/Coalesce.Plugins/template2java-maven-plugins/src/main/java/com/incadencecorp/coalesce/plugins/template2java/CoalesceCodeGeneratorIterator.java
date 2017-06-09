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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.framework.DefaultNormalizer;
import com.incadencecorp.coalesce.framework.DefaultNormalizer.EParameters;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.iterators.CoalesceIterator;
import com.incadencecorp.coalesce.mapper.impl.FieldMapperImpl;

public class CoalesceCodeGeneratorIterator extends CoalesceIterator<Path> {

    private VelocityEngine ve;

    public CoalesceCodeGeneratorIterator()
    {
        // Configure Engine to read templates from resources
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        ve = new VelocityEngine();
        ve.init(p);
    }

    public void generateCode(CoalesceEntityTemplate template, Path directory) throws CoalesceException
    {
        generateCode(template.createNewEntity(), directory);
    }

    public void generateCode(CoalesceEntity entity, Path directory) throws CoalesceException
    {
        processAllElements(entity, directory);
    }

    @Override
    protected boolean visitCoalesceRecordset(CoalesceRecordset recordset, Path directory) throws CoalesceException
    {
        // Get Template
        Template t = ve.getTemplate("record.vm");

        DefaultNormalizer normalizer = new DefaultNormalizer();
        normalizer.setParameter(EParameters.MAX_LENGTH, "64");

        String normalizedName = normalizer.normalize(recordset.getName());
        normalizedName = normalizedName.substring(0, 1).toUpperCase() + normalizedName.substring(1);

        if (NumberUtils.isNumber(normalizedName.substring(0, 1)))
        {
            throw new CoalesceException("Recordset names must start with a character");
        }

        // Set Parameters
        VelocityContext context = new VelocityContext();
        context.put("name", normalizedName);
        context.put("package", recordset.getEntity().getClassName());
        context.put("definitions", recordset.getFieldDefinitions());
        context.put("fieldmapper", new FieldMapperImpl());

        // Populate Template
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

        try
        {
            FileUtils.writeStringToFile(new File(directory.resolve(normalizedName + ".java").toUri()), writer.toString());
        }
        catch (IOException e)
        {
            throw new CoalesceException(e);
        }
        // TODO Save output
        System.out.println(writer.toString());
        return false;
    }

}
