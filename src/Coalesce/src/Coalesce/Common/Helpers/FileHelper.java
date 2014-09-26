package Coalesce.Common.Helpers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FilenameUtils;

import Coalesce.Common.Exceptions.CoalesceCryptoException;
import Coalesce.Common.Runtime.CoalesceSettings;
import Coalesce.Framework.Persistance.CoalesceEncrypter;
import Coalesce.Framework.Persistance.ICoalesceEncrypter;

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
 *
 */
public class FileHelper {

    // Make static
    private FileHelper()
    {

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
        if (StringHelper.IsNullOrEmpty(filename)) return "";

        String extension = FilenameUtils.getExtension(filename).toLowerCase();

        return extension;

    }

    /**
     * Gets the name minus the path from a full filename.
     *
     * @param filename the filename
     * @return the name of the file without the path, or an empty string if none exists
     * @see org.apache.commons.io.FilenameUtils#getName(String filename)
     */
    public static String getShortFilename(String filename)
    {
        String name = FilenameUtils.getName(filename);

        return name;

    }

    public static byte[] getFileAsByteArray(String filename) throws IOException
    {
        return getFileAsByteArray(filename, false);
    }

    /**
     * Returns all the bytes for the file. If the file is encrypted then it is first decrypted using the pass phrase returned
     * by {@link CoalesceSettings#getPassPhrase()}
     * 
     * @param filename the filename
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
     * Deletes the file if it exists.
     * 
     * @param filename the full path and name of the file to be deleted.
     * @return <code>true</code> if the file is successfully deleted.
     */
    public static boolean deleteFile(String filename)
    {
        Path path = Paths.get(filename);
        if (Files.exists(path))
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
     * Deletes the folder if it exists. If the folder path does not point to a folder then it will not be deleted.
     * 
     * @param folderPath the full folder path
     * @return <code>true</code> if the folder is successfully deleted.
     */
    public static boolean deleteFolder(String folderPath)
    {
        Path path = Paths.get(folderPath);
        if (Files.isDirectory(path) && Files.exists(path))
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
     * Checks if the folder already exists in the filesystem and if it does not then attempts to create it.
     * 
     * @param folderPath the full folder path
     * @return <code>true</code> if the folder already exists or was successfully created. <code>false</code> if the
     *         <code>folderPath</code> points to a non-folder or creation of the folder fails.
     */
    public static boolean checkFolder(String folderPath)
    {
        Path path = Paths.get(folderPath);
        if (Files.exists(path))
        {

            return Files.isDirectory(path);

        }
        else
        {

            try
            {
                Files.createDirectory(path);
            }
            catch (IOException e)
            {
                return false;
            }

            return true;
        }
    }

    /**
     * Returns the base filename including the full path generated for the provided <code>key</code>. This method using the
     * values from both {@link CoalesceSettings#getBinaryFileStoreBasePath()} and
     * {@link CoalesceSettings#getSubDirectoryLength()} to build the path.
     * 
     * @param key the key to use for generating the path.
     * @return the base filename including the full path.
     */
    public static String getBaseFilenameWithFullDirectoryPathForKey(String key)
    {
        return getBaseFilenameWithFullDirectoryPathForKey(key, true);
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

        if (key == null || StringHelper.IsNullOrEmpty(key.trim())) return null;

        String baseFilename = GUIDHelper.removeBrackets(key);

        if (baseFilename == null) return null;

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
