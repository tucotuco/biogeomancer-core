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
package edu.berkeley.biogeomancer.webservice.server.services.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.biogeomancer.records.Georef;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.berkeley.biogeomancer.webservice.client.services.controller.ControllerService;
import edu.berkeley.biogeomancer.webservice.server.util.BgUtil;

/**
 * Implements the server side asynchronous controller service specification.
 * 
 * @see edu.berkeley.cache.client.services.controller.ControllerService
 */

/**
 * @author aaron
 * 
 */
public class ControllerServiceImpl extends RemoteServiceServlet implements
    ControllerService {

  private static final Logger log = Logger
      .getLogger(ControllerServiceImpl.class);

  public String georeference(String locality, String higherGeog, String interp) {
    List<Georef> georefs = BgUtil.georeference(locality, higherGeog, interp);
    StringBuilder response = new StringBuilder();
    response.append("Locality: " + locality + " HigherGeography: " + higherGeog
        + " Interpreter: " + interp + "<BR>");
    for (Georef g : georefs) {
      response.append("Georeference: ");
      response.append("DecimalLatitude=" + g.pointRadius.y + "<BR>");
      response.append("DecimalLongitude=" + g.pointRadius.x + "<BR>");
      response.append("CoordinateUncertaintyInMeters=" + g.pointRadius.extent
          + "<P>");
    }
    return response.toString();
  }
}
