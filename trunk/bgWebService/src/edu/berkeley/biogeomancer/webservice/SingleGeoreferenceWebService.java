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
package edu.berkeley.biogeomancer.webservice;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.managers.GeorefManager.GeorefManagerException;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;

public class SingleGeoreferenceWebService extends HttpServlet {

  /**
   * Georeferences a single locality using the BioGeomancer Core API. Returns
   * all generated georeferences as XML.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, java.io.IOException {
    response.setContentType("text/xml");
    String locality = request.getParameter("l");
    String higherGeography = request.getParameter("hg");
    String interpreter = request.getParameter("i");

    PrintWriter out = response.getWriter();

    // Build the XML header.
    out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    out.println("<biogeomancer xmlns=\"http://bg.berkeley.edu\" "
        + "xmlns:dwc=\"http://rs.tdwg.org/tapir/1.0\">");

    out.println("<interpreter>" + interpreter + "</interpreter>");
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
    out.println("</biogeomancer>");
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
  private List<Georef> georeference(String locality, String higherGeography,
      String interpreter) {

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
}

/*
 * 
 * <biogeomancer> <request type="batch" interpreter="Yale"> <records> <record>
 * <dwc:Locality>Berkeley</dwc:Locality> <dwc:HigherGeography>California</dwc:HigherGeography>
 * </record> <record> <dwc:Locality>Stuttgart</dwc:Locality>
 * <dwc:HigherGeography>Germany</dwc:HigherGeography> </record> <record>
 * <dwc:Locality>St. Petersburg</dwc:Locality> <dwc:HigherGeography>Russia</dwc:HigherGeography>
 * </record> </request> </biogeomancer>
 * 
 * 
 * 
 * 
 */