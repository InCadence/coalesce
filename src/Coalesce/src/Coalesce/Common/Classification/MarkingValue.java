package Coalesce.Common.Classification;

import java.io.Serializable;

import org.apache.commons.lang.NullArgumentException;

public class MarkingValue implements Serializable {

    private static final long serialVersionUID = 4042377574862929570L;

    private String _parent = "";
    private String _title = "";
    private String _abbreviation = "";
    private String _portion = "";
    
    public MarkingValue() {
    }
    
    public MarkingValue(String parent,
                        String title,
                        String abbreviation,
                        String portion) {
        
        if (parent == null) throw new NullArgumentException("parent");
        if (title == null) throw new NullArgumentException("title");
        if (abbreviation == null) throw new NullArgumentException("abbreviation");
        if (portion == null) throw new NullArgumentException("portion");

        _parent = parent;
        _title = title;
        _abbreviation = abbreviation;
        _portion = portion;
    }
    
    public String GetParent() {
        return _parent;
    }
    
    public void SetParent(String parent) {
        if (parent == null) throw new NullArgumentException("parent");
        _parent = parent;
    }
    
    public String GetTitle() {
        return _title;
    }
    
    public void SetTitle(String title) {
        if (title == null) throw new NullArgumentException("title");
        _title = title;
    }
    
    public String GetAbbreviation() {
        return _abbreviation;
    }
    
    public void SetAbbreviation(String abbreviation) {
        if (abbreviation == null) throw new NullArgumentException("abbreviation");
        _abbreviation = abbreviation;
    }
    
    public String GetPortion() {
        return _portion;
    }
    
    public void SetPortion(String portion) {
        if (portion == null) throw new NullArgumentException("portion");
        _portion = portion;
    }
}
