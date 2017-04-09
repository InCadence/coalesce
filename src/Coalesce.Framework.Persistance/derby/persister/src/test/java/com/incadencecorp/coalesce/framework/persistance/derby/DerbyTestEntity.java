	/**
	 * ///-----------SECURITY CLASSIFICATION: UNCLASSIFIED------------------------
	 * /// Copyright 2014 - Lockheed Martin Corporation, All Rights Reserved /// ///
	 * Notwithstanding any contractor copyright notice, the government has ///
	 * Unlimited Rights in this work as defined by DFARS 252.227-7013 and ///
	 * 252.227-7014. Use of this work other than as specifically authorized by ///
	 * these DFARS Clauses may violate government rights in this work. /// /// DFARS
	 * Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16) /// Unlimited
	 * Rights. The Government has the right to use, modify, /// reproduce, perform,
	 * display, release or disclose this computer software /// in whole or in part,
	 * in any manner, and for any purpose whatsoever, /// and to have or authorize
	 * others to do so. /// /// Distribution Statement D. Distribution authorized to
	 * the Department of /// Defense and U.S. DoD contractors only in support of US
	 * DoD efforts. /// Other requests shall be referred to the ACINT Modernization
	 * Program /// Management under the Director of the Office of Naval
	 * Intelligence. ///
	 * -------------------------------UNCLASSIFIED---------------------------------
	 */
package com.incadencecorp.coalesce.framework.persistance.derby;

import com.incadencecorp.coalesce.framework.datamodel.CoalesceEntity;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceRecordset;
import com.incadencecorp.coalesce.framework.datamodel.CoalesceSection;
import com.incadencecorp.coalesce.framework.datamodel.TestRecord;

/**
 * This class is a Coalesce Entity that contains recordsets with every data type
 * to be used during unit testing.
 * 
 * @author mdaconta
 */
public class DerbyTestEntity extends CoalesceEntity {

	    public static final String NAME = "DERBY_TEST";
	    public static final String SOURCE = "JUNIT";
	    public static final String VERSION = "1.0";

	    public static final String RECORDSET1 = "test1";
	    public static final String RECORDSET2 = "test2";
	    public static final String TESTSECTION = "test section";

	    private CoalesceRecordset recordset1;

	    /**
	     * Default Constructor
	     */
	    public DerbyTestEntity()
	    {
	        // Do Nothing
	    }

	    @Override
	    public boolean initialize()
	    {
	        boolean isInitialized = false;
	        if (initializeEntity(NAME, SOURCE, VERSION, "", "", ""))
	        {
	            isInitialized = initializeReferences();
	        }
	        return isInitialized;
	    }

	    @Override
	    protected boolean initializeEntity(String name,
	                                       String source,
	                                       String version,
	                                       String entityId,
	                                       String entityIdType,
	                                       String title)
	    {
	        boolean isInitialized = false;

	        if (super.initializeEntity(name, source, version, entityId, entityIdType, title))
	        {

	            // Create Section
	            CoalesceSection section = CoalesceSection.create(this, TESTSECTION);

	            // Create Record Sets
	            recordset1 = DerbyTestRecord.createCoalesceRecordset(section, RECORDSET1);

	            isInitialized = true;
	        }

	        return isInitialized;
	    }

	    @Override
	    protected boolean initializeReferences()
	    {
	        if (super.initializeReferences())
	        {
	            if (recordset1 == null)
	            {
	                recordset1 = (CoalesceRecordset) getCoalesceObjectForNamePath(getName(), TESTSECTION, RECORDSET1);
	            }
	        }
	        
	        return recordset1 != null;
	    }

	    /**
	     * @return the test1 record set name
	     */
	    public static String getTest1RecordsetName()
	    {
	        return RECORDSET1;
	    }

	    /**
	     * @return the test2 record set name
	     */
	    public static String getTest2RecordsetName()
	    {
	        return RECORDSET2;
	    }

	    public CoalesceRecordset getRecordset1()
	    {
	        return recordset1;
	    }

	    public DerbyTestRecord addRecord1()
	    {
	        return new DerbyTestRecord(recordset1.addNew());
	    }
}
