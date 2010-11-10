package edu.berkeley.biogeomancer.webservice.util;

import org.apache.log4j.Logger;
import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.managers.GeorefManager.GeorefManagerException;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;
import org.biogeomancer.records.RecSet.RecSetException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for wrapping BioGeomancer Core API.
 */
public class BgUtil {

  private static Logger log = Logger.getLogger(BgUtil.class);

  public static void buildSingleXmlText(Rec r, String interpreter,
      boolean showheader, PrintWriter out) {
    List<Georef> georefs = georeference(r, interpreter);
    out.println("<interpreter>" + interpreter + "</interpreter>");
    if (r.get("id") != null) {
      out.println("<id>" + r.get("id") + "</id>");
    }
    if (showheader) {
      if (r.get("highergeography") != null) {
        out.println("<dwc:higherGeography>" + r.get("highergeography")
            + "</dwc:higherGeography>");
      }
      if (r.get("continent") != null) {
        out.println("<dwc:continent>" + r.get("continent") + "</dwc:continent>");
      }
      if (r.get("waterbody") != null) {
        out.println("<dwc:waterBody>" + r.get("waterbody") + "</dwc:waterBody>");
      }
      if (r.get("islandGroup") != null) {
        out.println("<dwc:islandGroup>" + r.get("islandgroup")
            + "</dwc:islandGroup>");
      }
      if (r.get("island") != null) {
        out.println("<dwc:island>" + r.get("island") + "</dwc:island>");
      }
      if (r.get("country") != null) {
        out.println("<dwc:country>" + r.get("country") + "</dwc:country>");
      }
      if (r.get("countrycode") != null) {
        out.println("<dwc:countryCode>" + r.get("countrycode")
            + "</dwc:countryCode>");
      }
      if (r.get("stateProvince") != null) {
        out.println("<dwc:stateProvince>" + r.get("stateprovince")
            + "</dwc:stateProvince>");
      }
      if (r.get("county") != null) {
        out.println("<dwc:county>" + r.get("county") + "</dwc:county>");
      }
      if (r.get("locality") != null) {
        out.println("<dwc:locality>" + r.get("locality") + "</dwc:locality>");
      }
      if (r.get("verbatimLatitude") != null) {
        out.println("<dwc:verbatimLatitude>" + r.get("verbatimlaitude")
            + "</dwc:verbatimLatitude>");
      }
      if (r.get("verbatimLongitude") != null) {
        out.println("<dwc:verbatimLongitude>" + r.get("verbatimlongitude")
            + "</dwc:verbatimLongitude>");
      }
    }
    for (Georef g : georefs) {
      out.println("<georeference>");
      out.println("<interpretedLocality>" + g.iLocality
          + "</interpretedLocality>");
      out.println("<dwc:decimalLatitude>" + g.pointRadius.y
          + "</dwc:decimalLatitude>");
      out.println("<dwc:decimalLongitude>" + g.pointRadius.x
          + "</dwc:decimalLongitude>");
      out.println("<dwc:geodeticDatum>WGS84</dwc:geodeticDatum>");
      out.println("<dwc:coordinateUncertaintyInMeters>" + g.pointRadius.extent
          + "</dwc:coordinateUncertaintyInMeters>");
      out.println("</georeference>");
    }
  }

  public static List<Georef> georeference(Rec r, String interpreter) {
    // Default interpreter is "yale"
    if (interpreter == null) {
      interpreter = "yale";
    } else {
      if (interpreter.toLowerCase().equals("uiuc") == false
          && interpreter.toLowerCase().equals("tulane") == false) {
        interpreter = "yale";
      }
    }

    GeorefManager gm;
    try {
      gm = new GeorefManager(true);
      gm.georeference(r, new GeorefPreferences(interpreter));
      return r.georefs;
    } catch (GeorefManagerException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Georeferences the list of recs using BioGeomancer Core API.
   * 
   * @param recs the list of Rec objects to georeference
   * @return the georeferences
   */
  public static List<Georef> georeference(String FileName, String interpreter) {
    // TODO: tri
    // take FileName argument and interpreter name
    // get RecSet from the file and return Georef List
    // has not been tested yet
    try {
      RecSet referenceSet = new RecSet(FileName, "\t");
      // Iterator<Rec> recIter = referenceSet.recs.iterator();
      List<Georef> recsList = new ArrayList<Georef>();
      for (Iterator<Rec> recIter = referenceSet.recs.iterator(); recIter.hasNext();) {
        Rec currentRec = recIter.next();
        GeorefManager gm;
        try {
          gm = new GeorefManager(true);
          System.out.println(currentRec.toString());
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
