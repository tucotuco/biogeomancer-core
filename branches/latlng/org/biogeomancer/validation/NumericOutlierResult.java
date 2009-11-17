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

import java.util.HashMap;
import java.util.Map;

import org.gbif.datatester.AssignedTag;
import org.gbif.datatester.GenericRecord;
import org.gbif.datatester.ResultManager;
import org.gbif.datatester.Target;
import org.gbif.datatester.exception.DataTesterException;
import org.gbif.datatester.tests.NumericOutlierTest;

/**
 * <p>
 * Wrapper class giving programmer-friendly access to numeric outlier results.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class NumericOutlierResult {

  /**
   * <p>
   * Map storing a DataTester ResultManager for each part of the record set that
   * was separately tested.
   * </p>
   */
  private final Map<String, ResultManager> results = new HashMap();

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
   */
  public NumericOutlierResult(ValidationManager validationManager) {

    this.validationManager = validationManager;
  }

  /**
   * <p>
   * Adds a new result related to part of the record set.
   * </p>
   * 
   * @param scientificName
   *          Scientific name related to the part of the record set.
   * @param resultManager
   *          Related DataTester ResultManager instance.
   */
  public void addResult(String scientificName, ResultManager resultManager) {

    this.results.put(scientificName, resultManager);
  }

  /**
   * <p>
   * Returns the maximum value of all records, or null if for some reason it was
   * not calculated.
   * </p>
   * 
   * @param scientificName
   *          Scientific name related to the part of the record set.
   * 
   * @return Maximum value or null.
   */
  public Double getMaximumValue(String scientificName) {

    if (this.results.containsKey(scientificName)) {

      ResultManager resultManager = this.results.get(scientificName);

      AssignedTag assignedTag = this.validationManager.getFirstAssignedTag(
          resultManager, NumericOutlierTest.MAXIMUMACCEPTABLEVALUE_TAG_ID);

      if (assignedTag != null) {

        // There should be only one tag of this type
        return (Double) assignedTag.getValue();
      }
    }

    return null;
  }

  /**
   * <p>
   * Returns the minimum value of all records, or null if for some reason it was
   * not calculated.
   * </p>
   * 
   * @param scientificName
   *          Scientific name related to the part of the record set.
   * 
   * @return Minimum value or null.
   */
  public Double getMinimumValue(String scientificName) {

    if (this.results.containsKey(scientificName)) {

      ResultManager resultManager = this.results.get(scientificName);

      AssignedTag assignedTag = this.validationManager.getFirstAssignedTag(
          resultManager, NumericOutlierTest.MINIMUMACCEPTABLEVALUE_TAG_ID);

      if (assignedTag != null) {

        // There should be only one tag of this type
        return (Double) assignedTag.getValue();
      }
    }

    return null;
  }

  /**
   * <p>
   * Returns the outlierness associated with an original record, or null if for
   * some reason it was not calculated. Note: if the species was not tested,
   * this method will also return false! You can always use method
   * "skippedSpecies" to know if the species was tested or not.
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * @return outlierness value or null.
   */
  public Double getOutlierness(int recIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex);

    String scientificName = this.getScientificName(target);

    return this.getOutlierness(scientificName, target);
  }

  /**
   * <p>
   * Returns the outlierness associated with a generated interpretation, or null
   * if for some reason it was not calculated. Note: if the species was not
   * tested, this method will also return false! You can always use method
   * "skippedSpecies" to know if the species was tested or not.
   * </p>
   * 
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * @param georefIndex
   *          Index of the interpretation in the original record.
   * 
   * @return outlierness value or null.
   */
  public Double getOutlierness(int recIndex, int georefIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex,
        georefIndex);

    String scientificName = this.getScientificName(target);

    return this.getOutlierness(scientificName, target);
  }

  /**
   * <p>
   * Returns the outlierness associated with a DataTester Target (in this case a
   * GenericRecord), or null if for some reason it was not calculated.
   * </p>
   * 
   * 
   * @param scientificName
   *          Scientific name related to the part of the record set.
   * @param target
   *          DataTester Target object.
   * 
   * @return outlierness value.
   */
  public Double getOutlierness(String scientificName, Target target) {

    if (this.results.containsKey(scientificName)) {

      ResultManager resultManager = this.results.get(scientificName);

      AssignedTag assignedTag = this.validationManager.getFirstAssignedTag(
          resultManager, target, NumericOutlierTest.OUTLIERNESS_TAG_ID);

      if (assignedTag != null) {

        // There should be only one tag of this type
        return (Double) assignedTag.getValue();
      }
    }

    return null;
  }

  /**
   * <p>
   * Returns the scientific name associated with a target record.
   * </p>
   * 
   * 
   * @param target
   *          Target object related to an original record or an interpretation.
   * 
   * @return Scientific name.
   */
  public String getScientificName(Target target) {

    GenericRecord genericRecord = (target.getRecords())[0];

    String scientificName = "";

    try {

      scientificName = genericRecord.getStringValue(BGConcepts.SCIENTIFIC_NAME);
    } catch (DataTesterException exception) {
    }

    return scientificName;
  }

  /**
   * <p>
   * Indicates if an original record is an outlier. Note: if for some reason the
   * species was not tested, this method will also return false! You can always
   * use method "skippedSpecies" to know if the species was tested or not.
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

    String scientificName = this.getScientificName(target);

    return this.isOutlier(scientificName, target);
  }

  /**
   * <p>
   * Indicates if a generated interpretation is an outlier. Note: if for some
   * reason the species was not tested, this method will also return false! You
   * can always use method "skippedSpecies" to know if the species was tested or
   * not.
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

    String scientificName = this.getScientificName(target);

    return this.isOutlier(scientificName, target);
  }

  /**
   * <p>
   * Indicates if a specific DataTester Target (in this case a GenericRecord) is
   * an outlier. If for some reason the species was not tested, this method will
   * also return false.
   * </p>
   * 
   * 
   * @param scientificName
   *          Scientific name related to the part of the record set.
   * @param target
   *          DataTester Target object.
   * 
   * @return True if the record is an outlier, false otherwise.
   */
  public boolean isOutlier(String scientificName, Target target) {

    if (this.results.containsKey(scientificName)) {

      ResultManager resultManager = this.results.get(scientificName);

      return resultManager.isTagged(target, NumericOutlierTest.OUTLIER_TAG_ID);
    } else {

      return false;
    }
  }

  /**
   * <p>
   * Indicates if the original record was skipped from the test because the
   * number of records related to the species was not big enough.
   * </p>
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * 
   * @return True is records from this species were not tested, false otherwise.
   */
  public boolean skippedRecord(int recIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex);

    String scientificName = this.getScientificName(target);

    return this.skippedSpecies(scientificName);
  }

  /**
   * <p>
   * Indicates if the interpretation was skipped from the test (because the
   * number of records related to the species was not big enough.
   * </p>
   * 
   * @param recIndex
   *          Index of the original record in the record set.
   * @param georefIndex
   *          Index of the interpretation in the original record.
   * 
   * @return True is records from this species were not tested, false otherwise.
   */
  public boolean skippedRecord(int recIndex, int georefIndex) {

    Target target = this.validationManager.getDataTesterTarget(recIndex,
        georefIndex);

    String scientificName = this.getScientificName(target);

    return this.skippedSpecies(scientificName);
  }

  /**
   * <p>
   * Indicates if the respective species was skipped from the test (because the
   * number of records was not big enough). It will also return false if the
   * species was not even present in the original record set.
   * </p>
   * 
   * @param scientificName
   *          Scientific name related to the part of the record set.
   * 
   * @return True is records from this species were not tested, false otherwise.
   */
  public boolean skippedSpecies(String scientificName) {

    return (this.results.containsKey(scientificName)) ? false : true;
  }
}
