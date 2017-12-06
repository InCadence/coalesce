package com.incadencecorp.coalesce.framework.persistance.accumulo;

import com.incadencecorp.coalesce.framework.datamodel.TestEntity;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MutationWrapperFactoryTest {

    @Test
    public void test() throws Exception
    {
        TestEntity entity = new TestEntity();
        entity.initialize();

        // System.out.println(entity.toXml());

        MutationWrapperFactory factory = new MutationWrapperFactory();
        assertNotNull(factory);

        MutationWrapper mutationGuy = factory.createMutationGuy(entity);
        assertNotNull(mutationGuy);

        /*
         * for(MutationRow row : mutationGuy.getMutationGuyList()){
         * 
         * System.out.print("ColumnFamily:"); System.out.print(row.getColumnFamily()); System.out.print(" | ");
         * System.out.print("ColumnQualifier:"); System.out.print(row.getColumnQualifier()); System.out.print(" | ");
         * System.out.print("NamePath:"); System.out.print(row.getNamePath()); System.out.print(" | ");
         * System.out.print("Value:"); System.out.println(row.getValue().toString());
         * 
         * }
         */
    }

}
