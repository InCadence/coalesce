package com.incadencecorp.coalesce.common.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incadencecorp.coalesce.api.CoalesceParameters;
import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.framework.CoalesceSettings;
import com.incadencecorp.coalesce.framework.persistance.CoalesceEncrypter;
import com.incadencecorp.coalesce.framework.persistance.ICoalesceEncrypter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

/**
 * Provides helper methods for accessing and interacting with files.
 *
 * @author InCadence
 */
public final class FileHelper {

    // Make static
    private FileHelper()
    {
        // Do Nothing
    }

    /*--------------------------------------------------------------------------
        Public Shared Methods
     --------------------------------------------------------------------------*/

    /**
     * Returns the extension for the filename.
     *
     * @param filename the filename
     * @return the extension for the filename
     * @see org.apache.commons.io.FilenameUtils#getExtension(String filename)
     */
    public static String getExtension(String filename)
    {
        if (StringHelper.isNullOrEmpty(filename))
            return "";

        return FilenameUtils.getExtension(filename).toLowerCase();

    }

    /**
     * Gets the name minus the path from a full filename.
     *
     * @param filename the filename
     * @return the name of the file without the path, or an empty string if none
     * exists
     * @see org.apache.commons.io.FilenameUtils#getName(String filename)
     */
    public static String getShortFilename(String filename)
    {

        return FilenameUtils.getName(filename);

    }

    public static byte[] getFileAsByteArray(String filename) throws IOException
    {
        return getFileAsByteArray(filename, false);
    }

    /**
     * Returns all the bytes for the file. If the file is encrypted then it is
     * first decrypted using the pass phrase returned by
     * {@link CoalesceSettings#getPassPhrase()}
     *
     * @param filename  the filename
     * @param encrypted whether the file needs to be decrypted first
     * @return the bytes from the file.
     * @throws IOException
     */
    public static byte[] getFileAsByteArray(String filename, boolean encrypted) throws IOException
    {
        Path path = Paths.get(filename);
        if (Files.exists(path) && !Files.isDirectory(path))
        {

            if (encrypted)
            {
                ICoalesceEncrypter cipher;
                try
                {
                    cipher = new CoalesceEncrypter(CoalesceSettings.getPassPhrase());
                }
                catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e)
                {
                    return null;
                }

                try
                {
                    return cipher.decryptValueToBytes(Files.readAllBytes(path));
                }
                catch (CoalesceCryptoException cce)
                {
                    return null;
                }
            }
            else
            {
                return Files.readAllBytes(path);
            }
        }

