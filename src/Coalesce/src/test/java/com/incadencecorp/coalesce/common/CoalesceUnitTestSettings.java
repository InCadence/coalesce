package com.incadencecorp.coalesce.common;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assume;

import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.persistance.CoalesceEncrypter;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

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

public class CoalesceUnitTestSettings extends CoalesceSettings {

    public static void initialize() throws IOException
    {
        File projectRoot = new File("");

        String projectPath = projectRoot.getAbsolutePath();

        CoalesceUnitTestSettings.setDefaultApplicationRoot(FilenameUtils.concat(projectPath, "UnitTestBin\\bin"));

        CoalesceUnitTestSettings.setDefaultApplicationName("Coalesce.UnitTest");

        File root = new File(CoalesceUnitTestSettings.getDefaultApplicationRoot());

        if (root.exists())
        {
            FileUtils.cleanDirectory(root.getParentFile());
        }

        root.mkdirs();

        File uploads = new File(CoalesceUnitTestSettings.getBinaryFileStoreBasePath());

        uploads.mkdirs();

    }

    public static void tearDownAfterClass() throws IOException
    {
        Path dirPath = Paths.get(FilenameUtils.concat(CoalesceUnitTestSettings.getDefaultApplicationRoot(), ".."));

        FileUtils.cleanDirectory(new File(dirPath.toUri()));

        Files.delete(dirPath);

        setDefaultApplicationName(null);

        setDefaultApplicationRoot(null);
    }

    public static boolean setSubDirectoryLength(int value)
    {
        return settings.setSetting(getConfigurationFileName(), "Coalesce.FileStore.SubDirectoryLength", value);
    }

    public static URL getResource(String resource)
    {
        URL url;

        // Try with the Thread Context Loader.
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null)
        {
            url = classLoader.getResource(resource);
            if (url != null)
            {
                return url;
            }
        }

        // Let's now try with the System class loader
        classLoader = System.class.getClassLoader();
        if (classLoader != null)
        {
            url = classLoader.getResource(resource);
            if (url != null)
            {
                return url;
            }
        }

        // Last ditch attempt. Get the resource from the classpath.
        return ClassLoader.getSystemResource(resource);
    }

    public static String getResourceAbsolutePath(String resource)
    {
        URL url = CoalesceUnitTestSettings.getResource(resource);

        if (url != null)
        {
            try
            {
                return new File(url.toURI()).getAbsolutePath();
            }
            catch (URISyntaxException e)
            {
                return null;
            }
        }
        else
        {
            return null;
        }

    }
    
    public static void verifyEncryption() 
    {
        try 
        {
            CoalesceEncrypter aes = new CoalesceEncrypter(CoalesceSettings.getPassPhrase());
        
            aes.getEncryptionCipher();
        } 
        catch (CoalesceCryptoException | InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Assume.assumeNoException(e);
        }
    }

}
