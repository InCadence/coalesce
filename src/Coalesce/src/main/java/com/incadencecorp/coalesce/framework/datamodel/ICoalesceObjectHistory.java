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
 * This interface defines methods that are common amongst elements that contain
 * history.
 * 
 * @author n78554
 *
 */
public interface ICoalesceObjectHistory {

    /**
     * Creates a history entry for the current state of this element.
     * 
     * @param user
     * @param ip
     * @param version
     */
    void createHistory(String user, String ip, Integer version);

    /**
     * @return whether the history is to be disabled.
     */
    boolean isDisableHistory();

    /**
     * Sets the value indicating if history will be disabled. Unlike
     * SuspendHistory, this value is persisted with the object. The setting of
     * this value overrides the SuspendHistory value.
     * 
     * @param disable the value to set the disable history attribute to.
     */
    void setDisableHistory(boolean disable);

    /**
     * @return whether the history is currently being suspended.
     */
    boolean isSuspendHistory();

    /**
     * Sets the value indicating if history should be maintained for changes
     * made to this object instance of an element. This setting is not
     * persisted. The value of the fields disablehistory attribute (
     * {@link CoalesceObjectHistory#isDisableHistory()}) overrides this
     * temporary suspension.
     * 
     * @param suspend the value indicating if history should be temporarily
     *            suspended.
     */
    void setSuspendHistory(boolean suspend);

    /**
     * @return the history for this element.
     */
    CoalesceObject[] getHistory();

    /**
     * Removes all history records from the entity.
     */
    void clearHistory();

    /**
     * Returns an
     * {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceField}'s
     * change history entry.
     * 
     * @param historyKey
     * 
     * @return {@link com.incadencecorp.coalesce.framework.datamodel.CoalesceFieldHistory}
     *         the modification history of this field with matching key.
     */
    CoalesceObject getHistoryRecord(String historyKey);

}