        return null;

    }

    /**
     * Deletes the file if it exists. If the <code>filename</code> is a
     * directory then the deletion will fail.
     *
     * @param filename the full path and name of the file to be deleted.
     * @return <code>true</code> if the file is successfully deleted.
     */
    public static boolean deleteFile(String filename)
    {
        if (filename == null)
            return false;

        Path path;
        try
        {
            path = Paths.get(filename);
        }
        catch (InvalidPathException ipe)
        {
            return false;
        }

        if (Files.exists(path) && !Files.isDirectory(path))
        {
            try
            {
                Files.delete(path);
            }
            catch (IOException e)
            {
                return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Deletes the folder if it exists. If the folder path does not point to a
     * folder then it will not be deleted. Folders will be delete even if they
     * are not empty.
     *
     * @param folderPath the full folder path
     * @return <code>true</code> if the folder is successfully deleted.
     */
    public static boolean deleteFolder(String folderPath)
    {
        return FileHelper.deleteFolder(folderPath, true);
    }

    /**
     * Deletes the folder if it exists. If the folder path does not point to a
     * folder then it will not be deleted. Folders that are not empty will only
     * be deleted if <code>forceDelete</code> is <code>true</code>.
     *
     * @param folderPath the full folder path
     * @return <code>true</code> if the folder is successfully deleted.
     */
    public static boolean deleteFolder(String folderPath, boolean forceDelete)
    {
        if (folderPath == null)
            return false;

        Path path;
        try
        {
            path = Paths.get(folderPath);
        }
        catch (InvalidPathException ipe)
        {
            return false;
        }

        if (Files.exists(path) && Files.isDirectory(path))
        {
            try
            {
                if (forceDelete)
                {
                    FileUtils.cleanDirectory(new File(folderPath));
                    Files.delete(Paths.get(folderPath));
                }
                else
                {
                    Files.delete(path);
                }

                return true;

            }
            catch (IOException e)
            {
                return false;
            }
        }

        return false;
    }

    /**
     * Checks if the folder already exists in the file system and if it does not
     * then attempts to create it including all missing parent directories.
     *
     * @param folderPath the full folder path
     * @return <code>true</code> if the folder already exists or was
     * successfully created. <code>false</code> if the
     * <code>folderPath</code> points to a non-folder or creation of the
     * folder fails.
     */
    public static boolean checkFolder(String folderPath)
    {
        if (folderPath == null)
            return false;

        Path path;
        try
        {
            path = Paths.get(folderPath);
        }
        catch (InvalidPathException ipe)
        {
            return false;
        }

        if (Files.exists(path))
        {
            return Files.isDirectory(path);
        }
        else
        {
            try
            {
                Files.createDirectory(path);

                return true;
            }
            catch (IOException e)
            {
                return false;
            }
        }
    }

    /**
     * Returns the base filename including the full path generated for the
     * provided <code>key</code>. This method using the values from both
     * {@link CoalesceSettings#getBinaryFileStoreBasePath()} and
     * {@link CoalesceSettings#getSubDirectoryLength()} to build the path.
     *
     * @param key the key to use for generating the path.
     * @return the base filename including the full path.
     */
    public static String getBaseFilenameWithFullDirectoryPathForKey(String key)
    {
        return getBaseFilenameWithFullDirectoryPathForKey(key, true);
    }

    /**
     * @param value
     * @return If the value provided is relative it will return a URI with an
     * absolute path from System.getProperty("user.dir").
     * @throws URISyntaxException
     */
    public static URI getFullPath(String value) throws URISyntaxException
    {
        URI directory = new URI(value);

        if (!directory.isAbsolute())
        {
            directory = Paths.get(System.getProperty("user.dir"), value).toUri();
        }

        return directory;
    }

    /**
     * @param name  of JSON file
     * @param clazz to map the JSON into
     * @param <T>
     * @return the JSON serialized into the specified class.
     * @throws IOException on error
     */
    public static <T> T loadJSON(String name, Class<T> clazz) throws IOException
    {
        T result;

        Path path = Paths.get(CoalesceParameters.COALESCE_CONFIG_LOCATION).resolve(name + ".json");
        if (Files.exists(path))
        {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(path.toFile(), clazz);
        }
        else
        {
            throw new FileNotFoundException(path.toString());
        }

        return result;
    }

    /*--------------------------------------------------------------------------
        Private Shared Methods
    --------------------------------------------------------------------------*/

    private static String getBaseFilenameWithFullDirectoryPathForKey(String key, boolean createIfDoesNotExist)
    {
        return getBaseFilenameWithFullDirectoryPathForKey(CoalesceSettings.getBinaryFileStoreBasePath(),
                                                          CoalesceSettings.getSubDirectoryLength(),
                                                          key,
                                                          createIfDoesNotExist);
    }

    private static String getBaseFilenameWithFullDirectoryPathForKey(String binaryFileStoreBasePath,
                                                                     int subDirectoryLength,
                                                                     String key,
                                                                     boolean createIfDoesNotExist)
    {

        if (key == null || StringHelper.isNullOrEmpty(key.trim()) || binaryFileStoreBasePath == null)
            return null;

        String baseFilename = GUIDHelper.removeBrackets(key);

        if (baseFilename == null)
            return null;

        String fullDirectory;

        if (subDirectoryLength > 0 && subDirectoryLength < baseFilename.length())
        {

            fullDirectory = FilenameUtils.concat(binaryFileStoreBasePath, baseFilename.substring(0, subDirectoryLength));
        }
        else
        {
            fullDirectory = binaryFileStoreBasePath;
        }

        if (createIfDoesNotExist)
        {
            File fileDir = new File(fullDirectory);
            if (!fileDir.exists())
            {
                fileDir.mkdirs();
            }
        }

        return FilenameUtils.concat(fullDirectory, baseFilename);

    }

}
