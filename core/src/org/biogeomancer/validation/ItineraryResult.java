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
import org.gbif.datatester.tests.*;

/**
 * <p>Wrapper class giving programmer-friendly access to itinerary outlier results.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class ItineraryResult {

/**
 * <p>Corresponding DataTester ResultManager instance.</p>
 */
    private ResultManager resultManager;

/**
 * <p>ValidationManager instance.</p>
 */
    private ValidationManager validationManager;

/**
 * <p>Constructor.</p>
 *
 * @param validationManager ValidationManager instance.
 * @param resultManager Related DataTester ResultManager instance.
 */
    public ItineraryResult( ValidationManager validationManager, ResultManager resultManager ) {

        this.resultManager = resultManager;
        this.validationManager = validationManager;
    }

/**
 * <p>Returns itinerary outliers related to a generated interpretation.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 * @param georefIndex Index of the interpretation in the original record.
 *
 * @return Collection of ItineraryOutliers.
 */
    public Collection getOutliers( int recIndex, int georefIndex ) {

        Target target = this.validationManager.getDataTesterTarget( recIndex, georefIndex );

        return this.getOutliers( target );
    }

/**
 * <p>Returns itinerary outliers related to an original record.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 *
 * @return Collection of ItineraryOutliers.
 */
    public Collection getOutliers( int recIndex ) {

        Target target = this.validationManager.getDataTesterTarget( recIndex );

        return this.getOutliers( target );
    }

/**
 * <p>Returns itinerary outliers related to a generic target that could be an original record
 * or a generated interpretation.</p>
 *
 *
 * @param target DataTester Target.
 *
 * @return Collection of ItineraryOutliers.
 */
    public Collection getOutliers( Target target ) {

        Collection relatedTags = this.resultManager.getAssignedTags( target, ItineraryTest.ITINERARY_OUTLIER_TAG_ID );

        if ( relatedTags.size() == 0 ) {

            return Collections.EMPTY_LIST;
        }

        Collection results = new ArrayList();

        for ( Iterator i = relatedTags.iterator(); i.hasNext(); ) {

            results.add( new ItineraryOutlier( (AssignedTag) i.next(), target ) );
        }

        return results;
    }
}
