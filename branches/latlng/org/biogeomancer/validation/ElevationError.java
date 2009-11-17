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

import java.util.Collection;
import java.util.Iterator;

import org.gbif.datatester.AssignedTag;
import org.gbif.datatester.Tag;
import org.gbif.datatester.tests.ElevationErrorTest;

/**
 * <p>
 * Wrapper class giving programmer-friendly access to a specific elevation
 * error.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class ElevationError {

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
  public ElevationError(AssignedTag assignedTag) {

    this.assignedTag = assignedTag;
  }

  /**
   * <p>
   * Returns a value associated with an additional tag.
   * </p>
   * 
   * 
   * @param tagId
   *          Tag identifier as a static value.
   * @return Value.
   */
  public Object getAdditionalTagValue(String tagId) {

    Collection additionalTags = this.assignedTag.getAdditionalTags();

    for (Iterator i = additionalTags.iterator(); i.hasNext();) {

      AssignedTag additionalTag = (AssignedTag) i.next();
      Tag tag = additionalTag.getTag();

      if (tag.getId() == tagId) {

        return additionalTag.getValue();
      }
    }

    return null;
  }

  /**
   * <p>
   * Returns the elevation according to a DEM.
   * </p>
   * 
   * 
   * @return Calculated elevation.
   */
  public Double getCalculatedElevation() {

    return (Double) this
        .getAdditionalTagValue(ElevationErrorTest.CALCULATED_ELEVATION_TAG_ID);
  }

  /**
   * <p>
   * Returns the calculated maximum elevation.
   * </p>
   * 
   * 
   * @return Calculated maximum elevation.
   */
  public Double getCalculatedMaximumElevation() {

    return (Double) this
        .getAdditionalTagValue(ElevationErrorTest.CALCULATED_MAXIMUM_ELEVATION_TAG_ID);
  }

  /**
   * <p>
   * Returns the calculated minimum elevation.
   * </p>
   * 
   * 
   * @return Calculated minimum elevation.
   */
  public Double getCalculatedMinimumElevation() {

    return (Double) this
        .getAdditionalTagValue(ElevationErrorTest.CALCULATED_MINIMUM_ELEVATION_TAG_ID);
  }

}
