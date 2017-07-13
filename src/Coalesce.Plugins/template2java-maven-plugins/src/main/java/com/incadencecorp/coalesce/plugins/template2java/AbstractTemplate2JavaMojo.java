package com.incadencecorp.coalesce.plugins.template2java;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.xml.sax.SAXException;

import com.incadencecorp.coalesce.api.CoalesceErrors;
import com.incadencecorp.coalesce.common.exceptions.CoalesceException;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;
import com.incadencecorp.coalesce.framework.persistance.ICoalescePersistor;
import com.incadencecorp.coalesce.framework.persistance.ObjectMetaData;

/**
 * @requiresDependencyResolution test
 */
public abstract class AbstractTemplate2JavaMojo extends AbstractMojo {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(AbstractTemplate2JavaMojo.class);

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

    private void addDependenciesToClasspath(String artifactId) throws ClassNotFoundException
    {
        for (Object artifact : project.getDependencyArtifacts())
        {
            if (((Artifact) artifact).getArtifactId().equals(artifactId))
            {
                System.out.println(((Artifact) artifact).getArtifactId());

                try
                {
                    System.out.println(((Artifact) artifact).isResolved());

                    final URL url = ((Artifact) artifact).getFile().toURI().toURL();

                    System.out.println(url.toString());

                    final ClassRealm realm = descriptor.getClassRealm();
                    realm.addURL(url);

                }
                catch (MalformedURLException e)
                {
                    throw new RuntimeException(e);
                }

            }
        }

    }

    private ClassLoader getClassLoader() throws MojoExecutionException
    {
        try
        {
            List<String> classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            classpathElements.add(project.getBuild().getSourceDirectory());

            for (String value : classpathElements)
            {
                System.out.println(value);
            }

            URL urls[] = new URL[classpathElements.size()];

            for (int i = 0; i < classpathElements.size(); ++i)
            {
                urls[i] = new File((String) classpathElements.get(i)).toURI().toURL();
            }
            return new URLClassLoader(classpathElements.toArray(new URL[0]));
//            return new URLClassLoader(urls, getClass().getClassLoader());
        }
        catch (Exception e)// gotta catch em all
        {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }

    public void execute() throws MojoExecutionException
    {
        URL[] runtimeUrls = null;
        System.out.println("Starting");
        // try{
        //
        // if (project == null) {
        // System.out.println("ERROR");
        // }
        //
        // List runtimeClasspathElements =
        // project.getRuntimeClasspathElements();
        // System.out.println("Have classpath Elements");
        // runtimeUrls = new URL[runtimeClasspathElements.size()];
        // for (int i = 0; i < runtimeClasspathElements.size(); i++) {
        // String element = (String) runtimeClasspathElements.get(i);
        // runtimeUrls[i] = new File(element).toURI().toURL();
        // }
        // } catch(MalformedURLException | DependencyResolutionRequiredException
        // e){
        // System.out.println(e);
        // return;
        // }
        // System.out.println("Trying to get Loader");
        // URLClassLoader newLoader = new URLClassLoader(runtimeUrls,
        // Thread.currentThread().getContextClassLoader());
        //
        // if(filePersistorName.equals("NoName")){
        // return;
        // }
        System.out.println("I'm looking for a persistor " + filePersistorName + " my target is " + outputDir);
        ICoalescePersistor persistor;
        try
        {
//            addDependenciesToClasspath("coalesce-framework-persister-postgresql");
//            addDependenciesToClasspath("coalesce-search");
//            
//            Object persister=descriptor.getClassRealm().loadClass(filePersistorName).newInstance();
            
//            List<String> classpathElements = null;
//            URLClassLoader loader;
//            Object persister = null;
//            try {
//                classpathElements = project.getCompileClasspathElements();
//                List<URL> projectClasspathList = new ArrayList<URL>();
//                for (String element : classpathElements) {
//                    try {
//                        projectClasspathList.add(new File(element).toURI().toURL());
//                    } catch (MalformedURLException e) {
//                        throw new MojoExecutionException(element + " is an invalid classpath element", e);
//                    }
//                }
//
//                loader = new URLClassLoader(projectClasspathList.toArray(new URL[0]));
//                persister = loader.loadClass(filePersistorName);
//                // ... and now you can pass the above classloader to Reflections
//
//            } catch (ClassNotFoundException e) {
//                throw new MojoExecutionException(e.getMessage());
//            } catch (DependencyResolutionRequiredException e) {
//                new MojoExecutionException("Dependency resolution failed", e);
//            }
//
//            System.out.println(persister.getClass().getSimpleName());

            Object persister = getClassLoader().loadClass(filePersistorName).newInstance(); // ClassLoader.getSystemClassLoader().loadClass(filePersistorName).newInstance();
            System.out.println("Loaded Class");
            CoalesceCodeGeneratorIterator it = new CoalesceCodeGeneratorIterator(Paths.get(outputDir));
            if (persister instanceof ICoalescePersistor)
            {
                persistor = (ICoalescePersistor) persister;

                for (ObjectMetaData meta : persistor.getEntityTemplateMetadata())
                {
                    CoalesceEntityTemplate template;
                    try
                    {
                        template = CoalesceEntityTemplate.create(persistor.getEntityTemplateXml(meta.getKey()));
                        if (template != null)
                        {
                            it.generateCode(template);
                        }
                    }
                    catch (SAXException | IOException | CoalesceException e)
                    {
                        String errorMsg = String.format(CoalesceErrors.INVALID_OBJECT,
                                                        CoalesceEntityTemplate.class.getSimpleName(),
                                                        meta.getKey(),
                                                        e.getMessage());
                    }
                }
            }
            else
            {
                System.out.println("Failed to load Persistor");
                // LOGGER.warn("(FAILED) Loading Persister ({}): {}",
                // filePeristorName, "Invalid Implementation");
            }
        }
        catch (CoalescePersistorException | InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            e.printStackTrace();
            // LOGGER.warn("(FAILED) Loading Persister ({})", filePeristorName,
            // e);
        }
    }
}
