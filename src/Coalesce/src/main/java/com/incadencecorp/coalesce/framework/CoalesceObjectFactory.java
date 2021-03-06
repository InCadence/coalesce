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

package com.incadencecorp.coalesce.framework;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This factory creates and loads Coalesce objects.
 *
 * @author n78554
 */
public final class CoalesceObjectFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoalesceObjectFactory.class);

    /*--------------------------------------------------------------------------
    Private Members
    --------------------------------------------------------------------------*/

    private static final Map<String, Constructor<? extends CoalesceEntity>> map = new ConcurrentHashMap<>();

    private static final String DEFAULT_KEY = "*";

    /*--------------------------------------------------------------------------
    Public Methods
    --------------------------------------------------------------------------*/

    /**
     * Registers a class to a given class name.
     *
     * @param classname
     * @param clazz
     */
    public static void register(String classname, Class<? extends CoalesceEntity> clazz)
    {

        try
        {
            map.put(classname, clazz.getConstructor());
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            throw new IllegalArgumentException("Invalid CoalesceEntity", e);
        }

    }

    /**
     * Registers the default class to be used if a matching one is not present.
     *
     * @param clazz
     */
    public static void registerDefault(Class<? extends CoalesceEntity> clazz)
    {
        register(DEFAULT_KEY, clazz);
    }

    /**
     * Registers a Coalesce object.
     *
     * @param clazz
     */
    public static void register(Class<? extends CoalesceEntity> clazz)
    {
        register(clazz.getName(), clazz);
    }

    /**
     * Unregisters a Coalesce object.
     *
     * @param clazz
     */
    public static void unregister(Class<? extends CoalesceEntity> clazz)
    {
        unregister(clazz.getName());
    }

    /**
     * Unregisters the default.
     */
    public static void unregisterDefault()
    {
        unregister(DEFAULT_KEY);
    }

    /**
     * Unregisters a class name.
     *
     * @param classname
     */
    public static void unregister(String classname)
    {
        map.remove(classname);
    }

    /**
     * @param entity
     * @return a Coalesce object that can be safely cast between types.
     */
    public static CoalesceEntity createAndLoad(CoalesceEntity entity)
    {
        CoalesceEntity result = entity;

        try
        {
            if (entity != null)
            {
                if (entity.getClassName() != null)
                {
                    Constructor<? extends CoalesceEntity> constructor = getConstructor(entity.getClassName());

                    if (constructor != null)
                    {
                        result = constructor.newInstance();
                        if (!result.initialize(entity))
                        {
                            throw new IllegalArgumentException("Failed to intialize " + entity.getClassName());
                        }
                    }
                    else
                    {
                        LOGGER.warn("(FAILED) Locating Constructor for {}", entity.getClassName());
                    }
                }
                else
                {
                    LOGGER.warn("Entity's Classname is Null");
                }
            }
            else
            {
                LOGGER.warn("Entity Argument is Null");
            }
        }
        catch (InvocationTargetException | InstantiationException | IllegalAccessException e)
        {
            throw new IllegalArgumentException("createAndLoad", e);
        }

        return result;

    }

    /*--------------------------------------------------------------------------
    Private Methods
    --------------------------------------------------------------------------*/

    /**
     * Attempts to retrieve the constructor for the provided classname. If it
     * not found it will attempt to load it from the class path. If its still
     * not found it will use the default class if specified.
     *
     * @param classname of the entity
     * @return the entity's constructor
     */
    private static Constructor<? extends CoalesceEntity> getConstructor(String classname)
    {
        // Contain Class
        if (!map.containsKey(classname))
        {
            try
            {
                // No; Attempt Class Path
                Class<? extends CoalesceEntity> clazz = (Class<? extends CoalesceEntity>) Class.forName(classname);

                Constructor<? extends CoalesceEntity> constructor = clazz.getConstructor();

                map.put(classname, constructor);
            }
            catch (ClassNotFoundException | NoSuchMethodException | SecurityException | ClassCastException e)
            {
                LOGGER.warn("(FAILED) Locating {} Constructor", classname);

                // Default Defined?
                if (map.containsKey(DEFAULT_KEY))
                {
                    classname = DEFAULT_KEY;
                }
                else
                {
                    throw new IllegalArgumentException("Object Not Registered (" + classname + ")", e);
                }
            }

        }

        return map.get(classname);
    }

}
