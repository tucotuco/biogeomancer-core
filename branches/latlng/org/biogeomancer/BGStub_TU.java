/**
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

/*
 * bgStub_Local.java
 *
 * Created on August 3, 2006, 12:17 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.biogeomancer;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;

import edu.tulane.bg_geolocate;

/**
 * 
 * @author nelson
 */
public class BGStub_TU {
  public static boolean isUrl(String resource) {
    boolean ret = true;
    try {
      URL url = new URL(resource);
    } catch (Exception e) {
      ret = false;
    }
    return ret;
  }

  public static void main(String args[]) {
    System.out.println("BGStub running...");
    String configfile = args[0];
    Properties props = new Properties();
    try {
      props.load(new FileInputStream(configfile));
      String datafile = props.getProperty("datafile");
      RecSet recset = null;
      if (isUrl(datafile)) {
        recset = new RecSet(datafile, props.getProperty("delineator"), props
            .getProperty("destination"));
      } else {
        recset = new RecSet(datafile, props.getProperty("delineator"));
      }
      // String enableGeorefStr = props.getProperty("GeorefEngine.enabled");
      // if (enableGeorefStr == null || (enableGeorefStr != null &&
      // Boolean.parseBoolean(enableGeorefStr))) {
      // GeorefManager georefmanager = new GeorefManager(recset);
      // GeorefPreferences prefs = new GeorefPreferences();
      // prefs.locinterp = props.getProperty("interpreter");
      // georefmanager.georeference(prefs);
      // }

      bg_geolocate TULocInterp = new bg_geolocate();
      System.out.println("Starting Georeference: " + recset.recs.size()
          + " records");
      TULocInterp.doParsing(recset, "vlocality", "vgeography", "vcounty",
          "vstate", "vcounty");
      System.out.println("Results:");
      for (int i = 0; i < recset.recs.size(); i++) {
        Rec rr = recset.recs.get(i);
        System.out.println("++++++++++++++++");
        System.out.println(rr.get("vlocality"));
        System.out.println("Num Georefs = " + rr.georefs.size());

        for (Georef g : rr.georefs) {
          System.out.println("    Latitude: " + g.pointRadius.y
              + " Longitude: " + g.pointRadius.x);
          System.out.println("    " + g.iLocality);
        }
        System.out.println("++++++++++++++++");
      }

    } catch (Exception e) {
      System.out.println(e.toString());
      e.printStackTrace();
    }
  }
}
