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
import org.gbif.datatester.tests.ProbabilityProxyTest;

/**
 * <p>Wrapper class giving programmer-friendly access to ecological outlier results.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class EcologicalOutlierResult {

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
    public EcologicalOutlierResult( ValidationManager validationManager, ResultManager resultManager ) {

        this.resultManager = resultManager;
        this.validationManager = validationManager;
    }

/**
 * <p>Indicates if a generated interpretation is an ecological outlier. False
 * can also mean that the test could not be performed for some reason
 * (for instance when there's no distribution map for the species).</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 * @param georefIndex Index of the interpretation in the original record.
 *
 * @return True if the record is an outlier, false otherwise.
 */
    public boolean isOutlier( int recIndex, int georefIndex ) {

        Target target = this.validationManager.getDataTesterTarget( recIndex, georefIndex );

        return this.isOutlier( target );
    }

/**
 * <p>Indicates if an original record is an ecological outlier. False
 * can also mean that the test could not be performed for some reason
 * (for instance when there's no distribution map for the species).</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 *
 * @return True if the record is an outlier, false otherwise.
 */
    public boolean isOutlier( int recIndex ) {

        Target target = this.validationManager.getDataTesterTarget( recIndex );

        return this.isOutlier( target );
    }

/**
 * <p>Indicates if a specific DataTester Target (in this case a GenericRecord) is 
 * an ecological outlier. False can also mean that the test could not be performed 
 * for some reason (for instance when there's no distribution map for the species).</p>
 *
 *
 * @param target DataTester Target object.
 *
 * @return True if the record is an outlier, false otherwise.
 */
    public boolean isOutlier( Target target ) {

        return this.resultManager.isTagged( target, ProbabilityProxyTest.LOW_PROBABILITY_TAG_ID );
    }

/**
 * <p>Returns the probability of presence associated with an ecological outlier.
 * If it's not an outlier or if the test was not performed, then null is returned.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 * @param georefIndex Index of the interpretation in the original record.
 *
 * @return Probability of presence according to a distribution model. Can be null 
 *         if it's not an outlier or if the test was not performed.
 */
    public Double getProbability( int recIndex, int georefIndex ) {

        Target target = this.validationManager.getDataTesterTarget( recIndex, georefIndex );

        return this.getProbability( target );
    }

/**
 * <p>Returns the probability of presence associated with an ecological outlier.
 * If it's not an outlier or if the test was not performed, then null is returned.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 * @return Probability of presence according to a distribution model. Can be null 
 *         if it's not an outlier or if the test was not performed.
 */
    public Double getProbability( int recIndex ) {

        Target target = this.validationManager.getDataTesterTarget( recIndex );

        return this.getProbability( target );
    }

/**
 * <p>Returns the probability of presence associated with an ecological outlier
 * given the respective DataTester Target (in this case a GenericRecord).
 * If it's not an outlier or if the test was not performed, then null is returned.</p>
 *
 *
 * @param target DataTester Target object.
 *
 * @return Probability of presence according to a distribution model. Can be null 
 *         if it's not an outlier or if the test was not performed.
 */
    public Double getProbability( Target target ) {

        AssignedTag assignedTag = this.validationManager.getFirstAssignedTag( this.resultManager, target, ProbabilityProxyTest.LOW_PROBABILITY_TAG_ID );

        if ( assignedTag != null ) {

            // There should be only one tag of this type
	    return (Double)assignedTag.getValue();
        }

        return null;
    }
}
