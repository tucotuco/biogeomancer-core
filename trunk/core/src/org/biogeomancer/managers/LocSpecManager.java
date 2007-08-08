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

import java.util.Properties;

import org.apache.log4j.Logger;
import org.biogeomancer.managers.DatumManager.Datum;
import org.biogeomancer.records.LocSpec;
import org.biogeomancer.records.LocSpecState;
import org.biogeomancer.utils.Concepts;
import org.biogeomancer.utils.SupportedLanguages;
import org.biogeomancer.utils.Units;

public class LocSpecManager extends BGManager {
	private static LocSpecManager instance;
	public SupportedLanguages preferredlanguage = SupportedLanguages.english;
	private static final Logger log = Logger.getLogger(LocSpecManager.class);
	private static Properties props = new Properties();
	
	public static LocSpecManager getInstance() throws Exception {
		if (instance == null)
			instance = new LocSpecManager();
		return instance;
	}
	public LocSpecManager() {
		super();
		this.preferredlanguage = SupportedLanguages.english;
		startup("LocSpecManager.properties");
	}
	
	public LocSpecManager(String propsfile, SupportedLanguages preferredlanguage ) {	
		this.preferredlanguage = preferredlanguage;
		startup(propsfile);
	}
	public void startup(String propsfile){
		
		initProps(propsfile, props);
		
//    if (null == log)
//  		log = getLog(this.getClass(), 
//  				props.getProperty("log.dir") + "/" + 
//  				props.getProperty("log.filename"));

//		log.info("LocSpecManager started");
	}
	public void log(String s){
		log.info(s);
	}
	public static void main(String[] args) {
		if(args.length != 14)
			System.out.println(
"Required args (" + args.length + ") :\nfeature, offset, offsetunit, heading, offsetew, offsetewunit, headingew, offsetns, offsetnsunit, headingns, elev, elevunit, lat, lng");
		int FEATURENAME = 0;
		int OFFSET = 1;
		int OFFSETUNIT = 2;
		int HEADING = 3;
		int OFFSETEW = 4;
		int OFFSETEWUNIT = 5;
		int HEADINGEW = 6;
		int OFFSETNS = 7;
		int OFFSETNSUNIT = 8;
		int HEADINGNS = 9;
		int ELEV = 10;
		int ELEVUNIT = 11;
		int VLAT = 12;
		int VLNG = 13;
		
		LocSpecManager locspecmanager = null;
		try {
			locspecmanager = LocSpecManager.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LocSpec locspec = new LocSpec();
		locspec.addLocSpecManager(locspecmanager);
		
		locspec.featurename = new String(args[FEATURENAME]);
		locspec.voffset = new String(args[OFFSET]);
		locspec.voffsetunit = new String(args[OFFSETUNIT]);
		locspec.vheading = new String(args[HEADING]);
		locspec.voffsetew = new String(args[OFFSETEW]);
		locspec.voffsetewunit = new String(args[OFFSETEWUNIT]);
		locspec.voffsetns = new String(args[OFFSETNS]);
		locspec.voffsetnsunit = new String(args[OFFSETNSUNIT]);
		locspec.vheadingew = new String(args[HEADINGEW]);
		locspec.vheadingns = new String(args[HEADINGNS]);
		locspec.velevation = new String(args[ELEV]);
		locspec.velevationunits = new String(args[ELEVUNIT]);
		locspec.vlat = new String(args[VLAT]);
		locspec.vlng = new String(args[VLNG]);
		
//		locspec.vlat = new String("46.6692");
//		locspec.vlng = new String("-114.0896");
//		locspec.vlat = new String("46.6692°N");
//		locspec.vlng = new String("114.0896W");
		locspec.vdatum = new String("NAD27");
		
		locspec.vutmzone = new String("11 T");
		locspec.vutme = new String("722626 E");
		locspec.vutmn = new String("5172304 N");
		locspec.vtownship = new String("T34");
		locspec.vtownshipdir = new String("N");
		locspec.vrange = new String("R43");
		locspec.vrangedir = new String("W");
		locspec.vsection = new String("34");
		locspec.vsubdivision = new String("SW 1/4 of the NE 1/4");
		
		locspecmanager.interpretVerbatimAttributes(locspec);
		System.out.println(locspec.toString());
	}
	public void interpretVerbatimAttributes(LocSpec locspec){
		interpretOffset(locspec);
		interpretOffsetEW(locspec);
		interpretOffsetNS(locspec);
		interpretHeading(locspec);
		interpretHeadingEW(locspec);
		interpretHeadingNS(locspec);
		interpretElevation(locspec);
		
		interpretLatLng(locspec);
		interpretDatum(locspec);
		interpretUTM(locspec);
		interpretTRS(locspec);
		interpretSubdivision(locspec);
	}
	public LocSpecState interpretOffset(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.voffset == null ) {
			problems = true;
			locspec.ioffset = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSET_MISSING;
			log.error("Verbatim offset missing.");
		}
		if( locspec.voffsetunit == null) {
			problems = true;
			locspec.ioffsetunit = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSETUNIT_MISSING;
			log.error("Verbatim offset units missing.");
		}
		locspec.ioffset = locspec.voffset;
		Double d = null;
		try { // Try to make a double out of the voffset value.
			d = new Double( locspec.voffset );
		} catch(Exception e) { // It isn't a valid double value.
			problems = true;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSET_MALFORMED;
			log.error("Verbatim offset ("+locspec.voffset+") isn't a valid number.");
		}
		if( d != null) {
			locspec.ioffset = locspec.voffset;
			if( d.doubleValue() < 0.0 ) {
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSET_NEGATIVE; // Negative offsets are bad form.
				log.error("Verbatim offset ("+locspec.voffset+") is a negative number.");
			}
		}
		// TODO: Need input and output language selection capability
		String u = UnitConverterManager.getInstance().getStandardUnitString(locspec.voffsetunit, SupportedLanguages.english);
//		Units u = UnitConverterManager.getInstance().getStandardUnit(locspec.voffsetunit);
		if ( u == null ) {
			problems = true;
			locspec.ioffsetunit = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSETUNIT_NOT_FOUND;
			log.error("Verbatim offset unit ("+locspec.voffsetunit+") not found in the GeorefDictionary.");
		}else {
			locspec.ioffsetunit = new String(u);
//			locspec.ioffsetunit = u.name();
		}
		if( problems == false ) {
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
//			log.info("Offset interpretation completed successfully.");
		}
		return locspec.state;
		}
	
