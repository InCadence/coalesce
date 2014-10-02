package Coalesce.Common.Classification;

import java.io.Serializable;

import org.apache.commons.lang.NullArgumentException;

/*-----------------------------------------------------------------------------'
 Copyright 2014 - InCadence Strategic Solutions Inc., All Rights Reserved

 Notwithstanding any contractor copyright notice, the Government has Unlimited
 Rights in this work as defined by DFARS 252.227-7013 and 252.227-7014.  Use
 of this work other than as specifically authorized by these DFARS Clauses may
 violate Government rights in this work.

 DFARS Clause reference: 252.227-7013 (a)(16) and 252.227-7014 (a)(16)
 Unlimited Rights. The Government has the right to use, modify, reproduce,
 perform, display, release or disclose this computer software and to have or
 authorize others to do so.

 Distribution Statement D. Distribution authorized to the Department of
 Defense and U.S. DoD contractors only in support of U.S. DoD efforts.
 -----------------------------------------------------------------------------*/

public class MarkingValue implements Serializable {

    private static final long serialVersionUID = 4042377574862929570L;

    private String _parent = "";
    private String _title = "";
    private String _abbreviation = "";
    private String _portion = "";

    public MarkingValue()
    {
    }

    public MarkingValue(String parent, String title, String abbreviation, String portion)
    {

        if (parent == null) throw new NullArgumentException("parent");
        if (title == null) throw new NullArgumentException("title");
        if (abbreviation == null) throw new NullArgumentException("abbreviation");
        if (portion == null) throw new NullArgumentException("portion");

        _parent = parent;
        _title = title;
        _abbreviation = abbreviation;
        _portion = portion;
    }

    public String getParent()
    {
        return _parent;
    }

    public void setParent(String parent)
    {
        if (parent == null) throw new NullArgumentException("parent");
        _parent = parent;
    }

    public String getTitle()
    {
        return _title;
    }

    public void setTitle(String title)
    {
        if (title == null) throw new NullArgumentException("title");
        _title = title;
    }

    public String getAbbreviation()
    {
        return _abbreviation;
    }

    public void setAbbreviation(String abbreviation)
    {
        if (abbreviation == null) throw new NullArgumentException("abbreviation");
        _abbreviation = abbreviation;
    }

    public String getPortion()
    {
        return _portion;
    }

    public void setPortion(String portion)
    {
        if (portion == null) throw new NullArgumentException("portion");
        _portion = portion;
    }
}
