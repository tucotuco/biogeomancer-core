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
import org.gbif.datatester.GenericRecord;
import org.gbif.datatester.Tag;
import org.gbif.datatester.Target;
import org.gbif.datatester.exception.DataTesterException;
import org.gbif.datatester.tests.ItineraryTest;

/**
 * <p>
 * Wrapper class giving programmer-friendly access to itinerary outlier results
 * related to a specific record.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class ItineraryOutlier {

  /**
   * <p>
   * Related assigned tag.
   * </p>
   */
  private final AssignedTag assignedTag;

  /**
   * <p>
   * Target related to the record.
   * </p>
   */
  private final Target target;

  /**
   * <p>
   * Constructor.
   * </p>
   * 
   * @param assignedTag
   *          Related DataTester AssignedTag object.
   * @param target
   *          Target related to the record.
   */
  public ItineraryOutlier(AssignedTag assignedTag, Target target) {

    this.assignedTag = assignedTag;
    this.target = target;
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
   * Returns the calculated distance.
   * </p>
   * 
   * 
   * @return Calculated distance between two points.
   */
  public double getCalculatedDistance() {

    Object value = this
        .getAdditionalTagValue(ItineraryTest.CALCULATED_DISTANCE_TAG_ID);

    if (value != null) {

      return ((Double) value).intValue();
    }

    return 0;
  }

  /**
   * <p>
   * Returns the date.
   * </p>
   * 
   * 
   * @return Earliest collecting date.
   * 
   * @throws DataTesterException
   */
  public String getDate() throws DataTesterException {

    GenericRecord[] records = this.target.getRecords();

    return records[0].getStringValue(BGConcepts.EARLIEST_COLLECTING_DATE);
  }

  /**
   * <p>
   * Returns the maximum distance.
   * </p>
   * 
   * 
   * @return Maximum distance.
   */
  public double getMaximumDistance() {

    Object value = this
        .getAdditionalTagValue(ItineraryTest.MAXIMUM_DISTANCE_TAG_ID);

    if (value != null) {

      return ((Double) value).intValue();
    }

    return 0;
  }

  /**
   * <p>
   * Returns the number of days.
   * </p>
   * 
   * 
   * @return Number of days.
   */
  public int getNumberOfDays() {

    Object value = this
        .getAdditionalTagValue(ItineraryTest.NUMBER_OF_DAYS_TAG_ID);

    if (value != null) {

      return ((Integer) value).intValue();
    }

    return 0;
  }

  /**
   * <p>
   * Returns the other record id.
   * </p>
   * 
   * 
   * @return Other record id.
   * 
   * @throws DataTesterException
   */
  public String getOtherRecordId() throws DataTesterException {

    Target fullTarget = this.assignedTag.getTarget();

    GenericRecord[] records = fullTarget.getRecords(); // contains two records

    GenericRecord[] record = this.target.getRecords(); // contains one record

    if (records[0].equals(record[0])) {

      return records[1].getStringValue(BGConcepts.ID);
    } else {

      return records[0].getStringValue(BGConcepts.ID);
    }
  }
}