	public LocSpecState interpretOffsetEW(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.voffsetew == null ) {
			problems = true;
			locspec.ioffsetew = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSET_MISSING;
			log.error("Verbatim offsetew missing.");
		}
		if( locspec.voffsetewunit == null) {
			problems = true;
			locspec.ioffsetewunit = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSETUNIT_MISSING;
			log.error("Verbatim offsetew units missing.");
		}
		locspec.ioffsetew = locspec.voffsetew;
		Double d = null;
		try { // Try to make a double out of the voffsetew value.
			d = new Double( locspec.voffsetew );
		} catch(Exception e) { // It isn't a valid double value.
			problems = true;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSET_MALFORMED;
			log.error("Verbatim offsetew ("+locspec.voffsetew+") isn't a valid number.");
		}
		if( d != null) {
			locspec.ioffsetew = locspec.voffsetew;
			if( d.doubleValue() < 0.0 ) {
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSET_NEGATIVE; // Negative offsetews are bad form.
				log.error("Verbatim offsetew ("+locspec.voffsetew+") is a negative number.");
			}
		}
		String u = UnitConverterManager.getInstance().getStandardUnitString(locspec.voffsetewunit, SupportedLanguages.english);
//		Units u = UnitConverterManager.getInstance().getStandardUnit(locspec.voffsetewunit);
		if ( u == null ) {
			problems = true;
			locspec.ioffsetewunit = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSETUNIT_NOT_FOUND;
			log.error("Verbatim offsetew unit ("+locspec.voffsetewunit+") not found in the GeorefDictionary.");
		}else {
			locspec.ioffsetewunit = new String(u);
//			locspec.ioffsetewunit = u.name();
		}
		if( problems == false ) {
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
//			log.info("Offset interpretation completed successfully.");
		}
		return locspec.state;
	}
	
	public LocSpecState interpretOffsetNS(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.voffsetns == null ) {
			problems = true;
			locspec.ioffsetns = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSET_MISSING;
			log.error("Verbatim offsetns missing.");
		}
		if( locspec.voffsetnsunit == null) {
			problems = true;
			locspec.ioffsetnsunit = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSETUNIT_MISSING;
			log.error("Verbatim offsetns units missing.");
		}
		locspec.ioffsetns = locspec.voffsetns;
		Double d = null;
		try { // Try to make a double out of the voffsetns value.
			d = new Double( locspec.voffsetns );
		} catch(Exception e) { // It isn't a valid double value.
			problems = true;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSET_MALFORMED;
			log.error("Verbatim offsetns ("+locspec.voffsetns+") isn't a valid number.");
		}
		if( d != null) {
			locspec.ioffsetns = locspec.voffsetns;
			if( d.doubleValue() < 0.0 ) {
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSET_NEGATIVE; // Negative offsetnss are bad form.
				log.error("Verbatim offsetns ("+locspec.voffsetns+") is a negative number.");
			}
		}
		String u = UnitConverterManager.getInstance().getStandardUnitString(locspec.voffsetnsunit, SupportedLanguages.english);
