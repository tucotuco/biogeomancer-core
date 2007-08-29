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
import org.gbif.datatester.tests.ElevationErrorTest;

/**
 * <p>
 * Wrapper class giving programmer-friendly access to elevation error results.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class ElevationErrorResult {

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
  public ElevationErrorResult(ValidationManager validationManager,
      ResultManager resultManager) {

    this.resultManager = resultManager;
    this.validationManager = validationManager;
  }

  /**
   * <p>
   * Returns the associated elevation error of an original record, if there was
   * any error detected.
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * 
   * @return ElevationError object. Can be null if no error was detected.
   */
  public ElevationError getError(int recIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex);

    return this.getError(target);
  }

  /**
   * <p>
   * Returns the associated elevation error of a generated interpretation, if
   * there was any error detected.
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * @param georefIndex
   *          Index of the interpretation in the original record.
   * 
   * @return ElevationError object. Can be null if no error was detected.
   */
  public ElevationError getError(int recIndex, int georefIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex,
        georefIndex);

    return this.getError(target);
  }

  /**
   * <p>
   * Returns the associated elevation error of a generic target, if there was
   * any error detected.
   * </p>
   * 
   * 
   * @param target
   *          DataTester Target.
   * 
   * @return ElevationError object. Can be null if no error was detected.
   */
  public ElevationError getError(Target target) {

    AssignedTag assignedTag = this.validationManager.getFirstAssignedTag(
        this.resultManager, target, ElevationErrorTest.ELEVATION_ERROR_TAG_ID);

    if (assignedTag != null) {

      return new ElevationError(assignedTag);
    }

    return null;
  }
}
