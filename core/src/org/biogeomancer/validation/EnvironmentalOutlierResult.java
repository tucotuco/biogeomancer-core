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


/**
 * <p>Wrapper class giving programmer-friendly access to environmentl outlier results.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class EnvironmentalOutlierResult {

/**
 * <p>ValidationManager instance.</p>
 */
    private ValidationManager validationManager;

/**
 * <p>Concepts for each environmental variable.</p>
 */
    private Set<Concept> concepts;

/**
 * <p>Number of failures (outliers in environmental variables) for a record be considered an outlier.</p>
 */
    private int threshold;


/**
 * <p>Constructor.</p>
 *
 * @param validationManager ValidationManager instance.
 * @param concepts Set of concepts related to all environmental variables tested.
 * @param threshold Number of failures (outliers in environmental variables) for a record be considered an outlier.
 */
    public EnvironmentalOutlierResult( ValidationManager validationManager, Set<Concept> concepts, int threshold ) {

        this.validationManager = validationManager;
        this.concepts = concepts;
        this.threshold = threshold;
    }

/**
 * <p>Returns an EnvironmentalOutlier object related to a generated interpretation. If it
 * is not an outlier, then null is returned.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 * @param georefIndex Index of the interpretation in the original record.
 *
 * @return  EnvironmentalOutlier or null.
 */
    public EnvironmentalOutlier getOutlier( int recIndex, int georefIndex ) {

        Target target = this.validationManager.getDataTesterTarget( recIndex, georefIndex );

        return this.getOutlier( target );
    }

/**
 * <p>Returns an EnvironmentalOutlier object related to an original record. If it
 * is not an outlier, then null is returned.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 *
 * @return EnvironmentalOutlier or null.
 */
    public EnvironmentalOutlier getOutlier( int recIndex ) {

        Target target = this.validationManager.getDataTesterTarget( recIndex );

        return this.getOutlier( target );
    }

/**
 * <p>Returns an EnvironmentalOutlier object related to a DataTester Target (in this
 * case a GenericRecord). If it is not an outlier, then null is returned.</p>
 *
 *
 * @param target DataTester Target.
 *
 * @return EnvironmentalOutlier or null.
 */
    public EnvironmentalOutlier getOutlier( Target target ) {

        Collection failures = new ArrayList();

        double avgOutlierness = 0;

        for ( Concept concept : this.concepts ) {

            NumericOutlierResult numericOutlierResult = this.validationManager.getNumericOutlierResult( concept );

            String scientificName = numericOutlierResult.getScientificName( target );

            if ( numericOutlierResult.skippedSpecies( scientificName ) ) {

                break;
            }
            else {

                if ( numericOutlierResult.isOutlier( scientificName, target ) ) {

                    failures.add( concept.getId() );
                }

                avgOutlierness += numericOutlierResult.getOutlierness( scientificName, target ).doubleValue();
            }
        }

        if ( failures.size() >= this.threshold ) {

            avgOutlierness /= this.concepts.size();

            return new EnvironmentalOutlier( failures, avgOutlierness );
        }

        return null;
    }
}
