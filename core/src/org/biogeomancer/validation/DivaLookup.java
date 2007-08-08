/* 
 * This file is part of BioGeomancer.
 *
 *  Biogeomancer is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  any later version.
 *
 *  Biogeomancer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Biogeomancer; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.biogeomancer.validation;

import java.util.*;

import org.gbif.datatester.tests.ExternalGeospatialRasterReader;
import org.gbif.datatester.tests.ExternalGeospatialVectorReader;
import org.gbif.datatester.tests.ExternalProbabilityTest;
import org.gbif.datatester.tests.ValueRange;
import org.gbif.datatester.exception.TestProcessingException;
import org.gbif.datatester.DataType;


/**
 * <p>Class used by {@link ElevationErrorTest}, {@link GeographicErrorTest} and 
 * {@link ProbabilityProxyTest} to read data from geospatial raster and vector files.</p>
 * 
 * @author Alexandre Marino ( marino at cria . org . br )
 *         Renato De Giovanni ( renato at cria . org . br )
 * @version $Revision: 1.0 $
 * 
 */
public class DivaLookup implements ExternalGeospatialRasterReader, ExternalGeospatialVectorReader, ExternalProbabilityTest  {

private SpatialAttributeLookup sal;

/**
 * <p>Default constructor.</p>
 * 
 */
    public DivaLookup( ) {

        this.sal = new SpatialAttributeLookup();
    }

/**
 * <p>Returns a cell value given a point and an attribute (raster identification).</p>
 * <p>Method necessary for the ExternalGeospatialRasterReader interface.</p> 
 * 
 * 
 * @param longitude Longitude in decimal degrees (datum WGS84).
 * @param latitude Latitude in decimal degrees (datum WGS84).
 * @param attributeId Raster identifier (typically a file name).
 * @return Raster cell value.
 *
 * @throws TestProcessingException
 */
    public Double getValue( Double longitude, Double latitude, String attributeId ) throws TestProcessingException { 

        String resp = this.sal.lookup( longitude.toString(), latitude.toString(), attributeId );
        if ( resp == null ) {

            return null;
        }
        else {

            try { 

                return new Double( resp );
            }
            catch ( NumberFormatException exception ) {

                return null;
            }
        }
    }

/**
 * <p>Returns a value range given a point, an uncertainty, and an attribute (raster 
 * identification).</p>
 * <p>Method necessary for the ExternalGeospatialRasterReader interface.</p> 
 * 
 * 
 * @param longitude Longitude in decimal degrees (datum WGS84).
 * @param latitude Latitude in decimal degrees (datum WGS84).
 * @param uncertainty Uncertainty value.
 * @param attributeId Raster identifier (typically a file name).
 * @return Value range object.
 *
 * @throws TestProcessingException
 */
    public ValueRange getValueRange( Double longitude, Double latitude, Double uncertainty, String attributeId ) throws TestProcessingException {

       // For now just get the point value and use it for min and max too
       Double value = this.getValue( longitude, latitude, attributeId );

       return new ValueRange( value, value, value );
    }

/**
 * <p>Returns a feature value given a point and an attribute.</p>
 * <p>Method necessary for the ExternalGeospatialVectorReader interface.</p> 
 * 
 * 
 * @param longitude Longitude in decimal degrees (datum WGS84).
 * @param latitude Latitude in decimal degrees (datum WGS84).
 * @param attributeId Vector attribute identifier (like population, country name, etc).
 * @return Feature value.
 *
 * @throws TestProcessingException
 */
    public String getFeature( Double longitude, Double latitude, String attributeId ) throws TestProcessingException {

        return this.sal.lookup( longitude.toString(), latitude.toString(), attributeId );
    }


/**
 * <p>Returns the distance between x,y point and a polygon associated with an attribute name/value.</p>
 * 
 * 
 * @param longitude Longitude in decimal degrees (datum WGS84).
 * @param latitude Latitude in decimal degrees (datum WGS84).
 * @param attributeId Vector attribute identifier (like population, country name, etc).
 * @param value Value the attribute identifier (like "Brazil", "Madagascar", "Bahia", etc).
 * @return kilometers. 0 if x,y in polygon associated with attribute name/value, -1 if error
 * or if polygon not found, else the distance in kilometers.
 *
 * @throws TestProcessingException
 */
    public Double getDistance( Double longitude, Double latitude, String attributeId, String value ) throws TestProcessingException {

        return this.sal.distance( longitude.toString(), latitude.toString(), attributeId, value );
    }

 
/**
 * <p>Method necessary for the ExternalProbabilityTest interface. Specifies the data type 
 * for parameters longitude (Double), latitude (Double) and scientific name (string).</p>
 * 
 * 
 * @return array of data types for longitude, latitude, scientific name.
 */
    public DataType[] getParametersDataTypes() {

       return new DataType[] { DataType.DOUBLE, DataType.DOUBLE, DataType.STRING };
    }

/**
 * <p>Method necessary for the ExternalProbabilityTest interface. Returns a probability 
 * value given a point (longitude / latitude) and a species name.</p>
 * 
 * 
 * @param parameters List with parameter values (long, lat and scientific name).
 * @return Probability value between 0 and 1, or null.
 *
 * @throws TestProcessingException
 */
    public Double getProbability( List parameters ) throws TestProcessingException {

         // Check if three parameters were passed
	 if ( parameters.size() != 3 ) {

             String msg = "Wrong parameters number (" + parameters.size() + ")! " + 
                          "External test requires 3 parameters (Longitude, Latitude and " +
                          "Scientific Name).";
             throw new TestProcessingException( msg );
         }

         Double longitude      = (Double)parameters.get( 0 );
         Double latitude       = (Double)parameters.get( 1 );
         String scientificName = (String)parameters.get( 2 );

        return this.getValue( longitude, latitude, scientificName );
    }
}
