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

import java.util.Set;

import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;
import org.gbif.datatester.Concept;

/**
 * <p>
 * Extends AbstractDataMap and represents a concrete Map interface
 * implementation representing original BioGeomancer records.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class OriginalRecordDataMap extends AbstractDataMap {

  /**
   * <p>
   * Reference to the OriginalRecordSet object.
   * </p>
   * 
   */
  private final OriginalRecordSet originalRecordSet;

  /**
   * <p>
   * Reference to the BioGeomancer record set object.
   * </p>
   * 
   */
  private final RecSet bgRecSet;

  /**
   * <p>
   * Index of the original BioGeomancer record inside the record set.
   * </p>
   * 
   */
  private final int bgRecordIndex;

  /**
   * <p>
   * Constructs an OriginalRecordDataMap. In other words, a Map wrapper around
   * the BioGeomancer data structure for original records.
   * </p>
   * 
   * @param originalRecordSet
   *          Reference to an OriginalRecordSet object.
   * @param bgRecordIndex
   *          Index of a BioGeomancer record inside the record set.
   */
  public OriginalRecordDataMap(OriginalRecordSet originalRecordSet,
      int bgRecordIndex) {

    this.originalRecordSet = originalRecordSet;
    this.bgRecSet = originalRecordSet.getBGRecSet();
    this.bgRecordIndex = bgRecordIndex;
  }

  /**
   * <p>
   * Compares the specified object with this map for equality.
   * </p>
   * 
   * @param obj
   *          object to be compared for equality with this map.
   * @return true if the specified object is equal to this map.
   */
  public boolean equals(Object obj) {

    if (obj == null) {

      return false;
    }

    if (!(obj instanceof OriginalRecordDataMap)) {

      return false;
    }

    OriginalRecordDataMap that = (OriginalRecordDataMap) obj;

    if (this == that) {

      return true;
    }

    if (this.bgRecordIndex == that.bgRecordIndex) {

      return true;
    }

    return false;
  }

  /**
   * <p>
   * Returns the value to which this map maps the specified key. Returns null if
   * the map contains no mapping for this key. A return value of null does not
   * necessarily indicate that the map contains no mapping for the key; it's
   * also possible that the map explicitly maps the key to null. The containsKey
   * operation may be used to distinguish these two cases.
   * </p>
   * 
   * @param key
   *          key whose associated value is to be returned.
   * @return the value to which this map maps the specified key, or null if the
   *         map contains no mapping for this key.
   * @throws ClassCastException
   *           if the key is of an inappropriate type for this map.
   * @throws NullPointerException
   *           key is null and this map does not not permit null keys.
   */
  public Object get(Object key) {

    if (key == null) {

      throw new NullPointerException();
    }

    if (!(key instanceof Concept)) {

      throw new ClassCastException();
    }

    Concept concept = (Concept) key;

    if (concept == BGConcepts.RECORD_INDEX) {

      return this.bgRecordIndex;
    }

    Rec rec = this.bgRecSet.recs.get(this.bgRecordIndex);

    if (concept == BGConcepts.ID) {

      return rec.get("id");
    } else if (concept == BGConcepts.LATITUDE) {

      String latStr = rec.get("declat");

      if (latStr != null && latStr.length() > 0) {

        return new Double(latStr);
      }
      return null;
    } else if (concept == BGConcepts.LONGITUDE) {

      String longStr = rec.get("declong");

      if (longStr != null && longStr.length() > 0) {

        return new Double(longStr);
      }
      return null;
    } else if (concept == BGConcepts.COUNTRY) {

      return rec.get("country");
    } else if (concept == BGConcepts.STATE_PROVINCE) {

      return rec.get("stateprovince");
    } else if (concept == BGConcepts.COUNTY) {

      return rec.get("county");
    } else if (concept == BGConcepts.COORDINATE_UNCERTAINTY) {

      String uncertaintyStr = rec.get("coordinateuncertaintyinmeters");

      if (uncertaintyStr != null && uncertaintyStr.length() > 0) {

        return new Double(uncertaintyStr);
      }
      return null;
    } else if (concept == BGConcepts.COLLECTOR) {

      return rec.get("collector");
    } else if (concept == BGConcepts.EARLIEST_COLLECTING_DATE) {

      return rec.get("earliestdate");
    } else if (concept == BGConcepts.MINIMUM_ELEVATION) {

      String value = rec.get("minimumelevationinmeters");

      if (value != null && value.length() > 0) {

        return new Double(value);
      }
      return null;
    } else if (concept == BGConcepts.MAXIMUM_ELEVATION) {

      String value = rec.get("maximumelevationinmeters");

      if (value != null && value.length() > 0) {

        return new Double(value);
      }
      return null;
    } else if (concept == BGConcepts.SCIENTIFIC_NAME) {

      return rec.get("scientificname");
    } else {

      // Additional concepts? (environmental data)
      return rec.envData.get(concept.getId());
    }
  }

  /**
   * <p>
   * Returns the hash code value for this map.
   * </p>
   * 
   * @return the hash code value for this map.
   */
  public int hashCode() {

    Integer property1 = new Integer(this.bgRecordIndex);

    int hash = 17;
    hash = 37 * hash + property1.hashCode();

    return hash;
  }

  /**
   * <p>
   * Returns a set view of the keys contained in this map.
   * </p>
   * 
   * @return a set view of the keys contained in this map.
   */
  public Set keySet() {

    return this.originalRecordSet.getConcepts();
  }
}
