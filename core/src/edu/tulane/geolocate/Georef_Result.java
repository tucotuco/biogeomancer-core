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

// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3
package edu.tulane.geolocate;

public class Georef_Result {
  protected edu.tulane.geolocate.GeographicPoint WGS84Coordinate;
  protected java.lang.String parsePattern;

  public Georef_Result() {
  }

  public Georef_Result(edu.tulane.geolocate.GeographicPoint WGS84Coordinate,
      java.lang.String parsePattern) {
    this.WGS84Coordinate = WGS84Coordinate;
    this.parsePattern = parsePattern;
  }

  public java.lang.String getParsePattern() {
    return parsePattern;
  }

  public edu.tulane.geolocate.GeographicPoint getWGS84Coordinate() {
    return WGS84Coordinate;
  }

  public void setParsePattern(java.lang.String parsePattern) {
    this.parsePattern = parsePattern;
  }

  public void setWGS84Coordinate(
      edu.tulane.geolocate.GeographicPoint WGS84Coordinate) {
    this.WGS84Coordinate = WGS84Coordinate;
  }
}