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

package org.biogeomancer.records;

import java.util.ArrayList;

import org.biogeomancer.managers.GeorefDictionaryManager;
import org.biogeomancer.managers.LocSpecManager;
import org.biogeomancer.managers.UnitConverterManager;
import org.biogeomancer.utils.Concepts;
import org.biogeomancer.utils.SupportedLanguages;

//TODO: Decide if every possible output From BG can also be an input.
//In other words, should there be vcoordinateuncertainty,
//icoordinateuncertainty, etc.
public class LocSpec {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out
			.println("LocSpec.main requires a language code (en, es, pt, fr) and boolean choice to show geometries as arguments.");
			return;
		}
		SupportedLanguages lang = SupportedLanguages.english;
		if (args[0].equalsIgnoreCase("es")) {
			lang = SupportedLanguages.spanish;
		} else if (args[0].equalsIgnoreCase("pt")) {
			lang = SupportedLanguages.portuguese;
		} else if (args[0].equalsIgnoreCase("fr")) {
			lang = SupportedLanguages.french;
		}
		boolean showgeom = false;
		if (args[1].equalsIgnoreCase("true"))
			showgeom = true;

		LocSpec ls = LocSpec.makeOne();
		ls.interpretVerbatimAttributes(lang);
		System.out.println(ls.toXML(showgeom));
	}

	public static LocSpec makeOne() {
		LocSpec ls = new LocSpec();
		ls.featurename = new String("Missoula");
		ls.voffset = new String("12");
		ls.voffsetunit = new String("miles");
		ls.vheading = new String("Northwest");
		ls.vsubdivision = new String("NW 1/4 of SE 1/4");
		ls.voffsetew = new String("7");
		ls.voffsetewunit = new String("kms");
		ls.voffsetns = new String("10");
		ls.voffsetnsunit = new String("yards");
		ls.vheadingew = new String("E");
		ls.vheadingns = new String("N");
		ls.velevation = new String("3240");
		ls.velevationunits = new String("feet");
		ls.vlat = new String("35d12'31\"N");
		ls.vlng = new String("-121.23563");
		ls.vdatum = new String("North American Datum 1927");
		ls.vutmzone = new String("14B");
		ls.vutme = new String("133572e");
		ls.vutmn = new String("2462348n");
		ls.vtownship = new String("20");
		ls.vtownshipdir = new String("N");
		ls.vrange = new String("17");
		ls.vrangedir = new String("W");
		ls.vsection = new String("27");
		FeatureInfo fi = FeatureInfo.makeOne();
		ls.featureinfos.add(fi);
		ls.state = LocSpecState.LOCSPEC_COMPLETED;
		return ls;
	}

	public LocSpecState state; // The processing state of the LocSpec
	// public MetaData metadata; // metadata object to track processing information,
	// such as timestamps and methods.
	// The following attributes are to hold the verbatim values parsed from the
	// locality text
	public String featurename; // a featurename parsed from the Clause
	public String voffset; // the linear distance from the featurename
	public String voffsetunit; // the unit of distance of the offset
	public String vheading; // the direction from the featurename
	public String vsubdivision; // a subdivision of the featurename, or of the
	// township, range, section
	public String voffsetew; // the offset due east or west from the
	// featurename if the Clause.LocType is
	// "orthogonal offsets from a feature"
	public String voffsetewunit; // the unit of distance of the offsetew
	public String voffsetns; // the offset due north or south from the
	// featurename if the Clause.LocType is
	// "orthogonal offsets from a feature"
	public String voffsetnsunit; // the unit of distance of the offsetns
	public String vheadingew; // the direction, east or west, from the
	// featurename if the Clause.LocType is
	// "orthogonal offsets from a feature"
	public String vheadingns; // the direction, north or south, from the
	// featurename if the Clause.LocType is
	// "orthogonal offsets from a feature"
	public String velevation; // the numeric part of the elevation parsed
	// from the Clause (or provided in an
	// elevation attribute)
	public String velevationunits; // an elevation parsed from the Clause (or
	// provided in an elevation attribute)
	public String vlat; // a latitude parsed from the Clause (or
	// provided in a Latitude attribute)
	public String vlng; // a longitude parsed from the Clause (or
	// provided in a Longitude attribute)
	public String vdatum; // a geodetic datum parsed from the Clause (or
	// provided in a Datum attribute)
	public String vutmzone; // a Universal Tranverse Mercator Zone parsed
	// from the Clause (or provided in a UTM
	// attribute)
	public String vutme; // a Universal Tranverse Mercator Easting
	// parsed from the Clause (or provided in a
	// UTM attribute)
	public String vutmn; // a Universal Tranverse Mercator Northing
	// parsed from the Clause (or provided in a
	// UTM attribute)
	public String vtownship; // a PLSS Township parsed from the Clause
	public String vtownshipdir; // the direction, north or south, of a PLSS
	// Township parsed from the Clause
	public String vrange; // a PLSS Range parsed from the Clause
	public String vrangedir; // the direction, east or west, of a PLSS
	// Range parsed from the Clause
	public String vsection; // a PLSS Section parsed from the Clause

	// The following attributes are to hold the values interpreted from the
	// verbatim values above
	public String ioffset; // the linear distance from the featurename
	// expressed as a double
	public String ioffsetunit; // the unit of distance of the offset
	public String iheading; // the direction from the featurename
	public String isubdivision; // a subdivision of the featurename, or of the
	// township, range, section
	public String ioffsetew; // the offset due east or west from the
	// featurename if the Clause.LocType is
	// "orthogonal offsets from a feature"
	public String ioffsetewunit; // the unit of distance of the offsetew
	public String ioffsetns; // the offset due north or south from the
	// featurename if the Clause.LocType is
	// "orthogonal offsets from a feature"
	public String ioffsetnsunit; // the unit of distance of the offsetns
	public String iheadingew; // the direction, east or west, from the
	// featurename if the Clause.LocType is
	// "orthogonal offsets from a feature"
	public String iheadingns; // the direction, north or south, from the
	// featurename if the Clause.LocType is
	// "orthogonal offsets from a feature"
	public String ielevation; // an elevation parsed from the Clause
	public String ielevationunits; // an elevation parsed from the Clause
	public String ilat; // a latitude parsed from the Clause
	public String ilng; // a longitude parsed from the Clause
	public String idatum; // a geodetic datum parsed from the Clause
	public String iutmzone; // a Universal Tranverse Mercator Zone parsed
	// from the Clause
	public String iutme; // a Universal Tranverse Mercator Easting
	// parsed from the Clause
	public String iutmn; // a Universal Tranverse Mercator Northing
	// parsed from the Clause
	public String itownship; // a PLSS Township parsed from the Clause
	public String itownshipdir; // the direction, north or south, of a PLSS
	// Township parsed from the Clause
	public String irange; // a PLSS Range parsed from the Clause
	public String irangedir; // the direction, east or west, of a PLSS
	// Range parsed from the Clause
	public String isection; // a PLSS Section parsed from the Clause
	public LocSpecManager lsm; // LocSpecManager to access dictionary,
	// unitconverter, and log errors.

	public ArrayList<FeatureInfo> featureinfos; // a list of featureinfos for

	// which the featurename returned
	// a match from the gazetteer
	// lookup
	public LocSpec() { // constructor
		this.featureinfos = new ArrayList<FeatureInfo>();
		this.state = LocSpecState.LOCSPEC_CREATED;
	}

	public LocSpecManager addLocSpecManager(LocSpecManager lsm) {
		if (lsm == null) {
			try {
				this.lsm = LocSpecManager.getInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			this.lsm = lsm;
		}
		return this.lsm;
	}

	public boolean areHeadingsOrthogonal() { // Test the validity of the
		// interpreted offset as a
		// non-negative distance with distance
		// understood units.
		if (!isHeading(iheadingew) || !isHeading(iheadingns))
			return false; // one
		// or
		// the
		// other
		// isn't
		// a
		// valid
		// heading
		// at
		// all
		// Config config = Config.getInstance();
		// HashMap locales = (HashMap)config.resources.get("locales");
		if (this.lsm == null)
			addLocSpecManager(new LocSpecManager());
		GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
		String headingstring = gd.lookup(iheadingew, SupportedLanguages.english,
				Concepts.headings, true);
		// if iheadingew isn't E or W
		if (!headingstring.equalsIgnoreCase("E")
				&& !headingstring.equalsIgnoreCase("W"))
			return false;
		headingstring = gd.lookup(iheadingns, SupportedLanguages.english,
				Concepts.headings, true);
		// if iheadingns isn't N or S
		if (!headingstring.equalsIgnoreCase("N")
				&& !headingstring.equalsIgnoreCase("S"))
			return false;
		return true;
	}

	public int finishTRSSection(){
		Integer sec = null;
		try { // Try to make a double out of the vsection value.
			sec = new Integer(vsection);
			if (sec.intValue() > 0 && sec.intValue() < 37) {
				if (vsection != null)
					isection = new String(vsection);
			}
		} catch (Exception e) {
			return 0;
		}
		if(isection!=null){
			featurename=featurename.concat(" Section "+isection);
			return sec.intValue();
		}
		return 0;
	}

	public double getElevationUncertaintyInMeters() {
		// determine the uncertainty associated with the elevation as found in
		// velevation and return that value in meters
		return 0.0; // for now...
	}

	public double getHeadingInDegrees() {
		if (!isHeading())
			return 0.0; // This is not a valid result, isHeading()
		// should be checked before using a result
		// from this method.
		return getHeadingInDegrees(iheading);
	}

	public double getHeadingInDegrees(String heading) {
		if (this.lsm == null)
			addLocSpecManager(new LocSpecManager());
		GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
		String headingstring = gd.lookup(heading, SupportedLanguages.english,
				Concepts.headings, true);
		if (headingstring != null) {
			// Heading was found in the GeorefDictionary.
			if (headingstring.equals("N"))
				return 0.0;
			if (headingstring.equals("E"))
				return 90.0;
			if (headingstring.equals("S"))
				return 180.0;
			if (headingstring.equals("W"))
				return 270.0;
			if (headingstring.equals("NE"))
				return 45.0;
			if (headingstring.equals("SE"))
				return 135.0;
			if (headingstring.equals("SW"))
				return 225.0;
			if (headingstring.equals("NW"))
				return 315.0;
			if (headingstring.equals("NNE"))
				return 22.5;
			if (headingstring.equals("ENE"))
				return 67.5;
			if (headingstring.equals("ESE"))
				return 112.5;
			if (headingstring.equals("SSE"))
				return 157.5;
			if (headingstring.equals("NNW"))
				return 337.5;
			if (headingstring.equals("WNW"))
				return 292.5;
			if (headingstring.equals("WSW"))
				return 247.5;
			if (headingstring.equals("SSW"))
				return 202.5;
		}
		// Heading not found in the GeorefDictionary, but it is a valid heading, so
		// it must be a number
		Double dheading = new Double(heading);
		return dheading.doubleValue();
	}

	public double getHeadingUncertaintyInDegrees() {
		// check validity of iheading as found in dictionary
		if (!isHeading())
			return 0.0; // This is not a valid result, isHeading()
		// should be checked before using a result
		// from this method.
		return getHeadingUncertaintyInDegrees(iheading);
	}

	public double getHeadingUncertaintyInDegrees(String tryheading) {
		if (this.lsm == null)
			addLocSpecManager(new LocSpecManager());
		GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
		String headingstring = gd.lookup(tryheading, SupportedLanguages.english,
				Concepts.headings, true);
		if (headingstring != null) { // Heading was found in the GeorefDictionary.
			if (headingstring.equals("N") || headingstring.equals("E")
					|| headingstring.equals("S") || headingstring.equals("W"))
				return 45.0;
			if (headingstring.equals("NE") || headingstring.equals("SE")
					|| headingstring.equals("SW") || headingstring.equals("NW"))
				return 22.5;
			if (headingstring.equals("NNE") || headingstring.equals("ENE")
					|| headingstring.equals("ESE") || headingstring.equals("SSE"))
				return 11.25;
			if (headingstring.equals("NNW") || headingstring.equals("WNW")
					|| headingstring.equals("WSW") || headingstring.equals("SSW"))
				return 11.25;
		}
		// Heading not found in the GeorefDictionary, but it is a valid heading, so
		// it must be a number
		// Determine the uncertainty based on the significant digits
		Double heading = new Double(iheading);
		double headinguncertainty = 0.0;
		int sigdigits = 0; // sigdigits is to store the number of significant
		// digits to the right of the decimal
		// double headinglog10 = Math.log10(heading);
		// First check to see if the verbatim original heading specified significant
		// digits occupied by 0's. These would be 0's following the decimalindicator
		// ('.' or ',').
		if (vheading.indexOf(".") != -1) {
			// There is a '.' decimal indicator in the vheading
			sigdigits = vheading.trim().length() - vheading.trim().indexOf("."); // There
			// are
			// digits
			// to
			// the
			// right
			// of
			// the
			// decimal
			// in
			// the
			// verbatim
			// heading
		} else if (vheading.indexOf(",") != -1) {
			// There is a ',' decimal indicator in the vheading
			sigdigits = vheading.trim().length() - vheading.trim().indexOf(","); // There
			// are
			// digits
			// to
			// the
			// right
			// of
			// the
			// decimal
			// in
			// the
			// verbatim
			// heading
		}
		// Otherwise there is no decimal indicator in the original vheading.
		if (sigdigits > 0) {
			// If the last digit is a zero, the original was specified to that level
			// of precision.
			if (vheading.trim().endsWith("0"))
				headinguncertainty = 1.0 * Math.pow(10.0, -1.0 * sigdigits); // Example:
			// vheading
			// =
			// "270.0"
			// headinguncertainty
			// =
			// 0.1
		} else if ((heading / 10.0) - Math.rint(heading / 10.0) == 0) {
			// If the iheading is an integer multiple of ten
			headinguncertainty = 10.0;
		} else if (heading - Math.rint(heading) == 0) {
			// If the iheading is an integer
			headinguncertainty = 1.0;
		} else {
			// Otherwise get the fractional part of the interpreted heading. We'll use
			// this to determine uncertainty.
			double intpart = Math.floor(heading.doubleValue()); // The integer part
			// of the iheading
			// expressed as a
			// double.
			double fracpart = heading.doubleValue() - intpart; // The fractional part
			// of the iheading
			// expressed as a
			// double.
			// test to see if the fracpart can be turned in to any of the target
			// fractions
			// fracpart/testfraction = integer within a predefined level of tolerance
			double[] denominators = { 2.0, 6.0, 60.0, 120.0, 360.0, 3600.0 }; // 30',
			// 10',
			// 1',
			// 30",
			// 10", 1"
			for (int i = 0; i < denominators.length; i++) {
				double numerator = fracpart * denominators[i];
				double nearestint = Math.rint(numerator);
				// If the numerator is within tolerance of being an integer, then the
				// denominator represents the distance precision in the original units.
				if (Math.abs(numerator - nearestint) < 0.0000001) { // This denominator
					// appears to
					// represent a
					// viable fraction.
					headinguncertainty = 1.0 / denominators[i];
					i = denominators.length;
				}
			}
		}
		return headinguncertainty;
	}

	public double getOffset() {
		return this.getOffset(ioffset, ioffsetunit);
	}

	// TODO: TEST ME
	public double getOffset(String offsetstring, String offsetunitstring) {
		if (!this.isOffset(offsetstring, offsetunitstring))
			return 0.0; // Note: 0
		// isn't
		// necessarily
		// what
		// you
		// want in
		// the
		// case of
		// an
		// invalid
		// offset,
		// you
		// should
		// check
		// before
		// making
		// the
		// call to
		// this
		// method.
		// offsetstring and offsetunitstring contain a valid convertible offset that
		// can be represented in a non-negative double with a standardized unit.
		Double offset = new Double(offsetstring);
		return offset.doubleValue();
	}

	public double getOffsetEW() {
		return this.getOffset(ioffsetew, ioffsetewunit);
	}

	public double getOffsetEWInMeters() {
		int sign = 1;
		if (this.iheadingew.equalsIgnoreCase("W"))
			sign = -1;
		return sign * getOffsetInMeters(this.ioffsetew, this.ioffsetewunit);
	}

	public double getOffsetEWUncertaintyInMeters() {
		return getOffsetUncertaintyInMeters(ioffsetew, ioffsetewunit);
	}

	public double getOffsetInMeters() {
		return getOffsetInMeters(this.ioffset, this.ioffsetunit);
	}

	// TODO: TEST ME
	public double getOffsetInMeters(String offsetstring, String offsetunit) {
		if (!this.isOffset(offsetstring, offsetunit))
			return 0.0; // Note: 0 isn't
		// necessarily
		// what you want
		// in the case
		// of an invalid
		// offset, you
		// should check
		// before making
		// the call to
		// this method.

		// offsetstring and offsetunit contain a valid convertible offset that can
		// be represented in a non-negative double with a standardized unit.
		if (this.lsm == null)
			addLocSpecManager(new LocSpecManager());
		UnitConverterManager uc = UnitConverterManager.getInstance();
		Double d = new Double(offsetstring);
		double meters = uc.unit2meters(d.doubleValue(), offsetunit); // Convert
		// the value
		// of the
		// offset
		// from the
		// original
		// units in
		// ioffsetunit
		// to
		// meters.
		return meters;
	}

	public double getOffsetNS() {
		return this.getOffset(ioffsetns, ioffsetnsunit);
	}

	public double getOffsetNSInMeters() {
		int sign = 1;
		if (this.iheadingns.equalsIgnoreCase("S"))
			sign = -1;
		return sign * getOffsetInMeters(this.ioffsetns, this.ioffsetnsunit);
	}

	public double getOffsetNSUncertaintyInMeters() {
		return getOffsetUncertaintyInMeters(ioffsetns, ioffsetnsunit);
	}

	public double getOffsetUncertaintyInMeters(String offsetstring,
			String offsetunit) {
		// Determine the uncertainty associated with the offsetstring and
		// offsetunit. Return that value in meters.
		// Should check that isOffset() == true before calling this method,
		// otherwise the return value of zero will be misleading.
		// Methods derived from Wieczorek, et al. 2004.
		if (!this.isOffset(offsetstring, offsetunit))
			return 0.0;

		// offsetstring and offsetunit contain a valid convertible offset that can
		// be represented in a non-negative double with a standardized unit.
		double offsetuncertainty = 0.0;
		double offset = this.getOffset(offsetstring, offsetunit);
		int sigdigits = 0; // sigdigits is to store the number of significant
		// digits to the right of the decimal.
		double offsetlog10 = Math.log10(offset);
		// First check to see if the verbatim original offset specified significant
		// digits occupied by 0's. These would be 0's following the decimalindicator
		// ('.' or ',').
		if (offsetstring.indexOf(".") != -1) { // There is a '.' decimal indicator
			// in the offsetstring.
			sigdigits = offsetstring.trim().length()
			- offsetstring.trim().indexOf(".") - 1; // There
			// are
			// digits
			// to
			// the
			// right
			// of
			// the
			// decimal
			// in
			// the
			// verbatim
			// offset.
		} else if (offsetstring.indexOf(",") != -1) { // There is a ',' decimal
			// indicator in the
			// offsetstring.
			sigdigits = offsetstring.trim().length()
			- offsetstring.trim().indexOf(",") - 1; // There
			// are
			// digits
			// to
			// the
			// right
			// of
			// the
			// decimal
			// in
			// the
			// verbatim
			// offset.
		} // Otherwise there is no decimal indicator in the offsetstring.
		if (sigdigits > 0) { // If the last digit is a zero, the original was
			// specified to that level of precision.
			if (offsetstring.trim().endsWith("0")) {
				offsetuncertainty = 1.0 * Math.pow(10.0, -1.0 * sigdigits); // Example:
				// offsetstring
				// = "10.0"
				// offsetuncertainty
				// = 0.1.
			} else if (offsetlog10 >= 0
					&& Math.abs(offsetlog10 - Math.rint(offsetlog10)) < 0.0001) { // If
				// the
				// ioffset
				// is a
				// positive
				// integer
				// power
				// of
				// ten.
				if (Math.abs(offsetlog10) < 0.0001)
					offsetuncertainty = 1.0; // If that
				// power
				// is zero
				// -
				// offset
				// is 1.
				else
					offsetuncertainty = Math.pow(10.0, Math.rint(offsetlog10)); // For
				// 10,
				// 100,
				// 1000,
				// etc.
				// Note
				// that
				// this
				// will
				// be
				// divided
				// by 2
				// as a
				// last
				// step
				// in
				// the
				// uncertainty
				// calculation
				// in
				// this
				// method.
			} else { // Otherwise get the fractional part of the interpreted offset.
				// We'll use this to determine uncertainty.
				Double d = new Double(offsetstring);
				double intpart = Math.floor(d.doubleValue()); // The integer part of
				// the offsetstring
				// expressed as a
				// double.
				double fracpart = d.doubleValue() - intpart; // The fractional part of
				// the offsetstring
				// expressed as a double.
				// Test to see if the fracpart can be turned in to any of the target
				// fractions.
				// fracpart/testfraction = integer within a predefined level of
				// tolerance.
				double[] denominators = { 2.0, 3.0, 4.0, 8.0, 10.0, 100.0, 1000.0 };
				for (int i = 0; i < denominators.length; i++) {
					double numerator = fracpart * denominators[i];
					double nearestint = Math.rint(numerator);
					// If the numerator is within tolerance of being an integer, then the
					// denominator represents the distance precision in the original
					// units.
					if (Math.abs(numerator - nearestint) < 0.001) { // This denominator
						// appears to
						// represent a viable
						// fraction.
						offsetuncertainty = 1.0 / denominators[i];
						i = denominators.length;
					}
				}
			}
		} else { // sigdigits = 0, which means the distance is specified as an
			// integer, e.g., 16
			offsetuncertainty = 1.0;
		}
		// Methods taken from Wieczorek, et al. 2004, but modified for fractions to
		// be one-half of that described in the paper, which
		// we now believe to be unreasonably conservative.
		offsetuncertainty *= 0.5;
		if (this.lsm == null)
			addLocSpecManager(new LocSpecManager());
		UnitConverterManager uc = UnitConverterManager.getInstance();
		// Now just convert it to meters and send it home.
		return uc.unit2meters(offsetuncertainty, offsetunit);
	}

	public void interpretElevation(SupportedLanguages lang) {
		ielevation = ielevationunits = null;
		if (isElevation(velevation, velevationunits) == false)
			return;
		ielevation = new String(velevation);
		String u = UnitConverterManager.getInstance().getStandardUnitString(
				velevationunits, lang);
		ielevationunits = new String(u);
	}

	public void interpretHeading(SupportedLanguages lang) {
		iheading = null;
		if (isHeading(vheading) == false)
			return;
		String headingstring = GeorefDictionaryManager.getInstance().lookup(
				vheading, lang, Concepts.headings, true);
		if (headingstring != null) { // Heading found in GeorefDictionary.
			iheading = new String(headingstring);
			return;
		}
		iheading = new String(vheading);
	}

	public void interpretHeadingEW(SupportedLanguages lang) {
		iheadingew = null;
		if (isHeading(vheadingew) == false)
			return;
		String headingstring = GeorefDictionaryManager.getInstance().lookup(
				vheadingew, lang, Concepts.headings, true);
		if (headingstring != null) { // Heading found in GeorefDictionary.
			iheadingew = new String(headingstring);
			return;
		}
		iheadingew = new String(vheadingew);
	}

	public void interpretHeadingNS(SupportedLanguages lang) {
		iheadingns = null;
		if (isHeading(vheadingns) == false)
			return;
		String headingstring = GeorefDictionaryManager.getInstance().lookup(
				vheadingns, lang, Concepts.headings, true);
		if (headingstring != null) { // Heading found in GeorefDictionary.
			iheadingns = new String(headingstring);
			return;
		}
		iheadingns = new String(vheadingns);
	}

	/*
	 * public LocSpecState interpretDatum(LocSpec locspec) { boolean problems =
	 * false; if( locspec == null ) return null; if( locspec.vdatum == null ) {
	 * problems = true; locspec.idatum = null; locspec.state =
	 * LocSpecState.LOCSPEC_ERROR_DATUM_MISSING; log.error("Verbatim datum
	 * missing."); } Datum d =
	 * DatumManager.getInstance().getDatum(locspec.vdatum); if ( d == null ) {
	 * problems = true; locspec.idatum = null; locspec.state =
	 * LocSpecState.LOCSPEC_ERROR_DATUM_NOT_FOUND; log.error("Verbatim datum
	 * ("+locspec.vdatum+") not found in the DatumManager."); }else {
	 * locspec.idatum = d.getCode(); } if( problems == false ) { locspec.state =
	 * LocSpecState.LOCSPEC_COMPLETED; // log.info("Datum interpretation completed
	 * successfully."); } return locspec.state; }
	 */
	// TODO: Uncomment and test Lat/Long interpretation
	public LocSpecState interpretLatLng(LocSpec locspec) {
		boolean problems = false;
		if (locspec == null)
			return null;
		if (locspec.vlat == null && locspec.vlng == null) {
			problems = true;
			locspec.ilat = null;
			locspec.ilng = null;
			locspec.state = LocSpecState.LOCSPEC_ERROR_LATLNG_MISSING;
			// log.error("Verbatim latitude and longitude missing.");
		} else { // vlat and vlng are not both null
			if (locspec.vlat == null) { // only vlat missing
				problems = true;
				locspec.ilat = null;
				locspec.ilng = null;
				locspec.state = LocSpecState.LOCSPEC_ERROR_LAT_MISSING;
				// log.error("Verbatim latitude missing.");
			}
			if (locspec.vlng == null) { // only vlng missing
				problems = true;
				locspec.ilat = null;
				locspec.ilng = null;
				locspec.state = LocSpecState.LOCSPEC_ERROR_LNG_MISSING;
				// log.error("Verbatim longitude missing.");
			}
		}
		if (problems == false) { // vlat and vlng both provided
			// turn these into decimal degrees if possible
			// There are many possible patterns, but not all are unambiguously
			// interpretable
			// Interpret only decimal degrees for now
			Double d = null;
			try { // Try to make a double out of the vlat value.
				d = new Double(locspec.vlat);
			} catch (Exception e) { // It isn't a valid double value.
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_LAT_MALFORMED;
				// log.error("Verbatim latitude ("+locspec.vlat+") isn't a valid decimal
				// latitude.");
			}
			if (d != null) {
				if (d.doubleValue() < -90 || d.doubleValue() > 90) { // not in the valid
					// range for a decimal
					// latitude
					problems = true;
					locspec.state = LocSpecState.LOCSPEC_ERROR_LAT_OUT_OF_RANGE;
					// log.error("Verbatim latitude ("+locspec.vlat+") is outside the range -90 <=
					// vlat <= 90.");
				}
			}
			try { // Try to make a double out of the vlat value.
				d = new Double(locspec.vlng);
			} catch (Exception e) { // It isn't a valid double value.
				problems = true;
				locspec.state = LocSpecState.LOCSPEC_ERROR_LNG_MALFORMED;
				// log.error("Verbatim longitude ("+locspec.vlng+") isn't a valid decimal
				// longitude.");
			}
			if (d != null) {
				if (d.doubleValue() < -180 || d.doubleValue() > 180) { // not in the valid
					// range for a
					// decimal longitude
					problems = true;
					locspec.state = LocSpecState.LOCSPEC_ERROR_LNG_OUT_OF_RANGE;
					// log.error("Verbatim longitude ("+locspec.vlng+") is outside the range -180 <=
					// vlat <= 180.");
				}
			}
		}
		if (problems == false) { // vlat and vlng were interpretable
			locspec.ilat = locspec.vlat;
			locspec.ilng = locspec.vlng;
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
			// log.info("LatLng interpretation completed successfully.");
		}
		return locspec.state;
	}

	public void interpretOffset(SupportedLanguages lang) {
		ioffset = ioffsetunit = null;
		if (isOffset(voffset, voffsetunit) == false)
			return;
		ioffset = new String(voffset);
		String u = UnitConverterManager.getInstance().getStandardUnitString(
				voffsetunit, lang);
		ioffsetunit = new String(u);
	}

	// public Coordinate getCoordinate(){
	// check validity of ilat as Latitude, ilng as Longitude and build Coordinate
	// with idatum
	// }

	public void interpretOffsetEW(SupportedLanguages lang) {
		ioffsetew = ioffsetewunit = null;
		if (isOffset(voffsetew, voffsetewunit) == false)
			return;
		ioffsetew = new String(voffsetew);
		String u = UnitConverterManager.getInstance().getStandardUnitString(
				voffsetewunit, lang);
		ioffsetewunit = new String(u);
	}

	public void interpretOffsetNS(SupportedLanguages lang) {
		ioffsetns = ioffsetnsunit = null;
		if (isOffset(voffsetns, voffsetnsunit) == false)
			return;
		ioffsetns = new String(voffsetns);
		String u = UnitConverterManager.getInstance().getStandardUnitString(
				voffsetnsunit, lang);
		ioffsetnsunit = new String(u);
	}

	public void interpretSubdivision(SupportedLanguages lang) {
		isubdivision = new String(vsubdivision); // TODO: this will do for now
	}

	public void interpretTRS() {
		itownship = itownshipdir = irange = irangedir = isection = isubdivision = null;
		if (isTRS(vtownship, vtownshipdir, vrange, vrangedir) == false)
			return;
		itownship = new String(vtownship);
		String dirstring = GeorefDictionaryManager.getInstance().lookup(
				vtownshipdir, SupportedLanguages.english, Concepts.headings, false);
		itownshipdir = new String(dirstring);
		irange = new String(vrange);
		dirstring = GeorefDictionaryManager.getInstance().lookup(vrangedir,
				SupportedLanguages.english, Concepts.headings, false);
		irangedir = new String(dirstring);
		featurename = new String("Township " + itownship + " " + itownshipdir
				+ " Range " + irange + " " + irangedir);
	}
	public LocSpecState interpretUTM() {
		// To be unambiguous on its own, the UTM must have a numerical zone
		// with an optional latitude band letter
		// There are at least three possible unambiguous patterns,
		// UTMZone, potentially including the latitude band letter, plus
		// 1) 5 digit utme with up to 6 digit utmn
		// 2) 6 digit utme with up to 7 digit utmn
		// 3) 7 digit utme with up to 8 digit utmn
		if (vutmzone == null && vutme == null && vutmn == null) {
			return LocSpecState.LOCSPEC_ERROR_UTM_MISSING;
		}

		boolean problems = false;

		if (vutmzone == null || vutme == null || vutmn == null
				|| vutmzone.length() == 0 || vutme.length() == 0 || vutmn.length() == 0) {
			problems = true;
			state = LocSpecState.LOCSPEC_ERROR_UTM_MALFORMED;
			return state;
		}
		// vutmzone, vutme and vutmn are all given and non-zero length
		Integer izone = null;
		Integer intutme = null;
		String leftofE = null;
		Integer intutmn = null;
		String leftofN = null;
		String zonenumber;
		String validlatbandletters = new String("CDEFGHJKLMNPQRSTUVWX");
		String zone = new String(vutmzone.toUpperCase().trim());
		char latitudebandletter = zone.charAt(zone.length() - 1);
		// If the latitudebandletter is an integer, assume the letter is missing and
		// the whole zone is a number
		if (latitudebandletter >= 0 && latitudebandletter <= 9) {
			latitudebandletter = ' ';
			zonenumber = new String(zone);
		} else { // Band letter is not an integer
			zonenumber = zone.substring(0, zone.indexOf(latitudebandletter)).trim();
			// if(validlatbandletters.indexOf(latitudebandletter) == -1){}
		}
		try { // Try to make an integer out of the rest of utmzone.
			izone = new Integer(zonenumber);
		} catch (Exception e) { // It isn't a valid integer value.
			problems = true;
			state = LocSpecState.LOCSPEC_ERROR_UTMZONE_MALFORMED;
		}
		if (izone != null) {
			if (izone.intValue() < 1 || izone.intValue() > 60) { // not in the valid
				// range for a utmzone
				problems = true;
				state = LocSpecState.LOCSPEC_ERROR_UTMZONE_OUT_OF_RANGE;
			}
		}
		if (vutme.trim().toUpperCase().charAt(vutme.trim().length() - 1) == 'E') {
			leftofE = new String(vutme.trim().substring(0,
					vutme.trim().toUpperCase().indexOf('E')).trim());
		} else {
			leftofE = new String(vutme.trim());
		}
		try { // Try to make an integer out of utme.
			intutme = new Integer(leftofE);
		} catch (Exception e) { // It isn't a valid integer value.
			problems = true;
			state = LocSpecState.LOCSPEC_ERROR_UTME_MALFORMED;
		}
		if (iutme != null) {
			if (leftofE.length() == 7) {
				if (intutme.intValue() < 0 || intutme.intValue() > 8340000) { // not in the
					// valid range
					// for a utme
					problems = true;
					state = LocSpecState.LOCSPEC_ERROR_UTME_OUT_OF_RANGE;
				}
			} else if (leftofE.length() == 6) {
				if (intutme.intValue() < 0 || intutme.intValue() > 834000) { // not in the
					// valid range
					// for a utme
					problems = true;
					state = LocSpecState.LOCSPEC_ERROR_UTME_OUT_OF_RANGE;
				}
			} else if (leftofE.length() == 5) {
				if (intutme.intValue() < 0 || intutme.intValue() > 83400) { // not in the
					// valid range
					// for a utme
					problems = true;
					state = LocSpecState.LOCSPEC_ERROR_UTME_OUT_OF_RANGE;
				}
			}
		}
		if (vutmn.trim().toUpperCase().charAt(vutmn.trim().length() - 1) == 'N') {
			leftofN = new String(vutmn.trim().substring(0,
					vutmn.trim().toUpperCase().indexOf('N')).trim());
		} else {
			leftofN = new String(vutmn.trim());
		}
		try { // Try to make an integer out of utmn.
			intutmn = new Integer(leftofN);
		} catch (Exception e) { // It isn't a valid integer value.
			problems = true;
			state = LocSpecState.LOCSPEC_ERROR_UTMN_MALFORMED;
		}
		if (iutmn != null) {
			if (leftofN.length() == 8) {
				if (intutmn.intValue() < 0 || intutmn.intValue() > 10000000) { // not in
					// the valid
					// range for
					// a utmn
					problems = true;
					state = LocSpecState.LOCSPEC_ERROR_UTMN_OUT_OF_RANGE;
				}
			} else if (leftofN.length() == 7) {
				if (intutmn.intValue() < 0 || intutmn.intValue() > 1000000) { // not in the
					// valid range
					// for a utmn
					problems = true;
					state = LocSpecState.LOCSPEC_ERROR_UTMN_OUT_OF_RANGE;
				}
			} else if (leftofN.length() == 6) {
				if (intutmn.intValue() < 0 || intutmn.intValue() > 100000) { // not in the
					// valid range
					// for a utmn
					problems = true;
					state = LocSpecState.LOCSPEC_ERROR_UTMN_OUT_OF_RANGE;
				}
			}
		}
		if (problems == false) {
			// vutmzone, vutme, and vutmn were interpretable
			iutmzone = new String(zonenumber + latitudebandletter).trim();
			iutme = new String(leftofE.concat("E"));
			iutmn = new String(leftofN.concat("N"));
			state = LocSpecState.LOCSPEC_COMPLETED;
			// log.info("UTM interpretation completed successfully.");
		}
		return state;
	}

	/*
	 * public void updateInterpretations(){ if(idatum == null && vdatum != null )
	 * idatum = new String(vdatum); if(ielevation == null && velevation != null )
	 * ielevation = new String(velevation); if(ielevationunits == null &&
	 * velevationunits != null ) ielevationunits = new String(velevationunits);
	 * if(iheading == null && vheading != null ) iheading = new String(vheading);
	 * if(iheadingew == null && vheadingew != null ) iheadingew = new
	 * String(vheadingew); if(iheadingns == null && vheadingns != null )
	 * iheadingns = new String(vheadingns); if(ilat == null && vlat != null ) ilat =
	 * new String(vlat); if(ilng == null && vlng != null ) ilng = new
	 * String(vlng); if(ioffset == null && voffset != null ) ioffset = new
	 * String(voffset); if(ioffsetunit == null && voffsetunit != null )
	 * ioffsetunit = new String(voffsetunit); if(ioffsetew == null && voffsetew !=
	 * null ) ioffsetew = new String(voffsetew); if(ioffsetewunit == null &&
	 * voffsetewunit != null ) ioffsetewunit = new String(voffsetewunit);
	 * if(ioffsetns == null && voffsetns != null ) ioffsetns = new
	 * String(voffsetns); if(ioffsetnsunit == null && voffsetnsunit != null )
	 * ioffsetnsunit = new String(voffsetnsunit); if(irange == null && vrange !=
	 * null ) irange = new String(vrange); if(irangedir == null && vrangedir !=
	 * null ) irangedir = new String(vrangedir); if(isection == null && vsection !=
	 * null ) isection = new String(vsection); if(isubdivision == null &&
	 * vsubdivision != null ) isubdivision = new String(vsubdivision);
	 * if(itownship == null && vtownship != null ) itownship = new
	 * String(vtownship); if(itownshipdir == null && vtownshipdir != null )
	 * itownshipdir = new String(vtownshipdir); if(iutme == null && vutme != null )
	 * iutme = new String(vutme); if(iutmn == null && vutmn != null ) iutmn = new
	 * String(vutmn); if(iutmzone == null && vutmzone != null ) iutmzone = new
	 * String(vutmzone); }
	 */
	public void interpretVerbatimAttributes(SupportedLanguages lang) {
		// TODO: Need input and output language selection capability
		interpretOffset(lang);
		interpretOffsetEW(lang);
		interpretOffsetNS(lang);
		interpretHeading(lang);
		interpretHeadingEW(lang);
		interpretHeadingNS(lang);
		interpretElevation(lang);
		interpretTRS();
		interpretUTM();
		/*
		 * interpretLatLng(lang); interpretDatum(lang); interpretSubdivision(lang);
		 */
	}

	public boolean isCoordinate() {
		return true; // for now...
	}

	public boolean isElevation() {
		return this.isOffset(ielevation, ielevationunits);
	}

	public boolean isElevation(String value, String unit) {
		if (value == null || unit == null)
			return false;
		Double d = null;
		try { // Try to make a double out of the ioffset value.
			d = new Double(value);
		} catch (Exception e) { // It isn't a valid double value.
			return false;
		}
		// d contains a valid double.
		if (d.doubleValue() < 0.0)
			return false; // Negative offsets are bad form.
		if (this.lsm == null)
			addLocSpecManager(new LocSpecManager());
		UnitConverterManager uc = UnitConverterManager.getInstance();
		if (uc.isUnit(unit) == false) { // unit is not a valid unit found in the
			// UnitConverter.
			if (unit != null)
				uc.log("Unit not found: " + unit);
			return false;
		}
		return true; // Looks like a valid elevation to me.
	}

	public boolean isHeading() { // Test the validity of the interpreted heading by
		// checking for it in the GeorefDictionary.
		return this.isHeading(this.iheading);
	}

	public boolean isHeading(String heading) { // Test the validity of the string
		// as a heading by checking for it
		// in the GeorefDictionary.
		if (heading == null || heading.length() == 0)
			return false;
		if (this.lsm == null)
			addLocSpecManager(new LocSpecManager());
		GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
		String headingstring = gd.lookup(heading, SupportedLanguages.english,
				Concepts.headings, true);
		Double d = null;
		if (headingstring == null) { // No heading matching headingstring found in
			// the GeorefDictionary.
			// Check to see if the heading is a number, in which case we'll interpret
			// that as a degree heading.
			try { // Try to make a double out of the iheading value.
				d = new Double(heading);
				if (d.doubleValue() < 0.0 || d.doubleValue() > 360.0)
					return false; // Heading
				// not
				// between
				// 0
				// and
				// 360
				// degrees.
			} catch (Exception e) { // It isn't a valid double value.
				return false;
			}
		}
		return true; // Heading matching headingstring found in the
		// GeorefDictionary or heading is a number.
	}

	public boolean isOffset() { // Test the validity of the interpreted offset as
		// a non-negative distance with distance
		// understood units.
		return this.isOffset(ioffset, ioffsetunit);
	}

	// Test the validity of the offset as a non-negative distance with distance in
	// understood units.
	public boolean isOffset(String offset, String offsetunit) {
		if (offset == null || offsetunit == null)
			return false;
		Double d = null;
		try { // Try to make a double out of the ioffset value.
			d = new Double(offset);
		} catch (Exception e) { // It isn't a valid double value.
			return false;
		}
		// d contains a valid double.
		if (d.doubleValue() < 0.0)
			return false; // Negative offsets are bad form.
		if (this.lsm == null)
			addLocSpecManager(new LocSpecManager());
		UnitConverterManager uc = UnitConverterManager.getInstance();
		if (uc.isUnit(offsetunit) == false) { // offsetunit is not a valid unit
			// found in the UnitConverter.
			if (offsetunit != null)
				uc.log("Offsetunit not found: " + offsetunit);
			return false;
		}
		return true; // Looks like a valid offset to me.
	}

	public boolean isOrthogonalOffset() {
		return true; // for now...
	}

	/*
	 * public Coordinate getUTM(){ // check validity of utmzone, utme, and utmn as
	 * UTM coordinates and construct a coordinate with idatum }
	 */
	public boolean isTRS(String t, String tdir, String r, String rdir) {
		// check that the interpreted PLSS values can make a valid TRS construct
		// (doesn't need to assure that it exists)
		if (t == null || t.length() == 0 || tdir == null || tdir.length() == 0
				|| r == null || r.length() == 0 || rdir == null || rdir.length() == 0)
			return false;
		Integer tint = null, rint = null;
		try { // Try to make a double out of the iheading value.
			tint = new Integer(t);
			if (tint.intValue() < 0.0)
				return false; // Township numbers are
			// positive.
			rint = new Integer(r);
			if (rint.intValue() < 0.0)
				return false; // Range numbers are positive.
		} catch (Exception e) {
			return false;
		}
		GeorefDictionaryManager gd = GeorefDictionaryManager.getInstance();
		String headingstring = gd.lookup(rdir, SupportedLanguages.english,
				Concepts.headings, true);
		// if rdir isn't E or W
		if (!headingstring.equalsIgnoreCase("E")
				&& !headingstring.equalsIgnoreCase("W"))
			return false;
		headingstring = gd.lookup(tdir, SupportedLanguages.english,
				Concepts.headings, true);
		// if tdir isn't N or S
		if (!headingstring.equalsIgnoreCase("N")
				&& !headingstring.equalsIgnoreCase("S"))
			return false;

		return true; // for now...
	}

	public boolean isUTM() {
		// TODO: determine if the parameters meet the constraints of UTM
		// coordinates.
		return true; // for now...
	}

	public boolean containsParentFeature(int parentFeatureID){
		for(FeatureInfo f:featureinfos){
			if(f.parentFeatureID==parentFeatureID) return true;
		}
		return false;
	}
	/*
	 * public LocSpecState interpretSubdivision(LocSpec locspec) { boolean
	 * problems = false; if( locspec == null ) return null; if(
	 * locspec.vsubdivision == null ) { problems = true; locspec.isubdivision =
	 * null; locspec.state = LocSpecState.LOCSPEC_ERROR_SUBDIVISION_MISSING;
	 * log.error("Verbatim subdivision missing."); } locspec.isubdivision =
	 * locspec.vsubdivision; // pass it through untouched for now. ShapeManager
	 * will have to deal with it. if( problems == false ) { // subdivision is
	 * interpretable locspec.isubdivision=locspec.vsubdivision; locspec.state =
	 * LocSpecState.LOCSPEC_COMPLETED; // log.info("Subdivision interpretation
	 * completed successfully."); } return locspec.state; }
	 */

	public String toString() {
		String s = new String("<LOCSPEC>\n");
		s = s.concat("LocSpec state: " + state + "\n");
		if (featurename != null) {
			s = s.concat("vFeatureName: " + featurename + "\n");
			if (featureinfos != null) {
				if (featureinfos.size() > 0) {
					for (int i = 0; i < featureinfos.size(); i++) {
						s.concat(featureinfos.get(i).toString());
					}
				} else
					s.concat("no featureinfos in featureinfos array\n");
			} else
				s.concat("featureinfos array not created\n");
		} else
			s = s.concat("FeatureName not given\n");
		if (voffset != null && voffset.trim().length() > 0) {
			s = s.concat("voffset: " + voffset);
			if (ioffset == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + ioffset + "\n");
		}
		if (voffsetunit != null && voffsetunit.trim().length() > 0) {
			s = s.concat("voffsetunit: " + voffsetunit);
			if (ioffsetunit == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + ioffsetunit + "\n");
		}
		if (vheading != null && vheading.trim().length() > 0) {
			s = s.concat("vheading: " + vheading);
			if (iheading == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + iheading + "\n");
		}
		if (voffsetew != null && voffsetew.trim().length() > 0) {
			s = s.concat("voffsetew: " + voffsetew);
			if (ioffsetew == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + ioffsetew + "\n");
		}
		if (voffsetewunit != null && voffsetewunit.trim().length() > 0) {
			s = s.concat("voffsetewunit: " + voffsetewunit);
			if (ioffsetewunit == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + ioffsetewunit + "\n");
		}
		if (vheadingew != null && vheadingew.trim().length() > 0) {
			s = s.concat("vheadingew: " + vheadingew);
			if (iheadingew == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + iheadingew + "\n");
		}
		if (voffsetns != null && voffsetns.trim().length() > 0) {
			s = s.concat("voffsetns: " + voffsetns);
			if (ioffsetns == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + ioffsetns + "\n");
		}
		if (voffsetnsunit != null && voffsetnsunit.trim().length() > 0) {
			s = s.concat("voffsetnsunit: " + voffsetnsunit);
			if (ioffsetnsunit == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + ioffsetnsunit + "\n");
		}
		if (vheadingns != null && vheadingns.trim().length() > 0) {
			s = s.concat("vheadingns: " + vheadingns);
			if (iheadingns == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + iheadingns + "\n");
		}
		if (velevation != null && velevation.trim().length() > 0) {
			s = s.concat("velevation: " + velevation);
			if (ielevation == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + ielevation + "\n");
		}
		if (velevationunits != null && velevationunits.trim().length() > 0) {
			s = s.concat("velevationunits: " + velevationunits);
			if (ielevationunits == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + ielevationunits + "\n");
		}
		if (vlat != null && vlat.trim().length() > 0) {
			s = s.concat("vlat: " + vlat);
			if (ilat == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + ilat + "\n");
		}
		if (vlng != null && vlng.trim().length() > 0) {
			s = s.concat("vlng: " + vlng);
			if (ilng == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + ilng + "\n");
		}
		if (vdatum != null && vdatum.trim().length() > 0) {
			s = s.concat("vdatum: " + vdatum);
			if (idatum == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + idatum + "\n");
		}
		if (vutmzone != null && vutmzone.trim().length() > 0) {
			s = s.concat("vutmzone: " + vutmzone);
			if (iutmzone == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + iutmzone + "\n");
		}
		if (vutme != null && vutme.trim().length() > 0) {
			s = s.concat("vutme: " + vutme);
			if (iutme == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + iutme + "\n");
		}
		if (vutmn != null && vutmn.trim().length() > 0) {
			s = s.concat("vutmn: " + vutmn);
			if (iutmn == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + iutmn + "\n");
		}
		if (vtownship != null && vtownship.trim().length() > 0) {
			s = s.concat("vtownship: " + vtownship);
			if (itownship == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + itownship + "\n");
		}
		if (vtownshipdir != null && vtownshipdir.trim().length() > 0) {
			s = s.concat("vtownshipdir: " + vtownshipdir);
			if (itownshipdir == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + itownshipdir + "\n");
		}
		if (vrange != null && vrange.trim().length() > 0) {
			s = s.concat("vrange: " + vrange);
			if (irange == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + irange + "\n");
		}
		if (vrangedir != null && vrangedir.trim().length() > 0) {
			s = s.concat("vrangedir: " + vrangedir);
			if (irangedir == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + irangedir + "\n");
		}
		if (vsection != null && vsection.trim().length() > 0) {
			s = s.concat("vsection: " + vsection);
			if (isection == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + isection + "\n");
		}
		if (vsubdivision != null && vsubdivision.trim().length() > 0) {
			s = s.concat("vsubdivision: " + vsubdivision);
			if (isubdivision == null) {
				s = s.concat(" --> {uninterpreted}\n");
			} else
				s = s.concat(" --> " + isubdivision + "\n");
		}
		s = s.concat("</LOCSPEC>\n");
		return s;
	}

	public String toXML(boolean showgeom) {
		String s = new String("<LOCSPEC>\n");
		if (featurename != null && featurename.length() > 0) {
			s = s.concat("<FEATURENAME>" + featurename + "</FEATURENAME>\n");
			s = s.concat("<FEATUREINFOS>\n");
			if (featureinfos != null) {
				for (int i = 0; i < featureinfos.size(); i++) {
					s = s.concat(featureinfos.get(i).toXML(showgeom));
				}
			}
			s = s.concat("</FEATUREINFOS>\n");
		} else {
			s = s.concat("<FEATURENAME></FEATURENAME>\n");
		}

		if (voffset != null && voffset.length() > 0) {
			s = s.concat("<VOFFSET>" + voffset + "</VOFFSET>\n");
			if (ioffset != null && ioffset.length() > 0) {
				s = s.concat("<IOFFSET>" + ioffset + "</IOFFSET>\n");
			}
		}
		if (voffsetunit != null && voffsetunit.length() > 0) {
			s = s.concat("<VOFFSETUNIT>" + voffsetunit + "</VOFFSETUNIT>\n");
			if (ioffsetunit != null && ioffsetunit.length() > 0) {
				s = s.concat("<IOFFSETUNIT>" + ioffsetunit + "</IOFFSETUNIT>\n");
			}
		}
		if (vheading != null && vheading.length() > 0) {
			s = s.concat("<VHEADING>" + vheading + "</VHEADING>\n");
			if (iheading != null) {
				s = s.concat("<IHEADING>" + iheading + "</IHEADING>\n");
			}
		}
		if (voffsetew != null && voffsetew.length() > 0) {
			s = s.concat("<VOFFSETEW>" + voffsetew + "</VOFFSETEW>\n");
			if (ioffsetew != null && ioffsetew.length() > 0) {
				s = s.concat("<IOFFSETEW>" + ioffsetew + "</IOFFSETEW>\n");
			}
		}
		if (voffsetewunit != null && voffsetewunit.length() > 0) {
			s = s.concat("<VOFFSETEWUNIT>" + voffsetewunit + "</VOFFSETEWUNIT>\n");
			if (ioffsetewunit != null && ioffsetewunit.length() > 0) {
				s = s.concat("<IOFFSETEWUNIT>" + ioffsetewunit + "</IOFFSETEWUNIT\n");
			}
		}
		if (vheadingew != null && vheadingew.length() > 0) {
			s = s.concat("<VHEADNGEW>" + vheadingew + "</VHEADINGEW>\n");
			if (iheadingew != null && iheadingew.length() > 0) {
				s = s.concat("<IHEADINGEW>" + iheadingew + "</IHEADINGEW\n");
			}
		}
		if (voffsetns != null && voffsetns.length() > 0) {
			s = s.concat("<VOFFSETNS>" + voffsetns + "</VOFFSETNS>\n");
			if (ioffsetns != null && ioffsetns.length() > 0) {
				s = s.concat("<IOFFSETNS>" + ioffsetns + "</IOFFSETNS\n");
			}
		}
		if (voffsetnsunit != null && voffsetnsunit.length() > 0) {
			s = s.concat("<VOFFSETNSUIT>" + voffsetnsunit + "</VOFFSETNSUNIT>\n");
			if (ioffsetnsunit != null && ioffsetnsunit.length() > 0) {
				s = s.concat("<IOFFSETNSUNIT>" + ioffsetnsunit + "</IOFFSETNSUNIT\n");
			}
		}
		if (vheadingns != null && vheadingns.length() > 0) {
			s = s.concat("<VHEADNGNS>" + vheadingns + "</VHEADINGNS>\n");
			if (iheadingns != null && iheadingns.length() > 0) {
				s = s.concat("<IHEADINGNS>" + iheadingns + "</IHEADINGNS\n");
			}
		}
		if (velevation != null && velevation.length() > 0) {
			s = s.concat("<VELEVATION>" + velevation + "</VELEVATION>\n");
			if (ielevation != null && ielevation.length() > 0) {
				s = s.concat("<IELEVATION>" + ielevation + "</IELEVATION>\n");
			}
		}
		if (velevationunits != null && velevationunits.length() > 0) {
			s = s.concat("<VELEVATIONUNITS>" + velevationunits
					+ "</VELEVATIONUNITS>\n");
			if (ielevationunits != null && ielevationunits.length() > 0) {
				s = s.concat("<IELEVATIONUNITS>" + ielevationunits
						+ "</IELEVATIONUNITS\n");
			}
		}
		if (vlat != null && vlat.length() > 0) {
			s = s.concat("<VLATITUDE>" + vlat + "</VLATITUDE>\n");
			if (ilat != null && ilat.length() > 0) {
				s = s.concat("<ILATITUDE>" + ilat + "</ILATITUDE>\n");
			}
		}
		if (vlng != null && vlng.length() > 0) {
			s = s.concat("<VLONGITUDE>" + vlng + "</VLONITUDE>\n");
			if (ilng != null && ilng.length() > 0) {
				s = s.concat("<ILONGITUDE>" + ilng + "</ILONGITUDE>\n");
			}
		}
		if (vdatum != null && vdatum.length() > 0) {
			s = s.concat("<VDATUM>" + vdatum + "</VDATUM>\n");
			if (idatum != null && idatum.length() > 0) {
				s = s.concat("<IDATUM>" + idatum + "</IDATUM>\n");
			}
		}
		if (vutmzone != null && vutmzone.length() > 0) {
			s = s.concat("<VUTMZONE>" + vutmzone + "</VUTMZONE>\n");
			if (iutmzone != null && iutmzone.length() > 0) {
				s = s.concat("<IUTMZONE>" + iutmzone + "</IUTMZONE>\n");
			}
		}
		if (vutme != null && vutme.length() > 0) {
			s = s.concat("<VUTME>" + vutme + "</VUTME>\n");
			if (iutme != null && iutme.length() > 0) {
				s = s.concat("<IUTME>" + iutme + "</IUTME>\n");
			}
		}
		if (vutmn != null && vutmn.length() > 0) {
			s = s.concat("<VUTMN>" + vutmn + "</VUTMN>\n");
			if (iutmn != null && iutmn.length() > 0) {
				s = s.concat("<IUTMN>" + iutmn + "</IUTMN>\n");
			}
		}
		if (vtownship != null && vtownship.length() > 0) {
			s = s.concat("<VTOWNSHIP>" + vtownship + "</VTOWNSHIP>\n");
			if (itownship != null && itownship.length() > 0) {
				s = s.concat("<ITOWNSHIP>" + itownship + "</ITOWNSHIP>\n");
			}
		}
		if (vtownshipdir != null && vtownshipdir.length() > 0) {
			s = s.concat("<VTOWNSHIPDIR>" + vtownshipdir + "</VTOWNSHIPDIR>\n");
			if (itownshipdir != null && itownshipdir.length() > 0) {
				s = s.concat("<ITOWNSHIPDIR>" + itownshipdir + "</ITOWNSHIPDIR>\n");
			}
		}
		if (vrange != null && vrange.length() > 0) {
			s = s.concat("<VRANGE>" + vrange + "</VRANGE>\n");
			if (irange != null && irange.length() > 0) {
				s = s.concat("<IRANGE>" + irange + "</IRANGE>\n");
			}
		}
		if (vrangedir != null && vrangedir.length() > 0) {
			s = s.concat("<VRANGEDIR>" + vrangedir + "</VRANGEDIR>\n");
			if (irangedir != null && irangedir.length() > 0) {
				s = s.concat("<IRANGEDIR>" + irangedir + "</IRANGEDIR>\n");
			}
		}
		if (vsection != null && vsection.length() > 0) {
			s = s.concat("<VSECTION>" + vsection + "</VSECTION>\n");
			if (isection != null && isection.length() > 0) {
				s = s.concat("<ISECTION>" + isection + "</ISECTION>\n");
			}
		}
		if (vsubdivision != null && vsubdivision.length() > 0) {
			s = s.concat("<VSUBDIVISION>" + vsubdivision + "</VSUBDIVISION>\n");
			if (isubdivision != null && isubdivision.length() > 0) {
				s = s.concat("<ISUBDIVISION>" + isubdivision + "</ISUBDIVISION>\n");
			}
		}
		s = s.concat("<LOCSPEC_STATE>" + state + "</LOCSPEC_STATE>\n");
		s = s.concat("</LOCSPEC>\n");
		return s;
	}
}