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

public class ISO3166Country implements Serializable, Comparable<ISO3166Country> {

    // This module is coded based on guidance found in DoDM 5200.01-V2, February 24, 2012

    private static final long serialVersionUID = -1463057591228825948L;

    private String _alpha2;
    private String _alpha3;
    private String _name;
    
    public ISO3166Country() {
    }
    
    public ISO3166Country(String alpha2,
                          String alpha3,
                          String name) {
    	
    	if (alpha2 == null) throw new NullArgumentException("alpha2");
    	if (alpha3 == null) throw new NullArgumentException("alpha3");
    	if (name == null) throw new NullArgumentException("name");
    	
    	_alpha2 = alpha2;
    	_alpha3 = alpha3;
    	_name = name;
    }
    
    public static ISO3166Country withAlpha3EqualTo(String alpha3) {
    	ISO3166Country country = new ISO3166Country();
    	country.setAlpha3(alpha3);
    	return country;
    }
    
    public static ISO3166Country USA() {
    	return new ISO3166Country("US", "USA", "UNITED STATES");
    }

    @Override
    public boolean equals(Object other) {
    	if (!(other instanceof ISO3166Country)) return false;
    	
    	ISO3166Country otherCountry = (ISO3166Country)other;
    	
    	return getAlpha3().equals(otherCountry.getAlpha3());
    	
    }
	@Override
    public int compareTo(ISO3166Country other)
    {
		return getName().compareTo(other.getName());
    }
    
	public String getAlpha2() {
		return _alpha2;
	}
	
	public void setAlpha2(String alpha2) {
    	if (alpha2 == null) throw new NullArgumentException("alpha2");
		_alpha2 = alpha2;
	}
	
	public String getAlpha3() {
		return _alpha3;
	}
	
	public void setAlpha3(String alhpa3) {
    	if (alhpa3 == null) throw new NullArgumentException("alhpa3");
		_alpha3 = alhpa3;
	}
	
    public String getName() {
    	return _name;
    }
    
    public void setName(String name) {
    	if (name == null) throw new NullArgumentException("name");
    	_name = name;
    }
    
}
