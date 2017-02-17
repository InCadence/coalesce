package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

/**
 * 
 * Wraps a FeatureIterator to iterate over columns
 * 
 * @author Matthew Defazio
 *
 * @param <T>
 */
public class FeatureColumnIterator<T> implements Iterator<T> {

    FeatureIterator<?> featureIterator;
    
    public FeatureColumnIterator(FeatureIterator<?> featureIterator){
        this.featureIterator = featureIterator;
    }
    
    @Override
    public boolean hasNext()
    {
        return featureIterator.hasNext();
    }

    @Override
    public T next()
    {
       Feature feature = featureIterator.next();
        
       Collection<Property> properties = feature.getProperties();
       
       List<Object> valuesList= new ArrayList<>();
       
       for(Property prop:properties){
           
           valuesList.add(prop.getValue());
           
       }
       
        return (T) valuesList.toArray(new Object[0]);
    }

}
