package com.incadencecorp.coalesce.framework.persistance.accumulo;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;


/**
 * Walks through a Filter, re-writing any property names removing the tablename from the property along with the /
 * @deprecated
 * @see AccumuloQueryRewriter2
 */
class AccumuloQueryRewriter extends DuplicatingFilterVisitor {
    ArrayList<String> features = new ArrayList<>();
    Query origQuery = null;
    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
    
    /** Standard java logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloQueryRewriter.class);

    AccumuloQueryRewriter(Query query) {
        this.origQuery = query;
     }
    
 
    @Override
    public Object visit(PropertyName expression, Object extraData) {
    	// Split on either / or .  sometimes dots get rewritten to /
        String props[] = expression.getPropertyName().split("[/.]");
        PropertyName newName;
        
        // If there is no table name do nothing
        if (props.length == 1) return super.visit(expression, extraData);
        
        // If this feature name is not in the list add it.
        if (!features.contains(props[0])) {
        	features.add(props[0]);
        }
        
        newName = ff.property(props[1]);
        	
       

        return newName;
    }
    
   
    public Query rewrite() throws CoalescePersistorException {
        //create a new filter with the rewritten property names
          
          Query newQuery = new Query(origQuery);
          
          // See if a valid featurename was set in the query.  If
          // so make sure it is the first in the list
          if((newQuery.getTypeName() != null) && (!newQuery.getTypeName().equalsIgnoreCase("coalesce"))) {
        	  features.add(newQuery.getTypeName());
          }
          // Clear the type name from the query
          newQuery.setTypeName(null);
          
          Filter f = origQuery.getFilter();
          
          // Rewrite the filter
          newQuery.setFilter( (Filter)f.accept(this, null));
          
          
          // Rewrite properties also. Strip any table names off these
          
          if (newQuery.getPropertyNames() != null) {
        	  ArrayList<String> props = new ArrayList<>(Arrays.asList(newQuery.getPropertyNames()));
        	  for (int i=0;i<props.size();i++) {
  	        	String[] nameParts = props.get(i).split("[/.]");
  	        	if (nameParts.length == 2) {
  	        		if (!features.contains(nameParts[0])) {
  	        			features.add(nameParts[0]);
  	        		}
  	        		props.set(i, nameParts[1]);
  	        	}
  	        }
  	       
  	        // Make sure objectkey is returned UNLESS the user specifically
  	        // specified no properties. - Which is done by an empty props array
        	// objectkey is always the first parameter
  	        if (props.size() > 0) {
  	        	props.add(0,AccumuloPersistor.ENTITY_KEY_COLUMN_NAME);
  	        }
  	        newQuery.setPropertyNames(props);
          } else {
        	  // Another hack to match Coalesce Behavior
        	  // Null for properties is supposed to mean all fields but
        	  // Coalesce expects the objectkey
        	  newQuery.setPropertyNames(new String[] {AccumuloPersistor.ENTITY_KEY_COLUMN_NAME});
          }
          
          // Rewrite the any sorts also
          SortBy[] sorts = newQuery.getSortBy();
  	      if (sorts != null) {
	  	        for (int i=0;i<sorts.length;i++) {
	  	        	String[] sortParts = sorts[i].getPropertyName().getPropertyName().split("[/.]");
	  	        	if (sortParts.length == 2) {
	  	        		if (!features.contains(sortParts[0])) {
	  	        			features.add(sortParts[0]);
	  	        		}
	  	        		sorts[i] = ff.sort(sortParts[1], sorts[i].getSortOrder());
	  	        	}
	  	        }
  	        
	  	        newQuery.setSortBy(sorts);
  	      }  
  	      
  	      // Now go through the features used and figure
  	      // out what is the key feature
  	      
  	      // If there are none use coalesceentity
  	      if (features.isEmpty()) {
  	    	  newQuery.setTypeName(AccumuloPersistor.ENTITY_FEATURE_NAME);
  	      } else if (features.size() == 1) {
  	    	  newQuery.setTypeName(features.get(0));
  	      } else {
  	      
	  	      // Remove ENTITY_FEATURE_NAME if it is also used.
	  	      Predicate<String> namePredicate = s->s.equalsIgnoreCase(AccumuloPersistor.ENTITY_FEATURE_NAME);
	  	      features.removeIf(namePredicate);
	  	      
	  	      // Now if there is more than one feature throw an exception
	  	      if (features.size() > 1) {
	  	    	  throw new CoalescePersistorException("Multiple featuretypes in query is not supported");
	  	      }
	  	      newQuery.setTypeName(features.get(0));
  	      }
  	      
          
          return newQuery;
      }
}
