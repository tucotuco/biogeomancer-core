/*
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
package edu.berkeley.biogeomancer.webservice.server.services;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.managers.GeorefManager.GeorefManagerException;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;

/**
 * The SingleGeorefServlet class provides an HTTP GET web service for
 * georeferencing single DwC records.
 * 
 * @see http://code.google.com/p/biogeomancer-core/wiki/WebServicesRequirements
 * 
 */
public class SingleGeorefServlet extends HttpServlet {

  /**
   * The default BioGeomancer locality interpreter.
   */
  private static String INTERPRETER = "yale";

  Logger log = Logger.getLogger(SingleGeorefServlet.class);

  /**
   * Handles the HTTP GET request.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, java.io.IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    sb.append("<biogeomancer xmlns:dwc=\"http://rs.tdwg.org/tapir/1.0\">");

    // Output the original DwC record.
    Rec rec = buildRecFromRequest(request);
    for (Entry<String, String> entry : rec.entrySet()) {
      sb.append("<dwc:" + entry.getKey() + ">");
      sb.append(entry.getValue());
      sb.append("</dwc:" + entry.getKey() + ">");
    }

    // Output the georeferences.
    List<Georef> georefs = georeference(rec, INTERPRETER);
    double lng, lat, extent;
    for (Georef g : georefs) {
      lat = g.pointRadius.y;
      lng = g.pointRadius.x;
      extent = g.pointRadius.extent;
      sb.append("<georeference>");
      sb.append("<dwc:DecimalLatitude>" + lat + "</dwc:DecimalLatitude>");
      sb.append("<dwc:DecimalLongitude>" + lng + "</dwc:DecimalLongitude>");
      sb.append("<dwc:CoordinateUncertaintyInMeters>" + extent
          + "</dwc:CoordinateUncertaintyInMeters>");
      sb.append("</georeference>");
    }

    sb.append("</biogeomancer>");

    // Send output to client.
    response.setContentType("text/xml");
    PrintWriter out = response.getWriter();
    out.print(sb.toString());
  }

  /**
   * Returns a new Rec built from the request URL parameter key/value pairs.
   * 
   * @param request the HTTP GET request
   * @return Rec
   */
  private Rec buildRecFromRequest(HttpServletRequest request) {
    Rec rec = new Rec();
    Map<String, String> conceptMap = URLParameters.getConceptMap();
    Map<String, String[]> urlParamVals = request.getParameterMap();

    // Adds valid URL parameter KVP to new Rec.
    String paramName = null, conceptName = null, conceptVal = null;
    Enumeration<String> urlParamNames = request.getParameterNames();
    while (urlParamNames.hasMoreElements()) {
      paramName = urlParamNames.nextElement();
      if (conceptMap.containsKey(paramName)) {
        conceptName = conceptMap.get(paramName);
        conceptVal = urlParamVals.get(paramName)[0];
        rec.put(conceptName.toLowerCase(), conceptVal);
      }
    }

    return rec;
  }

  /**
   * Georeferences the Rec and returns a list of Georef objects.
   * 
   * @param rec the record to georeference
   * @param interpreter the locality interpreter
   * @return
   */
  private List<Georef> georeference(Rec rec, String interpreter) {
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
}
