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

/**
 * <p>
 * Individual result for an environmental outlier related to some record.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class EnvironmentalOutlier {

  /**
   * <p>
   * Collection of variables for which the record was considered an outlier.
   * </p>
   */
  private final Collection failures;

  /**
   * <p>
   * Average outlierness for all environmental variables tested.
   * </p>
   */
  private final double averageOutlierness;

  /**
   * <p>
   * Constructor.
   * </p>
   * 
   * @param failures
   *          Collection of variables for which the record was considered an
   *          outlier.
   * @param averageOutlierness
   *          Average outlierness for all environmental variables tested.
   */
  public EnvironmentalOutlier(Collection failures, double averageOutlierness) {

    this.failures = failures;
    this.averageOutlierness = averageOutlierness;
  }

  /**
   * <p>
   * Returns the average outlierness for all environmental variables tested.
   * </p>
   * 
   * 
   * @return Average outlierness.
   */
  public double getAverageOutlierness() {

    return this.averageOutlierness;
  }

  /**
   * <p>
   * Returns a Collection of variables for which the record was considered an
   * outlier.
   * </p>
   * 
   * 
   * @return Collection of variables (String identifiers) for which the record was
   *         considered an outlier.
   */
  public Collection getOutlierVariables() {

    return this.failures;
  }
}
