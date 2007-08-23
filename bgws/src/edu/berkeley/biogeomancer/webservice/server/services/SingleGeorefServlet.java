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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.berkeley.biogeomancer.webservice.server.util.BgUtil;

/**
 * Web service for georeferencing a single locality.
 * 
 */
public class SingleGeorefServlet extends HttpServlet {

  Logger log = Logger.getLogger(SingleGeorefServlet.class);

  private final BgUtil bgUtil = new BgUtil();

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

    /*
     * log.info("Locality: " + locality + " HigherGeography: " + higherGeography + "
     * Interpreter: " + interpreter);
     */

    PrintWriter out = response.getWriter();

    // Build the XML header.
    out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    out.println("<biogeomancer xmlns:dwc=\"http://rs.tdwg.org/tapir/1.0\">");

    out.println("<interpreter>" + interpreter + "</interpreter>");
    BgUtil.buildSingleXmlText(locality, higherGeography, interpreter, out);
    out.println("</biogeomancer>");
  }
}
