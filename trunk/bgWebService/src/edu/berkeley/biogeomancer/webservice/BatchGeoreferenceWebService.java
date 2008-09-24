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

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.biogeomancer.records.Rec;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import edu.berkeley.biogeomancer.webservice.util.BgUtil;
import edu.berkeley.biogeomancer.webservice.util.CustomNamespaceDocument;

/**
 * Web service for batch georeferencing using the BioGeomancer Core API.
 * 
 */
public class BatchGeoreferenceWebService extends HttpServlet {

  private static final long serialVersionUID = 1L;

  Logger log = Logger.getLogger(BatchGeoreferenceWebService.class);

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, java.io.IOException {
    System.out.println("doPost...");
    response.setContentType("text/html");

    BufferedReader reader = request.getReader();
    PrintWriter out = response.getWriter();

    out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    out
        .println("<biogeomancer xmlns:dwcore=\"http://rs.tdwg.org/dwc/dwcore\" xmlns:dwgeo=\"http://rs.tdwg.org/dwc/geospatial\">");

    parseXml(reader, out);
    out.println("</biogeomancer>");
    out.close();
  }

  private void parseXml(Reader read, PrintWriter out) {
    SAXReader reader = new SAXReader();
    try {
      Document xmlDoc = reader.read(read);
      CustomNamespaceDocument cnd = new CustomNamespaceDocument(xmlDoc);
      List<Node> recordNodes = cnd.getNodes("record");
      Element requestElement = recordNodes.get(0).getParent();

      String higherGeography = null;
      String continent = null;
      String waterBody = null;
      String islandGroup = null;
      String island = null;
      String country = null;
      String stateProvince = null;
      String county = null;
      String locality = null;
      String verbatimLatitude = null;
      String verbatimLongitude = null;
      String interpreter = requestElement.attributeValue("interpreter");
      String header = requestElement.attributeValue("header");
      boolean showheader = false;
      if (header != null && header.equalsIgnoreCase("true"))
        showheader = true;

      out.println("<records>");
      for (Node recordNode : recordNodes) {
        higherGeography = null;
        continent = null;
        waterBody = null;
        islandGroup = null;
        island = null;
        country = null;
        stateProvince = null;
        county = null;
        locality = null;
        verbatimLatitude = null;
        verbatimLongitude = null;
        out.println("<record>");
        Node higherGeogNode = recordNode
            .selectSingleNode("dwcore:HigherGeography");
        if (higherGeogNode != null)
          higherGeography = higherGeogNode.getStringValue();
        Node continentNode = recordNode.selectSingleNode("dwcore:Continent");
        if (continentNode != null)
          continent = continentNode.getStringValue();
        Node waterBodyNode = recordNode.selectSingleNode("dwcore:WaterBody");
        if (waterBodyNode != null)
          waterBody = waterBodyNode.getStringValue();
        Node islandGroupNode = recordNode
            .selectSingleNode("dwcore:IslandGroup");
        if (islandGroupNode != null)
          islandGroup = islandGroupNode.getStringValue();
        Node islandNode = recordNode.selectSingleNode("dwcore:Island");
        if (islandNode != null)
          island = islandNode.getStringValue();
        Node countryNode = recordNode.selectSingleNode("dwcore:Country");
        if (countryNode != null)
          country = countryNode.getStringValue();
        Node stateProvinceNode = recordNode
            .selectSingleNode("dwcore:StateProvince");
        if (stateProvinceNode != null)
          stateProvince = stateProvinceNode.getStringValue();
        Node countyNode = recordNode.selectSingleNode("dwcore:County");
        if (countyNode != null)
          county = countyNode.getStringValue();
        Node localityNode = recordNode.selectSingleNode("dwcore:Locality");
        if (localityNode != null)
          locality = localityNode.getStringValue();
        Node verbatimLatitudeNode = recordNode
            .selectSingleNode("dwgeo:VerbatimLatitude");
        if (verbatimLatitudeNode != null)
          verbatimLatitude = verbatimLatitudeNode.getStringValue();
        Node verbatimLongitudeNode = recordNode
            .selectSingleNode("dwgeo:VerbatimLongitude");
        if (verbatimLongitudeNode != null)
          verbatimLongitude = verbatimLongitudeNode.getStringValue();

        Rec r = new Rec();
        if (r != null) {
          if (higherGeography != null) {
            r.put("highergeography", higherGeography);
          }
          if (continent != null) {
            r.put("continent", continent);
          }
          if (waterBody != null) {
            r.put("waterbody", waterBody);
          }
          if (islandGroup != null) {
            r.put("islandGroup", islandGroup);
          }
          if (island != null) {
            r.put("island", island);
          }
          if (country != null) {
            r.put("country", country);
          }
          if (stateProvince != null) {
            r.put("stateprovince", stateProvince);
          }
          if (county != null) {
            r.put("county", county);
          }
          if (locality != null) {
            r.put("locality", locality);
          }
          if (verbatimLatitude != null) {
            r.put("verbatimlatitude", verbatimLatitude);
          }
          if (verbatimLongitude != null) {
            r.put("verbatimlongitude", verbatimLongitude);
          }
        }

        BgUtil.buildSingleXmlText(r, interpreter, showheader, out);
        out.println("</record>");
      }
      out.println("</records>");
    } catch (DocumentException e) {
      e.printStackTrace();
    }

  }
}
