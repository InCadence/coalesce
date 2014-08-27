package Coalesce.Common.Classification;

import java.io.Serializable;
import org.apache.commons.lang.NullArgumentException;

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
    
    public static ISO3166Country WithAlpha3EqualTo(String alpha3) {
    	ISO3166Country country = new ISO3166Country();
    	country.SetAlpha3(alpha3);
    	return country;
    }
    
    public static ISO3166Country USA() {
    	return new ISO3166Country("US", "USA", "UNITED STATES");
    }

    @Override
    public boolean equals(Object other) {
    	if (!(other instanceof ISO3166Country)) return false;
    	
    	ISO3166Country otherCountry = (ISO3166Country)other;
    	
    	return GetAlpha3().equals(otherCountry.GetAlpha3());
    	
    }
	@Override
    public int compareTo(ISO3166Country other)
    {
		return GetName().compareTo(other.GetName());
    }
    
	public String GetAlpha2() {
		return _alpha2;
	}
	
	public void SetAlpha2(String alpha2) {
    	if (alpha2 == null) throw new NullArgumentException("alpha2");
		_alpha2 = alpha2;
	}
	
	public String GetAlpha3() {
		return _alpha3;
	}
	
	public void SetAlpha3(String alhpa3) {
    	if (alhpa3 == null) throw new NullArgumentException("alhpa3");
		_alpha3 = alhpa3;
	}
	
    public String GetName() {
    	return _name;
    }
    
    public void SetName(String name) {
    	if (name == null) throw new NullArgumentException("name");
    	_name = name;
    }
    
}
