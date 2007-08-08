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

import org.biogeomancer.records.*;

/**
 * <p>An adapter class to use the original records of a BioGeomancer 
 * record set as a GenericRecordSet in the data tester framework context.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class OriginalRecordSet extends RamRecordSet {

/**
 * <p>Set of all concepts used by this record set.</p>
 * 
 */
    public static Set<Concept> CONCEPTS;

    static {

        Set bgConcepts = new HashSet();
        bgConcepts.add( BGConcepts.ID );
        bgConcepts.add( BGConcepts.RECORD_INDEX );
        bgConcepts.add( BGConcepts.LATITUDE );
        bgConcepts.add( BGConcepts.LONGITUDE );
        bgConcepts.add( BGConcepts.COORDINATE_UNCERTAINTY );
        bgConcepts.add( BGConcepts.COLLECTOR );
        bgConcepts.add( BGConcepts.EARLIEST_COLLECTING_DATE );
        bgConcepts.add( BGConcepts.MINIMUM_ELEVATION );
        bgConcepts.add( BGConcepts.MAXIMUM_ELEVATION );
        bgConcepts.add( BGConcepts.SCIENTIFIC_NAME );
        bgConcepts.add( BGConcepts.COUNTRY );
        bgConcepts.add( BGConcepts.STATE_PROVINCE );
        bgConcepts.add( BGConcepts.COUNTY );

        CONCEPTS = Collections.unmodifiableSet( bgConcepts );
    }

/**
 * <p>Record id definition from the DataTester perspective.</p>
 * 
 */
    public static List ID_DEFINITION;

    static {

        List idDefinition = new ArrayList();
        idDefinition.add( BGConcepts.RECORD_INDEX );

        ID_DEFINITION = Collections.unmodifiableList( idDefinition );
    }

/**
 * <p>Original BioGeomancer RecSet.</p>
 * 
 */
    private RecSet bgRecSet;

/**
 * <p>Complete set of concepts - the ones statically defined above plus any
 * extra dynamically defined ones.</p>
 * 
 */
    private Set concepts;


/**
 * <p>Contructs an OriginalRecordSet for validation purposes based on 
 * a BioGeomancer RecSet. The default set of concepts will be used.</p>
 * 
 * 
 * @param bgRecSet BioGeomancer RecSet object.
 * @throws RecordSetException
 */
    public OriginalRecordSet( RecSet bgRecSet ) throws RecordSetException {

        super( CONCEPTS );

        this.bgRecSet = bgRecSet;
        this.concepts = CONCEPTS;

        this.load();
    }

/**
 * <p>Contructs an OriginalRecordSet for validation purposes based on 
 * a BioGeomancer RecSet. In this case the list of concepts to be used
 * is passed as a parameter.</p>
 * 
 * 
 * @param bgRecSet BioGeomancer RecSet object.
 * @param concepts Complete set of concepts (static + dynamic) for the record set.
 * @throws RecordSetException
 */
    public OriginalRecordSet( RecSet bgRecSet, Set concepts ) throws RecordSetException {

        super( concepts );

        this.bgRecSet = bgRecSet;
        this.concepts = concepts;

        this.load();
    }

/**
 * <p>Loads the record set.</p>
 * 
 * 
 * @throws RecordSetException
 */
    private void load() throws RecordSetException {

        // Instantiate record adapters
        // (better to do that once instead of instantiating them
        // each time nextRecord() gets called)

        int recordIndex = -1;

        // Loop over BioGeomancer records
        List<Rec> bgRecords = this.bgRecSet.recs;
        for ( Rec record : bgRecords ) {

            ++recordIndex;

            Map dataMap = new OriginalRecordDataMap( this, recordIndex );

            this.addRecord( new RamRecord( ID_DEFINITION, dataMap ) );
        }
    }

/**
 * <p>Returns the original BioGeomancer RecSet.</p>
 * 
 * 
 * @return Reference to BioGeomancer RecSet object.
 */
    public RecSet getBGRecSet() {

        return this.bgRecSet;
    }

/**
 * <p>Returns the whole set of concepts being considered in the record set.</p>
 * 
 * 
 * @return Complete set of Concepts.
 */
    public Set getConcepts() {

        return this.concepts;
    }
 }
