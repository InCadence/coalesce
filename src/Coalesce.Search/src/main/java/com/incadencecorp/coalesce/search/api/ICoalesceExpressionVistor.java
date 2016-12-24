/*-----------------------------------------------------------------------------'
 Copyright 2016 - InCadence Strategic Solutions Inc., All Rights Reserved

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

package com.incadencecorp.coalesce.search.api;

import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.PropertyName;

import com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes;

/**
 * Interface to be implemented by vistors to provide callbacks for determining
 * the context of a property.
 * 
 * @author n78554
 */
public interface ICoalesceExpressionVistor extends ExpressionVisitor {

    /**
     * @param recordset
     * @param fieldname
     * @return the context of the provided record set and field name.
     */
    Class<?> getContext(String recordset, String fieldname);

    /**
     * @param name
     * @return the context of the provided property.
     */
    Class<?> getContext(PropertyName name);

    /**
     * @param name
     * @return the context of the provided property.
     */
    ECoalesceFieldDataTypes getDataType(PropertyName name);

    /**
     * @return the factory used by this vistor.
     */
    FilterFactory getFilterFactory();

    /**
     * @param value
     * @return the string normalized for this vistor.
     */
    String normalize(String value);

}