//		Units u = UnitConverterManager.getInstance().getStandardUnit(locspec.voffsetnsunit);
		if ( u == null ) {
			problems = true;
			locspec.ioffsetnsunit = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_OFFSETUNIT_NOT_FOUND;
			log.error("Verbatim offsetns unit ("+locspec.voffsetnsunit+") not found in the GeorefDictionary.");
		}else {
			locspec.ioffsetnsunit = new String(u);
//			locspec.ioffsetnsunit = u.name();
		}
		if( problems == false ) {
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
//			log.info("Offset interpretation completed successfully.");
		}
		return locspec.state;
	}
	
	public LocSpecState interpretHeading(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.vheading == null ) {
			problems = true;
			locspec.iheading = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_HEADING_MISSING;
			log.error("Verbatim heading missing.");
		}
		String headingstring = GeorefDictionaryManager.getInstance().lookup(locspec.vheading, preferredlanguage, Concepts.headings, true);
		if( headingstring != null ) { // Heading found in GeorefDictionary.
			locspec.iheading = headingstring;
		}else {// Heading not found in dictionary, but it could still be a numerical heading.
			locspec.state = LocSpecState.LOCSPEC_ERROR_HEADING_NOT_FOUND;
//			log.error("Verbatim heading ("+locspec.vheading+") not found in the GeorefDictionary.");
			Double d = null;
			// Check to see if the heading is a number, in which case we'll interpret that as a degree heading.
			try { // Try to make a double out of the vheading value.
				d = new Double( locspec.vheading );
			} catch(Exception e) { // It isn't a valid double value.
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_HEADING_MALFORMED;
//				log.error("Verbatim heading ("+locspec.vheading+") not in GeorefDictionary and not a numerrical heading.");
			}
			if( d != null) {
				locspec.iheading = locspec.vheading;
				if( d.doubleValue() < 0.0 || d.doubleValue() > 360.0 ) {
					problems = true;
					locspec.state = LocSpecState.LOCSPEC_ERROR_HEADING_MALFORMED_NUMERICAL; // Negative offsets are bad form.
					log.info("Verbatim heading ("+locspec.vheading+") is not between 0 and 360 degrees.");
				}
			}
			
			if( problems == false ) {
				locspec.state = LocSpecState.LOCSPEC_COMPLETED;
			}
		}
		return locspec.state;
	}
	
	public LocSpecState interpretHeadingEW(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.vheadingew == null ) {
			problems = true;
			locspec.iheadingew = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_HEADING_MISSING;
			log.error("Verbatim headingew missing.");
		}
		String headingewstring = GeorefDictionaryManager.getInstance().lookup(locspec.vheadingew, preferredlanguage, Concepts.headings, true);
		if( headingewstring == null ) { // Heading found in GeorefDictionary.
			problems = true;
			locspec.iheadingew = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_HEADING_NOT_FOUND;
			log.error("Verbatim headingew ("+locspec.vheadingew+") not found in GeorefDictionary.");
		}else {
			if( !headingewstring.equals("E") && !headingewstring.equals("W")) {// HeadingEW is neither East nor West.
				problems = true;
				locspec.iheadingew = null;
				locspec.state = LocSpecState.LOCSPEC_ERROR_HEADINGEW_NOT_EW;
				log.error("Verbatim headingew ("+locspec.vheadingew+") is neither east nor west.");
			}else {
				locspec.iheadingew = headingewstring;
			}
		}
		if( problems == false ) {
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
		}
		return locspec.state;
	}
	
	public LocSpecState interpretHeadingNS(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.vheadingns == null ) {
			problems = true;
			locspec.iheadingns = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_HEADING_MISSING;
			log.error("Verbatim headingns missing.");
		}
		String headingnsstring = GeorefDictionaryManager.getInstance().lookup(locspec.vheadingns, preferredlanguage, Concepts.headings, true);
		if( headingnsstring == null ) { // Heading found in GeorefDictionary.
			problems = true;
			locspec.iheadingns = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_HEADING_NOT_FOUND;
			log.error("Verbatim headingns ("+locspec.vheadingns+") not found in GeorefDictionary.");
		}else {
			if( !headingnsstring.equals("N") && !headingnsstring.equals("S")) {// HeadingNS is neither North nor South.
				problems = true;
				locspec.iheadingns = null;
				locspec.state = LocSpecState.LOCSPEC_ERROR_HEADINGNS_NOT_NS;
				log.error("Verbatim headingns ("+locspec.vheadingns+") is neither north nor south.");
			}else {
				locspec.iheadingns = headingnsstring;
			}
		}
		if( problems == false ) {
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
		}
		return locspec.state;
	}
	
	public LocSpecState interpretElevation(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.velevation == null ) {
			problems = true;
			locspec.ielevation = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_ELEVATION_MISSING;
			log.error("Verbatim elevation missing.");
		}
		if( locspec.velevationunits == null) {
			problems = true;
			locspec.ielevationunits = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_ELEVATIONUNIT_MISSING;
			log.error("Verbatim elevation units missing.");
		}
		Double d = null;
		try { // Try to make a double out of the velevation value.
			d = new Double( locspec.velevation );
		} catch(Exception e) { // It isn't a valid double value.
			problems = true;
			locspec.state = LocSpecState.LOCSPEC_ERROR_ELEVATION_MALFORMED;
			log.error("Verbatim elevation ("+locspec.velevation+") isn't a valid number.");
		}
		if( d != null) {
			locspec.ielevation = locspec.velevation;
		}
		String u = UnitConverterManager.getInstance().getStandardUnitString(locspec.velevationunits, SupportedLanguages.english);
