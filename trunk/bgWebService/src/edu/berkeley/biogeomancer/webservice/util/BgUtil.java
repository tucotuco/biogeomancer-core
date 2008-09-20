package edu.berkeley.biogeomancer.webservice.util;

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

  public static void buildSingleXmlText(Rec r, String interpreter,
      boolean showheader, PrintWriter out) {
    List<Georef> georefs = georeference(r, interpreter);
    out.println("<interpreter>" + interpreter + "</interpreter>");
    if (r.get("id") != null)
      out.println("<id>" + r.get("id") + "</id>");
    if (showheader) {
      if (r.get("highergeography") != null)
        out.println("<dwcore:HigherGeography>" + r.get("highergeography")
            + "</dwcore:HigherGeography>");
      if (r.get("continent") != null) {
        out.println("<dwcore:Continent>" + r.get("continent")
            + "</dwcore:Continent>");
      }
      if (r.get("waterBody") != null) {
        out.println("<dwcore:WaterBody>" + r.get("waterbody")
            + "</dwcore:WaterBody>");
      }
      if (r.get("islandGroup") != null) {
        out.println("<dwcore:IslandGroup>" + r.get("islandgroup")
            + "</dwcore:IslandGroup>");
      }
      if (r.get("island") != null) {
        out.println("<dwcore:Island>" + r.get("island") + "</dwcore:Island>");
      }
      if (r.get("country") != null) {
        out
            .println("<dwcore:Country>" + r.get("country")
                + "</dwcore:Country>");
      }
      if (r.get("stateProvince") != null) {
        out.println("<dwcore:StateProvince>" + r.get("stateprovince")
            + "</dwcore:StateProvince>");
      }
      if (r.get("county") != null) {
        out.println("<dwcore:County>" + r.get("county") + "</dwcore:County>");
      }
      if (r.get("locality") != null) {
        out.println("<dwcore:Locality>" + r.get("locality")
            + "</dwcore:Locality>");
      }
      if (r.get("verbatimLatitude") != null) {
        out.println("<dwgeo:VerbatimLatitude>" + r.get("verbatimlaitude")
            + "</dwgeo:VerbatimLatitude>");
      }
      if (r.get("verbatimLongitude") != null) {
        out.println("<dwcore:VerbatimLongitude>" + r.get("verbatimlongitude")
            + "</dwgeo:VerbatimLongitude>");
      }
    }
    for (Georef g : georefs) {
      out.println("<georeference>");
      out.println("<interpretedLocality>" + g.iLocality
          + "</interpretedLocality>");
      out.println("<dwgeo:DecimalLatitude>" + g.pointRadius.y
          + "</dwgeo:DecimalLatitude>");
      out.println("<dwgeo:DecimalLongitude>" + g.pointRadius.x
          + "</dwgeo:DecimalLongitude>");
      out.println("<dwgeo:GeodeticDatum>WGS84</dwgeo:GeodeticDatum>");
      out.println("<dwgeo:CoordinateUncertaintyInMeters>"
          + g.pointRadius.extent + "</dwgeo:CoordinateUncertaintyInMeters>");
      out.println("</georeference>");
    }
  }

  public static List<Georef> georeference(Rec r, String interpreter) {
    // Default interpreter is "yale"
    if (interpreter == null)
      interpreter = "yale";
    else {
      if (interpreter.toLowerCase().equals("uiuc") == false
          && interpreter.toLowerCase().equals("tulane") == false) {
        interpreter = "yale";
      }
    }

    GeorefManager gm;
    try {
      gm = new GeorefManager();
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
   * @param recs
   *          the list of Rec objects to georeference
   * @return the georeferences
   */
  public static List<Georef> georeference(String FileName, String interpreter) {
    // TODO: tri
    // take FileName argument and interperter name
    // get RecSet from the file and return Georef List
    // has not been tested yet
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
   * @param data
   *          write string of data to a file
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
