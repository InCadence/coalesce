package Coalesce.Common.Classification;

import java.io.IOException;
import java.io.ObjectInputStream;
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
    	
    	return GetName().equals(otherCountry.GetName());
    	
    }
	@Override
    public int compareTo(ISO3166Country other)
    {
		return GetName().compareTo(other.GetName());
    }
    
	public String GetAlpha2() {
		return _alpha2;
	}
	
	public void SetAlpha2(String value) {
    	if (value == null) throw new NullArgumentException("value");
		_alpha2 = value;
	}
	
	public String GetAlpha3() {
		return _alpha3;
	}
	
	public void SetAlpha3(String value) {
    	if (value == null) throw new NullArgumentException("value");
		_alpha3 = value;
	}
	
    public String GetName() {
    	return _name;
    }
    
    public void SetName(String value) {
    	if (value == null) throw new NullArgumentException("value");
    	_name = value;
    }
    
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
    	
    	inputStream.defaultReadObject();
    	
    	// TODO: need to finish processing
    }

}
