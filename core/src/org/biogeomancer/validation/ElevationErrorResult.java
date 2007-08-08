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
 * <p>Wrapper class giving programmer-friendly access to elevation error results.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class ElevationErrorResult {

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
    public ElevationErrorResult( ValidationManager validationManager, ResultManager resultManager ) {

        this.resultManager = resultManager;
        this.validationManager = validationManager;
    }

/**
 * <p>Returns the associated elevation error of a generated interpretation, if there was any
 * error detected.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 * @param georefIndex Index of the interpretation in the original record.
 *
 * @return ElevationError object. Can be null if no error was detected.
 */
    public ElevationError getError( int recIndex, int georefIndex ) {

        Target target = this.validationManager.getDataTesterTarget( recIndex, georefIndex );

        return this.getError( target );
    }

/**
 * <p>Returns the associated elevation error of an original record, if there was any
 * error detected.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 *
 * @return ElevationError object. Can be null if no error was detected.
 */
    public ElevationError getError( int recIndex ) {

        Target target = this.validationManager.getDataTesterTarget( recIndex );

        return this.getError( target );
    }

/**
 * <p>Returns the associated elevation error of a generic target, if there was any
 * error detected.</p>
 *
 *
 * @param target DataTester Target.
 *
 * @return ElevationError object. Can be null if no error was detected.
 */
    public ElevationError getError( Target target ) {

        AssignedTag assignedTag = this.validationManager.getFirstAssignedTag( this.resultManager, target, ElevationErrorTest.ELEVATION_ERROR_TAG_ID );

        if ( assignedTag != null ) {

	    return new ElevationError( assignedTag );
        }

        return null;
    }
}
