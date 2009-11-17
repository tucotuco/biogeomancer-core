/**
 * Copyright 2007 University of California at Berkeley.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.biogeomancer.validation;

import org.gbif.datatester.Concept;
import org.gbif.datatester.DataType;

/**
 * <p>
 * Utility class statically defining all concepts needed by the DataTester
 * framework. It avoids loading concepts from a configuration file and helps
 * other classes when they need to reference concepts.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class BGConcepts {

  /**
   * <p>
 * Concept for the index of the record in a record set.
 * </p>
   * 
   */
  public static final Concept RECORD_INDEX = new Concept("recordIndex",
      DataType.INTEGER);

  /**
   * <p>
 * Concept for the index of a georeference interpretation associated with a
 * record.
 * </p>
   * 
   */
  public static final Concept GEOREF_INDEX = new Concept("georefIndex",
      DataType.INTEGER);

  /**
   * <p>
 * Concept for global unique identifier of records.
 * </p>
   * 
   */
  public static final Concept ID = new Concept("id", DataType.STRING);

  /**
   * <p>
 * Concept for latitude.
 * </p>
   * 
   */
  public static final Concept LATITUDE = new Concept("Latitude",
      DataType.DOUBLE);

  /**
   * <p>
 * Concept for longitude.
 * </p>
   * 
   */
  public static final Concept LONGITUDE = new Concept("Longitude",
      DataType.DOUBLE);

  /**
   * <p>
 * Concept for coordinate uncertainty.
 * </p>
   * 
   */
  public static final Concept COORDINATE_UNCERTAINTY = new Concept(
      "CoordinateUncertainty", DataType.DOUBLE);

  /**
   * <p>
 * Concept for collector name.
 * </p>
   * 
   */
  public static final Concept COLLECTOR = new Concept("Collector",
      DataType.STRING);

  /**
   * <p>
 * Concept for earliest collecting date.
 * </p>
   * 
   */
  public static final Concept EARLIEST_COLLECTING_DATE = new Concept(
      "EarliestCollectingDate", DataType.STRING);

  /**
   * <p>
 * Concept for minimum elevation.
 * </p>
   * 
   */
  public static final Concept MINIMUM_ELEVATION = new Concept(
      "MinimumElevationInMeters", DataType.DOUBLE);

  /**
   * <p>
 * Concept for maximum elevation.
 * </p>
   * 
   */
  public static final Concept MAXIMUM_ELEVATION = new Concept(
      "MaximumElevationInMeters", DataType.DOUBLE);

  /**
   * <p>
 * Concept for scientific name.
 * </p>
   * 
   */
  public static final Concept SCIENTIFIC_NAME = new Concept("ScientificName",
      DataType.STRING);

  /**
   * <p>
 * Concept for country.
 * </p>
   * 
   */
  public static final Concept COUNTRY = new Concept("Country", DataType.STRING);

  /**
   * <p>
 * Concept for state/province.
 * </p>
   * 
   */
  public static final Concept STATE_PROVINCE = new Concept("StateProvince",
      DataType.STRING);

  /**
   * <p>
 * Concept for county.
 * </p>
   * 
   */
  public static final Concept COUNTY = new Concept("County", DataType.STRING);

}
