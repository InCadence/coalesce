package com.incadencecorp.coalesce.framework.datamodel;

import java.io.IOException;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;
import com.incadencecorp.coalesce.common.helpers.DocumentProperties;

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

public class CoalesceFileField extends CoalesceField<DocumentProperties> {

    /*
     * (non-Javadoc)
     * 
     * @see com.incadencecorp.coalesce.framework.datamodel.CoalesceField#getValue()
     */
    @Override
    public DocumentProperties getValue() throws CoalesceDataFormatException
    {
        return getFileValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.incadencecorp.coalesce.framework.datamodel.CoalesceField#setValue(java.lang.Object)
     */
    @Override
    public void setValue(DocumentProperties value)
    {
        try
        {
            setTypedValue(value);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sets the Field's value by the byte array parameter. Also sets the filename, extension and MIME type.
     * 
     * @param dataBytes, field's value as a byte array
     * @param filename, field's filename
     * @param extension, field's extension
     */
    public void setValue(byte[] dataBytes, String filename, String extension)
    {
        setTypedValue(dataBytes, filename, extension);
    }

    /**
     * Sets the Field's hash value. Also sets the filename, extension and MIME type.
     * 
     * @param filename, field's filename
     * @param extension, field's extension
     * @param hash, field's hash value
     */
    public void setValue(String filename, String extension, String hash)
    {
        setTypedValue(filename, extension, hash);
    }

    /**
     * Sets the Field's value by the byte array parameter. Also sets the filename, extension and MIME type by the Document
     * Properties.
     * 
     * @param dataBytes, file's value as a byte array
     * @param docProps, file's DocumentProperties
     */
    protected void setValue(byte[] dataBytes, DocumentProperties docProps)
    {
        setTypedValue(dataBytes, docProps);
    }

}
