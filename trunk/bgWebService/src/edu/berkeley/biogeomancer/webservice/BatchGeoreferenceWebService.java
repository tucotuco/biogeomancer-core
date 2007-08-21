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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.berkeley.biogeomancer.webservice.util.BgUtil;

/**
 * Web service for batch georeferencing using the BioGeomancer Core API.
 * 
 */
public class BatchGeoreferenceWebService extends HttpServlet {

  Logger log = Logger.getLogger(BatchGeoreferenceWebService.class);

  private final BgUtil bgUtil = new BgUtil();

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, java.io.IOException {
    System.out.println("doPost...");
    response.setContentType("text/html");

    BufferedReader reader = request.getReader();
    String requestData = reader.readLine();

    // TODO
    // Use dom4j to parse <record> elements from the requestData.
    // Create a Rec for each <record> element and store in RecSet
    // List<Georef> georefs = bgUtil.georeference(List<Rec> recs);
    // iterate over georefs to build XML response (see Batch Ouput:
    // http://code.google.com/p/biogeomancer-core/wiki/WebServicesRequirements)
    // out.prinln(xmlResponse);

    PrintWriter out = response.getWriter();
    out.println(requestData);
  }
}
