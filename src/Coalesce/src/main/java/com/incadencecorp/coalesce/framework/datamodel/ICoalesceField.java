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
package com.incadencecorp.coalesce.framework.datamodel;

import java.util.Locale;

import com.incadencecorp.coalesce.common.exceptions.CoalesceDataFormatException;

/**
 * Interface for access fields of Coalesce Entities.
 *
 * @param <T>
 */
public interface ICoalesceField<T> extends ICoalesceObject {

    /**
     * Return the value of the Field's Value attribute.
     * 
     * @return String of the Field's value attribute.
     * @throws CoalesceDataFormatException
     */
    T getValue() throws CoalesceDataFormatException;

    /**
     * Return the value of the Field's Label attribute.
     * 
     * @return String of the Field's label attribute.
     */
    String getLabel();

    /**
     * Return the value of the Field's DataType attribute.
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.ECoalesceFieldDataTypes}
     *         of the Field's DataType attribute.
     */
    ECoalesceFieldDataTypes getDataType();

    /**
     * Returns the string representation of the classification marking.
     * 
     * @return String, the classification marking.
     */
    String getClassificationMarkingAsString();

    /**
     * Returns the Input Language used when the Field's value was set.
     * 
     * @return Locale of the Field's InputLang attribute.
     */
    Locale getInputLang();

    /**
     * Sets the value of the Field's Value attribute.
     * 
     * @param value String to be the Field's value attribute.
     * @throws CoalesceDataFormatException
     */
    void setValue(T value) throws CoalesceDataFormatException;

    /**
     * Sets the value of the Field's Label attribute.
     * 
     * @param value String to be the Field's label attribute.
     */
    void setLabel(String value);

    /**
     * Sets the classification marking to the value of the string parameter.
     * 
     * @param value String, the new classification marking.
     */
    void setClassificationMarkingAsString(String value);

    /**
     * Sets the Input Language used as the Field's value is set.
     * 
     * @param value Locale to be the Field's InputLang attribute.
     */
    void setInputLang(Locale value);

}
