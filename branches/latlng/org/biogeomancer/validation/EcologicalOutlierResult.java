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

import org.gbif.datatester.AssignedTag;
import org.gbif.datatester.ResultManager;
import org.gbif.datatester.Target;
import org.gbif.datatester.tests.ProbabilityProxyTest;

/**
 * <p>
 * Wrapper class giving programmer-friendly access to ecological outlier
 * results.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class EcologicalOutlierResult {

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
  public EcologicalOutlierResult(ValidationManager validationManager,
      ResultManager resultManager) {

    this.resultManager = resultManager;
    this.validationManager = validationManager;
  }

  /**
   * <p>
   * Returns the probability of presence associated with an ecological outlier.
   * If it's not an outlier or if the test was not performed, then null is
   * returned.
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * @return Probability of presence according to a distribution model. Can be
   *         null if it's not an outlier or if the test was not performed.
   */
  public Double getProbability(int recIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex);

    return this.getProbability(target);
  }

  /**
   * <p>
   * Returns the probability of presence associated with an ecological outlier.
   * If it's not an outlier or if the test was not performed, then null is
   * returned.
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * @param georefIndex
   *          Index of the interpretation in the original record.
   * 
   * @return Probability of presence according to a distribution model. Can be
   *         null if it's not an outlier or if the test was not performed.
   */
  public Double getProbability(int recIndex, int georefIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex,
        georefIndex);

    return this.getProbability(target);
  }

  /**
   * <p>
   * Returns the probability of presence associated with an ecological outlier
   * given the respective DataTester Target (in this case a GenericRecord). If
   * it's not an outlier or if the test was not performed, then null is
   * returned.
   * </p>
   * 
   * 
   * @param target
   *          DataTester Target object.
   * 
   * @return Probability of presence according to a distribution model. Can be
   *         null if it's not an outlier or if the test was not performed.
   */
  public Double getProbability(Target target) {

    AssignedTag assignedTag = this.validationManager
        .getFirstAssignedTag(this.resultManager, target,
            ProbabilityProxyTest.LOW_PROBABILITY_TAG_ID);

    if (assignedTag != null) {

      // There should be only one tag of this type
      return (Double) assignedTag.getValue();
    }

    return null;
  }

  /**
   * <p>
   * Indicates if an original record is an ecological outlier. False can also
   * mean that the test could not be performed for some reason (for instance
   * when there's no distribution map for the species).
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * 
   * @return True if the record is an outlier, false otherwise.
   */
  public boolean isOutlier(int recIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex);

    return this.isOutlier(target);
  }

  /**
   * <p>
   * Indicates if a generated interpretation is an ecological outlier. False can
   * also mean that the test could not be performed for some reason (for
   * instance when there's no distribution map for the species).
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * @param georefIndex
   *          Index of the interpretation in the original record.
   * 
   * @return True if the record is an outlier, false otherwise.
   */
  public boolean isOutlier(int recIndex, int georefIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex,
        georefIndex);

    return this.isOutlier(target);
  }

  /**
   * <p>
   * Indicates if a specific DataTester Target (in this case a GenericRecord) is
   * an ecological outlier. False can also mean that the test could not be
   * performed for some reason (for instance when there's no distribution map
   * for the species).
   * </p>
   * 
   * 
   * @param target
   *          DataTester Target object.
   * 
   * @return True if the record is an outlier, false otherwise.
   */
  public boolean isOutlier(Target target) {

    return this.resultManager.isTagged(target,
        ProbabilityProxyTest.LOW_PROBABILITY_TAG_ID);
  }
}
