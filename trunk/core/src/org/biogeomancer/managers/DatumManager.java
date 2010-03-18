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

package org.biogeomancer.managers;

import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * @author tuco
 * 
 */

public class DatumManager extends BGManager {
  public class Datum {
    String code;
    String name;
    String ellipsecode;
    String type;
    DatumManager.Ellipsoid ellipsoid;

    public Datum() {
      this.code = new String("unknown");
      this.name = new String("not recorded");
      this.type = "WGS84";
      this.ellipsecode = new String("WE");
      this.ellipsoid = new Ellipsoid("WE", "World Geodetic System 1984",
          6378137.0, 1.0 / 298.257223563);
    }

    public Datum(String code, String name, String ellipsecode) {
      this.code = code;
      this.name = name;
      this.ellipsecode = ellipsecode;
      Ellipsoid e = ellipsoids.get(ellipsecode);
      if (e == null) {
        log.error("Datum (" + code + ") created with unknown ellipsoidcode ("
            + ellipsecode + "), using WGS84 ellipsoid.");
        this.ellipsoid = new Ellipsoid("WE", "World Geodetic System 1984",
            6378137.0, 1.0 / 298.257223563);
      } else {
        this.ellipsoid = ellipsoids.get(ellipsecode);
      }
      if (this.ellipsecode.equalsIgnoreCase("WE")) {
        this.type = "WGS84";
      }
    }

    public double get_esquared() {
      return ellipsoid.flattening * (2 - ellipsoid.flattening);
    }

    public String getCode() {
      return this.code;
    }

    public String getEllipseCode() {
      return this.ellipsecode;
    }

    public double getFlattening() {
      return ellipsoid.flattening;
    }

    public String getName() {
      return this.name;
    }

    public double getSemiMajorAxis() {
      return ellipsoid.semiMajorAxis;
    }

    public String getType() {
      return this.type;
    }

    public String toString() {
      if (ellipsoid == null) {
        return "Datum code: " + this.code + "\tdatum name: " + this.name + "\t"
            + "no ellipsoid found in configuration file for this datum." + "\n";
      }
      return "Datum code: " + this.code + "\tdatum name: " + this.name + "\t"
          + ellipsoid.toString();
    }
  }

  public class Ellipsoid {
    String code;
    String name;
    double flattening;
    double semiMajorAxis;

    public Ellipsoid() {
      this.code = "WE";
      this.name = "World Geodetic System 1984";
      this.semiMajorAxis = 6378137.0;
      this.flattening = 1.0 / 298.257223563;
    }

    public Ellipsoid(String code, String name, double semiMajorAxis,
        double flattening) {
      this.code = code;
      this.name = name;
      this.flattening = flattening;
      this.semiMajorAxis = semiMajorAxis;
    }

    public String getCode() {
      return this.code;
    }

    public double getFlattening() {
      return this.flattening;
    }

    public String getName() {
      return this.name;
    }

    public double getSemiMajorAxis() {
      return this.semiMajorAxis;
    }

    public String toString() {
      return "ellipsoid code: " + this.code + "\tellipsoid name: " + this.name
          + "\tinverse flattening: " + 1.0 / this.flattening
          + "\tsemi-major axis (m): " + this.semiMajorAxis + "\n";
    }
  }

  public static final String PROPS_FILE = "DatumManager.properties";

  private static final Logger log = Logger.getLogger(DatumManager.class);

  private static Properties props = new Properties();

  private static DatumManager instance = null;
  static {
    initProps(PROPS_FILE, props);
  }

  public static DatumManager getInstance() {
    if (instance == null)
      instance = new DatumManager();
    return instance;
  }

  public static void main(String[] args) { // main
    DatumManager dmanager = DatumManager.getInstance();
    log.info("Ellipsoids:\n" + dmanager.ellipsoids.toString() + "\n"
        + "Datums:\n" + dmanager.datums.toString());
  }

  public HashMap<String, DatumManager.Datum> datums = new HashMap<String, DatumManager.Datum>(); // key
  // =
  // datumname,
  // value
  // =
  // datum
  // object
  public HashMap<String, DatumManager.Ellipsoid> ellipsoids = new HashMap<String, DatumManager.Ellipsoid>(); // key

  // =
  // ellipsoidname,
  // value
  // =
  // ellipsoid
  // object

