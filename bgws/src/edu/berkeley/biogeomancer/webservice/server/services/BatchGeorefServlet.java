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

import edu.berkeley.biogeomancer.webservice.server.util.CustomNamespaceDocument;

import org.apache.log4j.Logger;
import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.managers.GeorefManager.GeorefManagerException;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The BatchGeorefServlet provides an HTTP POST web service for batch
 * georeferencing using the BioGeomancer Core API.
 * 
 * @see http://code.google.com/p/biogeomancer-core/wiki/WebServicesRequirements
 * 
 */
public class BatchGeorefServlet extends HttpServlet {

  Logger log = Logger.getLogger(BatchGeorefServlet.class);

  /**
   * Handler the HTTP POST request and returns an XML response.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String xml = request.getParameter("b");

    // Build response.
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    sb.append("<biogeomancer xmlns:dwc=\"http://rs.tdwg.org/tapir/1.0\">");
    sb.append(getGeoreferencedResponse(xml));
    sb.append("</biogeomancer>");

    // Return output to client.
    PrintWriter out = response.getWriter();
    out.print(sb.toString());
    out.flush();
    out.close();
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

  /**
   * Creates the <records> element for the XML response.
   * 
   * @param xml the batch XML request
   * @return the georeferenced XML records element
   */
  private String getGeoreferencedResponse(String xml) {
    SAXReader reader = new SAXReader();
    List<String> concepts = URLParameters.getConceptList();
    Rec rec;
    StringBuilder sb = new StringBuilder();

    try {
      // Parses the XML request.
      Document xmlDoc = reader.read(new StringReader(xml));
      CustomNamespaceDocument cnd = new CustomNamespaceDocument(xmlDoc);
      List<Node> recordNodes = cnd.getNodes("record");

      sb.append("<records interpreter=\"" + "yale" + "\">");
      for (Node recordNode : recordNodes) {
        rec = new Rec();
        sb.append("<record>");

        // Builds the Rec from the <record> element.
        for (String c : concepts) {
          Node node = recordNode.selectSingleNode("dwc:" + c);
          if (node == null) {
            continue;
          }
          String nodeValue = node.getStringValue();
          rec.put(c.toLowerCase(), nodeValue);
        }

        // Output <record> element XML.
        for (Entry<String, String> entry : rec.entrySet()) {
          sb.append("<dwc:" + entry.getKey() + ">");
          sb.append(entry.getValue());
          sb.append("</dwc:" + entry.getKey() + ">");
        }

        // Georeferences the Rec and outputs the <georeferences> element XML.
        List<Georef> georefs = georeference(rec, "yale");
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
        sb.append("</record>");
      }
      sb.append("</records>");

      return sb.toString();
    } catch (DocumentException e) {
      e.printStackTrace();
      return null;
    }
  }
}
