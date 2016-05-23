package com.incadencecorp.coalesce.framework.persistance.accumulo;

import org.apache.accumulo.core.data.Value;

public class MutationRow {

	private String columnFamily;
	private String columnQualifier;
	private Value value;
	private String namePath;

	public MutationRow(String columnFamily, String columnQualifier, byte[] value, String namePath) {

		this.columnFamily = columnFamily;
		this.columnQualifier = columnQualifier;
		this.value = new Value(value);
		this.namePath = namePath;
	}

	public String getColumnFamily() {
		return columnFamily;
	}

	public void setColumnFamily(String columnFamily) {
		this.columnFamily = columnFamily;
	}

	public String getColumnQualifier() {
		return columnQualifier;
	}

	public void setColumnQualifier(String columnQualifier) {
		this.columnQualifier = columnQualifier;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public String getNamePath() {
		return namePath;
	}

	public void setNamePath(String namePath) {
		this.namePath = namePath;
	}
}
