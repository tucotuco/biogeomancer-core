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

import org.biogeomancer.managers.DatumManager;

public class PointRadius extends Coordinate { // point radius inner class
  public double extent; // maximum uncertainty radius

  public PointRadius(Coordinate c) { // constructor
    super(c.x, c.y);
    this.datum = c.getDatum();
    this.precision = c.precision;
    this.extent = 0.0;
  }

  public PointRadius(Coordinate c, double extent) { // constructor
    super(c.x, c.y);
    this.datum = c.getDatum();
    this.precision = c.precision;
    this.extent = extent;
    TruncateExtent();
  }

  public PointRadius(double lng, double lat, DatumManager.Datum datum) { // constructor
    super(lng, lat, datum);
    this.extent = 0.0;
  }

  public PointRadius(double lng, double lat, DatumManager.Datum datum,
      double precision) { // constructor
    super(lng, lat, datum, precision);
    this.extent = 0.0;
  }

  public PointRadius(double lng, double lat, DatumManager.Datum datum,
      double precision, double extent) { // constructor
    super(lng, lat, datum, precision);
    this.extent = extent;
    TruncateExtent();
  }

  public PointRadius(double lng, double lat, double extent) { // constructor
    super(lng, lat);
    this.extent = extent;
    TruncateExtent();
  }

  // public PointRadius() {} // nothing
  private PointRadius(double lng, double lat) { // constructor
    super(lng, lat);
    this.extent = 0.0;
  }

  public boolean equals(PointRadius pr) {
    if (this.x != pr.x)
      return false;
    if (this.y != pr.y)
      return false;
    if (this.extent != pr.extent)
      return false;
    if (this.datum != pr.datum)
      return false;
    return true;
  }

  public String toString() {
    return "<DecimalLatitude>" + y + "</DecimalLatitude>\n<DecimalLongitude>"
        + x + "</DecimalLongitude>\n<CoordinateUncertaintyInMeters>" + extent
        + "</CoordinateUncertaintyInMeters>\n<GeodeticDatum>" + datum.getName()
        + "</GeodeticDatum>";
  }

  public String toXML() {
    String s = new String("<POINT_RADIUS>\n");
    s = s.concat("<DecimalLatitude>" + y + "</DecimalLatitude>\n");
    s = s.concat("<DecimalLongitude>" + x + "</DecimalLongitude>\n");
    s = s.concat("<CoordinateUncertaintyInMeters>" + extent
        + "</CoordinateUncertaintyInMeters>\n");
    s = s.concat("<GeodeticDatum>" + datum.getName() + "</GeodeticDatum>\n");
    s = s.concat("</POINT_RADIUS>\n");
    return s;
  }

  public void TruncateExtent() {
    extent = (int) Math.floor(extent + 0.5);
  }
}