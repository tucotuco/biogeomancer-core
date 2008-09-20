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
package edu.berkeley.biogeomancer.webservice.client;

import java.net.URL;
import java.util.List;

import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;

/**
 * Interface for the BioGeomancer georeferencing webservice.
 * 
 */
public interface BioGeomancerClient {
  /**
   * Georeferences a record using the chosen interpreter.
   * 
   * @param record
   *          the Rec
   * @param interpreter
   *          the interpreter
   */
  public List<Georef> georeference(Rec rec, String interpreter);

  /**
   * Sets the service URL for the BioGeomancer web service.
   * 
   * @param serviceUrl
   *          the service URL
   */
  public void setServiceUrl(URL serviceUrl);
}
