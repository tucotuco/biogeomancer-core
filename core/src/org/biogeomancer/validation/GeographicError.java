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
import org.gbif.datatester.tests.GeographicErrorTest;

/**
 * <p>Wrapper class giving programmer-friendly access to a specific geographic error.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class GeographicError {

/**
 * <p>Related assigned tag.</p>
 */
    private AssignedTag assignedTag;


/**
 * <p>Constructor.</p>
 *
 * @param assignedTag Related DataTester AssignedTag object.
 */
    public GeographicError( AssignedTag assignedTag ) {

        this.assignedTag = assignedTag;
    }

/**
 * <p>Returns the country feature name for the given coordinates.</p>
 *
 *
 * @return Country feature name.
 */
    public String getCountryFeature() {

        return (String)this.getAdditionalTagValue( GeographicErrorTest.MAPPED_FEATURE_TAG_ID, BGConcepts.COUNTRY );
    }

/**
 * <p>Returns the state/province feature name for the given coordinates.</p>
 *
 *
 * @return State/province feature name.
 */
    public String getStateProvinceFeature() {

        return (String)this.getAdditionalTagValue( GeographicErrorTest.MAPPED_FEATURE_TAG_ID, BGConcepts.STATE_PROVINCE );
    }

/**
 * <p>Returns the county feature name for the given coordinates.</p>
 *
 *
 * @return County feature name.
 */
    public String getCountyFeature() {

        return (String)this.getAdditionalTagValue( GeographicErrorTest.MAPPED_FEATURE_TAG_ID, BGConcepts.COUNTY );
    }

/**
 * <p>Returns the shortest distance between the point and the original administrative region provided by the record.</p>
 *
 *
 * @return Shortest distance (in kilometers) between the point and the original administrative region provided by the record.
 */
    public Double getDistance( Concept concept ) {

        return (Double)this.getAdditionalTagValue( GeographicErrorTest.DISTANCE_TAG_ID, concept );
    }

/**
 * <p>Returns a value associated with an additional tag given a specific tag id and
 * a specific concept.</p>
 *
 *
 * @param tagId Tag identifier as a static value.
 * @param concept Related concept.
 * @return Value.
 */
    public Object getAdditionalTagValue( String tagId, Concept concept ) {

        Collection additionalTags = this.assignedTag.getAdditionalTags();

        for ( Iterator i = additionalTags.iterator(); i.hasNext(); ) {

            AssignedTag additionalTag = (AssignedTag) i.next();
            Tag tag = additionalTag.getTag();
            Concept[] concepts = ((Target)additionalTag.getTarget()).getConcepts();
            
            if ( tag.getId() == tagId && Arrays.binarySearch( concepts, concept ) >= 0 ) {

                return additionalTag.getValue();
            }
        }

        return null;
    }

/**
 * <p>Indicates if there's an inconsistency between country and coordinates.</p>
 *
 *
 * @return True if there's an inconsistency between country and coordinates, false otherwise.
 */
    public boolean hasErrorInCountry() {

        return ( this.getCountryFeature() == null ) ? false : true;
    }

/**
 * <p>Indicates if there's an inconsistency between state/province and coordinates.</p>
 *
 *
 * @return True if there's an inconsistency between state/province and coordinates, false otherwise.
 */
    public boolean hasErrorInStateProvince() {

        return ( this.getStateProvinceFeature() == null ) ? false : true;
    }

/**
 * <p>Indicates if there's an inconsistency between county and coordinates.</p>
 *
 *
 * @return True if there's an inconsistency between county and coordinates, false otherwise.
 */
    public boolean hasErrorInCounty() {

        return ( this.getCountyFeature() == null ) ? false : true;
    }

}
