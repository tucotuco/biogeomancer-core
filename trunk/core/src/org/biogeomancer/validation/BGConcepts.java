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

import org.gbif.datatester.*;

/**
 * <p>Utility class statically defining all concepts needed by the DataTester 
 * framework. It avoids loading concepts from a configuration file and helps
 * other classes when they need to reference concepts.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class BGConcepts {

/**
 * <p>Concept for the index of the record in a record set.</p>
 * 
 */
    public static final Concept RECORD_INDEX = new Concept( "recordIndex", DataType.INTEGER );

/**
 * <p>Concept for the index of a georeference interpretation associated with a record.</p>
 * 
 */
    public static final Concept GEOREF_INDEX = new Concept( "georefIndex", DataType.INTEGER );

/**
 * <p>Concept for global unique identifier of records.</p>
 * 
 */
    public static final Concept ID = new Concept( "id", DataType.STRING );

/**
 * <p>Concept for latitude.</p>
 * 
 */
    public static final Concept LATITUDE = new Concept( "Latitude", DataType.DOUBLE );

/**
 * <p>Concept for longitude.</p>
 * 
 */
    public static final Concept LONGITUDE = new Concept( "Longitude", DataType.DOUBLE );

/**
 * <p>Concept for coordinate uncertainty.</p>
 * 
 */
    public static final Concept COORDINATE_UNCERTAINTY = new Concept( "CoordinateUncertainty", DataType.DOUBLE );

/**
 * <p>Concept for collector name.</p>
 * 
 */
    public static final Concept COLLECTOR = new Concept( "Collector", DataType.STRING );

/**
 * <p>Concept for earliest collecting date.</p>
 * 
 */
    public static final Concept EARLIEST_COLLECTING_DATE = new Concept( "EarliestCollectingDate", DataType.STRING );

/**
 * <p>Concept for minimum elevation.</p>
 * 
 */
    public static final Concept MINIMUM_ELEVATION = new Concept( "MinimumElevationInMeters", DataType.DOUBLE );

/**
 * <p>Concept for maximum elevation.</p>
 * 
 */
    public static final Concept MAXIMUM_ELEVATION = new Concept( "MaximumElevationInMeters", DataType.DOUBLE );

/**
 * <p>Concept for scientific name.</p>
 * 
 */
    public static final Concept SCIENTIFIC_NAME = new Concept( "ScientificName", DataType.STRING );

/**
 * <p>Concept for country.</p>
 * 
 */
    public static final Concept COUNTRY = new Concept( "Country", DataType.STRING );

/**
 * <p>Concept for state/province.</p>
 * 
 */
    public static final Concept STATE_PROVINCE = new Concept( "StateProvince", DataType.STRING );

/**
 * <p>Concept for county.</p>
 * 
 */
    public static final Concept COUNTY = new Concept( "County", DataType.STRING );

 }
