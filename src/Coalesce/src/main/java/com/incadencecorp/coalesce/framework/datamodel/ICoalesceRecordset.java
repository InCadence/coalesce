package com.incadencecorp.coalesce.framework.datamodel;

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
 * Interface for access record sets of Coalesce Entities. 
 */
public interface ICoalesceRecordset {

    /**
     * Return the value of the RecordSet's Name attribute.
     * 
     * @return String of the RecordSet's name attribute.
     */
    String getName();

    /**
     * Return the value corresponding to the minimum number of records the RecordSet must contain.
     * 
     * @return integer of the RecordSet's min records attribute.
     */
    int getMinRecords();

    /**
     * Return the value corresponding to the maximum number of records the RecordSet must contain.
     * 
     * @return integer of the RecordSet's max records attribute.
     */
    int getMaxRecords();

    /**
     * Sets the value of the RecordSet's Name attribute.
     * 
     * @param value String to be the RecordSet's name attribute.
     */
    void setName(String value);

    /**
     * Sets the value corresponding to the minimum number of records the RecordSet must contain.
     * 
     * @param value integer to be the RecordSet's min records attribute.
     */
    void setMinRecords(int value);

    /**
     * Sets the value corresponding to the maximum number of records the RecordSet must contain.
     * 
     * @param value integer to be the RecordSet's max records attribute.
     */
    void setMaxRecords(int value);
}
