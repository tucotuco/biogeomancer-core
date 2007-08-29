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

package org.biogeomancer.managers;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.biogeomancer.utils.Concepts;
import org.biogeomancer.utils.SupportedLanguages;
import org.biogeomancer.utils.Units;

public class UnitConverterManager extends BGManager {
  private static final Logger log = Logger
      .getLogger(UnitConverterManager.class);
  private static Properties props = new Properties();
  private static UnitConverterManager instance = null;

  public static UnitConverterManager getInstance() {
    if (instance == null)
      instance = new UnitConverterManager();
    return instance;
  }

  public static void main(String[] args) { // main
    UnitConverterManager ucm = UnitConverterManager.getInstance();
    log.info("Units:\n" + ucm.toString());
  }

  private HashMap meterConversionFactors;

  private UnitConverterManager() {
    init();
    HashMap factors = new HashMap();
    for (Units unit : Units.values()) {
      switch (unit) {
      case meter:
        factors.put(unit, 1.0);
        break;
      case foot:
        factors.put(unit, 0.3048);
        break;
      case mile:
        factors.put(unit, 1609.344);
        break;
      case yard:
        factors.put(unit, 0.9144);
        break;
      case kilometer:
        factors.put(unit, 1000.0);
        break;
      case nauticalmile:
        factors.put(unit, 1852.0);
        break;
      case fathom:
        factors.put(unit, 1.8288);
        break;
      }
      meterConversionFactors = factors;
    }
  }

  public Units getStandardUnit(String unitstring) {
    GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
    String unit = gd.lookup(unitstring, SupportedLanguages.english,
        Concepts.units, true);
    if (unit.equals("m"))
      return Units.meter;
    if (unit.equals("ft"))
      return Units.foot;
    if (unit.equals("mi"))
      return Units.mile;
    if (unit.equals("yd"))
      return Units.yard;
    if (unit.equals("km"))
      return Units.kilometer;
    if (unit.equals("nm"))
      return Units.nauticalmile;
    if (unit.equals("fth"))
      return Units.fathom;
    return null;
  }

  public String getStandardUnitString(String unitstring, SupportedLanguages lang) {
    GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
    return gd.lookup(unitstring, lang, Concepts.units, true);
  }

  public boolean isUnit(String unitstring) {
    if (unitstring == null)
      return false;
    GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
    String unit = gd.lookup(unitstring, SupportedLanguages.english,
        Concepts.units, true);
    if (unit == null)
      return false; // No unit matching unitstring in the GeorefDictionary.
    return true; // Unit matching unitstring found in the GeorefDictionary.
  }

  public void log(String s) {
    this.log.info(s);
  }

  public String toString() {
    String s = new String("Unit:\tToMeters:\n");
    for (Units unit : Units.values()) {
      s = s.concat(unit.name() + "\t");
      double m = unit2meters(1, unit.name());
      if (m != 0)
        s = s.concat(Double.toString(m));
      else
        s = s.concat("not supported");
      s = s.concat("\n");
    }
    return s;
  }

  public double unit2meters(double value, String fromunitstring) {
    if (isUnit(fromunitstring) == false)
      return 0.0;
    Units fromunit = getStandardUnit(fromunitstring);
    return value * (Double) meterConversionFactors.get(fromunit);
  }

  /*****************************************************************************
   * Converts any supported unit to meters.
   * 
   * @param value
   *          The value of the unit that you are converting from
   * @param from
   *          The unit that you are converting from
   ****************************************************************************/
  public double unit2meters(double value, Units fromunit) {
    return value * (Double) meterConversionFactors.get(fromunit);
  }

  private void init() {
    initProps("UnitConverterManager.properties", props);
    // log.info("UnitConverterManager started");
  }
}