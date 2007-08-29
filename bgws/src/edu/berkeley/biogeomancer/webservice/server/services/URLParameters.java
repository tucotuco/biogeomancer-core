/**
 * 
 */
package edu.berkeley.biogeomancer.webservice.server.services;

import java.util.HashMap;

/**
 * The URLParameters class maps URL parameters to DwC concepts. For example, the
 * 'l' and 'locality' URL parameters map to the DwC Locality concept:
 * 
 * <pre>
 * conceptMap.put('l', 'locality'); 
 * conceptMap.put('locality', 'locality');
 * </pre>
 * 
 */
public class URLParameters {

  public static final String CONTINENT = "Continent";
  public static final String COUNTRY = "Country";
  public static final String COUNTY = "County";
  public static final String HIGHER_GEOGRAPHY = "HigherGeography";
  public static final String ISLAND = "Island";
  public static final String ISLAND_GROUP = "IslandGroup";
  public static final String LOCALITY = "Locality";
  public static final String STATE_PROVINCE = "StateProvince";
  public static final String VERBATIM_LATITUDE = "VerbatimLatitude";
  public static final String VERBATIM_LONGITUDE = "VerbatimLongitude";
  public static final String WATER_BODY = "WaterBody";

  /**
   * Maps URL parameters to DwC concepts.
   */
  private static HashMap<String, String> conceptMap;

  /**
   * Returns the concept map.
   * 
   * @return
   */
  public static HashMap<String, String> getConceptMap() {
    if (conceptMap == null) {
      conceptMap = new HashMap<String, String>();

      conceptMap.put("l", LOCALITY);
      conceptMap.put("locality", LOCALITY);

      conceptMap.put("hg", HIGHER_GEOGRAPHY);
      conceptMap.put("highergeography", HIGHER_GEOGRAPHY);

      conceptMap.put("cy", COUNTRY);
      conceptMap.put("country", COUNTRY);

      conceptMap.put("s", STATE_PROVINCE);
      conceptMap.put("stateprovince", STATE_PROVINCE);

      conceptMap.put("co", COUNTY);
      conceptMap.put("county", COUNTY);

      conceptMap.put("vlat", VERBATIM_LATITUDE);
      conceptMap.put("verbatimlatitude", VERBATIM_LATITUDE);

      conceptMap.put("vlng", VERBATIM_LONGITUDE);
      conceptMap.put("verbatimlongitude", VERBATIM_LONGITUDE);

      conceptMap.put("is", ISLAND);
      conceptMap.put("island", ISLAND);

      conceptMap.put("ig", ISLAND_GROUP);
      conceptMap.put("islandgroup", ISLAND_GROUP);

      conceptMap.put("w", WATER_BODY);
      conceptMap.put("waterbody", WATER_BODY);

      conceptMap.put("c", CONTINENT);
      conceptMap.put("continent", CONTINENT);
    }

    return conceptMap;
  }

}
