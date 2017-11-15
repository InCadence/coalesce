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

package com.incadencecorp.coalesce.framework.datamodel;

import org.apache.commons.lang.NullArgumentException;
import org.apache.xerces.impl.dv.util.Base64;

import com.incadencecorp.coalesce.api.ICoalesceBinaryField;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;
import com.incadencecorp.coalesce.common.helpers.MimeHelper;

abstract public class CoalesceBinaryFieldBase<T> extends CoalesceField<T> implements ICoalesceBinaryField {

    public static final String ATTRIBUTE_FILENAME = "filename";
    public static final String ATTRIBUTE_EXTENSION = "extension";
    public static final String ATTRIBUTE_MIME_TYPE = "mimetype";
    public static final String ATTRIBUTE_SIZE = "size";
    public static final String ATTRIBUTE_HASH = "hash";

    /**
     * Sets the Field's value by the byte array parameter. Also sets the
     * filename, extension and MIME type by the Document Properties.
     * 
     * @param dataBytes, file's value as a byte array
     * @param docProps, file's DocumentProperties
     */
    public void setValue(byte[] dataBytes, DocumentProperties docProps)
    {
        if (getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (docProps != null)
        {
            setValue(dataBytes, docProps.getFilename(), docProps.getExtension());
        }
        else
        {
            setValue(dataBytes, null, null);
        }
    }
    
    /**
     * Sets the Field's value by the byte array parameter. Also sets the
     * filename, extension and MIME type.
     * 
     * @param dataBytes, field's value as a byte array
     * @param filename, field's filename
     * @param extension, field's extension
     */
    public void setValue(byte[] dataBytes, String filename, String extension)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BINARY_TYPE && getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (filename == null)
        {
            setBaseValue(null);
            setFilename(null);
            setExtension(null);
            setMimeType(null);
            setSize(0);
        }
        else
        {
            if (dataBytes == null)
                throw new NullArgumentException("dataBytes");

            setBaseValue(Base64.encode(dataBytes));
            setFilename(filename);
            setExtension(extension);
            setMimeType(MimeHelper.getMimeTypeForExtension(extension));
            setSize(dataBytes.length);
        }
    }
    
    @Override
    public int getSize()
    {
        try
        {
            return Integer.parseInt(getOtherAttribute(ATTRIBUTE_SIZE));
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    @Override
    public void setSize(int value)
    {
        setOtherAttribute(ATTRIBUTE_SIZE, Integer.toString(value));
    }

    @Override
    public String getFilename()
    {
        return getStringElement(getOtherAttribute(ATTRIBUTE_FILENAME));
    }

    @Override
    public void setFilename(String value)
    {
        // Don't Allow null
        if (value == null)
            value = "";

        createHistory(getOtherAttribute(ATTRIBUTE_FILENAME), value);
        setOtherAttribute(ATTRIBUTE_FILENAME, value);
    }

    @Override
    public String getExtension()
    {
        return getStringElement(getOtherAttribute(ATTRIBUTE_EXTENSION));
    }

    @Override
    public void setExtension(String value)
    {
        // Don't Allow null
        if (value == null)
            value = "";

        createHistory(getOtherAttribute(ATTRIBUTE_EXTENSION), value);
        setOtherAttribute(ATTRIBUTE_EXTENSION, value.replace(".", ""));
    }

    @Override
    public String getMimeType()
    {
        return getStringElement(getOtherAttribute(ATTRIBUTE_MIME_TYPE));
    }

    @Override
    public void setMimeType(String value)
    {
        setOtherAttribute(ATTRIBUTE_MIME_TYPE, value);
    }

    @Override
    public String getHash()
    {
        return getStringElement(getOtherAttribute(ATTRIBUTE_HASH));
    }

    @Override
    public void setHash(String value)
    {
        // Don't Allow null
        if (value == null)
            value = "";

        createHistory(getOtherAttribute(ATTRIBUTE_HASH), value);
        setOtherAttribute(ATTRIBUTE_HASH, value);
    }
}
