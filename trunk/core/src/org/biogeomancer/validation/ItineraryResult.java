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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.gbif.datatester.AssignedTag;
import org.gbif.datatester.ResultManager;
import org.gbif.datatester.Target;
import org.gbif.datatester.tests.ItineraryTest;

/**
 * <p>
 * Wrapper class giving programmer-friendly access to itinerary outlier results.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class ItineraryResult {

  /**
   * <p>
   * Corresponding DataTester ResultManager instance.
   * </p>
   */
  private final ResultManager resultManager;

  /**
   * <p>
   * ValidationManager instance.
   * </p>
   */
  private final ValidationManager validationManager;

  /**
   * <p>
   * Constructor.
   * </p>
   * 
   * @param validationManager
   *          ValidationManager instance.
   * @param resultManager
   *          Related DataTester ResultManager instance.
   */
  public ItineraryResult(ValidationManager validationManager,
      ResultManager resultManager) {

    this.resultManager = resultManager;
    this.validationManager = validationManager;
  }

  /**
   * <p>
   * Returns itinerary outliers related to an original record.
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * 
   * @return Collection of ItineraryOutliers.
   */
  public Collection getOutliers(int recIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex);

    return this.getOutliers(target);
  }

  /**
   * <p>
   * Returns itinerary outliers related to a generated interpretation.
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * @param georefIndex
   *          Index of the interpretation in the original record.
   * 
   * @return Collection of ItineraryOutliers.
   */
  public Collection getOutliers(int recIndex, int georefIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex,
        georefIndex);

    return this.getOutliers(target);
  }

  /**
   * <p>
   * Returns itinerary outliers related to a generic target that could be an
   * original record or a generated interpretation.
   * </p>
   * 
   * 
   * @param target
   *          DataTester Target.
   * 
   * @return Collection of ItineraryOutliers.
   */
  public Collection getOutliers(Target target) {

    Collection relatedTags = this.resultManager.getAssignedTags(target,
        ItineraryTest.ITINERARY_OUTLIER_TAG_ID);

    if (relatedTags.size() == 0) {

      return Collections.EMPTY_LIST;
    }

    Collection results = new ArrayList();

    for (Iterator i = relatedTags.iterator(); i.hasNext();) {

      results.add(new ItineraryOutlier((AssignedTag) i.next(), target));
    }

    return results;
  }
}