//		Units u = UnitConverterManager.getInstance().getStandardUnit(locspec.velevationunits);
		if ( u == null ) {
			problems = true;
			locspec.ielevationunits = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_ELEVATIONUNIT_NOT_FOUND;
			log.error("Verbatim elevation unit ("+locspec.velevationunits+") not found in the GeorefDictionary.");
		}else {
			locspec.ielevationunits = new String(u);
//			locspec.ielevationunits = u.name();
		}
		if( problems == false ) {
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
//			log.info("Elevation interpretation completed successfully.");
		}
		return locspec.state;
	}
	
	public LocSpecState interpretDatum(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.vdatum == null ) {
			problems = true;
			locspec.idatum = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_DATUM_MISSING;
			log.error("Verbatim datum missing.");
		}
		Datum d = DatumManager.getInstance().getDatum(locspec.vdatum);
		if ( d == null ) {
			problems = true;
			locspec.idatum = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_DATUM_NOT_FOUND;
			log.error("Verbatim datum ("+locspec.vdatum+") not found in the DatumManager.");
		}else {
			locspec.idatum = d.getCode();
		}
		if( problems == false ) {
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
//			log.info("Datum interpretation completed successfully.");
		}
		return locspec.state;
	}
	
	public LocSpecState interpretLatLng(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.vlat == null && locspec.vlng == null) {
			problems = true;
			locspec.ilat = null;
			locspec.ilng = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_LATLNG_MISSING;
			log.error("Verbatim latitude and longitude missing.");
		}else { // vlat and vlng are not both null
			if(locspec.vlat == null ){ // only vlat missing
				problems = true;
				locspec.ilat = null;
				locspec.ilng = null;
				locspec.state = LocSpecState.LOCSPEC_ERROR_LAT_MISSING;
				log.error("Verbatim latitude missing.");
			} 
			if(locspec.vlng == null ){ // only vlng missing
				problems = true;
				locspec.ilat = null;
				locspec.ilng = null;
				locspec.state = LocSpecState.LOCSPEC_ERROR_LNG_MISSING;
				log.error("Verbatim longitude missing.");
			}
		}
		if( problems == false ) { // vlat and vlng both provided
			// turn these into decimal degrees if possible
			// There are many possible patterns, but not all are unambiguously interpretable
			// Interpret only decimal degrees for now
			Double d = null;
			try { // Try to make a double out of the vlat value.
				d = new Double( locspec.vlat );
			} catch(Exception e) { // It isn't a valid double value.
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_LAT_MALFORMED;
				log.error("Verbatim latitude ("+locspec.vlat+") isn't a valid decimal latitude.");
			}
			if( d != null ) {
				if( d.doubleValue()<-90 || d.doubleValue()>90) { // not in the valid range for a decimal latitude
					problems = true;
					locspec.state = LocSpecState.LOCSPEC_ERROR_LAT_OUT_OF_RANGE;
					log.error("Verbatim latitude ("+locspec.vlat+") is outside the range -90 <= vlat <= 90.");
				}
			}
			try { // Try to make a double out of the vlat value.
				d = new Double( locspec.vlng );
			} catch(Exception e) { // It isn't a valid double value.
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_LNG_MALFORMED;
				log.error("Verbatim longitude ("+locspec.vlng+") isn't a valid decimal longitude.");
			}
			if( d != null ) {
				if( d.doubleValue()<-180 || d.doubleValue()>180) { // not in the valid range for a decimal longitude
					problems = true;
					locspec.state = LocSpecState.LOCSPEC_ERROR_LNG_OUT_OF_RANGE;
					log.error("Verbatim longitude ("+locspec.vlng+") is outside the range -180 <= vlat <= 180.");
				}
			}
		}
		if( problems == false ) { // vlat and vlng were interpretable
			locspec.ilat=locspec.vlat;
			locspec.ilng=locspec.vlng;
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
//			log.info("LatLng interpretation completed successfully.");
		}
		return locspec.state;
	}
	
	public LocSpecState interpretUTM(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.vutmzone == null && locspec.vutme == null && locspec.vutmn == null) {
			problems = true;
			locspec.iutmzone = null;
			locspec.iutme = null;
			locspec.iutmn = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_UTM_MISSING;
			log.error("Verbatim utmzone, utme, and utmn all missing.");
		}else { // vutmzone, vutme and vutmn are all given
			if(locspec.vutmzone == null ){ // vutmzone missing
				problems = true;
				locspec.iutmzone = null;
				locspec.iutme = null;
				locspec.iutmn = null;
				locspec.state = LocSpecState.LOCSPEC_ERROR_UTMZONE_MISSING;
				log.error("Verbatim utmzone missing.");
			} 
			if(locspec.vutme == null ){ // vutme missing
				problems = true;
				locspec.iutmzone = null;
				locspec.iutme = null;
				locspec.iutmn = null;
				locspec.state = LocSpecState.LOCSPEC_ERROR_UTME_MISSING;
				log.error("Verbatim utme missing.");
			} 
			if(locspec.vutmn == null ){ // vutmn missing
				problems = true;
				locspec.iutmzone = null;
				locspec.iutme = null;
				locspec.iutmn = null;
				locspec.state = LocSpecState.LOCSPEC_ERROR_UTMN_MISSING;
				log.error("Verbatim utmn missing.");
			}
		}
		String zone = locspec.vutmzone.toUpperCase().trim();
		char latitudebandletter = zone.charAt(zone.length()-1);
		String zonenumber = zone.substring(0,zone.indexOf(latitudebandletter)).trim();
		Integer izone = null;
		Integer iutme = null;
		String leftofE = null;
		Integer iutmn = null;
		String leftofN = null;
		if( problems == false ) { // vutmzone, vutme, and vutmn all provided
			// There are at least three possible unambiguous patterns, 
			// UTMZone,including the latitude band letter plus
			// 1) 5 digit utme with up to 6 digit utmn - not implemented yet
			// 2) 6 digit utme with up to 7 digit utmn - not implemented yet
			// 3) 7 digit utme with up to 8 digit utmn
			
			// To be unambiguous on its own, the UTM must have a zone with the latitude band letter
			String validlatbandletters = new String("CDEFGHJKLMNPQRSTUVWX");
			if( validlatbandletters.indexOf(latitudebandletter) == -1 ) { // not a valid latitude band letter
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_UTMZONE_MISSING_BAND_LETTER;
				log.error("Verbatim utmzone ("+locspec.vutmzone+") doesn't end with a valid latitude band letter.");
			}else {
				try { // Try to make an integer out of the rest of utmzone.
					izone = new Integer( zonenumber );
				} catch(Exception e) { // It isn't a valid integer value.
					problems = true;
					locspec.state = LocSpecState.LOCSPEC_ERROR_UTMZONE_MALFORMED;
					log.error("Verbatim utmzone ("+locspec.vutmzone+") doesn't start with an integer.");
				}
				if( izone != null ) {
					if( izone.intValue()<1 || izone.intValue()>60) { // not in the valid range for a utmzone
						problems = true;
						locspec.state = LocSpecState.LOCSPEC_ERROR_UTMZONE_OUT_OF_RANGE;
						log.error("Verbatim utmzone ("+locspec.vutmzone+") is outside the range 1 <= vutmzone <= 60.");
					}
				}
			}
			if( locspec.vutme.trim().toUpperCase().charAt(locspec.vutme.trim().length()-1) == 'E' ) {
				leftofE = new String(locspec.vutme.trim().substring(0,locspec.vutme.trim().toUpperCase().indexOf('E')).trim());
			}else {
				leftofE = new String(locspec.vutme.trim());
			}
			try { // Try to make an integer out of utme.
				iutme = new Integer( leftofE );
			} catch(Exception e) { // It isn't a valid integer value.
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_UTME_MALFORMED;
				log.error("Verbatim utme ("+locspec.vutme+") isn't an integer, nor an interger followed by 'E'.");
			}
			if( iutme != null ) {
				if( iutme.intValue()<160000 || iutme.intValue()>834000) { // not in the valid range for a utme
					problems = true;
					locspec.state = LocSpecState.LOCSPEC_ERROR_UTME_OUT_OF_RANGE;
					log.error("Verbatim utme ("+locspec.vutme+") is outside the range 160000 <= vutme <= 834000.");
				}
			}
			if( locspec.vutmn.trim().toUpperCase().charAt(locspec.vutmn.trim().length()-1) == 'N' ) {
				leftofN = new String(locspec.vutmn.trim().substring(0,locspec.vutmn.trim().toUpperCase().indexOf('N')).trim());
			}else {
				leftofN = new String(locspec.vutmn.trim());
			}
			try { // Try to make an integer out of utmn.
				iutmn = new Integer( leftofN );
			} catch(Exception e) { // It isn't a valid integer value.
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_UTMN_MALFORMED;
				log.error("Verbatim utmn ("+locspec.vutmn+") isn't an integer.");
			}
			if( iutmn != null ) {
				if( iutmn.intValue()<0 || iutmn.intValue()>10000000) { // not in the valid range for a utmn
					problems = true;
					locspec.state = LocSpecState.LOCSPEC_ERROR_UTMN_OUT_OF_RANGE;
					log.error("Verbatim utmn ("+locspec.vutmn+") is outside the range 0 <= vutmn <= 10000000.");
				}
			}
		}
		if( problems == false ) { // vutmzone, vutme, and vutmn were interpretable
			locspec.iutmzone=zonenumber+latitudebandletter;
			locspec.iutme=leftofE+"E";
			locspec.iutmn=leftofN+"N";
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
//			log.info("UTM interpretation completed successfully.");
		}
		return locspec.state;
	}
	
	public LocSpecState interpretTRS(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		// No precessing done on TRS yet.
		if( problems == false ) { // TRS is interpretable
			locspec.itownship=locspec.vtownship;
			locspec.itownshipdir=locspec.vtownshipdir;
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
			locspec.irange=locspec.vrange;
			locspec.irangedir=locspec.vrangedir;
			locspec.isection=locspec.vsection;
//			log.info("TRS interpretation completed successfully.");
		}
		return locspec.state;
	}

	public LocSpecState interpretSubdivision(LocSpec locspec) {
		boolean problems = false;
		if( locspec == null ) return null;
		if( locspec.vsubdivision == null ) {
			problems = true;
			locspec.isubdivision = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_SUBDIVISION_MISSING;
			log.error("Verbatim subdivision missing.");
		}
		locspec.isubdivision = locspec.vsubdivision; // pass it through untouched for now. ShapeManager will have to deal with it.
		if( problems == false ) { // subdivision is interpretable
			locspec.isubdivision=locspec.vsubdivision;
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
//			log.info("Subdivision interpretation completed successfully.");
		}
		return locspec.state;
	}
}