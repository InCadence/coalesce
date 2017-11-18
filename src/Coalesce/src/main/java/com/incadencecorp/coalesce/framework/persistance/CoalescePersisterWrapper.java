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

package com.incadencecorp.coalesce.framework.persistance;

import com.incadencecorp.coalesce.api.persistance.EPersistorCapabilities;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import com.incadencecorp.coalesce.common.helpers.StringHelper;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntityTemplate;

import java.util.*;

/**
 * This implementation is a wrapper for the interface allowing filters to be
 * specified to either exclude or include entities from being saved through the
 * specified persister. <code>*</code> can be used as wild cards.
 *
 * @author n78554
 */
public class CoalescePersisterWrapper implements ICoalescePersistor {

    private static final String[] KEY_FORMATS = new String[] { "%1$s.%2$s.%3$s", "%1$s.%2$s.*", "%1$s.*.*", "%1$s.*.%3$s",
                                                               "*.%2$s.*", "*.%2$s.%3$s", "*.*.%3$s", "*.*.*"
    };

    /*--------------------------------------------------------------------------
    Private Member Variables
    --------------------------------------------------------------------------*/

    private ICoalescePersistor persister;
    private boolean isExclusion = false;
    private Map<String, ObjectMetaData> filters;

    /*--------------------------------------------------------------------------
    Constructors
    --------------------------------------------------------------------------*/

    /**
     * Creates a wrapper specifying filters to use for the specified persister.
     *
     * @param persister   is the persister that this implementation wraps.
     * @param isExclusion if <code>true</code> and an entity appears in the list
     *                    it will be denied and vice-versa if this value is
     *                    <code>false</code>.
     * @param filters     Specifying a * or leaving the property blank means it will
     *                    be treated as a wild. Setting
     *                    {@link ObjectMetaData#getCreated()} and
     *                    {@link ObjectMetaData#getLastModified()} has no affect.
     */
    public CoalescePersisterWrapper(ICoalescePersistor persister, boolean isExclusion, ObjectMetaData... filters)
    {
        this.persister = persister;
        this.isExclusion = isExclusion;
        this.filters = new HashMap<String, ObjectMetaData>();

        for (ObjectMetaData meta : filters)
        {
            if (meta.getKey() != null)
            {
                // Re-create the meta data with a lowercase key
                meta = new ObjectMetaData(meta.getKey().toLowerCase(), meta.getName(), meta.getSource(), meta.getVersion());
            }

            this.filters.put(CreateKey(meta.getName(), meta.getSource(), meta.getVersion()), meta);
        }
    }

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * @return the persister this wrapper wraps.
     */
    public ICoalescePersistor getPersister()
    {
        return persister;
    }

    /**
     * @param entity
     * @return whether this persistor should handle this entity.
     */
    public boolean isAllowed(CoalesceEntity entity)
    {
        return isAllowed(new ObjectMetaData(entity));
    }

    /**
     * @param data
     * @return whether this persistor should handle this entity.
     */
    public boolean isAllowed(ObjectMetaData data)
    {
        ObjectMetaData filter = getMetaData(data.getName(), data.getSource(), data.getVersion());

        // Entry Found?
        if (filter != null)
        {
            // Yes; Check the key
            if (filter.getKey() != null && data.getKey() != null && !filter.getKey().equals("*")
                    && !data.getKey().toLowerCase().startsWith(filter.getKey()))
            {
                // Invalid Key
                filter = null;
            }
        }

        return (isExclusion && filter == null) || (!isExclusion && filter != null);
    }

    /*--------------------------------------------------------------------------
    ICoalescePersister Override Methods
    --------------------------------------------------------------------------*/

    @Override
    public boolean saveEntity(boolean allowRemoval, CoalesceEntity... entities) throws CoalescePersistorException
    {
        List<CoalesceEntity> allowed = new ArrayList<CoalesceEntity>();

        for (CoalesceEntity entity : entities)
        {
            if (isAllowed(entity))
            {
                allowed.add(entity);
            }
        }

        return persister.saveEntity(allowRemoval, allowed.toArray(new CoalesceEntity[allowed.size()]));
    }

    @Override
    public CoalesceEntity[] getEntity(String... keys) throws CoalescePersistorException
    {
        return persister.getEntity(keys);
    }

    @Override
    public String[] getEntityXml(String... keys) throws CoalescePersistorException
    {
        return persister.getEntityXml(keys);
    }

    @Override
    public void saveTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        persister.saveTemplate(templates);
    }

    @Override
    public void registerTemplate(CoalesceEntityTemplate... templates) throws CoalescePersistorException
    {
        List<CoalesceEntityTemplate> allowed = new ArrayList<CoalesceEntityTemplate>();

        for (CoalesceEntityTemplate template : templates)
        {
            if (isAllowed(new ObjectMetaData(null, template.getName(), template.getSource(), template.getVersion())))
            {
                allowed.add(template);
            }
        }

        persister.registerTemplate(allowed.toArray(new CoalesceEntityTemplate[allowed.size()]));
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String key) throws CoalescePersistorException
    {
        return persister.getEntityTemplate(key);
    }

    @Override
    public CoalesceEntityTemplate getEntityTemplate(String name, String source, String version) throws CoalescePersistorException
    {
        return persister.getEntityTemplate(name, source, version);
    }

    @Override
    public String getEntityTemplateKey(String name, String source, String version) throws CoalescePersistorException
    {
        return persister.getEntityTemplateKey(name, source, version);
    }

    @Override
    public List<ObjectMetaData> getEntityTemplateMetadata() throws CoalescePersistorException
    {
        return persister.getEntityTemplateMetadata();
    }

    @Override
    public EnumSet<EPersistorCapabilities> getCapabilities()
    {
        return persister.getCapabilities();
    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    private String CreateKey(String name, String source, String version)
    {
        if (StringHelper.isNullOrEmpty(name))
        {
            name = "*";
        }
        if (StringHelper.isNullOrEmpty(source))
        {
            source = "*";
        }
        if (StringHelper.isNullOrEmpty(version))
        {
            version = "*";
        }

        return String.format(KEY_FORMATS[0], name.toLowerCase(), source.toLowerCase(), version.toLowerCase());
    }

    private ObjectMetaData getMetaData(String name, String source, String version)
    {
        ObjectMetaData result = null;

        for (String format : KEY_FORMATS)
        {
            String key = String.format(format, name.toLowerCase(), source.toLowerCase(), version.toLowerCase());
            if (filters.containsKey(key))
            {
                result = filters.get(key);
                break;
            }
        }

        return result;
    }

}
