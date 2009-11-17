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

package org.biogeomancer.validation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.gbif.datatester.AssignedTag;
import org.gbif.datatester.Concept;
import org.gbif.datatester.Tag;
import org.gbif.datatester.tests.GeographicErrorTest;

/**
 * <p>
 * Wrapper class giving programmer-friendly access to a specific geographic
 * error.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class GeographicError {

  /**
   * <p>
   * Related assigned tag.
   * </p>
   */
  private final AssignedTag assignedTag;

  /**
   * <p>
   * Constructor.
   * </p>
   * 
   * @param assignedTag
   *          Related DataTester AssignedTag object.
   */
  public GeographicError(AssignedTag assignedTag) {

    this.assignedTag = assignedTag;
  }

  /**
   * <p>
   * Returns a value associated with an additional tag given a specific tag id
   * and a specific concept.
   * </p>
   * 
   * 
   * @param tagId
   *          Tag identifier as a static value.
   * @param concept
   *          Related concept.
   * @return Value.
   */
  public Object getAdditionalTagValue(String tagId, Concept concept) {

    Collection additionalTags = this.assignedTag.getAdditionalTags();

    for (Iterator i = additionalTags.iterator(); i.hasNext();) {

      AssignedTag additionalTag = (AssignedTag) i.next();
      Tag tag = additionalTag.getTag();
      Concept[] concepts = (additionalTag.getTarget()).getConcepts();

      if (tag.getId() == tagId && Arrays.binarySearch(concepts, concept) >= 0) {

        return additionalTag.getValue();
      }
    }

    return null;
  }

  /**
   * <p>
   * Returns the country feature name for the given coordinates.
   * </p>
   * 
   * 
   * @return Country feature name.
   */
  public String getCountryFeature() {

    return (String) this.getAdditionalTagValue(
        GeographicErrorTest.MAPPED_FEATURE_TAG_ID, BGConcepts.COUNTRY);
  }

  /**
   * <p>
   * Returns the county feature name for the given coordinates.
   * </p>
   * 
   * 
   * @return County feature name.
   */
  public String getCountyFeature() {

    return (String) this.getAdditionalTagValue(
        GeographicErrorTest.MAPPED_FEATURE_TAG_ID, BGConcepts.COUNTY);
  }

  /**
   * <p>
   * Returns the shortest distance between the point and the original
   * administrative region provided by the record.
   * </p>
   * 
   * 
   * @return Shortest distance (in kilometers) between the point and the
   *         original administrative region provided by the record.
   */
  public Double getDistance(Concept concept) {

    return (Double) this.getAdditionalTagValue(
        GeographicErrorTest.DISTANCE_TAG_ID, concept);
  }

  /**
   * <p>
   * Returns the state/province feature name for the given coordinates.
   * </p>
   * 
   * 
   * @return State/province feature name.
   */
  public String getStateProvinceFeature() {

    return (String) this.getAdditionalTagValue(
        GeographicErrorTest.MAPPED_FEATURE_TAG_ID, BGConcepts.STATE_PROVINCE);
  }

  /**
   * <p>
   * Indicates if there's an inconsistency between country and coordinates.
   * </p>
   * 
   * 
   * @return True if there's an inconsistency between country and coordinates,
   *         false otherwise.
   */
  public boolean hasErrorInCountry() {

    return (this.getCountryFeature() == null) ? false : true;
  }

  /**
   * <p>
   * Indicates if there's an inconsistency between county and coordinates.
   * </p>
   * 
   * 
   * @return True if there's an inconsistency between county and coordinates,
   *         false otherwise.
   */
  public boolean hasErrorInCounty() {

    return (this.getCountyFeature() == null) ? false : true;
  }

  /**
   * <p>
   * Indicates if there's an inconsistency between state/province and
   * coordinates.
   * </p>
   * 
   * 
   * @return True if there's an inconsistency between state/province and
   *         coordinates, false otherwise.
   */
  public boolean hasErrorInStateProvince() {

    return (this.getStateProvinceFeature() == null) ? false : true;
  }

}
