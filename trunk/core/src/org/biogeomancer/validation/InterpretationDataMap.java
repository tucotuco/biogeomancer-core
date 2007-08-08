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

import org.biogeomancer.records.*;
import org.biogeomancer.utils.*;

/**
 * <p>Extends AbstractDataMap and represents a concrete Map interface implementation
 * representing BioGeomancer interpretation records, where each interpretation is
 * tied to an original record.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class InterpretationDataMap extends AbstractDataMap {

/**
 * <p>Reference to the InterpretationRecordSet object.</p>
 * 
 */
    private InterpretationRecordSet interpretationRecordSet;

/**
 * <p>Reference to the BioGeomancer record set object.</p>
 * 
 */
    private RecSet bgRecSet;

/**
 * <p>Index of the original BioGeomancer record inside the record set.</p>
 * 
 */
    private int bgRecordIndex;

/**
 * <p>Index of the original BioGeomancer georeference (interpretation) inside the record.</p>
 * 
 */
    private int bgGeoref;


/**
 * <p>Constructs an InterpretationDataMap. In other words, a Map wrapper around the BioGeomancer
 * data structure for interpretations.</p>
 * 
 * @param interpretationRecordSet Reference to an InterpretationRecordSet object.
 * @param bgRecordIndex Index of a BioGeomancer record inside the record set.
 * @param bgGeoref Index of a BioGeomancer georeference (interpretation) inside the record.
 */
    public InterpretationDataMap( InterpretationRecordSet interpretationRecordSet, int bgRecordIndex, int bgGeoref ) {

        this.interpretationRecordSet = interpretationRecordSet;
        this.bgRecSet = interpretationRecordSet.getBGRecSet();
        this.bgRecordIndex = bgRecordIndex;
        this.bgGeoref = bgGeoref;
    }

/**
 * <p>Compares the specified object with this map for equality.</p>
 * 
 * @param obj object to be compared for equality with this map.
 * @return true if the specified object is equal to this map.
 */
    public boolean equals( Object obj ) {

        if ( obj == null ) {

            return false; 
        }

        if ( ! ( obj instanceof InterpretationDataMap ) ) {

            return false;
        }

        InterpretationDataMap that = (InterpretationDataMap) obj;

        if ( this == that ) {

            return true;
        }

        if ( this.bgRecordIndex == that.bgRecordIndex && 
             this.bgGeoref == that.bgGeoref ) {

            return true;
        }

        return false;
    }

/**
 * <p>Returns the value to which this map maps the specified key. Returns null if 
 * the map contains no mapping for this key. A return value of null does not 
 * necessarily indicate that the map contains no mapping for the key; it's also 
 * possible that the map explicitly maps the key to null. The containsKey operation 
 * may be used to distinguish these two cases. </p>
 * 
 * @param key key whose associated value is to be returned.
 * @return the value to which this map maps the specified key, or null if the map 
 *         contains no mapping for this key.
 * @throws ClassCastException if the key is of an inappropriate type for this map. 
 * @throws NullPointerException key is null and this map does not not permit null keys.
 */
    public Object get( Object key ) {

        if ( key == null ) {

            throw new NullPointerException();
        }

        if ( ! ( key instanceof Concept ) ) {

            throw new ClassCastException();
        }

        Concept concept = (Concept)key;

        if ( concept == BGConcepts.RECORD_INDEX ) {

            return this.bgRecordIndex;
        }
        else if ( concept == BGConcepts.GEOREF_INDEX ) {

            return this.bgGeoref;
        }
        else if ( concept == BGConcepts.ID ) {

            return this.bgRecSet.recs.get( this.bgRecordIndex ).get( "id" );
        }
        else if ( concept == BGConcepts.COUNTRY ) {

            return this.bgRecSet.recs.get( this.bgRecordIndex ).get( "country" );
        }
        else if ( concept == BGConcepts.STATE_PROVINCE ) {

            return this.bgRecSet.recs.get( this.bgRecordIndex ).get( "stateprovince" );
        }
        else if ( concept == BGConcepts.COUNTY ) {

            return this.bgRecSet.recs.get( this.bgRecordIndex ).get( "county" );
        }
        else if ( concept == BGConcepts.COLLECTOR ) {

            return this.bgRecSet.recs.get( this.bgRecordIndex ).get( "collector" );
        }
        else if ( concept == BGConcepts.EARLIEST_COLLECTING_DATE ) {

            return this.bgRecSet.recs.get( this.bgRecordIndex ).get( "earliestdate" );
        }
        else if ( concept == BGConcepts.MINIMUM_ELEVATION ) {

            String value = this.bgRecSet.recs.get( this.bgRecordIndex ).get( "minimumelevationinmeters" );

            if ( value != null && value.length() > 0 ) {

                return new Double( value );
            }

            return null;
        }
        else if ( concept == BGConcepts.MAXIMUM_ELEVATION ) {

            String value = this.bgRecSet.recs.get( this.bgRecordIndex ).get( "maximumelevationinmeters" );

            if ( value != null && value.length() > 0 ) {

                return new Double( value );
            }

            return null;
        }
        else if ( concept == BGConcepts.SCIENTIFIC_NAME ) {

            return this.bgRecSet.recs.get( this.bgRecordIndex ).get( "scientificname" );
        }
        else {

            // Interpretation (Georef) attributes
            Georef georef = this.bgRecSet.recs.get( this.bgRecordIndex ).georefs.get( this.bgGeoref );

            if ( concept == BGConcepts.LATITUDE ) {

                return new Double( georef.pointRadius.getY() );
            }
            else if ( concept == BGConcepts.LONGITUDE ) {

                return new Double( georef.pointRadius.getX() );
            }
            else if ( concept == BGConcepts.COORDINATE_UNCERTAINTY ) {

                return new Double( georef.pointRadius.extent );
            }
            else {

                // Additional concepts? (environmental data)
                return georef.envData.get( concept.getId() );
            }

        }
    }

/**
 * <p>Returns the hash code value for this map.</p>
 * 
 * @return the hash code value for this map.
 */
    public int hashCode() {

        Integer property1 = new Integer( this.bgRecordIndex );
        Integer property2 = new Integer( this.bgGeoref );

        int hash = 17;
        hash = 37 * hash + property1.hashCode();
        hash = 37 * hash + property2.hashCode();

        return hash;
    }

/**
 * <p>Returns a set view of the keys contained in this map.</p>
 * 
 * @return a set view of the keys contained in this map.
 */
    public Set keySet() {


        return this.interpretationRecordSet.getConcepts();
    }
}
