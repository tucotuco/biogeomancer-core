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
package org.biogeomancer.managers;

import java.util.*;

import org.apache.log4j.Logger;
import org.biogeomancer.utils.*;

public class UnitConverterManager extends BGManager {
	private HashMap meterConversionFactors;
	private static final Logger log = Logger.getLogger(UnitConverterManager.class);
	private static Properties props = new Properties();
	private static UnitConverterManager instance = null;

	private UnitConverterManager() {
		init();
		HashMap factors = new HashMap();
		for(Units unit : Units.values()) {
			switch(unit) {
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
	private void init() {
		initProps("UnitConverterManager.properties", props);
//		log.info("UnitConverterManager started");
	}
	public static void main(String[] args) { // main
		UnitConverterManager ucm = UnitConverterManager.getInstance();
		log.info("Units:\n"+ucm.toString());
	}
	
	public static UnitConverterManager getInstance(){
		if(instance == null) instance = new UnitConverterManager();
		return instance;
	}
	public void log(String s){
		this.log.info(s);
	}
	/***
	 Converts any supported unit to meters.
	 @param value The value of the unit that you are converting from
	 @param from The unit that you are converting from
	 ***/
	public double unit2meters(double value, Units fromunit) {
		return value * (Double)meterConversionFactors.get(fromunit); 
	}
	public double unit2meters(double value, String fromunitstring) {
		if(isUnit(fromunitstring)==false) return 0.0;
		Units fromunit = getStandardUnit(fromunitstring);
		return value * (Double)meterConversionFactors.get(fromunit); 
	}
	public boolean isUnit( String unitstring ) {
		if( unitstring == null ) return false;
		GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
		String unit = gd.lookup(unitstring, SupportedLanguages.english, 
				Concepts.units, true);
		if( unit == null ) return false; // No unit matching unitstring in the GeorefDictionary.
		return true; // Unit matching unitstring found in the GeorefDictionary.
	}
	public String getStandardUnitString(String unitstring, SupportedLanguages lang){
		GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
		return gd.lookup(unitstring, lang, Concepts.units, true);
	}
	public Units getStandardUnit(String unitstring) {
		GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
		String unit = gd.lookup(unitstring, SupportedLanguages.english, Concepts.units, true);
		if( unit.equals("m") ) return Units.meter;
		if( unit.equals("ft") ) return Units.foot;
		if( unit.equals("mi") ) return Units.mile;
		if( unit.equals("yd") ) return Units.yard;
		if( unit.equals("km") ) return Units.kilometer;
		if( unit.equals("nm") ) return Units.nauticalmile;
		if( unit.equals("fth") ) return Units.fathom;
		return null;
	}
	public String toString(){
		String s = new String("Unit:\tToMeters:\n");
		for(Units unit : Units.values()) {
			s = s.concat(unit.name()+"\t");
			double m = unit2meters(1,unit.name());
			if(m!=0) s = s.concat(Double.toString(m));
			else s = s.concat("not supported");
			s=s.concat("\n");
		}
		return s;
	}
}