  private DatumManager() { // constructor
    if (props == null) {
      log
          .error("Unable to load DatumManager.properties file. Only the default datum (WGS84) will be known to the system.");
      datums.put("WGS84", new Datum()); // always have a default datum
      // definition
      ellipsoids.put("WE", new Ellipsoid());
      return;
    }

    // log.info("DatumManager started");

    String ep = props.getProperty("ellipsoid.properties");
    if (ep == null) {
      ellipsoids.put("WE", new Ellipsoid()); // always have a default ellipsoid
      // definition
      log
          .error("Could not find the ellipsoidfile property in DatumManager.properties. Only the default ellipsoid (WGS84) will be known to the system.");
    } else {
      parseEllipsoidPropertiesFile();
    }
    String dp = props.getProperty("datum.properties");
    if (dp == null) {
      datums.put("WGS84", new Datum()); // always have a default datum
      // definition
      log
          .error("Could not find the datumfile property in DatumManager.properties. Only the default datum (WGS84) will be known to the system.");
    } else {
      parseDatumPropertiesFile();
    }
  }

  // gets a datum from datums map using datumcode as key
  public DatumManager.Datum getDatum(String datumname) {
    Datum d = datums.get(datumname);
    if (d == null) {
      return datums.get("unknown");
    }
    return d;
  }

  // gets a datum from datums map using datumcode as key
  public DatumManager.Ellipsoid getEllipsoid(String ellipsoidcode) {
    return ellipsoids.get(ellipsoidcode);
  }

  public void log(String s) {
    this.log(s);
  }

  public void parseDatumPropertiesFile() {

    // Load the datum properties file.
    Properties datumProps = new Properties();
    getProps(props.getProperty("datum.properties"), datumProps);

    // Parse the datum properties.
    Pattern delineator = Pattern.compile(";");
    String[] datumvals;
    Set datumcodes = datumProps.keySet();
    int DNAME = 0; // datum name
    int ECODE = 1; // ellipsoid code
    int EFLAT = 2; // ellipsoid flattening constant
    int EAXIS = 3; // ellipsoid semimajor axis constant
    for (Object code : datumcodes) { // build datum objects, add them to datums
      // map
      String dcode = new String((String) code);
      datumvals = delineator.split(datumProps.getProperty(dcode));
      String ecode = new String(datumvals[ECODE]).trim();
      DatumManager.Ellipsoid e = ellipsoids.get(ecode);
      // DatumManager.Ellipsoid e = this.new Ellipsoid(datumvals[ECODE],
      // "pending",
      // 1.0/(new Double(datumvals[EFLAT]).doubleValue()),
      // The properties file contains the inverse of the flattening for easy
      // recognition.
      // new Double(datumvals[EAXIS]).doubleValue());
      if (e != null) {
        DatumManager.Datum d = this.new Datum(dcode, datumvals[DNAME], e
            .getCode());
        datums.put(dcode, d);
      }
    }
    if (datums.get("unknown") == null) {
      datums.put("WGS84", new Datum());
    }
    if (datums.get("WGS84") == null) {
      datums.put("WGS84", new Datum());
    }
  }

  public void parseEllipsoidPropertiesFile() {

    // Load the ellipsoid properties file.
    Properties ellipProps = new Properties();
    getProps(props.getProperty("ellipsoid.properties"), ellipProps);

    // Parse the ellipsoid properties file.
    Pattern delineator = Pattern.compile(";");
    String[] ellipsoidvals;
    Set ellipsoidcodes = ellipProps.keySet();
    int ENAME = 0; // ellipsoid name
    int EAXIS = 1; // ellipsoid semimajor axis constant
    int EFLAT = 2; // ellipsoid flattening constant
    for (Object code : ellipsoidcodes) { // build ellipsoid objects, add them
      // to ellipsoids map
      String ecode = new String((String) code);
      ellipsoidvals = delineator.split(ellipProps.getProperty(ecode));
      DatumManager.Ellipsoid e = this.new Ellipsoid(ecode,
          ellipsoidvals[ENAME], new Double(ellipsoidvals[EAXIS]).doubleValue(),
          1.0 / (new Double(ellipsoidvals[EFLAT]).doubleValue()));
      ellipsoids.put(ecode, e);
    }
  }
}