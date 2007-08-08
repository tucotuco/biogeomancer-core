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
import org.gbif.datatester.tests.ElevationErrorTest;

/**
 * <p>Wrapper class giving programmer-friendly access to a specific elevation error.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class ElevationError {

/**
 * <p>Related assigned tag.</p>
 */
    private AssignedTag assignedTag;


/**
 * <p>Constructor.</p>
 *
 * @param assignedTag Related DataTester AssignedTag object.
 */
    public ElevationError( AssignedTag assignedTag ) {

        this.assignedTag = assignedTag;
    }

/**
 * <p>Returns the elevation according to a DEM.</p>
 *
 *
 * @return Calculated elevation.
 */
    public Double getCalculatedElevation() {

        return (Double)this.getAdditionalTagValue( ElevationErrorTest.CALCULATED_ELEVATION_TAG_ID );
    }

/**
 * <p>Returns the calculated maximum elevation.</p>
 *
 *
 * @return Calculated maximum elevation.
 */
    public Double getCalculatedMaximumElevation() {

        return (Double)this.getAdditionalTagValue( ElevationErrorTest.CALCULATED_MAXIMUM_ELEVATION_TAG_ID );
    }

/**
 * <p>Returns the calculated minimum elevation.</p>
 *
 *
 * @return Calculated minimum elevation.
 */
    public Double getCalculatedMinimumElevation() {

        return (Double)this.getAdditionalTagValue( ElevationErrorTest.CALCULATED_MINIMUM_ELEVATION_TAG_ID );
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

}
