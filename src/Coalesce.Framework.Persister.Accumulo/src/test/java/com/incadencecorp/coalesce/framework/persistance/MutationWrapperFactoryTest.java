package com.incadencecorp.coalesce.framework.persistance;

import static org.junit.Assert.*;

import org.junit.Test;

import com.incadencecorp.coalesce.framework.persistance.accumulo.MutationWrapper;
import com.incadencecorp.coalesce.framework.persistance.accumulo.MutationWrapperFactory;
import com.incadencecorp.coalesce.framework.persistance.accumulo.MutationRow;

public class MutationWrapperFactoryTest {

	@Test
	public void test() {
		MissionEntity entity = new MissionEntity();
		entity.initialize();
		
		System.out.println(entity.toXml());
		
		MutationWrapperFactory factory = new MutationWrapperFactory(); 
		
		MutationWrapper mutationGuy= factory.createMutationGuy(entity, false);
		
		for(MutationRow row : mutationGuy.getMutationGuyList()){
			
			System.out.print("ColumnFamily:");
			System.out.print(row.getColumnFamily());
			System.out.print(" | ");
			System.out.print("ColumnQualifier:");
			System.out.print(row.getColumnQualifier());
			System.out.print(" | ");
			System.out.print("NamePath:");
			System.out.print(row.getNamePath());
			System.out.print(" | ");
			System.out.print("Value:");
			System.out.println(row.getValue().toString());

		}
		
	}

}
