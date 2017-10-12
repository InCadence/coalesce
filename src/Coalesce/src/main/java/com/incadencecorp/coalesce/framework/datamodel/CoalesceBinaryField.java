/**
 * 
 */
package com.incadencecorp.coalesce.framework.datamodel;

import org.apache.xerces.impl.dv.util.Base64;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;

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
 * This field type stores binary data within the XML. Consider using FILE type instead.
 */
public class CoalesceBinaryField extends CoalesceBinaryFieldBase<byte[]>{

    @Override
    public byte[] getValue() throws CoalesceDataFormatException
    {
        if (getDataType() != ECoalesceFieldDataTypes.BINARY_TYPE && getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        String rawValue = getBaseValue();

        if (rawValue == null)
            return null;

        if (rawValue != null && rawValue.length() > 0)
        {
            byte[] bytes = Base64.decode(rawValue);

            return bytes;

        }
        else
        {
            return new byte[0];
        }
    }

    @Override
    public void setValue(byte[] value)
    {
        if (getDataType() != ECoalesceFieldDataTypes.BINARY_TYPE && getDataType() != ECoalesceFieldDataTypes.FILE_TYPE)
        {
            throw new ClassCastException("Type mismatch");
        }

        if (value != null)
        {
            setBaseValue(Base64.encode(value));
            setSize(value.length);
        }
        else
        {
            setBaseValue(null);
            setSize(0);
        }
    }
    
}
