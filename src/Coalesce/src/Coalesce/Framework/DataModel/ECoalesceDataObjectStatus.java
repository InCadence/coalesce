package Coalesce.Framework.DataModel;

import java.util.HashMap;
import java.util.Map;

public enum ECoalesceDataObjectStatus {
	ACTIVE (1, "Active"),
    DELETED(2, "Deleted"),
    UNKNOWN(1073741824, "Unknown");
	
	private int value;
	private String label;

    /**
     * A mapping between the integer code and its corresponding Status to facilitate lookup by code.
     */
    private static Map<Integer, ECoalesceDataObjectStatus> codeToStatusMapping;

    private ECoalesceDataObjectStatus(int code, String label){
        this.value = code;
        this.label = label;
    }
 
    public static ECoalesceDataObjectStatus getStatus(int code) {
        if (codeToStatusMapping == null) {
            initMapping();
        }
        return codeToStatusMapping.get(code);
    }
 
    private static void initMapping() {
        codeToStatusMapping = new HashMap<Integer, ECoalesceDataObjectStatus>();
        for (ECoalesceDataObjectStatus s : values()) {
            codeToStatusMapping.put(s.value, s);
        }
    }
 
	public static ECoalesceDataObjectStatus fromLabel(String value)
	{
		try {
			switch (value.toUpperCase()) {
			case "ACTIVE":
				return ECoalesceDataObjectStatus.ACTIVE;
			case "DELETED":
				return ECoalesceDataObjectStatus.DELETED;
			default:
				return ECoalesceDataObjectStatus.UNKNOWN;
			}

		} catch (Exception ex) {
			return ECoalesceDataObjectStatus.UNKNOWN;
		}
	}

    public int toValue() {
        return this.value;
    }
 
    public String toLabel() {
        return this.label;
    }
 
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CoalesceDataObjectStatus");
        sb.append("{code=").append(value);
        sb.append(", label='").append(label);
        sb.append('}');
        return sb.toString();
    }
 
    /*
    public static void main(String[] args) {
        System.out.println(ECoalesceDataObjectStatus.ACTIVE);
        System.out.println(ECoalesceDataObjectStatus.getStatus(1));
    }
    */

}
