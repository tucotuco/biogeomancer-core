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


/**
 * <p>Individual result for an environmental outlier related to some record.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class EnvironmentalOutlier {

/**
 * <p>Collection of variables for which the record was considered an outlier.</p>
 */
    private Collection failures;

/**
 * <p>Average outlierness for all environmental variables tested.</p>
 */
    private double averageOutlierness;

/**
 * <p>Constructor.</p>
 *
 * @param failures Collection of variables for which the record was considered an outlier.
 * @param averageOutlierness Average outlierness for all environmental variables tested.
 */
    public EnvironmentalOutlier( Collection failures, double averageOutlierness ) {

        this.failures = failures;
        this.averageOutlierness = averageOutlierness;
    }

/**
 * <p>Returns a Collection of variables for which the record was considered an outlier.</p>
 *
 *
 * @return Collection of variables (String identifiers) for which the record was considered an outlier.
 */
    public Collection getOutlierVariables() {

        return this.failures;
    }

/**
 * <p>Returns the average outlierness for all environmental variables tested.</p>
 *
 *
 * @return Average outlierness.
 */
    public double getAverageOutlierness() {

        return this.averageOutlierness;
    }
}
