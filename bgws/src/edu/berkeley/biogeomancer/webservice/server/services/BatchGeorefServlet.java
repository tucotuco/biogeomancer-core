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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.google.gwt.user.client.Window;

import edu.berkeley.biogeomancer.webservice.server.util.BgUtil;
import edu.berkeley.biogeomancer.webservice.server.util.CustomNamespaceDocument;

/**
 * Web service for batch georeferencing using the BioGeomancer Core API.
 * 
 */
public class BatchGeorefServlet extends HttpServlet {

  Logger log = Logger.getLogger(BatchGeorefServlet.class);

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    while (request.getParameterNames().hasMoreElements()) {
      log.info("Cool");
    }

    log.info(request.getReader().readLine());

    String requestXml = request.getParameter("requestXml");
    BufferedReader reader = request.getReader();
    Window.alert("requestXml");
    String line;
    // System.out.println("doPost");
    while ((line = reader.readLine()) != null) {
      log.info(line);
    }
    PrintWriter out = response.getWriter();

    out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    out.println("<biogeomancer xmlns:dwc=\"http://rs.tdwg.org/tapir/1.0\">");

    // parseXml(reader, out);
    out.println("</biogeomancer>");
    out.close();
  }

  /**
   * @param Reader read
   * @param PrintWriter out read xml text from Reader read and output (using
   *          PrintWriter out) just as like Batch output at
   *          http://code.google.com/p/biogeomancer-core/wiki/WebServicesRequirements
   */
  private void parseXml(Reader read, PrintWriter out) {
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
        Node localityNode = recordNode.selectSingleNode("dwc:Locality");
        String locality = localityNode.getStringValue();
        Node higherGeogNode = recordNode
            .selectSingleNode("dwc:HigherGeography");
        String higherGeography = higherGeogNode.getStringValue();
        BgUtil.buildSingleXmlText(locality, higherGeography, interpreter, out);
        out.println("</record>");
      }
      out.println("</records>");
    } catch (DocumentException e) {
      e.printStackTrace();
    }

  }
}
