/*
 * bg_geolocate.java
 * GEOLocate web service connector.  Uses JWSDP 1.6
 * Created on June 22, 2006, 11:00 AM
 */

package edu.tulane;

import edu.tulane.geolocate.*;
import org.biogeomancer.records.*;
import org.biogeomancer.records.Georef;

/*
 * @author Nelson E. Rios
 */

public class bg_geolocate {
      
    public class bg_geolocate_Exception extends Exception 
      {
	public bg_geolocate_Exception(Exception e) 
        {
	    //System.out.println(e.getMessage());
            super(e.toString());
	    e.printStackTrace();
	}
    }

    public void doParsing(RecSet rs, String LocalityKey, String GeographyKey, String CountryKey, String StateKey, String CountyKey) throws bg_geolocate_Exception 
    {
    	for(int i=0;i<rs.recs.size();i++)
	    doParsing((Rec)rs.recs.get(i),LocalityKey,GeographyKey, CountryKey, StateKey, CountyKey);
    }
    
    public void doParsing(Rec r, String LocalityKey, String GeographyKey, String CountryKey, String StateKey, String CountyKey) throws bg_geolocate_Exception 
    {
	String VerbatimLocality = r.get(LocalityKey);
        String Geography = r.get(GeographyKey);
        String Country = r.get(CountryKey);
        String State = r.get(StateKey);
        String County = r.get(CountyKey);
        
	if (VerbatimLocality == null) 
        {
	    return;
	}
        
        if ((Country != null)  && (State != null))
        {
            if (County == null)
            {
                County = "";
            }
            try 
            {			
                Geolocatesvc_Impl impl = new Geolocatesvc_Impl();
                GeolocatesvcSoap sp = impl.getGeolocatesvcSoap();
        
                Georef_Result_Set rs = sp.georef2(Country, State, County, VerbatimLocality, true,  true);
                Georef_Result[] r2 = rs.getResultSet();  
        
              if (r2.length > 0)
                {
                    for (int i = 0; i<r2.length; i++)
                    {
                       float lat = (float)r2[i].getWGS84Coordinate().getLatitude();
                        float lon = (float)r2[i].getWGS84Coordinate().getLongitude();
                        String interpreted_string = r2[i].getParsePattern();
                        
                        org.biogeomancer.records.Georef g = new org.biogeomancer.records.Georef(lon,lat, 0);
                        g.iLocality = interpreted_string;
                        r.georefs.add(g);  
                    } 
                }
            }
            catch (Exception e) 
            {
                throw this.new bg_geolocate_Exception(e);
            }
        }
        else if (Geography != null)
        {
             try 
             {			
                Geolocatesvc_Impl impl = new Geolocatesvc_Impl();
                GeolocatesvcSoap sp = impl.getGeolocatesvcSoap();

                Georef_Result_Set rs = sp.georef3(VerbatimLocality, Geography, true,true);
                Georef_Result[] r2 = rs.getResultSet();  

                if (r2.length > 0)
                {
                    for (int i = 0; i<r2.length; i++)
                    {
                        float lat = (float)r2[i].getWGS84Coordinate().getLatitude();
                        float lon = (float)r2[i].getWGS84Coordinate().getLongitude();
                        String interpreted_string = r2[i].getParsePattern();
                        org.biogeomancer.records.Georef g = new org.biogeomancer.records.Georef(lon,lat, 0);
                        g.iLocality = interpreted_string;
                        r.georefs.add(g);  
                    } 
                }
             }
             catch (Exception e) 
             {
                throw this.new bg_geolocate_Exception(e);
             }   
        }
	
    }
}
