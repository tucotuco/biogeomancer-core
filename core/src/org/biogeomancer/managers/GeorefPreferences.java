package org.biogeomancer.managers;

import org.biogeomancer.records.RecSet;

public class GeorefPreferences {
    public String locinterp;
    
    public GeorefPreferences() {
    }

    public GeorefPreferences(String interpprefs) {
    	locinterp = new String(interpprefs);
    }
    
}