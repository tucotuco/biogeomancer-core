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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.biogeomancer.records.Georef;

/**
 * Interface for the BioGeomancer georeferencing webservice.
 * 
 */
public interface BioGeomancerClient {

  /**
   * Batch georeferences the file using the BioGeomancer web service.
   * 
   * @param file tab delineated with a DarwinCore header
   * @return List<Georef> of georeferences
   */
  public List<Georef> georeference(String filename, String interpreter);

  /**
   * Batch georeferences an XML request using the BioGeomancer web service.
   * 
   * @param xmlRequest the georeference XML request
   * @return List<Georef> of georeferences
   */
  public List<Georef> georeference(String xmlRequest);

  /**
   * Georeferences a single locality.
   * 
   * @param locality the DwC Locality
   * @param higherGeography the DwC HigherGeography
   * @param interpreter the locality interpreter (defaults to Yale)
   * @return the Georef
   */
  public List<Georef> georeference(String locality, String higherGeography,
      String interpreter);

  /**
   * Sets the service URL for the BioGeomancer web service.
   * 
   * @param serviceUrl the service URL 
   */
  public void setServiceUrl(URL serviceUrl);
}
