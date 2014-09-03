package Coalesce.Framework.DataModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Coalesce.Common.Helpers.FileHelper;

public class XsdFieldCommon {

    private XsdFieldCommon()
    {

    }

    public static String CallGetBaseFilenameWithFullDirectoryPathForKey(String key,
                                                                        boolean createIfDoesNotExist) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Class<?>[] args = new Class[2];
        args[0] = String.class;
        args[1] = boolean.class;

        Method method = FileHelper.class.getDeclaredMethod("GetBaseFilenameWithFullDirectoryPathForKey", args);
        method.setAccessible(true);

        Object results = method.invoke(null, key, createIfDoesNotExist);

        return (String) results;
    }
}
