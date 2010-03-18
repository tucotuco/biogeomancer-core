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

package org.biogeomancer.utils;

import java.awt.geom.Point2D;

import org.biogeomancer.managers.DatumManager;
import org.biogeomancer.managers.DatumManager.Datum;

public class Coordinate extends Point2D.Double {
  public static void main(String[] argv) {
    Coordinate c = null;
    double p = 0;
    c = new Coordinate(12, 14); // 1
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12, 14.5); // 2
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.5, 14.6666667); // 6
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.3333333, 14.1); // 10
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.4, 14.116666667); // 60
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.216666667, 14.17); // 100
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.241666667); // 120
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.602777778); // 360
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.998333333); // 600
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.023); // 1000
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.116388889); // 3600
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.0698333333); // 6000
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.1237); // 10000
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.0116388889); // 36000
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.0151833333); // 60000
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.04193); // 100000
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(12.31, 14.0999972222); // 360000
    p = c.calculateCoordinatePrecision();
    System.out.println("Coord: " + c.toString() + "\tPrecision: " + 1 / p);

    c = new Coordinate(-179.5, 0);
    p = c.getLngDistanceInMetersToCoordinate(new Coordinate(179.5, 0));
    System.out.println("Coord: " + c.toString() + "\tLng Distance (m): " + p);
  }

  public DatumManager.Datum datum;

  public double precision; // a value in decimal degrees to express precision

  // (e.g., 0.5 means nearest 30 minutes or half
  // degree). 0 means exact to within 0.01 seconds.

  public Coordinate(double lng, double lat) { // constructor
    super(lng, lat);
    this.precision = calculateCoordinatePrecision();
    TruncateCoordinates();
    datum = DatumManager.getInstance().getDatum(null);
  }

  public Coordinate(double lng, double lat, DatumManager.Datum datum) { // constructor
    super(lng, lat);
    this.precision = calculateCoordinatePrecision();
    TruncateCoordinates();
    this.datum = datum;
  }

  public Coordinate(double lng, double lat, DatumManager.Datum datum,
      double precision) { // constructor
    super(lng, lat);
    this.datum = datum;
    if (precision <= 0)
      this.precision = calculateCoordinatePrecision();
    else
      this.precision = precision;
    TruncateCoordinates();
  }

  public Coordinate(double lng, double lat, String datumstring, double precision) { // constructor
    super(lng, lat);
    this.datum = DatumManager.getInstance().getDatum(datumstring);
    if (precision <= 0)
      this.precision = calculateCoordinatePrecision();
    else
      this.precision = precision;
    TruncateCoordinates();
  }

  public double calculateCoordinatePrecision() {
    // Use the content of the coordinates to deduce an overall precision
    // Test to see if the fractional part of the coordinate value can be
    // turned in to any of the target fractions within a tolerance level.
    // 30', 10', .1d, 1', .01d, 30", 10", .1', .001d, 1", .01', .0001d, .1",
    // .001', .00001d, .01"
    double[] denominators = { 2, 6, 10, 60, 100, 120, 360, 600, 1000, 3600,
        6000, 10000, 36000, 60000, 100000, 360000 };
    double latcoorduncertainty = 0;
    double lngcoorduncertainty = 0;
    double intpart = Math.floor(Math.abs(y)); // The integer part of the
    // absolute value of the latitude.
    double fracpart = Math.abs(y) - intpart; // The fractional part of the
    // absolute value of the latitude.
    if (fracpart == 0) {
      latcoorduncertainty = 1;
    } else {
      for (int i = 0; i < denominators.length; i++) {
        double d = denominators[i];
        double x = (fracpart * d) / Math.rint(fracpart * d);
        if (x > 0.99999 && x < 1.00001) {
          latcoorduncertainty = 1.0 / denominators[i]; // A matching fraction
          // was found.
          i = denominators.length;
        }
      }
    }
    intpart = Math.floor(Math.abs(x)); // The integer part of the absolute
    // value of the longitude.
    fracpart = Math.abs(x) - intpart; // The fractional part of the absolute
    // value of the longitude.
    if (fracpart == 0) {
      lngcoorduncertainty = 1;
    } else {
      for (int i = 0; i < denominators.length; i++) {
        double d = denominators[i];
        double x = (fracpart * d) / Math.rint(fracpart * d);
        if (x > 0.99999 && x < 1.00001) {
          lngcoorduncertainty = 1.0 / denominators[i]; // A matching fraction
          // was found.
          i = denominators.length;
        }
      }
    }
    // return the higher of the lat and long precisions
    if (latcoorduncertainty < lngcoorduncertainty)
      return latcoorduncertainty;
    return lngcoorduncertainty;
  }

  public Coordinate clone() {
    Coordinate c = new Coordinate(this.x, this.y, this.datum, this.precision);
    return c;
  }

  public Coordinate geodeticToGeocentric() {
    // gi->Geocent_a = a;
    // gi->Geocent_b = b;
    // gi->Geocent_a2 = a * a;
    // gi->Geocent_b2 = b * b;
    // gi->Geocent_e2 = (gi->Geocent_a2 - gi->Geocent_b2) / gi->Geocent_a2;
    // gi->Geocent_ep2 = (gi->Geocent_a2 - gi->Geocent_b2) / gi->Geocent_b2;

    /*
     * * Don't blow up if Latitude is just a little out of the value* range as
     * it may just be a rounding issue. Also removed longitude* test, it should
     * be wrapped by cos() and sin(). NFW for PROJ.4, Sep/2001.
     */
    // if( Latitude < -PI_OVER_2 && Latitude > -1.001 * PI_OVER_2 )
    // Latitude = -PI_OVER_2;
    // else if( Latitude > PI_OVER_2 && Latitude < 1.001 * PI_OVER_2 )
    // Latitude = PI_OVER_2;
    // else if ((Latitude < -PI_OVER_2) || (Latitude > PI_OVER_2))
    // { /* Latitude out of range */
    // Error_Code |= GEOCENT_LAT_ERROR;
    // }

    // if (!Error_Code)
    // { /* no errors */
    // if (Longitude > PI)
    // Longitude -= (2*PI);
    // Sin_Lat = sin(Latitude);
    // Cos_Lat = cos(Latitude);
    // Sin2_Lat = Sin_Lat * Sin_Lat;

    // Rn = gi->Geocent_a / (sqrt(1.0e0 - gi->Geocent_e2 * Sin2_Lat));

    // *X = (Rn + Height) * Cos_Lat * cos(Longitude);
    // *Y = (Rn + Height) * Cos_Lat * sin(Longitude);
    // *Z = ((Rn * (1 - gi->Geocent_e2)) + Height) * Sin_Lat;
    return new Coordinate(0, 0);
  }

  public DatumManager.Datum getDatum() {
    return this.datum;
  }

  public double getDatumError() { // return the uncertainty (in meters) of the
    // coordinate if the datum is not known
    /*
     * TODO: This should be an analysis of the maximum distance between datums
     * normally in use at the given coordinates.
     */

    double error = 0.0;
    /*
     * If the coordinates are in North America and the Coordinate Reference
     * System (CRS) is not given, then the candidate CRSs are WGS84 and NAD27.
     * For the moment, if no datum is specified, we need to find the worst case
     * uncertainty at this location based on all of the ellipsoids. Outside
     * North America, an unknown datum is taken to contribute up to a kilometer
     * of error. The worst case scenario 3.552 km would be extremely rare. The
     * mean difference between any datum and WGS84 is 0.653 km.
     */
    if (datum == null || datum.getCode().equalsIgnoreCase("unknown")) {
      if (this.y >= 13.79 && this.y <= 84.69 && this.x >= -179.48
          && this.x <= -51.48) { // Coordinates are in North American region
        error = 320;
      } else {
        error = 1000.0;
      }
    } // otherwise the datum is known and there is no uncertainty from this
    // source
    return error;
  }

  // This calculation is not rigorously correct, especially over large distances
  // in non-cardinal directions. Use with caution.
  public double getDistanceInMetersToCoordinate(Coordinate c) {
    double d = 0;
    d = Math.pow(Math.pow(this.getLatDistanceInMetersToCoordinate(c), 2.0)
        + Math.pow(this.getLngDistanceInMetersToCoordinate(c), 2.0), 0.5);
    return d;
  }

  public double getLatDistanceInMetersToCoordinate(Coordinate c) {
    double d = 0;
    // TODO: reproject if the same datum isn't used
    d = this.getLatMetersPerDegree() * (this.y - c.y);
    return d;
  }

  public double getLatLngMetersPerDegree() { // return the maximum distance in
    // meters from this point to a
    // point one degree away in
    // Latitude and Longitude
    double latmpd = getLatMetersPerDegree();
    double lngmpd = getLngMetersPerDegree();
    double error = Math.sqrt(Math.pow(latmpd, 2.0) + Math.pow(lngmpd, 2.0));
    return error;
  }

  public double getLatLngPrecisionInMeters() {
    return Math.sqrt(Math.pow(this.getLatPrecisionInMeters(), 2.0)
        + Math.pow(this.getLngPrecisionInMeters(), 2.0));
  }

  public double getLatMetersPerDegree() { // return the number of meters in one
    // degree of Latitude at this
    // coordinate
    // The distance between point A at a latitude equal to decimallatitude and
    // point B
    // at a latitude one degree removed from point A, but at the same longitude,
    // is here
    // estimated to be the length of an arc subtending an angle of one degree
    // with a
    // radius equal to the distance from the center of the ellipsoid to the
    // point A.
    // The source for the following values is NIMA 8350.2, 4 Jul 1977
    double a = this.datum.getSemiMajorAxis(); // a = semimajor axis of the datum
    // ellipsoid
    double f = this.datum.getFlattening(); // f = flattening of the datum
    // ellipsoid
    double e_squared = this.datum.get_esquared(); // e^2 = 2f - f^2

    // M - radius of curvature in the prime meridian, (tangent to ellipsoid at
    // latitude)
    // M is a function of latitude.
    double M = a
        * (1.0 - e_squared)
        / Math.pow(1.0 - e_squared
            * Math.pow(Math.sin(this.y * Math.PI / 180.0), 2.0), 1.5); // M(lat)
    // =
    // a(1-e^2)/(1-e^2*sin^2(lat))^1.5
    double latmpd = Math.PI * M / 180.0;
    return latmpd;
  }

  public double getLatPrecisionInMeters() {
    return this.precision * this.getLatMetersPerDegree();
  }

  public double getLngDistanceInMetersToCoordinate(Coordinate c) {
    // TODO: reproject if the same datum isn't used
    double fromx = this.x, tox = c.x;
    if (c == null)
      return -1;
    double maxx = -180.0;
    double minx = 180.0;
    if (fromx > maxx)
      maxx = fromx;
    if (tox > maxx)
      maxx = tox;
    if (fromx < minx)
      minx = fromx;
    if (tox < minx)
      minx = tox;
    if (maxx > 90 && minx < -90) { // coordinates on opposite sides of
      // longitude = 180
      if (fromx < tox)
        fromx += 360;
      else
        fromx += 360;
    }
    double d = this.getLngMetersPerDegree() * (fromx - tox);
    return d;
  }

  public double getLngMetersPerDegree() { // return the number of meters in one
    // degree of Longitude at this
    // coordinate
    // The distance between point A at a latitude equal to decimallatitude and
    // point B
    // at the same latitude, but one degree removed from point A in longitude,
    // is here
    // estimated to be the length of an arc subtending an angle of one degree
    // with a
    // radius equal to the distance from point A to the polar axis and
    // orthogonal to it.
    double a = this.datum.getSemiMajorAxis(); // a = semimajor axis of the datum
    // ellipsoid
    double f = this.datum.getFlattening(); // f = flattening of the datum
    // ellipsoid
    double e_squared = this.datum.get_esquared(); // e^2 = 2f - f^2

    // N - radius of curvature in the prime vertical, (tangent to ellipsoid at
    // latitude)
    double N = a
        / Math.sqrt(1.0 - e_squared
            * (Math.pow(Math.sin(this.y * Math.PI / 180.0), 2.0))); // N(lat) =
    // a/(1-e^2*sin^2(lat))^0.5

    // longitude is irrelevant for the calculations to follow so simplify by
    // using longitude = 0, so Y = 0
    // X is the orthogonal distance to the polar axis.
    double X = N * Math.cos(this.y * Math.PI / 180.0) * 1.0; // X =
    // Ncos(lat)cos(long).
    // long = 0, so
    // cos(long) = 1.0
    double lngmpd = Math.PI * X / 180.0;
    return lngmpd;
  }

  public double getLngPrecisionInMeters() {
    return this.precision * this.getLngMetersPerDegree();
  }

  public double getPrecision() {
    return this.precision;
  }

  public boolean isGeographicCoordinate() {
    if (y < -90 || y > 90 || x < -180 || x > 180)
      return false;
    return true;
  }

  // TODO:
  public String toString() {
    return "lat: " + y + " lng: " + x + " datum: " + datum.getName()
        + " precision: " + precision;
  }

  public void toWGS84() { // transform this coordinate to WGS84
    if (this.datum == null || this.datum.getCode().equalsIgnoreCase("unknown")) {
      // if the datum is unknown, set it to WGS84. Note: there will be
      // uncertainty ramifications for this in a point-radius.
      this.datum = DatumManager.getInstance().getDatum("WGS84");
    }
    if (this.datum.getCode().equalsIgnoreCase("WGS84"))
      return;
    // Use PROJ.4 to do a datum transformation
    // http://trac.osgeo.org/proj/browser/trunk/proj/src
    // http://pyproj.googlecode.com/svn/trunk/src/geocent.c
    // http://trac.osgeo.org/proj/browser/trunk/proj/src/pj_transform.c

    Datum wgs84 = DatumManager.getInstance().getDatum("WGS84");
    // get parameters for this.datum
    double source_axis = this.datum.getSemiMajorAxis(); // a
    double source_f = this.datum.getFlattening(); // f
    double source_e2 = this.datum.get_esquared(); // e^2
    double dest_axis = wgs84.getSemiMajorAxis();
    double dest_f = wgs84.getFlattening();
    double dest_e2 = wgs84.get_esquared();

    // If this.datum requires a grid shift, then apply the grid shift to the
    // geodetic coordinates.
    if (this.datum.getType().equalsIgnoreCase("GRIDSHIFT")) {

    }

  }

  public void TruncateCoordinates() {
    double lat7 = (int) Math.floor(10000000 * y + 0.5);
    y = lat7 / 10000000;
    double lng7 = (int) Math.floor(10000000 * x + 0.5);
    x = lng7 / 10000000;
  }
}