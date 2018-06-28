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

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.classification.helpers.StringHelper;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * @requiresDependencyResolution test
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true)
public class Template2JavaMojo extends AbstractMojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Template2JavaMojo.class);

    // Temporary ---- Replace with Template Parameter that contains Template
    // Directory
    @Parameter(defaultValue = "NoName")
    private String filePersistorName;

    @Parameter(defaultValue = "target")
    private String outputDir;

    @Component
    private MavenProject project;
    @Component
    private PluginDescriptor descriptor;

    public void execute() throws MojoExecutionException
    {
        LOGGER.info("I'm looking for a persistor ({}) my target is ({})", filePersistorName, outputDir);
        ICoalescePersistor persistor;
        try
        {
            addDependenciesToClasspath();

            Object persister = Class.forName(filePersistorName, true, getClassLoaderFromRealm()).newInstance();

            LOGGER.info("(Loaded) {}", persister.getClass().getSimpleName());
            LOGGER.info("Output Directory = ({})", Paths.get(outputDir).toUri().toString());

            CoalesceCodeGeneratorIterator it = new CoalesceCodeGeneratorIterator(Paths.get(outputDir));
            if (persister instanceof ICoalescePersistor)
            {
                persistor = (ICoalescePersistor) persister;

                for (ObjectMetaData meta : persistor.getEntityTemplateMetadata())
                {
                    CoalesceEntityTemplate template;
                    try
                    {
                        template = persistor.getEntityTemplate(meta.getKey());

                        if (template != null)
                        {
                            LOGGER.info("Generating ({}) ({}) ({})",
                                        template.getName(),
                                        template.getSource(),
                                        template.getKey());

                            if (StringHelper.isNullOrEmpty(template.getClassName()))
                            {
                                LOGGER.warn("No Classname Specified!!!");
                            }

                            it.generateCode(template);
                        }
                    }
                    catch (CoalesceException e)
                    {
                        String errorMsg = String.format(CoalesceErrors.INVALID_OBJECT, meta.getKey(), e.getMessage());
                        LOGGER.error(errorMsg, e);
                    }
                }
            }
            else
            {
                LOGGER.warn("(FAILED) Loading Persister ({}): {}", filePersistorName, "Invalid Implementation");
            }
        }
        catch (MojoExecutionException | ClassNotFoundException | InstantiationException | IllegalAccessException | CoalescePersistorException e)
        {
            LOGGER.warn("(FAILED) Loading Persister ({})", filePersistorName, e);
            throw new MojoExecutionException("", e);
        }
    }

    private void addDependenciesToClasspath() throws MojoExecutionException
    {
        for (Object artifact : project.getDependencyArtifacts())
        {
            try
            {
                if (((Artifact) artifact).getFile() != null)
                {
                    LOGGER.info("(ADDING) {}", ((Artifact) artifact).getArtifactId());
                    final URL url = ((Artifact) artifact).getFile().toURI().toURL();
                    final ClassRealm realm = descriptor.getClassRealm();
                    realm.addURL(url);
                }
                else
                {
                    LOGGER.warn("(SKIPPING) {}", ((Artifact) artifact).getArtifactId());
                }
            }
            catch (MalformedURLException e)
            {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }

    private ClassLoader getClassLoaderFromRealm() throws MojoExecutionException
    {
        return descriptor.getClassRealm();
    }

}
