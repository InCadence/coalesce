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

import java.util.ArrayList;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;

import com.incadencecorp.coalesce.common.helpers.StringHelper;

/**
 * This class is a wrapper for elements within Coalesce that can contain
 * history.
 * 
 * @author n78554
 *
 */
public abstract class CoalesceObjectHistory extends CoalesceObject implements ICoalesceObjectHistory {

    private CoalesceObjectHistoryType _object;
    private boolean _suspendHistory = false;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/
    /**
     * Class constructor. Creates a CoalesceObject class.
     */
    CoalesceObjectHistory()
    {
        // Do Nothing
    }

    /**
     * Class constructor. Creates a CoalesceObject class.
     * 
     * @param coalesceObject allowed object is {@link CoalesceObject }
     */
    CoalesceObjectHistory(CoalesceObjectHistory coalesceObject)
    {
        super(coalesceObject);

        // Copy Member Variables
        _object = coalesceObject._object;
    }

    protected boolean initialize(CoalesceObjectHistoryType object)
    {

        _object = object;

        for (History history : _object.getHistory())
        {

            CoalesceHistory coalesceHistory = new CoalesceHistory();
            coalesceHistory.initialize(this, history);

            // Add to Child Collection
            addChildCoalesceObject(coalesceHistory.getKey(), coalesceHistory);
        }

        return super.initialize(object);

    }

    protected boolean initialize(CoalesceObjectHistory coalesceObject)
    {

        _object = coalesceObject._object;

        for (History history : _object.getHistory())
        {

            CoalesceHistory coalesceHistory = new CoalesceHistory();
            coalesceHistory.initialize(this, history);

            // Add to Child Collection
            addChildCoalesceObject(coalesceHistory.getKey(), coalesceHistory);
        }

        return super.initialize(coalesceObject);
    }

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    @Override
    public void createHistory(String user, String ip, Integer version)
    {

        // History Suspended?
        if (!isSuspendHistory())
        {
            History hObject = new History();

            // Set References
            CoalesceHistory history = new CoalesceHistory();
            if (history.initialize(this, hObject))
            {
                Map<QName, String> attributes = getAttributes();

                attributes.remove(new QName("key"));

                for (Map.Entry<QName, String> attribute : attributes.entrySet())
                {
                    if (!StringHelper.isNullOrEmpty(attribute.getValue()))
                    {
                        history.setAttribute(attribute.getKey().toString(), attribute.getValue());
                    }
                }
                
                // Append to parent's child node collection
                _object.getHistory().add(0, hObject);

                addChildCoalesceObject(history);

                // Add History
                setPreviousHistoryKey(history.getKey());
                setModifiedBy(user);
                setModifiedByIP(ip);
                setObjectVersion(version);
                
            }

        }

    }

    @Override
    public boolean isDisableHistory()
    {
        return getBooleanElement(_object.isDisablehistory());
    }

    @Override
    public void setDisableHistory(boolean disable)
    {
        if (disable)
        {
            _object.setDisablehistory(disable);
        }
        else
        {
            _object.setDisablehistory(null);
        }

        _suspendHistory = disable;

    }

    @Override
    public boolean isSuspendHistory()
    {
        return (_suspendHistory || isDisableHistory());
    }

    @Override
    public void setSuspendHistory(boolean suspend)
    {
        if (!isDisableHistory())
        {
            _suspendHistory = suspend;
        }
    }

    @Override
    public CoalesceHistory[] getHistory()
    {
        ArrayList<CoalesceHistory> historyList = new ArrayList<CoalesceHistory>();

        // Return history items in the same order they are in the Entity
        for (History history : _object.getHistory())
        {

            CoalesceObject fdo = getChildCoalesceObject(history.getKey());

            if (fdo != null && fdo instanceof CoalesceHistory)
            {
                historyList.add((CoalesceHistory) getChildCoalesceObject(history.getKey()));
            }
        }

        return historyList.toArray(new CoalesceHistory[historyList.size()]);
    }

    @Override
    public void clearHistory()
    {
        _object.setPrevioushistorykey(null);
        _object.getHistory().clear();
    }

    @Override
    public CoalesceHistory getHistoryRecord(String historyKey)
    {
        CoalesceHistory historyRecord = (CoalesceHistory) getChildCoalesceObject(historyKey);

        return historyRecord;

    }

    @Override
    protected boolean setExtendedAttributes(String name, String value)
    {
        return setOtherAttribute(name, value);
    }

}
