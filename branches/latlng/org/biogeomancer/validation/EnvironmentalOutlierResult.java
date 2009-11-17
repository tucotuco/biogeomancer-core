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
import java.util.Set;

import org.gbif.datatester.Concept;
import org.gbif.datatester.Target;

/**
 * <p>
 * Wrapper class giving programmer-friendly access to environmentl outlier
 * results.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class EnvironmentalOutlierResult {

  /**
   * <p>
   * ValidationManager instance.
   * </p>
   */
  private final ValidationManager validationManager;

  /**
   * <p>
   * Concepts for each environmental variable.
   * </p>
   */
  private final Set<Concept> concepts;

  /**
   * <p>
   * Number of failures (outliers in environmental variables) for a record be
   * considered an outlier.
   * </p>
   */
  private final int threshold;

  /**
   * <p>
   * Constructor.
   * </p>
   * 
   * @param validationManager
   *          ValidationManager instance.
   * @param concepts
   *          Set of concepts related to all environmental variables tested.
   * @param threshold
   *          Number of failures (outliers in environmental variables) for a
   *          record be considered an outlier.
   */
  public EnvironmentalOutlierResult(ValidationManager validationManager,
      Set<Concept> concepts, int threshold) {

    this.validationManager = validationManager;
    this.concepts = concepts;
    this.threshold = threshold;
  }

  /**
   * <p>
   * Returns an EnvironmentalOutlier object related to an original record. If it
   * is not an outlier, then null is returned.
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * 
   * @return EnvironmentalOutlier or null.
   */
  public EnvironmentalOutlier getOutlier(int recIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex);

    return this.getOutlier(target);
  }

  /**
   * <p>
   * Returns an EnvironmentalOutlier object related to a generated interpretation.
   * If it is not an outlier, then null is returned.
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * @param georefIndex
   *          Index of the interpretation in the original record.
   * 
   * @return EnvironmentalOutlier or null.
   */
  public EnvironmentalOutlier getOutlier(int recIndex, int georefIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex,
        georefIndex);

    return this.getOutlier(target);
  }

  /**
   * <p>
   * Returns an EnvironmentalOutlier object related to a DataTester Target (in
   * this case a GenericRecord). If it is not an outlier, then null is returned.
   * </p>
   * 
   * 
   * @param target
   *          DataTester Target.
   * 
   * @return EnvironmentalOutlier or null.
   */
  public EnvironmentalOutlier getOutlier(Target target) {

    Collection failures = new ArrayList();

    double avgOutlierness = 0;

    for (Concept concept : this.concepts) {

      NumericOutlierResult numericOutlierResult = this.validationManager
          .getNumericOutlierResult(concept);

      String scientificName = numericOutlierResult.getScientificName(target);

      if (numericOutlierResult.skippedSpecies(scientificName)) {

        break;
      } else {

        if (numericOutlierResult.isOutlier(scientificName, target)) {

          failures.add(concept.getId());
        }

        avgOutlierness += numericOutlierResult.getOutlierness(scientificName,
            target).doubleValue();
      }
    }

    if (failures.size() >= this.threshold) {

      avgOutlierness /= this.concepts.size();

      return new EnvironmentalOutlier(failures, avgOutlierness);
    }

    return null;
  }
}
