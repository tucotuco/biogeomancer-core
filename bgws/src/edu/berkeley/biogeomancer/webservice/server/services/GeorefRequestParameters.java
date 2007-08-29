/**
 * 
 */
package edu.berkeley.biogeomancer.webservice.server.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * The GeorefRequestParameters class maps URL parameters to DwC concepts. For
 * example, the 'l' and 'locality' URL parameters map to the DwC Locality
 * concept:
 * 
 * <pre>
 * conceptMap.put('l', 'locality'); 
 * conceptMap.put('locality', 'locality');
 * </pre>
 * 
 */
public class GeorefRequestParameters {

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
  private static List<String> conceptList;

  /**
   * Returns a
   * <code>List<String><code> of concept names that are accepted in a request.
   * @return <code>List<String><code> of concept names that are accepted in a request
   */
  public static List<String> getConceptList() {
    if (conceptList == null) {
      conceptList = new LinkedList<String>();
      for (String c : new String[] { CONTINENT, COUNTRY, COUNTY,
              HIGHER_GEOGRAPHY, ISLAND, ISLAND_GROUP, LOCALITY, STATE_PROVINCE,
              VERBATIM_LATITUDE, VERBATIM_LONGITUDE, WATER_BODY }) {
        conceptList.add(c);
      }
    }
    return conceptList;
  }

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

      conceptMap.put("sp", STATE_PROVINCE);
      conceptMap.put("stateprovince", STATE_PROVINCE);

      conceptMap.put("co", COUNTY);
      conceptMap.put("county", COUNTY);

      conceptMap.put("vt", VERBATIM_LATITUDE);
      conceptMap.put("verbatimlatitude", VERBATIM_LATITUDE);

      conceptMap.put("vg", VERBATIM_LONGITUDE);
      conceptMap.put("verbatimlongitude", VERBATIM_LONGITUDE);

      conceptMap.put("is", ISLAND);
      conceptMap.put("island", ISLAND);

      conceptMap.put("ig", ISLAND_GROUP);
      conceptMap.put("islandgroup", ISLAND_GROUP);

      conceptMap.put("wb", WATER_BODY);
      conceptMap.put("waterbody", WATER_BODY);

      conceptMap.put("c", CONTINENT);
      conceptMap.put("continent", CONTINENT);
    }

    return conceptMap;
  }

}
