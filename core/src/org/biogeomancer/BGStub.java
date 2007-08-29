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

package org.biogeomancer;

import java.io.FileInputStream;
import java.util.Properties;
import java.net.URL;

import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.validation.ValidationManager;
import org.biogeomancer.records.Clause;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;

public class BGStub {
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
      String enableGeorefStr = props.getProperty("GeorefEngine.enabled");
      if (enableGeorefStr == null
          || (enableGeorefStr != null && Boolean.parseBoolean(enableGeorefStr))) {
        GeorefManager georefmanager = new GeorefManager(recset);
        GeorefPreferences prefs = new GeorefPreferences();
        prefs.locinterp = props.getProperty("interpreter");
        georefmanager.georeference(prefs);
      }
      ValidationManager vm = new ValidationManager(props, recset);
      if (vm.enabled()) {
        vm.validate();
      }
      int recIndex = -1;
      int georefIndex;
      for (Rec rec : recset.recs) {
        ++recIndex;
        System.out.println("Rec [id =\"" + rec.get("id") + "\", "
            + "scientific name =\"" + rec.get("scientificname") + "\", "
            + "country =\"" + rec.get("country") + "\", " + "locality =\""
            + rec.get("locality") + "\", " + "declat = \"" + rec.get("declat")
            + "\", " + "declong = \"" + rec.get("declong") + "\"]");
        System.out.println("\tClauses: ");
        for (Clause clause : rec.clauses) {
          System.out.println("\t\tLocType: " + clause.locType);
        }
        System.out.println("\tInterpretations: ");
        georefIndex = -1;
        for (Georef georef : rec.georefs) {
          ++georefIndex;
          if (georef.pointRadius == null) {
            System.out.println("\t\t--> NULL!");
          } else {
            System.out.println("\t\t--> y: " + georef.pointRadius.getY()
                + " x: " + georef.pointRadius.getX());
          }
          vm.displayResults(recIndex, georefIndex);
        }
        vm.displayResults(recIndex);
      }
      vm.displaySummary();
    } catch (Exception e) {
      System.out.println(e.toString());
      e.printStackTrace();
    }
  }
}
