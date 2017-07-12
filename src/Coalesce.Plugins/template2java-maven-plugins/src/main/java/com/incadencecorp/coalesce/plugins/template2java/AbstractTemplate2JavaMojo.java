package com.incadencecorp.coalesce.plugins.template2java;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
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
 * @requiresDependencyResolution runtime
*/
public abstract class AbstractTemplate2JavaMojo extends AbstractMojo {
	
//    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTemplate2JavaMojo.class);
    
    //Temporary ---- Replace with Template Parameter that contains Template Directory
    @Parameter(defaultValue = "NoName")
    private String filePersistorName;

    @Parameter(defaultValue = "target")
    private String outputDir;
    
    @Component
    private MavenProject project;
    @Component
    private PluginDescriptor descriptor;

    private void addDependenciesToClasspath(String artifactId) {

        System.out.println(artifactId);
        
        for (Object artifact : project.getDependencyArtifacts()) {
            if (((Artifact) artifact).getArtifactId().equals(artifactId)) {
                try {
                    
                    final URL url = ((Artifact) artifact).getFile().toURI().toURL();
                    final ClassRealm realm = descriptor.getClassRealm();
                    realm.addURL(url);
                }
                catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
   
	 public void execute() throws MojoExecutionException {
		 URL[] runtimeUrls = null;
		 System.out.println("Starting");
//		 try{
//		     
//		     if (project == null) {
//		         System.out.println("ERROR");
//		     }
//		     
//			 List runtimeClasspathElements = project.getRuntimeClasspathElements();
//			 System.out.println("Have classpath Elements");
//			 runtimeUrls = new URL[runtimeClasspathElements.size()];		
//		 	 for (int i = 0; i < runtimeClasspathElements.size(); i++) {
//		 		 String element = (String) runtimeClasspathElements.get(i);
//		 		 runtimeUrls[i] = new File(element).toURI().toURL();
//		 	 }
//		 } catch(MalformedURLException | DependencyResolutionRequiredException e){
//			 System.out.println(e);
//			 return;
//		 }
//		 System.out.println("Trying to get Loader");
//		 URLClassLoader newLoader = new URLClassLoader(runtimeUrls,
//		   Thread.currentThread().getContextClassLoader());
//		 
//		 if(filePersistorName.equals("NoName")){
//			 return;
//		 }
		 System.out.println("I'm looking for a persistor " + filePersistorName + " my target is " + outputDir);
		 ICoalescePersistor persistor;
		 try
         {
		     addDependenciesToClasspath(filePersistorName);
		     
             Object persister = ClassLoader.getSystemClassLoader().loadClass(filePersistorName).newInstance();
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
            		 } catch (SAXException | IOException | CoalesceException e) {
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
                 //LOGGER.warn("(FAILED) Loading Persister ({}): {}", filePeristorName, "Invalid Implementation");
             }
         }
         catch (ClassNotFoundException | InstantiationException | IllegalAccessException |CoalescePersistorException e)
         {
        	 System.out.println(e);
            // LOGGER.warn("(FAILED) Loading Persister ({})", filePeristorName, e);
         }
	 }
}
