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

public class SnapPointToNearestFoundWaterBody2Response {
  protected edu.tulane.geolocate.GeographicPoint WGS84Coordinate;

  public SnapPointToNearestFoundWaterBody2Response() {
  }

  public SnapPointToNearestFoundWaterBody2Response(
      edu.tulane.geolocate.GeographicPoint WGS84Coordinate) {
    this.WGS84Coordinate = WGS84Coordinate;
  }

  public edu.tulane.geolocate.GeographicPoint getWGS84Coordinate() {
    return WGS84Coordinate;
  }

  public void setWGS84Coordinate(
      edu.tulane.geolocate.GeographicPoint WGS84Coordinate) {
    this.WGS84Coordinate = WGS84Coordinate;
  }
}
