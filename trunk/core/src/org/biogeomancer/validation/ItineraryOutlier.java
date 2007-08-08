/* 
 * This file is part of BioGeomancer.
 *
 *  Biogeomancer is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  any later version.
 *
 *  Biogeomancer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Biogeomancer; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.biogeomancer.validation;

import java.util.*;

import org.gbif.datatester.*;
import org.gbif.datatester.exception.*;
import org.gbif.datatester.tests.ItineraryTest;

/**
 * <p>Wrapper class giving programmer-friendly access to itinerary outlier results related
 * to a specific record.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class ItineraryOutlier {

/**
 * <p>Related assigned tag.</p>
 */
    private AssignedTag assignedTag;

/**
 * <p>Target related to the record.</p>
 */
    private Target target;

/**
 * <p>Constructor.</p>
 *
 * @param assignedTag Related DataTester AssignedTag object.
 * @param target Target related to the record.
 */
    public ItineraryOutlier( AssignedTag assignedTag, Target target ) {

        this.assignedTag = assignedTag;
        this.target = target;
    }

/**
 * <p>Returns the calculated distance.</p>
 *
 *
 * @return Calculated distance between two points.
 */
    public double getCalculatedDistance() {

        Object value = this.getAdditionalTagValue( ItineraryTest.CALCULATED_DISTANCE_TAG_ID );

        if ( value != null ) {

            return ((Double)value).intValue();
        }

        return 0;
    }

/**
 * <p>Returns the maximum distance.</p>
 *
 *
 * @return Maximum distance.
 */
    public double getMaximumDistance() {

        Object value = this.getAdditionalTagValue( ItineraryTest.MAXIMUM_DISTANCE_TAG_ID );

        if ( value != null ) {

            return ((Double)value).intValue();
        }

        return 0;
    }

/**
 * <p>Returns the number of days.</p>
 *
 *
 * @return Number of days.
 */
    public int getNumberOfDays() {

        Object value = this.getAdditionalTagValue( ItineraryTest.NUMBER_OF_DAYS_TAG_ID );

        if ( value != null ) {

            return ((Integer)value).intValue();
        }

        return 0;
    }

/**
 * <p>Returns a value associated with an additional tag.</p>
 *
 *
 * @param tagId Tag identifier as a static value.
 * @return Value.
 */
    public Object getAdditionalTagValue( String tagId ) {

        Collection additionalTags = this.assignedTag.getAdditionalTags();

        for ( Iterator i = additionalTags.iterator(); i.hasNext(); ) {

            AssignedTag additionalTag = (AssignedTag) i.next();
            Tag tag = additionalTag.getTag();
            
            if ( tag.getId() == tagId ) {

                return additionalTag.getValue();
            }
        }

        return null;
    }

/**
 * <p>Returns the date.</p>
 *
 *
 * @return Earliest collecting date.
 *
 * @throws DataTesterException
 */
    public String getDate() throws DataTesterException {

        GenericRecord[] records = this.target.getRecords();

        return records[0].getStringValue( BGConcepts.EARLIEST_COLLECTING_DATE );
    }

/**
 * <p>Returns the other record id.</p>
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

        if ( records[0].equals( record[0] ) ) {

            return records[1].getStringValue( BGConcepts.ID );
        }
        else {

            return records[0].getStringValue( BGConcepts.ID );
        }
    }
}
