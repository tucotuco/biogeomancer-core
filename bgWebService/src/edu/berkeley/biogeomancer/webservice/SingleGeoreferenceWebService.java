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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.biogeomancer.records.Rec;

import edu.berkeley.biogeomancer.webservice.util.BgUtil;

/**
 * Web service for georeferencing a single locality.
 * 
 */
public class SingleGeoreferenceWebService extends HttpServlet {

  Logger log = Logger.getLogger(SingleGeoreferenceWebService.class);

  private final BgUtil bgUtil = new BgUtil();

  /**
   * Georeferences a single locality using the BioGeomancer Core API. Returns
   * all generated georeferences as XML.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, java.io.IOException {
    response.setContentType("text/xml");
    PrintWriter out = response.getWriter();

    // Build the XML header.
    out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    out
        .println("<biogeomancer xmlns:dwcore=\"http://rs.tdwg.org/dwc/dwcore/\" xmlns:dwgeo=\"http://rs.tdwg.org/dwc/geospatial/\">");

    // The fully spelled out parameter names
    String id = request.getParameter("id");
    String higherGeography = request.getParameter("highergeography");
    String continent = request.getParameter("continent");
    String waterBody = request.getParameter("waterbody");
    String islandGroup = request.getParameter("islandgroup");
    String island = request.getParameter("island");
    String country = request.getParameter("country");
    String stateProvince = request.getParameter("statepprovince");
    String county = request.getParameter("county");
    String locality = request.getParameter("locality");
    String verbatimLatitude = request.getParameter("verbatimlatitude");
    String verbatimLongitude = request.getParameter("verbatimlongitude");
    String interpreter = request.getParameter("interpreter");
    String headerFlag = request.getParameter("header");
    boolean showheader = false;

    // Alternate parameter names
    if (higherGeography == null)
      higherGeography = request.getParameter("hg");
    if (higherGeography == null)
      higherGeography = request.getParameter("g");
    if (continent == null)
      continent = request.getParameter("cont");
    if (waterBody == null)
      waterBody = request.getParameter("wb");
    if (waterBody == null)
      waterBody = request.getParameter("w");
    if (islandGroup == null)
      islandGroup = request.getParameter("ig");
    if (island == null)
      island = request.getParameter("is");
    if (country == null)
      country = request.getParameter("cy");
    if (country == null)
      country = request.getParameter("gadm0");
    if (stateProvince == null)
      stateProvince = request.getParameter("sp");
    if (stateProvince == null)
      stateProvince = request.getParameter("s");
    if (stateProvince == null)
      stateProvince = request.getParameter("gadm1");
    if (county == null)
      county = request.getParameter("co");
    if (county == null)
      county = request.getParameter("gadm2");
    if (locality == null)
      locality = request.getParameter("loc");
    if (locality == null)
      locality = request.getParameter("l");
    if (verbatimLatitude == null)
      verbatimLatitude = request.getParameter("latitude");
    if (verbatimLatitude == null)
      verbatimLatitude = request.getParameter("lat");
    if (verbatimLatitude == null)
      verbatimLatitude = request.getParameter("vlat");
    if (verbatimLongitude == null)
      verbatimLongitude = request.getParameter("longitude");
    if (verbatimLongitude == null)
      verbatimLongitude = request.getParameter("long");
    if (verbatimLongitude == null)
      verbatimLongitude = request.getParameter("lng");
    if (verbatimLongitude == null)
      verbatimLongitude = request.getParameter("vlong");
    if (verbatimLongitude == null)
      verbatimLongitude = request.getParameter("vlng");
    if (interpreter == null)
      interpreter = request.getParameter("int");
    if (interpreter == null)
      interpreter = request.getParameter("i");
    if (interpreter == null)
      interpreter = "yale";
    if (headerFlag != null && headerFlag.equalsIgnoreCase("true"))
      showheader = true;

    Rec r = new Rec();
    if (r != null) {
      if (id != null) {
        r.put("id", id);
      }
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
        r.put("islandgroup:", islandGroup);
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
    out.println("</biogeomancer>");
  }
}