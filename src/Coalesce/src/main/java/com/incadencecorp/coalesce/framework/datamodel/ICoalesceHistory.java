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

/**
 * Interface for history elements.
 * 
 * @author n78554
 *
 */
public interface ICoalesceHistory {

    /**
     * @return the identification of who last modified a attribute.
     */
    String getModifiedBy();

    /**
     * 
     * @return the IP address of the computer who last modified a attribute.
     */
    String getModifiedByIP();

    /**
     * @return the history key of the previous history.
     */
    String getPreviousHistoryKey();

    /**
     * Sets the history key of the previous history.
     * 
     * @param value
     */
    void setPreviousHistoryKey(String value);

    /**
     * Sets the identification of who last modified a attribute.
     * 
     * @param value
     */
    void setModifiedBy(String value);

    /**
     * Sets the IP address of the computer who last modified a attribute.
     * 
     * @param value
     */
    void setModifiedByIP(String value);

}
