/**
 * Copyright 2007 University of California at Berkeley.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.biogeomancer.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;
import org.gbif.datatester.Concept;
import org.gbif.datatester.RamRecord;
import org.gbif.datatester.RamRecordSet;
import org.gbif.datatester.exception.RecordSetException;

/**
 * <p>
 * An adapter class to use the interpretations of a BioGeomancer record set as a
 * GenericRecordSet in the data tester framework context.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class InterpretationRecordSet extends RamRecordSet {

  /**
   * <p>
   * Set of all concepts used by this record set.
   * </p>
   * 
   */
  public static Set<Concept> CONCEPTS;

  /**
   * <p>
   * Record id definition from the DataTester perspective.
   * </p>
   * 
   */
  public static List ID_DEFINITION;

  static {

    Set bgConcepts = new HashSet();
    bgConcepts.add(BGConcepts.ID);
    bgConcepts.add(BGConcepts.RECORD_INDEX);
    bgConcepts.add(BGConcepts.GEOREF_INDEX);
    bgConcepts.add(BGConcepts.LATITUDE);
    bgConcepts.add(BGConcepts.LONGITUDE);
    bgConcepts.add(BGConcepts.COORDINATE_UNCERTAINTY);
    bgConcepts.add(BGConcepts.COLLECTOR);
    bgConcepts.add(BGConcepts.EARLIEST_COLLECTING_DATE);
    bgConcepts.add(BGConcepts.MINIMUM_ELEVATION);
    bgConcepts.add(BGConcepts.MAXIMUM_ELEVATION);
    bgConcepts.add(BGConcepts.SCIENTIFIC_NAME);
    bgConcepts.add(BGConcepts.COUNTRY);
    bgConcepts.add(BGConcepts.STATE_PROVINCE);
    bgConcepts.add(BGConcepts.COUNTY);

    CONCEPTS = Collections.unmodifiableSet(bgConcepts);
  }

  static {

    List idDefinition = new ArrayList();
    idDefinition.add(BGConcepts.RECORD_INDEX);
    idDefinition.add(BGConcepts.GEOREF_INDEX);

    ID_DEFINITION = Collections.unmodifiableList(idDefinition);
  }

  /**
   * <p>
   * Original BioGeomancer RecSet.
   * </p>
   * 
   */
  private final RecSet bgRecSet;

  /**
   * <p>
   * Complete set of concepts - the ones statically defined above plus any extra
   * dynamically defined ones.
   * </p>
   * 
   */
  private final Set concepts;

  /**
   * <p>
   * Contructs an InterpretationRecordSet for validation purposes based on a
   * BioGeomancer RecSet. The default set of concepts will be used.
   * </p>
   * 
   * 
   * @param bgRecSet
   *          BioGeomancer RecSet object.
   * @throws RecordSetException
   */
  public InterpretationRecordSet(RecSet bgRecSet) throws RecordSetException {

    super(CONCEPTS);

    this.bgRecSet = bgRecSet;
    this.concepts = CONCEPTS;

    this.load();
  }

  /**
   * <p>
   * Contructs an InterpretationRecordSet for validation purposes based on a
   * BioGeomancer RecSet. In this case the list of concepts to be used is passed
   * as a parameter.
   * </p>
   * 
   * 
   * @param bgRecSet
   *          BioGeomancer RecSet object.
   * @param concepts
   *          Complete set of concepts (static + dynamic) for the record set.
   * @throws RecordSetException
   */
  public InterpretationRecordSet(RecSet bgRecSet, Set concepts)
      throws RecordSetException {

    super(concepts);

    this.bgRecSet = bgRecSet;
    this.concepts = concepts;

    this.load();
  }

  /**
   * <p>
   * Returns the original BioGeomancer RecSet.
   * </p>
   * 
   * 
   * @return Reference to BioGeomancer RecSet object.
   */
  public RecSet getBGRecSet() {

    return this.bgRecSet;
  }

  /**
   * <p>
   * Returns the whole set of concepts being considered in the record set.
   * </p>
   * 
   * 
   * @return Complete set of Concepts.
   */
  public Set getConcepts() {

    return this.concepts;
  }

  /**
   * <p>
   * Load the record set.
   * </p>
   * 
   * 
   * @throws RecordSetException
   */
  private void load() throws RecordSetException {

    // Instantiate record adapters
    // (better to do that once instead of instantiating them
    // each time nextRecord() gets called)

    int recordIndex = -1;
    int georefIndex;

    // Loop over BioGeomancer records
    List<Rec> bgRecords = this.bgRecSet.recs;
    for (Rec record : bgRecords) {

      ++recordIndex;
      georefIndex = -1;

      // Loop over georefs (generated interpretations) for each bg record
      List<Georef> georefs = record.georefs;

      for (Georef georef : georefs) {

        ++georefIndex;

        Map dataMap = new InterpretationDataMap(this, recordIndex, georefIndex);

        this.addRecord(new RamRecord(ID_DEFINITION, dataMap));
      }
    }
  }
}
