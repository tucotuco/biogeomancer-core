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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * An abstract class starting to implement the Map interface based on the data
 * structure of either BioGeomancer interpretations or original records. This
 * class allows the RamRecord class to be used by both the
 * {@link InterpretationRecordSet} and the {@link OriginalRecordSet}.
 * RamRecords receive a Map as a parameter and they only use these methods of a
 * Map:
 * </p>
 * <ul>
 * <li>isEmpty</li>
 * <li>keySet</li>
 * <li>entrySet</li>
 * <li>containsKey</li>
 * <li>get</li>
 * </ul>
 * From the previous list, this class implements methods isEmpty, entrySet,
 * containsKey and get, leaving only keySet to be implemented by subclasses.
 * </p>
 * <p>
 * However, it additionaly implements size, values and containsValue, and it
 * requires the following methods to be implemented by subclasses:
 * </p>
 * <ul>
 * <li>equals</li>
 * <li>hashcode</li>
 * </ul>
 * <p>
 * All other methods in this Map implementation (clear, put, putAll, remove, and
 * setValue) throw an UnsupportedOperationException.
 * </p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public abstract class AbstractDataMap implements Map {

  /**
   * <p>
   * A simple implementation of the Map.Entry interface to be used by the
   * entrySet method.
   * </p>
   * 
   * 
   */
  public class Entry implements Map.Entry {

    /**
     * <p>
     * Entry key.
     * </p>
     * 
     */
    private final Object key;

    /**
     * <p>
     * Entry value.
     * </p>
     * 
     */
    private final Object value;

    /**
     * <p>
     * Constructs a map entry.
     * </p>
     * 
     * @param key
     *          Entry key.
     * @param value
     *          Entry value.
     */
    private Entry(Object key, Object value) {

      this.key = key;
      this.value = value;
    }

    /**
     * <p>
     * Compares the specified object with this entry for equality.
     * </p>
     * 
     * @param obj
     *          object to be compared for equality with this entry.
     * @return true if the specified object is equal to this entry.
     */
    public boolean equals(Object obj) {

      if (obj == null) {

        return false;
      }

      if (!(obj instanceof AbstractDataMap.Entry)) {

        return false;
      }

      Entry that = (AbstractDataMap.Entry) obj;

      if (that == this) {

        return true;
      }

      if (that.key.equals(this.key) && that.value.equals(this.value)) {

        return true;
      } else {

        return false;
      }
    }

    /**
     * <p>
     * Returns the key corresponding to this entry.
     * </p>
     * 
     * @return Entry key.
     */
    public Object getKey() {

      return this.key;
    }

    /**
     * <p>
     * Returns the value corresponding to this entry.
     * </p>
     * 
     * @return Entry value.
     */
    public Object getValue() {

      return this.value;
    }

    /**
     * <p>
     * Returns the hash code value for this entry.
     * </p>
     * 
     * @return the hash code value for this entry.
     */
    public int hashCode() {

      int hash = 17;

      if (this.key != null) {

        hash = 37 * hash + this.key.hashCode();
      }
      if (this.value != null) {

        hash = 37 * hash + this.value.hashCode();
      }

      return hash;
    }

    /**
     * <p>
     * Method designed to replace the value corresponding to this entry with the
     * specified value, but not implemented here.
     * </p>
     * 
     * @return never returns anything.
     * @throws UnsupportedOperationException
     */
    public Object setValue(Object value) {

      throw new UnsupportedOperationException();
    }
  }

  /**
   * <p>
   * Method designed to remove all mappings from this map, but not supported.
   * </p>
   * 
   * @throws UnsupportedOperationException
   *           clear method is not supported by this implementation.
   */
  public void clear() {

    throw new UnsupportedOperationException();
  }

  /**
   * <p>
   * Returns true if this map contains a mapping for the specified key.
   * </p>
   * 
   * @param key
   *          key whose presence in this map is to be tested.
   * @return true if this map contains a mapping for the specified key.
   */
  public boolean containsKey(Object key) {

    // Just check that the key is one of the known concepts
    if (this.keySet().contains(key)) {

      return true;
    }

    return false;
  }

  /**
   * <p>
   * Returns true if this map maps one or more keys to the specified value.
   * </p>
   * 
   * @param value
   *          value whose presence in this map is to be tested.
   * @return true if this map maps one or more keys to the specified value.
   */
  public boolean containsValue(Object value) {

    return this.values().contains(value);
  }

  /**
   * <p>
   * Returns a set view of the mappings contained in this map. Should be
   * overloaded by subclasses.
   * </p>
   * 
   * @return a set view of the mappings contained in this map.
   */
  public Set entrySet() {

    Set entries = new HashSet();

    for (Object obj : this.keySet()) {

      entries.add(new Entry(obj, this.get(obj)));
    }

    return entries;
  }

  /**
   * <p>
   * Compares the specified object with this map for equality. Should be
   * overloaded by subclasses.
   * </p>
   * 
   * @param obj
   *          object to be compared for equality with this map.
   * @return true if the specified object is equal to this map.
   */
  public abstract boolean equals(Object obj);

  /**
   * <p>
   * Returns the value to which this map maps the specified key. Returns null if
   * the map contains no mapping for this key. A return value of null does not
   * necessarily indicate that the map contains no mapping for the key; it's
   * also possible that the map explicitly maps the key to null. The containsKey
   * operation may be used to distinguish these two cases. Should be overloaded
   * by subclasses.
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
  public abstract Object get(Object key);

  /**
   * <p>
   * Returns the hash code value for this map. Should be overloaded by
   * subclasses.
   * </p>
   * 
   * @return the hash code value for this map.
   */
  public abstract int hashCode();

  /**
   * <p>
   * Method designed to return true if this map contains no key-value mappings,
   * but in this implementation it will never be considered empty.
   * </p>
   * 
   * @return always false, since objects of this class will never be empty.
   */
  public boolean isEmpty() {

    return false;
  }

  /**
   * <p>
   * Returns a set view of the keys contained in this map. Should be overloaded
   * by subclasses.
   * </p>
   * 
   * @return a set view of the keys contained in this map.
   */
  public abstract Set keySet();

  /**
   * <p>
   * Associates the specified value with the specified key in this map (optional
   * operation).
   * </p>
   * 
   * @param key
   *          key with which the specified value is to be associated.
   * @param value
   *          value to be associated with the specified key.
   * @return never returns anything.
   * @throws UnsupportedOperationException
   *           put method is not supported by this implementation.
   */
  public Object put(Object key, Object value) {

    throw new UnsupportedOperationException();
  }

  /**
   * <p>
   * Copies all of the mappings from the specified map to this map (optional
   * operation).
   * </p>
   * 
   * @param t
   *          Mappings to be stored in this map.
   * @return never returns anything.
   * @throws UnsupportedOperationException
   *           putAll method is not supported by this implementation.
   */
  public void putAll(Map t) {

    throw new UnsupportedOperationException();
  }

  /**
   * <p>
   * Removes the mapping for this key from this map if it is present (optional
   * operation).
   * </p>
   * 
   * @param key
   *          key whose mapping is to be removed from the map.
   * @return never returns anything.
   * @throws UnsupportedOperationException
   *           remove method is not supported by this implementation.
   */
  public Object remove(Object key) {

    throw new UnsupportedOperationException();
  }

  /**
   * <p>
   * Returns the number of key-value mappings in this map.
   * </p>
   * 
   * @return the number of key-value mappings in this map.
   */
  public int size() {

    return this.keySet().size();
  }

  /**
   * <p>
   * Returns a collection view of the values contained in this map.
   * </p>
   * 
   * @return a collection view of the values contained in this map.
   */
  public Collection values() {

    Collection values = new HashSet();

    for (Object obj : this.keySet()) {

      values.add(this.get(obj));
    }

    return values;
  }
}
