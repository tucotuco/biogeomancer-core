package edu.berkeley.biogeomancer.webservice.server.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.managers.GeorefManager.GeorefManagerException;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;
import org.biogeomancer.records.RecSet.RecSetException;

/**
 * Utility class for wrapping BioGeomancer Core API.
 */
public class BgUtil {

  private static Logger log = Logger.getLogger(BgUtil.class);

  /**
   * 
   * @param String locality
   * @param String higherGeography
   * @param String interpreter
   * @param String out create a list of Georefs from locality, higherGeography,
   *          and interpreter output xml tag and value for these georeferences
   *          in format: <dwc:Locality>LocalityName</dwc:Locality>
   *          <dwc:HigherGeography>HigherGeographyName</dwc:HigherGeography>
   *          <georeference> <dwc:DecimalLatitude>value</dwc:DecimalLatitude>
   *          <dwc:DecimalLongitude>value</dwc:DecimalLongitude>
   *          <dwc:CoordinateUncertaintyInMeters>value</dwc:CoordinateUncertaintyInMeters>
   *          </georeference> more georeferences here if there are more
   */
  public static void buildSingleXmlText(String locality,
      String higherGeography, String interpreter, PrintWriter out) {
    log.info("Locality: " + locality + " HigherGeography: " + higherGeography
        + " Interpreter: " + interpreter);
    out.println("<dwc:Locality>" + locality + "</dwc:Locality>");
    out.println("<dwc:HigherGeography>" + higherGeography
        + "</dwc:HigherGeography>");
    List<Georef> georefs = georeference(locality, higherGeography, interpreter);
    for (Georef g : georefs) {
      out.println("<georeference>");
      out.println("<dwc:DecimalLatitude>" + g.pointRadius.y
          + "</dwc:DecimalLatitude>");
      out.println("<dwc:DecimalLongitude>" + g.pointRadius.x
          + "</dwc:DecimalLongitude>");
      out.println("<dwc:CoordinateUncertaintyInMeters>" + g.pointRadius.extent
          + "</dwc:CoordinateUncertaintyInMeters>");
      out.println("</georeference>");
    }
  }
  /**
   * 
   * @param String locality
   * @param String higherGeography
   * @param String interpreter
   * @param StringBuilder response
   * generate single georeference and append it to response
   */
  public static void buildSingleGeoreference(String locality,
	      String higherGeography, String interpreter, StringBuilder response) {
	    log.info("Locality: " + locality + " HigherGeography: " + higherGeography
	        + " Interpreter: " + interpreter);
	    List<Georef> georefs = georeference(locality, higherGeography, interpreter);
	    response.append("Locality: " + locality + " HigherGeography: " + higherGeography
	            + " Interpreter: " + interpreter + "<BR>");
	    for (Georef g : georefs) {
		    response.append("Georeference: ");
		    response.append("DecimalLatitude=" + g.pointRadius.y + "<BR>");
		    response.append("DecimalLongitude=" + g.pointRadius.x + "<BR>");
		    response.append("CoordinateUncertaintyInMeters=" + g.pointRadius.extent+ "<P>");
	  }
  }

  /**
   * Georeferences the list of recs using BioGeomancer Core API.
   * 
   * @param recs the list of Rec objects to georeference
   * @return the georeferences
   */
  public static List<Georef> georeference(String FileName, String interpreter) {
    // take FileName argument and interperter name
    // get RecSet from the file and return Georef List
    // has not tested yet
    try {
      RecSet referenceSet = new RecSet(FileName, "\t");
      // Iterator<Rec> recIter = referenceSet.recs.iterator();
      List<Georef> recsList = new ArrayList<Georef>();
      for (Iterator<Rec> recIter = referenceSet.recs.iterator(); recIter
          .hasNext();) {
        Rec currentRec = recIter.next();
        GeorefManager gm;
        try {
          gm = new GeorefManager();
          gm.georeference(currentRec, new GeorefPreferences(interpreter));
          recsList.addAll(currentRec.georefs);
        } catch (GeorefManagerException e) {
          // TODO: Logging error to an error log
          e.printStackTrace();
        }

      }
      return recsList;
    } catch (RecSetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }

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
    if (interpreter == null || interpreter.equals("")) {
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

  /**
   * 
   * @param fileName
   * @param data write string of data to a file
   */
  public static void recordToFile(String fileName, String data) {
    try {
      BufferedWriter buff = new BufferedWriter(new FileWriter(fileName));
      buff.write(data);
      buff.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
