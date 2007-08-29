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

package org.biogeomancer.ws;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;
import org.biogeomancer.validation.SpatialAttributeLookup;

import com.megginson.sax.DataWriter;

public class BioGeomancer {
  private static Properties props;
  private static Logger log;

  /**
   * Constructs a BioGeomancer object.
   */
  public BioGeomancer() {
    loadprops();
    startlog();
  }

  public String Georeference(String dataurl, String datadelineator,
      String interpreter) {
    try {
      String uploadPath = props.getProperty("uploadpath");

      String delineator = null;
      if (datadelineator.equalsIgnoreCase("tab"))
        delineator = "\t";
      if (datadelineator.equalsIgnoreCase("comma"))
        delineator = ",";

      // download data and create the recset
      // HttpClient client = new HttpClient();
      String tofile = uploadPath + "/bg_datafile_" + System.currentTimeMillis();
      // client.download(dataurl, tofile);
      RecSet recset = new RecSet(dataurl, datadelineator, tofile);

      // georeference the recset
      GeorefPreferences prefs = new GeorefPreferences();
      prefs.locinterp = interpreter;
      GeorefManager georefManager = new GeorefManager(recset);
      georefManager.georeference(prefs);
      if (recset != null)
        return outputResults(recset);
      return "Could not download RecSet from " + dataurl;
    }

    catch (GeorefManager.GeorefManagerException e) {
      e.printStackTrace();
      // TODO
    } catch (RecSet.RecSetException e) { // unable to create recset from file.
      e.printStackTrace();
    } catch (Exception e) { // unable to process uploaded file.
      e.printStackTrace();
    }

    return null;
  }

  public String spatialAttributeList() {
    SpatialAttributeLookup sal = new SpatialAttributeLookup();
    return "test"; // sal.getattributes();
  }

  public String spatialAttributeLookup(String lng, String lat, String attribute) {
    SpatialAttributeLookup sal = new SpatialAttributeLookup();
    return sal.lookup(lng, lat, attribute);
  }

  /**
   * Loads the properties file.
   */
  private void loadprops() {
    try {
      ClassLoader loader = SpatialAttributeLookup.class.getClassLoader();
      InputStream inputStream = loader
          .getResourceAsStream("BioGeomancer.properties");
      props = new Properties();
      props.load(inputStream);
    } catch (Exception e) {
      log.error("could not read properties file: " + e.toString());
    }
  }

  /**
   * outputResults()
   * 
   * Outputs results.
   * 
   * @param response
   *          the http response object
   * @param output
   *          the request's output parameter value.
   * @param recset
   *          the recset generated with this request.
   */
  private String outputResults(RecSet recset) {
    try {
      StringWriter xml = new StringWriter();
      DataWriter w = new DataWriter(xml);
      w.setIndentStep(2);
      w.startDocument();
      w.startElement("BioGeomancer");
      for (Rec rec : recset.recs) {
        w.startElement("Record");

        // Print out the rec hashmap properties.
        Iterator keys = rec.keySet().iterator();
        String name, value = null;
        while (keys.hasNext()) {
          name = (String) keys.next();
          value = rec.get(name);
          w.dataElement(name, value);
        }

        w.endElement("Record");
      }
      w.endElement("BioGeomancer");
      w.endDocument();
      String result = xml.toString();
      if (result == null)
        return "Problem parsing RecSet";
      return result;
    } catch (Exception e) {
      System.out.println("Problem in outputResults() --> ");
      e.printStackTrace();
      return "Problem parsing RecSet";
    }
  }

  /**
   * Starts the log.
   */
  private void startlog() {
    ConsoleAppender console = new ConsoleAppender(new SimpleLayout(),
        ConsoleAppender.SYSTEM_OUT);
    log = Logger.getLogger(BioGeomancer.class);
    // log.addAppender(console);
  }
}
