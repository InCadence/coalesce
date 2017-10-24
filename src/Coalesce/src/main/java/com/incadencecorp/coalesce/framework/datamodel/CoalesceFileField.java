package com.incadencecorp.coalesce.framework.datamodel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang.NullArgumentException;
import org.apache.xerces.impl.dv.util.Base64;
import org.jdom2.JDOMException;

import com.drew.imaging.ImageProcessingException;
import com.incadencecorp.coalesce.api.ICoalesceBinaryField;
import com.incadencecorp.coalesce.common.exceptions.CoalesceCryptoException;
import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;
import com.incadencecorp.coalesce.common.helpers.FileHelper;
import com.incadencecorp.coalesce.common.helpers.GUIDHelper;
import com.incadencecorp.coalesce.common.helpers.MimeHelper;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.CoalesceSettings;

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

public class CoalesceFileField extends CoalesceBinaryFieldBase<DocumentProperties> {

    @Override
    public DocumentProperties getValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        try
        {
            if (StringHelper.isNullOrEmpty(getFilename()))
                return null;

            // Initialize Properties from Filename
            DocumentProperties properties = new DocumentProperties();
            properties.initialize(getFilename());

            return properties;
        }
        catch (ImageProcessingException | CoalesceCryptoException | IOException | JDOMException e)
        {
            throw new CoalesceDataFormatException("Invalid File");
        }
    }

    @Override
    public void setValue(DocumentProperties value)
    {
        try
        {
            if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
            {
                throw new ClassCastException("Type mismatch");
            }

            if (value != null)
            {
                // Does File Exist?
                Path path = Paths.get(value.getFullFilename());
                if (Files.exists(path))
                {
                    setValue(Files.readAllBytes(path), value);
                }
            }
            else
            {
                setValue(null, null);
            }
        }
        catch (IOException e)
        {
            // Rethrow as a runtime exception
            throw new RuntimeException(e);
        }
    }

    

    /**
     * Sets the Field's hash value. Also sets the filename, extension and MIME
     * type.
     * 
     * @param filename, field's filename
     * @param extension, field's extension
     * @param hash, field's hash value
     */
    public void setValue(String filename, String extension, String hash)
    {
        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        setFilename(filename);
        setExtension(extension);
        setMimeType(MimeHelper.getMimeTypeForExtension(extension));
        setHash(hash);
    }


    

    /**
     * Returns the filename with directory path and file extension.
     * <code>NOTE:</code> This method relies on the configuration settings for
     * both {@link CoalesceSettings#getBinaryFileStoreBasePath()} and
     * {@link CoalesceSettings#getSubDirectoryLength()} to build the directory
     * path.
     * 
     * @return String, full filename.
     */
    public String getCoalesceFullFilename()
    {

        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            return "";
        }

        String baseFilename = FileHelper.getBaseFilenameWithFullDirectoryPathForKey(getKey());

        return baseFilename + "." + getExtension();

    }

    /**
     * Returns the base filename and extension.
     * 
     * @return String, the filename and extension, without the path.
     */
    public String getCoalesceFilename()
    {

        if (getDataType() == ECoalesceFieldDataTypes.FILE_TYPE)
        {

            String baseFilename = getKey();
            baseFilename = GUIDHelper.removeBrackets(baseFilename);

            return baseFilename + "." + getExtension();

        }
        else
        {
            return "";
        }
    }

    /**
     * Returns the thumbnail base filename and extension.
     * 
     * @return String, the thumbnail's filename and extension, without the path.
     */
    public String getCoalesceThumbnailFilename()
    {

        if (getDataType() == ECoalesceFieldDataTypes.FILE_TYPE)
        {

            String baseFilename = getKey();
            baseFilename = GUIDHelper.removeBrackets(baseFilename);

            return baseFilename + "_thumb.jpg";

        }
        else
        {
            return "";
        }
    }

    /**
     * Returns the filename with directory path and file extension for a
     * thumbnail image. <code>NOTE:</code> This method relies on the
     * configuration settings for both
     * {@link CoalesceSettings#getBinaryFileStoreBasePath()} and
     * {@link CoalesceSettings#getSubDirectoryLength()} to build the directory
     * path.
     *
     * @return String, full thumbnail filename.
     */
    public String getCoalesceFullThumbnailFilename()
    {

        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            return "";
        }

        String baseFilename = FileHelper.getBaseFilenameWithFullDirectoryPathForKey(getKey());

        return baseFilename + "_thumb.jpg";

    }

    /**
     * Returns the thumbnail filename with a long representation of last
     * modified datetime (Name?lastmodifiedlong). Returns empty string when
     * filename does not exist. If an error is encountered, only the thumbnail
     * filename is returned.
     * 
     * @return String, full thumbnail filename with LastModifiedTag appended.
     */
    public String getCoalesceThumbnailFilenameWithLastModifiedTag()
    {
        try
        {
            String fullThumbPath = getCoalesceFullThumbnailFilename();
            if (StringHelper.isNullOrEmpty(fullThumbPath))
                return "";

            File theFile = new File(fullThumbPath);
            long lastModifiedTicks = theFile.lastModified();

            return theFile.getName() + "?" + lastModifiedTicks;

        }
        catch (Exception ex)
        {
            return getCoalesceThumbnailFilename();
        }
    }

    /**
     * Returns the filename with a long representation of last modified datetime
     * (Name?lastmodifiedlong). Returns empty string when filename does not
     * exist. If an error is encountered, only the filename is returned.
     * 
     * @return String, full filename with LastModifiedTag appended.
     */
    public String getCoalesceFilenameWithLastModifiedTag()
    {
        try
        {
            String fullPath = getCoalesceFullFilename();
            if (StringHelper.isNullOrEmpty(fullPath))
                return "";

            File theFile = new File(fullPath);
            long lastModifiedTicks = theFile.lastModified();

            return theFile.getName() + "?" + lastModifiedTicks;

        }
        catch (Exception ex)
        {
            return getCoalesceFilename();
        }
    }

}
