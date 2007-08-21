package edu.berkeley.biogeomancer.webservice.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.managers.GeorefManager.GeorefManagerException;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;

/**
 * Utility class for wrapping BioGeomancer Core API.
 */
public class BgUtil {

  /**
   * Georeferences the list of recs using BioGeomancer Core API.
   * 
   * @param recs the list of Rec objects to georeference
   * @return the georeferences
   */
  public static List<Georef> georeference(List<Rec> recs) {
    // TODO: tri
    return null;
  }

  /**
   * Returns a list of Georef objects generated using BioGeomancer Core API. If
   * there is an error or if no georeferences were generated, returns null.
   * 
   * @param locality the locality to georeference
   * @param higherGeography the higher geography to georeference
   * @param interpreter the BioGeomancer locality intepreter to use
   * @return List<Georef> the generated georeferences
   */
  public static List<Georef> georeference(String locality,
      String higherGeography, String interpreter) {

    // Default interpreter is Yale.
    if (interpreter.equals("") || interpreter == null) {
      interpreter = "yale";
    }

    final Rec rec = new Rec();
    rec.put("locality", locality);
    rec.put("highergeography", higherGeography);
    GeorefManager gm;
    try {
      gm = new GeorefManager();
      gm.georeference(rec, new GeorefPreferences(interpreter));
      return rec.georefs;
    } catch (GeorefManagerException e) {
      e.printStackTrace();
      return null;
    }
  }

  private final Logger log = Logger.getLogger(BgUtil.class);
}
