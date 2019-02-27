/*-----------------------------------------------------------------------------'
 Copyright 2018 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.synchronizer.drone;

import com.incadencecorp.coalesce.synchronizer.service.SynchronizerService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Derek Clemenzi
 */
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] parameters) throws Exception
    {
        //System.setProperty( "user.dir", "/c/Users/Derek Clemenzi/Documents/oe-bdp/oe-services/spider/drone");
        LOGGER.info("Starting...");

        Path contextPath = Paths.get("config", "applicationContext.xml");

        ApplicationContext parent = new ClassPathXmlApplicationContext("classpath*:**/applicationContext.xml");

        ApplicationContext ctx = new FileSystemXmlApplicationContext(new String[] { contextPath.toString()
        }, parent);

        LOGGER.info("Context Override Location: ", contextPath.toAbsolutePath());

        SynchronizerService service = ctx.getBean("service", SynchronizerService.class);
        service.start();

        System.out.println("Press ENTER to exit");
        try
        {
            LOGGER.info("Running");
            while (true)
            {
                Thread.sleep(1000);

                try
                {
                    if (Thread.interrupted() || System.in.available() != 0)
                    {
                        LOGGER.info("User Stopped");
                        break;
                    }
                }
                catch (IOException e)
                {
                    // Do Nothing
                }
            }
        }
        catch (InterruptedException e)
        {
            LOGGER.warn("Application Interrupted");
        }

        service.stop();
    }

}
