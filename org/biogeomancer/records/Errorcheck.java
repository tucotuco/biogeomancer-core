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

package org.biogeomancer.records;

public class Errorcheck {
  // basic alt data
  public int mapalt; // altitude of point on map
  public int mapaltmin_uncertainty; // min alt based on uncertainty radius
  public int mapaltmax_uncertainty; // max of same
  public int mapaltmin_neighborhoods; // min alt based on neighborhood (9 cells)
  public int mapaltmax_neighborhoods; // max of same

  // alt test results
  public float dif; // dif between mapalt and user provided alt (alt_collect)
  public boolean inaltrange; // is alt_colect with range of mapaltmin &
                              // mapaltmax

  // admin boundaries checking
  // all arrays: 0 = country; 1 = adm1; 2 = adm2; 3 = adm3; 4 = adm4

  public String adm[] = new String[5]; // the adm name observed

  public boolean inadm[] = new boolean[5]; // is the point inside the specified
                                            // adm?

  public boolean indatabaseatthislevel[] = new boolean[5]; // is the admin name
                                                            // known at this
                                                            // level?
  public float disttoadm[] = new float[5]; // how far from the specified adm?

  public boolean indatabase[] = new boolean[5]; // is the admin name known at
                                                // any level?
  public boolean inotherleveladm[] = new boolean[5]; // is the point inside the
                                                      // specified adm (at
                                                      // different level)?

  public float disttootherleveladm[] = new float[5]; // how far from the
                                                      // specified adm (at
                                                      // different level)?

}
