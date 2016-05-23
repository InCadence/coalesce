package com.incadencecorp.coalesce.framework.persistance.accumulo;

import java.util.ArrayList;
import java.util.List;

import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;

public class MutationWrapper {

	private Mutation mutation;
	
	List <MutationRow> mutationGuyList;

	public MutationWrapper(Mutation mutation){
		this.mutation = mutation;
		mutationGuyList = new ArrayList<MutationRow>();
	}
	
	public Mutation getMutation() {
		return mutation;
	}


	public void addRow(MutationRow row){
		mutationGuyList.add(row);
		mutation.put(row.getColumnFamily(), row.getColumnQualifier(), row.getValue());
	}
	
	public List <MutationRow> getMutationGuyList(){
		return mutationGuyList;
	}
	
}
