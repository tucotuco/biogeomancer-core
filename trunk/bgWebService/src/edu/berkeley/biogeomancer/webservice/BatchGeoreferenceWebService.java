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
    out.println("<biogeomancer xmlns:dwc=\"http://rs.tdwg.org/tapir/1.0\">");

    parseXml(reader, out, true);
    out.println("</biogeomancer>");
    out.close();
  }

  private void parseXml(Reader read, PrintWriter out, boolean showheader) {
    SAXReader reader = new SAXReader();
    try {
      Document xmlDoc = reader.read(read);
      CustomNamespaceDocument cnd = new CustomNamespaceDocument(xmlDoc);
      List<Node> recordNodes = cnd.getNodes("record");
      Element requestElement = recordNodes.get(0).getParent();

      String interpreter = requestElement.attributeValue("interpreter");
      out.println("<records interpreter=\"" + interpreter + "\">");
      for (Node recordNode : recordNodes) {
        out.println("<record>");
        Node higherGeogNode = recordNode
            .selectSingleNode("dwcore:HigherGeography");
        String higherGeography = higherGeogNode.getStringValue();
        Node continentNode = recordNode.selectSingleNode("dwcore:Continent");
        String continent = continentNode.getStringValue();
        Node waterBodyNode = recordNode.selectSingleNode("dwcore:WaterBody");
        String waterBody = waterBodyNode.getStringValue();
        Node islandGroupNode = recordNode
            .selectSingleNode("dwcore:IslandGroup");
        String islandGroup = islandGroupNode.getStringValue();
        Node islandNode = recordNode.selectSingleNode("dwcore:Island");
        String island = islandNode.getStringValue();
        Node countryNode = recordNode.selectSingleNode("dwcore:Country");
        String country = countryNode.getStringValue();
        Node stateProvinceNode = recordNode
            .selectSingleNode("dwcore:StateProvince");
        String stateProvince = stateProvinceNode.getStringValue();
        Node countyNode = recordNode.selectSingleNode("dwcore:County");
        String county = countyNode.getStringValue();
        Node localityNode = recordNode.selectSingleNode("dwcore:Locality");
        String locality = localityNode.getStringValue();
        Node verbatimLatitudeNode = recordNode
            .selectSingleNode("dwgeo:VerbatimLatitude");
        String verbatimLatitude = verbatimLatitudeNode.getStringValue();
        Node verbatimLongitudeNode = recordNode
            .selectSingleNode("dwgeo:VerbatimLongitude");
        String verbatimLongitude = verbatimLongitudeNode.getStringValue();

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
